package com.example.mapfun;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapfun.Database.PlaceModel;

import java.util.List;
import java.util.zip.Inflater;

public class PlacesAdapter extends  RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder>{

    private final List<PlaceModel> placeModelList;
    Context context;




    public PlacesAdapter(Context context, List<PlaceModel> placeModelList) {
        this.context = context;
        this.placeModelList=placeModelList;
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.place_item,parent,false);
        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int position) {

        holder.latTextView.setText(placeModelList.get(position).getLatitude());
        holder.longTextView.setText(placeModelList.get(position).getLongitude());

        if(placeModelList.get(position).getAddress().equals(""))
        {
            holder.placeTextView.setText("Coordinates don't have an address");
        }else
        {
        holder.placeTextView.setText(placeModelList.get(position).getAddress());
        }
    }

    @Override
    public int getItemCount() {
        return placeModelList.size();
    }



    //view holder
    public class PlacesViewHolder extends RecyclerView.ViewHolder {


        TextView latTextView,longTextView,placeTextView;

        public PlacesViewHolder(@NonNull View itemView) {
            super(itemView);


            latTextView=itemView.findViewById(R.id.latTextView);
            longTextView=itemView.findViewById(R.id.longTextView);
            placeTextView=itemView.findViewById(R.id.placeTextView);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context,MapsActivity.class);
                    intent.putExtra("lat",placeModelList.get(getAdapterPosition()).getLatitude());
                    intent.putExtra("long",placeModelList.get(getAdapterPosition()).getLongitude());
                    intent.putExtra("address",placeModelList.get(getAdapterPosition()).getAddress());
                    HomeActivity homeActivity=(HomeActivity)context;
                    homeActivity.startActivity(intent);
                }
            });
        }
    }


}
