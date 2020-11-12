package com.myBubble.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import static com.myBubble.MainActivity.dateUpdated;
import static com.myBubble.MainActivity.tableDataArrayList;

public class GetSummaryDataWorker extends Worker {

    public GetSummaryDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String url = "https://www.health.govt.nz/our-work/diseases-and-conditions/covid-19-novel-coronavirus/covid-19-current-situation/covid-19-current-cases";
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure();
        }
        Elements newsHeadlines = doc.select(".table-style-two");

        dateUpdated = getCurrentDate();

        Elements table = newsHeadlines.get(1).children().get(2).children();
        Elements active = table.get(0).children();
        Elements dead = table.get(2).children();
        Elements recovered = table.get(1).children();

        TableData numActive = new TableData(active.get(0).text(), active.get(2).text(), active.get(1).text());
        TableData numDeaths = new TableData(dead.get(0).text(), dead.get(2).text(), dead.get(1).text());
        TableData numRecovered = new TableData(recovered.get(0).text(), recovered.get(2).text(), recovered.get(1).text());

        tableDataArrayList.clear();
        tableDataArrayList.addAll(Arrays.asList(numActive, numDeaths, numRecovered));

        return Result.success();
    }

    private String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(cal.getTime());
    }
}
