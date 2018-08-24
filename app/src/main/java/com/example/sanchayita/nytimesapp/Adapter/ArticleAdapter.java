package com.example.sanchayita.nytimesapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanchayita.nytimesapp.Model.ArticleItem;

import com.example.sanchayita.nytimesapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private ArrayList<ArticleItem> mData;
    private Context mContext;

    public ArticleAdapter(ArrayList<ArticleItem> data, Context context) {
        mData = data;
        mContext = context;
    }

    // Called when a new view for an item must be created. This method does not return the view of
    // the item, but a ViewHolder, which holds references to all the elements of the view.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // The view for the item
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        // Create a ViewHolder for this view and return it
        return new ViewHolder(itemView);
    }

    // Populate the elements of the passed view (represented by the ViewHolder) with the data of
    // the item at the specified position.
    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        ArticleItem article = mData.get(position);

        vh.tvTitle.setText(getSafeString(article.getHeadline()));
        String img = article.getThumbnail();


        if (!TextUtils.isEmpty(img)) {

            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.placeholder_thumb)
                    .error(R.drawable.error_thumb)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            // Picasso.with(mContext).load(img).error(R.drawable.placeholder_thumb).into(vh.ivThumb);
            Glide.with(mContext)
                    .load(img)
                     .apply(options)
                    .into(vh.ivThumb);
        }

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    private String getSafeString(String str) {
        if (str == null)
            return "";
        else
            return str;
    }

    public void clearArticles() {
        mData.clear();
        notifyItemRangeRemoved(0, getItemCount());
    }

    public void appendArticles(List<ArticleItem> articles) {
        int oldSize = getItemCount();
        mData.addAll(articles);
        notifyItemRangeInserted(oldSize, articles.size());
    }


    public void add(ArticleItem object) {
        mData.add(object);
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvTitle;

        // Create a viewHolder for the passed view (item view)
        ViewHolder(View view) {
            super(view);
            ivThumb = (ImageView) view.findViewById(R.id.ivThumb);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        }
    }

}