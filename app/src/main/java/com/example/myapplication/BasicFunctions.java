package com.example.myapplication;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class BasicFunctions {
    public static Spinner spinnerGenerator(Spinner spinner, int array, Context context) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,array,R.layout.spinner_bg);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return spinner;
    }
}
