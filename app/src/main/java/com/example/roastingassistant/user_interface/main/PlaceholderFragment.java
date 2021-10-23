package com.example.roastingassistant.user_interface.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import Database.Checkpoint;
import Database.RoastCheckpointAssociation;
import Networking.HttpClient;
import Utilities.CommonFunctions;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.roastingassistant.user_interface.BlendActivity;
import com.example.roastingassistant.user_interface.CameraCalibrationActivity;
import com.example.roastingassistant.user_interface.MainActivity;
import com.example.roastingassistant.R;
import com.example.roastingassistant.user_interface.PreviousRoastActivity;
import com.example.roastingassistant.user_interface.RemoteDataBrowserActivity;
import com.example.roastingassistant.user_interface.RoastParamActivity;

import java.util.ArrayList;

import Database.Bean;
import Database.Blend;
import Database.DatabaseHelper;
import Database.Roast;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private int pageNumber;

    int currentLongHoldRoastId=-1;

    View root;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
        pageNumber = index;
    }

    @Override
    public void onResume() {
        super.onResume();

        switch (pageNumber){
            case 1:
                setupRoastFragment(root);
                break;
            case 2:
                setupBeanFragment(root);
                break;
            case 3:
                setupBlendFragment(root);
                break;
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_main, container, false);

        return root;
    }

    private void setupRoastFragment(View root){
        LinearLayout ll = root.findViewById(R.id.linearLayout1);
        ll.removeAllViews();

        Button newRoastBtn = createMenuButton(root, "New Roast", "");
        newRoastBtn.setId(R.id.fragment_button_0);
        newRoastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity main = (MainActivity) getActivity();
                main.startRoastParamActivity();
            }
        });

        ll.addView(newRoastBtn);

        DatabaseHelper db = DatabaseHelper.getInstance(getContext().getApplicationContext());
        ArrayList<Roast> roasts = (ArrayList<Roast>) db.getAllRoastsForButtons();
        if(roasts!=null) {
            for (Roast roast : roasts) {
                Button viewRoastButton = createMenuButton(root, roast.name, roast.roastLevel+" "+roast.dropTemp);
                viewRoastButton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //Toast.makeText(getContext(), "long press!", Toast.LENGTH_SHORT).show();
                        currentLongHoldRoastId = roast.id;
                        showMenu(v);
                        return true;
                    }
                });
                viewRoastButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity main = (MainActivity) getActivity();
                        main.startActivity(RoastParamActivity.class, new Intent().putExtra("Mode", RoastParamActivity.mode.viewing).putExtra("Id", roast.id));
                    }
                });
                ll.addView(viewRoastButton);
            }
        }

        //----Create and add button for browsing network data
        Button previousRoastButton = createMenuButton(root, "View Roast Data", "");
        previousRoastButton.setId(R.id.roast_roastdata_button);
        previousRoastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity main = (MainActivity) getActivity();
                main.startActivity(PreviousRoastActivity.class);
            }
        });

        //add buttons to the layout
        ll.addView(previousRoastButton);

        //----Create and add button for browsing network data
        Button dataBrowserButton = createMenuButton(root, "Download Roasts", "");
        dataBrowserButton.setId(R.id.roast_remotedata_button);
        dataBrowserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity main = (MainActivity) getActivity();
                main.startActivity(RemoteDataBrowserActivity.class, new Intent().putExtra("ViewType", "Roast"));
            }
        });

        //add buttons to the layout
        ll.addView(dataBrowserButton);
    }

    public Context getContext(){
        MainActivity main = (MainActivity) getActivity();
        return main.getContext();
    }

    public void showMenu(View v){
        PopupMenu popup = new PopupMenu(getContext(), v);
        MainActivity main = (MainActivity) getActivity();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        LinearLayout ll = root.findViewById(R.id.linearLayout1);
                                        //if(dbType.equals("Roast"))
                                            DatabaseHelper.getInstance(getContext()).deleteRoast(currentLongHoldRoastId);
                                        //if(dbType.equals("Bean"))
                                        //    DatabaseHelper.getInstance(getContext()).deleteBean(currentLongHoldRoastId);
                                        MainActivity main = (MainActivity) getActivity();
                                        ll.removeView(v);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Delete from database?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                        return true;
                    case R.id.upload:
                        Roast roast = DatabaseHelper.getInstance(getContext()).getRoast(currentLongHoldRoastId);
                        HttpClient client = new HttpClient();
                        String result;

                        for(int i=0; i<roast.checkpoints.size();i++){
                            Checkpoint curCheck = roast.checkpoints.get(i);
                            curCheck.minutes=0;
                            curCheck.seconds=0;
                            result = client.postRequest(curCheck);
                            curCheck.serverId = client.getIdFromResult(result);
                        }

                        result = client.postRequest(roast.bean);
                        roast.bean.serverId = client.getIdFromResult(result);

                        result = client.postRequest(roast);
                        roast.serverId = client.getIdFromResult(result);

                        for(int i=0; i<roast.checkpoints.size();i++){
                            RoastCheckpointAssociation roastCheck = new RoastCheckpointAssociation();
                            roastCheck.roastId = roast.serverId;
                            roastCheck.checkpointId = roast.checkpoints.get(i).serverId;
                            client.postRequest(roastCheck);
                        }

                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_dropdown, popup.getMenu());
        popup.show();
    }








    private void setupBeanFragment(View root){
        LinearLayout ll = root.findViewById(R.id.linearLayout1);
        ll.removeAllViews();

        Button newBeanBtn = createMenuButton(root, "New Bean", "");

        newBeanBtn.setId(R.id.fragment_button_1);
        newBeanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity main = (MainActivity) getActivity();
                main.startBeanViewActivity();
            }
        });

        ll.addView(newBeanBtn);

        DatabaseHelper db = DatabaseHelper.getInstance(getContext().getApplicationContext());
        ArrayList<Bean> beans = (ArrayList<Bean>) db.getAllBeans();
        for(Bean bean: beans){
            Button dbBeanButton = createMenuButton(root, bean.name, bean.origin);
            dbBeanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity main = (MainActivity) getActivity();
                    main.startBeanViewActivity(bean.id);
                }
            });
            ll.addView(dbBeanButton);
        }

        //----Create and add button for browsing network data
        Button dataBrowserButton = createMenuButton(root, "Download Beans", "");
        dataBrowserButton.setId(R.id.roast_remotedata_button);
        dataBrowserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity main = (MainActivity) getActivity();
                main.startActivity(RemoteDataBrowserActivity.class, new Intent().putExtra("ViewType", "Bean"));
            }
        });

        //add buttons to the layout
        ll.addView(dataBrowserButton);
    }

    private void setupBlendFragment(View root){
        LinearLayout ll = root.findViewById(R.id.linearLayout1);
        ll.removeAllViews();

        //------Create and add button for creating a new blend
        Button newBlendBtn = createMenuButton(root, "New Blend", "");

        newBlendBtn.setId(R.id.fragment_button_2);
        newBlendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity main = (MainActivity) getActivity();
                //main.startBlendViewActivity();
                main.startActivity(BlendActivity.class);
            }
        });

        ll.addView(newBlendBtn);

        DatabaseHelper db = DatabaseHelper.getInstance(getContext().getApplicationContext());
        ArrayList<Blend> blends = db.getAllBlends();
        if(blends!=null) {
            for (Blend blend : blends) {
                Button blendButton = createMenuButton(root, blend.name, blend.description);
                blendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO: start blendactivity and send blendid.
                    MainActivity main = (MainActivity) getActivity();
                    //main.startBlendViewActivity();
                    main.startActivity(BlendActivity.class, new Intent().putExtra("Id", blend.id));
                    }
                });
                ll.addView(blendButton);
            }
        }

        //----Create and add button for browsing network data
        Button dataBrowserButton = createMenuButton(root, "Download Blends", "");
        dataBrowserButton.setId(R.id.roast_remotedata_button);
        dataBrowserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity main = (MainActivity) getActivity();
                main.startActivity(RemoteDataBrowserActivity.class, new Intent().putExtra("ViewType", "Blend"));
            }
        });

        //add buttons to the layout
        ll.addView(dataBrowserButton);
    }

    private Button createMenuButton(View root, String title, String subText){
        //set the properties for button
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        Resources r = root.getContext().getResources();

        //--------Converting sizes to dp----------
        int fortydp = CommonFunctions.dp(40, getResources());
        int tendp = CommonFunctions.dp(10, getResources());
        int eightydp = CommonFunctions.dp(60, getResources());

        params.setMargins(fortydp,fortydp,fortydp,tendp);
        Button btnTag = new Button(root.getContext());
        btnTag.setLayoutParams(params);
        if(title.equals("New Roast")||title.equals("New Bean")||title.equals("New Blend")||title.equals("Download Roasts")){
            btnTag.setText(Html.fromHtml("<b>" + title + "</b>"));
            btnTag.setTextSize(fortydp/6);
        }else {
            btnTag.setText(Html.fromHtml("<h1><b>" + title + "</b></h1>\n" + subText));
            btnTag.setTextSize(fortydp/8);
        }
        btnTag.setTextColor(getResources().getColor(R.color.white));
        btnTag.setBackgroundColor(getResources().getColor(R.color.lightGray));
        btnTag.setMinHeight(eightydp+eightydp);
        btnTag.setBackground(this.getResources().getDrawable(R.drawable.round_shape_btn));
        //btnTag.setId("1");
        return btnTag;
    }


}