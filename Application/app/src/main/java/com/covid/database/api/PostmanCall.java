package com.covid.database.api;

import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostmanCall {
    public static void postValue() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"personalUserID\": \"randomnumber777\"\r\n}");
        Request request = new Request.Builder()
                .url("https://yirlg8c7kc.execute-api.ap-southeast-2.amazonaws.com/prod/data")
                .method("POST", body)
                .addHeader("Content-Type", "text/plain")
                .build();
        try {
            Response response = client.newCall(request).execute();
            Log.v("postReturn", response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
