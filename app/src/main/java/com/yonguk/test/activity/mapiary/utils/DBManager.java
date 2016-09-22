package com.yonguk.test.activity.mapiary.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dosi on 2016-04-05.
 */
public class DBManager extends SQLiteOpenHelper {
    static final String DB_NAME = "Mapiary.db";
    static final String TABLE_NAME = "contents";
    static final String FILE_PATH = "file_path";
    static final String LOCATION = "location";
    static final String EMOTION = "emotion";
    static final String DATETIME = "datetime";
    static final int DB_VERSION = 1;

    Context mContext = null;
    private static DBManager mDbManager = null;

    private DBManager(Context context,
                      String dbName,
                      SQLiteDatabase.CursorFactory factory,
                      int version) {

        super(context, dbName, factory, version);
        mContext = context;

    }

    public static DBManager getInstance(Context context) {
        if (mDbManager == null) {
            mDbManager = new DBManager(context, DB_NAME, null, DB_VERSION);
        }
        return mDbManager;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "file_path TEXT, " +
                "location   TEXT, " +
                "emotion TEXT);");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }


    //DB Version이 바뀌면 호출됨
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < newVersion){
            db.execSQL("DROP TABLE IF EXIST "+TABLE_NAME);
            onCreate(db);
        }
    }

    public long insert(ContentValues addRowValue) {
        return getWritableDatabase().insert(TABLE_NAME, null, addRowValue);
    }

    public Cursor query(String[] columns,
                        String selection,
                        String[] selectionArgs,
                        String groupBy,
                        String having,
                        String orderBy) {

        return getReadableDatabase().query(TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy);
    }

    public int update(ContentValues updateRowValue, String whereClause, String[] whereArgs){
        return getWritableDatabase().update(TABLE_NAME,
                updateRowValue,
                whereClause,
                whereArgs);
    }

    public int delete(String whereClause, String[] whereArgs){
        return getWritableDatabase().delete(TABLE_NAME, whereClause, whereArgs);
    }


    /**테이블 새로 만들기**/
    public void deleteTable(){
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(getWritableDatabase());
    }


}
