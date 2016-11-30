package com.example.lista.cumparaturi.app.internals;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.asadmshah.materiallistitem.AvatarImageView;
import com.asadmshah.materiallistitem.TwoLineAvatarWithTextAndIconView;
import com.daimajia.swipe.SwipeLayout;
import com.example.lista.cumparaturi.R;
import com.example.lista.cumparaturi.app.ContainerDate;
import com.example.lista.cumparaturi.app.activities.AdaugaProdusNou;
import com.example.lista.cumparaturi.app.activities.VizualizarePreturiActivity;
import com.example.lista.cumparaturi.app.beans.Preferinta;
import com.example.lista.cumparaturi.app.beans.Trend;
import com.example.lista.cumparaturi.app.stats.StatsManager;

import java.util.List;

/**
 * Created by macbookproritena on 11/7/16.
 */

public class ListaPreferintaRecyclerAdapter extends RecyclerView.Adapter<ListaPreferintaRecyclerAdapter.PreferintaViewHolder> {
    private final Context context;

    public ListaPreferintaRecyclerAdapter(Context context) {
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
    public void onBindViewHolder(final PreferintaViewHolder holder, final int position) {
        final Preferinta p = ContainerDate.instance().getPreferinte().get(position);
        holder.titleView.setText(p.getProdus().getName());
        holder.subtitleView.setText("Cel mai bun pret: " + StatsManager.instance().lowestPrice(p.getProdus(), context));
        holder.iconView.setImageResource(R.drawable.ic_hourglass_empty_white_24dp);
        holder.iconView.setColorFilter(context.getResources().getColor(p.getUrgente().getColor()));
        holder.avatarView.setImageResource(Trend.getByVal(StatsManager.instance().getGeneralProdSlope(p.getProdus())).getResource());

        holder.deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Preferinta> preferinte = ContainerDate.instance().getPreferinte();
                preferinte.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });

        holder.editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdaugaProdusNou.class);
                intent.putExtra(AdaugaProdusNou.PREFERINTA_EXTRA_TAG, holder.getAdapterPosition());
                context.startActivity(intent);
                notifyItemChanged(holder.getAdapterPosition());
            }
        });

        holder.view.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Intent intent = new Intent(context, VizualizarePreturiActivity.class);
                intent.putExtra(AdaugaProdusNou.PREFERINTA_EXTRA_TAG, holder.getAdapterPosition());
                context.startActivity(intent);
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
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
        FrameLayout deleteLayout, editLayout;

        public PreferintaViewHolder(SwipeLayout itemView) {
            super(itemView);
            view = itemView;
            lineView = (TwoLineAvatarWithTextAndIconView)view.findViewById(R.id.list_item_preferinta);
            titleView = lineView.getTitleView();
            subtitleView = lineView.getSubtitleView();
            iconView = lineView.getIconImageView();
            avatarView = lineView.getAvatarImageView();
            deleteLayout = (FrameLayout) view.findViewById(R.id.deleteFrame);
            editLayout = (FrameLayout) view.findViewById(R.id.editFrame);
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
