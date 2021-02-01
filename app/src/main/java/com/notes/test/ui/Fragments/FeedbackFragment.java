package com.notes.test.ui.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.notes.test.R;
import com.notes.test.urlConstants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.os.ParcelFileDescriptor.MODE_APPEND;
import static com.android.volley.VolleyLog.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public EditText name, email, phoneNumber, feedback_content;
    public Button submit;
    public TextView errortext;
    public JSONObject jsonObject;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedbackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedbackFragment newInstance(String param1, String param2) {
        FeedbackFragment fragment = new FeedbackFragment();
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

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // check Fields For Empty Values
            checkFieldsForEmptyValues();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_feedback, container, false);
        name = root.findViewById(R.id.name_feedback);
        email = root.findViewById(R.id.email_feedback);
        phoneNumber = root.findViewById(R.id.phno_feedback);
        feedback_content = root.findViewById(R.id.content_feedback);
        submit = root.findViewById(R.id.btn_syllabus_go);
        errortext = root.findViewById(R.id.errorText);

        name.addTextChangedListener(mTextWatcher);
        feedback_content.addTextChangedListener(mTextWatcher);
        phoneNumber.addTextChangedListener(mTextWatcher);
        email.addTextChangedListener(mTextWatcher);

        checkFieldsForEmptyValues();



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//TODO : call api and send data to back end
                validateInput();
                showProgressBar(root);
                //clearAllfields();
                if (errortext.getVisibility() != View.VISIBLE){
                   // jsonObject = createJson();

                    RequestQueue queue = Volley.newRequestQueue(getContext());

                    Map<String, String> postParam= new HashMap<String, String>();
                    postParam.put("device_id", getDatFromSharedpref(getContext()));
                    postParam.put("name", name.getText().toString());
                    postParam.put("email", email.getText().toString());
                    postParam.put("contact_number", phoneNumber.getText().toString());
                    postParam.put("feed_back", feedback_content.getText().toString());

                        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                urlConstants.URL_BAS + urlConstants.URL_FEEDBACK, new JSONObject(postParam),
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("feedbackresponse", response.toString());
                                       if (response.toString().contains("O.K")){
                                           //clearAllfields();
                                           dialogueSuccess(root);
                                           Toast.makeText(getContext(),"Your feedback has been submitted successfully",Toast.LENGTH_LONG).show();
                                       }

                                    }
                                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.d(TAG, "Error: " + error.getMessage());
                            }
                        }) {

                            /**
                             * Passing some request headers
                             * */
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                return headers;
                            }

                        };

                        jsonObjReq.setTag(TAG);
                        // Adding request to request queue
                        queue.add(jsonObjReq);

                }
            }


        });




        return root;

    }

    private void dialogueSuccess(View root) {
        hideProgressBar(root);
        //final EditText otpEditText = new EditText(getContext());
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Success")
                .setMessage("Thanks for submitting your valuable feedback!!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().onBackPressed();
                        Log.d("Dialogue", "Success Dialogue :");
                    }
                })
                .create();
        dialog.show();
    }

    private void clearAllfields() {
        name.getText().clear();
        email.getText().clear();
        phoneNumber.getText().clear();
        feedback_content.getText().clear();
        submit.setEnabled(false);
    }

/*    private JSONObject createJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", getDatFromSharedpref(getContext()));
            jsonObject.put("name", name.getText().toString());
            jsonObject.put("email", email.getText().toString());
            jsonObject.put("contact_number", phoneNumber.getText().toString());
            jsonObject.put("feed_back", feedback_content.getText().toString());
            Log.d("json", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }*/

    private void validateInput() {
        if ((!email.getText().toString().contains("@")) || !email.getText().toString().endsWith(".com") ){
            errortext.setVisibility(View.VISIBLE);
            errortext.setText("Please enter valid emailID");
        }

        else if (phoneNumber.getText().toString().length() != 10){
            errortext.setVisibility(View.VISIBLE);
            errortext.setText("Please enter valid Phone number");
        } else errortext.setVisibility(View.GONE);

    }

    void checkFieldsForEmptyValues(){
        String nameStr = name.getText().toString();
        String feedbackStr = feedback_content.getText().toString();

        if(nameStr.equals("")|| feedbackStr.equals("") /*|| (phoneNumber.getText().toString().length() != 10)*/ ){
            submit.setEnabled(false);
        } else {
            submit.setEnabled(true);
            submit.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public String getDatFromSharedpref(Context context) {
        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences = context.getSharedPreferences("FirstSharedpref", MODE_APPEND);
        String deviceID = sharedPreferences.getString("DeviceID", null);
        //String version = sharedPreferences.getString("Version", " ");
        Log.d("fromSharedpred", deviceID);
        return deviceID;

    }

    public void showProgressBar(View root){
        root.findViewById(R.id.loading_overlay_feedback).setVisibility(View.VISIBLE);
        root.findViewById(R.id.loading_overlay_feedback).sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
        root.findViewById(R.id.loading_overlay_feedback).requestFocus();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        root.setClickable(false);
    }

    public void hideProgressBar(View root){
        root.findViewById(R.id.loading_overlay_feedback).setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }
}