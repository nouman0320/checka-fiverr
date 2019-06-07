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
        public TextView name, address, lastUpdate, tv_vote;
        public ImageView availibility, iv_vote, iv_map;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvName);
            address = view.findViewById(R.id.tvAddress);
            lastUpdate = view.findViewById(R.id.tvLastUpdated);
            availibility = view.findViewById(R.id.imageViewAvailibility);
            iv_vote = view.findViewById(R.id.iv_vote);
            iv_map = view.findViewById(R.id.iv_map);
            tv_vote = view.findViewById(R.id.tvVotes);
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
        holder.address.setText(fuel.getAddress());
        holder.lastUpdate.setText(fuel.getLastUpdate());

        if(fuel.getAvailabe())
            holder.availibility.setImageResource(R.drawable.ic_checked);
        else holder.availibility.setImageResource(R.drawable.ic_error);

        if(fuel.getVoteCount()>0) {
            holder.tv_vote.setText("Votes +" + fuel.getVoteCount());
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_positive));
        }
        else if(fuel.getVoteCount() == 0){
            holder.tv_vote.setText("Votes -");
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_neutral));
        }
        else if(fuel.getVoteCount() < 0){
            holder.tv_vote.setText("Votes " + fuel.getVoteCount());
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_negative));
        }

        holder.iv_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, DetailFuelActivity.class);
                i.putExtra("key", key);
                i.putExtra("fuel", fuel);
                mContext.startActivity(i);
            }
        });

        holder.iv_vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                ArrayList<String> temp = fuel.getPositiveVoteUsers();
                temp.addAll(fuel.negativeVoteUsers);
                boolean valid = true;
                for(int i=0;i<temp.size();i++)
                {
                    if(uid.equals(temp.get(i))){

                        new MaterialStyledDialog.Builder(mContext)
                                .setIcon(R.drawable.ic_testing)
                                .setTitle("Vote")
                                .setDescription("You have already voted for this fuel update")
                                .setHeaderColor(R.color.colorPrimary)
                                .setPositiveText("Close")
                                .show();
                        return;
                    }
                }


                new MaterialStyledDialog.Builder(mContext)
                        .setIcon(R.drawable.ic_testing)
                        .setTitle("Vote")
                        .setDescription("Is this helpful to you?")
                        .setHeaderColor(R.color.colorPrimary)
                        .setPositiveText("+1")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                Toast.makeText(mContext, "+1", Toast.LENGTH_SHORT).show();
                                FirebaseDatabase.getInstance().getReference("Fuel")
                                        .child(key+"/voteCount").setValue(fuel.getVoteCount()+1);
                                fuel.getPositiveVoteUsers().add(uid);
                                FirebaseDatabase.getInstance().getReference("Fuel")
                                        .child(key+"/positiveVoteUsers").setValue(fuel.getPositiveVoteUsers());

                            }
                        })
                        .setNegativeText("-1")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Toast.makeText(mContext, "-1", Toast.LENGTH_SHORT).show();
                                FirebaseDatabase.getInstance().getReference("Fuel")
                                        .child(key+"/voteCount").setValue(fuel.getVoteCount()-1);
                                fuel.getPositiveVoteUsers().add(uid);
                                FirebaseDatabase.getInstance().getReference("Fuel")
                                        .child(key+"/negativeVoteUsers").setValue(fuel.getPositiveVoteUsers());
                            }
                        })
                        .setNeutralText("Cancel")
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Toast.makeText(mContext, "cancel", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
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
