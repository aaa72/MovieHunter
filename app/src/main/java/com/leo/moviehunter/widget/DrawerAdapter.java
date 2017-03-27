package com.leo.moviehunter.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.leo.moviehunter.R;

import java.util.ArrayList;
import java.util.List;

public class DrawerAdapter extends BaseAdapter {

    public static final int TYPE_ACCOUNT = 0;
    public static final int TYPE_LINK = 1;

    private final List<DrawerItem> EMPTY_LIST = new ArrayList<>(0);

    private List<DrawerItem> mDrawerItemList = EMPTY_LIST;
    private GoogleSignInAccount mGoogleSingInAccount;

    public void setSignInAccount(GoogleSignInAccount account) {
        mGoogleSingInAccount = account;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDrawerItemList.size();
    }

    @Override
    public DrawerItem getItem(int position) {
        return mDrawerItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            switch (getItemViewType(position)) {
                case TYPE_ACCOUNT: {
                    convertView = inflater.inflate(R.layout.drawer_account, null);
                    ViewHolderAccount holder = new ViewHolderAccount();
                    holder.photo = (ImageView) convertView.findViewById(R.id.account_photo);
                    holder.name = (TextView) convertView.findViewById(R.id.account_name);
                    holder.email = (TextView) convertView.findViewById(R.id.account_email);
                    convertView.setTag(holder);
                }
                break;

                case TYPE_LINK: {
                    convertView = inflater.inflate(R.layout.drawer_list_item, null);
                    ViewHolderLink holder = new ViewHolderLink();
                    holder.icon1 = (ImageView) convertView.findViewById(R.id.icon);
                    holder.title = (TextView) convertView.findViewById(R.id.title);
                    holder.icon2 = (ImageView) convertView.findViewById(R.id.icon2);
                    convertView.setTag(holder);
                }
                break;
            }
        }

        switch (getItemViewType(position)) {
            case TYPE_ACCOUNT: {
                ViewHolderAccount holder = (ViewHolderAccount) convertView.getTag();
                if (mGoogleSingInAccount != null) {
                    if (mGoogleSingInAccount.getPhotoUrl() != null) {
                        Glide.with(parent.getContext())
                                .load(mGoogleSingInAccount.getPhotoUrl())
                                .placeholder(R.mipmap.ic_launcher_round)
                                .centerCrop()
                                .crossFade()
                                .into(holder.photo);
                    }
                    holder.name.setVisibility(View.VISIBLE);
                    holder.name.setText(mGoogleSingInAccount.getDisplayName());
                    holder.email.setVisibility(View.VISIBLE);
                    holder.email.setText(mGoogleSingInAccount.getEmail());
                } else {
                    holder.photo.setImageResource(R.mipmap.ic_launcher_round);
                    holder.name.setVisibility(View.GONE);
                    holder.email.setVisibility(View.GONE);
                }
            }
            break;

            case TYPE_LINK: {
                ViewHolderLink holder = (ViewHolderLink) convertView.getTag();
                DrawerItem item = getItem(position);
                holder.title.setText(item.getTitle());
            }
            break;
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    public void setDrawerItemList(List<DrawerItem> drawerItemList) {
        mDrawerItemList = drawerItemList != null ? drawerItemList : EMPTY_LIST;
    }

    private static class ViewHolderAccount {
        ImageView photo;
        TextView name;
        TextView email;
    }

    private static class ViewHolderLink {
        ImageView icon1;
        TextView title;
        ImageView icon2;
    }

    public static class DrawerItem {
        private int type;
        private int id;
        private String title;

        private DrawerItem(int type) {
            this.type = type;
        }

        public static DrawerItem createAccountItem() {
            DrawerItem item = new DrawerItem(TYPE_ACCOUNT);
            return item;
        }

        public static DrawerItem createLinkItem(int id, String title) {
            DrawerItem item = new DrawerItem(TYPE_LINK);
            item.id = id;
            item.title = title;
            return item;
        }

        public int getType() {
            return type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
