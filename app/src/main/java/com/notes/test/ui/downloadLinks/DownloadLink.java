package com.notes.test.ui.downloadLinks;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.notes.test.R;
import com.notes.test.ui.MySingleton;
import com.notes.test.ui.RecyclerView.MyListAdapter;
import com.notes.test.ui.RecyclerView.MyListData;
import com.notes.test.urlConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.os.ParcelFileDescriptor.MODE_APPEND;

public class DownloadLink extends AppCompatActivity {

    RecyclerView recyclerView;
    List<MyListData> myListDataList;
    private MyListAdapter adapter;
    static WebView webView1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_link);

        webView1 = findViewById(R.id.webView1);
        webView1.setVisibility(View.GONE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        recyclerView = findViewById(R.id.recyclerView);
        myListDataList = new ArrayList<>();



        Bundle bundle = getIntent().getExtras();
        String Array = bundle.getString("JsonResponse");
        String selectMethod = bundle.getString("fragmentName");


        try {
            JSONArray jsonArray = new JSONArray(Array);
//extractData method will extract data from json and populate List
            if (selectMethod.equals("Notes"))
                extractDataForNotes(jsonArray);
            else if (selectMethod.equals("QP")){
                extractDataforQP(jsonArray);
            } else extractDataforSyllabus(jsonArray);
            //TODO : Implement recycler view using https://www.javatpoint.com/android-recyclerview-list-example


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


//This method will extract data from json and populate List
    private void extractDataForNotes(JSONArray jsonArray) {
        for (int i = 0 ; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                MyListData myList = new MyListData();
                myList.setDescription(jsonObject.getString("Description"));
                myList.setHeader(jsonObject.getString("author"));
                myList.setDownloadableLink(jsonObject.getString("file"));
                myList.setId(jsonObject.getString("id"));
                myList.setType(jsonObject.getString("type"));
                myList.setNumberOfDownloads(jsonObject.getString("downloads"));
                myList.setPreviewImg(jsonObject.getString("file_snippet"));
                myListDataList.add(myList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new MyListAdapter(this,myListDataList);
        recyclerView.setAdapter(adapter);
    }

    private void extractDataforQP(JSONArray jsonArray) {
        for (int i = 0 ; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                MyListData myList = new MyListData();
                myList.setDescription(jsonObject.getString("Description"));
                myList.setHeader(jsonObject.getString("owner"));
                myList.setId(jsonObject.getString("id"));
                myList.setType(jsonObject.getString("type"));
                myList.setDownloadableLink(jsonObject.getString("file"));
                myList.setNumberOfDownloads(jsonObject.getString("downloads"));
                myList.setPreviewImg(jsonObject.getString("file_snippet"));
                myListDataList.add(myList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new MyListAdapter(this,myListDataList);
        recyclerView.setAdapter(adapter);
    }

    private void extractDataforSyllabus(JSONArray jsonArray) {
        for (int i = 0 ; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                MyListData myList = new MyListData();
                myList.setDescription(jsonObject.getString("id"));
                myList.setHeader(jsonObject.getString("updated_on"));
                myList.setId(jsonObject.getString("id"));
                myList.setType(jsonObject.getString("type"));
                myList.setDownloadableLink(jsonObject.getString("file"));
                myList.setNumberOfDownloads(jsonObject.getString("downloads"));
                myList.setPreviewImg(jsonObject.getString("file"));
                myListDataList.add(myList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new MyListAdapter(this,myListDataList);
        recyclerView.setAdapter(adapter);
    }


// This method will download the pdf file
    public static void downloadfile(String url, String title, Context context) {
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));
        String tempTitle=title.replace("","");
        request.setTitle(tempTitle);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,tempTitle+".pdf");
        DownloadManager downloadManager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        request.setMimeType("application/pdf");
        request.allowScanningByMediaScanner();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        downloadManager.enqueue(request);
    }


// This method will open pdf in external browser
    public static void openInBrowser(String url, String title, Context context, String id, String type){
        incrementDownloadsApi(context, id, type);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

    public static void incrementDownloadsApi(final Context context, String id, String type) {
        final RequestQueue queue;
        queue = MySingleton.getInstance(context).getRequestQueue();
        final String[] jsonObject = new String[1];

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                /*"https://vtu.pythonanywhere.com/apiv1/TrackDownloads/Notes/4/ladsbjfevgmfmttc"*/getDownloadCountApi(id, context, type),
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

    private static String getDownloadCountApi(String id, Context context, String type) {
        String url = urlConstants.URL_TRACK_DOWNLOADS + "/" + type + "/" + id + "/" + getDeviceId(context);
        return url;
    }

    private static String getDeviceId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FirstSharedpref", MODE_APPEND);
        String deviceID = sharedPreferences.getString("DeviceID", null);
        String version = sharedPreferences.getString("Version", " ");
        Log.d("fromSharedpred", deviceID);
        return deviceID;
    }


}