package com.ifchan.pdftest2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    PDFView mPDFView;
    public static final int REQUEST_CODE = 441;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        declarePermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    Toast.makeText(MainActivity.this, "No Permission!", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    private void init() {
        mPDFView = findViewById(R.id.pdfView);
        String path = Environment.getExternalStorageDirectory().getPath() + "/《Effective Java中文版 " +
                "第2版》.(Joshua Bloch).[PDF]&ckook.pdf";
        File file = new File(path);
        if (file.exists()) {
            mPDFView.fromFile(file).load();
            Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Not exist!", Toast.LENGTH_LONG).show();
        }
    }

    private void declarePermission() {
        boolean flag = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED;
        if (flag) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            init();
        }
    }
}
