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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.covid.MainActivity;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class CloudInfectedUsers extends MainActivity {
    private Context context;



    //Default constructor
    public CloudInfectedUsers(){
        this.context = context;

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////////////*/

//    public void sendIdToDB(View addUserToDB) {
//        // Do something in response to button click
//        Log.v("btnMsg", "IT WORKS!");
//    }
//
//    RequestQueue requestQueue = Volley.newRequestQueue(context);
//
//
//
//
//
//    public void getInfectedUsers() {
//
//        String url = "https://s6bimnllqb.execute-api.ap-southeast-2.amazonaws.com/prod/data";
//
//
//        JsonArrayRequest request;
//
//        {
//            Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
//                @Override
//                public void onResponse(JSONArray response) {
//                    Log.d("response", response.toString());
//
//                }
//            };
//
//            Response.ErrorListener errorListener = new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e("error", error.getMessage());
//                }
//            };
//
//            request = new JsonArrayRequest(Request.Method.GET, url, null, responseListener, errorListener);
//
//            requestQueue.add(request);
//        }
//    }

}
