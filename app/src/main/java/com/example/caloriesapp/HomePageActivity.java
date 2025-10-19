package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topbar);

        email = getIntent().getStringExtra("email");


        findViewById(R.id.icon_bell).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.avatar).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, ProfileActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        findViewById(R.id.lightning).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, TdeeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.fire).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, AddActivity.class);
            startActivity(intent);
        });

        // Navigate to list when clicking activity title (if present in current layout)
        View titleActivityList = findViewById(R.id.title_activity_list);
        if (titleActivityList != null) {
            titleActivityList.setOnClickListener(v -> {
                Intent intent = new Intent(HomePageActivity.this, ListActivity.class);
                startActivity(intent);
            });
        }

        findViewById(R.id.icon_calendar).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, MonitorActivity.class);
            startActivity(intent);
        });

        // Expand/Collapse handlers for activity items
        LinearLayout details1 = findViewById(R.id.activity_details_1);
        LinearLayout details2 = findViewById(R.id.activity_details_2);
        LinearLayout details3 = findViewById(R.id.activity_details_3);

        View expand1 = findViewById(R.id.expand_item_1_btn);
        View expand2 = findViewById(R.id.expand_item_2_btn);
        View expand3 = findViewById(R.id.expand_item_3_btn);

        View collapse1 = findViewById(R.id.collapse_item_1_btn);
        View collapse2 = findViewById(R.id.collapse_item_2_btn);
        View collapse3 = findViewById(R.id.collapse_item_3_btn);

        if (expand1 != null && details1 != null) {
            expand1.setOnClickListener(v -> details1.setVisibility(View.VISIBLE));
        }
        if (collapse1 != null && details1 != null) {
            collapse1.setOnClickListener(v -> details1.setVisibility(View.GONE));
        }

        if (expand2 != null && details2 != null) {
            expand2.setOnClickListener(v -> details2.setVisibility(View.VISIBLE));
        }
        if (collapse2 != null && details2 != null) {
            collapse2.setOnClickListener(v -> details2.setVisibility(View.GONE));
        }

        if (expand3 != null && details3 != null) {
            expand3.setOnClickListener(v -> details3.setVisibility(View.VISIBLE));
        }
        if (collapse3 != null && details3 != null) {
            collapse3.setOnClickListener(v -> details3.setVisibility(View.GONE));
        }

    }
}