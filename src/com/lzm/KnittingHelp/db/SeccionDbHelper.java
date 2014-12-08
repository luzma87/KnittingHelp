package com.lzm.KnittingHelp.db;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by DELL on 29/10/2014.
 */
public class SeccionDbHelper extends DbHelper {

    private static final String LOG = "SeccionDbHelper";
    public static final String KEY_PATTERN_ID = "pattern_id";
    public static final String KEY_CONTENIDO = "contenido";
    public static final String KEY_IMAGEN = "imagen";
    public static final String KEY_ORDEN = "orden";
    public static final String KEY_SECCION_PADRE_ID = "seccion_padre_id";
    public static final String KEY_TIPO = "tipo";

    public static final String[] KEYS_SECCION = {KEY_PATTERN_ID, KEY_CONTENIDO, KEY_IMAGEN, KEY_ORDEN, KEY_SECCION_PADRE_ID, KEY_TIPO};

    public SeccionDbHelper(Activity context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SECCION);

        // create new tables
        onCreate(db);
    }

    public long create(Seccion seccion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(seccion);

        // insert row
        long res = db.insert(TABLE_SECCION, null, values);
        db.close();
        return res;
    }

    public Seccion get(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = selectQuerySeccionPattern();
        selectQuery += " WHERE s." + ALIAS_SECCION + "_" + KEY_ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);
        Seccion obj = null;
        if (c.getCount() > 0) {
            c.moveToFirst();
            obj = setDatosConPattern(c);
        }
        db.close();
        return obj;
    }

    public ArrayList<Seccion> getAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Seccion> list = new ArrayList<Seccion>();

        String selectQuery = selectQuerySeccionPattern();
        selectQuery += " ORDER BY CAST(s." + ALIAS_SECCION + "_" + KEY_ORDEN + " AS INTEGER) ASC";

        logQuery(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Seccion obj = setDatosConPattern(c);
                // adding to tags list
                list.add(obj);
            } while (c.moveToNext());
        }
        db.close();
        return list;
    }

    public ArrayList<Seccion> getAllByPattern(Pattern pattern) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Seccion> list = new ArrayList<Seccion>();
        String selectQuery = selectQuerySeccionPattern();
        selectQuery += " WHERE s." + ALIAS_SECCION + "_" + KEY_PATTERN_ID + " = " + pattern.id;
        selectQuery += " ORDER BY CAST(s." + ALIAS_SECCION + "_" + KEY_ORDEN + " AS INTEGER) ASC";

        //CAST(value AS INTEGER)

        logQuery(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Seccion obj = setDatosConPattern(c);
                // adding to tags list
                list.add(obj);
            } while (c.moveToNext());
        }
        db.close();
        return list;
    }

    public ArrayList<Seccion> getAllByPatternAndTipo(Pattern pattern, int tipo) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Seccion> list = new ArrayList<Seccion>();
        String selectQuery = selectQuerySeccionPattern();
        selectQuery += " WHERE s." + ALIAS_SECCION + "_" + KEY_TIPO + " = " + tipo +
                " AND s." + ALIAS_SECCION + "_" + KEY_PATTERN_ID + " = " + pattern.id;
        selectQuery += " ORDER BY CAST(s." + ALIAS_SECCION + "_" + KEY_ORDEN + " AS INTEGER) ASC";

        //CAST(value AS INTEGER)

        logQuery(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Seccion obj = setDatosConPattern(c);
                // adding to tags list
                list.add(obj);
            } while (c.moveToNext());
        }
        db.close();
        return list;
    }

    public ArrayList<Seccion> getAllByPadre(Seccion padre) {
        return getAllByPadre(padre.id);
    }

    public ArrayList<Seccion> getAllByPadre(long padreId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Seccion> list = new ArrayList<Seccion>();
        String selectQuery = selectQuerySeccionPattern();
        selectQuery += " WHERE s." + ALIAS_SECCION + "_" + KEY_SECCION_PADRE_ID + " = " + padreId;
        selectQuery += " ORDER BY CAST(s." + ALIAS_SECCION + "_" + KEY_ORDEN + " AS INTEGER) ASC";

        //CAST(value AS INTEGER)

        logQuery(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Seccion obj = setDatosConPattern(c);
                // adding to tags list
                list.add(obj);
            } while (c.moveToNext());
        }
        db.close();
        return list;
    }

    public int updateOrdenFromSeccion(Seccion seccion, int cant, boolean includes) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "UPDATE " + TABLE_SECCION +
                " SET " + ALIAS_SECCION + "_" + KEY_ORDEN + " = (CAST(" + ALIAS_SECCION + "_" + KEY_ORDEN + " AS INTEGER) + " + cant + ")" +
                " WHERE " + ALIAS_SECCION + "_" + KEY_PATTERN_ID + " = " + seccion.patternId + " AND";
        String innerSelect = "SELECT " + ALIAS_SECCION + "_" + KEY_ORDEN + " FROM " + TABLE_SECCION +
                " WHERE " + ALIAS_SECCION + "_" + KEY_ID + " = " + seccion.id;
        if (includes) {
            sql += " CAST(" + ALIAS_SECCION + "_" + KEY_ORDEN + " AS INTEGER) >= (" + innerSelect + ")";
        } else {
            sql += " CAST(" + ALIAS_SECCION + "_" + KEY_ORDEN + " AS INTEGER) > (" + innerSelect + ")";
        }
        db.execSQL(sql);
        Cursor c = db.rawQuery(innerSelect, null);
        int nuevoOrden = -1;
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                nuevoOrden = c.getInt(c.getColumnIndex(ALIAS_SECCION + "_" + KEY_ORDEN));
            } while (c.moveToNext());
        }
        db.close();
        return nuevoOrden;
    }

    public int update(Seccion obj) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(obj);

        // updating row
        int res = db.update(TABLE_SECCION, values, ALIAS_SECCION + "_" + KEY_ID + " = ?",
                new String[]{String.valueOf(obj.id)});
        db.close();
        return res;
    }

    public int delete(Seccion obj) {
        long lineaId = obj.seccionPadreId;
        int count = 1;
        ArrayList<Seccion> seccionesLinea = getAllByPadre(lineaId);
        if (seccionesLinea.size() <= 1) {
            count = 2;
            deleteById(lineaId);
        }
        updateOrdenFromSeccion(obj, count * -1, false);

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SECCION, ALIAS_SECCION + "_" + KEY_ID + " = ?",
                new String[]{String.valueOf(obj.id)});
        db.close();
        return count;
    }

    public void deleteById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SECCION, ALIAS_SECCION + "_" + KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteByPattern(Pattern obj) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SECCION, ALIAS_SECCION + "_" + KEY_PATTERN_ID + " = ?",
                new String[]{String.valueOf(obj.id)});
        db.close();
    }

    public void deleteByPadre(Seccion obj) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SECCION, ALIAS_SECCION + "_" + KEY_SECCION_PADRE_ID + " = ?",
                new String[]{String.valueOf(obj.id)});
        db.close();
    }

    private String selectQuerySeccionPattern() {
        return "SELECT s." + ALIAS_SECCION + "_" + KEY_ID + ", " +
                "s." + ALIAS_SECCION + "_" + KEY_FECHA_CREACION + "," +
                "s." + ALIAS_SECCION + "_" + KEY_FECHA_MODIFICACION + "," +
                "s." + ALIAS_SECCION + "_" + KEY_PATTERN_ID + "," +
                "s." + ALIAS_SECCION + "_" + KEY_SECCION_PADRE_ID + "," +
                "s." + ALIAS_SECCION + "_" + KEY_CONTENIDO + "," +
                "s." + ALIAS_SECCION + "_" + KEY_IMAGEN + "," +
                "s." + ALIAS_SECCION + "_" + KEY_ORDEN + "," +
                "s." + ALIAS_SECCION + "_" + KEY_TIPO + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_ID + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_FECHA_CREACION + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_FECHA_MODIFICACION + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_NOMBRE + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_CONTENIDO + "," +
                "p." + ALIAS_PATTERN + "_" + PatternDbHelper.KEY_IMAGEN +
                " FROM " + TABLE_SECCION + " s" +
                " INNER JOIN " + TABLE_PATTERN + " p ON s." + ALIAS_SECCION + "_" + KEY_PATTERN_ID + " = p." + ALIAS_PATTERN + "_" + KEY_ID;
    }

    private Seccion setDatos(Cursor c) {
        Seccion obj = new Seccion(this.context);
        obj.id = c.getLong((c.getColumnIndex(ALIAS_SECCION + "_" + KEY_ID)));
        obj.fechaCreacion = c.getString(c.getColumnIndex(ALIAS_SECCION + "_" + KEY_FECHA_CREACION));
        obj.fechaModificacion = c.getString(c.getColumnIndex(ALIAS_SECCION + "_" + KEY_FECHA_MODIFICACION));
        obj.patternId = c.getLong(c.getColumnIndex(ALIAS_SECCION + "_" + KEY_PATTERN_ID));
        obj.contenido = c.getString(c.getColumnIndex(ALIAS_SECCION + "_" + KEY_CONTENIDO));
        obj.imagen = c.getString(c.getColumnIndex(ALIAS_SECCION + "_" + KEY_IMAGEN));
        obj.orden = c.getInt(c.getColumnIndex(ALIAS_SECCION + "_" + KEY_ORDEN));
        obj.seccionPadreId = c.getLong(c.getColumnIndex(ALIAS_SECCION + "_" + KEY_SECCION_PADRE_ID));
        obj.tipo = c.getInt(c.getColumnIndex(ALIAS_SECCION + "_" + KEY_TIPO));
        return obj;
    }

    private Seccion setDatosConPattern(Cursor c) {
        Seccion obj = new Seccion(this.context);
        obj.id = c.getLong((c.getColumnIndex(ALIAS_SECCION + "_" + KEY_ID)));
        obj.fechaCreacion = c.getString(c.getColumnIndex(ALIAS_SECCION + "_" + KEY_FECHA_CREACION));
        obj.fechaModificacion = c.getString(c.getColumnIndex(ALIAS_SECCION + "_" + KEY_FECHA_MODIFICACION));
        obj.patternId = c.getLong(c.getColumnIndex(ALIAS_SECCION + "_" + KEY_PATTERN_ID));
        obj.contenido = c.getString(c.getColumnIndex(ALIAS_SECCION + "_" + KEY_CONTENIDO));
        obj.imagen = c.getString(c.getColumnIndex(ALIAS_SECCION + "_" + KEY_IMAGEN));
        obj.orden = c.getInt(c.getColumnIndex(ALIAS_SECCION + "_" + KEY_ORDEN));
        obj.seccionPadreId = c.getLong(c.getColumnIndex(ALIAS_SECCION + "_" + KEY_SECCION_PADRE_ID));
        obj.tipo = c.getInt(c.getColumnIndex(ALIAS_SECCION + "_" + KEY_TIPO));

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

    private ContentValues setValues(Seccion obj) {
        ContentValues values = new ContentValues();
        values.put(ALIAS_SECCION + "_" + KEY_FECHA_CREACION, obj.fechaCreacion);
        values.put(ALIAS_SECCION + "_" + KEY_FECHA_MODIFICACION, obj.fechaModificacion);
        values.put(ALIAS_SECCION + "_" + KEY_PATTERN_ID, obj.patternId);
        values.put(ALIAS_SECCION + "_" + KEY_CONTENIDO, obj.contenido);
        values.put(ALIAS_SECCION + "_" + KEY_IMAGEN, obj.imagen);
        values.put(ALIAS_SECCION + "_" + KEY_ORDEN, obj.orden);
        values.put(ALIAS_SECCION + "_" + KEY_SECCION_PADRE_ID, obj.seccionPadreId);
        values.put(ALIAS_SECCION + "_" + KEY_TIPO, obj.tipo);
        return values;
    }
}
