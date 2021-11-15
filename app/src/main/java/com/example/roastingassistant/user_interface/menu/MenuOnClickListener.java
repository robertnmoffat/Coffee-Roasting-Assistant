package com.example.roastingassistant.user_interface.menu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.roastingassistant.R;
import com.example.roastingassistant.user_interface.CameraCalibrationActivity;
import com.example.roastingassistant.user_interface.MainActivity;
import com.example.roastingassistant.user_interface.RoasterActivity;

import Utilities.GlobalSettings;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ShareCompat;

public class MenuOnClickListener implements View.OnClickListener {
    Context context;

    public MenuOnClickListener(Context context){
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settingsmenu_units:
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        GlobalSettings.getSettings(context).setMetric(true, context);
                                        Toast.makeText(context, "Set to Metric.", Toast.LENGTH_SHORT).show();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        GlobalSettings.getSettings(context).setMetric(false, context);
                                        Toast.makeText(context, "Set to Standard.", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Which unit to use?").setPositiveButton("Metric", dialogClickListener)
                                .setNegativeButton("Standard", dialogClickListener).show();
                        break;
                    case R.id.settingsmenu_language:
                        Toast.makeText(context, "Only English is currently supported.", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.settingsmenu_username:
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);

                        alert.setTitle("Username");
                        //alert.setMessage("Message");

                        // Set an EditText view to get user input
                        final EditText input = new EditText(context);
                        alert.setView(input);

                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                GlobalSettings.getSettings(context).setUsername(input.getText().toString(), context);
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                            }
                        });

                        alert.show();
                        break;
                    case R.id.settingsmenu_delDb:
                        DialogInterface.OnClickListener delDialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        context.deleteDatabase("coffeeDatabase");
                                        Toast.makeText(context, "Database deleted.", Toast.LENGTH_SHORT).show();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder delBuilder = new AlertDialog.Builder(context);
                        delBuilder.setMessage("Are you sure you wish to clear local database?").setPositiveButton("Yes", delDialogClickListener)
                                .setNegativeButton("No", delDialogClickListener).show();
                        break;
                    case R.id.settingsmenu_calibrate:
                        Intent intent = new Intent(context, CameraCalibrationActivity.class);
                        context.startActivity(intent);
                        break;

                    case R.id.settingsmenu_setRoaster:
                        Intent intent2 = new Intent(context, RoasterActivity.class);
                        context.startActivity(intent2);
                        break;
                }

                return false;//whether to consume the click message or send on to others
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.settings_menu, popup.getMenu());
        popup.show();
    }
}
