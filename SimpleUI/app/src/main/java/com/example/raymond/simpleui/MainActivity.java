package com.example.raymond.simpleui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    EditText editText;
    CheckBox hideCheckBox;
    /*2016-0310*/
    SharedPreferences sp;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // res/layout/activity_main.xml

        textView = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);
        /*2016-0310*/
        sp = getSharedPreferences("setting" , Context.MODE_PRIVATE );
        editor = sp.edit();
        //setting:
        //  editText: xxxxxx
        editText.setText(sp.getString("editText" , ""));

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                /*2016-0310*/
                editor.putString("editText" , editText.getText().toString()); //Put value in editText into setting:editText as xxxxxxx
                editor.apply(); //Execute

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    submit(v);
                    return true;
                }
                return false;
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    submit(v);
                    return true;
                }
                return false;
            }
        });

        hideCheckBox = (CheckBox)findViewById(R.id.checkBox);

    }

    public void submit(View view)
    {
        String text = editText.getText().toString();
        if(hideCheckBox.isChecked())
        {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            textView.setText("******");
            editText.setText("******");
            return;
        }
        textView.setText(text);
        editText.setText("");

        //Toast.makeText(this, "Hello World", Toast.LENGTH_LONG).show();
        //textView.setText("START");
    }

}
