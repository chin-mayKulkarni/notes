package com.notes.test.ui.SyllabusCopy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.notes.test.R;
import com.notes.test.ui.downloadLinks.DownloadLink;
import com.notes.test.urlConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SyllabusCopyFragment extends Fragment {

    List<String> jsonBranch = new ArrayList<String>();
    Button btn_go;
    String branchSel;
    int branchPos = 0;
    private AdView mAdView;


    private SyllabusCopyViewModel syllabusCopyViewModel;

    public static void openDownloadlinkActivity(String s, Context context) {
        Intent intent = new Intent(context, DownloadLink.class);
        Bundle bundle = new Bundle();
        bundle.putString("JsonResponse", String.valueOf(s));
        bundle.putString("fragmentName", "Syllabus Copy");
        Log.d("Value passed:", String.valueOf(s));
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        syllabusCopyViewModel = ViewModelProviders.of(this).get(SyllabusCopyViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        showProgressBar(root);
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        mAdView = root.findViewById(R.id.adView5);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        jsonBranch.add("BRANCH");
        getBranchDetailsForNotes(getContext(), root);

        Spinner dropdown = root.findViewById(R.id.branch_spinner_sc);
        btn_go = root.findViewById(R.id.btn_syllabus_go);

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Clicked on Go", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                syllabusCopyViewModel.getJsonResponse(getContext(), branchSel);

            }
        });

        ArrayAdapter<String> adapter = adapterFunList(jsonBranch);
        dropdown.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        dropdown.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                       long id) {
                branchPos = pos;
                //showProgressBar(root);
                if (jsonBranch.get(pos) != "BRANCH"){
                    btn_go.setEnabled(true);
                    btn_go.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    btn_go.setTextColor(getResources().getColor(R.color.white));
                }
                branchSel = jsonBranch.get(pos);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });






        return root;
    }

    private ArrayAdapter<String> adapterFunList(List<String> jsonBranch) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_item, jsonBranch);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        return adapter;
    }

    private void getBranchDetailsForNotes(Context context, final View root) {

        RequestQueue queue;
        queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                appendDeviceId(urlConstants.URL_MASTER),
                (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response;
                            JSONArray jsonBranchArray = jsonObject.getJSONArray("branches");
                            for (int i =0; i<jsonBranchArray.length(); i++){
                                JSONObject jsonObject1 = jsonBranchArray.getJSONObject(i);
                                String branch = jsonObject1.getString("branch_name");
                                jsonBranch.add(branch);
                            }
                            Log.d("jsonresponse", "branch json" + jsonBranch);
                            hideProgressBar(root);
                        } catch (Exception e) {
                            e.printStackTrace();
                            hideProgressBar(root);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressBar(root);

            }
        });
        queue.add(jsonObjectRequest);
    }

    public String appendDeviceId(String url){
        url = url + "/" + syllabusCopyViewModel.getDatFromSharedpref(getContext());
        return url;
    }

    public void hideProgressBar(View root){
        root.findViewById(R.id.loading_overlay).setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void showProgressBar(View root) {
        root.findViewById(R.id.loading_overlay).setVisibility(View.VISIBLE);
        root.findViewById(R.id.loading_overlay).sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
        root.findViewById(R.id.loading_overlay).requestFocus();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        root.setClickable(false);
    }
}