package com.hbmcc.shilinlin.networkoptz.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hbmcc.shilinlin.networkoptz.R;
import com.hbmcc.shilinlin.networkoptz.telephony.cellinfo.LteCellInfo;

import java.util.List;

public class NeighbourCellAdapter extends RecyclerView.Adapter<NeighbourCellAdapter.ViewHolder> {
    private List<LteCellInfo> neighbourCellList;

    public NeighbourCellAdapter(List<LteCellInfo> neighbourCellList) {
        this.neighbourCellList = neighbourCellList;
    }

    @Override
    public void onBindViewHolder(NeighbourCellAdapter.ViewHolder holder, int position) {
        LteCellInfo neighbourCell = neighbourCellList.get(position);
        holder.textViewType.setText(neighbourCell.cellType + "");
        holder.textViewEarfcn.setText(neighbourCell.lteEarFcn + "");
        holder.textViewTAC.setText(neighbourCell.tac + "");
        holder.textViewPCI.setText(neighbourCell.pci + "");
        holder.textViewRSRP.setText(neighbourCell.signalStrength + "");
        holder.textViewSINR.setText(neighbourCell.sinr + "");
    }

    @Override
    public NeighbourCellAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .recyclerview_item_neighbourcellinfo, parent, false);
        return new NeighbourCellAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return neighbourCellList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewType;
        TextView textViewTAC;
        TextView textViewPCI;
        TextView textViewRSRP;
        TextView textViewEarfcn;
        TextView textViewSINR;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewType = itemView.findViewById(R.id.textview_recyclerview_item_neighbourcellinfo_type);
            textViewEarfcn = itemView.findViewById(R.id
                    .textview_recyclerview_item_neighbourcellinfo_earfcn);
            textViewTAC = itemView.findViewById(R.id.textview_recyclerview_item_neighbourcellinfo_tac);
            textViewPCI = itemView.findViewById(R.id.textview_recyclerview_item_neighbourcellinfo_pci);
            textViewRSRP = itemView.findViewById(R.id.textview_recyclerview_item_neighbourcellinfo_rsrp);
            textViewSINR = itemView.findViewById(R.id
                    .textview_recyclerview_item_neighbourcellinfo_sinr);
        }
    }
}
