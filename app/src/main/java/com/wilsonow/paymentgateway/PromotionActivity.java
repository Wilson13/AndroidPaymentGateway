package com.wilsonow.paymentgateway;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class PromotionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);
        String region = getIntent().getStringExtra("REGION");

        if ( region != null )
            ((TextView) findViewById(R.id.tv_content)).setText(region);
    }
}
