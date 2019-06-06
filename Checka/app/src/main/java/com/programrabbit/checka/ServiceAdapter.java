package com.programrabbit.checka;

import android.content.Context;
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
        public TextView name, address, lastUpdate, service, vote;
        public ImageView availibility, serviceImageView, iv_vote;
        public CardView cardView;


        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvName);
            vote = view.findViewById(R.id.tvVotes);
            address = view.findViewById(R.id.tvAddress);
            lastUpdate = view.findViewById(R.id.tvLastUpdated);
            availibility = view.findViewById(R.id.imageViewAvailibility);
            service = view.findViewById(R.id.tvServiceType);
            serviceImageView = view.findViewById(R.id.imageViewService);
            cardView = view.findViewById(R.id.cardView);
            iv_vote = view.findViewById(R.id.iv_vote);
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
        holder.address.setText(service.getAddress());
        holder.lastUpdate.setText(service.getLastUpdate());
        if(service.getVoteCount()>0) {
            holder.vote.setText("Votes +" + service.getVoteCount());
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_positive));
        }
        else if(service.getVoteCount() == 0){
            holder.vote.setText("Votes -");
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_neutral));
        }
        else if(service.getVoteCount() < 0){
            holder.vote.setText("Votes " + service.getVoteCount());
            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.card_negative));
        }

        holder.iv_vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                ArrayList<String> temp = service.getPositiveVoteUsers();
                temp.addAll(service.negativeVoteUsers);
                boolean valid = true;
                for(int i=0;i<temp.size();i++)
                {
                    if(uid.equals(temp.get(i))){

                        new MaterialStyledDialog.Builder(mContext)
                                .setIcon(R.drawable.ic_testing)
                                .setTitle("Vote")
                                .setDescription("You have already voted for this service")
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
                                FirebaseDatabase.getInstance().getReference("Service")
                                        .child(key+"/voteCount").setValue(service.getVoteCount()+1);
                                service.getPositiveVoteUsers().add(uid);
                                FirebaseDatabase.getInstance().getReference("Service")
                                        .child(key+"/positiveVoteUsers").setValue(service.getPositiveVoteUsers());

                            }
                        })
                        .setNegativeText("-1")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Toast.makeText(mContext, "-1", Toast.LENGTH_SHORT).show();
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
