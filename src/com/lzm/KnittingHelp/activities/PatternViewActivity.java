package com.lzm.KnittingHelp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lzm.KnittingHelp.R;
import com.lzm.KnittingHelp.db.Pattern;
import com.lzm.KnittingHelp.fragments.PatternsListFragment;

/**
 * Created by luz on 19/11/14.
 */
public class PatternViewActivity extends Activity {
    Pattern pattern;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_view_layout);

        // Get the message from the intent
        Intent intent = getIntent();
        long patternId = intent.getLongExtra(PatternsListFragment.PATTERN_ID_MESSAGE, 0);
        pattern = Pattern.get(this, patternId);

        getActionBar().setTitle(pattern.nombre);

        LinearLayout layout = (LinearLayout) findViewById(R.id.pattern_view_linear_layout);

        String contenido = pattern.contenido;

        String[] secciones = contenido.split("\n\n");
        for (String seccion : secciones) {
            String[] lineas = seccion.split("\n");
            String nombreSeccion = lineas[0];

            TextView txvSeccion = new TextView(this);
            txvSeccion.setText(nombreSeccion);
            txvSeccion.setTextAppearance(this, R.style.seccionNombre);
            txvSeccion.setBackgroundResource(R.color.seccion_name);

            LinearLayout.LayoutParams txvSeccionParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            txvSeccion.setLayoutParams(txvSeccionParams);

            txvSeccion.setPadding(20, 20, 20, 20);

            layout.addView(txvSeccion);

            LinearLayout layoutSeccion = new LinearLayout(this);
            layoutSeccion.setOrientation(LinearLayout.VERTICAL);
            layoutSeccion.setBackgroundResource(R.color.seccion_content);
            layoutSeccion.setPadding(20, 20, 20, 20);

            LinearLayout.LayoutParams layoutSeccionParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutSeccionParams.setMargins(0, 0, 0, 25);
            layoutSeccion.setLayoutParams(layoutSeccionParams);

            for (int i = 1; i < lineas.length; i++) {
                String linea = lineas[i];
                TextView txvLinea = new TextView(this);
                txvLinea.setText(linea);
                txvLinea.setBackgroundResource(R.color.line_content);
                txvLinea.setPadding(10, 10, 10, 10);

                LinearLayout.LayoutParams txvLineaParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                txvLineaParams.setMargins(0, 0, 0, 10);
                txvLinea.setLayoutParams(txvLineaParams);

                layoutSeccion.addView(txvLinea);
            }
            layout.addView(layoutSeccion);
        }


    }
}