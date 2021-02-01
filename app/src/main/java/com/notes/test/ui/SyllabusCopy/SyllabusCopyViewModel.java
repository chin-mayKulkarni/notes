package com.notes.test.ui.SyllabusCopy;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.notes.test.MainActivity;
import com.notes.test.ui.MySingleton;
import com.notes.test.ui.RecyclerView.MyListData;
import com.notes.test.ui.notes.GalleryFragment;
import com.notes.test.urlConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.os.ParcelFileDescriptor.MODE_APPEND;

public class SyllabusCopyViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    String urlSyllabusCopy, titleSyllabusCopy;

    public SyllabusCopyViewModel() {
        mText = new MutableLiveData<>();
       // mText.setValue("Syllabus copy feature comming soon....!");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public String getDatFromSharedpref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FirstSharedpref", MODE_APPEND);
        String deviceID = sharedPreferences.getString("DeviceID", null);
        String version = sharedPreferences.getString("Version", " ");
        Log.d("fromSharedpred", deviceID);
        return deviceID;
    }

    public void getJsonResponse(final Context context, String branchSel) {

        final RequestQueue queue;
        queue = MySingleton.getInstance(context).getRequestQueue();
        final String[] jsonArray = new String[1];

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                /*urlConstants.URL_TEST */getApi(branchSel,context),
                (JSONArray) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        getDownloadableUrl(response);
                        openInBrowser(getMediaUrl(urlSyllabusCopy), titleSyllabusCopy, context);
                    }

// Method will append base url to media url
                    private String getMediaUrl(String urlSyllabusCopy) {
                        urlSyllabusCopy = urlConstants.URL_BASE + "/" + urlSyllabusCopy;
                        return  urlSyllabusCopy;
                    }

//Method will get url and title from json response to download syllabusCopy file
                    private void getDownloadableUrl(JSONArray response) {
                        for (int i= 0 ;i< response.length(); i++){
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                urlSyllabusCopy= jsonObject.getString("file");
                                titleSyllabusCopy = jsonObject.getString("title");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SyllabusCopyFragment.showDialogue("Not Found!!", "Syllabus copy is not available for selected branch", "Ok");
                //Toast.makeText(context,"Syllabus copy is not available for selected branch",Toast.LENGTH_LONG).show();
                Log.d("error ", "error occured :::::" + error);
            }
        });
        queue.add(jsonArrayRequest);
    }

    private String getApi(String branchSel, Context context) {
        String api = urlConstants.URL_BAS + urlConstants.URL_SYLLABUS;

        api = api + "/" + stringNoSpace(branchSel);
        api = appendDeviceId(api, context);
        Log.d("api", "Api Value is:" + api);
        return api;
    }

    private String appendDeviceId(String url, Context context) {
        url = url + "/" + getDatFromSharedpref(context);
        return url;
    }

    private String stringNoSpace(String branchSel) {
        String stringWithoutSpaces = branchSel.replaceAll("\\s+", "%20");
        return stringWithoutSpaces;
    }


    // This method will open pdf in external browser
    public static void openInBrowser(String url, String title, Context context){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);

    }


}