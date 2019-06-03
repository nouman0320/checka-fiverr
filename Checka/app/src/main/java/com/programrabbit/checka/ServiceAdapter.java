package com.programrabbit.checka;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {
    private Context mContext;
    private List<Service> serviceList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, address, lastUpdate, service;
        public ImageView availibility, serviceImageView;


        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvName);
            address = view.findViewById(R.id.tvAddress);
            lastUpdate = view.findViewById(R.id.tvLastUpdated);
            availibility = view.findViewById(R.id.imageViewAvailibility);
            service = view.findViewById(R.id.tvServiceType);
            serviceImageView = view.findViewById(R.id.imageViewService);
        }
    }


    public ServiceAdapter(Context mContext, List<Service> serviceList) {
        this.mContext = mContext;
        this.serviceList = serviceList;
    }

    @Override
    public ServiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_card, parent, false);

        return new ServiceAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ServiceAdapter.MyViewHolder holder, int position) {
        Service service = serviceList.get(position);

        holder.name.setText(service.getName());
        holder.address.setText(service.getAddress());
        holder.lastUpdate.setText(service.getLastUpdate());

        if(service.getServiceType() == 0) {
            holder.service.setText("Electricity");
            holder.serviceImageView.setImageResource(R.drawable.ic_flash);
        }
        else if(service.getServiceType() == 1) {
            holder.service.setText("Water");
            holder.serviceImageView.setImageResource(R.drawable.ic_drop);
        }
        else if(service.getServiceType() == 2) {
            holder.service.setText("Sewage");
            holder.serviceImageView.setImageResource(R.drawable.ic_sewage);
        }
        else {
            holder.service.setText("Unknown");
            holder.serviceImageView.setImageResource(R.drawable.ic_error);
        }


        if(service.getProblemLevel() == 0){
            holder.availibility.setImageResource(R.drawable.ic_checked);
        }
        else if(service.getProblemLevel() == 1){
            holder.availibility.setImageResource(R.drawable.ic_checked_orange);
        }
        else if(service.getProblemLevel() == 2){
            holder.availibility.setImageResource(R.drawable.ic_error);
        } else {
            holder.availibility.setImageResource(R.drawable.ic_error);
        }

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
        return serviceList.size();
    }
}
