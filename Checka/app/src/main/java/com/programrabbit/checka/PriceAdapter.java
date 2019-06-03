package com.programrabbit.checka;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.MyViewHolder> {
    private Context mContext;
    private List<Price> priceList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, address, lastUpdate, price;


        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvName);
            address = view.findViewById(R.id.tvAddress);
            lastUpdate = view.findViewById(R.id.tvLastUpdated);
            price = view.findViewById(R.id.tv_price);
        }
    }


    public PriceAdapter(Context mContext, List<Price> priceList) {
        this.mContext = mContext;
        this.priceList = priceList;
    }

    @Override
    public PriceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.price_card, parent, false);

        return new PriceAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PriceAdapter.MyViewHolder holder, int position) {
        Price price = priceList.get(position);

        holder.name.setText(price.getName());
        holder.address.setText(price.getAddress());
        holder.lastUpdate.setText(price.getLastUpdate());
        holder.price.setText(price.getPrice().toString());


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
        return priceList.size();
    }
}
