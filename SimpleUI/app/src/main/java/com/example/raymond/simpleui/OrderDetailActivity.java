package com.example.raymond.simpleui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderDetailActivity extends AppCompatActivity {

    TextView note;
    TextView storeInfo;
    TextView menu;
    ImageView photo;
    ImageView staticMapImageView;
    WebView webView;

    /* 0331 */
    MapFragment mapFragment;
    GoogleMap map;

    /* HW */
    Switch switchMap;

//    /* 0328 */
//    private String url;
//    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        note = (TextView)findViewById(R.id.noteView);
        storeInfo = (TextView)findViewById(R.id.storeInfoView);
        menu = (TextView)findViewById(R.id.menuView);
        photo = (ImageView)findViewById(R.id.photoView);
        staticMapImageView = (ImageView)findViewById(R.id.staticMapImageView);
        webView = (WebView)findViewById(R.id.webView);

        /* 0331 */
        mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
            }
        });


        /* HW */
        switchMap = (Switch)findViewById(R.id.switchMap);
        switchMap.setChecked(true);
        switchMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {
                    staticMapImageView.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.VISIBLE);
                }
                else
                {
                    staticMapImageView.setVisibility(View.GONE);
                    webView.setVisibility(View.GONE);
                }
            }
        });

        note.setText(getIntent().getStringExtra("note"));

        /* 0328 */
        String storeInformation = getIntent().getStringExtra("storeInfo");

        storeInfo.setText(getIntent().getStringExtra("storeInfo"));
        String menuResult = getIntent().getStringExtra("menu");

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
            menu.setText(text);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /* 0328 urlToBytes: Using Thread (not recommended) */
        String address = storeInformation.split(",")[1];

        new GeocodingTask().execute(address);
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                double[] locations = Utils.addressToLatLng(address);
//                String debugLog = "lat: " + String.valueOf(locations[0]) + " lng: " + String.valueOf(locations[1]);
//                Log.d("debug", debugLog);
//            }
//        });
//        thread.start();
        /* Thread End */

        String url = getIntent().getStringExtra("photoURL");

        if (url == null)
            return;

        new ImageLoadingTask(photo).execute(url);



//        /* 0328 optional: uriToBytes with Picasso */
//        if (url != null)
//        {
//            Picasso.with(this).load(url).into(photo);
//        }

        /* 0328 urlToBytes: Using Thread (not recommended) */
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                byte[] bytes = Utils.urlToBytes(url);
//                String result = new String(bytes);
//                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                photo.setImageBitmap(bmp);
//            }
//        });
//        thread.start();

        /* 0328 urlToBytes: Using AsyncTask */
//        new AsyncTask<String, Void, byte[]>()
//        {
//            @Override
//            protected byte[] doInBackground(String... params) {
//
//                String url = params[0];
//                return Utils.urlToBytes(url);
//                //return new Byte[0];
//            }
//
//            @Override
//            protected void onPostExecute(byte[] bytes) {
//                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                photo.setImageBitmap(bmp);
//                super.onPostExecute(bytes);
//            }
//        }.execute(url);
    }



    class GeocodingTask extends AsyncTask<String, Void, byte[]> //繼承
    {
        private double[] latlng;
        private String url;

        @Override
        protected byte[] doInBackground(String... params) {
            String address = params[0];
            latlng = Utils.addressToLatLng(address);
            url = Utils.getStaticMapUrl(latlng, 17);
            return Utils.urlToBytes(url);
            //return new byte[0];
        }

        @Override
        protected void onPostExecute(byte[] bytes) {

            webView.loadUrl(url);

            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            staticMapImageView.setImageBitmap(bmp);

            /* 0331 */
            LatLng location = new LatLng(latlng[0], latlng[1]);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));

            String[] storeInfos = getIntent().getStringExtra("storeInfo").split(",");
            map.addMarker(new MarkerOptions()
                    .title(storeInfos[0])
                    .snippet(storeInfos[1])
                    .position(location));
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Toast.makeText(OrderDetailActivity.this, marker.getTitle(), Toast.LENGTH_LONG).show();
                    return false;
                }
            });


            super.onPostExecute(bytes);
        }

    }

    class ImageLoadingTask extends AsyncTask<String, Void, byte[]> //繼承
    {
        ImageView imageView;

        @Override
        protected byte[] doInBackground(String... params) {
            String url = params[0];
            return Utils.urlToBytes(url);
            //return new byte[0];
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bmp);
            super.onPostExecute(bytes);
        }

        public ImageLoadingTask(ImageView imageView)
        {
            this.imageView = imageView;
        }

    }

}
