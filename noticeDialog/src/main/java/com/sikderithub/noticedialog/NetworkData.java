package com.sikderithub.noticedialog;

public class NetworkData {
    private boolean isCancelable;
    private String thumbUrl;
    private String thumbAction;
    private String title;
    private String body;
    private String negativeButtonText;
    private String positiveButtonText;
    private boolean showPositiveButton;
    private boolean showNegativeButton;
    private String positiveButtonAction;
    private String negativeButtonAction;

    public boolean isCancelable() {
        return isCancelable;
    }

    public void setCancelable(boolean cancelable) {
        isCancelable = cancelable;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getThumbAction() {
        return thumbAction;
    }

    public void setThumbAction(String thumbAction) {
        this.thumbAction = thumbAction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getNegativeButtonText() {
        return negativeButtonText;
    }

    public void setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
    }

    public String getPositiveButtonText() {
        return positiveButtonText;
    }

    public void setPositiveButtonText(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
    }

    public boolean isShowPositiveButton() {
        return showPositiveButton;
    }

    public void setShowPositiveButton(boolean showPositiveButton) {
        this.showPositiveButton = showPositiveButton;
    }

    public boolean isShowNegativeButton() {
        return showNegativeButton;
    }

    public void setShowNegativeButton(boolean showNegativeButton) {
        this.showNegativeButton = showNegativeButton;
    }

    public String getPositiveButtonAction() {
        return positiveButtonAction;
    }

    public void setPositiveButtonAction(String positiveButtonAction) {
        this.positiveButtonAction = positiveButtonAction;
    }

    public String getNegativeButtonAction() {
        return negativeButtonAction;
    }

    public void setNegativeButtonAction(String negativeButtonAction) {
        this.negativeButtonAction = negativeButtonAction;
    }
}
