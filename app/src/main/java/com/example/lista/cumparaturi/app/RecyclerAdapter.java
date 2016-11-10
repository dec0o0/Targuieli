package com.example.lista.cumparaturi.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asadmshah.materiallistitem.AvatarImageView;
import com.asadmshah.materiallistitem.TwoLineAvatarWithTextAndIconView;
import com.daimajia.swipe.SwipeLayout;
import com.example.lista.cumparaturi.R;
import com.example.lista.cumparaturi.app.beans.Preferinta;

/**
 * Created by macbookproritena on 11/7/16.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PreferintaViewHolder> {
    private final Context context;

    public RecyclerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public PreferintaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SwipeLayout view = (SwipeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.swipe_layout, parent, false);

        view.setShowMode(SwipeLayout.ShowMode.PullOut);
        view.addDrag(SwipeLayout.DragEdge.Left, view.findViewById(R.id.bottom_wrapper));
        view.addDrag(SwipeLayout.DragEdge.Right, null);
        view.addSwipeListener(newListener());

        return new PreferintaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PreferintaViewHolder holder, int position) {
        Preferinta p = ContainerDate.instance().getPreferinte().get(position);
        holder.titleView.setText(p.getProdus().getName());
        holder.subtitleView.setText("Cel mai bun pret: 22 LEI");
        holder.iconView.setImageResource(R.drawable.ic_hourglass_full_white_24dp);
        holder.iconView.setColorFilter(context.getResources().getColor(p.getUrgente().getColor()));
        holder.avatarView.setImageResource(R.drawable.ic_trending_down_black_24dp);
    }

    @Override
    public int getItemCount() {
        return ContainerDate.instance().getPreferinte().size();
    }

    public static class PreferintaViewHolder extends RecyclerView.ViewHolder{
        SwipeLayout view;
        TwoLineAvatarWithTextAndIconView lineView;
        TextView titleView, subtitleView;
        ImageView iconView;
        AvatarImageView avatarView;

        public PreferintaViewHolder(SwipeLayout itemView) {
            super(itemView);
            view = itemView;
            lineView = (TwoLineAvatarWithTextAndIconView)view.findViewById(R.id.list_item_preferinta);
            titleView = lineView.getTitleView();
            subtitleView = lineView.getSubtitleView();
            iconView = lineView.getIconImageView();
            avatarView = lineView.getAvatarImageView();
        }
    }

    public SwipeLayout.SwipeListener newListener(){
        return new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
            }

            @Override
            public void onOpen(SwipeLayout layout) {

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        };
    }
}
