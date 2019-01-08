package com.tencent.wstt.gt.datatool.analysis.prism;

import com.tencent.wstt.gt.datatool.obj.LeakInfo;

import java.util.ArrayList;

public class PrismLeakAnalysis {
    private ArrayList<LeakInfo> leakList;

    public PrismLeakAnalysis(ArrayList<LeakInfo> list) {
        leakList = list;
    }

    public void collectLeakInfo(String time,String className) {
        LeakInfo leakInfo = new LeakInfo();
        leakInfo.className = className;
        leakInfo.time = time;
        leakList.add(leakInfo);
    }
}
