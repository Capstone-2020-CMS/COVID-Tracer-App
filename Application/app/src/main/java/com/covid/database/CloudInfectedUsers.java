package com.covid.database;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.covid.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class CloudInfectedUsers {
    private Context context;


    //Default constructor
    public CloudInfectedUsers(Context context){
        this.context = context;
    }

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
}
