package com.victor.sexytalk.bisou.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.victor.sexytalk.bisou.NavigationDrawerItems;
import com.victor.sexytalk.bisou.R;

import java.util.List;

/**
 * Created by Victor on 14/03/2015.
 */
public class AdapterNavigationDrawer extends ArrayAdapter<NavigationDrawerItems> {
    private final Context context;
    private final List<NavigationDrawerItems> drawerItems;

    public AdapterNavigationDrawer(Context context, List<NavigationDrawerItems> drawerItems) {
        super(context, R.layout.drawer_list_item, drawerItems);


        this.context = context;
        this.drawerItems = drawerItems;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater

        View rowView = null;
        if(!drawerItems.get(position).isGroupHeader()){
            //spisak
            rowView = inflater.inflate(R.layout.drawer_list_item, parent, false);

            // 3. Get icon,title & counter views from the rowView
            TextView titleView = (TextView) rowView.findViewById(R.id.drawerListItem);

            // 4. Set the text for textView
            titleView.setText(drawerItems.get(position).getTitle());
        }
        else{
            //zaglavna stranica
            rowView = inflater.inflate(R.layout.drawer_group_title, parent, false);

            ImageView imgView = (ImageView) rowView.findViewById(R.id.iconHeader);
            TextView titleView = (TextView) rowView.findViewById(R.id.headerTitle);

            imgView.setImageResource(drawerItems.get(position).getIcon());
            titleView.setText(drawerItems.get(position).getTitle());

        }

        // 5. retrn rowView
        return rowView;
    }
}
