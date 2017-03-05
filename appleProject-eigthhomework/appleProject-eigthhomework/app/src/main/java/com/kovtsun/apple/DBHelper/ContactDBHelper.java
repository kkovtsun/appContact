package com.kovtsun.apple.DBHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.kovtsun.apple.DBTables.Contact;
import com.kovtsun.apple.DBTables.Markers;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class ContactDBHelper extends OrmLiteSqliteOpenHelper {

    private  static  final String TAG = ContactDBHelper.class.getSimpleName();

    private static final String DB_NAME = "contacts";
    private static final int DB_VERSION = 1;

    private Dao<Contact, Integer> contactDao = null;

    private RuntimeExceptionDao<Contact, Integer> contactRuntimeDao = null;

    public ContactDBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try{
            TableUtils.createTable(connectionSource, Contact.class);
        }catch (SQLException e){

            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try{
            TableUtils.dropTable(connectionSource, Contact.class, true);
            onCreate(database, connectionSource);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Dao<Contact, Integer> getDao() throws SQLException{
        if (contactDao == null){
            contactDao = getDao(Contact.class);
        }
        return contactDao;
    }


    public RuntimeExceptionDao<Contact, Integer> getFruitRuntimeExceptionDao(){
        if (contactRuntimeDao == null){
            contactRuntimeDao = getRuntimeExceptionDao(Contact.class);
        }
        return contactRuntimeDao;
    }
}
