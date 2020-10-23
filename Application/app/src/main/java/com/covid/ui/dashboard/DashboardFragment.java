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
import com.covid.database.cloud.VolleyGET;
import com.google.android.material.card.MaterialCardView;

import static com.covid.MainActivity.dateUpdated;
import static com.covid.MainActivity.getSummaryDataWorkRequest;
import static com.covid.MainActivity.myDB;
import static com.covid.MainActivity.tableDataArrayList;
import static com.covid.MainActivity.workManager;

public class DashboardFragment extends Fragment {

    private TextView txtConfirmed;
    private TextView txtDeaths;
    private TextView txtRecovered;
    private TextView txtDateUpdated;
    private ImageView imgRefresh;
    private TextView txtLinkToResources;
    private TextView txtNumOfBubbleActiveCases;

    private MaterialCardView cardInfectedDBSize;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        txtConfirmed = root.findViewById(R.id.txtConfirmed);
        txtDeaths = root.findViewById(R.id.txtDeaths);
        txtRecovered = root.findViewById(R.id.txtRecovered);
        txtDateUpdated = root.findViewById(R.id.txtDateUpdated);
        imgRefresh = root.findViewById(R.id.imgRefresh);
        txtLinkToResources = root.findViewById(R.id.txtLinkToResources);
        txtNumOfBubbleActiveCases = root.findViewById(R.id.txtNumOfBubbleActiveCases);
        cardInfectedDBSize = root.findViewById(R.id.cardInfectedDBSize);

        updateNumber();
        updateTableData();

        cardInfectedDBSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VolleyGET.checkExposure(requireContext());
            }
        });

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
                workManager.enqueue(getSummaryDataWorkRequest);
                LiveData<WorkInfo> status = workManager.getWorkInfoByIdLiveData(getSummaryDataWorkRequest.getId());
                status.observe(getViewLifecycleOwner(), workInfoObserver);
            }
        });

        txtLinkToResources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.health.govt.nz/our-work/diseases-and-conditions/covid-19-novel-coronavirus";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                requireContext().startActivity(browserIntent);
            }
        });

        return root;
    }

    public void updateNumber(){
        if(myDB.getNumOfInfectedEncounters() > -1){
            int size = myDB.getNumOfInfectedEncounters();
            String newString = String.valueOf(size);
            txtNumOfBubbleActiveCases.setText(newString);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTableData();
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