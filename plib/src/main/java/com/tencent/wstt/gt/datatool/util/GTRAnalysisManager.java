package com.tencent.wstt.gt.datatool.util;

import com.tencent.wstt.gt.datatool.GTRAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class GTRAnalysisManager {
    public static GTRAnalysis getGTRAnalysis(String dataFilePath) throws Exception {
        File file = new File(dataFilePath);
        if (!file.exists()) {
            throw new Exception("dataFilePath is not exists:" + dataFilePath);
        } else {
            GTRAnalysis gtrAnalysis = new GTRAnalysis();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));

            for (String dataLine = bufferedReader.readLine(); dataLine != null; dataLine = bufferedReader.readLine()) {
                try {
                    if (dataLine.length() > 0) {
                        gtrAnalysis.distribute(dataLine.split("\\^"));
                    }
                } catch (Exception e) {
                    System.out.println("ErrorData:" + dataLine);
                    e.printStackTrace();
                }
            }

            return gtrAnalysis;
        }
    }
}
