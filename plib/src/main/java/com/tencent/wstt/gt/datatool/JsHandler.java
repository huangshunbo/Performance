package com.tencent.wstt.gt.datatool;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class JsHandler {
    private static final int BUFFER_SIZE = 1 << 20;

    public static void toDataJs(File des, GTRAnalysis gtrAnalysis) {
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(des), BUFFER_SIZE);
            writer.write("\n\n\n");

            appendJSObject(writer, "appInfo", gtrAnalysis.getAppInfo());
            appendJSObject(writer, "deviceInfo", gtrAnalysis.getDeviceInfo());
            appendJSList(writer, "frames", gtrAnalysis.getFrames());
            appendJSList(writer, "normalInfos", gtrAnalysis.getNormalInfos());
            appendJSList(writer, "gtrThreadInfos", gtrAnalysis.getGtrThreadInfos());

            appendJSList(writer, "frontBackStates", gtrAnalysis.getFrontBackStates());
            appendJSObject(writer, "frontBackInfo", gtrAnalysis.getFrontBackInfo());
            appendJSList(writer, "lowSMInfos", gtrAnalysis.getLowSMInfos());
            appendJSList(writer, "allBlockInfos", gtrAnalysis.getAllBlockInfos());
            appendJSList(writer, "bigBlockIDs", gtrAnalysis.getBigBlockIDs());

            appendJSList(writer, "pageLoadInfos", gtrAnalysis.getPageLoadInfos());
            appendJSList(writer, "overActivityInfos", gtrAnalysis.getOverActivityInfos());
            appendJSList(writer, "overViewDraws", gtrAnalysis.getOverViewDraws());
            appendJSList(writer, "operationInfos", gtrAnalysis.getOperationInfos());
            appendJSList(writer, "viewBuildInfos", gtrAnalysis.getViewBuildInfos());

            appendJSList(writer, "overViewBuilds", gtrAnalysis.getOverViewBuilds());
            appendJSList(writer, "fragmentInfos", gtrAnalysis.getFragmentInfos());
            appendJSList(writer, "overFragments", gtrAnalysis.getOverFragments());
            appendJSList(writer, "allGCInfos", gtrAnalysis.getAllGCInfos());
            appendJSList(writer, "explicitGCs", gtrAnalysis.getExplicitGCs());

            appendJSList(writer, "diskIOInfos", gtrAnalysis.getDiskIOInfos());
            appendJSList(writer, "fileActionInfos", gtrAnalysis.getFileActionInfos());
            appendJSList(writer, "fileActionInfosInMainThread", gtrAnalysis.getFileActionInfosInMainThread());
            appendJSList(writer, "dbActionInfos", gtrAnalysis.getDbActionInfos());

            appendJSList(writer, "dbActionInfosInMainThread", gtrAnalysis.getDbActionInfosInMainThread());
            appendJSList(writer, "logInfos", gtrAnalysis.getLogInfos());
            appendJSList(writer, "flagInfo", gtrAnalysis.getFlagList());
            appendJSList(writer, "tagInfo", gtrAnalysis.getUserTagInfos());
            appendJSList(writer, "leakInfo", gtrAnalysis.getLeakInfos());

            writer.write("\n\n\n");

            String data = "//基础性能\nvar tableBaseData_base= frontBackInfo;\n" +
                    "//卡顿检测\nvar tableBaseData_lowSM = lowSMInfos;\n" +
                    "var tableBaseData_bigBlock = bigBlockIDs;\n" +
                    "//页面测速\nvar tableBaseData_overActivity = overActivityInfos;\nvar tableBaseData_allPage = pageLoadInfos;\n" +
                    "//Fragment测速\nvar tableBaseData_overFragment = overFragments;\nvar tableBaseData_allFragment = fragmentInfos;\n" +
                    "//布局检测\nvar tableBaseData_overViewBuild = overViewBuilds;\nvar tableBaseData_overViewDraw = overViewDraws;\n" +
                    "//GC检测\nvar tableBaseData_explicitGC = explicitGCs;\n" +
                    "//IO检测\nvar tableBaseData_fileActionInMainThread = fileActionInfosInMainThread;\n" +
                    "var tableBaseData_dbActionInMainThread = dbActionInfosInMainThread;\nvar tableBaseData_db = dbActionInfos;\n" +
                    "//关键日志\nvar tableBaseData_logcat = logInfos;\n";
            appendRawString(writer, data);

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer == null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void appendJSObject(BufferedWriter writer, String varName, Object obj) throws IOException {
        appendVariableName(writer, varName);
        writer.write(new Gson().toJson(obj));
        writer.write(";\n");
    }

    private static void appendVariableName(BufferedWriter writer, String varName) throws IOException {
        writer.write("var ");
        writer.write(varName);
        writer.write("=");
    }

    private static void appendJSList(BufferedWriter writer, String varName, ArrayList list) throws IOException {
        appendVariableName(writer, varName);
        appendList(writer, list);
        writer.write(";\n");
    }

    private static void appendList(BufferedWriter writer, ArrayList list) throws IOException {
        writer.write("[");

        if (!list.isEmpty()) {
            writer.write(new Gson().toJson(list.get(0)));

            for (int i = 1; i < list.size(); i++) {
                writer.write(",");
                writer.write(new Gson().toJson(list.get(i)));
            }

            list.clear();
        }

        writer.write("]");
    }

    private static void appendRawString(BufferedWriter writer, String data) throws IOException {
        writer.write(data);
        writer.write(";\n");
    }
}
