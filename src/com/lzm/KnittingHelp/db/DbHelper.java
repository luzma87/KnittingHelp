package com.lzm.KnittingHelp.db;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

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
    private static final int DATABASE_VERSION = 1;

    // Database Name
//    private static String DB_PATH = "/data/data/com.tmm.android.chuck/databases/";
    public static String DB_PATH = Environment.getExternalStorageDirectory().getPath() + "/Knitting/db/";
    //    public static String DB_PATH = "";
    private static final String DATABASE_NAME = "knittingDb.db";

    // Table Names
    protected static final String TABLE_PATTERN = "patterns";

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
        db.execSQL(createTableSql(TABLE_PATTERN, KEYS_COMMON, PatternDbHelper.KEYS_PATTERN));

        DbInserter.insertDb(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    }

    /**
     * @param tableName:   el nombre de la tabla
     * @param common:      los campos comunes a las tablas: en pos 0 el id i en pos 1 la fecha de creacion, en pos 2 la fecha de modif.
     * @param columnNames: los campos
     * @return el sql
     */
    public static String createTableSql(String tableName, String[] common, String[] columnNames) {
        String sql = "CREATE TABLE " + tableName + " (" +
                common[0] + " INTEGER PRIMARY KEY, " +
                common[1] + " DATETIME, " +
                common[2] + " DATETIME";
        for (String c : columnNames) {
            sql += ", " + c + " TEXT";
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
        Log.e(log, query);
    }

    public static String formatDateTime(Activity context, String timeToFormat) {
        String finalDateTime = "";

        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        Date date = null;
        if (timeToFormat != null) {
            try {
                date = iso8601Format.parse(timeToFormat);
            } catch (ParseException e) {
                date = null;
            }

            if (date != null) {
                long when = date.getTime();
                int flags = 0;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

                finalDateTime = android.text.format.DateUtils.formatDateTime(context,
                        when + TimeZone.getDefault().getOffset(when), flags);
            }
        }
        return finalDateTime;
    }
}
