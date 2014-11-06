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

    EditText txtTitulo;
    EditText txtContenido;
    Button btnSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.create_pattern_layout, container, false);

        txtTitulo = (EditText) view.findViewById(R.id.create_pattern_title);
        txtContenido = (EditText) view.findViewById(R.id.create_pattern_content);
        btnSave = (Button) view.findViewById(R.id.create_pattern_btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideSoftKeyboard(context);
                String nombre = txtTitulo.getText().toString().trim();
                String contenido = txtContenido.getText().toString().trim();

                if (nombre.equals("")) {
                    Utils.toast(getString(R.string.pattern_title_validation_error), context);
                } else if (contenido.equals("")) {
                    Utils.toast(getString(R.string.pattern_content_validation_error), context);
                } else {
                    Pattern pattern = new Pattern(context);
                    pattern.nombre = nombre;
                    pattern.contenido = contenido;
                    pattern.save();
                    Utils.toast(getString(R.string.pattern_saved), context);
                }
            }
        });

        return view;
    }
}