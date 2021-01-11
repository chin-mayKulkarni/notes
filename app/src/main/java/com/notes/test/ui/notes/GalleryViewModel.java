package com.notes.test.ui.notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.notes.test.MainActivity;
import com.notes.test.ui.MySingleton;
import com.notes.test.urlConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.os.ParcelFileDescriptor.MODE_APPEND;

public class GalleryViewModel extends ViewModel {

    private MutableLiveData<String> mText;
/*    private JSONArray JSONArray;
    ArrayList<String> jsonSem = new ArrayList<String>();
    ArrayList<String> jsonBranch = new ArrayList<String>();*/

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }


    public void getJsonResponse(final Context context, String sem, String branch, String sub) {
        final RequestQueue queue;
        queue = MySingleton.getInstance(context).getRequestQueue();
        final String[] jsonArray = new String[1];

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                getApi(sem, branch, sub, context),
                (JSONArray) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        jsonArray[0] = response.toString();
                        GalleryFragment.openDownloadlinkActivity(jsonArray[0], context);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Notes are not available for selected Subject",Toast.LENGTH_LONG).show();
                Log.d("error ", "error occured :::::" + error);
            }
        });
        queue.add(jsonArrayRequest);
    }

    private String getApi(String sem, String branch, String sub, Context context) {
        String api = urlConstants.URL_TEST;

        api = api + "/" + stringNoSpace(sem) + "/" + stringNoSpace(branch) + "/" + stringNoSpace(sub);
        api = appendDeviceId(api, context);
        Log.d("api", "Api Value is:" + api);
        return api;
    }

    public String appendDeviceId(String url, Context context){
        url = url + "/" + getDatFromSharedpref(context);
        return url;
    }



    public String stringNoSpace(String str){
        String stringWithoutSpaces = str.replaceAll("\\s+", "%20");
        return stringWithoutSpaces;
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
}