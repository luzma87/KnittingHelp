package com.lzm.KnittingHelp.db;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public long currentSeccionId;
    public Seccion currentSeccion = null;

    PatternDbHelper patternDbHelper;

    public Pattern(Activity context) {
        this.patternDbHelper = new PatternDbHelper(context);
    }

    public Pattern(Activity context, String nombre) {
        this.nombre = nombre;
        this.patternDbHelper = new PatternDbHelper(context);
    }

    public Pattern(Activity context, String nombre, String contenido) {
        this.nombre = nombre;
        this.contenido = contenido;
        this.patternDbHelper = new PatternDbHelper(context);
    }

    public void setCurrentSeccion(Seccion currentSeccion) {
        this.currentSeccion = currentSeccion;
        this.currentSeccionId = currentSeccion.id;
    }

    public String toString() {
        return nombre;
    }

    public void save() {
        if (this.id == 0) {
            this.fechaCreacion = DbHelper.date2string(new Date());
            this.fechaModificacion = this.fechaCreacion;
            this.id = this.patternDbHelper.create(this);
        } else {
            this.fechaModificacion = DbHelper.date2string(new Date());
            this.patternDbHelper.update(this);
        }
    }

    public void saveReset() {
        this.fechaModificacion = this.fechaCreacion;
        this.id = this.patternDbHelper.create(this);
    }

    public static Pattern get(Activity context, long id) {
        PatternDbHelper e = new PatternDbHelper(context);
        return e.get(id);
    }

    public static Pattern getConCurrentSeccion(Activity context, long id) {
        PatternDbHelper e = new PatternDbHelper(context);
        return e.getConCurrentSeccion(id);
    }

    public static ArrayList<Pattern> list(Activity context) {
        PatternDbHelper e = new PatternDbHelper(context);
        return e.getAll();
    }


    public void delete(Activity context) {
        PatternDbHelper e = new PatternDbHelper(context);
        Foto.deleteAllByPattern(context, this);
        Seccion.deleteAllByPattern(context, this);
        e.delete(this);
    }
}
