package com.zyydqpi.noburialdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zyydqpi.noburialdemo.sdk.TopRunningActivity;

public class MainActivity extends AppCompatActivity {

    private TopRunningActivity topRunningActivity;
    private Button button;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        topRunningActivity = new TopRunningActivity();
        topRunningActivity.register(this);

        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.btnTest2);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Test2Activity.class);
                startActivity(intent);
            }
        });

    }
}
