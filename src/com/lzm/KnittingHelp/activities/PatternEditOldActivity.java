package com.lzm.KnittingHelp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luz on 19/11/14.
 * <p/>
 * <div>Icon made by <a href="http://www.typicons.com" title="Stephen Hutchings">Stephen Hutchings</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed under <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0">CC BY 3.0</a></div>
 */
public class PatternEditOldActivity extends Activity implements View.OnClickListener {
    final int SELECTED_SECCION = 0;
    final int SELECTED_CHUNK = 1;

    int selected;
    Seccion current;
    int currentPos = -1;

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

    float density;

    ImageButton btnPlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_edit_layout);

        activity = this;

        density = getResources().getDisplayMetrics().density;

        // Get the message from the intent
        Intent intent = getIntent();
        long patternId = intent.getLongExtra(PatternsListFragment.PATTERN_ID_MESSAGE, 0);
        pattern = Pattern.getConCurrentSeccion(this, patternId);
        current = pattern.currentSeccion;

        getActionBar().setTitle(pattern.nombre);

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

        layout = (LinearLayout) findViewById(R.id.pattern_edit_linear_layout);
        scrollView = (ScrollView) findViewById(R.id.pattern_edit_scroll);

        btnPlay = (ImageButton) findViewById(R.id.pattern_edit_btn_play);
        btnPlay.setOnClickListener(this);

        LinearLayout layoutSeccion = null;
        LinearLayout layoutLinea = null;
        List<View> listChunks = new ArrayList<View>();

        int pos = 0;
        for (Seccion seccion : seccionList) {
            if (seccion == current) {
                currentPos = pos;
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
            pos++;
        }
        if (layoutLinea != null) {
            populateViews(layoutLinea, listChunks, this, null);
        }
    } //onCreate

    private View setChunk(final Seccion seccion) {
        TextView txvChunk = new TextView(this);
        txvChunk.setText(/*"[" + seccion.orden + "] " +*/ seccion.contenido);
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
        txvSeccion.setText(/*"[" + seccion.orden + "] " + */seccion.contenido);
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

    public void resetArrays() {
        selectedSecciones = new ArrayList<Seccion>();
        for (TextView selectedTextView : selectedTextViews) {
            selectedTextView.setSelected(false);
        }
        selectedTextViews = new ArrayList<TextView>();
    }

    public void resetEditor() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        editing = false;
        editText = null;
        editingTv = null;
        editingTvParent = null;
        editingSeccion = null;
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
            inflater.inflate(R.menu.pattern_edit_contextual_action_bar, menu);
            mActionMode = mode;
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated. (Used for updates to CAB after invalidate() request)
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mMenu = menu;
            if (selected == SELECTED_SECCION) {
                // si es seccion escondo el menu de linea
                menu.findItem(R.id.pattern_edit_cab_chunk).setVisible(false);
                menu.findItem(R.id.pattern_edit_cab_seccion).setVisible(true);
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
            } else if (selected == SELECTED_CHUNK) {
                // si es linea escondo el menu seccion
                menu.findItem(R.id.pattern_edit_cab_seccion).setVisible(false);
                menu.findItem(R.id.pattern_edit_cab_chunk).setVisible(true);
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
                    menu.findItem(R.id.cab_chunk_editar).setVisible(true);
                    menu.findItem(R.id.cab_chunk_join).setVisible(false);
                } else {
                    menu.findItem(R.id.cab_chunk_editar).setVisible(false);
//                    menu.findItem(R.id.cab_linea_join).setVisible(true);
                }
            }
            return true;
//            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.cab_seccion_linea:
                    //cambiar seccion => linea
                    int pos = 0;
                    for (Seccion selectedSeccion : selectedSecciones) {
                        int nuevoOrdenSeccion = Seccion.updateOrdenExcludes(activity, selectedSeccion, 2);

                        Seccion nuevaLinea = new Seccion(activity);
                        nuevaLinea.tipo = Seccion.TIPO_LINEA;
                        nuevaLinea.contenido = "";
                        nuevaLinea.setPattern(pattern);
                        nuevaLinea.setSeccionPadre(selectedSeccion);
                        nuevaLinea.orden = nuevoOrdenSeccion + 1;
                        nuevaLinea.save();

                        Seccion nuevoChunk = new Seccion(activity);
                        nuevoChunk.tipo = Seccion.TIPO_CHUNK;
                        nuevoChunk.contenido = selectedSeccion.contenido;
                        nuevoChunk.setPattern(pattern);
                        nuevoChunk.setSeccionPadre(nuevaLinea);
                        nuevoChunk.orden = nuevaLinea.orden + 1;
                        nuevoChunk.save();

                        List<View> listChunks = new ArrayList<View>();
                        listChunks.add(setChunk(nuevoChunk));

                        View seccionView = selectedTextViews.get(pos);
                        LinearLayout seccionViewParent = (LinearLayout) seccionView.getParent();
                        int indexSeccion = ((ViewGroup) seccionViewParent).indexOfChild(seccionView);
                        LinearLayout layoutSeccion = (LinearLayout) seccionViewParent.getChildAt(indexSeccion + 1);
                        LinearLayout layoutLinea = setLinea(nuevaLinea, layoutSeccion, 0);
                        populateViews(layoutLinea, listChunks, activity, null);
                        pos++;
                    }
                    mode.finish(); // Action picked, so close the CAB
                    break;
                case R.id.cab_seccion_duplicar:
                    //duplicar la seccion con todas sus lineas
                    break;
                case R.id.cab_seccion_eliminar:
                    //elimina la seccion con todas sus lineas
                    break;
                case R.id.cab_seccion_editar:
                    //textView = > editText + btn guardar
                case R.id.cab_chunk_editar:
                    //textView = > editText + btn guardar
                    //pattern_edit_cab_save     btn save
                    editing = true;
                    if (selectedSecciones.size() == 1) {
                        mMenu.findItem(R.id.pattern_edit_cab_save).setVisible(true);
                    } else {
                        mMenu.findItem(R.id.pattern_edit_cab_save).setVisible(false);
                    }
                    editingTv = selectedTextViews.get(0);
                    editingSeccion = selectedSecciones.get(0);
                    editingTv.setVisibility(View.GONE);

                    editingTvParent = (LinearLayout) editingTv.getParent();

                    String origText = editingTv.getText().toString();

                    int index = ((ViewGroup) editingTvParent).indexOfChild(editingTv);

                    editText = new EditText(activity);
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
                    editText.setText(origText);
                    if (selected == SELECTED_CHUNK) {
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        editText.setSingleLine(false);
                        editText.setLines(3);
                    }
                    editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                    LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                    editTextParams.setMargins(0, 0, 0, 0);

                    if (selected == SELECTED_CHUNK) {
                        // Gets linearlayout
                        LinearLayout layout = (LinearLayout) editingTv.getParent();
                        // Gets the layout params that will allow you to resize the layout
                        ViewGroup.LayoutParams params = layout.getLayoutParams();
                        // Changes the height and width to the specified *pixels*
                        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());

                        editTextParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                    }
                    editText.setLayoutParams(editTextParams);
                    ((ViewGroup) editingTv.getParent()).addView(editText, index);

                    editText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    imm.toggleSoftInput(0, 0);
                    break;
                case R.id.cab_chunk_eliminar:
                    //elimina la linea
                    pos = 0;
                    for (Seccion selectedSeccion : selectedSecciones) {
                        int c = selectedSeccion.delete();
                        seccionList.remove(selectedSeccion);
                        TextView tx = selectedTextViews.get(pos);
                        LinearLayout layoutLinea = ((LinearLayout) tx.getParent());
                        LinearLayout layoutLinea2 = ((LinearLayout) tx.getParent().getParent());
                        LinearLayout layoutSeccion = ((LinearLayout) tx.getParent().getParent().getParent());
                        LinearLayout layoutSeccion2 = ((LinearLayout) tx.getParent().getParent().getParent().getParent());

//                        tx.setBackgroundColor(Color.RED);
//                        layoutLinea.setBackgroundColor(Color.BLUE);
//                        layoutLinea2.setBackgroundColor(Color.YELLOW);
//                        layoutSeccion.setBackgroundColor(Color.GREEN);
//                        layoutSeccion2.setBackgroundColor(Color.GRAY);

                        layoutLinea.removeView(tx);
                        textViewList.remove(tx);
                        if (c == 2) {
                            layoutSeccion2.removeView(layoutSeccion);
                        }
                        pos++;
                    }
                    mode.finish();
                    break;
                case R.id.cab_chunk_split:
                    //muestra opciones para separar en .    ,   ;   (   [
                    break;
                case R.id.cab_chunk_duplicar:
                    //duplica la linea
                    pos = 0;
                    for (Seccion selectedSeccion : selectedSecciones) {
                        int nuevoOrdenSeccion = Seccion.updateOrdenExcludes(activity, selectedSeccion, 2);

                        Seccion nuevaLinea = new Seccion(activity);
                        nuevaLinea.tipo = Seccion.TIPO_LINEA;
                        nuevaLinea.contenido = "";
                        nuevaLinea.setPattern(pattern);
                        nuevaLinea.seccionPadreId = selectedSeccion.seccionPadreId;
                        nuevaLinea.orden = nuevoOrdenSeccion + 1;
                        nuevaLinea.save();

                        Seccion nuevoChunk = new Seccion(activity);
                        nuevoChunk.tipo = Seccion.TIPO_CHUNK;
                        nuevoChunk.contenido = selectedSeccion.contenido;
                        nuevoChunk.setPattern(pattern);
                        nuevoChunk.setSeccionPadre(nuevaLinea);
                        nuevoChunk.orden = nuevaLinea.orden + 1;
                        nuevoChunk.save();

                        List<View> listChunks = new ArrayList<View>();
                        listChunks.add(setChunk(nuevoChunk));

                        View seccionView = selectedTextViews.get(pos);
                        LinearLayout seccionViewParent = (LinearLayout) seccionView.getParent().getParent().getParent();
                        int indexSeccion = ((ViewGroup) seccionViewParent).indexOfChild(seccionView);
                        LinearLayout layoutSeccion = (LinearLayout) seccionViewParent.getChildAt(indexSeccion + 1);
                        LinearLayout layoutLinea = setLinea(nuevaLinea, layoutSeccion, 0);
                        populateViews(layoutLinea, listChunks, activity, null);
                        pos++;
                    }
                    mode.finish(); // Action picked, so close the CAB
                    break;
                case R.id.cab_chunk_join:
                    //muestra opciones para unir con .  ,   ;   [_]
                    break;
                case R.id.cab_linea_new:
                    //crea una nueva seccion con las lineas seleccionadas
                    break;
                case R.id.pattern_edit_cab_save:
                    //guarda los cambios y cierra el cab
                    if (editText != null) {
                        String newContent = editText.getText().toString().trim();
                        editingTvParent.removeView(editText);
                        editingTv.setText(newContent);
                        editingTv.setVisibility(View.VISIBLE);
                        editingSeccion.contenido = newContent;
                        editingSeccion.save();
                        if (selected == SELECTED_SECCION) {
                            LinearLayout.LayoutParams editingTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            editingTv.setLayoutParams(editingTvParams);
                        } else if (selected == SELECTED_CHUNK) {
                            // Save de un chunk
                            LinearLayout.LayoutParams editingTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            editingTv.setLayoutParams(editingTvParams);

                            List<View> viewsAdd = new ArrayList<View>();
                            LinearLayout lLayout = (LinearLayout) editingTv.getParent().getParent().getParent();
                            LinearLayout layoutSeccion = null;
                            int childcount2 = lLayout.getChildCount();
                            for (int i = 0; i < childcount2; i++) {
                                View v = lLayout.getChildAt(i);
//                            v.setBackgroundColor(Color.GREEN);
                                layoutSeccion = (LinearLayout) v;
                                int cc = layoutSeccion.getChildCount();
                                for (int j = 0; j < cc; j++) {
                                    View v2 = layoutSeccion.getChildAt(j);
//                                v2.setBackgroundColor(Color.BLUE);
                                    LinearLayout l2 = (LinearLayout) v2;
                                    int cc2 = l2.getChildCount();
                                    for (int k = 0; k < cc2; k++) {
                                        View v3 = l2.getChildAt(k);
//                                    v3.setBackgroundColor(Color.RED);
                                        viewsAdd.add(v3);
                                    }
                                    l2.removeAllViews();
                                }
                                layoutSeccion.removeAllViews();
                            }
                            if (layoutSeccion != null) {
                                populateViews(layoutSeccion, viewsAdd, activity, null);

                                LinearLayout.LayoutParams layoutLineaParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutLineaParams.setMargins(0, 0, 0, 10);

                                layoutSeccion.setLayoutParams(layoutLineaParams);
                            }

                            editingTv.setGravity(Gravity.LEFT);
                        }
                        //                        Utils.hideSoftKeyboard(activity);
                        resetEditor();
                        mode.finish(); // Action picked, so close the CAB
                    }
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
            if (editing) {
                editingTvParent.removeView(editText);
                editingTv.setVisibility(View.VISIBLE);
                if (selected == SELECTED_CHUNK) {
                    LinearLayout.LayoutParams layoutLineaParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutLineaParams.setMargins(0, 0, 0, 10);
                    editingTvParent.setLayoutParams(layoutLineaParams);
                } else {
                    LinearLayout.LayoutParams editingTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    editingTv.setLayoutParams(editingTvParams);
                }
                resetEditor();
            }
            closeCAB();
        }
    };

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

    @Override
    public void onClick(View view) {
        if (view.getId() == btnPlay.getId()) {
            Intent intent = new Intent(activity, PatternViewActivity.class);
            intent.putExtra(PatternsListFragment.PATTERN_ID_MESSAGE, pattern.id);
            startActivity(intent);
        }
    }
}