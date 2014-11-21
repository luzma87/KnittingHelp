package com.lzm.KnittingHelp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.lzm.KnittingHelp.R;
import com.lzm.KnittingHelp.db.Pattern;
import com.lzm.KnittingHelp.db.Seccion;
import com.lzm.KnittingHelp.fragments.PatternsListFragment;
import com.lzm.KnittingHelp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luz on 19/11/14.
 */
public class PatternViewActivity extends Activity {
    final int SELECTED_SECCION = 0;
    final int SELECTED_LINEA = 1;
    final int SELECTED_CHUNK = 2;

    int selected;

    Pattern pattern;
    Activity activity;
    ActionMode mActionMode;

    LinearLayout layout;

    List<Seccion> seccionList;
    ArrayList<Seccion> selectedSecciones;
    ArrayList<TextView> selectedTextViews;

    public int screenHeight;
    public int screenWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_view_layout);

        activity = this;

        // Get the message from the intent
        Intent intent = getIntent();
        long patternId = intent.getLongExtra(PatternsListFragment.PATTERN_ID_MESSAGE, 0);
        pattern = Pattern.get(this, patternId);

        getActionBar().setTitle(pattern.nombre);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;

        seccionList = Seccion.findAllByPattern(this, pattern);
        selectedSecciones = new ArrayList<Seccion>();
        selectedTextViews = new ArrayList<TextView>();

        if (seccionList.size() == 0) {
            String contenido = pattern.contenido;
            String[] secciones = contenido.split("\n\n");
            int orden = 1;

            for (String strSeccion : secciones) {
                String[] lineas = strSeccion.split("\n");
                String nombreSeccion = lineas[0];
                Seccion seccion = new Seccion(this);
                seccion.setPattern(pattern);
                seccion.contenido = nombreSeccion;
                seccion.orden = orden;
                seccion.tipo = Seccion.TIPO_SECCION;
                seccion.save();
                seccionList.add(seccion);
                orden++;
                for (int i = 1; i < lineas.length; i++) {
                    String strLinea = lineas[i];
                    Seccion linea = new Seccion(this);
                    linea.setPattern(pattern);
                    linea.setSeccionPadre(seccion);
                    linea.contenido = "";
                    linea.orden = orden;
                    linea.tipo = Seccion.TIPO_LINEA;
                    linea.save();
                    seccionList.add(linea);
                    orden++;

                    String[] chunks = strLinea.split("[.,]");
                    for (String strChunk : chunks) {
                        Seccion chunk = new Seccion(this);
                        chunk.setPattern(pattern);
                        chunk.setSeccionPadre(linea);
                        chunk.contenido = strChunk;
                        chunk.orden = orden;
                        chunk.tipo = Seccion.TIPO_CHUNK;
                        chunk.save();
                        seccionList.add(chunk);
                        orden++;
                    }
                }
            }
        }

        layout = (LinearLayout) findViewById(R.id.pattern_view_linear_layout);

        LinearLayout layoutSeccion = null;
        LinearLayout layoutLinea = null;
        List<View> listChunks = new ArrayList<View>();

        for (Seccion seccion : seccionList) {
            if (seccion.tipo == Seccion.TIPO_SECCION) {
                layoutSeccion = setSeccion(seccion);
            } // if seccion.tipo == TIPO_SECCION
            else if (seccion.tipo == Seccion.TIPO_LINEA) {
                if (layoutLinea != null) {
                    populateViews(layoutLinea, listChunks, this, null);
                    listChunks = new ArrayList<View>();
                }
                layoutLinea = setLinea(seccion, layoutSeccion);
            } // if seccion.tipo == TIPO_LINEA
            else if (seccion.tipo == Seccion.TIPO_CHUNK) {
                listChunks.add(setChunk(seccion));
            } // if seccion.tipo == TIPO_CHUNK
        }
    } //onCreate

    private View setChunk(final Seccion seccion) {
        TextView txvChunk = new TextView(this);
        txvChunk.setText(seccion.contenido);
        txvChunk.setTextAppearance(this, R.style.chunk);
        txvChunk.setBackgroundResource(R.drawable.selector_chunk);
        txvChunk.setPadding(10, 10, 10, 10);

        LinearLayout.LayoutParams txvLineaParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txvLineaParams.setMargins(0, 0, 0, 10);
        txvChunk.setLayoutParams(txvLineaParams);

        // los listeners para el click y el long clik de la linea
        txvChunk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionMode != null) {
                    selectChunk(seccion, v);
                }
            }
        });

        txvChunk.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                if (mActionMode != null) {
                    return false;
                }

                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = startActionMode(mActionModeCallback);
                selected = SELECTED_CHUNK;
                resetArrays();
                selectChunk(seccion, v);
                return true;
            }
        }); // fin listeners del nombre de la seccion
        return txvChunk;
    }

    private LinearLayout setLinea(final Seccion seccion, LinearLayout layoutSeccion) {
        // este es el linear layout donde van a ir los chunks de la linea
        final LinearLayout layoutLinea = new LinearLayout(this);
        layoutLinea.setOrientation(LinearLayout.HORIZONTAL);
        layoutLinea.setBackgroundResource(R.color.linea_content);
        layoutLinea.setPadding(10, 10, 10, 10);
        LinearLayout.LayoutParams layoutLineaParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutLineaParams.setMargins(0, 0, 0, 10);
        layoutLinea.setLayoutParams(layoutLineaParams);
        layoutSeccion.addView(layoutLinea);
        return layoutLinea;
    }

    private LinearLayout setSeccion(final Seccion seccion) {
        // Aqui crea el textView para el nombre de la seccion
        final TextView txvSeccion = new TextView(this);
        txvSeccion.setText(seccion.contenido);
        txvSeccion.setTextAppearance(this, R.style.seccionNombre);
        txvSeccion.setBackgroundResource(R.drawable.selector_seccion);
        final LinearLayout.LayoutParams txvSeccionParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txvSeccion.setLayoutParams(txvSeccionParams);
        txvSeccion.setPadding(20, 20, 20, 20);
        layout.addView(txvSeccion);

        // este es el linear layout donde van a ir las lineas de la seccion
        final LinearLayout layoutSeccion = new LinearLayout(this);
        layoutSeccion.setOrientation(LinearLayout.VERTICAL);
        layoutSeccion.setBackgroundResource(R.color.seccion_content);
        layoutSeccion.setPadding(10, 10, 10, 10);
        LinearLayout.LayoutParams layoutSeccionParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutSeccionParams.setMargins(0, 0, 0, 25);
        layoutSeccion.setLayoutParams(layoutSeccionParams);

        // los listeners para el click y el long clik del nombre de la seccion
        txvSeccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionMode == null) {
                    if (layoutSeccion.isShown()) {
                        layoutSeccion.setVisibility(View.GONE);
                        txvSeccionParams.setMargins(0, 0, 0, 20);
                        txvSeccion.setLayoutParams(txvSeccionParams);
                    } else {
                        layoutSeccion.setVisibility(View.VISIBLE);
                        txvSeccionParams.setMargins(0, 0, 0, 0);
                        txvSeccion.setLayoutParams(txvSeccionParams);
                    }
                } else {
                    selectSeccion(seccion, v);
                }
            }
        });

        txvSeccion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                if (mActionMode != null) {
                    return false;
                }

                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = startActionMode(mActionModeCallback);
                selected = SELECTED_SECCION;
                resetArrays();
                selectSeccion(seccion, v);
                return true;
            }
        }); // fin listeners del nombre de la seccion
        layout.addView(layoutSeccion);

        return layoutSeccion;
    }

    private void selectSeccion(Seccion seccion, View v) {
        if (selected == SELECTED_SECCION) {
            TextView tv = (TextView) v;
            if (v.isSelected()) {
                v.setSelected(false);
                selectedSecciones.remove(seccion);
                selectedTextViews.remove(tv);
            } else {
                v.setSelected(true);
                selectedSecciones.add(seccion);
                selectedTextViews.add(tv);
            }
            int selCount = selectedSecciones.size();
            if (selCount == 0) {
                mActionMode.finish();
            } else {
                String strSelected = Utils.getPluralResourceByName(activity, "global_n_selected", selCount, "" + selCount);
                mActionMode.setTitle(strSelected);
                mActionMode.invalidate();
            }
        }
    }

    private void selectLinea(Seccion seccion, View v) {
        if (selected == SELECTED_LINEA) {
            TextView tv = (TextView) v;
            if (v.isSelected()) {
                v.setSelected(false);
                selectedSecciones.remove(seccion);
                selectedTextViews.remove(tv);
            } else {
                v.setSelected(true);
                selectedSecciones.add(seccion);
                selectedTextViews.add(tv);
            }
            int selCount = selectedSecciones.size();
            if (selCount == 0) {
                mActionMode.finish();
            } else {
                String strSelected = Utils.getPluralResourceByName(activity, "global_n_selected", selCount, "" + selCount);
                mActionMode.setTitle(strSelected);
                mActionMode.invalidate();
            }
        }
    }

    private void selectChunk(Seccion seccion, View v) {
        if (selected == SELECTED_CHUNK) {
            TextView tv = (TextView) v;
            if (v.isSelected()) {
                v.setSelected(false);
                selectedSecciones.remove(seccion);
                selectedTextViews.remove(tv);
            } else {
                v.setSelected(true);
                selectedSecciones.add(seccion);
                selectedTextViews.add(tv);
            }
            int selCount = selectedSecciones.size();
            if (selCount == 0) {
                mActionMode.finish();
            } else {
                String strSelected = Utils.getPluralResourceByName(activity, "global_n_selected", selCount, "" + selCount);
                mActionMode.setTitle(strSelected);
                mActionMode.invalidate();
            }
        }
    }

    public void resetArrays() {
        selectedSecciones = new ArrayList<Seccion>();
        for (TextView selectedTextView : selectedTextViews) {
            selectedTextView.setSelected(false);
        }
        selectedTextViews = new ArrayList<TextView>();
    }

    public void closeCAB() {
        mActionMode = null;
        resetArrays();
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.pattern_view_contextual_action_bar, menu);
            mActionMode = mode;
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated. (Used for updates to CAB after invalidate() request)
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if (selected == SELECTED_SECCION) {
                // si es seccion escondo el menu de linea
                menu.findItem(R.id.pattern_view_cab_linea).setVisible(false);
                menu.findItem(R.id.pattern_view_cab_seccion).setVisible(true);
                /*
                    cambiar a linea             1-n     cab_seccion_linea
                    duplicar                    1-n     cab_seccion_duplicar
                    editar                      1       cab_seccion_editar
                    eliminar                    1-n     cab_seccion_eliminar
                 */
                if (selectedSecciones.size() == 1) {
                    menu.findItem(R.id.cab_seccion_editar).setVisible(true);
                } else {
                    menu.findItem(R.id.cab_seccion_editar).setVisible(false);
                }
            } else if (selected == SELECTED_LINEA) {
                // si es linea escondo el menu seccion
                menu.findItem(R.id.pattern_view_cab_seccion).setVisible(false);
                menu.findItem(R.id.pattern_view_cab_linea).setVisible(true);
                /*
                    editar                              1       cab_linea_editar
                    eliminar                            1-n     cab_linea_eliminar
                    separar en  .   ,   ;   (   [       1-n     cab_linea_split
                    duplicar                            1-n     cab_linea_duplicar
                    unir con    .   ,   ;   _           n       cab_linea_join
                    hacer nueva seccion                 1-n     cab_linea_new
                 */
                menu.findItem(R.id.cab_linea_new).setVisible(true);
                if (selectedSecciones.size() == 1) {
                    menu.findItem(R.id.cab_linea_editar).setVisible(true);
                    menu.findItem(R.id.cab_linea_join).setVisible(false);
                } else {
                    menu.findItem(R.id.cab_linea_editar).setVisible(false);
                    menu.findItem(R.id.cab_linea_join).setVisible(true);
                }
            } else if (selected == SELECTED_CHUNK) {
                // si es linea escondo el menu seccion
                menu.findItem(R.id.pattern_view_cab_seccion).setVisible(false);
                menu.findItem(R.id.pattern_view_cab_linea).setVisible(true);
                /*
                    editar                              1       cab_linea_editar
                    eliminar                            1-n     cab_linea_eliminar
                    separar en  .   ,   ;   (   [       1-n     cab_linea_split
                    duplicar                            1-n     cab_linea_duplicar
                    unir con    .   ,   ;   _           n       cab_linea_join
                    hacer nueva seccion                 1-n     cab_linea_new
                 */
                menu.findItem(R.id.cab_linea_new).setVisible(false);
                if (selectedSecciones.size() == 1) {
                    menu.findItem(R.id.cab_linea_editar).setVisible(true);
                    menu.findItem(R.id.cab_linea_join).setVisible(false);
                } else {
                    menu.findItem(R.id.cab_linea_editar).setVisible(false);
                    menu.findItem(R.id.cab_linea_join).setVisible(true);
                }
            }
            return true;
//            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                // OPCIONES PARA LAS SECCIONES
                case R.id.cab_seccion_linea:
                    //cambiar seccion => linea
                    break;
                case R.id.cab_seccion_duplicar:
                    //duplicar la seccion con todas sus lineas
                    break;
                case R.id.cab_seccion_editar:
                    //textView = > editText + btn guardar
                    break;
                case R.id.cab_seccion_eliminar:
                    //elimina la seccion con todas sus lineas
                    break;
                // OPCIONES PARA LAS LINEAS
                case R.id.cab_linea_editar:
                    //textView = > editText + btn guardar
                    break;
                case R.id.cab_linea_eliminar:
                    //elimina la linea
                    break;
                case R.id.cab_linea_split:
                    //muestra opciones para separar en .    ,   ;   (   [
                    break;
                case R.id.cab_linea_duplicar:
                    //duplica la linea
                    break;
                case R.id.cab_linea_join:
                    //muestra opciones para unir con .  ,   ;   [_]
                    break;
                case R.id.cab_linea_new:
                    //crea una nueva seccion con las lineas seleccionadas
                    break;
                default:
                    return false;
            }
            mode.finish(); // Action picked, so close the CAB
            return true;
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            closeCAB();
        }
    };

    /**
     * Copyright 2011 Sherif
     * Updated by Karim Varela to handle LinearLayouts with other views on either side.
     *
     * @param linearLayout
     * @param views        : The views to wrap within LinearLayout
     * @param context
     * @param extraView    : An extra view that may be to the right or left of your LinearLayout.
     * @author Karim Varela
     */
    private void populateViews(LinearLayout linearLayout, List<View> views, Context context, View extraView) {
        if (extraView != null) {
            extraView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        // kv : May need to replace 'getSherlockActivity()' with 'this' or 'getActivity()'
//        Display display = activity.getWindowManager().getDefaultDisplay();
        linearLayout.removeAllViews();
        int maxWidth = screenWidth - 80;
//        if (extraView != null) {
//            maxWidth = display.getWidth() - extraView.getMeasuredWidth() - 20;
//        } else {
//            maxWidth = display.getWidth() ;
//        }
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params;
        LinearLayout newLL = new LinearLayout(context);
        newLL.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newLL.setGravity(Gravity.LEFT);
        newLL.setOrientation(LinearLayout.HORIZONTAL);

        int widthSoFar = 0;

        for (int i = 0; i < views.size(); i++) {
            LinearLayout LL = new LinearLayout(context);
            LL.setOrientation(LinearLayout.HORIZONTAL);
            LL.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            LL.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            views.get(i).measure(0, 0);
            params = new LinearLayout.LayoutParams(views.get(i).getMeasuredWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 0, 5, 0);

            LL.addView(views.get(i), params);
            LL.measure(0, 0);
            widthSoFar += views.get(i).getMeasuredWidth();
            if (widthSoFar >= maxWidth) {
                linearLayout.addView(newLL);

                newLL = new LinearLayout(context);
                newLL.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                newLL.setOrientation(LinearLayout.HORIZONTAL);
                newLL.setGravity(Gravity.LEFT);
                params = new LinearLayout.LayoutParams(LL.getMeasuredWidth(), LL.getMeasuredHeight());
                newLL.addView(LL, params);
                widthSoFar = LL.getMeasuredWidth();
            } else {
                newLL.addView(LL);
            }
        }
        linearLayout.addView(newLL);
    }

}