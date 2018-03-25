package com.ifchan.pdftest3.Adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ifchan.pdftest3.Entities.AnnoInfo;
import com.ifchan.pdftest3.MainActivity;
import com.ifchan.pdftest3.PDFViewActivity;
import com.ifchan.pdftest3.R;
import com.ifchan.pdftest3.Utils.MyApplication;
import com.ifchan.pdftest3.view.AnnotationItem;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.ifchan.pdftest3.Utils.Util.SER_KEY;

/**
 * Created by user on 2018/2/6.
 */

public class Annotation_Adapter extends  RecyclerView.Adapter<Annotation_Adapter.ViewHolder> {
    private List<AnnoInfo> mAnnoInfoList;
    private Context mContext;
    private IntentFilter intentFilter;
    private LocalBroadcastManager localBroadcastManager;

    static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView annotation;
//        CardView cardView;
//        TextView page;
        AnnotationItem item;

        public ViewHolder(View view) {
            super(view);
//            annotation = (TextView) view.findViewById(R.id.annotation);
//            cardView = (CardView) itemView;
//            page = (TextView) view.findViewById(R.id.page);
            item = view.findViewById(R.id.list_item);
        }
    }

    public Annotation_Adapter(ArrayList<AnnoInfo> annotationList) {
        mAnnoInfoList = annotationList;
        Log.i("AnnoInfo", mAnnoInfoList.toString());
        for(final AnnoInfo annotation : mAnnoInfoList) {
            Log.i("AnnoInfo", "adapter adAnnoInfo: "+annotation);
//            Toast.makeText(context, (CharSequence) annotation,Toast.LENGTH_SHORT);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.annotation_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext); // 获取实例
        AnnoInfo thisannotation = mAnnoInfoList.get(position);

        holder.item.setContent(thisannotation.content);
        holder.item.setInfo("第"+thisannotation.page+"页");
        Log.i("AnnoInfo", mAnnoInfoList.toString());

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SER_KEY);


                int position = holder.getAdapterPosition();
//                Intent intent = new Intent(mContext, PDFViewActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putInt(SER_KEY,mAnnoInfoList.get(position).page);
                intent.putExtras(mBundle);
//                mContext.startActivity(intent);
                localBroadcastManager.sendBroadcast(intent); // 发送本地广播
            }
        });

    }

    @Override
    public int getItemCount() {
        return mAnnoInfoList.size();
    }


}