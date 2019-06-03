package com.programrabbit.checka;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FuelAdapter extends RecyclerView.Adapter<FuelAdapter.MyViewHolder> {
    private Context mContext;
    private List<Fuel> fuelList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, address, lastUpdate;
        public ImageView availibility;


        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvName);
            address = view.findViewById(R.id.tvAddress);
            lastUpdate = view.findViewById(R.id.tvLastUpdated);
            availibility = view.findViewById(R.id.imageViewAvailibility);
        }
    }


    public FuelAdapter(Context mContext, List<Fuel> fuelList) {
        this.mContext = mContext;
        this.fuelList = fuelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fuel_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Fuel fuel = fuelList.get(position);

        holder.name.setText(fuel.getName());
        holder.address.setText(fuel.getAddress());
        holder.lastUpdate.setText(fuel.getLastUpdate());

        if(fuel.getAvailabe())
            holder.availibility.setImageResource(R.drawable.ic_checked);
        else holder.availibility.setImageResource(R.drawable.ic_error);

        // loading album cover using Glide library
       // Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

        /*
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return fuelList.size();
    }
}
