package com.picsapp.texdoo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.lucem.anb.characterscanner.Scanner;
import com.lucem.anb.characterscanner.ScannerListener;

public class ScannerActivity extends AppCompatActivity {
    private Button backIcon;
    private TextView displayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewCompat.setLayoutDirection(getWindow().getDecorView(), ViewCompat.LAYOUT_DIRECTION_LTR);
        setContentView(R.layout.activity_scanner);
        HideStatusBar();
        BackButton();
        ScannerView();
    }

    private void HideStatusBar () {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void BackButton () {
        backIcon = findViewById(R.id.button_back_scanner);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void ScannerView () {
        displayText = findViewById(R.id.text_write);
        SurfaceView surfaceView = findViewById(R.id.surface);
        final Scanner scanner = new Scanner(this, surfaceView, new ScannerListener() {
            @Override
            public void onDetected(String detections) {
                displayText.setText(detections);
            }

            @Override
            public void onStateChanged(String state, int i) {
                Log.d("state", state);
            }
        });
    }
}