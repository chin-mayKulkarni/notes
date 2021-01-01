package com.notes.test.ui.fragmentHolder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.notes.test.R;
import com.notes.test.ui.notes.GalleryFragment;
import com.notes.test.ui.questionpaper.HomeFragment;

public class FragmentHolder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_holder);
        VideoView vidView = (VideoView)findViewById(R.id.myVideo);
        MediaController vidControl = new MediaController(this);
        vidControl.setAnchorView(vidView);
        vidView.setMediaController(vidControl);

        Bundle bundle = getIntent().getExtras();
        String header = bundle.getString("Header");
        String fragmentName = bundle.getString("fragmentName");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(header);
        }
        String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
        Uri vidUri = Uri.parse(vidAddress);
        vidView.setVideoURI(vidUri);
        vidView.start();
       // displayFragmentInActivity(fragmentName);

    }

    private void displayFragmentInActivity(String fragmentName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentName != null) {
            if (fragmentName.equals("GalleryFragment")) {
                GalleryFragment fragmentSent = new GalleryFragment();
                fragmentManager.beginTransaction().replace(R.id.fragment_layout, fragmentSent, "tag").commit();
            } else if (fragmentName.equals("HomeFragment")) {
                HomeFragment fragmentSent = new HomeFragment();
                fragmentManager.beginTransaction().replace(R.id.fragment_layout, fragmentSent, "tag").commit();
            } else if(fragmentName.equals("VideoPlayer")){
                String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
                Uri vidUri = Uri.parse(vidAddress);
                /*vidView.setVideoURI(vidUri);
                vidView.start();*/

            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}