package com.example.raymond.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MENU_ACTIVITY = 0;

    TextView textView;
    EditText editText;
    CheckBox hideCheckBox;
    /*2016-0310*/
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    ListView listView;
    Spinner spinner;

    /* 0317 */
    String menuResult = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // res/layout/activity_main.xml


//        /* 0317 */
//        Parse.enableLocalDatastore(this);
//        Parse.initialize(this);
//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("hi", "heyyyyyyyyyyyyy"); //testObject.put("lai", "heyyyyyyyyyyyyy");
//        //testObject.saveInBackground();
//        /* NW issue can be found in debug message as below */
//        testObject.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e != null) {
//                    Log.d("debug", e.toString());
//                }
//            }
//        });


        /* homework */
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        ParseObject hwObject = new ParseObject("HomeworkParse");
        hwObject.put("sid", "And26315");
        hwObject.put("email", "tienhungfong@gmail.com");
        hwObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d("debug", e.toString());
                }
            }
        });

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

        /* 0317 */
        ParseObject orderObject = new ParseObject("Order");

        orderObject.put("note", text);
        orderObject.put("storeInfo", spinner.getSelectedItem());
        orderObject.put("menu", menuResult);

        orderObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(MainActivity.this, "Submit OK", Toast.LENGTH_LONG);
                } else {
                    Toast.makeText(MainActivity.this, "Submit FAIL", Toast.LENGTH_LONG);
                }
            }
        });

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug", "Main onDestroy");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug", "Main onRestart");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug", "Main onStart");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug", "Main onStop");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug", "Main onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug", "Main onPause");
    }

    public void goToMenu(View view)
    {
        Intent intent = new Intent();
        intent.setClass(this, DrinkMenuActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, REQUEST_CODE_MENU_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("debug", "Main onActivityResult");

        if(requestCode == REQUEST_CODE_MENU_ACTIVITY)
        {
            if(resultCode == RESULT_OK)
            {
                menuResult = data.getStringExtra("result");

                try{
                    JSONArray array = new JSONArray(menuResult);
                    String text="";

                    for(int i=0; i < array.length(); i++)
                    {
                        JSONObject order = array.getJSONObject(i);

                        String name = order.getString("name");
                        String lNumber = String.valueOf(order.getInt("lNumber"));
                        String mNumber = String.valueOf(order.getInt("mNumber"));

                        text = text + name + " l: " + lNumber + " m: " + mNumber + "\n";

                    }

                    textView.setText(text);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //textView.setText(data.getStringExtra("result"));

            }
        }
    }
}
