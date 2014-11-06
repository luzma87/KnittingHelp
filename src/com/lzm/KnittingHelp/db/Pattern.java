package com.lzm.KnittingHelp.db;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by DELL on 29/10/2014.
 */
public class Pattern {
    public long id = 0;
    public String fechaCreacion;
    public String fechaModificacion;

    public String nombre;
    public String contenido;
    public String imagen;

    PatternDbHelper patternDbHelper;

    public Pattern(Activity context) {
    }

    public Pattern(Activity context, String nombre) {
        this.nombre = nombre;
    }

    public Pattern(Activity context, String nombre, String contenido) {
        this.nombre = nombre;
        this.contenido = contenido;
    }

    public String toString() {
        return nombre;
    }

    public void save() {
        if (this.id == 0) {
            this.fechaCreacion = DbHelper.date2string(new Date());
            this.id = this.patternDbHelper.create(this);
        } else {
            this.fechaModificacion = DbHelper.date2string(new Date());
            this.patternDbHelper.update(this);
        }
    }

    public static Pattern get(Activity context, long id) {
        PatternDbHelper e = new PatternDbHelper(context);
        return e.get(id);
    }

    public static ArrayList<Pattern> list(Activity context) {
        PatternDbHelper e = new PatternDbHelper(context);
        return e.getAll();
    }


    public void delete(Activity context) {
        PatternDbHelper e = new PatternDbHelper(context);
        e.delete(this);
    }
}
