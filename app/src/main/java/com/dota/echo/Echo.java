package com.dota.echo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Echo extends AppCompatActivity {
    private Button startBtn;
    private Button stopBtn;
    private Parrot parrot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_echo);

        startBtn = findViewById(R.id.record_btn);
        stopBtn = findViewById(R.id.stop_btn);
        parrot = new Parrot();

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parrot.init();
                parrot.recordAndPlay();
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parrot.stopRecordPlay();
            }
        });
    }

    @Override
    protected void onDestroy() {
        parrot.dispose();
        super.onDestroy();
    }
}
