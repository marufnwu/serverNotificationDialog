package com.sikderithub.noticedialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NoticeDialog implements View.OnClickListener {

    private static final String TAG = "NoticeDialog";
    @SuppressLint("StaticFieldLeak")
    private static NoticeDialog nDialog;
    private static android.app.Dialog dialog;
    private final String url;
    private final Context context;
    boolean isDialogStateEnable = false;
    NetworkData data;
    Button btnPositive;
    Button btnNegative;
    ImageView thumbNailImage;
    TextView tvTitle;
    TextView tvBody;
    ProgressBar imageLoading;
    private DialogCallback dialogCallback;

    private NoticeDialog(Context context, String baseUrl, String appName) {
        this.context = context;

        Uri buildUri = Uri.parse(baseUrl)
                .buildUpon()
                .appendQueryParameter("app", appName)
                .build();

        url = buildUri.toString();


        setupDialog();
    }

    /**
     * A static function call by client
     * Create a notice dialog object and setup dialog
     *
     * @param context application Context
     * @param baseUrl webData url
     * @param appName appName
     * @return a NoticeDialog object
     */
    public static NoticeDialog init(Context context, String baseUrl, String appName) {
        nDialog = new NoticeDialog(context, baseUrl, appName);
        return nDialog;
    }

    /**
     * set layout to dialog
     * <p>
     * This function is called by constructor
     */
    private void setupDialog() {
        if (dialog == null) {
            dialog = new android.app.Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_layout);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }


        dialog.setOnDismissListener(dialog -> {
            if (isDialogStateEnable)
                dialogCallback.onDialogDismiss();
        });
    }

    /**
     * initialize the view of the dialog
     * operate network call and set data to the views
     * <p>
     * this is a public method called by client
     */
    public void build() {
        btnPositive = dialog.findViewById(R.id.btn_positive);
        btnNegative = dialog.findViewById(R.id.btn_negative);
        thumbNailImage = dialog.findViewById(R.id.iv_thumbnail);
        tvTitle = dialog.findViewById(R.id.tv_title);
        tvBody = dialog.findViewById(R.id.tv_body);
        imageLoading = dialog.findViewById(R.id.pb_image_loading);

        nDialog.setData();
    }

    /**
     * if Internet available then it call the asyncTask to network call
     * <p>
     * called by build method
     */
    private void setData() {
        if (isNetworkConnected())
            new dataAsync().execute(url);
        else
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
    }

    /**
     * check network is connected
     *
     * @return connected or not
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public NoticeDialog getDialogStateCallback(DialogCallback dialogCallback) {
        isDialogStateEnable = true;
        this.dialogCallback = dialogCallback;
        return nDialog;
    }


    /**
     * Hide the dialog
     */
    public void hideDialog() {
        Activity acc = (Activity) context;

        if (dialog != null && !acc.isFinishing() && !acc.isDestroyed()) {
            dialog.dismiss();
            dialog = null;
            if (isDialogStateEnable)
                dialogCallback.onDialogDismiss();
        }
    }

    /**
     * get data from json web response
     * <p>
     * called by asyncTask onPostExecute method
     *
     * @param response is json response from web called by asyncTask
     * @return Network data object to onPostExecute
     */
    private NetworkData getDataFRomJson(String response) {
        JSONObject jsonObject;
        NetworkData data = new NetworkData();

        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.has("data")) {
                JSONObject jsonData = jsonObject.getJSONObject("data");

                if (jsonData.has("isCancelable"))
                    data.setCancelable(jsonData.getBoolean("isCancelable"));
                if (jsonData.has("thumbUrl"))
                    data.setThumbUrl(jsonData.getString("thumbUrl"));
                if (jsonData.has("thumbAction"))
                    data.setThumbAction(jsonData.getString("thumbAction"));
                if (jsonData.has("title"))
                    data.setTitle(jsonData.getString("title"));
                if (jsonData.has("body"))
                    data.setBody(jsonData.getString("body"));
                if (jsonData.has("positiveButtonText"))
                    data.setPositiveButtonText(jsonData.getString("positiveButtonText"));
                if (jsonData.has("negativeButtonAction"))
                    data.setPositiveButtonAction(jsonData.getString("negativeButtonAction"));
                if (jsonData.has("negativeButtonText"))
                    data.setNegativeButtonText(jsonData.getString("negativeButtonText"));
                if (jsonData.has("negativeButtonAction"))
                    data.setNegativeButtonAction(jsonData.getString("negativeButtonAction"));
                if (jsonData.has("showNegativeButton"))
                    data.setShowNegativeButton(jsonData.getBoolean("showNegativeButton"));
                if (jsonData.has("showPositiveButton"))
                    data.setShowPositiveButton(jsonData.getBoolean("showPositiveButton"));

            } else {
                dialog.dismiss();
                return null;
            }


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    /**
     * called by onPostExecute
     * bind data to the views
     *
     * @param data sent by the onPost execute
     */
    private void bindData(NetworkData data) {

        //TestData
        data.setPositiveButtonAction("proceed");
        data.setNegativeButtonAction("close");

        if (data.isCancelable())
            dialog.setCancelable(data.isCancelable());

        if (data.getThumbUrl() != null) {

            new Thread(() -> {
                try {
                    URL url = new URL(data.getThumbUrl());
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        thumbNailImage.setImageBitmap(bmp);
                        imageLoading.setVisibility(View.GONE);
                        Log.d(TAG, "bindData: image set");
                    });

                } catch (IOException e) {
                    Log.d(TAG, "bindData: error to image load");
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        thumbNailImage.setVisibility(View.GONE);
                        imageLoading.setVisibility(View.GONE);
                    });
                }
            }).start();
        } else
            thumbNailImage.setVisibility(View.GONE);

        if (data.getPositiveButtonText() != null && data.getPositiveButtonAction() != null)
            btnPositive.setText(data.getPositiveButtonText());
        else
            btnPositive.setVisibility(View.GONE);

        if (data.getNegativeButtonText() != null && data.getNegativeButtonAction() != null)
            btnNegative.setText(data.getNegativeButtonText());
        else
            btnNegative.setVisibility(View.GONE);

        if (data.getTitle() != null)
            tvTitle.setText(data.getTitle());
        if (data.getBody() != null) {
            tvBody.setText(data.getBody());
        } else
            tvBody.setVisibility(View.GONE);

        if (data.getPositiveButtonAction() != null)
            btnPositive.setOnClickListener(this);
        if (data.getNegativeButtonAction() != null)
            btnNegative.setOnClickListener(this);
        if (data.getThumbAction() != null)
            thumbNailImage.setOnClickListener(this);


        Activity activity = (Activity) context;
        if (dialog != null && !activity.isFinishing() && !activity.isDestroyed()) {
            if (dialog.isShowing()) {
                hideDialog();
            }
            dialog.show();
            if (isDialogStateEnable)
                dialogCallback.onShowDialog();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Uri uri;

        if (id == R.id.btn_positive) {
            String btnAction = data.getPositiveButtonAction().toLowerCase();
            if (btnAction.equals("close")) {
                hideDialog();
                Activity acc = (Activity) context;
                acc.finishAffinity();
            } else if (btnAction.equals("proceed"))
                hideDialog();
            else if (URLUtil.isValidUrl(data.getPositiveButtonAction())) {
                uri = Uri.parse(data.getPositiveButtonAction());
                makeImplicitIntent(uri);
            }


        } else if (id == R.id.btn_negative) {
            String btnAction = data.getNegativeButtonAction().toLowerCase();
            if (btnAction.equals("close")) {
                hideDialog();
                Activity acc = (Activity) context;
                acc.finishAffinity();

            } else if (btnAction.equals("proceed"))
                hideDialog();

            else if (URLUtil.isValidUrl(data.getPositiveButtonAction())) {
                uri = Uri.parse(data.getNegativeButtonAction());
                makeImplicitIntent(uri);
            }

        } else if (id == R.id.iv_thumbnail) {
            uri = Uri.parse(data.getThumbAction());
            makeImplicitIntent(uri);
        }
    }

    private void makeImplicitIntent(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     * Interface for handling status callback.
     */
    public interface DialogCallback {
        void onNetworkCall();

        void onNetworkCallFinished(String Message);

        void onShowDialog();

        void onError(String message);

        void onDialogDismiss();
    }


    /**
     * get web response in a background thread and bind data to the views
     */
    class dataAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String response;
            try {
                URL webUrl = new URL(strings[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) webUrl.openConnection();
                try {
                    if (isDialogStateEnable)
                        dialogCallback.onNetworkCall();

                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder total = new StringBuilder();

                    for (String line; (line = r.readLine()) != null; ) {
                        total.append(line).append('\n');
                    }

                    if (isDialogStateEnable)
                        dialogCallback.onNetworkCallFinished("success");

                    response = total.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                if (isNetworkConnected())
                    dialogCallback.onError(e.getMessage());
                throw new RuntimeException(e);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String networkData) {
            super.onPostExecute(networkData);

            data = getDataFRomJson(networkData);
            if (data == null) {
                if (isDialogStateEnable)
                    dialogCallback.onDialogDismiss();
                return;
            }

            bindData(data);
        }
    }
}
