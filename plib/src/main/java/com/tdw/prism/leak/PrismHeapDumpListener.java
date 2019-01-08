package com.tdw.prism.leak;

import com.squareup.leakcanary.HeapDump;
import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.client.GTRClient;

public class PrismHeapDumpListener implements HeapDump.Listener {


    public PrismHeapDumpListener() {

    }

    @Override
    public void analyze(HeapDump heapDump) {
        if (heapDump == null) {
            return;
        }

        String content = "leakCollect" +
                GTConfig.separator + heapDump.referenceKey +
                GTConfig.separator + heapDump.referenceName +
                GTConfig.separator + heapDump.heapDumpFile.getAbsolutePath();
        GTRClient.pushData(content);
    }
}

