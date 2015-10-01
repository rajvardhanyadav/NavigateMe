package com.zensar.navigateme.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zensar.navigateme.R;
import com.zensar.navigateme.dto.Location;

import java.util.ArrayList;

/**
 * Created by ry41071 on 04-09-2015.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<Location> locationList;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewAddress, mTextViewSpeed, mTextViewDateTime, mTextViewLat, mTextViewLong, textViewX, textViewY, textViewZ;

        public ViewHolder(View v) {
            super(v);
            this.mTextViewAddress = (TextView) v.findViewById(R.id.textViewAddress);
            this.mTextViewSpeed = (TextView) v.findViewById(R.id.textViewSpeed);
            this.mTextViewDateTime = (TextView) v.findViewById(R.id.textViewDateTime);
            this.mTextViewLat = (TextView) v.findViewById(R.id.textViewLat);
            this.mTextViewLong = (TextView) v.findViewById(R.id.textViewLong);
            this.textViewX = (TextView) v.findViewById(R.id.textViewX);
            this.textViewY = (TextView) v.findViewById(R.id.textViewY);
            this.textViewZ = (TextView) v.findViewById(R.id.textViewZ);
        }
    }

    public MyAdapter(ArrayList<Location> locationList) {
        this.locationList = new ArrayList<Location>();
        this.locationList = locationList;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextViewAddress.setText(locationList.get(position).getLocationName());
        holder.mTextViewSpeed.setText("Speed : " + locationList.get(position).getSpeed() + " km/hr");
        holder.mTextViewDateTime.setText(locationList.get(position).getDateTime());
        holder.mTextViewLat.setText("Latitude : " + locationList.get(position).getLatitude());
        holder.mTextViewLong.setText("Longitude : " + locationList.get(position).getLongitude());
        holder.textViewX.setText("X : " + locationList.get(position).getX());
        holder.textViewY.setText("Y : " + locationList.get(position).getY());
        holder.textViewZ.setText("Z : " + locationList.get(position).getZ());
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }
}
