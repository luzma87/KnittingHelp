package com.lzm.KnittingHelp.db;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by DELL on 29/10/2014.
 */
public class PatternDbHelper extends DbHelper {

    private static final String LOG = "PatternDbHelper";
    public static final String KEY_NOMBRE = "nombre";
    public static final String KEY_CONTENIDO = "contenido";
    public static final String KEY_IMAGEN = "imagen";
    public static final String KEY_CURRENT_SECCION_ID = "current_seccion_id";

    public static final String[] KEYS_PATTERN = {KEY_NOMBRE, KEY_CONTENIDO, KEY_IMAGEN, KEY_CURRENT_SECCION_ID};

    public PatternDbHelper(Activity context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATTERN);

        // create new tables
        onCreate(db);
    }

    public long create(Pattern pattern) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(pattern);

        // insert row
        long res = db.insert(TABLE_PATTERN, null, values);
        db.close();
        return res;
    }

    public Pattern get(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PATTERN + " WHERE "
                + ALIAS_PATTERN + "_" + KEY_ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);
        Pattern obj = null;
        if (c.getCount() > 0) {
            c.moveToFirst();
            obj = setDatos(c);
        }
        db.close();
        return obj;
    }

    public ArrayList<Pattern> getAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Pattern> list = new ArrayList<Pattern>();
        String selectQuery = "SELECT * FROM " + TABLE_PATTERN +
                " ORDER BY " + ALIAS_PATTERN + "_" + KEY_FECHA_MODIFICACION + " DESC";
        logQuery(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Pattern obj = setDatos(c);
                // adding to tags list
                list.add(obj);
            } while (c.moveToNext());
        }
        db.close();
        return list;
    }

    public int update(Pattern obj) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(obj);

        // updating row
        int res = db.update(TABLE_PATTERN, values, ALIAS_PATTERN + "_" + KEY_ID + " = ?",
                new String[]{String.valueOf(obj.id)});
        db.close();
        return res;
    }

    public void delete(Pattern obj) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PATTERN, ALIAS_PATTERN + "_" + KEY_ID + " = ?",
                new String[]{String.valueOf(obj.id)});
        db.close();
    }

    private Pattern setDatos(Cursor c) {
        Pattern obj = new Pattern(this.context);
        obj.id = c.getLong((c.getColumnIndex(ALIAS_PATTERN + "_" + KEY_ID)));
        obj.fechaCreacion = c.getString(c.getColumnIndex(ALIAS_PATTERN + "_" + KEY_FECHA_CREACION));
        obj.fechaModificacion = c.getString(c.getColumnIndex(ALIAS_PATTERN + "_" + KEY_FECHA_MODIFICACION));
        obj.nombre = c.getString(c.getColumnIndex(ALIAS_PATTERN + "_" + KEY_NOMBRE));
        obj.contenido = c.getString(c.getColumnIndex(ALIAS_PATTERN + "_" + KEY_CONTENIDO));
        obj.imagen = c.getString(c.getColumnIndex(ALIAS_PATTERN + "_" + KEY_IMAGEN));
        return obj;
    }

    private ContentValues setValues(Pattern obj) {
        ContentValues values = new ContentValues();
        values.put(ALIAS_PATTERN + "_" + KEY_FECHA_CREACION, obj.fechaCreacion);
        values.put(ALIAS_PATTERN + "_" + KEY_FECHA_MODIFICACION, obj.fechaModificacion);
        values.put(ALIAS_PATTERN + "_" + KEY_NOMBRE, obj.nombre);
        values.put(ALIAS_PATTERN + "_" + KEY_CONTENIDO, obj.contenido);
        values.put(ALIAS_PATTERN + "_" + KEY_IMAGEN, obj.imagen);
        return values;
    }
}
