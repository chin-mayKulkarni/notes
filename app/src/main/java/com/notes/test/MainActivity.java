package com.notes.test;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.notes.test.ui.MySingleton;
import com.notes.test.ui.fragmentHolder.FragmentHolder;

import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public ArrayList<String> jsonSem , jsonBranch;
    public static String FACEBOOK_URL = "https://www.facebook.com/chinmay.kulkarni.75839";
    public static String FACEBOOK_PAGE_ID = "chinmay.kulkarni.75839";
    private AppBarConfiguration mAppBarConfiguration;
    private AdView mAdView, mAdView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isInternetConnected()) {
            processWithInternet();
        } else showCustomDialogue();
    }

    private void processWithInternet(){
        //Device id generation
        if (getDatFromSharedpref() ==null){
            String dev = generateDeviceId();
        }
        callInitialLoadApi(getApplicationContext());

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        //MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        mAdView = findViewById(R.id.adView);
        mAdView2 = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView2.loadAd(adRequest);


        CardView cardNotes = findViewById(R.id.notes_card);
        CardView qpNotes = findViewById(R.id.qp_card);
        LinearLayout facebook = findViewById(R.id.ButtonFacebook);
        CardView syllabuscopy = findViewById(R.id.sc_card);
        CardView supportUs = findViewById(R.id.support_card);
        LinearLayout instagram = findViewById(R.id.ButtonInstagram);
        LinearLayout telegram = findViewById(R.id.buttonTelegram);
        LinearLayout whatsapp = findViewById(R.id.ButtonWhatsapp);
        TextView feedback = findViewById(R.id.feedbackText);
        TextView aboutUs = findViewById(R.id.aboutText);
        ImageView upload_card = findViewById(R.id.donate_icon);

        supportUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(urlConstants.URL_BUY_COFFEE)));
            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetConnected()){
                    Intent intent = new Intent(MainActivity.this,
                            FragmentHolder.class);
                    intent.putExtra("Header", "About Us");
                    intent.putExtra("fragmentName", "about");
                    MainActivity.this.startActivity(intent);
                } else showCustomDialogue();

            }
        });

        upload_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetConnected()) {
                    Intent intent = new Intent(MainActivity.this,
                            FragmentHolder.class);
                    intent.putExtra("Header", "Donate Notes");
                    intent.putExtra("fragmentName", "DonateNotes");
                    MainActivity.this.startActivity(intent);
                }else showCustomDialogue();
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetConnected()) {
                    Intent intent = new Intent(MainActivity.this,
                            FragmentHolder.class);
                    intent.putExtra("Header", "Feedback");
                    intent.putExtra("fragmentName", "feedback");
                    MainActivity.this.startActivity(intent);

                }else showCustomDialogue();

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

                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(getApplication());
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out this amazing app where you can easily get Free VTU notes for all subjects : https://play.google.com/store/apps/details?id=com.notes.test" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);


            }
        });
        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
                String url = "https://t.me/joinchat/FzuqiL62dreK2Pkx";
                intentWhatsapp.setData(Uri.parse(url));
                intentWhatsapp.setPackage("org.telegram.messenger");
                try {
                    startActivity(intentWhatsapp);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url)));
                }
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




//This API is called when app is launched, here device ID is passed to backend.
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

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConnection != null && wifiConnection.isConnected()) || (mobileConnection != null && mobileConnection.isConnected())) {
            return true;
        } else return false;

    }


    private void showCustomDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please connect to internet to Proceed")
                .setCancelable(false)
                .setTitle("No Internet")
                .setIcon(R.drawable.nointernet)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}