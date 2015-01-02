package com.lzm.KnittingHelp.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.lzm.KnittingHelp.R;
import com.lzm.KnittingHelp.db.Pattern;
import com.lzm.KnittingHelp.db.Seccion;
import com.lzm.KnittingHelp.fragments.PatternsListFragment;
import com.lzm.KnittingHelp.utils.Utils;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luz on 19/11/14.
 * <p/>
 * <div>Icon made by <a href="http://www.typicons.com" title="Stephen Hutchings">Stephen Hutchings</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed under <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0">CC BY 3.0</a></div>
 * <div>Icon made by <a href="http://graphberry.com" title="GraphBerry">GraphBerry</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed under <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0">CC BY 3.0</a></div>
 * <div>Icon made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed under <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0">CC BY 3.0</a></div>
 */
public class PatternViewActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {
    final int SELECTED_SECCION = 0;
    final int SELECTED_CHUNK = 1;

    int selected;
    Seccion current;
    int currentPos = -1;

    int lastScroll = 0;
    int firstViewTop = 0;

    boolean changed = false;

    Pattern pattern;
    Activity activity;
    ActionMode mActionMode;
    Menu mMenu;

    boolean editing = false;
    EditText editText = null;
    TextView editingTv = null;
    LinearLayout editingTvParent = null;
    Seccion editingSeccion = null;

    LinearLayout layout;
    ScrollView scrollView;

    List<Seccion> seccionList;
    List<TextView> textViewList;
    ArrayList<Seccion> selectedSecciones;
    ArrayList<TextView> selectedTextViews;

    public int screenHeight;
    public int screenWidth;

    int fontSize = 12;

    ImageButton btnPrevSeccion;
    ImageButton btnPrevChunk;
    ImageButton btnNextChunk;
    ImageButton btnNextSeccion;

    ImageButton btnCounters;
//    LinearLayout layoutCounter1;
//    LinearLayout layoutCounter2;
//    Button btnCounter1Menos;
//    Button btnCounter1Mas;
//    Button btnCounter2Menos;
//    Button btnCounter2Mas;

    LinearLayout[] layoutsCounters;
    Button[] btnCountersMas;
    Button[] btnCountersMenos;
    int[] counters;

//    int counter1 = 1;
//    int counter2 = 1;

    int scrollThresh = 55;

    float density;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_view_layout);

        activity = this;

        density = getResources().getDisplayMetrics().density;

        // Get the message from the intent
        Intent intent = getIntent();
        long patternId = intent.getLongExtra(PatternsListFragment.PATTERN_ID_MESSAGE, 0);
        pattern = Pattern.getConCurrentSeccion(this, patternId);
        current = pattern.currentSeccion;

        getActionBar().setTitle(pattern.nombre);

        btnPrevSeccion = (ImageButton) findViewById(R.id.pattern_view_btn_prev_seccion);
        btnPrevChunk = (ImageButton) findViewById(R.id.pattern_view_btn_prev_linea);
        btnNextChunk = (ImageButton) findViewById(R.id.pattern_view_btn_next_linea);
        btnNextSeccion = (ImageButton) findViewById(R.id.pattern_view_btn_next_seccion);

        btnCounters = (ImageButton) findViewById(R.id.pattern_view_btn_counters);

        btnCounters.setOnClickListener(this);

        layoutsCounters = new LinearLayout[3];
        btnCountersMas = new Button[3];
        btnCountersMenos = new Button[3];
        counters = new int[3];

        layoutsCounters[0] = (LinearLayout) findViewById(R.id.pattern_view_layout_counter1);
        layoutsCounters[1] = (LinearLayout) findViewById(R.id.pattern_view_layout_counter2);
        layoutsCounters[2] = (LinearLayout) findViewById(R.id.pattern_view_layout_counter3);

        btnCountersMas[0] = (Button) findViewById(R.id.pattern_view_btn_counter1_mas);
        btnCountersMas[1] = (Button) findViewById(R.id.pattern_view_btn_counter2_mas);
        btnCountersMas[2] = (Button) findViewById(R.id.pattern_view_btn_counter3_mas);

        btnCountersMenos[0] = (Button) findViewById(R.id.pattern_view_btn_counter1_menos);
        btnCountersMenos[1] = (Button) findViewById(R.id.pattern_view_btn_counter2_menos);
        btnCountersMenos[2] = (Button) findViewById(R.id.pattern_view_btn_counter3_menos);

        for (Button button : btnCountersMas) {
            button.setOnClickListener(this);
            button.setOnLongClickListener(this);
        }
        for (Button button : btnCountersMenos) {
            button.setOnClickListener(this);
        }
        for (int i = 0; i < counters.length; i++) {
            counters[i] = 1;
        }


//        layoutCounter1 = (LinearLayout) findViewById(R.id.pattern_view_layout_counter1);
//        layoutCounter2 = (LinearLayout) findViewById(R.id.pattern_view_layout_counter2);
//        btnCounters = (ImageButton) findViewById(R.id.pattern_view_btn_counters);
//        btnCounter1Menos = (Button) findViewById(R.id.pattern_view_btn_counter1_menos);
//        btnCounter1Mas = (Button) findViewById(R.id.pattern_view_btn_counter1_mas);
//        btnCounter2Menos = (Button) findViewById(R.id.pattern_view_btn_counter2_menos);
//        btnCounter2Mas = (Button) findViewById(R.id.pattern_view_btn_counter2_mas);

//        btnCounter1Menos.setOnClickListener(this);
//        btnCounter1Mas.setOnClickListener(this);
//        btnCounter2Menos.setOnClickListener(this);
//        btnCounter2Mas.setOnClickListener(this);
//
//        btnCounter1Mas.setOnLongClickListener(this);
//        btnCounter2Mas.setOnLongClickListener(this);

        btnPrevSeccion.setOnClickListener(this);
        btnPrevChunk.setOnClickListener(this);
        btnNextChunk.setOnClickListener(this);
        btnNextSeccion.setOnClickListener(this);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;

        textViewList = new ArrayList<TextView>();
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

//                    String[] chunks = strLinea.split("[.,]");
                    String[] chunks = new String[1];
                    chunks[0] = strLinea;
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
        scrollView = (ScrollView) findViewById(R.id.pattern_view_scroll);

        populateAll();
    } //onCreate

    private void populateAll() {
        layout.removeAllViews();

        LinearLayout layoutSeccion = null;
        LinearLayout layoutLinea = null;
        List<View> listChunks = new ArrayList<View>();

//        System.out.println("CURRENT:::: " + current.id);

        textViewList = new ArrayList<TextView>();

        int pos = 0;
        for (Seccion seccion : seccionList) {
//            System.out.println("seccion id: " + seccion.id + "     current id: " + current.id);
            if (seccion.id == current.id) {
                currentPos = pos;
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//                System.out.println("POS= " + pos + "    current pos .. .. " + currentPos);
            }
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
//            System.out.println("............................... POS = " + pos + "    current pos .. .. " + currentPos);
            pos++;
        }
        if (layoutLinea != null) {
            populateViews(layoutLinea, listChunks, this, null);
        }
//        System.out.println(":::END::: " + currentPos);

        if (current != null && current.id > 0) {
            setChunkSelected(current);

            final TextView currentTv = textViewList.get(currentPos);

            ViewTreeObserver viewTreeObserver = currentTv.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        currentTv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        moveToChunk(currentTv);
//                        Rect rectTv = new Rect();
//                        currentTv.getGlobalVisibleRect(rectTv);
//                        Rect rectLl = new Rect();
//                        layout.getGlobalVisibleRect(rectLl);
//
//                        int currentChunkRealHeight = currentTv.getHeight();
//                        int currentChunkVisibleBottom = rectTv.bottom;
//                        int currentChunkRealTop = currentChunkVisibleBottom - currentChunkRealHeight;
//
//                        scrollView.scrollBy(0, currentChunkRealTop - scrollThresh);
                    }
                });
            }
        } else {
            moveToChunk(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.view_menu_edit_btn:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.pttern_view_dialog_edit_title)
                        .setMessage(R.string.pttern_view_dialog_edit_contenido)
                        .setPositiveButton(R.string.global_continuar, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(activity, PatternEditActivity.class);
                                intent.putExtra(PatternsListFragment.PATTERN_ID_MESSAGE, pattern.id);
                                startActivity(intent);
                            }

                        })
                        .setNegativeButton(R.string.global_cancel, null)
                        .show();

                return true;
            case R.id.view_menu_font_size_increase_btn:

                if (fontSize < 25) {
                    fontSize += 2;
                } else {
                    fontSize = 26;
                }
                populateAll();
                return true;
            case R.id.view_menu_font_size_decrease_btn:
                if (fontSize > 9) {
                    fontSize -= 2;
                } else {
                    fontSize = 8;
                }
                populateAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private View setChunk(final Seccion seccion) {
        TextView txvChunk = new TextView(this);
        txvChunk.setText(seccion.contenido);
        txvChunk.setMaxLines(20);
        txvChunk.setMaxWidth(screenWidth - 100);
        txvChunk.setTextAppearance(this, R.style.chunk);
        txvChunk.setBackgroundResource(R.drawable.selector_chunk);
        txvChunk.setPadding(10, 10, 10, 10);
        txvChunk.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

        LinearLayout.LayoutParams txvChunkParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txvChunkParams.setMargins(0, 0, 0, 10);
        txvChunk.setLayoutParams(txvChunkParams);

        // los listeners para el click y el long clik de la linea
        txvChunk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mActionMode != null) {
//                    selectChunk(seccion, v);
//                }
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
                resetSelectedArrays();
                selectChunk(seccion, v);
                return true;
            }
        }); // fin listeners del nombre de la seccion
        textViewList.add(txvChunk);
        return txvChunk;
    }

    private LinearLayout setLinea(final Seccion seccion, LinearLayout layoutSeccion) {
        textViewList.add(null);
        return setLinea(seccion, layoutSeccion, -1);
    }

    private LinearLayout setLinea(final Seccion seccion, LinearLayout layoutSeccion, int pos) {
        // este es el linear layout donde van a ir los chunks de la linea
        final LinearLayout layoutLinea = new LinearLayout(this);
        layoutLinea.setOrientation(LinearLayout.HORIZONTAL);
        layoutLinea.setBackgroundResource(R.color.linea_content);
        layoutLinea.setPadding(10, 10, 10, 10);
        LinearLayout.LayoutParams layoutLineaParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutLineaParams.setMargins(0, 0, 0, 10);
        layoutLinea.setLayoutParams(layoutLineaParams);
        if (pos > -1) {
            layoutSeccion.addView(layoutLinea, pos);
        } else {
            layoutSeccion.addView(layoutLinea);
        }
        return layoutLinea;
    }

    private LinearLayout setSeccion(final Seccion seccion) {
        // Aqui crea el textView para el nombre de la seccion
        final TextView txvSeccion = new TextView(this);
        txvSeccion.setText(seccion.contenido);
        txvSeccion.setTextAppearance(this, R.style.seccionNombre);
        txvSeccion.setBackgroundResource(R.drawable.selector_seccion);
        txvSeccion.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize + 2);
        final LinearLayout.LayoutParams txvSeccionParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txvSeccion.setLayoutParams(txvSeccionParams);
        txvSeccion.setPadding(20, 20, 20, 20);
        layout.addView(txvSeccion);
        textViewList.add(txvSeccion);

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
//                if (mActionMode != null) {
//                    selectSeccion(seccion, v);
//                }
            }
        });

//        txvSeccion.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                // TODO Auto-generated method stub
//                if (mActionMode != null) {
//                    return false;
//                }
//
//                // Start the CAB using the ActionMode.Callback defined above
//                mActionMode = startActionMode(mActionModeCallback);
//                selected = SELECTED_SECCION;
//                resetSelectedArrays();
//                selectSeccion(seccion, v);
//                return true;
//            }
//        }); // fin listeners del nombre de la seccion
        layout.addView(layoutSeccion);

        return layoutSeccion;
    }

    private void selectSeccion(Seccion seccion, View v) {
        if (!editing && selected == SELECTED_SECCION) {
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
        if (!editing && selected == SELECTED_CHUNK) {
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

    public void resetSelectedArrays() {
        selectedSecciones = new ArrayList<Seccion>();
        for (TextView selectedTextView : selectedTextViews) {
            selectedTextView.setSelected(false);
        }
        selectedTextViews = new ArrayList<TextView>();
    }

    public void closeCAB() {
        mActionMode = null;
        resetSelectedArrays();
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
//            mMenu = menu;
//            if (selected == SELECTED_CHUNK) {
//                menu.findItem(R.id.cab_linea_new).setVisible(false);
//                if (selectedSecciones.size() == 1) {
//                    menu.findItem(R.id.cab_linea_editar).setVisible(true);
//                    menu.findItem(R.id.cab_linea_join).setVisible(false);
//                } else {
//                    menu.findItem(R.id.cab_linea_editar).setVisible(false);
//                    menu.findItem(R.id.cab_linea_join).setVisible(true);
//                }
//            }
//            return true;
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.pattern_view_cab_active:
                    //activa el chunk seleccionado
                    TextView chunk = selectedTextViews.get(0);
                    setChunkSelected(chunk);
                    mode.finish();
                    break;
                default:
                    return false;
            }
//            mode.finish(); // Action picked, so close the CAB
            //sss
            return true;
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            closeCAB();
        }
    };

    private void setChunkSelected(TextView view) {
//        System.out.println("********************* set chunk selected");
//        System.out.println("currentPos = " + currentPos);
        if (currentPos > -1) {
            TextView sel = textViewList.get(currentPos);
            sel.setBackgroundResource(R.drawable.selector_chunk);
        }
        currentPos = textViewList.indexOf(view);
        Seccion chunk = seccionList.get(currentPos);
        current = chunk;
        view.setBackgroundResource(R.drawable.selector_current_chunk);
        pattern.setCurrentSeccion(chunk);
        pattern.save();
    }

    private long getCurrentChunk(Seccion chunk) {
        long pos = 0;
        for (Seccion seccion : seccionList) {
            if (seccion.id == chunk.id) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    private void setChunkSelected(Seccion chunk) {
//        System.out.println("********************* set chunk selected");
//        System.out.println("currentPos = " + currentPos);
        if (currentPos > -1) {
            TextView sel = textViewList.get(currentPos);
            sel.setBackgroundResource(R.drawable.selector_chunk);
        }
        currentPos = (int) getCurrentChunk(chunk);
        if (currentPos > -1) {
            TextView view = textViewList.get(currentPos);
            current = chunk;
            view.setBackgroundResource(R.drawable.selector_current_chunk);
            pattern.setCurrentSeccion(chunk);
            pattern.save();
        }
    }

    /**
     * Copyright 2011 Sherif
     * Updated by Karim Varela to handle LinearLayouts with other views on either side.
     *
     * @param linearLayout : The linear layout in which to add the views
     * @param views        : The views to wrap within LinearLayout
     * @param context      : The context of the app
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
        newLL.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
                newLL.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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

    private void resetCurrentState() {
        for (int i = 0; i < textViewList.size(); i++) {
            TextView textView = textViewList.get(i);
            if (textView != null) {
                if (seccionList.get(i).tipo == Seccion.TIPO_CHUNK) {
                    textView.setBackgroundResource(R.drawable.selector_chunk);
                } else if (seccionList.get(i).tipo == Seccion.TIPO_SECCION) {
                    textView.setBackgroundResource(R.drawable.selector_seccion);
                }
            }
        }
    }

    private void moveToChunk(final boolean next) {
        if (current == null) {
            currentPos = 0;
        }
        resetCurrentState();
        int salto = 0;

        do {
            if (next) {
                currentPos += 1;
                if (currentPos < seccionList.size() - 1) {
                    current = seccionList.get(currentPos);
                } else {
                    currentPos = seccionList.size() - 1;
                    break;
                }
            } else {
                currentPos -= 1;
                if (currentPos >= 0) {
                    current = seccionList.get(currentPos);
                } else {
                    currentPos = 2;
                    break;
                }
            }
            if (current.tipo == Seccion.TIPO_SECCION) {
                salto += textViewList.get(currentPos).getHeight();
            }
        } while (current.tipo != Seccion.TIPO_CHUNK);

        final TextView currentTv = textViewList.get(currentPos);
        final int fSalto = salto;
        if (currentTv != null) {
//            currentTv.setBackgroundResource(R.drawable.selector_current_chunk);
            setChunkSelected(currentTv);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    int scrollBy = 0;
                    Rect rectTv = new Rect();
                    boolean vis = currentTv.getGlobalVisibleRect(rectTv);
                    Rect rectLl = new Rect();
                    layout.getGlobalVisibleRect(rectLl);

                    int currentChunkRealHeight = currentTv.getHeight();
                    int currentChunkVisibleTop = rectTv.top;
                    int currentChunkRealBottom = rectTv.top + currentChunkRealHeight;
                    int currentChunkVisibleBottom = rectTv.bottom;
                    int currentChunkRealTop = currentChunkVisibleBottom - currentChunkRealHeight;

                    if (next) {
                        if (currentChunkRealBottom >= rectLl.bottom) {
                            if (vis) {
                                int x = rectLl.bottom - currentChunkRealHeight;
                                scrollBy = currentChunkVisibleTop - x;
                                scrollBy += (int) Math.ceil(scrollThresh * density);
                            } else {
                                scrollBy = fSalto + currentChunkRealHeight + (int) Math.ceil(scrollThresh * density);
                            }
                        }
                    } else {
                        if (currentChunkRealTop <= rectLl.top) {
                            if (vis) {
                                scrollBy = rectLl.top - currentChunkRealTop;
                                scrollBy += (int) Math.ceil(scrollThresh * density);
                                scrollBy *= -1;
                            }
                        } else {
                            if (!vis) {
                                scrollBy = fSalto + currentChunkRealHeight + (int) Math.ceil(scrollThresh * density);
                                scrollBy *= -1;
                            }
                        }
                    }
                    System.out.println("*************************************************");
                    System.out.println("visible:   " + vis);
                    System.out.println("scroll0     " + scrollView.getScrollY());
                    System.out.println("*************************************************");
                    System.out.println("SCROLL BY 1:::: " + scrollBy);
                    scrollView.scrollBy(0, scrollBy);
                    System.out.println("scroll1    " + scrollView.getScrollY());

                    vis = currentTv.getGlobalVisibleRect(rectTv);
                    currentChunkRealHeight = currentTv.getHeight();
                    currentChunkVisibleTop = rectTv.top;
                    currentChunkRealBottom = rectTv.top + currentChunkRealHeight;
                    currentChunkVisibleBottom = rectTv.bottom;
                    currentChunkRealTop = currentChunkVisibleBottom - currentChunkRealHeight;

                    System.out.println("'''''''''''''''''''''''''''''''''''''''''''''");
                    System.out.println("real height    " + currentChunkRealHeight);
                    System.out.println("visible top    " + currentChunkVisibleTop);
                    System.out.println("real top       " + currentChunkRealTop);
                    System.out.println("visible bottom " + currentChunkVisibleBottom);
                    System.out.println("real bottom    " + currentChunkRealBottom);
                    System.out.println("Layout top      " + rectLl.top);
                    System.out.println("'''''''''''''''''''''''''''''''''''''''''''''");
                    if (!vis || currentChunkRealHeight != rectTv.height()) {
                        int ns = currentChunkRealTop;
                        if (scrollView.getScrollY() > currentChunkRealTop) {
                            ns = currentChunkRealTop - scrollView.getScrollY();
                            ns *= -1;
                        }
                        System.out.println("SCROLL BY 2:::: " + ns);
                        scrollView.scrollBy(0, ns);
                        System.out.println("scroll2     " + scrollView.getScrollY());
                        vis = currentTv.getGlobalVisibleRect(rectTv);
                    }
                    System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++");
                    System.out.println("visible:   " + vis);
                    System.out.println("scroll3     " + scrollView.getScrollY());
                    System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++");
                }
            });
        }
    }

    private void moveToChunk(TextView textView) {
        Rect rectTv = new Rect();
        textView.getGlobalVisibleRect(rectTv);
        Rect rectLl = new Rect();
        layout.getGlobalVisibleRect(rectLl);

        int currentChunkRealHeight = textView.getHeight();
        int currentChunkVisibleBottom = rectTv.bottom;
        int currentChunkRealTop = currentChunkVisibleBottom - currentChunkRealHeight;

        scrollView.scrollTo(0, currentChunkRealTop - scrollThresh);
    }

    @Override
    public void onClick(View view) {
        boolean cont = true;
        for (int i = 0; i < btnCountersMas.length; i++) {
            if (view.getId() == btnCountersMas[i].getId()) {
                counters[i] += 1;
                btnCountersMas[i].setText("" + counters[i]);
                cont = false;
                break;
            }
        }
        if (cont) {
            for (int i = 0; i < btnCountersMenos.length; i++) {
                if (view.getId() == btnCountersMenos[i].getId()) {
                    if (counters[i] > 1) {
                        counters[i] -= 1;
                        btnCountersMas[i].setText("" + counters[i]);
                    }
                    cont = false;
                    break;
                }
            }
            if (cont) {
                if (view.getId() == btnPrevSeccion.getId()) {
                    do {
                        currentPos -= 1;
                        if (currentPos < seccionList.size() - 1) {
                            current = seccionList.get(currentPos);
                        } else {
                            currentPos = seccionList.size() - 1;
                            break;
                        }
                    } while (current.tipo != Seccion.TIPO_SECCION);
                    moveToChunk(true);
                } else if (view.getId() == btnPrevChunk.getId()) {
                    moveToChunk(false);
                } else if (view.getId() == btnNextChunk.getId()) {
                    moveToChunk(true);
                } else if (view.getId() == btnNextSeccion.getId()) {
                    do {
                        currentPos += 1;
                        if (currentPos < seccionList.size() - 1) {
                            current = seccionList.get(currentPos);
                        } else {
                            currentPos = seccionList.size() - 1;
                            break;
                        }
                    } while (current.tipo != Seccion.TIPO_SECCION);
                    moveToChunk(true);
                } else if (view.getId() == btnCounters.getId()) {
                    if (layoutsCounters[0].isShown()) {
                        btnCounters.setImageResource(R.drawable.ic_counters);
                        for (LinearLayout linearLayout : layoutsCounters) {
                            linearLayout.setVisibility(View.GONE);
                        }

//                btnCounters.setBackgroundResource(R.drawable.ic_counters);
//                layoutCounter1.setVisibility(View.GONE);
//                layoutCounter2.setVisibility(View.GONE);
                    } else {
                        btnCounters.setImageResource(R.drawable.ic_counters_hide);
                        for (LinearLayout linearLayout : layoutsCounters) {
                            linearLayout.setVisibility(View.VISIBLE);
                        }
//                btnCounters.setBackgroundResource(R.drawable.ic_counters_hide);
//                layoutCounter1.setVisibility(View.VISIBLE);
//                layoutCounter2.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        /*else if (view.getId() == btnCounter1Mas.getId()) {
            counter1++;
            btnCounter1Mas.setText("" + counter1);
        } else if (view.getId() == btnCounter1Menos.getId()) {
            if (counter1 > 1) {
                counter1--;
            }
            btnCounter1Mas.setText("" + counter1);
        } else if (view.getId() == btnCounter2Mas.getId()) {
            counter2++;
            btnCounter2Mas.setText("" + counter2);
        } else if (view.getId() == btnCounter2Menos.getId()) {
            if (counter2 > 1) {
                counter2--;
            }
            btnCounter2Mas.setText("" + counter2);
        }*/
//        TextView currentTv = textViewList.get(currentPos);
//        moveToChunk(currentTv);
    }

    private void alertCounter(final int pos) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(R.string.set_counters_title);
        alert.setMessage(R.string.set_counters_message);

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);

        alert.setPositiveButton(R.string.global_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                counters[pos] = Integer.parseInt(input.getText().toString());
                btnCountersMas[pos].setText("" + counters[pos]);
                // Do something with value!
            }
        });

        alert.setNegativeButton(R.string.global_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    @Override
    public boolean onLongClick(View view) {
        for (int i = 0; i < btnCountersMas.length; i++) {
            if (view.getId() == btnCountersMas[i].getId()) {
                alertCounter(i);
            }
        }
        return false;
    }
}