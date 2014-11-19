package com.lzm.KnittingHelp.db;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by DELL on 28/09/2014.
 */
public class DbInserter {

    static String tmntNombre = "Amigurumi Tiny Mutant Ninja Turtle";
    static String tmntImagen = "tmnt.jpg";
    static String tmntContenido = "BELLY PLATE\n" +
            "1: ch 5\n" +
            "2: st in 2nd from hook, st in next 2, st 3 in next. Continue on the other side of the ch, st in next 2, st 2 in next (10)\n" +
            "3: st 2 in first, st in next 2, st 2 in next 3, st in next 2, st 2 in next 2 (16)\n" +
            "4: st 2 in first, st in next 4, st 2 in next, st in next 2, st 2 in next, st in next 4, st 2 in next, st in next 2 (20)\n" +
            "5: st 2 in first, st in next 6, st 3 in next, st in next 3, st 3 in next, st in next 7, st 3 in next, sl st, finish. Leave tail for sewing (27)\n\n" +
            "SHELL\n" +
            "1: st 5 in magic ring (5)\n" +
            "2: st 2 in each around (10)\n" +
            "3: st in first, st 3 in next, st in next 2, st 3 in next, st in next 2, st 3 in next, st in next 2 (16)\n" +
            "4: st in first 2, st 2 in next, st in next 4, st 2 in next, st in next 4, st 2 in next, st in next 3 (19)\n" +
            "5: st in each around (19)\n" +
            "6: st 3 in first, st in next 7, st 3 in next, st in next 5, st 3 in next, st in next 4 (25)\n" +
            "7: st in each around (25)\n" +
            "8: st 2 in first, st in next 5, st 2 in next, st in next 5, st 2 in next, st in next 7, st 2 in next, st in next 4. Sl st, finish. Leave tail for sewing. (29)\n\n" +
            "LEGS (make 2)\n" +
            "1: st 4 in magic ring (4)\n" +
            "2: st 2 in each around (8)\n" +
            "3-7: st in each (8) Finish. Leave tail for sewing\n\n" +
            "ARMS (make 2, I used a smaller hook to make them slightly smaller than the legs)\n" +
            "1: st 4 in magic ring (4)\n" +
            "2: st 2 in each (8)\n" +
            "3-7: st in each (8)\n" +
            "8: hdc, dc, dc, hdc, st, sl st, finish. Leave tail for sewing\n\n" +
            "BODY\n" +
            "1: st 5 in magic ring (5)\n" +
            "2: st 2 in each (10)\n" +
            "3: st around (10)\n" +
            "4: (St in next, st 2 in next) around (15)\n" +
            "5: St around (15)\n" +
            "6: (St in next 2, st 2 in next) around (20)\n" +
            "7: St around (20)\n" +
            "8: (St in next 3, st 2 in next) around (25)\n" +
            "9-12: st around (25)\n" +
            "13: (st in next 3, dec over next 2) around (20)\n" +
            "14: (st in next 2, dec over next 2) around (15)\n" +
            "15: (St in next, dec over next 2) around (10) Stuff.\n" +
            "16: (Dec over next 2) around (5). Sl st, finish\n\n" +
            "MASK\n" +
            "1: ch 46 (you can make it longer if you want)\n" +
            "2: st in 2nd st from hook and in next 44. sl st, finish (45)\n\n" +
            "HEAD\n" +
            "1: st 6 in magic ring (6)\n" +
            "2: St 2 in each (12)\n" +
            "3: (St in next, st 2 in next) around (18)\n" +
            "4: (St in next 2, st 2 in next) around(24)\n" +
            "5-9: st around (24)\n" +
            "10: (St in next 2, dec over next 2) around (18)\n" +
            "11: (St in next, dec over next 2) around (12) Attach mask and eyes, embroider mouth, stuff.\n" +
            "12: (dec over next 2) around (6). Sl st, finish. Leave tail for sewing\n\n" +
            "Pin all the parts together to make sure everything is in place before sewing.\n" +
            "I added armbands by doing some slip stitches around the arms once everything was sewn together";

    static String spNombre = "Amigurumi Tiny Splinter";
    static String spImagen = "sp.jpg";
    static String spContenido = "LEGS (make 2)\n" +
            "1: st 4 in magic ring (4)\n" +
            "2: st 2 in each around (8)\n" +
            "3-7: st in each (8) Finish. Leave tail for sewing\n\n" +
            "ARMS (make 2, I used a smaller hook to make them slightly smaller than the legs)\n" +
            "1: st 4 in magic ring (4)\n" +
            "2: st 2 in each (8)\n" +
            "3-7: st in each (8)\n" +
            "8: hdc, dc, dc, hdc, st, sl st, finish. Leave tail for sewing\n\n" +
            "BODY\n" +
            "1: st 5 in magic ring (5)\n" +
            "2: st 2 in each (10)\n" +
            "3: st around (10)\n" +
            "4: (St in next, st 2 in next) around (15)\n" +
            "5: St around (15)\n" +
            "6: (St in next 2, st 2 in next) around (20)\n" +
            "7-13: st around (20) \n" +
            "14: (st in next 2, dec over next 2) around (15)\n" +
            "15: (St in next, dec over next 2) around (10). Stuff.\n" +
            "16: (Dec over next 2) around (5). Sl st, finish\n\n" +
            "EARS\n" +
            "1: ch 3\n" +
            "2: dc in first st (2nd from hook)\n" +
            "Thread the tail from the tip of the ear to the bottom to sew to the head\n\n" +
            "HEAD\n" +
            "1: st 3 in magic ring (3)\n" +
            "2: st 2 in first st, st in next, st 2 in next (5)\n" +
            "3: st 2 in first st, (st in next, st 2 in next) around (8)\n" +
            "4: (st in next, st 2 in next) around (12)\n" +
            "5: st in each around (12)\n" +
            "6: (st in next st, st 2 in next) around (18)\n" +
            "7: (st in next 2 st, 2 st in next) around (24)\n" +
            "8-9: st in each around (24)\n" +
            "10: (st in next 2 st, dec over next 2) around (18)\n" +
            "11: (st in next 4 st, dec over next 2) around (15)\n" +
            "12: (st in next 3 st, dec over next 2) around (12). Attach ears, nose and eyes\n" +
            "13: (st in next 2 st, dec over next 2) around (9). Stuff\n" +
            "14: st in first, (dec over next 2) around (5). Sl st, finish. Leave tail for sewing\n" +
            "Pin all the parts together to make sure everything is in place before sewing.\n" +
            "Chain enough stitches to make the tail as long as you want, then attach it to the body.\n\n" +
            "KIMONO\n" +
            "1: ch 21\n" +
            "2: st in 2nd st from hook and in next 19 st (20)\n" +
            "3: ch 2, turn, dc in next 20 st (20 dc)\n" +
            "4: ch 2, turn, dc in next 20 st (20 dc)\n" +
            "5: ch 2, turn, dc in next 20 st (20 dc)\n" +
            "6: ch 2, turn, dc in next 3 st, ch3 3, skip next 3 st, dc in next 7 st, ch 3, skip next 3 st, dc in last 4 st\n" +
            "7: ch 1, turn, st in next 20\n" +
            "You now have a vest. You can leave it like that, or add the sleeves.\n" +
            "To make the sleeves:\n" +
            "1: ch 9 and join with sl st to form a ring (9)\n" +
            "2: st 9 in the ring, join with sl st\n" +
            "3: ch 2, dc in next 9 st, join with sl st. Finish\n" +
            "Stitch the sleeves to the vest\n" +
            "To make the belt simply chain some stitches to make it the length you want.";

    public static void resetPatterns(Activity context) {
        Pattern tmnt = new Pattern(context, tmntNombre, tmntContenido);
        tmnt.imagen = tmntImagen;
        tmnt.saveReset();
        Pattern sp = new Pattern(context, spNombre, spContenido);
        sp.imagen = spImagen;
        sp.saveReset();
    }

    public static void insertDb(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + DbHelper.TABLE_PATTERN + " " +
                "(" + DbHelper.ALIAS_PATTERN + "_" + PatternDbHelper.KEY_ID + ", " +
                DbHelper.ALIAS_PATTERN + "_" + PatternDbHelper.KEY_NOMBRE + ", " +
                DbHelper.ALIAS_PATTERN + "_" + PatternDbHelper.KEY_IMAGEN + ", " +
                DbHelper.ALIAS_PATTERN + "_" + PatternDbHelper.KEY_CONTENIDO + ") VALUES " +
                "(\"1\", \"" + tmntNombre + "\", \"" + tmntImagen + "\", \"" + tmntContenido + "\");");

        db.execSQL("INSERT INTO " + DbHelper.TABLE_PATTERN + " " +
                "(" + DbHelper.ALIAS_PATTERN + "_" + PatternDbHelper.KEY_ID + ", " +
                DbHelper.ALIAS_PATTERN + "_" + PatternDbHelper.KEY_NOMBRE + ", " +
                DbHelper.ALIAS_PATTERN + "_" + PatternDbHelper.KEY_IMAGEN + ", " +
                DbHelper.ALIAS_PATTERN + "_" + PatternDbHelper.KEY_CONTENIDO + ") VALUES " +
                "(\"2\", \"" + spNombre + "\", \"" + spImagen + "\", \"" + spContenido + "\");");
//        db.execSQL("INSERT INTO " + DbHelper.TABLE_PATTERN + " " +
//                "(" + DbHelper.ALIAS_PATTERN + "_" + PatternDbHelper.KEY_ID + ", " +
//                DbHelper.ALIAS_PATTERN + "_" + PatternDbHelper.KEY_NOMBRE + ", " +
//                DbHelper.ALIAS_PATTERN + "_" + PatternDbHelper.KEY_CONTENIDO + ") VALUES " +
//                "(\"1\", \"" + tmntNombre + "\", \"" + tmntContenido + "\");");
//        db.execSQL("INSERT INTO " + DbHelper.TABLE_FOTO + " " +
//                "(" + DbHelper.ALIAS_FOTO + "_" + FotoDbHelper.KEY_ID + ", " +
//                DbHelper.ALIAS_FOTO + "_" + FotoDbHelper.KEY_PATTERN_ID + ", " +
//                DbHelper.ALIAS_FOTO + "_" + FotoDbHelper.KEY_PATH + ") VALUES " +
//                "(\"1\", \"1\", \"" + tmntImagen + "\");");
//
//        db.execSQL("INSERT INTO " + DbHelper.TABLE_PATTERN + " " +
//                "(" + DbHelper.ALIAS_PATTERN + "_" + PatternDbHelper.KEY_ID + ", " +
//                DbHelper.ALIAS_PATTERN + "_" + PatternDbHelper.KEY_NOMBRE + ", " +
//                DbHelper.ALIAS_PATTERN + "_" + PatternDbHelper.KEY_CONTENIDO + ") VALUES " +
//                "(\"2\", \"" + spNombre + "\", \"" + spContenido + "\");");
//        db.execSQL("INSERT INTO " + DbHelper.TABLE_FOTO + " " +
//                "(" + DbHelper.ALIAS_FOTO + "_" + FotoDbHelper.KEY_ID + ", " +
//                DbHelper.ALIAS_FOTO + "_" + FotoDbHelper.KEY_PATTERN_ID + ", " +
//                DbHelper.ALIAS_FOTO + "_" + FotoDbHelper.KEY_PATH + ") VALUES " +
//                "(\"2\", \"2\", \"" + spImagen + "\");");

    }
}
