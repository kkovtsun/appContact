package com.kovtsun.apple.DBHelper;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.kovtsun.apple.DBHelper.ContactDBHelper;

public class HelperFactory {

    private static ContactDBHelper contactDBHelper;

    public static ContactDBHelper getContactDBHelper() {
        return contactDBHelper;
    }

    public static void setContactDBHelper(Context context) {
        contactDBHelper = OpenHelperManager.getHelper(context, ContactDBHelper.class);
    }

    public static void releaseHelper(){
        OpenHelperManager.releaseHelper();
        contactDBHelper = null;
    }
}
