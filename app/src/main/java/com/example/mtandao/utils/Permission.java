package com.example.mtandao.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class Permission {
    private Context context;

    public Permission(Context context) {
        this.context = context;
    }

    public boolean checkStoragePermissions(){
        //check if storage permission is granted
        //return true if permission is enabled
        //if not enabled return false
        boolean result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
}
