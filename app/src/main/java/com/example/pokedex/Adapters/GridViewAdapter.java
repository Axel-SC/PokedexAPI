package com.example.pokedex.Adapters;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.example.pokedex.R;

        import java.util.List;


public class GridViewAdapter extends ArrayAdapter<typesGridView> {
    public GridViewAdapter(Context context, int resource, List<typesGridView> objects) {
        super(context, resource, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(null == v) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_pokemon, null);
        }
        typesGridView types = getItem(position);
        ImageView img = (ImageView) v.findViewById(R.id.fotoImgView);
        TextView txtName = (TextView) v.findViewById(R.id.nombreTxtView);

        img.setImageResource(types.getImageId());
        txtName.setText(types.getName());

        return v;
    }
}
