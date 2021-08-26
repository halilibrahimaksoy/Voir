package com.halilibrahimaksoy.voir.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.halilibrahimaksoy.voir.Model.HeadLineItem;
import com.halilibrahimaksoy.voir.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Halil ibrahim AKSOY on 22.12.2015.
 */
public class HeadLineAdapter extends ArrayAdapter<HeadLineItem> {
    LayoutInflater layoutInflater;
    Context context;
    List<HeadLineItem> headLineItemList = new ArrayList<>();

    public HeadLineAdapter(Context context, int resource, List<HeadLineItem> objects) {
        super(context, resource, objects);
        headLineItemList = objects;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.headline_item, parent, false);//true dene
            holder = new ViewHolder();
            holder.txtHeadLineCount = (TextView) view.findViewById(R.id.txtHeadLineCount);
            holder.txtHeadTitle = (TextView) view.findViewById(R.id.txtHeadTitle);
            holder.txtHeadDate = (TextView) view.findViewById(R.id.txtHeadDate);
            view.setTag(holder);

        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        HeadLineItem headLineItem = headLineItemList.get(position);
        if (headLineItem.getFeedCount() > 999)
            holder.txtHeadLineCount.setText("999+");
        else
            holder.txtHeadLineCount.setText(headLineItem.getFeedCount() + "");


        holder.txtHeadTitle.setText(headLineItem.getHeadLineText());
        Date dateNow = new Date();
        Date dateSend;
        dateSend = headLineItem.getDate();

        long dateDifference = dateNow.getTime() - dateSend.getTime();

        dateDifference /= 1000;

        if ((dateDifference / (60 * 60 * 24 * 30 * 12)) >= 1)
            holder.txtHeadDate.setText(dateDifference / (60 * 60 * 24 * 30 * 12) + " yıl");
        else if (dateDifference / (60 * 60 * 24 * 30) >= 1)
            holder.txtHeadDate.setText(dateDifference / (60 * 60 * 24 * 30) + " ay");
        else if (dateDifference / (60 * 60 * 24) >= 1)
            holder.txtHeadDate.setText(dateDifference / (60 * 60 * 24) + " gün");
        else if (dateDifference / (60 * 60) >= 1)
            holder.txtHeadDate.setText(dateDifference / (60 * 60) + " saat");
        else if (dateDifference / (60) >= 1)
            holder.txtHeadDate.setText(dateDifference / (60) + " dakika");
        else
            holder.txtHeadDate.setText(dateDifference + " saniye");

        return view;


    }


    private class ViewHolder {
        private TextView txtHeadLineCount, txtHeadTitle, txtHeadDate, imgHeadDate;
    }
}
