package com.ifchan.pdftest3;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.pspdfkit.annotations.Annotation;
import com.pspdfkit.annotations.AnnotationProvider;
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

import static com.ifchan.pdftest3.Utils.MyApplication.context;
import static com.ifchan.pdftest3.Utils.Util.EXTRA_URI;
import static com.ifchan.pdftest3.Utils.Util.PSPDFKIT_LICENSE_KEY;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        final PdfConfiguration config = new PdfConfiguration
                .Builder().build();
        setSupportActionBar(null);

        toolbarCoordinatorLayout = (ToolbarCoordinatorLayout) findViewById(R.id.toolbarCoordinatorLayout);
        annotationCreationToolbar = new AnnotationCreationToolbar(this);
        textSelectionToolbar = new TextSelectionToolbar(this);
        annotationEditingToolbar = new AnnotationEditingToolbar(this);

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
                } else {
                    fragment.enterAnnotationCreationMode();
                }
            }
        });

        annotationClearButton = (Button) findViewById(R.id.changePage);
    }


    @Override
    public void onEnterAnnotationCreationMode(@NonNull AnnotationCreationController controller) {
        annotationCreationInspectorController.bindAnnotationCreationController(controller);
        annotationCreationToolbar.bindController(controller);
        toolbarCoordinatorLayout.displayContextualToolbar(annotationCreationToolbar, true);
        annotationCreationActive = true;
        annotationClearButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onChangeAnnotationCreationMode(@NonNull AnnotationCreationController annotationCreationController) {

    }

    @Override
    public void onExitAnnotationCreationMode(@NonNull AnnotationCreationController annotationCreationController) {
        toolbarCoordinatorLayout.removeContextualToolbar(true);
        annotationCreationToolbar.unbindController();
        annotationCreationActive = false;

        annotationCreationInspectorController.unbindAnnotationCreationController();

        annotationClearButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEnterAnnotationEditingMode(@NonNull AnnotationEditingController controller) {
        annotationEditingInspectorController.bindAnnotationEditingController(controller);

        annotationEditingToolbar.bindController(controller);
        toolbarCoordinatorLayout.displayContextualToolbar(annotationEditingToolbar, true);

    }

    @Override
    public void onChangeAnnotationEditingMode(@NonNull AnnotationEditingController annotationEditingController) {

    }

    @Override
    public void onExitAnnotationEditingMode(@NonNull AnnotationEditingController annotationEditingController) {
        toolbarCoordinatorLayout.removeContextualToolbar(true);
        annotationEditingToolbar.unbindController();

        annotationEditingInspectorController.unbindAnnotationEditingController();
    }

    @Override
    public void onEnterTextSelectionMode(@NonNull TextSelectionController controller) {
        textSelectionToolbar.bindController(controller);
        toolbarCoordinatorLayout.displayContextualToolbar(textSelectionToolbar, true);
    }

    @Override
    public void onExitTextSelectionMode(@NonNull TextSelectionController textSelectionController) {
        toolbarCoordinatorLayout.removeContextualToolbar(true);
        textSelectionToolbar.unbindController();
    }

    @Override
    public boolean onPrepareAnnotationSelection(@NonNull AnnotationSelectionController annotationSelectionController, @NonNull Annotation annotation, boolean b) {
        return true;
    }

    @Override
    public void onAnnotationSelected(@NonNull Annotation annotation, boolean b) {
        Toast.makeText(PDFViewActivity.this,"可以显示作者", Toast.LENGTH_LONG);
    }

    @Override
    public void onAnnotationUpdated(@NonNull Annotation annotation) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fragment.removeOnAnnotationCreationModeChangeListener(this);
        fragment.removeOnAnnotationEditingModeChangeListener(this);
        fragment.removeOnTextSelectionModeChangeListener(this);
        fragment.removeOnAnnotationUpdatedListener(this);
    }
}
