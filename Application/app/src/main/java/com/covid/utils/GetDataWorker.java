package com.covid.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;

import static com.covid.MainActivity.dateUpdated;
import static com.covid.MainActivity.tableDataArrayList;

public class GetDataWorker extends Worker {

    public GetDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
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

        dateUpdated = newsHeadlines.get(0).children().get(0).text();

        Element table = newsHeadlines.get(0).children().get(2);

        Elements temp = table.children().get(0).children();
        TableData numConfirmedCases = new TableData(temp.get(0).text(), temp.get(1).text(), temp.get(2).text());

        temp = table.children().get(4).children();
        TableData numDeaths = new TableData(temp.get(0).text(), temp.get(1).text(), temp.get(2).text());

        temp = table.children().get(3).children();
        TableData numRecovered = new TableData(temp.get(0).text(), temp.get(1).text(), temp.get(2).text());

        tableDataArrayList.clear();
        tableDataArrayList.addAll(Arrays.asList(numConfirmedCases, numDeaths, numRecovered));

        return Result.success();
    }
}
