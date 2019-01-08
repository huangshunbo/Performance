package com.paisheng.psliblite.performance;

import android.app.Application;

import com.paisheng.prism.anotation.SpyVoid;
import com.tdw.prism.Prism;

public class MApplication extends Application {

    @SpyVoid
    @Override
    public void onCreate() {
        super.onCreate();
        Prism.install(this);
    }
}
