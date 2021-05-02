package com.example.PIS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdaptadorBD extends SQLiteOpenHelper {

    public static final String TABLE_ID = "_idNote";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String DATABASE = "Note";
    public static final String TABLE = "notes";

    public AdaptadorBD(Context context) {
        super(context, DATABASE, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TABLE +" ("+
                TABLE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                TITLE + " TEXT," + CONTENT +" TEXT)");

    } 

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE);
        onCreate(db);
    }
    public void addNote(String title,String content){
        ContentValues valores= new ContentValues();
        valores.put(TITLE,title);
        valores.put(CONTENT,content);
        this.getWritableDatabase().insert(TABLE,null,valores);
    }
    public Cursor getNote(String condition){
        String columnas[]={TABLE_ID,TITLE,CONTENT};
        String[] args = new String[] {condition};
        Cursor c = this.getReadableDatabase().query(TABLE,columnas,TITLE+"=?",args,null,null,null);
        return c;
    }
    public void deleteNota(String condition){
        String args[]={condition};
        this.getWritableDatabase().delete(TABLE,TITLE +"=?",args);
    }
    public void updateNota(String title,String content,String condition){
        String args[]={condition};
        ContentValues valores= new ContentValues();
        valores.put(TITLE,title);
        valores.put(CONTENT,content);
        this.getWritableDatabase().update(TABLE,valores,TITLE +"=?",args);
    }
    public Cursor getNotes(){
        String columnas[]={TABLE_ID,TITLE,CONTENT};
        Cursor c = this.getReadableDatabase().query(TABLE,columnas,null,null,null,null,null);
        return c;
    }
    public void deleteNotes(){
        this.getWritableDatabase().delete(TABLE,null,null);
    }
}
