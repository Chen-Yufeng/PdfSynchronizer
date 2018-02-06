package com.ifchan.pdftest3.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ifchan.pdftest3.R;
import com.pspdfkit.annotations.Annotation;


import java.util.List;

/**
 * Created by user on 2018/2/6.
 */

public class Annotation_Adapter extends  RecyclerView.Adapter<Annotation_Adapter.ViewHolder> {
    private List<Annotation> mAnnotationList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView annotation;

        public ViewHolder(View view) {
            super(view);
            annotation = (TextView) view.findViewById(R.id.annotation);
        }
    }

    public Annotation_Adapter(List<Annotation> annotationList) {
        mAnnotationList = annotationList;
        Log.i("Annotation", mAnnotationList.toString());
        for(final Annotation annotation : mAnnotationList) {
            Log.i("Annotation", "adapter adAnnotation: "+annotation);
//            Toast.makeText(context, (CharSequence) annotation,Toast.LENGTH_SHORT);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.annotation_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Annotation thisannotation = mAnnotationList.get(position);
        holder.annotation.setText(thisannotation.getContents());
        Log.i("Annotation", mAnnotationList.toString());
    }

    @Override
    public int getItemCount() {
        return mAnnotationList.size();
    }
}