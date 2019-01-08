package com.tdw.prism.leak;

import android.content.Context;
import android.os.Debug;

import com.squareup.leakcanary.CanaryLog;
import com.squareup.leakcanary.HeapDumper;
import com.squareup.leakcanary.LeakDirectoryProvider;

import java.io.File;

public class PrismHeapDumper implements HeapDumper {
    private final LeakDirectoryProvider leakDirectoryProvider;

    public PrismHeapDumper(Context context, LeakDirectoryProvider leakDirectoryProvider) {
        this.leakDirectoryProvider = leakDirectoryProvider;
    }

    @SuppressWarnings("ReferenceEquality") // Explicitly checking for named null.
    @Override
    public File dumpHeap() {
        File heapDumpFile = leakDirectoryProvider.newHeapDumpFile();

        if (heapDumpFile == RETRY_LATER) {
            return RETRY_LATER;
        }

        try {
            Debug.dumpHprofData(heapDumpFile.getAbsolutePath());
            return heapDumpFile;
        } catch (Exception e) {
            CanaryLog.d(e, "Could not dump heap");
            // Abort heap dump
            return RETRY_LATER;
        }
    }
}

