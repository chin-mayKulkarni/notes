package com.notes.test;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.navigation.NavigationView;
import com.notes.test.ui.MySingleton;
import com.notes.test.ui.feedback.FeedbackFragment;
import com.notes.test.ui.fragmentHolder.FragmentHolder;
import com.notes.test.ui.notes.GalleryFragment;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public ArrayList<String> jsonSem , jsonBranch;
    public static String FACEBOOK_URL = "https://www.facebook.com/chinmay.kulkarni.75839";
    public static String FACEBOOK_PAGE_ID = "chinmay.kulkarni.75839";

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
//Device id generation
        if (getDatFromSharedpref() ==null){
            String dev = generateDeviceId();
        }
        callInitialLoadApi(getApplicationContext());

        CardView cardNotes = findViewById(R.id.notes_card);
        CardView qpNotes = findViewById(R.id.qp_card);
        LinearLayout facebook = findViewById(R.id.ButtonFacebook);
        CardView syllabuscopy = findViewById(R.id.sc_card);
        LinearLayout instagram = findViewById(R.id.ButtonInstagram);
        LinearLayout telegram = findViewById(R.id.buttonTelegram);
        LinearLayout whatsapp = findViewById(R.id.ButtonWhatsapp);
        TextView feedback = findViewById(R.id.feedbackText);

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        FragmentHolder.class);
                intent.putExtra("Header", "Feedback");
                intent.putExtra("fragmentName", "feedback");
                MainActivity.this.startActivity(intent);

            }
        });
        cardNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        FragmentHolder.class);
                intent.putExtra("Header", "Notes");
                intent.putExtra("fragmentName", "GalleryFragment");
                MainActivity.this.startActivity(intent);
            }
        });
        qpNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        FragmentHolder.class);
                intent.putExtra("Header", "Question Papers");
                intent.putExtra("fragmentName", "HomeFragment");
                MainActivity.this.startActivity(intent);
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
                String url = "https://chat.whatsapp.com/EDkwCsWCErIF5AWNuVmYXJ";
                intentWhatsapp.setData(Uri.parse(url));
                intentWhatsapp.setPackage("com.whatsapp");
                startActivity(intentWhatsapp);*/

                /*Intent intent = new Intent(MainActivity.this,
                        FragmentHolder.class);
                intent.putExtra("Header", "About us");
                intent.putExtra("fragmentName", "AboutUs");
                MainActivity.this.startActivity(intent);*/

                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(getApplication());
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
                String url = "https://chat.whatsapp.com/EDkwCsWCErIF5AWNuVmYXJ";
                intentWhatsapp.setData(Uri.parse(url));
                intentWhatsapp.setPackage("com.whatsapp");
                startActivity(intentWhatsapp);
            }
        });
        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
                String url = "https://t.me/joinchat/FzuqiL62dreK2Pkx";
                intentWhatsapp.setData(Uri.parse(url));
                intentWhatsapp.setPackage("org.telegram.messenger");
                startActivity(intentWhatsapp);
            }
        });
        syllabuscopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        FragmentHolder.class);
                intent.putExtra("Header", "Syllabus Copy");
                intent.putExtra("fragmentName", "Syllabus Copy");
                MainActivity.this.startActivity(intent);
            }
        });
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInstagram("https://www.instagram.com/chin_mai_kulkarni/");
            }
        });

    }

    private void callInitialLoadApi(final Context context) {
            final RequestQueue queue;
            queue = MySingleton.getInstance(context).getRequestQueue();
            final String[] jsonObject = new String[1];

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    /*urlConstants.URL_TEST */getApi(),
                    (JSONObject) null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            jsonObject[0] = response.toString();
                            Log.d("response", jsonObject[0]);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context,"Some error has occured",Toast.LENGTH_LONG).show();
                    Log.d("error ", "error occured :::::" + error);
                }
            });
            queue.add(jsonObjectRequest);
    }

    private String getApi() {
        String api;
        api = urlConstants.URL_DEVICE_ID + "/" + getDatFromSharedpref();
        return api;
    }

    public String getDatFromSharedpref() {
        SharedPreferences sharedPreferences = getSharedPreferences("FirstSharedpref", 0);
        String deviceID = sharedPreferences.getString("DeviceID", null);
        String version = sharedPreferences.getString("Version", " ");
        Log.d("fromSharedpred", "This is message : " +deviceID);
        return deviceID;
    }

    private String generateDeviceId() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 16;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();
        Log.d("Device ID","This is device id:" + generatedString);
        storeDataInSharedPref(generatedString);
        return generatedString;
    }

    private void storeDataInSharedPref(String generatedString) {
        SharedPreferences sharedPreferences = getSharedPreferences("FirstSharedpref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("DeviceID", generatedString);
        myEdit.putString("Version", "1.0.2");
        myEdit.commit();

    }

    private void openInstagram(String url){
        Uri uri = Uri.parse(url);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(url)));
        }
    }

    private String getFacebookPageURL(Context onClickListener) {
        PackageManager packageManager = onClickListener.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}