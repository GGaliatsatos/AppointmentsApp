package co.app.my.myandroidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "AppointmentApp.db";
    public static final String TABLE_NAME = "contacts_table";
    public static final String CREDENTIALS = "credentials_table";
    public static final String COL_1 = "USERNAME";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "SURNAME";
    public static final String COL_4 = "EMAIL";
    public static final String COL_USER = "USERNAME";
    public static final String COL_PASS = "PASSWORD";

    public DatabaseHelper(Context context) {
        super(context,DATABASE_NAME, null, 1);
        //SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (USERNAME TEXT PRIMARY KEY,NAME TEXT,SURNAME TEXT,EMAIL TEXT)");
        db.execSQL("create table if not exists " + CREDENTIALS +" (USERNAME TEXT PRIMARY KEY, PASSWORD TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+CREDENTIALS);
        onCreate(db);
    }

    public boolean insertContact(String username,String name,String surname,String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,username);
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,surname);
        contentValues.put(COL_4,email);
        long result = db.insert(TABLE_NAME ,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertCredentials(String username,String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USER,username);
        contentValues.put(COL_PASS,password);
        long result = db.insert(CREDENTIALS ,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public void deleteCredentials(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ CREDENTIALS);
    }
    public  void deleteAllContacts(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }



    public Cursor getCredentials(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+CREDENTIALS+" LIMIT 1",null);
        return res;
    }
    public Cursor WhoAmI(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select "+COL_USER+" from "+CREDENTIALS+" LIMIT 1",null);
        return res;
    }


    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public Cursor getAllUsernames() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select USERNAME from "+TABLE_NAME,null);
        return res;
    }

    public Integer deleteContact (String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "USERNAME = ?" ,new String[] {key});
    }



}
