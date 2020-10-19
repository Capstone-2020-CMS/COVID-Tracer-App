package com.covid.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.covid.database.cloud.VolleyGET.checkExposure;
import static com.covid.database.cloud.VolleyGET.getInfectedUsers;

public class DBUpdateWorker extends Worker {


    public DBUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        // Does this even do anything?
        Context context = super.getApplicationContext();

        checkExposure(context);

        return Result.success();

        // return null;
    }
}
