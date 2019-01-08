package com.tdw.prism;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.tdw.prism.monitor.TagMonitor;
import com.tdw.prism.report.PrismSDCardPath;
import com.tdw.prism.view.PrismNotification;
import com.tencent.wstt.gt.collector.GTRCollector;
import com.tencent.wstt.gt.collector.util.ProcessUtil;
import com.tencent.wstt.gt.controller.GTRController;

/**
 * Created by yexiaochuan on 2018/7/3.
 */

public class Prism {
    public static boolean install(Application application) {
        build().init(application);
        return true;
    }

    public static PrismBuilder build() {
        return new PrismBuilder();
    }

    private static class PrismBuilder {

        public PrismBuilder openDumpHeap(boolean value) {
            PrismInternal.isOpenDumpHeap = value;
            return this;
        }

        public PrismBuilder openLeakCanary(boolean value) {
            PrismInternal.isOpenLeak = value;
            return this;
        }

        public boolean init(Application application) {
            if (!checkAllow(application)) {
                return false;
            }

            PrismInternal.isBuild = true;
            //用户标识初始化
            TagMonitor.init();

            new PrismNotification(application).onNotificationInit();

            PrismSDCardPath.init(application.getPackageName());

            GTRController.init(application);

            if (PrismInternal.isOpenLeak && !LeakCanary.isInAnalyzerProcess(application)) {
                LeakCanary.install(application);
            }

            return true;
        }

        private boolean checkAllow(Application application) {
            if (PrismInternal.isBuild) {
                return false;
            }
            if (!ProcessUtil.isUIProcess(application, android.os.Process.myPid())) {
                return false;
            }
            return true;
        }
    }
}
