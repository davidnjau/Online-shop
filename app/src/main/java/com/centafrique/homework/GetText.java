package com.centafrique.homework;

import android.widget.EditText;

public class GetText {

    public String getText(EditText editText){

        String txtString = null;
        txtString = editText.getText().toString();

        return txtString;

    }



}
