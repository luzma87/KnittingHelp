package com.lzm.KnittingHelp.db;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 29/10/2014.
 */
public class FotoDbHelper extends DbHelper {

    private static final String LOG = "FotoDbHelper";
    public static final String KEY_PATTERN_ID = "pattern_id";
    public static final String KEY_PATH = "path";

    public static final String[] KEYS_FOTO = {KEY_PATTERN_ID, KEY_PATH};

    public FotoDbHelper(Activity context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOTO);

        // create new tables
        onCreate(db);
    }

    public long create(Foto foto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(foto);

        // insert row
        long res = db.insert(TABLE_FOTO, null, values);
        db.close();
        return res;
    }

    public Foto get(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT f." + ALIAS_FOTO + "_" + KEY_ID + ", " +
                "f." + ALIAS_FOTO + "_" + KEY_FECHA_CREACION + "," +
                "f." + ALIAS_FOTO + "_" + KEY_FECHA_MODIFICACION + "," +
                "f." + ALIAS_FOTO + "_" + KEY_PATTERN_ID + "," +
                "f." + ALIAS_FOTO + "_" + KEY_PATH + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_ID +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_FECHA_CREACION + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_FECHA_MODIFICACION + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_NOMBRE + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_CONTENIDO + "," +
                " FROM " + TABLE_FOTO + "f" +
                " INNER JOIN " + TABLE_PATTERN + " p ON f." + KEY_PATTERN_ID + " = p." + ALIAS_PATTERN + "_" + KEY_ID +
                " WHERE " + ALIAS_FOTO + "_" + KEY_ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);
        Foto obj = null;
        if (c.getCount() > 0) {
            c.moveToFirst();
            obj = setDatosConPattern(c);
        }
        db.close();
        return obj;
    }

    public ArrayList<Foto> getAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Foto> list = new ArrayList<Foto>();
        String selectQuery = "SELECT f." + ALIAS_FOTO + "_" + KEY_ID + ", " +
                "f." + ALIAS_FOTO + "_" + KEY_FECHA_CREACION + "," +
                "f." + ALIAS_FOTO + "_" + KEY_FECHA_MODIFICACION + "," +
                "f." + ALIAS_FOTO + "_" + KEY_PATTERN_ID + "," +
                "f." + ALIAS_FOTO + "_" + KEY_PATH + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_ID +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_FECHA_CREACION + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_FECHA_MODIFICACION + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_NOMBRE + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_CONTENIDO + "," +
                " FROM " + TABLE_FOTO + " f" +
                " INNER JOIN " + TABLE_PATTERN + " p ON f." + KEY_PATTERN_ID + " = p." + ALIAS_PATTERN + "_" + KEY_ID +
                " ORDER BY f." + ALIAS_FOTO + "_" + KEY_FECHA_MODIFICACION + " DESC";
        logQuery(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Foto obj = setDatosConPattern(c);
                // adding to tags list
                list.add(obj);
            } while (c.moveToNext());
        }
        db.close();
        return list;
    }

    public ArrayList<Foto> getAllByPattern(Pattern pattern) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Foto> list = new ArrayList<Foto>();
        String selectQuery = "SELECT f." + ALIAS_FOTO + "_" + KEY_ID + ", " +
                "f." + ALIAS_FOTO + "_" + KEY_FECHA_CREACION + "," +
                "f." + ALIAS_FOTO + "_" + KEY_FECHA_MODIFICACION + "," +
                "f." + ALIAS_FOTO + "_" + KEY_PATTERN_ID + "," +
                "f." + ALIAS_FOTO + "_" + KEY_PATH + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_ID +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_FECHA_CREACION + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_FECHA_MODIFICACION + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_NOMBRE + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_CONTENIDO + "," +
                " FROM " + TABLE_FOTO + " f" +
                " INNER JOIN " + TABLE_PATTERN + " p ON f." + KEY_PATTERN_ID + " = p." + ALIAS_PATTERN + "_" + KEY_ID +
                " WHERE f." + KEY_PATTERN_ID + " = " + pattern.id +
                " ORDER BY f." + ALIAS_FOTO + "_" + KEY_FECHA_MODIFICACION + " DESC";
        logQuery(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Foto obj = setDatosConPattern(c);
                // adding to tags list
                list.add(obj);
            } while (c.moveToNext());
        }
        db.close();
        return list;
    }

    public int update(Foto obj) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(obj);

        // updating row
        int res = db.update(TABLE_FOTO, values, ALIAS_FOTO + "_" + KEY_ID + " = ?",
                new String[]{String.valueOf(obj.id)});
        db.close();
        return res;
    }

    public void delete(Foto obj) {
        //primero elimino el archivo
        File file = new File(obj.path);
        boolean deleted = file.delete();

        if (deleted) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_FOTO, ALIAS_FOTO + "_" + KEY_ID + " = ?",
                    new String[]{String.valueOf(obj.id)});
            db.close();
        }
    }

    public void deleteByPattern(Pattern obj) {
        //primero tengo q sacar la lista de fotos para eliminar los archivos
        List<Foto> fotos = Foto.findAllByPattern(context, obj);
        boolean ok = true;
        for (Foto foto : fotos) {
            File file = new File(foto.path);
            boolean deleted = file.delete();
            ok = ok && deleted;
        }
        if (ok) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_FOTO, ALIAS_FOTO + "_" + KEY_PATTERN_ID + " = ?",
                    new String[]{String.valueOf(obj.id)});
            db.close();
        }
    }

    private Foto setDatos(Cursor c) {
        Foto obj = new Foto(this.context);
        obj.id = c.getLong((c.getColumnIndex(ALIAS_FOTO + "_" + KEY_ID)));
        obj.fechaCreacion = c.getString(c.getColumnIndex(ALIAS_FOTO + "_" + KEY_FECHA_CREACION));
        obj.fechaModificacion = c.getString(c.getColumnIndex(ALIAS_FOTO + "_" + KEY_FECHA_MODIFICACION));
        obj.patternId = c.getLong(c.getColumnIndex(ALIAS_FOTO + "_" + KEY_PATTERN_ID));
        obj.path = c.getString(c.getColumnIndex(ALIAS_FOTO + "_" + KEY_PATH));
        return obj;
    }

    private Foto setDatosConPattern(Cursor c) {
        Foto obj = new Foto(this.context);
        obj.id = c.getLong((c.getColumnIndex(ALIAS_FOTO + "_" + KEY_ID)));
        obj.fechaCreacion = c.getString(c.getColumnIndex(ALIAS_FOTO + "_" + KEY_FECHA_CREACION));
        obj.fechaModificacion = c.getString(c.getColumnIndex(ALIAS_FOTO + "_" + KEY_FECHA_MODIFICACION));
        obj.patternId = c.getLong(c.getColumnIndex(ALIAS_FOTO + "_" + KEY_PATTERN_ID));
        obj.path = c.getString(c.getColumnIndex(ALIAS_FOTO + "_" + KEY_PATH));

        Pattern obj2 = new Pattern(this.context);
        obj2.id = c.getLong((c.getColumnIndex(ALIAS_PATTERN + "_" + PatternDbHelper.KEY_ID)));
        obj2.fechaCreacion = c.getString(c.getColumnIndex(ALIAS_PATTERN + "_" + PatternDbHelper.KEY_FECHA_CREACION));
        obj2.fechaModificacion = c.getString(c.getColumnIndex(ALIAS_PATTERN + "_" + PatternDbHelper.KEY_FECHA_MODIFICACION));
        obj2.nombre = c.getString(c.getColumnIndex(ALIAS_PATTERN + "_" + PatternDbHelper.KEY_NOMBRE));
        obj2.contenido = c.getString(c.getColumnIndex(ALIAS_PATTERN + "_" + PatternDbHelper.KEY_CONTENIDO));
        obj2.imagen = c.getString(c.getColumnIndex(ALIAS_PATTERN + "_" + PatternDbHelper.KEY_IMAGEN));

        obj.pattern = obj2;
        return obj;
    }

    private ContentValues setValues(Foto obj) {
        ContentValues values = new ContentValues();
        values.put(KEY_FECHA_CREACION, obj.fechaCreacion);
        values.put(KEY_FECHA_MODIFICACION, obj.fechaModificacion);
        values.put(KEY_PATTERN_ID, obj.patternId);
        values.put(KEY_PATH, obj.path);
        return values;
    }
}
