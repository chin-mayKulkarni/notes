package com.notes.test.ui.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.notes.test.MainActivity;
import com.notes.test.R;
import com.notes.test.ui.fragmentHolder.FragmentHolder;
import com.notes.test.ui.fragmentHolder.WebViewActivity;
import com.notes.test.urlConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.os.ParcelFileDescriptor.MODE_APPEND;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutUsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutUsFragment extends Fragment {

    public TextView appName, version, dev_header, dev_desc;
    public CardView about_card, privacy_card, terms_card;
    public String about_url, privacy_url, terms_url;
    public WebView main_webview;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AboutUsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AboutUsFragment newInstance(String param1, String param2) {
        AboutUsFragment fragment = new AboutUsFragment();
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
        final View root = inflater.inflate(R.layout.fragment_about_us, container, false);
        appName = root.findViewById(R.id.app_name);
        version = root.findViewById(R.id.version_about);
        dev_header = root.findViewById(R.id.dev_text);
        dev_desc = root.findViewById(R.id.dev_desc_text);
        about_card = root.findViewById(R.id.about_card);
        privacy_card = root.findViewById(R.id.privacy_card);
        terms_card = root.findViewById(R.id.terms_card);

           if (isInternetConnected()){
               showProgressBar(root);
               getAboutApi(root);
           } else {
               showCustomDialogue();
           }




        about_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),
                        WebViewActivity.class);
                intent.putExtra("Header", "About Us");
                intent.putExtra("url", about_url);
                getActivity().startActivity(intent);
            }
        });

        privacy_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),
                        WebViewActivity.class);
                intent.putExtra("Header", "Privacy Policy");
                intent.putExtra("url", privacy_url);
                getActivity().startActivity(intent);
            }
        });

        terms_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),
                        WebViewActivity.class);
                intent.putExtra("Header", "Terms and Conditions");
                intent.putExtra("url", terms_url);
                getActivity().startActivity(intent);
            }
        });


        return root;
    }




    //To show loading spinner
    public void showProgressBar(View root){
        root.findViewById(R.id.loadingSpinner).setVisibility(View.VISIBLE);
        root.findViewById(R.id.loadingSpinner).sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
        root.findViewById(R.id.loadingSpinner).requestFocus();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        root.setClickable(false);
    }

    public void hideProgressBar(View root){
        root.findViewById(R.id.loadingSpinner).setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void getAboutApi(final View root) {


        RequestQueue queue;
        queue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                urlConstants.URL_BAS + "/" + "About" + "/" + getDeviceId(getContext()),
                (JSONObject) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Response", "Response from about" + response);
                        for (int i = 0; i< response.length(); i++){
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                appName.setText(jsonObject.getString("app_name"));
                                version.setText(jsonObject.getString("version"));
                                dev_header.setText(jsonObject.getString("header"));
                                dev_desc.setText(jsonObject.getString("description"));
                                about_url = jsonObject.getString("about_us_url");
                                terms_url = jsonObject.getString("terms_url");
                                privacy_url = jsonObject.getString("privacy_url");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        hideProgressBar(root);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", "Error is :" + error);
                Toast.makeText(getContext(), "Some error has occured, try later", Toast.LENGTH_LONG);

            }
        });
        queue.add(jsonArrayRequest);

    }

    public String getDeviceId(Context context) {
        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences = context.getSharedPreferences("FirstSharedpref", MODE_APPEND);
        String deviceID = sharedPreferences.getString("DeviceID", null);
        Log.d("fromSharedpred", deviceID);
        return deviceID;
    }


    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConnection != null && wifiConnection.isConnected()) || (mobileConnection != null && mobileConnection.isConnected())) {
            return true;
        } else return false;

    }


    private void showCustomDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        getActivity().onBackPressed();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}