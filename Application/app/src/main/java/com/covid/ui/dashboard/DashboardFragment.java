package com.covid.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.covid.MainActivity;
import com.covid.R;
import com.covid.utils.TableData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executor;

import static com.covid.MainActivity.getDataWorkRequest;
import static com.covid.MainActivity.tableDataArrayList;
import static com.covid.MainActivity.workManager;

public class DashboardFragment extends Fragment {

    private TextView txtConfirmed;
    private TextView txtDeaths;
    private TextView txtRecovered;
    private ImageView imgRefresh;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        txtConfirmed = root.findViewById(R.id.txtConfirmed);
        txtDeaths = root.findViewById(R.id.txtDeaths);
        txtRecovered = root.findViewById(R.id.txtRecovered);
        imgRefresh = root.findViewById(R.id.imgRefresh);

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

        return root;
    }

    private void updateTableData() {
        if (tableDataArrayList.size() == 3) {
            txtConfirmed.setText(tableDataArrayList.get(0).getTotal());
            txtDeaths.setText(tableDataArrayList.get(1).getTotal());
            txtRecovered.setText(tableDataArrayList.get(2).getTotal());
        }
    }
}