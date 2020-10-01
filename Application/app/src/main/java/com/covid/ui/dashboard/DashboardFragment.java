package com.covid.ui.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.work.WorkInfo;

import com.covid.R;
import com.google.android.material.card.MaterialCardView;

import java.util.Objects;

import static com.covid.MainActivity.dateUpdated;
import static com.covid.MainActivity.getDataWorkRequest;
import static com.covid.MainActivity.tableDataArrayList;
import static com.covid.MainActivity.workManager;

public class DashboardFragment extends Fragment {

    private TextView txtConfirmed;
    private TextView txtDeaths;
    private TextView txtRecovered;
    private TextView txtDateUpdated;
    private ImageView imgRefresh;
    private MaterialCardView cardButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        txtConfirmed = root.findViewById(R.id.txtConfirmed);
        txtDeaths = root.findViewById(R.id.txtDeaths);
        txtRecovered = root.findViewById(R.id.txtRecovered);
        txtDateUpdated = root.findViewById(R.id.txtDateUpdated);
        imgRefresh = root.findViewById(R.id.imgRefresh);
        cardButton = root.findViewById(R.id.cardButton);

        updateTableData();

        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Observer<WorkInfo> workInfoObserver = new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                            updateTableData();
                        } else if (workInfo.getState() == WorkInfo.State.FAILED) {
                            Toast.makeText(getContext(), "Failed to retrieve data, please make sure your internet is working and try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                workManager.pruneWork();
                workManager.enqueue(getDataWorkRequest);
                LiveData<WorkInfo> status = workManager.getWorkInfoByIdLiveData(getDataWorkRequest.getId());
                status.observe(getViewLifecycleOwner(), workInfoObserver);
            }
        });

        cardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.health.govt.nz/our-work/diseases-and-conditions/covid-19-novel-coronavirus";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                requireContext().startActivity(browserIntent);
            }
        });

        return root;
    }

    private void updateTableData() {
        if (tableDataArrayList.size() == 3) {
            txtConfirmed.setText(tableDataArrayList.get(0).getTotal());
            txtDeaths.setText(tableDataArrayList.get(1).getTotal());
            txtRecovered.setText(tableDataArrayList.get(2).getTotal());
            txtDateUpdated.setText(dateUpdated);
        }
    }
}