package com.ldh.android.satellitelayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ArcMenuLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLayout = findViewById(R.id.arcmenu_layout);
        mLayout.setContrlIv((ImageView) findViewById(R.id.iv_control_flag))
        .setOnMenuItemClickListener(new ArcMenuLayout.OnMenuItemClickListener() {
            @Override
            public void ItemClick(String name) {

            }
        });
    }
}
