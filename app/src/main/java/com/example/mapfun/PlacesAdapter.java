package com.example.mapfun;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.zip.Inflater;

public class PlacesAdapter extends  RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder>{

    Context context;
    String [] latitudeList,longitudeList,adressFromLatLngList;

    public PlacesAdapter(Context context,String [] latitudeList,String [] longitudeList, String [] adressFromLatLngList) {
        this.context = context;
        this.latitudeList = latitudeList;
        this.longitudeList = longitudeList;
        this.adressFromLatLngList = adressFromLatLngList;
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.place_item,parent,false);
        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int position) {

        holder.latTextView.setText(latitudeList[position]);
        holder.longTextView.setText(longitudeList[position]);
        holder.placeTextView.setText(adressFromLatLngList[position]);

    }

    @Override
    public int getItemCount() {
        return latitudeList.length;
    }



    //view holder
    public class PlacesViewHolder extends RecyclerView.ViewHolder {


        TextView latTextView,longTextView,placeTextView;

        public PlacesViewHolder(@NonNull View itemView) {
            super(itemView);


            latTextView=itemView.findViewById(R.id.latTextView);
            longTextView=itemView.findViewById(R.id.longTextView);
            placeTextView=itemView.findViewById(R.id.placeTextView);
        }
    }


}
