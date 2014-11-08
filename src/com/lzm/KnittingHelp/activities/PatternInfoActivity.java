package com.lzm.KnittingHelp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.lzm.KnittingHelp.MainActivity;
import com.lzm.KnittingHelp.R;
import com.lzm.KnittingHelp.db.Pattern;
import com.lzm.KnittingHelp.fragments.PatternsListFragment;
import com.lzm.KnittingHelp.utils.ImageUtils;
import com.lzm.KnittingHelp.utils.Utils;

import java.io.File;

/**
 * Created by DELL on 05/11/2014.
 */
public class PatternInfoActivity extends Activity implements View.OnClickListener {
    TextView txtNombre;
    TextView txtContenido;

    ImageView imgImagen;

    Pattern pattern;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_info_layout);

        // Get the message from the intent
        Intent intent = getIntent();
        long patternId = intent.getLongExtra(PatternsListFragment.PATTERN_ID_MESSAGE, 0);
        pattern = Pattern.get(this, patternId);

        getActionBar().setTitle(pattern.nombre);

        txtNombre = (TextView) findViewById(R.id.pattern_info_nombre);
        txtContenido = (TextView) findViewById(R.id.pattern_info_contenido);
        imgImagen = (ImageView) findViewById(R.id.pattern_info_imagen);

        txtNombre.setText(pattern.nombre);
        txtContenido.setText(pattern.contenido);

        if (pattern.imagen != null) {
            try {
                String path = pattern.imagen.replaceAll("\\.jpg", "").toLowerCase();
                if (pattern.fechaCreacion != null) {
                    File imgFile = new File(pattern.imagen);
                    if (imgFile.exists()) {
                        Bitmap myBitmap = ImageUtils.decodeFile(imgFile.getAbsolutePath(), 200, 200);
                        imgImagen.setImageBitmap(myBitmap);
                    }
                } else {
                    imgImagen.setImageResource(Utils.getImageResourceByName(this, path));
                }
                imgImagen.setOnClickListener(this);
            } catch (Exception e) {

            }
        } else {
            imgImagen.setImageResource(R.drawable.ic_launcher);
        }

    }


    @Override
    public void onClick(View view) {
        Utils.hideSoftKeyboard(this);

        if (view.getId() == imgImagen.getId()) {
            LayoutInflater inflater = this.getLayoutInflater();
            View v = inflater.inflate(R.layout.pattern_info_entry_dialog, null);
            final String t = pattern.nombre;
            final AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(v)
                    .setNeutralButton(R.string.dialog_btn_cerrar, null) //Set to null. We override the onclick
                    .setTitle(t);
            final AlertDialog d = builder.create();
            final ImageView img = (ImageView) v.findViewById(R.id.especie_info_dialog_image);
//            final TextView txt = (TextView) v.findViewById(R.id.especie_info_dialog_comentarios);
            setFoto(img/*, txt*/);
            d.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button cerrar = d.getButton(AlertDialog.BUTTON_NEUTRAL);
                    cerrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            d.dismiss();
                        }
                    });
                }
            });
            d.show();
        }
    }

    private void setFoto(ImageView img/*, TextView txt*/) {
//        String comentarios = pattern.contenido;
//        if (comentarios != null) {
//            comentarios = comentarios.trim();
//        }

        String path1 = pattern.imagen.replaceAll("\\.jpg", "").toLowerCase();
        if (pattern.fechaCreacion != null) {
            img.setImageBitmap(ImageUtils.decodeFile(path1, MainActivity.screenWidth, 300));
        } else {
            img.setImageResource(Utils.getImageResourceByName(this, path1));
        }
//        if (comentarios == null || comentarios.equals("")) {
//            txt.setVisibility(View.GONE);
//        } else {
//            txt.setText(comentarios);
////            txt.setSelection(txt.getText().length());
//            txt.setVisibility(View.VISIBLE);
//        }
    }
}