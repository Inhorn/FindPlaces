package babiy.findplaces.adapter;


import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import babiy.findplaces.R;
import babiy.findplaces.model.DataOfPlace;

public class MyRecycleAdapter extends RecyclerView.Adapter<MyRecycleAdapter.DataOfPlaceViewHolder> {

    List<DataOfPlace> dataOfPlaces;

    public MyRecycleAdapter(List<DataOfPlace> dataOfPlaces) {
        this.dataOfPlaces = dataOfPlaces;
    }

    @Override
    public DataOfPlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);
        DataOfPlaceViewHolder dvh = new DataOfPlaceViewHolder(v);
        return dvh;
    }

    @Override
    public void onBindViewHolder(DataOfPlaceViewHolder holder, int position) {

        holder.tvName.setText(dataOfPlaces.get(position).getName());
        holder.tvAddress.setText(dataOfPlaces.get(position).getAddress());

        String open = dataOfPlaces.get(position).getOpening();

        if (open.equals("Open")) {
            holder.tvOpening.setText(R.string.string_open);
            holder.tvOpening.setTextColor(Color.GREEN);
        } else if (open.equals("Close")) {
            holder.tvOpening.setText(R.string.string_close);
            holder.tvOpening.setTextColor(Color.RED);
        }

        holder.tvDistance.setText((int) dataOfPlaces.get(position).getDistance() + " m");
        holder.ratingBar.setNumStars(5);
        holder.ratingBar.setRating(Float.parseFloat(dataOfPlaces.get(position).getRating()));

    }

    @Override
    public int getItemCount() {
        return dataOfPlaces.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class DataOfPlaceViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView tvName;
        TextView tvAddress;
        TextView tvDistance;
        TextView tvOpening;
        RatingBar ratingBar;

        DataOfPlaceViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddressCard);
            tvName = (TextView) itemView.findViewById(R.id.tvNameCard);
            tvDistance = (TextView) itemView.findViewById(R.id.tvDistanceCard);
            tvOpening = (TextView) itemView.findViewById(R.id.tvOpeningCard);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBarCard);
        }
    }

}
