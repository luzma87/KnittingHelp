package com.lzm.KnittingHelp.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.lzm.KnittingHelp.MainActivity;
import com.lzm.KnittingHelp.R;
import com.lzm.KnittingHelp.utils.Utils;

/**
 * Created by DELL on 05/11/2014.
 */
public class CountersFragment extends MasterFragment implements View.OnClickListener {

    Button btnInc1;
    Button btnDec1;
    EditText txtMax1;
    EditText txtLbl1;

    int count1 = 1;
    int max1 = 0;

    Button btnInc2;
    Button btnDec2;
    EditText txtMax2;
    EditText txtLbl2;

    int count2 = 1;
    int max2 = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.counters_layout, container, false);

        btnInc1 = (Button) view.findViewById(R.id.counters_btn_inc1);
        btnDec1 = (Button) view.findViewById(R.id.counters_btn_dec1);
        txtMax1 = (EditText) view.findViewById(R.id.counters_txt_max1);
        txtLbl1 = (EditText) view.findViewById(R.id.counters_txt_name1);

        btnInc1.setOnClickListener(this);
        btnDec1.setOnClickListener(this);
        txtMax1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    max1 = Integer.parseInt(txtMax1.getText().toString());
                } catch (Exception e) {
                    max1 = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btnInc2 = (Button) view.findViewById(R.id.counters_btn_inc2);
        btnDec2 = (Button) view.findViewById(R.id.counters_btn_dec2);
        txtMax2 = (EditText) view.findViewById(R.id.counters_txt_max2);
        txtLbl2 = (EditText) view.findViewById(R.id.counters_txt_name2);

        btnInc2.setOnClickListener(this);
        btnDec2.setOnClickListener(this);
        txtMax2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    max2 = Integer.parseInt(txtMax2.getText().toString());
                } catch (Exception e) {
                    max2 = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        if (savedInstanceState != null) {
            count1 = savedInstanceState.getInt("count1");
            btnInc1.setText("" + count1);
            max1 = savedInstanceState.getInt("max1");
            txtMax1.setText("" + max1);
            txtLbl1.setText(savedInstanceState.getString("label1"));

            count2 = savedInstanceState.getInt("count2");
            btnInc2.setText("" + count2);
            max2 = savedInstanceState.getInt("max2");
            txtMax2.setText("" + max2);
            txtLbl2.setText(savedInstanceState.getString("label2"));
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        Utils.hideSoftKeyboard(context);
        if (view.getId() == btnInc1.getId()) {
            if ((max1 > 0 && count1 + 1 <= max1) || max1 == 0) {
                count1 += 1;
            }
            btnInc1.setText("" + count1);
            if (max1 > 0 && count1 == max1) {
                Utils.playSound(context, R.raw.bell);
                Utils.vibrate(1000, context);
            }
        } else if (view.getId() == btnDec1.getId()) {
            if (count1 > 1) {
                count1 -= 1;
            }
            btnInc1.setText("" + count1);
        }
        if (view.getId() == btnInc2.getId()) {
            if ((max2 > 0 && count2 + 1 <= max2) || max2 == 0) {
                count2 += 1;
            }
            btnInc2.setText("" + count2);
            if (max2 > 0 && count2 == max2) {
                Utils.playSound(context, R.raw.bell);
                Utils.vibrate(1000, context);
            }
        } else if (view.getId() == btnDec2.getId()) {
            if (count2 > 1) {
                count2 -= 1;
            }
            btnInc2.setText("" + count2);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("count1", count1);
        outState.putInt("max1", max1);
        outState.putString("label1", txtLbl1.getText().toString());
        outState.putInt("count2", count2);
        outState.putInt("max2", max2);
        outState.putString("label2", txtLbl2.getText().toString());
        super.onSaveInstanceState(outState);
    }
}