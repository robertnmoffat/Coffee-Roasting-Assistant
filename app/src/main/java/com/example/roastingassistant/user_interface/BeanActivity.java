package com.example.roastingassistant.user_interface;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.roastingassistant.R;

/**
 * Activity for displaying and handling alterations on different un-roasted green bean's information.
 */
public class BeanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bean);

        Button roastAddButton = findViewById(R.id.beanactivity_add_button);
        roastAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}