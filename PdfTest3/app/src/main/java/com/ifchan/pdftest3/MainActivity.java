package com.ifchan.pdftest3;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import static com.ifchan.pdftest3.Utils.Util.EXTRA_URI;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "@vir MainActivity";
    private static final int REQUEST_EXTERNAL_STORAGE = 41;
    public static final int READ_REQUEST_CODE = 42;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private ActionBarDrawerToggle drawerbar;
    public DrawerLayout drawerLayout;
    private RelativeLayout main_left_drawer_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBasicComponent();
        verifyStoragePermissions(this);
    }

    private void initBasicComponent() {
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);

        //设置菜单内容之外其他区域的背景色
        drawerLayout.setScrimColor(Color.argb(100, 200, 200, 200));

        //左边菜单
        main_left_drawer_layout = (RelativeLayout) findViewById(R.id.main_left_drawer_layout);

    }

    public void openFile(View view) {
        Uri uri = Uri.parse("file:///android_asset/linux.pdf");
        Intent intent = new Intent(MainActivity.this, PDFViewActivity.class);
        intent.putExtra(EXTRA_URI, uri);
//        final PdfActivityConfiguration config =
//                new PdfActivityConfiguration.Builder(context)
//                        .build();
        this.startActivity(intent);
    }

    public void openLeftLayout(View view) {
        if (drawerLayout.isDrawerOpen(main_left_drawer_layout)) {
            drawerLayout.closeDrawer(main_left_drawer_layout);
        } else {
            drawerLayout.openDrawer(main_left_drawer_layout);
        }
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
                Uri uri;
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
