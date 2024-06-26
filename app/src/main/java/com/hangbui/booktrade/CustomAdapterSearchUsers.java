package com.hangbui.booktrade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapterSearchUsers extends BaseAdapter {

    private Context context;
    private List<User> users;

    public CustomAdapterSearchUsers(Context context, List<User> users){
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_search_users_row, parent, false);
        }
        User thisUser = users.get(position);
        String photoUrl = thisUser.getPhotoUrl();

        TextView userName = convertView.findViewById(R.id.textView_user_name);
        TextView userUniversity = convertView.findViewById(R.id.textView_user_university);
        ImageView photo = convertView.findViewById(R.id.imageView_user_photo);

        userName.setText(thisUser.getName());
        userUniversity.setText(thisUser.getUniversity());
        if (!photoUrl.equals("")) {
            Picasso.get().load(thisUser.getPhotoUrl()).into(photo);
        }

        return convertView;
    }

}

