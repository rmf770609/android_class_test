package com.example.raymond.simpleui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DrinkMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_menu);
    }

    public void add(View view)
    {
        Button button = (Button)view; //view can be parent
        int number = Integer.parseInt(button.getText().toString()); //String to Int
        number++;
        button.setText(String.valueOf(number)); //Int to String
    }

    public void done(View view)
    {
        /* 0317 Lesson 5 */
        JSONArray array = getData();

        Intent data = new Intent();

        /* 0317 Lesson 5 */
        data.putExtra("result", array.toString());
        //data.putExtra("result", "order_done");

        setResult(RESULT_OK, data);
        finish();
    }

    /* 0317 Lesson 5 */
    public JSONArray getData()
    {
        LinearLayout rootLinearLayout = (LinearLayout)findViewById(R.id.root);
        int count = rootLinearLayout.getChildCount();

        JSONArray array = new JSONArray();

        for(int i=0; i < count - 1; i++) //最後一排不取
        {
            LinearLayout ll = (LinearLayout)rootLinearLayout.getChildAt(i);
            TextView drinkNameTextView = (TextView)ll.getChildAt(0);
            Button lButton = (Button)ll.getChildAt(1);
            Button mButton = (Button)ll.getChildAt(2);

            String drinkName = drinkNameTextView.getText().toString();
            int lNumber = Integer.parseInt(lButton.getText().toString());
            int mNumber = Integer.parseInt(mButton.getText().toString());


            try
            {
                JSONObject object = new JSONObject();

                object.put("name", drinkName);
                object.put("lNumber", lNumber);
                object.put("mNumber", mNumber);

                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return array;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug", "DrinkMenu onDestroy");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug", "DrinkMenu onRestart");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug", "DrinkMenu onStart");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug", "DrinkMenu onStop");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug", "DrinkMenu onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug", "DrinkMenu onPause");
    }

    public void cancel(View view)
    {
        finish();
    }

}
