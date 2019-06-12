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
        public TextView name, price;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvName);
            price = view.findViewById(R.id.tv_price);
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
        holder.price.setText("$"+price.getPrice().toString());

        if(price.getVoteCount()>0) {
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_positive));
        }
        else if(price.getVoteCount() == 0){
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_neutral));
        }
        else if(price.getVoteCount() < 0){
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_negative));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, DetailPriceActivity.class);
                i.putExtra("key", key);
                i.putExtra("price", price);
                mContext.startActivity(i);
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
