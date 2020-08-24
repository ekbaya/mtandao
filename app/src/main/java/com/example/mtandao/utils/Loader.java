package com.example.mtandao.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class Loader {
    private Context context;
    private ProgressDialog dialog;

    public Loader(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
    }

    public void showDialogue(){
        dialog.setMessage("Please wait...");
        dialog.show();
    }

    public void  hideDialogue(){
        dialog.dismiss();
    }
}
