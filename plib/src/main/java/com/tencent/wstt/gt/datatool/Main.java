package com.tencent.wstt.gt.datatool;

import com.tencent.wstt.gt.datatool.util.FileUtil;
import com.tencent.wstt.gt.datatool.util.GTRAnalysisManager;

import java.io.File;
import java.util.ArrayList;

public class Main {
    //获取数据目录：
    private static File nowDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
//    private static File nowDir = new File("D:\\Android\\demo\\PrismReport");

    public static final String dataDirPath = new File(nowDir, "data").getAbsolutePath();
    public static final String srcDirPath = new File(nowDir, "src").getAbsolutePath();
    public static final String resultDirPath = new File(nowDir, "result").getAbsolutePath();
    public static final String resultDataFilePath = resultDirPath + "/data/data.js";


    public static final ArrayList<GTRAnalysis> dataAnalysises = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        //GTRAnalysis：读取数据文件，装载数据对象
        File dataDir = new File(dataDirPath);
        if (!dataDir.exists()) {
            throw new Exception("dataDirPath is not exists :" + dataDirPath);
        }
        File[] dataFiles = dataDir.listFiles();
        for (File temp : dataFiles) {
            if (temp.getName().endsWith(".txt")) {
                GTRAnalysis gtrAnalysis = GTRAnalysisManager.getGTRAnalysis(temp.getAbsolutePath());
                dataAnalysises.add(gtrAnalysis);
            }
        }

        GTRAnalysis gtrAnalysis = dataAnalysises.get(0);//TODO 暂时只分析第一个文件
        FileUtil.copyDirectory(srcDirPath, resultDirPath, true);//拷贝HTML库文件

        File resultDataFile = new File(resultDataFilePath);
        if (resultDataFile.exists()) {
            resultDataFile.delete();
        }
        resultDataFile.getParentFile().mkdirs();
        resultDataFile.createNewFile();

        JsHandler.toDataJs(resultDataFile,gtrAnalysis);

        System.out.println("数据报告已生成：" + resultDirPath);
    }
}
