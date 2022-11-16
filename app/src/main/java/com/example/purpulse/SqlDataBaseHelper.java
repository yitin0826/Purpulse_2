package com.example.purpulse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class SqlDataBaseHelper extends SQLiteOpenHelper {
    private static final String DataBaseName = "db";
    private static final int DataBaseVersion = 11;

    public SqlDataBaseHelper(@Nullable Context context, @Nullable String name,
                             @Nullable SQLiteDatabase.CursorFactory factory, int version, String TableName) {
        super(context, DataBaseName, factory, DataBaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SqlTable = "CREATE TABLE IF NOT EXISTS Users (" +
                "name TEXT not null," +
                "account TEXT not null," +
                "password TEXT not null," +
                "email TEXT not null," +
                "birthday TEXT not null," +
                "gender TEXT not null," +
                "height TEXT not null," +
                "weight TEXT not null," +
                "RMSSD NONE," +
                "sdNN NONE," +
                "LFHF NONE," +
                "LFn NONE," +
                "HFn NONE," +
                "Heart NONE," +
                "RRi NONE" +
                ")";
        String DataTable = "CREATE TABLE IF NOT EXISTS Data (" +
                "account TEXT not null," +
                "time," +
                "week," +
                "state TEXT," +
                "RMSSD NONE," +
                "sdNN NONE," +
                "LFHF NONE," +
                "LFn NONE," +
                "HFn NONE," +
                "Heart NONE," +
                "RRi NONE" +
                ")";
        Log.d("test","test");
        sqLiteDatabase.execSQL(SqlTable);
        sqLiteDatabase.execSQL(DataTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        final String SQL = "DROP TABLE IF EXISTS Users";
        final String Data = "DROP TABLE IF EXISTS Data";
        sqLiteDatabase.execSQL(SQL);
        sqLiteDatabase.execSQL(Data);
        onCreate(sqLiteDatabase);
    }
}
