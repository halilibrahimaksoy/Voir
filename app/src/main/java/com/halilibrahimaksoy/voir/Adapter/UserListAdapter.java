package com.halilibrahimaksoy.voir.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.halilibrahimaksoy.voir.Model.HeadLineItem;
import com.halilibrahimaksoy.voir.Model.UserItem;
import com.halilibrahimaksoy.voir.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Halil ibrahim AKSOY on 30.12.2015.
 */
public class UserListAdapter extends ArrayAdapter<UserItem> {
    LayoutInflater layoutInflater;
    Context context;
    List<UserItem> userItemList = new ArrayList<>();

    public UserListAdapter(Context context, int resource, List<UserItem> objects) {
        super(context, resource, objects);
        userItemList = objects;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.user_item, parent, false);//true dene
            holder = new ViewHolder();
            holder.imgUserProfilImage = (CircleImageView) view.findViewById(R.id.imgUserProfilImage);
            holder.txtUserUsername = (TextView) view.findViewById(R.id.txtUserUsername);
            holder.txtUserName = (TextView) view.findViewById(R.id.txtUserName);
            holder.txtUserLike = (TextView) view.findViewById(R.id.txtUserLike);
            view.setTag(holder);

        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        UserItem userItem = userItemList.get(position);
        if (userItem.getUserProfilImageUrl() != null)
            Picasso.with(getContext()).load(userItem.getUserProfilImageUrl()).error(R.drawable.ic_profile_default).into(holder.imgUserProfilImage);
        else
            holder.imgUserProfilImage.setImageResource(R.drawable.ic_profile_default);
        holder.txtUserUsername.setText(userItem.getUserName());
        holder.txtUserName.setText(userItem.getName());
        holder.txtUserLike.setText(userItem.getUserLikeCounter() + "");

        return view;

    }

    private class ViewHolder {
        private CircleImageView imgUserProfilImage;
        private TextView txtUserUsername, txtUserName, txtUserLike;
    }
}
