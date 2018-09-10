package com.hbmcc.shilinlin.networkoptz.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hbmcc.shilinlin.networkoptz.R;
import com.hbmcc.shilinlin.networkoptz.database.LteBasestationCell;

import java.util.List;

public class LteBasestationDatabaseAdapter extends RecyclerView.Adapter<LteBasestationDatabaseAdapter.ViewHolder> {
    private List<LteBasestationCell> lteBasestationCellList;

    public LteBasestationDatabaseAdapter(List<LteBasestationCell> lteBasestationCellList) {
        this.lteBasestationCellList = lteBasestationCellList;
    }

    @Override
    public void onBindViewHolder(LteBasestationDatabaseAdapter.ViewHolder holder, int position) {
        LteBasestationCell lteBasestationCell = lteBasestationCellList.get(position);
        holder.textviewRecyclerviewItemLtebasestationcellCity.setText(lteBasestationCell.getCity
                () + "");
        holder.textviewRecyclerviewItemLtebasestationcellName.setText(lteBasestationCell.getName
                () + "");
        holder.textviewRecyclerviewItemLtebasestationcellEci.setText(lteBasestationCell.getEnbId()
                + "-" + lteBasestationCell.getEnbCellId());
        holder.textviewRecyclerviewItemLtebasestationcellTac.setText(lteBasestationCell.getTac() + "");
        holder.textviewRecyclerviewItemLtebasestationcellPci.setText(lteBasestationCell.getPci() +
                "");
        holder.textviewRecyclerviewItemLtebasestationcellEarfcn.setText(lteBasestationCell
                .getLteEarFcn() + "");

    }

    @Override
    public LteBasestationDatabaseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .recyclerview_item_ltebasestationcell, parent, false);
        return new LteBasestationDatabaseAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return lteBasestationCellList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textviewRecyclerviewItemLtebasestationcellCity;
        TextView textviewRecyclerviewItemLtebasestationcellName;
        TextView textviewRecyclerviewItemLtebasestationcellEci;
        TextView textviewRecyclerviewItemLtebasestationcellTac;
        TextView textviewRecyclerviewItemLtebasestationcellPci;
        TextView textviewRecyclerviewItemLtebasestationcellEarfcn;


        public ViewHolder(View itemView) {
            super(itemView);
            textviewRecyclerviewItemLtebasestationcellCity = itemView.findViewById(R.id.textview_recyclerview_item_ltebasestationcell_city);
            textviewRecyclerviewItemLtebasestationcellName = itemView.findViewById(R.id.textview_recyclerview_item_ltebasestationcell_name);
            textviewRecyclerviewItemLtebasestationcellEci = itemView.findViewById(R.id.textview_recyclerview_item_ltebasestationcell_eci);
            textviewRecyclerviewItemLtebasestationcellTac = itemView.findViewById(R.id.textview_recyclerview_item_ltebasestationcell_tac);
            textviewRecyclerviewItemLtebasestationcellPci = itemView.findViewById(R.id.textview_recyclerview_item_ltebasestationcell_pci);
            textviewRecyclerviewItemLtebasestationcellEarfcn = itemView.findViewById(R.id.textview_recyclerview_item_ltebasestationcell_earfcn);
        }
    }
}
