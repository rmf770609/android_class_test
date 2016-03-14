package com.example.raymond.simpleui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
        Intent data = new Intent();
        data.putExtra("result", "order_done");
        setResult(RESULT_OK, data);
        finish();
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
