package com.example.triph.chat_sever;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.triph.chat_sever.Utils.dateFormat;
import static com.example.triph.chat_sever.Utils.isToday;
import static com.example.triph.chat_sever.Utils.isTomorrow;
import static com.example.triph.chat_sever.Utils.timeFormat;

class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<String> users;
    private Context context;

    UsersAdapter(List<String> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvThumbnail)
        TextView tvThumbnail;
        @BindView(R.id.tvLoginTime)
        TextView tvLoginTime;
        UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(String s) {
            String parts[] = s.split("~\\|>\\$\\?<@");
            tvThumbnail.setText(parts[1]);
            tvName.setText(parts[1]);
            if (Boolean.parseBoolean(parts[3])) {
                tvName.setTextColor(ContextCompat.getColor(itemView.getContext(),R.color.colorPrimaryDark));
                tvName.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                tvName.setTextColor(Color.parseColor("#000000"));
                tvName.setTypeface(Typeface.DEFAULT);
            }
            long time = Long.parseLong(parts[2]);
            String date;
            if (isToday(time))
                date = "today";
            else if (isTomorrow(time))
                date = "tomorrow";
            else
                date = dateFormat.format(time);
            String loginTime = "Joined at "+ timeFormat.format(time)+ " " + date;
            tvLoginTime.setText(loginTime);
        }
    }
}
