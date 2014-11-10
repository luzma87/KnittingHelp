package com.lzm.KnittingHelp.db;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by DELL on 29/10/2014.
 */
public class Foto {
    public long id = 0;
    public String fechaCreacion;
    public String fechaModificacion;

    public long patternId;
    public String path;

    public Pattern pattern;

    FotoDbHelper fotoDbHelper;

    public Foto(Activity context) {
        this.fotoDbHelper = new FotoDbHelper(context);
    }

    public Foto(Activity context, Pattern pattern, String path) {
        this.fotoDbHelper = new FotoDbHelper(context);
        this.patternId = pattern.id;
        this.path = path;
    }

    public void save() {
        if (this.id == 0) {
            this.fechaCreacion = DbHelper.date2string(new Date());
            this.fechaModificacion = this.fechaCreacion;
            this.id = this.fotoDbHelper.create(this);
        } else {
            this.fechaModificacion = DbHelper.date2string(new Date());
            this.fotoDbHelper.update(this);
        }
    }

    public static Foto get(Activity context, long id) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.get(id);
    }

    public static ArrayList<Foto> list(Activity context) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.getAll();
    }

    public static ArrayList<Foto> findAllByPattern(Activity context, Pattern pattern) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.getAllByPattern(pattern);
    }

    public void delete(Activity context) {
        FotoDbHelper e = new FotoDbHelper(context);
        e.delete(this);
    }

    public static void deleteAllByPattern(Activity context, Pattern pattern) {
        FotoDbHelper e = new FotoDbHelper(context);
        e.deleteByPattern(pattern);
    }
}
