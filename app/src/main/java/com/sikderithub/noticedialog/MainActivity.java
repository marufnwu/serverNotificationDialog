package com.sikderithub.noticedialog;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    public static final String baseurl = "https://sikderithub.com/dialog.php";
    public static final String appName = "VG";
    private static final String TAG = "MainActivity";
    private final Activity activity = this;
    private boolean isDialogShowing = false;

    private NoticeDialog noticeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");

        Button showDialog = findViewById(R.id.btn_show_dialog);


        showDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noticeDialog = NoticeDialog.init(activity, baseurl, appName, new NoticeDialog.DialogCallback() {
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
                                isDialogShowing = true;
                            }

                            @Override
                            public void onError(String message) {

                                Log.d(TAG, "onError: is called " + message);
                            }

                            @Override
                            public void onDialogDismiss() {

                                Log.d(TAG, "onDialogDismiss: is called");
                                isDialogShowing = false;
                            }
                        });
                noticeDialog.build();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (isDialogShowing){
            outState.putBoolean("boolean_value",isDialogShowing);
            noticeDialog.hideDialog();
        }


        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

        if (isDialogShowing){
            Log.d(TAG, "onRestoreInstanceState: dialog showing");
            noticeDialog = NoticeDialog.init(activity, baseurl, appName, new NoticeDialog.DialogCallback() {
                @Override
                public void onNetworkCall() {

                }

                @Override
                public void onNetworkCallFinished(String Message) {

                }

                @Override
                public void onShowDialog() {

                }

                @Override
                public void onError(String message) {

                }

                @Override
                public void onDialogDismiss() {

                }
            });
            noticeDialog.build();

        }

        super.onRestoreInstanceState(savedInstanceState);
    }
}