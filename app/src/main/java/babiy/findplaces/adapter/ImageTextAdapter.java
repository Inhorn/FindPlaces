package babiy.findplaces.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import babiy.findplaces.R;


public class ImageTextAdapter extends BaseAdapter {
    private Context mContext;


    public ImageTextAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return mThumbIds[position];
    }

    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        View grid;
        if (convertView == null) {
            grid = new View(mContext);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.item_gridview, parent, false);
        } else {
            grid = convertView;
        }
        String [] categories = grid.getResources().getStringArray(R.array.categories_array);
        ImageView imageView = (ImageView) grid.findViewById(R.id.imagepart);
        TextView textView = (TextView) grid.findViewById(R.id.textpart);
        imageView.setImageResource(mThumbIds[position]);
        textView.setText(categories[position]);

        return grid;
    }
    // references to our images
    private Integer[] mThumbIds = {R.drawable.ic_pic_atm, R.drawable.ic_pic_bank, R.drawable.ic_pic_bar,
            R.drawable.ic_pic_busstation, R.drawable.ic_pic_cafe, R.drawable.ic_pic_castle,
            R.drawable.ic_pic_church, R.drawable.ic_pic_departmentstore, R.drawable.ic_pic_food,
            R.drawable.ic_pic_gasstation, R.drawable.ic_pic_cinema, R.drawable.ic_pic_museum,
            R.drawable.ic_pic_hospital, R.drawable.ic_pic_lodging, R.drawable.ic_pic_pab, R.drawable.ic_pic_parcking,
            R.drawable.ic_pic_pharmacy, R.drawable.ic_pic_park, R.drawable.ic_pic_police, R.drawable.ic_pic_restaurant,
            R.drawable.ic_pic_store, R.drawable.ic_pic_subway, R.drawable.ic_pic_theater, R.drawable.ic_pic_zoo};
}