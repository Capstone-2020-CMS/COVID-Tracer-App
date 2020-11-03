package com.myBubble;

import android.os.Build;
import android.util.Log;

import com.myBubble.utils.TableData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

@Config(sdk = {Build.VERSION_CODES.O_MR1})
@RunWith(RobolectricTestRunner.class)
public class WebScrapingTest {

    @Test
    public void getDataFromWeb_isWorking() {
        Document doc = null;
        try {
            doc = Jsoup.connect("https://en.wikipedia.org/").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("JSOUP", doc.title());
        Elements newsHeadlines = doc.select("#mp-itn b a");
        for (Element headline : newsHeadlines) {
            Log.d("JSOUP",headline.attr("title") + headline.absUrl("href"));
        }
    }

    @Test
    public void getDataFromGovt_isWorking() {
        String url = "https://www.health.govt.nz/our-work/diseases-and-conditions/covid-19-novel-coronavirus/covid-19-current-situation/covid-19-current-cases";
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements newsHeadlines = doc.select(".table-style-two");

        Element table = newsHeadlines.get(0).children().get(2);
        Elements temp = table.children().get(0).children();

        TableData numConfirmedCases = new TableData(temp.get(0).text(), temp.get(1).text(), temp.get(2).text());
    }
}
