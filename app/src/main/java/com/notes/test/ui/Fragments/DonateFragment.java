package com.notes.test.ui.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.notes.test.R;
import com.notes.test.ui.MySingleton;
import com.notes.test.urlConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.os.ParcelFileDescriptor.MODE_APPEND;
import static com.android.volley.VolleyLog.TAG;

public class DonateFragment extends Fragment {

    public EditText name, email, phoneNumber, donate_content;
    public Button submit;
    public TextView errortext;
    public String tncDescription, tncTitle;
    public boolean agreed;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_scrolling, container, false);
        name = root.findViewById(R.id.name_donate);
        email = root.findViewById(R.id.email_donate);
        phoneNumber = root.findViewById(R.id.phno_donate);
        donate_content = root.findViewById(R.id.content_donate);
        submit = root.findViewById(R.id.btn_donate_go);
        errortext = root.findViewById(R.id.errorTextDonate);
        getStringForTnC("terms", "title");
        /*tncDescription = getString(R.string.large_text);
        tncTitle = "Terms and Conditions";*/


        name.addTextChangedListener(mTextWatcher);
        donate_content.addTextChangedListener(mTextWatcher);
        phoneNumber.addTextChangedListener(mTextWatcher);
        email.addTextChangedListener(mTextWatcher);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
                if (errortext.getVisibility() != View.VISIBLE){
                    callOTPService();
                    //dialogueToReadOTP();

                }
            }
        });

        return root;

    }

    private void callOTPService() {

        // jsonObject = createJson();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("name", name.getText().toString());
        postParam.put("email", email.getText().toString());
        postParam.put("contact", phoneNumber.getText().toString());
        postParam.put("device_id", getDeviceId(getContext()));
        postParam.put("user_message", donate_content.getText().toString());


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlConstants.URL_CONTACT_US, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("contactusresponse", response.toString());
                       // dialogueToReadOTP();
                        if (response.toString().contains("no need of otp validation")){
                            clearAllfields();
                            dialogueSuccess();
                           // Toast.makeText(getContext(),"Your Information has been submitted successfully",Toast.LENGTH_LONG).show();
                           // getActivity().onBackPressed();
                        }
                        if (response.toString().contains("OTP has been shared")){
                            //clearAllfields();
                            dialogueToReadOTP();
                            Toast.makeText(getContext(),"OTP has been shared",Toast.LENGTH_LONG).show();
                            //getActivity().onBackPressed();
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

    private void clearAllfields() {
        name.getText().clear();
        email.getText().clear();
        phoneNumber.getText().clear();
        donate_content.getText().clear();
        submit.setEnabled(false);
    }

    private void dialogueToReadOTP() {
        final EditText otpEditText = new EditText(getContext());
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Enter OTP")
                .setMessage("Please check yout mail inbox for OTP")
                .setView(otpEditText)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String OTP = String.valueOf(otpEditText.getText());
                        sendOTPforVerification(OTP);
                        Log.d("OTP", "OTP :" + OTP);
                    }
                })
                .create();
        dialog.show();
    }
    private void dialogueForWrongOTP() {
        final EditText otpEditText = new EditText(getContext());
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Enter OTP")
                .setMessage("You have entered wrong OTP, Please enter again!!!")
                .setView(otpEditText)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String OTP = String.valueOf(otpEditText.getText());
                        sendOTPforVerification(OTP);
                        Log.d("OTP", "OTP :" + OTP);
                    }
                })
                .create();
        dialog.show();
    }
    private void dialogueSuccess() {
        //final EditText otpEditText = new EditText(getContext());
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Success")
                .setMessage("Thank you for reaching out to us, we'll contact you soon!!")
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

    private void sendOTPforVerification(String otp) {
        final RequestQueue queue;
        queue = MySingleton.getInstance(getContext()).getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                urlConstants.URL_VALIDATE_OTP + "/" + otp + "/" + getDeviceId(getContext()),
                (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //TODO : if we get positive response show happy dialogue.
                        if (response.toString().contains("O.K")){
                            dialogueSuccess();
                        }

                        Log.d("OTPResponse", "OTP" + response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().contains("AuthFailureError")){
                    dialogueForWrongOTP();
                }
                Toast.makeText(getContext(),"Error while submitting OTP",Toast.LENGTH_LONG).show();
                Log.d("error ", "error occured :::::" + error);
            }
        });
        queue.add(jsonObjectRequest);

    }

    private void validateInput() {
        if (!agreed) {
            errortext.setVisibility(View.VISIBLE);
            errortext.setText("Please accept Terms and Conditions");
            getStringForTnC("terms", "title");
        } else if ((!email.getText().toString().contains("@")) || !email.getText().toString().endsWith(".com")) {
            errortext.setVisibility(View.VISIBLE);
            errortext.setText("Please enter valid emailID");
        } else if (phoneNumber.getText().toString().length() != 10) {
            errortext.setVisibility(View.VISIBLE);
            errortext.setText("Please enter valid Phone number");
        } else errortext.setVisibility(View.GONE);
    }

    void checkFieldsForEmptyValues(){
        String nameStr = name.getText().toString();
        String emailStr = email.getText().toString();

        if(nameStr.equals("")|| emailStr.equals("") /*|| (phoneNumber.getText().toString().length() != 10)*/ ){
            submit.setEnabled(false);
        } else {
            submit.setEnabled(true);
            submit.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void createDialogue() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        agreed = sharedPreferences.getBoolean("agreed",false);
        if (!agreed) {
            new AlertDialog.Builder(getContext())
                    .setTitle(tncTitle)
                    .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("agreed", true);
                            editor.apply();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().onBackPressed();
                        }
                    })
                    .setMessage(tncDescription)
                    .show();
        }
    }

    private void getStringForTnC(final String desc, final String title) {
        final RequestQueue queue;
        queue = MySingleton.getInstance(getContext()).getRequestQueue();
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET,
                getTnCApi(),
                (JSONArray) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0 ; i < response.length(); i++){
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                tncDescription = jsonObject.getString(desc);
                                tncTitle = jsonObject.getString(title);
                                Log.d("tncDescription", tncDescription);
                                Log.d("tncTitle", tncTitle);
                                createDialogue();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Error while retrieving T&C",Toast.LENGTH_LONG).show();
                Log.d("error ", "error occured :::::" + error);
            }
        });
        queue.add(jsonObjectRequest);
    }

    private String getTnCApi() {
        Log.d("tncApi", "tncApi"+ urlConstants.URL_TERMS_AND_CONDITIONS + "/" + getDeviceId(getContext()));
        return urlConstants.URL_TERMS_AND_CONDITIONS + "/" + getDeviceId(getContext());
    }

    private static String getDeviceId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FirstSharedpref", MODE_APPEND);
        String deviceID = sharedPreferences.getString("DeviceID", null);
        String version = sharedPreferences.getString("Version", " ");
        Log.d("fromSharedpred", deviceID);
        return deviceID;
    }
}