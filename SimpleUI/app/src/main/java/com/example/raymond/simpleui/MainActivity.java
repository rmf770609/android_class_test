package com.example.raymond.simpleui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    EditText editText;
    CheckBox hideCheckBox;
    /*2016-0310*/
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    ListView listView;
    Spinner spinner;


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
        editText.setText(sp.getString("editText", "")); // if editText is empty, get "" as default

        listView = (ListView)findViewById(R.id.listView);
        spinner = (Spinner)findViewById(R.id.spinner);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                /*2016-0310*/
                editor.putString("editText", editText.getText().toString()); //Put value in editText into setting:editText as xxxxxxx
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

        /*2016-0310*/
        hideCheckBox.setChecked(sp.getBoolean("hideCheckBox" , false));  // if hideCheckBox is empty, get "false" as default
        hideCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("hideCheckBox", hideCheckBox.isChecked());
                editor.apply();
            }
        });

        setSpinner();
        setListView();

    }

    private void setListView()
    {
        //String[] data = {"1", "2", "3", "4", "5"};
        String[] data = Utils.readFile(this, "history.txt").split("\n");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
    }

    private void setSpinner()
    {
        //String[] data = {"1", "2", "3", "4", "5", "6"};
        String[] data = getResources().getStringArray(R.array.storeInfo);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        spinner.setAdapter(adapter);
    }

    public void submit(View view)
    {
        String text = editText.getText().toString();
        Utils.writeFile(this, "history.txt", text+'\n');
        if(hideCheckBox.isChecked())
        {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            textView.setText("******");
            editText.setText("******");
            return;
        }
        textView.setText(text);
        editText.setText("");
        setListView();
        //Toast.makeText(this, "Hello World", Toast.LENGTH_LONG).show();
        //textView.setText("START");
    }

}
