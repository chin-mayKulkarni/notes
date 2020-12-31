package com.notes.test;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.navigation.NavigationView;
import com.notes.test.ui.MySingleton;
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

public class MainActivity extends AppCompatActivity {

    public ArrayList<String> jsonSem , jsonBranch;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        CardView cardNotes = findViewById(R.id.notes_card);
        CardView qpNotes = findViewById(R.id.qp_card);
        CardView whatsapp = findViewById(R.id.support_card);
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
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
                String url = "https://chat.whatsapp.com/EDkwCsWCErIF5AWNuVmYXJ";
                intentWhatsapp.setData(Uri.parse(url));
                intentWhatsapp.setPackage("com.whatsapp");
                startActivity(intentWhatsapp);

                /*Intent intent = new Intent(MainActivity.this,
                        FragmentHolder.class);
                intent.putExtra("Header", "Question Papers");
                intent.putExtra("fragmentName", "HomeFragment");
                MainActivity.this.startActivity(intent);*/
            }
        });

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