package com.hbmcc.shilinlin.networkoptz.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class RecentRecordAdapter extends RecyclerView.Adapter<RecentRecordAdapter.ViewHolder> {
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTime;
        TextView textViewTAC;
        TextView textViewPCI;
        TextView textViewEnb;
        TextView textViewEnbCellId;
        TextView textViewRSRP;
        TextView textViewSINR;
        public ViewHolder(View itemView) {
            super(itemView);
            textViewTime = itemView.findViewById();


        }
    }
}
