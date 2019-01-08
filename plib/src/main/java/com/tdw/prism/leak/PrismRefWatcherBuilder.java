package com.tdw.prism.leak;

import android.content.Context;

import com.squareup.leakcanary.ActivityRefWatcher;
import com.squareup.leakcanary.AndroidDebuggerControl;
import com.squareup.leakcanary.AndroidExcludedRefs;
import com.squareup.leakcanary.AndroidReachabilityInspectors;
import com.squareup.leakcanary.AndroidWatchExecutor;
import com.squareup.leakcanary.DebuggerControl;
import com.squareup.leakcanary.ExcludedRefs;
import com.squareup.leakcanary.HeapDump;
import com.squareup.leakcanary.HeapDumper;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.LeakDirectoryProvider;
import com.squareup.leakcanary.Reachability;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.leakcanary.RefWatcherBuilder;
import com.squareup.leakcanary.WatchExecutor;
import com.squareup.leakcanary.internal.FragmentRefWatcher;
import com.squareup.leakcanary.internal.LeakCanaryInternals;
import static com.squareup.leakcanary.RefWatcher.DISABLED;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class PrismRefWatcherBuilder extends RefWatcherBuilder<PrismRefWatcherBuilder> {

    private static final long DEFAULT_WATCH_DELAY_MILLIS = SECONDS.toMillis(5);

    private final Context context;
    private boolean watchActivities = true;
    private boolean watchFragments = true;

    PrismRefWatcherBuilder(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     *<br> Description: 泄漏检测时机
     *<br> Author:      yexiaochuan
     *<br> Date:        2018/8/23 11:42
     */
    public PrismRefWatcherBuilder watchDelay(long delay, TimeUnit unit) {
        return watchExecutor(new AndroidWatchExecutor(unit.toMillis(delay)));
    }

    /**
     *<br> Description: 是否监听Activity对象
     *<br> Author:      yexiaochuan
     *<br> Date:        2018/8/23 11:43
     */
    public PrismRefWatcherBuilder watchActivities(boolean watchActivities) {
        this.watchActivities = watchActivities;
        return this;
    }

    /**
     *<br> Description: 是否监听Fragment对象
     *<br> Author:      yexiaochuan
     *<br> Date:        2018/8/23 11:43
     */
    public PrismRefWatcherBuilder watchFragments(boolean watchFragments) {
        this.watchFragments = watchFragments;
        return this;
    }

    /**
     *<br> Description: 存储DumpHeap文件的最大个数
     *<br> Author:      yexiaochuan
     *<br> Date:        2018/8/23 11:44
     */
    public PrismRefWatcherBuilder maxStoredHeapDumps(int maxStoredHeapDumps) {
        LeakDirectoryProvider leakDirectoryProvider =
                new PrismLeakDirectoryProvider(context, maxStoredHeapDumps);
        LeakCanary.setLeakDirectoryProvider(leakDirectoryProvider);
        return self();
    }

    /**
     *<br> Description: 构建并且运行监听
     *<br> Author:      yexiaochuan
     *<br> Date:        2018/8/23 11:45
     */
    public RefWatcher buildAndInstall() {
        if (LeakCanaryInternals.installedRefWatcher != null) {
            throw new UnsupportedOperationException("buildAndInstall() should only be called once.");
        }
        RefWatcher refWatcher = build();
        if (refWatcher != DISABLED) {
            if (watchActivities) {
                ActivityRefWatcher.install(context, refWatcher);
            }
            if (watchFragments) {
                FragmentRefWatcher.Helper.install(context, refWatcher);
            }
        }
        LeakCanaryInternals.installedRefWatcher = refWatcher;
        return refWatcher;
    }

    @Override
    protected boolean isDisabled() {
        return LeakCanary.isInAnalyzerProcess(context);
    }

    @Override
    protected HeapDumper defaultHeapDumper() {
        LeakDirectoryProvider leakDirectoryProvider =
                LeakCanaryInternals.getLeakDirectoryProvider(context);
        return new PrismHeapDumper(context, leakDirectoryProvider);
    }

    @Override
    protected DebuggerControl defaultDebuggerControl() {
        return new AndroidDebuggerControl();
    }

    @Override
    protected HeapDump.Listener defaultHeapDumpListener() {
        return new PrismHeapDumpListener();
    }

    @Override
    protected ExcludedRefs defaultExcludedRefs() {
        return AndroidExcludedRefs.createAppDefaults().build();
    }

    @Override
    protected WatchExecutor defaultWatchExecutor() {
        return new AndroidWatchExecutor(DEFAULT_WATCH_DELAY_MILLIS);
    }

    @Override
    protected List<Class<? extends Reachability.Inspector>> defaultReachabilityInspectorClasses() {
        return AndroidReachabilityInspectors.defaultAndroidInspectors();
    }

}
