package com.covid.database;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.covid.MainActivity;
import com.covid.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Callback;

public class CloudInfectedUsers extends MainActivity {
    private Context context;
    Button addInfectedUsers;



    //Default constructor
    public CloudInfectedUsers(){
        this.context = context;

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addInfectedUsers = findViewById(R.id.addUserToDB);

        addInfectedUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfectedUser(createRequest());
                Log.v("DbButton", "Successfully setOnClickListener");
            }
        });

    }*/

    /////////////////////////////////////////////////////////////////////////////////////////////////////*/

    RequestQueue requestQueue = Volley.newRequestQueue(context);





    public void getInfectedUsers() {

        String url = "https://s6bimnllqb.execute-api.ap-southeast-2.amazonaws.com/prod/data";


        JsonArrayRequest request;

        {
            Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d("response", response.toString());

                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", error.getMessage());
                }
            };

            request = new JsonArrayRequest(Request.Method.GET, url, null, responseListener, errorListener);

            requestQueue.add(request);
        }
    }

    public void setInfectedUsers() {


        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = "https://yirlg8c7kc.execute-api.ap-southeast-2.amazonaws.com/prod/data";
            final String requestBody = "{\"personalUserID\": \"john\"}";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


//    public void setInfectedUsers() {
//        saveInfectedUser(createRequest());
//    }




}
