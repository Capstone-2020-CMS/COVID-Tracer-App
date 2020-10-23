package com.covid.ui.notifications;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.covid.R;
import com.covid.database.cloud.VolleyDELETE;
import com.covid.database.cloud.VolleyPOST;
import com.google.android.material.card.MaterialCardView;

import static com.covid.MainActivity.activeExpo;
import static com.covid.MainActivity.myDB;
import static com.covid.MainActivity.myID;


public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    // TextView vars
    TextView txtStatus;
    TextView txtStatusDetails;
    TextView txtIDValue;
    TextView txtExposure;

    // MaterialCard vars
    private static MaterialCardView cardExposure;
    private static MaterialCardView cardStatus;

    // Colour vars
    private static int motorwayGreen;
    private static int red;

    private VolleyPOST.SetInfectedCallback infectedCallback;
    private VolleyDELETE.deleteInfectedUserCallback deleteInfectedUserCallback;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Initialise colours
        motorwayGreen = getResources().getColor(R.color.motorwayGreen);
        red = getResources().getColor(R.color.red);

        // Initialise TextViews
        txtStatus = root.findViewById(R.id.txtViewStatus);
        txtStatusDetails = root.findViewById(R.id.txtViewStatusDetail);
        txtExposure = root.findViewById(R.id.txtExposure);

        txtIDValue = root.findViewById(R.id.txtIDValue);
        txtIDValue.setText(myID);

        // Initialise MaterialCards
        cardExposure = root.findViewById(R.id.cardExposure);
        cardStatus = root.findViewById(R.id.cardStatus);

        // Create callback
        createCallbacks();

        // Set the initial card colours
        deployCardColours();

        // Create onClick methods for the buttons
        txtExposure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formStuff();
            }
        });

        return root;
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

            cardExposure.setCardBackgroundColor(motorwayGreen);
            cardStatus.setCardBackgroundColor(red);

            txtStatus.setText(R.string.notefragment_status_infectious);
            txtStatusDetails.setText(R.string.notefragment_status_infectious_detail);

            txtExposure.setText(R.string.note_fragment_revert_expo);
        }
        else {
            activeExpo = false;

            cardExposure.setCardBackgroundColor(red);
            cardStatus.setCardBackgroundColor(motorwayGreen);

            txtStatus.setText(R.string.notefragment_status_healthy);
            txtStatusDetails.setText(R.string.notefragment_status_healthy_detail);

            txtExposure.setText(R.string.note_fragment_confirm_expo);
        }
    }
}