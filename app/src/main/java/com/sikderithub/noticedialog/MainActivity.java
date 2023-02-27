package com.sikderithub.noticedialog;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    public static final String baseurl = "https://sikderithub.com/dialog.php";
    public static final String appName = "VG";
    private static final String TAG = "MainActivity";
    private final Activity activity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button showDialog = findViewById(R.id.btn_show_dialog);


        showDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoticeDialog();
            }
        });
    }

    private void showNoticeDialog() {
        NoticeDialog.init(activity, baseurl, appName)
                .getDialogStateCallback(new NoticeDialog.DialogCallback() {
                    @Override
                    public void onNetworkCall() {
                        Log.d(TAG, "onNetworkCall: is called");
                    }

                    @Override
                    public void onNetworkCallFinished(String message) {

                        Log.d(TAG, "onNetworkCallFinished: is called");
                    }

                    @Override
                    public void onShowDialog() {

                        Log.d(TAG, "onShowDialog: is called");
                    }

                    @Override
                    public void onError(String message) {

                        Log.d(TAG, "onError: is called " + message);
                    }

                    @Override
                    public void onDialogDismiss() {

                        Log.d(TAG, "onDialogDismiss: is called");
                    }
                }).build();
    }

}