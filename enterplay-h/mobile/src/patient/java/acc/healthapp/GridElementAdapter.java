package acc.healthapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

/**
 * Created by pronob.mukherjee on 05/01/16.
 */
public class GridElementAdapter extends RecyclerView.Adapter<GridElementAdapter.SimpleViewHolder>{

    private Context context;
    private List<AppDetail> elements;

    public GridElementAdapter(Context context, List elements){
        this.context = context;
        this.elements = elements;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public ImageView appIcon;
        public TextView appName;
        public TextView appLabel;


        public SimpleViewHolder(View view) {
            super(view);
            appIcon = (ImageView)view.findViewById(R.id.item_app_icon);

            appLabel = (TextView)view.findViewById(R.id.item_app_label);

//            appName = (TextView)view.findViewById(R.id.item_app_name);
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(this.context).inflate(R.layout.list_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        holder.appIcon.setImageDrawable(elements.get(position).icon);
        holder.appLabel.setText(elements.get(position).label);
//        holder.appName.setText(elements.get(position).name);

        holder.appIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = context.getPackageManager().getLaunchIntentForPackage(elements.get(position).name.toString());
                context.startActivity(i);
                Toast.makeText(context, "Position =" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.elements.size();
    }
}
