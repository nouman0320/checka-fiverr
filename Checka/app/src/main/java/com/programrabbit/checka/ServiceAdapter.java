package com.programrabbit.checka;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {
    private Context mContext;
    private List<Service> serviceList;
    private List<String> keys;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView availibility, serviceImageView;
        public CardView cardView;


        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvName);
            availibility = view.findViewById(R.id.imageViewAvailibility);
            serviceImageView = view.findViewById(R.id.imageViewService);
            cardView = view.findViewById(R.id.cardView);
        }
    }


    public ServiceAdapter(Context mContext, List<Service> serviceList, List<String> keys) {
        this.mContext = mContext;
        this.serviceList = serviceList;
        this.keys = keys;

        databaseReference = FirebaseDatabase.getInstance().getReference("Service");
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public ServiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_card, parent, false);

        return new ServiceAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ServiceAdapter.MyViewHolder holder, int position) {
        final Service service = serviceList.get(position);
        final String key = keys.get(position);

        holder.name.setText(service.getName());
        if(service.getVoteCount()>0) {
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_positive));
        }
        else if(service.getVoteCount() == 0){
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_neutral));
        }
        else if(service.getVoteCount() < 0){
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_negative));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, DetailServiceActivity.class);
                i.putExtra("key", key);
                i.putExtra("service", service);
                mContext.startActivity(i);
            }
        });



        if(service.getServiceType() == 0) {
            holder.serviceImageView.setImageResource(R.drawable.ic_flash);
        }
        else if(service.getServiceType() == 1) {
            holder.serviceImageView.setImageResource(R.drawable.ic_drop);
        }
        else if(service.getServiceType() == 2) {
            holder.serviceImageView.setImageResource(R.drawable.ic_sewage);
        }
        else {
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

    public void add(List<Service> sr, List<String> k){
        try{
            serviceList.clear();
            serviceList.addAll(sr);
            keys.clear();
            keys.addAll(k);
        }catch(Exception e){
        }
        notifyDataSetChanged();
    }
}
