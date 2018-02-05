package com.ifchan.pdftest3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ifchan.pdftest3.Utils.MyApplication;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.PdfFragment;
import com.pspdfkit.ui.inspector.PropertyInspectorCoordinatorLayout;
import com.pspdfkit.document.DocumentSource;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.document.providers.AssetDataProvider;

import java.io.IOException;

import static com.ifchan.pdftest3.Utils.MyApplication.context;
import static com.ifchan.pdftest3.Utils.Util.EXTRA_URI;

public class MainActivity extends AppCompatActivity {

    Button button;

    private static final int REQUEST_EXTERNAL_STORAGE = 41;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);

        verifyStoragePermissions(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
            }
        });


    }




    public void init()  {
        Uri uri = Uri.parse("file:///android_asset/linux.pdf");
        Intent intent = new Intent(MainActivity.this, PDFViewActivity.class);
        intent.putExtra(EXTRA_URI, uri);
//        final PdfActivityConfiguration config =
//                new PdfActivityConfiguration.Builder(context)
//                        .build();
        this.startActivity(intent);
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
