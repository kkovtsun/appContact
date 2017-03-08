package com.kovtsun.apple.DBHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.kovtsun.apple.DBTables.Markers;

import java.sql.SQLException;

public class MarkersDBHelper extends OrmLiteSqliteOpenHelper{

    private  static  final String TAG = MarkersDBHelper.class.getSimpleName();
    private static final String DB_NAME = "markers";
    private static final int DB_VERSION = 2;

    private Dao<Markers, Integer> markersDao = null;

    private RuntimeExceptionDao<Markers, Integer> markersRuntimeDao = null;

    public MarkersDBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try{
            TableUtils.createTable(connectionSource, Markers.class);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try{
            TableUtils.dropTable(connectionSource, Markers.class, true);
            onCreate(database, connectionSource);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Dao<Markers, Integer> getDao() throws SQLException{
        if (markersDao == null){
            markersDao = getDao(Markers.class);
        }
        return markersDao;
    }


    public RuntimeExceptionDao<Markers, Integer> getMarkersRuntimeExceptionDao(){
        if (markersRuntimeDao == null){
            markersRuntimeDao = getRuntimeExceptionDao(Markers.class);
        }
        return markersRuntimeDao;
    }
}
