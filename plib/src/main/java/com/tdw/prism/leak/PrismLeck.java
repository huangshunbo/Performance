package com.tdw.prism.leak;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.AndroidExcludedRefs;
import com.squareup.leakcanary.RefWatcher;

public class PrismLeck {
    public static RefWatcher install(Application application) {
        return refWatcher(application)
                .excludedRefs(AndroidExcludedRefs.createAppDefaults().build())
                .buildAndInstall();
    }

    public static PrismRefWatcherBuilder refWatcher(Context context) {
        return new PrismRefWatcherBuilder(context);
    }
}

