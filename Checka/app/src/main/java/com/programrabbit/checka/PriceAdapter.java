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

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.MyViewHolder> {
    private Context mContext;
    private List<Price> priceList;

    private List<String> keys;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, address, lastUpdate, price, tv_vote;

        public ImageView iv_vote, iv_map;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvName);
            address = view.findViewById(R.id.tvAddress);
            lastUpdate = view.findViewById(R.id.tvLastUpdated);
            price = view.findViewById(R.id.tv_price);
            tv_vote = view.findViewById(R.id.tvVotes);
            iv_vote = view.findViewById(R.id.iv_vote);
            iv_map = view.findViewById(R.id.iv_map);
            cardView = view.findViewById(R.id.cardView);
        }
    }


    public PriceAdapter(Context mContext, List<Price> priceList, List<String> keys) {
        this.mContext = mContext;
        this.priceList = priceList;

        this.keys = keys;

        databaseReference = FirebaseDatabase.getInstance().getReference("Price");
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public PriceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.price_card, parent, false);

        return new PriceAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PriceAdapter.MyViewHolder holder, int position) {
        final Price price = priceList.get(position);
        final String key = keys.get(position);

        holder.name.setText(price.getName());
        holder.address.setText(price.getAddress());
        holder.lastUpdate.setText(price.getLastUpdate());
        holder.price.setText("$"+price.getPrice().toString());

        if(price.getVoteCount()>0) {
            holder.tv_vote.setText("Votes +" + price.getVoteCount());
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_positive));
        }
        else if(price.getVoteCount() == 0){
            holder.tv_vote.setText("Votes -");
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_neutral));
        }
        else if(price.getVoteCount() < 0){
            holder.tv_vote.setText("Votes " + price.getVoteCount());
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_negative));
        }

        holder.iv_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, DetailPriceActivity.class);
                i.putExtra("key", key);
                i.putExtra("price", price);
                mContext.startActivity(i);
            }
        });

        holder.iv_vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                ArrayList<String> temp = price.getPositiveVoteUsers();
                temp.addAll(price.negativeVoteUsers);
                boolean valid = true;
                for(int i=0;i<temp.size();i++)
                {
                    if(uid.equals(temp.get(i))){

                        new MaterialStyledDialog.Builder(mContext)
                                .setIcon(R.drawable.ic_testing)
                                .setTitle("Vote")
                                .setDescription("You have already voted for this price update")
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
                                FirebaseDatabase.getInstance().getReference("Price")
                                        .child(key+"/voteCount").setValue(price.getVoteCount()+1);
                                price.getPositiveVoteUsers().add(uid);
                                FirebaseDatabase.getInstance().getReference("Price")
                                        .child(key+"/positiveVoteUsers").setValue(price.getPositiveVoteUsers());

                            }
                        })
                        .setNegativeText("-1")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Toast.makeText(mContext, "-1", Toast.LENGTH_SHORT).show();
                                FirebaseDatabase.getInstance().getReference("Price")
                                        .child(key+"/voteCount").setValue(price.getVoteCount()-1);
                                price.getPositiveVoteUsers().add(uid);
                                FirebaseDatabase.getInstance().getReference("Price")
                                        .child(key+"/negativeVoteUsers").setValue(price.getPositiveVoteUsers());
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
        return priceList.size();
    }

    public void add(List<Price> sr, List<String> k){
        try{
            priceList.clear();
            priceList.addAll(sr);
            keys.clear();
            keys.addAll(k);
        }catch(Exception e){
        }
        notifyDataSetChanged();
    }
}
