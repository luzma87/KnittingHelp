package com.lzm.KnittingHelp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.lzm.KnittingHelp.MainActivity;
import com.lzm.KnittingHelp.R;
import com.lzm.KnittingHelp.db.Pattern;
import com.lzm.KnittingHelp.utils.Utils;

/**
 * Created by DELL on 05/11/2014.
 */
public class CreatePatternFragment extends MasterFragment {

    EditText txtNombre;
    EditText txtContenido;
    Button btnSave;
    Button btnCancel;

    Pattern pattern;

    long id = 0;

    public void setData(long patternId) {
        id = patternId;
        if (id != 0) {
            pattern = Pattern.get(context, id);
            txtNombre.setText(pattern.nombre);
            txtContenido.setText(pattern.contenido);
        } else {
            resetForm();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.create_pattern_layout, container, false);

        txtNombre = (EditText) view.findViewById(R.id.create_pattern_name);
        txtContenido = (EditText) view.findViewById(R.id.create_pattern_content);

        pattern = new Pattern(context);

        btnCancel = (Button) view.findViewById(R.id.create_pattern_btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideSoftKeyboard(context);
                resetForm();
                context.selectTab(context.LIST_POS);
            }
        });
        btnSave = (Button) view.findViewById(R.id.create_pattern_btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideSoftKeyboard(context);
                String nombre = txtNombre.getText().toString().trim();
                String contenido = txtContenido.getText().toString().trim();

                if (nombre.equals("")) {
                    Utils.toast(getString(R.string.pattern_title_validation_error), context);
                } else if (contenido.equals("")) {
                    Utils.toast(getString(R.string.pattern_content_validation_error), context);
                } else {
                    pattern.nombre = nombre;
                    pattern.contenido = contenido;
                    pattern.save();
                    Utils.toast(getString(R.string.pattern_saved), context);
                    resetForm();
                    context.selectTab(context.LIST_POS);
                    ((PatternsListFragment) context.fragments.get(context.LIST_POS)).loadPatterns();
                }
            }
        });
        return view;
    }

    private void resetForm() {
        id = 0;
        pattern = new Pattern(context);
        txtNombre.setText("");
        txtContenido.setText("");
    }
}