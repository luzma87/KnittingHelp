package com.lzm.KnittingHelp.db;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by DELL on 29/10/2014.
 */
public class Seccion {
    public static final int TIPO_SECCION = 0;
    public static final int TIPO_LINEA = 1;
    public static final int TIPO_CHUNK = 2;

    public long id = 0;
    public String fechaCreacion;
    public String fechaModificacion;

    public long patternId;
    public Pattern pattern;

    public String contenido;
    public String imagen;
    public int orden;

    public long seccionPadreId;
    public Seccion seccionPadre;

    public int tipo;

    public String separador;

    SeccionDbHelper seccionDbHelper;

    public Seccion(Activity context) {
        this.seccionDbHelper = new SeccionDbHelper(context);
    }

    public String toString() {
        return contenido;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
        this.patternId = pattern.id;
    }

    public void setSeccionPadre(Seccion seccionPadre) {
        this.seccionPadre = seccionPadre;
        this.seccionPadreId = seccionPadre.id;
    }

    public void save() {
        if (this.separador == null) {
            this.separador = "";
        }
        if (this.id == 0) {
            this.fechaCreacion = DbHelper.date2string(new Date());
            this.fechaModificacion = this.fechaCreacion;
            this.id = this.seccionDbHelper.create(this);
        } else {
            this.fechaModificacion = DbHelper.date2string(new Date());
            this.seccionDbHelper.update(this);
        }
    }

    public static Seccion get(Activity context, long id) {
        SeccionDbHelper e = new SeccionDbHelper(context);
        return e.get(id);
    }

    public static ArrayList<Seccion> list(Activity context) {
        SeccionDbHelper e = new SeccionDbHelper(context);
        return e.getAll();
    }

    public static ArrayList<Seccion> findAllByPattern(Activity context, Pattern pattern) {
        SeccionDbHelper e = new SeccionDbHelper(context);
        return e.getAllByPattern(pattern);
    }

    public static ArrayList<Seccion> findAllSeccionesByPattern(Activity context, Pattern pattern) {
        SeccionDbHelper e = new SeccionDbHelper(context);
        return e.getAllByPatternAndTipo(pattern, TIPO_SECCION);
    }

    public static ArrayList<Seccion> findAllLineasByPattern(Activity context, Pattern pattern) {
        SeccionDbHelper e = new SeccionDbHelper(context);
        return e.getAllByPatternAndTipo(pattern, TIPO_LINEA);
    }

    public static ArrayList<Seccion> findAllChunksByPattern(Activity context, Pattern pattern) {
        SeccionDbHelper e = new SeccionDbHelper(context);
        return e.getAllByPatternAndTipo(pattern, TIPO_CHUNK);
    }

    public static void updateOrdenIncludes(Activity context, Seccion seccion, int cant) {
        SeccionDbHelper e = new SeccionDbHelper(context);
        e.updateOrdenFromSeccion(seccion, cant, true);
    }


    public static int updateOrdenExcludes(Activity context, Seccion seccion, int cant) {
        SeccionDbHelper e = new SeccionDbHelper(context);
        return e.updateOrdenFromSeccion(seccion, cant, false);
    }

    public int updateOrdenExcludes(int cant) {
        return this.seccionDbHelper.updateOrdenFromSeccion(this, cant, false);
    }

    public int delete() {
        return this.seccionDbHelper.delete(this);
    }

    public static void deleteAllByPattern(Activity context, Pattern pattern) {
        SeccionDbHelper e = new SeccionDbHelper(context);
        e.deleteByPattern(pattern);
    }
}
