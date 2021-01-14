package com.notes.test.ui.fragmentHolder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.notes.test.R;
import com.notes.test.ui.Fragments.AboutUsFragment;
import com.notes.test.ui.Fragments.FeedbackFragment;
import com.notes.test.ui.Fragments.DonateFragment;
import com.notes.test.ui.notes.GalleryFragment;
import com.notes.test.ui.questionpaper.HomeFragment;
import com.notes.test.ui.SyllabusCopy.SyllabusCopyFragment;

public class FragmentHolder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_holder);

        Bundle bundle = getIntent().getExtras();
        String header = bundle.getString("Header");
        String fragmentName = bundle.getString("fragmentName");



        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(header);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        }
         displayFragmentInActivity(fragmentName);

    }



    private void displayFragmentInActivity(String fragmentName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentName.equals("GalleryFragment")){
            GalleryFragment fragmentSent = new GalleryFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_layout , fragmentSent, "tag").commit();
        } else if (fragmentName.equals("HomeFragment")){
            HomeFragment fragmentSent = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_layout , fragmentSent, "tag").commit();
        } else if (fragmentName.equals("feedback")){
            FeedbackFragment fragmentSent = new FeedbackFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_layout , fragmentSent, "tag").commit();
        } else if (fragmentName.equals("DonateNotes")){
            DonateFragment fragmentSent = new DonateFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_layout , fragmentSent, "tag").commit();
        }else if (fragmentName.equals("about")){
            AboutUsFragment fragmentSent = new AboutUsFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_layout , fragmentSent, "tag").commit();
        }else {
            SyllabusCopyFragment fragmentSent = new SyllabusCopyFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_layout , fragmentSent, "tag").commit();
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