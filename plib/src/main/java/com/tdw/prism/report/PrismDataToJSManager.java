package com.tdw.prism.report;

import android.util.Log;

import com.tencent.wstt.gt.datatool.GTRAnalysis;
import com.tencent.wstt.gt.datatool.JsHandler;
import com.tencent.wstt.gt.datatool.util.FileUtil;
import com.tencent.wstt.gt.datatool.util.GTRAnalysisManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class PrismDataToJSManager {
    /**
     *<br> Description: 解析成js文件
     *<br> Author:      yexiaochuan
     *<br> Date:        2018/5/30 15:41
     * @param fileName    应用包名
     * @return
     * @throws Exception
     */
    public static Boolean toAnalysis(String fileName) throws Exception {
        GTRAnalysis gtrAnalysis;
        long startTime0 = System.currentTimeMillis();
        File dataDir = new File(fileName);
        if (!dataDir.exists()) {
            throw new Exception("file is not exists :" + dataDir.getAbsolutePath());
        }
        if (dataDir.getAbsolutePath().endsWith(".txt")) {
            // 解析数据
            gtrAnalysis = GTRAnalysisManager.getGTRAnalysis(dataDir.getAbsolutePath());
            long startTime = System.currentTimeMillis();
            Log.i("adam", " 序列化数据时间 =" + (startTime - startTime0) + "ms");
        } else {
            throw new Exception("file is not txt :" + dataDir.getAbsolutePath());
        }

        String srcDes = PrismSDCardPath.PRISM_REPORT_PATH +
                "/" + PrismSDCardPath.getSaveDateMs() + "/data.js";
        long startTime1 = System.currentTimeMillis();
        toDataJs(srcDes,gtrAnalysis);
        long startTime2 = System.currentTimeMillis();
        Log.i("adam", "写入数据时间 =" + (startTime2 - startTime1) + "ms");
        FileUtil.deleteFile(dataDir);
        FileUtil.copyFile(srcDes,PrismSDCardPath.PRISM_RESULT_DATA_PATH,true);
        return true;
    }

    private static void toDataJs(String desPath, GTRAnalysis gtrAnalysis) throws IOException {
        File des = new File(desPath);
        if (toCreateFileDir(des)) {
            JsHandler.toDataJs(des,gtrAnalysis);
        } else {
            throw new IOException("ErrorData: 文件创建失败，请开启系统读写权限后重试");
        }
    }

    private static boolean toCreateFileDir(File des) {
        try {
            if (des.exists()) {
                des.delete();
            }
            des.getParentFile().mkdirs();
            des.createNewFile();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
