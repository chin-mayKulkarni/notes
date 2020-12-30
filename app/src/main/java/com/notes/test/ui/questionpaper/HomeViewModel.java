package com.notes.test.ui.home;

import android.content.Context;
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
import com.notes.test.ui.MySingleton;
import com.notes.test.ui.notes.GalleryFragment;
import com.notes.test.urlConstants;

import org.json.JSONArray;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Dummy fragment, Go to Notes and click on GO Button");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void getJsonResponse(final Context context, String sem, String branch, String sub) {
        final RequestQueue queue;
        queue = MySingleton.getInstance(context).getRequestQueue();
        final String[] jsonArray = new String[1];

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                /*urlConstants.URL_TEST */getApi(sem, branch, sub),
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

    private String getApi(String sem, String branch, String sub) {
        String api = urlConstants.URL_TEST;

        api = api + "/" + stringNoSpace(sem) + "/" + stringNoSpace(branch) + "/" + stringNoSpace(sub);
        Log.d("api", "Api Value is:" + api);
        return api;
    }

    public String stringNoSpace(String str){
        String stringWithoutSpaces = str.replaceAll("\\s+", "%20");
        return stringWithoutSpaces;
    }


}