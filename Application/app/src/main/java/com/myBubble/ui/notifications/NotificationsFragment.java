package com.myBubble.ui.notifications;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.myBubble.NotificationActivity;
import com.myBubble.R;
import com.myBubble.database.cloud.VolleyDELETE;
import com.myBubble.database.cloud.VolleyPOST;
import com.myBubble.utils.InfectedUserData;

import java.util.ArrayList;

import static com.myBubble.MainActivity.activeExpo;
import static com.myBubble.MainActivity.myDB;
import static com.myBubble.MainActivity.myID;


public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    //Views
    ImageView imgStatusBackground;
    TextView txtStatus;
    Button btnExposure;
    LinearLayout linearLayoutInfected;

    // Colour vars
    private static int motorwayGreen;
    private static int red;

    private VolleyPOST.SetInfectedCallback infectedCallback;
    private VolleyDELETE.deleteInfectedUserCallback deleteInfectedUserCallback;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);

        View root = inflater.inflate(R.layout.fragment_notifications_v2, container, false);

        // Initialise colours
        motorwayGreen = getResources().getColor(R.color.motorwayGreen);
        red = getResources().getColor(R.color.red);

        // Initialise Views
        imgStatusBackground = root.findViewById(R.id.imgStatusBackground);
        txtStatus = root.findViewById(R.id.txtStatus);
        btnExposure = root.findViewById(R.id.btnExposure);

        // Get linear Layout
        linearLayoutInfected = root.findViewById(R.id.linearLayoutInfected);

        // Create callback
        createCallbacks();

        // Set the initial card colours
        deployCardColours();

        // Create onClick methods for the buttons
        btnExposure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formStuff();
            }
        });

        Thread setupTableThread = new Thread() {
            @Override
            public void run() {
                // Get a list of infected data
                SQLiteDatabase db = myDB.getWritableDatabase();
                ArrayList<InfectedUserData> listOfInfectedUsers = new ArrayList<>();

                String query = "SELECT * FROM INFECTED_ENCOUNTERS_TABLE ORDER BY date(DATE_REPORTED)";
                try (Cursor cursor = db.rawQuery(query, null)) {
                    while (cursor.moveToNext()) {
                        String id = cursor.getString(0);
                        String dateReported = cursor.getString(1);

                        // Add data to list if ID is in encounter table
                        if (!myDB.getEncounterData(id).equals("Data Not Found")) {

                            String query2 = "SELECT ENCOUNTER_DATE FROM ENCOUNTERS_TABLE WHERE ID='" + id + "'";
                            try (Cursor cursor2 = db.rawQuery(query2, null)) {
                                if (cursor2.moveToFirst()) {
                                    String dateEncountered = cursor2.getString(0);
                                    listOfInfectedUsers.add(new InfectedUserData(id, dateEncountered, dateReported));
                                }
                            }
                        }
                    }
                }

                for (InfectedUserData i : listOfInfectedUsers) {
                    createButton(i);
                }
            }
        };
        setupTableThread.start();

        return root;
    }

    private void createButton(InfectedUserData data) {
        Button button = new Button(new ContextThemeWrapper(requireContext(), R.style.ButtonMoreInfo));
        button.setText(data.getID());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfectedInfoDialog(data);
            }
        });

        Runnable uiStuff = new Runnable() {
            @Override
            public void run() {
                int test = getResources().getDimensionPixelSize(R.dimen.margin);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(test,0,test,test);
                linearLayoutInfected.addView(button, params);
            }
        };

        requireView().post(uiStuff);
    }

    private void showInfectedInfoDialog(InfectedUserData userData) {
        Intent intent = new Intent();
        intent.setClass(requireContext(), NotificationActivity.class);

        intent.putExtra("id", userData.getID());
        intent.putExtra("dateEncountered", userData.getDateEncountered());
        intent.putExtra("dateReported", userData.getDateReported());

        startActivity(intent);
    }

    private void createCallbacks() {
        infectedCallback = new VolleyPOST.SetInfectedCallback() {
            @Override
            public void onSuccessResponse() {
                activeExpo = true;
                // TODO replace placeholder date
                myDB.insertInfectedEncounterData(myID, "placeholderDate");
                deployCardColours();
                Toast.makeText(requireContext(), "Successfully set status as infected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailureResponse() {
                Toast.makeText(requireContext(), "Failed to set as infected, please check your internet connection and try again", Toast.LENGTH_SHORT).show();
            }
        };

        deleteInfectedUserCallback = new VolleyDELETE.deleteInfectedUserCallback() {
            @Override
            public void onSuccessResponse() {
                activeExpo = false;
                myDB.deleteSingleInfectedData(myID);
                deployCardColours();
                Toast.makeText(requireContext(), "Successfully removed infected status", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailureResponse() {
                Toast.makeText(requireContext(), "Failed to remove infected status, please check your internet connection and try again", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void formStuff() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(requireContext(), R.style.AlertDialogCustom));
        alertDialog.setTitle("Enter myBubble ID to proceed");
        alertDialog.setMessage("ID: " + myID);
        final EditText txtBubbleID = new EditText(requireContext());

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(txtBubbleID);
        alertDialog.setView(layout);
        alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String input = txtBubbleID.getText().toString();

                if (input.equals(myID)) {
                    finalConfirmation();
                } else {
                    Toast.makeText(requireContext(), "Failure, make sure the IDs match and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", null);

        alertDialog.show();
    }

    private void finalConfirmation() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(requireContext(), R.style.AlertDialogCustom));
        String title;
        String message;

        if (activeExpo) {
            title = "Are you sure you want to remove your ID from the infected ID list?";
            message = "This will mean that people from your bubble will no longer be warned about their exposure with you.";
        } else {
            title = "Are you sure you want to add your ID to the infected ID list?";
            message = "This will mean that people from your bubble will be notified of exposure to you.";
        }

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (activeExpo) {
                    VolleyDELETE.deleteInfectedUser(requireContext(), deleteInfectedUserCallback);
                } else {
                    VolleyPOST.setInfectedUsers(requireContext(), infectedCallback);
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", null);

        alertDialog.show();
    }


    // Method to set card colours and text contents
    public void deployCardColours() {

        // Check if personal id is in infected id table
        if (!myDB.getInfectedData(myDB.getPersonalInfoData()).equals("Data Not Found")) {
            activeExpo = true;
            txtStatus.setText("Status: Infected");
            imgStatusBackground.setBackgroundColor(red);
            btnExposure.setBackgroundColor(red);
        }
        else {
            activeExpo = false;
            txtStatus.setText("Status: Healthy");
            imgStatusBackground.setBackgroundColor(motorwayGreen);
            btnExposure.setBackgroundColor(motorwayGreen);
        }
    }
}