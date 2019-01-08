package com.tdw.prism.leak;

import android.content.Context;
import android.os.Environment;

import com.squareup.leakcanary.CanaryLog;
import com.squareup.leakcanary.LeakDirectoryProvider;
import com.tdw.prism.report.PrismSDCardPath;
import static com.squareup.leakcanary.HeapDumper.RETRY_LATER;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PrismLeakDirectoryProvider implements LeakDirectoryProvider {
    private static final int DEFAULT_MAX_STORED_HEAP_DUMPS = 7;

    private static final String HPROF_SUFFIX = ".hprof";
    private static final String PENDING_HEAPDUMP_SUFFIX = "_pending" + HPROF_SUFFIX;

    /** 10 minutes */
    private static final int ANALYSIS_MAX_DURATION_MS = 10 * 60 * 1000;

    private final Context context;
    private final int maxStoredHeapDumps;

    private volatile boolean writeExternalStorageGranted;
    private volatile boolean permissionNotificationDisplayed;

    public PrismLeakDirectoryProvider(Context context) {
        this(context, DEFAULT_MAX_STORED_HEAP_DUMPS);
    }

    public PrismLeakDirectoryProvider(Context context, int maxStoredHeapDumps) {
        if (maxStoredHeapDumps < 1) {
            throw new IllegalArgumentException("maxStoredHeapDumps must be at least 1");
        }
        this.context = context.getApplicationContext();
        this.maxStoredHeapDumps = maxStoredHeapDumps;
    }

    @Override
    public List<File> listFiles(FilenameFilter filter) {
        List<File> files = new ArrayList<>();

        File[] externalFiles = externalStorageDirectory().listFiles(filter);
        if (externalFiles != null) {
            files.addAll(Arrays.asList(externalFiles));
        }

        File[] appFiles = appStorageDirectory().listFiles(filter);
        if (appFiles != null) {
            files.addAll(Arrays.asList(appFiles));
        }
        return files;
    }

    @Override public File newHeapDumpFile() {
        List<File> pendingHeapDumps = listFiles(new FilenameFilter() {
            @Override public boolean accept(File dir, String filename) {
                return filename.endsWith(PENDING_HEAPDUMP_SUFFIX);
            }
        });

        // If a new heap dump file has been created recently and hasn't been processed yet, we skip.
        // Otherwise we move forward and assume that the analyzer process crashes. The file will
        // eventually be removed with heap dump file rotation.
        for (File file : pendingHeapDumps) {
            if (System.currentTimeMillis() - file.lastModified() < ANALYSIS_MAX_DURATION_MS) {
                CanaryLog.d("Could not dump heap, previous analysis still is in progress.");
                return RETRY_LATER;
            }
        }

        cleanupOldHeapDumps();

        File storageDirectory = externalStorageDirectory();
        if (!directoryWritableAfterMkdirs(storageDirectory)) {
            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                CanaryLog.d("External storage not mounted, state: %s", state);
            } else {
                CanaryLog.d("Could not create heap dump directory in external storage: [%s]",
                        storageDirectory.getAbsolutePath());
            }
            // Fallback to app storage.
            storageDirectory = appStorageDirectory();
            if (!directoryWritableAfterMkdirs(storageDirectory)) {
                CanaryLog.d("Could not create heap dump directory in app storage: [%s]",
                        storageDirectory.getAbsolutePath());
                return RETRY_LATER;
            }
        }
        // If two processes from the same app get to this step at the same time, they could both
        // create a heap dump. This is an edge case we ignore.
        return new File(storageDirectory, UUID.randomUUID().toString() + PENDING_HEAPDUMP_SUFFIX);
    }

    @Override public void clearLeakDirectory() {
        List<File> allFilesExceptPending = listFiles(new FilenameFilter() {
            @Override public boolean accept(File dir, String filename) {
                return !filename.endsWith(PENDING_HEAPDUMP_SUFFIX);
            }
        });
        for (File file : allFilesExceptPending) {
            boolean deleted = file.delete();
            if (!deleted) {
                CanaryLog.d("Could not delete file %s", file.getPath());
            }
        }
    }

    private File externalStorageDirectory() {
        File downloadsDirectory = new File(PrismSDCardPath.getHeapDir());
        if (!downloadsDirectory.exists()) {
            downloadsDirectory.mkdirs();
        }
        return new File(downloadsDirectory, "leakcanary-" + context.getPackageName());
    }

    private File appStorageDirectory() {
        File appFilesDirectory = context.getFilesDir();
        return new File(appFilesDirectory, "leakcanary");
    }

    private boolean directoryWritableAfterMkdirs(File directory) {
        boolean success = directory.mkdirs();
        return (success || directory.exists()) && directory.canWrite();
    }

    private void cleanupOldHeapDumps() {
        List<File> hprofFiles = listFiles(new FilenameFilter() {
            @Override public boolean accept(File dir, String filename) {
                return filename.endsWith(HPROF_SUFFIX);
            }
        });
        int filesToRemove = hprofFiles.size() - maxStoredHeapDumps;
        if (filesToRemove > 0) {
            CanaryLog.d("Removing %d heap dumps", filesToRemove);
            // Sort with oldest modified first.
            Collections.sort(hprofFiles, new Comparator<File>() {
                @Override public int compare(File lhs, File rhs) {
                    return Long.valueOf(lhs.lastModified()).compareTo(rhs.lastModified());
                }
            });
            for (int i = 0; i < filesToRemove; i++) {
                boolean deleted = hprofFiles.get(i).delete();
                if (!deleted) {
                    CanaryLog.d("Could not delete old hprof file %s", hprofFiles.get(i).getPath());
                }
            }
        }
    }
}
