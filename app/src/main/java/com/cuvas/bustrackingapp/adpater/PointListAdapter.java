package com.cuvas.bustrackingapp.adpater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cuvas.bustrackingapp.R;
import com.cuvas.bustrackingapp.activities.MapActivity;
import com.cuvas.bustrackingapp.model.PointModel;

import java.util.ArrayList;

public class PointListAdapter extends RecyclerView.Adapter<PointListAdapter.PointListViewHolder> {

    Context context;
    ArrayList<PointModel> pointModelArrayList;

    public PointListAdapter(Context context, ArrayList<PointModel> pointModelArrayList) {
        this.context = context;
        this.pointModelArrayList = pointModelArrayList;
    }

    @Override
    public PointListAdapter.PointListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.point_list_item, parent, false);
        return new PointListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointListAdapter.PointListViewHolder holder, int position) {
        PointModel pointModel = pointModelArrayList.get(position);
        int newPos = position + 1;
        holder.pointNumber.setText("Point " + "("+ pointModel.getRoute() +")");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapActivity.class);
                intent.putExtra("id", pointModel.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pointModelArrayList.size();
    }

    public class PointListViewHolder extends RecyclerView.ViewHolder {
        TextView pointNumber;
        public PointListViewHolder(@NonNull View itemView) {
            super(itemView);
            pointNumber = itemView.findViewById(R.id.pointNumber);

        }
    }
}
