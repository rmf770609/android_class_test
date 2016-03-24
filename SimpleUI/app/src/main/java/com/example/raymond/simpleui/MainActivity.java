package com.example.raymond.simpleui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.Manifest; //for version limited

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MENU_ACTIVITY = 0;
    private static final int REQUEST_CODE_CAMERA = 1;

    /* 0324 */
    private Boolean hasPhoto = false;

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

    /* 0321 */
    private List<ParseObject> queryResult;
    ImageView photoView;

    /* 0324 */
    ProgressDialog progressDialog;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // res/layout/activity_main.xml


        /* 0317 */
//        /* 0321 move to SimpleUIApplication */
//        Parse.enableLocalDatastore(this);
//        Parse.initialize(this);
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("hi", "heyyyyyyyyyyyyy"); //testObject.put("lai", "heyyyyyyyyyyyyy");
        //testObject.saveInBackground();
        /* NW issue can be found in debug message as below */
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d("debug", e.toString());
                }
            }
        });


//        /* homework */
//        Parse.enableLocalDatastore(this);
//        Parse.initialize(this);
//        ParseObject hwObject = new ParseObject("HomeworkParse");
//        hwObject.put("sid", "And26315");
//        hwObject.put("email", "tienhungfong@gmail.com");
//        hwObject.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e != null) {
//                    Log.d("debug", e.toString());
//                }
//            }
//        });

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

        /* 0321 */
        photoView = (ImageView)findViewById(R.id.imageView);

        progressDialog = new ProgressDialog(this); //0324
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

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
        hideCheckBox.setChecked(sp.getBoolean("hideCheckBox", false));  // if hideCheckBox is empty, get "false" as default

        hideCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("hideCheckBox", hideCheckBox.isChecked());
                editor.apply();

                /* 0324 */
                if(isChecked)
                {
                    photoView.setVisibility(View.GONE);
                }
                else
                {
                    photoView.setVisibility(View.VISIBLE);
                }

            }
        });

        /* 0324*/
        listView.setVisibility(View.GONE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToDetailOrder(position);
            }
        });

        setSpinner();
        setListView();

    }

    private void setListView()
    {
        /* 0321 */
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Order"); //要塞變數名稱“Order"
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e != null)
                {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE); //0324
                    return;
                }

                queryResult = objects;

                List<Map<String, String>> data = new ArrayList<Map<String, String>>();

                for(int i=0; i < queryResult.size(); i++)
                {
                    ParseObject object = queryResult.get(i);
                    String note = object.getString("note");
                    String storeInfo = object.getString("storeInfo");
                    String menu = object.getString("menu");

                    Map<String, String> item = new HashMap<String, String>();

                    item.put("note", note);
                    item.put("storeInfo", storeInfo);
                    item.put("drinkNum", getDrinkNumber(menu)); //HW

                    data.add(item);
                }

                String[] from = {"note", "storeInfo", "drinkNum"};
                int[] to = {R.id.note, R.id.storeInfo, R.id.drinkNum}; //why int ??

                SimpleAdapter adaptor = new SimpleAdapter(MainActivity.this, data, R.layout.listitem_item, from, to);

                listView.setAdapter(adaptor);
                /* 0324 */
                listView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
//        //String[] data = {"1", "2", "3", "4", "5"};
//        String[] data = Utils.readFile(this, "history.txt").split("\n");
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
//        listView.setAdapter(adapter);
    }

    /* 0321HW */
    private String getDrinkNumber(String menu) {
        String menuNumber = "";
        int totalNumber = 0;

        try{
            JSONArray menuArray = new JSONArray(menu);

            for(int i=0; i<menuArray.length(); i++)
            {
                JSONObject order = menuArray.getJSONObject(i);
                totalNumber += order.getInt("lNumber") + order.getInt("mNumber");
                menuNumber = String.valueOf(totalNumber);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return menuNumber;
    }

    private void setSpinner()
    {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("StoreInfo");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e != null)
                {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                String[] stores = new String[objects.size()];
                for(int i=0; i<objects.size(); i++)
                {
                    ParseObject object = objects.get(i);
                    stores[i] = object.getString("name") + ", " + object.getString("address");
                }

                ArrayAdapter<String> storeAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, stores);
                spinner.setAdapter(storeAdapter);

            }
        });
//        //String[] data = {"1", "2", "3", "4", "5", "6"};
//        String[] data = getResources().getStringArray(R.array.storeInfo);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
//        spinner.setAdapter(adapter);
    }

    public void submit(View view)
    {
        String text = editText.getText().toString();
        Utils.writeFile(this, "history.txt", text + '\n');

        /* 0317 */
        ParseObject orderObject = new ParseObject("Order");

        orderObject.put("note", text);
        orderObject.put("storeInfo", spinner.getSelectedItem());
        orderObject.put("menu", menuResult);

        /* 0324 */
        if(hasPhoto)
        {
            Uri uri = Utils.getPhotoUri();
            ParseFile file = new ParseFile("photo.png", Utils.uriToBytes(this, uri));

            orderObject.put("photo", file);
        }

        progressDialog.setTitle("Loading...");
        progressDialog.show();

        orderObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    Toast.makeText(MainActivity.this, "Submit OK", Toast.LENGTH_LONG);

                    /* 0324*/
                    photoView.setImageResource(0);
                    textView.setText("");
                    editText.setText("");
                    setListView();
                    hasPhoto = false;

                } else {
                    Toast.makeText(MainActivity.this, "Submit FAIL", Toast.LENGTH_LONG);
                }
            }
        });

//        /* 0324 delete */
//        if(hideCheckBox.isChecked())
//        {
//            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
//            textView.setText("******");
//            editText.setText("******");
//            return;
//        }
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
        /* 0321 */
        else if (requestCode == REQUEST_CODE_CAMERA)
        {
            Log.d("debug", "REQUEST_CODE_CAMERA");
            if (resultCode == RESULT_OK)
            {
                Log.d("debug", "CAMERA_Result_OK");
                photoView.setImageURI(Utils.getPhotoUri());
                hasPhoto = true; //0324
            }
        }
    }

    /* 0321 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);

        int id = item.getItemId();

        if(id == R.id.action_take_photo)
        {
            Toast.makeText(this, "Take Photo", Toast.LENGTH_LONG).show();
            goToCamera();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToCamera()
    {
//        if (Build.VERSION.SDK_INT >= 23)
//        {
//            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            {
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//                return;
//            }
//        }
//        if (Build.VERSION.SDK_INT >= 23)
//        {
//            if (checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)
//            {
//                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 0);
//                return;
//            }
//        }
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Utils.getPhotoUri());
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
        //startActivity(intent);
    }

    /* 0324 */
    public void goToDetailOrder(int position)
    {
        ParseObject object = queryResult.get(position);

        Intent intent = new Intent();
        intent.setClass(this, OrderDetailActivity.class);

        intent.putExtra("note", object.getString("note"));
        intent.putExtra("storeInfo", object.getString("storeInfo"));
        intent.putExtra("menu", object.getString("menu"));

        if(object.getParseFile("photo") != null)
        {
            intent.putExtra("photoURL", object.getParseFile("photo").getUrl());
        }

        startActivity(intent);
    }
}
