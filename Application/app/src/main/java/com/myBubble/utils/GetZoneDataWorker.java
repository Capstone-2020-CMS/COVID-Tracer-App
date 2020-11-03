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

import static com.myBubble.MainActivity.zoneCovidDataArray;

public class GetZoneDataWorker extends Worker {

    public GetZoneDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
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

        Element table = newsHeadlines.get(2).children().get(2);

        int i = 1;
        for (Element element : table.children()) {
            i++;
            Elements row = element.children();
            ZoneCovidData temp = new ZoneCovidData(row.get(0).text(), row.get(1).text(), row.get(2).text(), row.get(3).text(), row.get(4).text(), row.get(5).text());
            zoneCovidDataArray.add(temp);
        }

        return Result.success();
    }
}
