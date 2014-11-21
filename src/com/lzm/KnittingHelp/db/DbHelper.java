package com.lzm.KnittingHelp.db;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import com.lzm.KnittingHelp.R;
import com.lzm.KnittingHelp.activities.SettingsActivity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by DELL on 26/07/2014.
 */
public class DbHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
//    private static String DB_PATH = "/data/data/com.tmm.android.chuck/databases/";
    public static String DB_PATH = Environment.getExternalStorageDirectory().getPath() + "/Knitting/db/";
    //    public static String DB_PATH = "";
    private static final String DATABASE_NAME = "knittingDb.db";

    // Table Names
    protected static final String TABLE_PATTERN = "patterns";
    protected static final String TABLE_FOTO = "fotos";
    protected static final String TABLE_SECCION = "secciones";

    // alias
    protected static final String ALIAS_PATTERN = "pt";
    protected static final String ALIAS_FOTO = "ft";
    protected static final String ALIAS_SECCION = "sc";


    // Common column namesDbInserter
    protected static final String KEY_ID = "id";
    protected static final String KEY_FECHA_CREACION = "fecha_creacion";
    protected static final String KEY_FECHA_MODIFICACION = "fecha_modificacion";
    private static final String[] KEYS_COMMON = {KEY_ID, KEY_FECHA_CREACION, KEY_FECHA_MODIFICACION};

    protected Activity context;

    public DbHelper(Activity context) {
        super(context, DB_PATH + DATABASE_NAME, null, DATABASE_VERSION);
        new File(DB_PATH).mkdirs();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableSql(TABLE_PATTERN, ALIAS_PATTERN, KEYS_COMMON, PatternDbHelper.KEYS_PATTERN));
        db.execSQL(createTableSql(TABLE_FOTO, ALIAS_FOTO, KEYS_COMMON, FotoDbHelper.KEYS_FOTO));
        db.execSQL(createTableSql(TABLE_SECCION, ALIAS_SECCION, KEYS_COMMON, SeccionDbHelper.KEYS_SECCION));

        DbInserter.insertDb(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_PATTERN);
        db.execSQL("DROP TABLE " + TABLE_FOTO);
        db.execSQL("DROP TABLE " + TABLE_SECCION);

        db.execSQL(createTableSql(TABLE_PATTERN, ALIAS_PATTERN, KEYS_COMMON, PatternDbHelper.KEYS_PATTERN));
        db.execSQL(createTableSql(TABLE_FOTO, ALIAS_FOTO, KEYS_COMMON, FotoDbHelper.KEYS_FOTO));
        db.execSQL(createTableSql(TABLE_SECCION, ALIAS_SECCION, KEYS_COMMON, SeccionDbHelper.KEYS_SECCION));

        DbInserter.insertDb(db);
    }

    /**
     * @param tableName:   el nombre de la tabla
     * @param common:      los campos comunes a las tablas: en pos 0 el id i en pos 1 la fecha de creacion, en pos 2 la fecha de modif.
     * @param columnNames: los campos
     * @return el sql
     */
    public static String createTableSql(String tableName, String tableAlias, String[] common, String[] columnNames) {
        String sql = "CREATE TABLE " + tableName + " (" +
                tableAlias + "_" + common[0] + " INTEGER PRIMARY KEY, " +
                tableAlias + "_" + common[1] + " DATETIME, " +
                tableAlias + "_" + common[2] + " DATETIME";
        for (String c : columnNames) {
            sql += ", " + tableAlias + "_" + c + " TEXT";
        }
        sql += ")";

        return sql;
    }

    /**
     * get datetime
     */
    protected static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    protected void logQuery(String log, String query) {
//        Log.e(log, query);
    }


    public static String date2string(Date date) {
        return date2string(date, "yyyy-MM-dd HH:mm");
    }

    public static String date2string(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String formatDateTime(Activity context, String timeToFormat) {
        String finalDateTime = "";

        SimpleDateFormat dbFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String outFormat = sharedPref.getString(SettingsActivity.KEY_PREF_DATE_FORMAT, context.getString(R.string.date_format_value_default));

        Date date = null;
        if (timeToFormat != null) {
            try {
                date = dbFormat.parse(timeToFormat);
            } catch (ParseException e) {
                date = null;
            }

            if (date != null) {
                return date2string(date, outFormat);
//                long when = date.getTime();
//                int flags = 0;
//                flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
//                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
//                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
//                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;
//
//                finalDateTime = android.text.format.DateUtils.formatDateTime(context,
//                        when + TimeZone.getDefault().getOffset(when), flags);
            }
        }
        return finalDateTime;
    }
}
