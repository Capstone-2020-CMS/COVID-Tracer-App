package com.myBubble.database.cloud;

import android.content.Context;
import android.util.Log;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.myBubble.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class VolleyPOST {

    public interface SetInfectedCallback {
        void onSuccessResponse();
        void onFailureResponse();
    }

    public static void setInfectedUsers(Context context, SetInfectedCallback setInfectedCallback) {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String dbValue = MainActivity.myDB.getPersonalInfoData();
            String URL = "https://yirlg8c7kc.execute-api.ap-southeast-2.amazonaws.com/prod/data";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("InfectedUserID", dbValue);
            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    setInfectedCallback.onSuccessResponse();
                    Log.i("LOG_VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setInfectedCallback.onFailureResponse();
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {

                        responseString = String.valueOf(response.statusCode);

                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
