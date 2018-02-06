package com.ifchan.pdftest3;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static com.ifchan.pdftest3.Utils.Util.EXTRA_URI;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "@vir MainActivity";
    private static final int REQUEST_EXTERNAL_STORAGE = 41;
    public static final int READ_REQUEST_CODE = 42;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    Button selectLocalFileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBasicComponent();
        verifyStoragePermissions(this);


    }

    private void initBasicComponent() {
        selectLocalFileButton = findViewById(R.id.localFile);
        selectLocalFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });

    }

    public void init() {
        Uri uri = Uri.parse("file:///android_asset/linux.pdf");
        Intent intent = new Intent(MainActivity.this, PDFViewActivity.class);
        intent.putExtra(EXTRA_URI, uri);
//        final PdfActivityConfiguration config =
//                new PdfActivityConfiguration.Builder(context)
//                        .build();
        this.startActivity(intent);
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission
                .WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = null;
                uri = data.getData();
                if (uri.toString().substring(uri.toString().lastIndexOf('.')).equalsIgnoreCase("" +
                        ".pdf")) {
                    openPdfUsingUri(uri);
                }
            }
        }
    }

    private void openPdfUsingUri(Uri uri) {
        Intent intent = new Intent(MainActivity.this, PDFViewActivity.class);
        intent.putExtra(EXTRA_URI, uri);
        startActivity(intent);
    }
}
