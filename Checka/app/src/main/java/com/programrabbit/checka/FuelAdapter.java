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
import com.bumptech.glide.Glide;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FuelAdapter extends RecyclerView.Adapter<FuelAdapter.MyViewHolder> {
    private Context mContext;
    private List<Fuel> fuelList;

    private List<String> keys;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView availibility;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvName);
            availibility = view.findViewById(R.id.imageViewAvailibility);
            cardView = view.findViewById(R.id.cardView);
        }
    }


    public FuelAdapter(Context mContext, List<Fuel> fuelList, List<String> keys) {
        this.mContext = mContext;
        this.fuelList = fuelList;
        this.keys = keys;

        databaseReference = FirebaseDatabase.getInstance().getReference("Fuel");
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fuel_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Fuel fuel = fuelList.get(position);
        final String key = keys.get(position);

        holder.name.setText(fuel.getName());

        if(fuel.getAvailabe())
            holder.availibility.setImageResource(R.drawable.ic_checked);
        else holder.availibility.setImageResource(R.drawable.ic_error);

        if(fuel.getVoteCount()>0) {
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_positive));
        }
        else if(fuel.getVoteCount() == 0){
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_neutral));
        }
        else if(fuel.getVoteCount() < 0){
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_negative));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, DetailFuelActivity.class);
                i.putExtra("key", key);
                i.putExtra("fuel", fuel);
                mContext.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return fuelList.size();
    }

    public void add(List<Fuel> sr, List<String> k){
        try{
            fuelList.clear();
            fuelList.addAll(sr);
            keys.clear();
            keys.addAll(k);
        }catch(Exception e){
        }
        notifyDataSetChanged();
    }
}
