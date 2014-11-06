package com.lzm.KnittingHelp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lzm.KnittingHelp.MainActivity;
import com.lzm.KnittingHelp.R;
import com.lzm.KnittingHelp.db.Pattern;
import com.lzm.KnittingHelp.utils.ImageUtils;
import com.lzm.KnittingHelp.utils.Utils;

import java.util.List;

/**
 * Created by luz on 01/08/14.
 */
public class PatternsListAdapter extends ArrayAdapter<Pattern> {

    MainActivity context;

    List<Pattern> patterns;

    public PatternsListAdapter(MainActivity context, List<Pattern> patterns) {
        super(context, R.layout.patterns_list_row, patterns);
        this.context = context;
        this.patterns = patterns;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Pattern pattern = patterns.get(position);

        String labelNombre = pattern.nombre;
        String labelContenido = pattern.contenido;
        if (labelContenido.length() > 200) {
            labelContenido = labelContenido.substring(0, 196) + "...";
        }

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.patterns_list_row, null);
        }

        TextView itemNombre = (TextView) convertView.findViewById(R.id.patterns_list_row_nombre);
        TextView itemContenido = (TextView) convertView.findViewById(R.id.patterns_list_row_contenido);

        ImageView itemFoto = (ImageView) convertView.findViewById(R.id.patterns_list_row_image);

        itemNombre.setText(labelNombre);
        itemContenido.setText(labelContenido);

        if (pattern.fechaCreacion != null) {
            itemFoto.setImageBitmap(ImageUtils.decodeFile(pattern.imagen, 100, 100, false));
        } else {
            String path = pattern.imagen.replaceAll("\\.jpg", "").toLowerCase();
            path = "th_" + path;
            itemFoto.setImageResource(Utils.getImageResourceByName(context, path));
        }

        return convertView;
    }
}