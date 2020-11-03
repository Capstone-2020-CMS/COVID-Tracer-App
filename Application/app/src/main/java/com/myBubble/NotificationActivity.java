package com.myBubble;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Bundle extras = getIntent().getExtras();

        String id = "myBubble ID: " + extras.getString("id");
        String dateEncountered = extras.getString("dateEncountered");
        String dateReported = extras.getString("dateReported");

        TextView bubbleText = findViewById(R.id.txtNoteMyBubbleID);
        TextView dateEncounteredText = findViewById(R.id.txtDateEncountered);
        TextView dateReportedText = findViewById(R.id.txtDateReported);

        bubbleText.setText(id);
        dateEncounteredText.setText(dateEncountered);
        dateReportedText.setText(dateReported);

        Button btnClose = findViewById(R.id.btnCloseNote);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView txtNoteMoreInfo = findViewById(R.id.txtNoteMoreInfo);
        txtNoteMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.health.govt.nz/our-work/diseases-and-conditions/covid-19-novel-coronavirus/covid-19-health-advice-public/assessment-and-testing-covid-19";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(browserIntent);
            }
        });
    }
}