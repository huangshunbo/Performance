package com.tdw.prism.view;

import android.app.Activity;
import android.app.Dialog;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.paisheng.psliblite.plib.R;
import com.tdw.prism.ReportTaskManager;
import com.tdw.prism.report.Constants;
import com.tdw.prism.report.PrismSDCardPath;
import com.tdw.prism.report.ReportUtils;
import com.tdw.prism.report.WifiUtils;
import com.tdw.prism.report.server.WebService;

public class MainActivity extends Activity {
    TextView mTxtTitle;
    TextView mTxtSubTitle;
    ImageView mImgLanState;
    TextView mTxtStateHint;
    TextView mTxtAddress;
    Dialog mProgress;
    public boolean isRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prism_activity_main);
        mTxtTitle = (TextView) findViewById(R.id.menu_title);
        mTxtSubTitle = (TextView) findViewById(R.id.menu_subtitle);
        mImgLanState = (ImageView) findViewById(R.id.shared_wifi_state);
        mTxtStateHint = (TextView) findViewById(R.id.shared_wifi_state_hint);
        mTxtAddress = (TextView) findViewById(R.id.shared_wifi_address);
        checkWifiState(WifiUtils.getWifiConnectState(this));
        new Thread(new Runnable() {
            @Override
            public void run() {
                ReportUtils.copyAssets(MainActivity.this,"prism");
                WebService.start(MainActivity.this);
            }
        }).start();
    }

    public void history(View view) {
        new ReportSelectDialog(this).builder().show();
    }

    public void report(View view) {
        ReportTaskManager.publishReport(this, PrismSDCardPath.getSrcData(getPackageName()));
    }

    public void checkWifi(View view) {
        checkWifiState(WifiUtils.getWifiConnectState(this));
    }

    void checkWifiState(NetworkInfo.State state) {
        if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
            String ip = WifiUtils.getPhoneIpAddress(this);
            if (state == NetworkInfo.State.CONNECTED) {
                if (!TextUtils.isEmpty(ip)) {
                    onWifiConnected(ip);
                    return;
                }
            }

            if (!TextUtils.isEmpty(ip)) {
                onWifiConnected(ip);
            } else {
                onWifiConnecting();
            }
            return;
        }
        onWifiDisconnected();
    }

    void onWifiConnecting() {
        mTxtTitle.setText(R.string.wlan_enabled);
        mTxtTitle.setTextColor(getResources().getColor(R.color.colorWifiConnected));
        mTxtSubTitle.setVisibility(View.GONE);
        mImgLanState.setImageResource(R.drawable.shared_wifi_enable);
        mTxtStateHint.setText(R.string.retrofit_wlan_address);
        mTxtAddress.setVisibility(View.GONE);
    }

    void onWifiConnected(String ipAddr) {
        mTxtTitle.setText(R.string.wlan_enabled);
        mTxtTitle.setTextColor(getResources().getColor(R.color.colorWifiConnected));
        mTxtSubTitle.setVisibility(View.GONE);
        mImgLanState.setImageResource(R.drawable.shared_wifi_enable);
        mTxtStateHint.setText(R.string.pls_input_the_following_address_in_pc_browser);
        mTxtAddress.setVisibility(View.VISIBLE);
        mTxtAddress.setText(String.format(getString(R.string.http_address), ipAddr, Constants.port));
    }

    void onWifiDisconnected() {
        mTxtTitle.setText(R.string.wlan_disabled);
        mTxtTitle.setTextColor(getResources().getColor(android.R.color.black));
        mTxtSubTitle.setVisibility(View.VISIBLE);
        mImgLanState.setImageResource(R.drawable.shared_wifi_shut_down);
        mTxtStateHint.setText(R.string.fail_to_start_http_service);
        mTxtAddress.setVisibility(View.GONE);
    }
}
