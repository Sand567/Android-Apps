package com.sandeep.temperatureconverter;

import android.os.PersistableBundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private EditText editText1, editText2;
    private TextView history;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        history = (TextView) findViewById(R.id.historyText);
        history.setMovementMethod(new ScrollingMovementMethod());
    }

    public MainActivity() {
        super();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("HISTORY", history.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        history.setText(savedInstanceState.getString("HISTORY"));
    }

    public void buttonClicked(View view) {

        int buttonId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(buttonId);

        double temp = Double.parseDouble(editText1.getText().toString());
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        String inputText;
        String outputText;
        double celsius,fahrenheit;

        if(radioButton.getText().equals("Fah to Cel")) {

            celsius = (temp - 32.0) * (5.0/9.0);
            String cel = decimalFormat.format(celsius);
            editText2.setText(cel);
            inputText = editText1.getText().toString();
            outputText = editText2.getText().toString();
            String b = ("F to C: "+inputText+"   >>>>>>>   "+outputText);
            String a = history.getText().toString();
            history.setText(b+"\n"+a);
            editText1.setText("");

        } else if(radioButton.getText().equals("Cel to Fah")) {

            fahrenheit = (temp * (9.0/5.0)) + 32.0;
            String fah = decimalFormat.format(fahrenheit);
            editText2.setText(fah);
            inputText = editText1.getText().toString();
            outputText = editText2.getText().toString();
            String b = ("C to F: "+inputText+"   >>>>>>>   "+outputText);
            String a = history.getText().toString();
            history.setText(b+"\n"+a);
            editText1.setText("");
        }
    }
}
