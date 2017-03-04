package com.kovtsun.apple.DBHelper;


import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.kovtsun.apple.DBHelper.MarkersDBHelper;

public class MarkersHelperFactory {

    private static MarkersDBHelper markersDBHelper;

    public static MarkersDBHelper getMarkersDBHelper() {
        return markersDBHelper;
    }

    public static void setMarkersDBHelper(Context context) {
        markersDBHelper = OpenHelperManager.getHelper(context, MarkersDBHelper.class);
    }

    public static void releaseHelper(){
        OpenHelperManager.releaseHelper();
        markersDBHelper = null;
    }
}
