package babiy.findplaces.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import babiy.findplaces.R;
import babiy.findplaces.model.DataOfPlace;

public class MyResultAdapter extends ArrayAdapter<DataOfPlace> {

    List<DataOfPlace> placesList;
    Context context;
    private LayoutInflater placesInflater;

    public MyResultAdapter(Context context, List<DataOfPlace> objects) {
        super(context, 0, objects);
        this.context = context;
        this.placesInflater = LayoutInflater.from(context);
        placesList = objects;
    }

    @Override
    public DataOfPlace getItem(int position) {
        return placesList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            View view = placesInflater.inflate(R.layout.result_of_search, parent, false);
            vh = ViewHolder.create((LinearLayout) view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        DataOfPlace item = getItem(position);

        vh.textViewName.setText(item.getName());
        vh.textViewAddress.setText(item.getAddress());
        String open = item.getOpening();

        if (open.equals("Open")){
            vh.textViewOpening.setText(R.string.string_open);
            vh.textViewOpening.setTextColor(Color.GREEN);
        } else if (open.equals("Close")) {
            vh.textViewOpening.setText(R.string.string_close);
            vh.textViewOpening.setTextColor(Color.RED);
        }
        vh.textViewRating.setText(item.getRating());
        vh.textViewDistance.setText((int)item.getDistance() + " m");

        return vh.rootView;
    }

    private static class ViewHolder {
        final LinearLayout rootView;
        final TextView textViewAddress;
        final TextView textViewName;
        final TextView textViewDistance;
        final TextView textViewOpening;
        final TextView textViewRating;

        private ViewHolder(LinearLayout rootView, TextView textViewAddress, TextView textViewName, TextView textViewDistance,
                           TextView textViewOpening, TextView textViewRating) {
            this.rootView = rootView;
            this.textViewAddress = textViewAddress;
            this.textViewName = textViewName;
            this.textViewDistance = textViewDistance;
            this.textViewOpening = textViewOpening;
            this.textViewRating = textViewRating;
        }

        public static ViewHolder create(LinearLayout rootView) {

            TextView textViewName = (TextView) rootView.findViewById(R.id.tvName);
            TextView textViewAddress = (TextView) rootView.findViewById(R.id.tvAddress);
            TextView textViewDistance = (TextView) rootView.findViewById(R.id.tvDistance);
            TextView textViewOpening = (TextView) rootView.findViewById(R.id.tvOpening);
            TextView textViewRating = (TextView) rootView.findViewById(R.id.tvRating);
            return new ViewHolder(rootView, textViewAddress, textViewName, textViewDistance, textViewOpening, textViewRating);
        }

    }
}
