package com.notes.test.ui.notes;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.notes.test.R;
import com.notes.test.ui.downloadLinks.DownloadLink;
import com.notes.test.urlConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class GalleryFragment extends Fragment {

    int branchPos = 0;
    List<String> jsonSem = new ArrayList<String>();
    List<String> jsonSub = new ArrayList<String>();
    Button btn_go;
    public List<String> jsonBranch = new ArrayList<String>();
    String branchSel, subSel, semSel;
    private GalleryViewModel galleryViewModel;


    public GalleryFragment() { }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);

//Invokes progress bar for 5 seconds and disappears
        showProgressBar(root);

        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Branch");
        spinnerArray.add("ISE");
        jsonSem.add("SEMESTER");
        jsonBranch.add("BRANCH");
        jsonSub.add("SUBJECT");

        getBranchDetailsForNotes(getContext(), root);
        Spinner dropdown = root.findViewById(R.id.branch_spinner);
        final Spinner dropdown2 = root.findViewById(R.id.sem_spinner);
        final Spinner dropdown3 = root.findViewById(R.id.sub_spinner);

        btn_go = root.findViewById(R.id.btn_go);
        //galleryViewModel.getBranchDetails(getContext());

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Clicked on Go", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                galleryViewModel.getJsonResponse(getContext(), semSel, branchSel,subSel);

            }
        });


        ArrayAdapter<String> adapter = adapterFunList(jsonBranch);
        dropdown.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter = adapterFunList(jsonSem);
        dropdown2.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter = adapterFunList(jsonSub);
        dropdown3.setAdapter(adapter);


        dropdown.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                       long id) {
                branchPos = pos;
                //showProgressBar(root);
                if (jsonBranch.get(pos).equals("P cycle") || jsonBranch.get(pos).equals("C cycle")) {
                    getSubjectForNotes(getContext(), "1st SEMESTER", jsonBranch.get(branchPos), root);
                    ArrayAdapter<String> adapter = adapterFunList(jsonSub);
                    dropdown2.setVisibility(View.GONE);
                    dropdown3.setAdapter(adapter);
                    dropdown3.setSelection(0);
                    showProgressBar(root);
                    semSel = "1st SEMESTER";
                    dropdown3.setVisibility(View.VISIBLE);
                } else if (jsonBranch.get(pos) != "BRANCH") {
                        dropdown2.setVisibility(View.VISIBLE);
                        if (dropdown3.getVisibility() == View.VISIBLE) {
                            dropdown3.setVisibility(View.GONE);
                            ArrayAdapter<String> adapter = adapterFunList(jsonSem);
                            dropdown2.setAdapter(adapter);
                            btn_go.setEnabled(false);
                        }
                    } else {
                        dropdown2.setVisibility(View.GONE);
                        dropdown3.setVisibility(View.GONE);
                    }
                Toast.makeText(getContext(), "onItemSelected"+ jsonBranch.get(pos), Toast.LENGTH_LONG).show();
                branchSel = jsonBranch.get(pos);

//                ((TextView) view).setTextColor(Color.RED);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        dropdown2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                       long id) {
                if (jsonSem.get(pos) != "SEMESTER"){

                    dropdown3.setVisibility(View.VISIBLE);
                    getSubjectForNotes(getContext(), jsonSem.get(pos), jsonBranch.get(branchPos), root);

                    ArrayAdapter<String> adapter  = adapterFunList(jsonSub);
                    dropdown3.setAdapter(adapter);
                    dropdown3.setSelection(0);
                    showProgressBar(root);
                } else dropdown3.setVisibility(View.GONE);
                semSel = jsonSem.get(pos);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dropdown3.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                       long id) {
                btn_go = getActivity().findViewById(R.id.btn_go);
                if (jsonSub.get(pos) != "SUBJECT"){
                    btn_go.setEnabled(true);
                    btn_go.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    btn_go.setTextColor(getResources().getColor(R.color.white));
                }
                subSel = jsonSub.get(pos);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
                textView.setVisibility(View.GONE);
            }
        });
        return root;
    }

//To show loading spinner
    public void showProgressBar(View root){
        root.findViewById(R.id.loading_overlay).setVisibility(View.VISIBLE);
        root.findViewById(R.id.loading_overlay).sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
        root.findViewById(R.id.loading_overlay).requestFocus();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        root.setClickable(false);
    }

    public void hideProgressBar(View root){
        root.findViewById(R.id.loading_overlay).setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }



    private ArrayAdapter<String> adapterFunList(List<String> spinnerArray) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        return adapter;
    }

    private ArrayAdapter<String> adapterFun(String[] spinnerList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        return adapter;
    }


//Method will open DownloadLink Activity and passes JSON response
    public static void openDownloadlinkActivity(String s, Context context) {
        Intent intent = new Intent(context, DownloadLink.class);
        Bundle bundle = new Bundle();
        bundle.putString("JsonResponse", String.valueOf(s));
        bundle.putString("fragmentName", "Notes");
        Log.d("Value passed:", String.valueOf(s));
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


//Calls API and gets branch and Sem list
    public void getBranchDetailsForNotes(Context context, final View root) {

        RequestQueue queue;
        queue = Volley.newRequestQueue(context);
       // queue = MySingleton.getInstance(context).getRequestQueue();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                appendDeviceId(urlConstants.URL_MASTER),
                (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response;
                            JSONArray jsonArray = jsonObject.getJSONArray("semester");
                            for (int i =0; i<jsonArray.length(); i++){
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String sem = jsonObject1.getString("sem_name");
                                jsonSem.add(sem);
                            }
                            JSONArray jsonBranchArray = jsonObject.getJSONArray("branches");
                            for (int i =0; i<jsonBranchArray.length(); i++){
                                JSONObject jsonObject1 = jsonBranchArray.getJSONObject(i);
                                String branch = jsonObject1.getString("branch_name");
                                jsonBranch.add(branch);
                            }
                            Log.d("jsonresponse", "sem" + jsonSem);
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
        url = url + "/" + galleryViewModel.getDatFromSharedpref(getContext());
        return url;
    }

//Calls API and gets subjects list for selected branch and sem
    public void getSubjectForNotes(final Context context, String sem, String branch, final View root) {

        RequestQueue queue;
        queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                /*urlConstants.URL_GET_SUBJECT*/ getSubUrl(sem, branch),
                (JSONObject) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray jsonArray = response;
                            jsonSub.clear();
                            jsonSub.add("Subject");
                            for (int i = 0; i< jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String sub = jsonObject.getString("subject_name");
                                jsonSub.add(sub);
                            }
                            Log.d("jsonresponse", "Sunject json" + jsonSub);
                            hideProgressBar(root);
                        } catch (Exception e) {
                            e.printStackTrace();
                            hideProgressBar(root);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                jsonSub.clear();
                jsonSub.add("Subject");
                Toast.makeText(context,"Some error has occured",Toast.LENGTH_LONG).show();
                hideProgressBar(root);

            }
        });
        queue.add(jsonArrayRequest);
    }


//Method which appends Sem and Branch to base url and returns sub api.
    private String getSubUrl(String sem, String branch) {
        String subUrl = urlConstants.URL_GET_SUBJECT_BASE + "/" + galleryViewModel.stringNoSpace(sem) + "/" + galleryViewModel.stringNoSpace(branch);
        subUrl = appendDeviceId(subUrl);
        Log.d("json", "subURL::" + subUrl);
        return subUrl;
    }

    private void createSnackBar(){

        /*Snackbar.make(relativeLayout, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();*/

    }
}