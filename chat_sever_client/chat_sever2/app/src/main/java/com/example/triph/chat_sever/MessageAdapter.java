package com.example.triph.chat_sever;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


class  MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyVH> {

    private Context context;
    private List<Sms> smsList;
    private Handler handler;
    private List<Runnable> runnables;

    MessageAdapter(Context context, List<Sms> smsList) {
        this.context = context;
        this.smsList = smsList;
        handler = new Handler();
        runnables = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (smsList.get(position).isMine()) {
            return 0;
        }
        return 1;
    }

    @Override
    public MyVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == 0)
            itemView = LayoutInflater.from(context).inflate(R.layout.item_text_message_me, parent, false);
        else
            itemView = LayoutInflater.from(context).inflate(R.layout.item_text_message_friend, parent, false);
        return new MyVH(itemView);
    }

    @Override
    public void onBindViewHolder(final MyVH holder, int position) {
        Sms previous = null;
        Sms next = null;
        if (position > 0)
            previous = smsList.get(position - 1);
        if (position < getItemCount() - 1)
            next = smsList.get(position + 1);
        if (smsList.get(position).isMine())
            holder.bindMine(previous, smsList.get(position), next);
        else
            holder.bindFriend(previous, smsList.get(position), next);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tvTime.setVisibility(View.VISIBLE);
                holder.tvTime.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.status_appear));
                v.setOnClickListener(null);
                Runnable runnable = new Runnable() {
                    public void run() {
                        resetOnClick();
                    }
                };
                runnables.add(runnable);
                handler.postDelayed(runnable, 3000);
            }

            private void resetOnClick() {
                holder.itemView.setOnClickListener(this);
                Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.status_disappear);
                holder.tvTime.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        holder.tvTime.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

    void destroy() {
        for (Runnable runnable : runnables) {
            handler.removeCallbacks(runnable);
        }
    }
    static class MyVH extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @Nullable
        @BindView(R.id.tvThumbnail)
        TextView tvThumbnail;
        @Nullable
        @BindView(R.id.Mess)
        View mess;
        @BindView(R.id.tvMess)
        TextView tvMess;
        @BindView(R.id.tvTime)
        TextView tvTime;

        MyVH(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindMine(@Nullable Sms previous, @NonNull Sms sms, @Nullable Sms next) {
            String text = sms.getSms().replace("<<<|||space_bar|||>>>", "\n");
            tvMess.setText(text);
            tvTime.setText(Utils.timeFormat.format(sms.getTimestamp()));
            boolean isFirst = (previous == null) || !previous.getUsername().equals(sms.getUsername());
            boolean isLast = (next == null) || !next.getUsername().equals(sms.getUsername());
            boolean isSingleSms = isFirst && isLast;
            boolean isMiddle = !(isFirst || isLast);
            if (isMiddle)
                tvMess.setBackgroundResource(R.drawable.message_me_middle);
            else if (isSingleSms)
                tvMess.setBackgroundResource(R.drawable.message_me_single_sms);
            else if (isFirst)
                tvMess.setBackgroundResource(R.drawable.message_me_first);
            else
                tvMess.setBackgroundResource(R.drawable.message_me_last);
        }

        void bindFriend(@Nullable Sms previous, @NonNull Sms sms, @Nullable Sms next) {
            if (tvUserName != null && tvThumbnail != null && mess!=null) {
                tvUserName.setText(sms.getUsername());
                tvThumbnail.setText(sms.getUsername());
                String text = sms.getSms().replace("<<<|||space_bar|||>>>", "\n");
                tvMess.setText(text);
                tvTime.setText(Utils.timeFormat.format(sms.getTimestamp()));
                boolean isFirst = (previous == null) || !previous.getUsername().equals(sms.getUsername());
                boolean isLast = (next == null) || !next.getUsername().equals(sms.getUsername());
                boolean isSingleSms = isFirst && isLast;
                boolean isMiddle = !(isFirst || isLast);
                if (isMiddle)
                    mess.setBackgroundResource(R.drawable.message_friend_middle);
                else if (isSingleSms)
                    mess.setBackgroundResource(R.drawable.message_friend_single_sms);
                else if (isFirst)
                    mess.setBackgroundResource(R.drawable.message_friend_first);
                else
                    mess.setBackgroundResource(R.drawable.message_friend_last);
                if (!isFirst) {
                    tvUserName.setVisibility(View.GONE);
                    tvThumbnail.setVisibility(View.INVISIBLE);
                    tvThumbnail.setHeight(0);
                } else {
                    tvUserName.setVisibility(View.VISIBLE);
                    tvThumbnail.setVisibility(View.VISIBLE);
                    tvThumbnail.setHeight(itemView.getContext().getResources().getDimensionPixelSize(R.dimen.thumbnail_size));
                }
            }
        }
    }
}
