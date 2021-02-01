package com.notes.test.ui.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.DocumentsContract;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.notes.test.R;
import com.notes.test.ui.MySingleton;
import com.notes.test.ui.RecyclerView.TrackerListAdapter;
import com.notes.test.ui.RecyclerView.TrackerListData;
import com.notes.test.urlConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.os.ParcelFileDescriptor.MODE_APPEND;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrackerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrackerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public EditText email;
    public Button ok_btn, btn_redeem, btn_showStatements;
    public View view_id;
    public TextView errorEmail, totalRedemeed, totalEarned;
    public LinearLayout earningsLayout, btn_layout;
    List<TrackerListData> myListDataList;
    RecyclerView recyclerView;
    private TrackerListAdapter adapter;
    public String statement_api, Redeem_api;
    public Boolean Show_Redeem = false, show_statement = false;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TrackerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TrackerFragment newInstance(String param1, String param2) {
        TrackerFragment fragment = new TrackerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_tracker, container, false);
        email = root.findViewById(R.id.email_tracker);
        ok_btn = root.findViewById(R.id.btn_ok);
        btn_showStatements = root.findViewById(R.id.btn_showStatements);
        btn_redeem = root.findViewById(R.id.btn_redeem);
        view_id = root.findViewById(R.id.view_id);
        errorEmail = root.findViewById(R.id.errorEmail);
        totalRedemeed = root.findViewById(R.id.totalRedemeed);
        totalEarned = root.findViewById(R.id.totalEarned);
        earningsLayout = root.findViewById(R.id.earningsLayout);
        btn_layout = root.findViewById(R.id.btn_layout);
        recyclerView = root.findViewById(R.id.recyclerView2);
        myListDataList = new ArrayList<>();
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailFormat(root);
            }
        });

        // MyListAdapter myListAdapter = new MyListAdapter();


        if (getStoredMapped_Key() == null) {
            checkIfEmailIsEmpty();
            manageView(true);
            /*earningsLayout.setVisibility(View.GONE);
            btn_layout.setVisibility(View.GONE);
            view_id.setVisibility(View.GONE);
            email.setVisibility(View.VISIBLE);*/

        } else {
           /* email.setVisibility(View.GONE);
            ok_btn.setVisibility(View.GONE);*/
           manageView(false);
            callApi(getContext(), "OLD", getStoredMapped_Key(), root);
            showProgressBar(root);
            /*earningsLayout.setVisibility(View.VISIBLE);
            btn_layout.setVisibility(View.VISIBLE);
            view_id.setVisibility(View.VISIBLE);*/
            btn_showStatements.setClickable(show_statement);
            btn_redeem.setClickable(Show_Redeem);
        }

        btn_redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                apicall(Redeem_api, root);
                showProgressBar(root);



            }
        });

        btn_showStatements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apicall(statement_api, root);
                showProgressBar(root);
            }
        });


        return root;
    }

    private void apicall(String api, final View root) {
        final RequestQueue queue;
        queue = MySingleton.getInstance(getContext()).getRequestQueue();
        final String[] jsonObject = new String[1];

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                getfinalApi(api),
                (JSONObject) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        jsonObject[0] = response.toString();
                        //TODO : get response and populate the page

                        try {
                            String url = response.getString("statement");
                            if (url!=null){
                                hideProgressBar(root);
                                openInBrowser(response);
                            }
                        } catch (JSONException e) {
                            if (jsonObject[0].contains("Earnings")){
                                showCustomDialogue("Success", "Congrats!! We are processing your request. You'll receive an email once payment is done.");
                                refreshView(response, root);
                            }
                            e.printStackTrace();
                        }
                        Log.d("response", jsonObject[0]);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Some error has occured in apicall", Toast.LENGTH_LONG).show();
                Log.d("error ", "error occured :::::" + error);
            }
        });
        queue.add(jsonObjectRequest);

    }

    private void openInBrowser(JSONObject jsonObject) {
        String url = null;
        try {
            url = jsonObject.getString("statement");
            getfinalApi(url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getfinalApi(url)));
        getContext().startActivity(browserIntent);
    }

    private String getfinalApi(String api) {
        String finalApi;
        finalApi = urlConstants.URL_BASE + "/" + api;
        return  finalApi;

    }

    public void checkEmailFormat(View root) {
        String emailEntered = email.getText().toString();
        if (emailEntered.contains("@") && emailEntered.endsWith(".com")) {
            if (errorEmail.getVisibility() == View.VISIBLE) {
                errorEmail.setVisibility(View.INVISIBLE);
            }
            callApi(getContext(), "NEW", emailEntered, root);
            showProgressBar(root);

            //saveEmailInSharedPref(email.getText().toString());


        } else {
            errorEmail.setText("Please enter correct email ID");
            errorEmail.setVisibility(View.VISIBLE);
        }
    }


    //This API is called when user wants to track his earnings.
    private void callApi(final Context context, String type, final String key, final View root) {
        final RequestQueue queue;
        queue = MySingleton.getInstance(context).getRequestQueue();
        final String[] jsonObject = new String[1];

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                getApi(type, key),
                (JSONObject) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        jsonObject[0] = response.toString();
                        hideProgressBar(root);

                        if (response.toString().contains("OTP has been shared")) {
                            dialogueToReadOTP(key);
                            hideProgressBar(root);
                            Toast.makeText(getContext(), "OTP has been shared", Toast.LENGTH_LONG).show();
                            //getActivity().onBackPressed();
                        }

                        refreshView(response, root);

                        Log.d("response", jsonObject[0]);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Some error has occured", Toast.LENGTH_LONG).show();
                manageView(true);

                hideProgressBar(root);

                Log.d("error ", "error occured :::::" + error);
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void manageView(boolean visibility) {
        if (visibility) earningsLayout.setVisibility(View.GONE); else earningsLayout.setVisibility(View.VISIBLE);
        if (visibility) btn_layout.setVisibility(View.GONE); else btn_layout.setVisibility(View.VISIBLE);
        if (visibility) view_id.setVisibility(View.GONE); else view_id.setVisibility(View.VISIBLE);
        if (visibility) email.setVisibility(View.VISIBLE); else email.setVisibility(View.GONE);
        if (visibility) ok_btn.setVisibility(View.VISIBLE); else email.setVisibility(View.GONE);

    }

    private void refreshView(JSONObject response, View root) {

        try {
            JSONArray jsonArray = response.getJSONArray("NotesTrack");
            if (jsonArray.length() == 0) {
                showCustomDialogue("Not Found", "Sorry!! You don't have any notes uploaded with entered email address. Please go to 'UPLOAD Notes' module to upload notes ");
            } else {

                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String mapped_key = jsonObject.getString("Mapped_Key");
                Log.d("Mapped_Key", "Mapped_Key" + mapped_key);
                saveMappedKeyInSharedPref(mapped_key);
//Method to get populate the values that are available in jsonArray to tracker page.
                populateDetails(jsonArray);

                ok_btn.setVisibility(View.GONE);
                email.setVisibility(View.GONE);
                view_id.setVisibility(View.VISIBLE);
                earningsLayout.setVisibility(View.VISIBLE);
                btn_layout.setVisibility(View.VISIBLE);

            }

            JSONArray jsonArray2 = response.getJSONArray("Earnings");

            JSONObject jsonObject = jsonArray2.getJSONObject(0);
            totalRedemeed.setText(jsonObject.getString("totAmountRedeemed"));
            totalEarned.setText(jsonObject.getString("totAmountEarned"));

            // show_statement = jsonObject.getBoolean("show_statement");
            btn_redeem.setClickable(jsonObject.getBoolean("Show_Redeem"));
            btn_showStatements.setClickable(jsonObject.getBoolean("show_statement"));
            Redeem_api = jsonObject.getString("Redeem_api");
            statement_api = jsonObject.getString("statement_api");
            hideProgressBar(root);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        hideProgressBar(root);

    }


    private void sendOTPforVerification(String OTP, final String key) {
        final RequestQueue queue;
        queue = MySingleton.getInstance(getContext()).getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                urlConstants.URL_BAS + urlConstants.URL_TRACKER_OTP + "/" + OTP + "/" + key + "/" + getDeviceId(),
                (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //TODO : store Mapped_Key in the shared pref
                        if (response.toString().contains("404")) {
                            Log.d("tag", "404 ::::");
                        }
                        try {
                            JSONArray jsonArray = response.getJSONArray("NotesTrack");
                            if (jsonArray.length() == 0) {
                                showCustomDialogue("Not Found", "Sorry!! You don't have any notes uploaded with entered email address. Please go to 'UPLOAD Notes' module to upload notes.");
                            } else {

                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String mapped_key = jsonObject.getString("Mapped_Key");
                                Log.d("Mapped_Key", "Mapped_Key" + mapped_key);
                                saveMappedKeyInSharedPref(mapped_key);
//Method to populate the values that are available in jsonArray to tracker page.
                                populateDetails(jsonArray);

                                ok_btn.setVisibility(View.GONE);
                                email.setVisibility(View.GONE);
                                view_id.setVisibility(View.VISIBLE);
                                earningsLayout.setVisibility(View.VISIBLE);
                                btn_layout.setVisibility(View.VISIBLE);

                            }

                            JSONArray jsonArray2 = response.getJSONArray("Earnings");
                            JSONObject jsonObject = jsonArray2.getJSONObject(0);
                            totalRedemeed.setText(jsonObject.getString("totAmountRedeemed"));
                            totalEarned.setText(jsonObject.getString("totAmountEarned"));
                            earningsLayout.setVisibility(View.VISIBLE);
                            btn_layout.setVisibility(View.VISIBLE);

                            btn_redeem.setClickable(jsonObject.getBoolean("Show_Redeem"));
                            btn_showStatements.setClickable(jsonObject.getBoolean("show_statement"));

                            Redeem_api = jsonObject.getString("Redeem_api");
                            statement_api = jsonObject.getString("statement_api");
                            view_id.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        Log.d("OTPResponse", "OTP" + response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().contains("AuthFailureError")) {
                    dialogueForWrongOTP(key);
                }
                Toast.makeText(getContext(), "Error while submitting OTP", Toast.LENGTH_LONG).show();
                Log.d("error ", "error occured :::::" + error);
            }
        });
        queue.add(jsonObjectRequest);

    }

    private void populateDetails(JSONArray jsonArray) {
        myListDataList.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                TrackerListData myList = new TrackerListData();
                myList.setDescription(jsonObject.getString("Description"));
                myList.setHeader(jsonObject.getString("author"));
                myList.setId(jsonObject.getString("id"));
                myList.setNumberOfDownloads(jsonObject.getString("downloads"));
                myList.setPreviewImg(jsonObject.getString("file_snippet"));
                myListDataList.add(myList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TrackerListAdapter(getContext(), myListDataList);
        recyclerView.setAdapter(adapter);

    }

    private void dialogueToReadOTP(final String key) {
        final EditText otpEditText = new EditText(getContext());
        otpEditText.setTextSize(20);
        otpEditText.setGravity(View.TEXT_ALIGNMENT_CENTER);
        otpEditText.setGravity(1);
        otpEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Enter OTP")
                .setMessage("Please check yout mail inbox for OTP")
                .setView(otpEditText)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String OTP = String.valueOf(otpEditText.getText());
                        sendOTPforVerification(OTP, key);
                        Log.d("OTP", "OTP :" + OTP);
                    }
                })
                .create();
        dialog.show();
    }

    private String getApi(String type, String key) {
        String api;
        api = urlConstants.URL_BAS + urlConstants.URL_TRACKER + "/" + type + "/" + key + "/" + getDeviceId();

        return api;
    }

    private void saveMappedKeyInSharedPref(String toString) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("emailAddress", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("Mapped_Key", toString);
        myEdit.commit();
    }

    private String getStoredMapped_Key() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("emailAddress", 0);
        String email = sharedPreferences.getString("Mapped_Key", null);

        return email;

    }

    public void showProgressBar(View root){
        root.findViewById(R.id.loading_overlay_traker).setVisibility(View.VISIBLE);
        root.findViewById(R.id.loading_overlay_traker).sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
        root.findViewById(R.id.loading_overlay_traker).requestFocus();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        root.setClickable(false);
    }

    public void hideProgressBar(View root){
        root.findViewById(R.id.loading_overlay_traker).setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void checkIfEmailIsEmpty() {
        if (!email.getText().equals(null)) {
            ok_btn.setVisibility(View.VISIBLE);
        }
    }

    public String getDeviceId() {
        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences = getContext().getSharedPreferences("FirstSharedpref", MODE_APPEND);
        String deviceID = sharedPreferences.getString("DeviceID", null);
        //String version = sharedPreferences.getString("Version", " ");
        Log.d("fromSharedpred", deviceID);
        return deviceID;

    }

    private void dialogueForWrongOTP(final String key) {
        final EditText otpEditText = new EditText(getContext());
        otpEditText.setTextSize(20);
        otpEditText.setGravity(View.TEXT_ALIGNMENT_CENTER);
        otpEditText.setGravity(1);
        otpEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Enter OTP")
                .setMessage("You have entered wrong OTP, Please enter again!!!")
                .setView(otpEditText)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String OTP = String.valueOf(otpEditText.getText());
                        sendOTPforVerification(OTP, key);
                        Log.d("OTP", "OTP :" + OTP);
                    }
                })
                .create();
        dialog.show();
    }

    private void showCustomDialogue(String title, String desc) {
        int resId = R.drawable.notfound;
        if (title.equals("Success")){
            resId = R.drawable.iconsuccess;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(desc)
                .setCancelable(false)
                .setTitle(title)
                .setIcon(resId)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}