package com.ifchan.pdftest3;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.RectF;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.ifchan.pdftest3.Adapter.Annotation_Adapter;
import com.ifchan.pdftest3.Entities.AnnoInfo;
import com.pspdfkit.annotations.Annotation;
import com.pspdfkit.annotations.AnnotationProvider;
import com.pspdfkit.annotations.AnnotationType;
import com.pspdfkit.configuration.PdfConfiguration;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.ui.PdfFragment;
import com.pspdfkit.ui.inspector.PropertyInspectorCoordinatorLayout;
import com.pspdfkit.ui.inspector.annotation.AnnotationCreationInspectorController;
import com.pspdfkit.ui.inspector.annotation.AnnotationEditingInspectorController;
import com.pspdfkit.ui.inspector.annotation.DefaultAnnotationCreationInspectorController;
import com.pspdfkit.ui.inspector.annotation.DefaultAnnotationEditingInspectorController;
import com.pspdfkit.ui.special_mode.controller.AnnotationCreationController;
import com.pspdfkit.ui.special_mode.controller.AnnotationEditingController;
import com.pspdfkit.ui.special_mode.controller.AnnotationSelectionController;
import com.pspdfkit.ui.special_mode.controller.TextSelectionController;
import com.pspdfkit.ui.special_mode.manager.AnnotationManager;
import com.pspdfkit.ui.special_mode.manager.TextSelectionManager;
import com.pspdfkit.ui.toolbar.AnnotationCreationToolbar;
import com.pspdfkit.ui.toolbar.AnnotationEditingToolbar;
import com.pspdfkit.ui.toolbar.TextSelectionToolbar;
import com.pspdfkit.ui.toolbar.ToolbarCoordinatorLayout;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.security.auth.login.LoginException;

import static com.ifchan.pdftest3.Utils.MyApplication.context;
import static com.ifchan.pdftest3.Utils.Util.EXTRA_URI;
import static com.ifchan.pdftest3.Utils.Util.PSPDFKIT_LICENSE_KEY;
import static com.ifchan.pdftest3.Utils.Util.SER_KEY;

public class PDFViewActivity extends AppCompatActivity implements
        AnnotationManager.OnAnnotationCreationModeChangeListener,
        AnnotationManager.OnAnnotationEditingModeChangeListener,
        TextSelectionManager.OnTextSelectionModeChangeListener,
        AnnotationManager.OnAnnotationSelectedListener ,
        AnnotationManager.OnAnnotationUpdatedListener{

    public PdfFragment fragment;
    public Uri fileUri;
    public ToolbarCoordinatorLayout toolbarCoordinatorLayout;
    public AnnotationCreationToolbar annotationCreationToolbar;
    public TextSelectionToolbar textSelectionToolbar;
    public AnnotationEditingToolbar annotationEditingToolbar;
    public PropertyInspectorCoordinatorLayout inspectorCoordinatorLayout;
    public AnnotationEditingInspectorController annotationEditingInspectorController;
    public AnnotationCreationInspectorController annotationCreationInspectorController;

    public Button annotationCreationButton;
    public boolean annotationCreationActive = false;
    public Button annotationClearButton;
    public HashMap<Annotation, Boolean> hasUpload;
    public DrawerLayout mDrawerLayout;

    public IntentFilter intentFilter;
    public LocalReceiver localReceiver;
    public LocalBroadcastManager localBroadcastManager;

    public String TAG;
    public ArrayList<AnnoInfo> annoInfos = new ArrayList<>();
    public List<AnnoInfo> mlist ;

    public List<Annotation> guide = new ArrayList<>();

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    Annotation_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        TAG = "TESTwhere";

        localBroadcastManager = LocalBroadcastManager.getInstance(this); // 获取实例

        final PdfConfiguration config = new PdfConfiguration
                .Builder().build();
        setSupportActionBar(null);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_list);
        }

//        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        toolbarCoordinatorLayout = (ToolbarCoordinatorLayout) findViewById(R.id.toolbarCoordinatorLayout);
        annotationCreationToolbar = new AnnotationCreationToolbar(this);
        textSelectionToolbar = new TextSelectionToolbar(this);
        annotationEditingToolbar = new AnnotationEditingToolbar(this);
        hasUpload = new HashMap<Annotation, Boolean>();


        inspectorCoordinatorLayout = (PropertyInspectorCoordinatorLayout) findViewById(R.id.inspectorCoordinatorLayout);
        annotationEditingInspectorController = new DefaultAnnotationEditingInspectorController(this, inspectorCoordinatorLayout);
        annotationCreationInspectorController = new DefaultAnnotationCreationInspectorController(this, inspectorCoordinatorLayout);


        fileUri = getIntent().getParcelableExtra(EXTRA_URI);
        fragment = (PdfFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);
        fragment = PdfFragment.newInstance(fileUri, config);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit();

        fragment.addOnAnnotationCreationModeChangeListener(this);
        fragment.addOnAnnotationEditingModeChangeListener(this);
        fragment.addOnTextSelectionModeChangeListener(this);
        fragment.addOnAnnotationSelectedListener(this);

        // annotation listener
        fragment.addOnAnnotationUpdatedListener(this);

        annotationCreationButton = (Button) findViewById(R.id.openAnnotationEditing);

        annotationCreationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (annotationCreationActive) {
                    fragment.exitCurrentlyActiveMode();
//                    uploadAnnotation();

                } else {
                    fragment.enterAnnotationCreationMode();
                }
            }
        });

        annotationClearButton = (Button) findViewById(R.id.changePage);

        initAnnotationList();
//        navView.setCheckedItem(R.id.nav_call);
//        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem item) {
//                mDrawerLayout.closeDrawers();
//                return true;
//            }
//        });


        intentFilter = new IntentFilter();
        intentFilter.addAction(SER_KEY);
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    private void initAnnotationList() {

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Annotation_Adapter(annoInfos);
        recyclerView.setAdapter(adapter);
    }

    public void sendToList(){
        annoInfos.clear();
        guide =  fragment
                .getDocument()
                .getAnnotationProvider()
                .getAllAnnotationsOfType(EnumSet.of(AnnotationType.NOTE))
                .toList().blockingGet();
//        guide.addAll(noteAnnotations);
        for(final Annotation annotation : guide) {
            Log.i("Annotation", "uplo adAnnotation: "+"YOOOOOOOOOOOOOOOO"+annotation.toString());
            annoInfos.add(new AnnoInfo (annotation.getPageIndex(),annotation.getContents()));
//            Toast.makeText(context, (CharSequence) annotation,Toast.LENGTH_SHORT);
        }
        adapter.notifyDataSetChanged();

    }
//    public void uploadAnnotation(){
//        // get annotations
//        AnnotationProvider annotationProvider = fragment.getDocument().getAnnotationProvider();
//        @SuppressLint("Range") List<Annotation> annotationList = annotationProvider
//                .getAnnotations(fragment.getPageIndex());
//
//        for(final Annotation annotation : annotationList) {
//            Log.i("Annotation", "uploadAnnotation: "+annotation);
//        }
//    }



    @Override
    public void onEnterAnnotationCreationMode(@NonNull AnnotationCreationController controller) {
        annotationCreationInspectorController.bindAnnotationCreationController(controller);
        annotationCreationToolbar.bindController(controller);
        toolbarCoordinatorLayout.displayContextualToolbar(annotationCreationToolbar, true);
        annotationCreationActive = true;
        annotationClearButton.setVisibility(View.INVISIBLE);
        Log.i(TAG, "onEnterAnnotationCreationMode: ");
    }

    @Override
    public void onChangeAnnotationCreationMode(@NonNull AnnotationCreationController annotationCreationController) {
        Log.i(TAG, "onChangeAnnotationCreationMode: ");
    }

    @Override
    public void onExitAnnotationCreationMode(@NonNull AnnotationCreationController annotationCreationController) {
        toolbarCoordinatorLayout.removeContextualToolbar(true);
        annotationCreationToolbar.unbindController();
        annotationCreationActive = false;

        annotationCreationInspectorController.unbindAnnotationCreationController();

        annotationClearButton.setVisibility(View.VISIBLE);
        Log.i(TAG, "onExitAnnotationCreationMode: ");

    }

    @Override
    public void onEnterAnnotationEditingMode(@NonNull AnnotationEditingController controller) {
        annotationEditingInspectorController.bindAnnotationEditingController(controller);

        annotationEditingToolbar.bindController(controller);
        toolbarCoordinatorLayout.displayContextualToolbar(annotationEditingToolbar, true);
        Log.i(TAG, "onEnterAnnotationEditingMode: ");
    }

    @Override
    public void onChangeAnnotationEditingMode(@NonNull AnnotationEditingController annotationEditingController) {
        Log.i(TAG, "onChangeAnnotationEditingMode: ");
    }

    @Override
    public void onExitAnnotationEditingMode(@NonNull AnnotationEditingController annotationEditingController) {
        toolbarCoordinatorLayout.removeContextualToolbar(true);
        annotationEditingToolbar.unbindController();

        annotationEditingInspectorController.unbindAnnotationEditingController();
        Log.i(TAG, "onExitAnnotationEditingMode: ");
    }

    @Override
    public void onEnterTextSelectionMode(@NonNull TextSelectionController controller) {
        textSelectionToolbar.bindController(controller);
        toolbarCoordinatorLayout.displayContextualToolbar(textSelectionToolbar, true);
        Log.i(TAG, "onEnterTextSelectionMode: ");
    }

    @Override
    public void onExitTextSelectionMode(@NonNull TextSelectionController textSelectionController) {
        toolbarCoordinatorLayout.removeContextualToolbar(true);
        textSelectionToolbar.unbindController();
        Log.i(TAG, "onExitTextSelectionMode: ");
    }

    @Override
    public boolean onPrepareAnnotationSelection(@NonNull AnnotationSelectionController annotationSelectionController, @NonNull Annotation annotation, boolean b) {
        Log.i(TAG, "onPrepareAnnotationSelection: ");
        return true;
    }

    @Override
    public void onAnnotationSelected(@NonNull Annotation annotation, boolean b) {
        Toast.makeText(PDFViewActivity.this,"可以显示作者", Toast.LENGTH_LONG);
        Log.i(TAG, "onAnnotationSelected: ");
    }

    @Override
    public void onAnnotationUpdated(@NonNull Annotation annotation) {
        Log.i(TAG, "onAnnotationUpdated: ");
        sendToList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fragment.removeOnAnnotationCreationModeChangeListener(this);
        fragment.removeOnAnnotationEditingModeChangeListener(this);
        fragment.removeOnTextSelectionModeChangeListener(this);
        fragment.removeOnAnnotationUpdatedListener(this);
        Log.i(TAG, "onDestroy: ");
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "received local broadcast", Toast.LENGTH_SHORT).
                    show();
            int page = getIntent().getIntExtra(SER_KEY,-1);
            if(page!=-1) {
                fragment.setPageIndex(page);
            }
            else{
                Toast.makeText(context, "received local broadcast-------------1", Toast.LENGTH_SHORT).
                        show();
            }
        }
    }
}
