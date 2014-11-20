package com.lzm.KnittingHelp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.LinearLayout;
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

    int selected;

    Pattern pattern;
    Activity activity;
    ActionMode mActionMode;

    LinearLayout layout;

    List<Seccion> seccionList;
    ArrayList<Seccion> selectedSecciones;
    ArrayList<TextView> selectedTextViews;

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
                seccion.save();
                seccionList.add(seccion);
                orden++;
                for (int i = 1; i < lineas.length; i++) {
                    String strLinea = lineas[i];
                    Seccion linea = new Seccion(this);
                    linea.setPattern(pattern);
                    linea.setSeccionPadre(seccion);
                    linea.contenido = strLinea;
                    linea.orden = orden;
                    linea.save();
                    seccionList.add(linea);
                    orden++;
                }
            }
        }

        layout = (LinearLayout) findViewById(R.id.pattern_view_linear_layout);

        LinearLayout layoutSeccion = null;
        for (Seccion seccion : seccionList) {
            if (seccion.seccionPadreId == 0) {
                layoutSeccion = setSeccion(seccion);
            } // if seccion.padre.id = 0
            else {
                setLinea(seccion, layoutSeccion);
            }
        }
    } //onCreate

    private void setLinea(final Seccion seccion, LinearLayout layoutSeccion) {
        TextView txvLinea = new TextView(this);
        txvLinea.setText(seccion.contenido);
        txvLinea.setTextAppearance(this, R.style.linea);
        txvLinea.setBackgroundResource(R.drawable.selector_linea);
        txvLinea.setPadding(10, 10, 10, 10);

        LinearLayout.LayoutParams txvLineaParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txvLineaParams.setMargins(0, 0, 0, 10);
        txvLinea.setLayoutParams(txvLineaParams);

        // los listeners para el click y el long clik de la linea
        txvLinea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionMode != null) {
                    selectLinea(seccion, v);
                }
            }
        });

        txvLinea.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                if (mActionMode != null) {
                    return false;
                }

                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = startActionMode(mActionModeCallback);
                selected = SELECTED_LINEA;
                resetArrays();
                selectLinea(seccion, v);
                return true;
            }
        }); // fin listeners del nombre de la seccion

        if (layoutSeccion != null) {
            layoutSeccion.addView(txvLinea);
        }
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
        layoutSeccion.setPadding(20, 20, 20, 20);
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
                mActionMode.setTitle(strSelected + " seccion");
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
                mActionMode.setTitle(strSelected + " linea");
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
            inflater.inflate(R.menu.context_menu, menu);
            mActionMode = mode;
            return true;
        }

//        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
//            if (checked) {
//                selectedSecciones.add(seccionList.get(position));
//            } else {
//                selectedSecciones.remove(seccionList.get(position));
//            }
//            int selCount = selectedSecciones.size();
//            String strSelected = Utils.getPluralResourceByName(activity, "global_n_selected", selCount, "" + selCount);
//            mode.setTitle(strSelected);
////            mode.invalidate();  // Add this to Invalidate CAB
//        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
//                case R.id.menu_share:
////                    shareCurrentItem();
//                    mode.finish(); // Action picked, so close the CAB
//                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            closeCAB();
        }
    };

}