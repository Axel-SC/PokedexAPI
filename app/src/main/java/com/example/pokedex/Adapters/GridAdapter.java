package com.example.pokedex.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pokedex.R;
import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    private Context context;
    ArrayList<typesGridView> typesList;
    private int [] images;
    private String [] typeNames;


    public GridAdapter(Context context) {
        typesList = new ArrayList<>();
        String [] names = {"fire", "grass", "water", "electric", "dragon"};
        int [] images = {R.drawable.fire, R.drawable.grass,R.drawable.water, R.drawable.electric, R.drawable.dragon};
        this.context = context;
        for (int i = 0; i < names.length; i++) {
            typesList.add(new typesGridView(images[i], names[i]));
        }
    }

    @Override
    public int getCount() {
        return typesList.size();
    }

    @Override
    public Object getItem(int pos) {
        return typesList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.types_gridview, parent);
        }
        TextView name = (TextView) convertView.findViewById(R.id.nombreTxtView);
        ImageView img = (ImageView) convertView.findViewById(R.id.fotoImgView);

        name.setText(typeNames[pos]);
        img.setImageResource(images[pos]);
        return convertView;
    }
}
