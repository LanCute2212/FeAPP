package com.example.caloriesapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriesapp.R;
import com.example.caloriesapp.model.BlogArticle;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {
    private List<BlogArticle> articleList;
    private OnArticleClickListener clickListener;

    public interface OnArticleClickListener {
        void onArticleClick(BlogArticle article, int position);
    }

    public BlogAdapter(List<BlogArticle> articleList) {
        this.articleList = articleList;
    }

    public void setOnArticleClickListener(OnArticleClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blog_article, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        BlogArticle article = articleList.get(position);
        holder.bind(article, position, clickListener);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivArticleThumbnail;
        private TextView tvArticleTitle;
        private TextView tvArticleCategory;
        private TextView tvArticleTime;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            ivArticleThumbnail = itemView.findViewById(R.id.ivArticleThumbnail);
            tvArticleTitle = itemView.findViewById(R.id.tvArticleTitle);
            tvArticleCategory = itemView.findViewById(R.id.tvArticleCategory);
            tvArticleTime = itemView.findViewById(R.id.tvArticleTime);
        }

        public void bind(BlogArticle article, int position, OnArticleClickListener clickListener) {
            tvArticleTitle.setText(article.getTitle());
            tvArticleCategory.setText(article.getCategory());
            tvArticleTime.setText(article.getTimeAgo());

            // Set placeholder image (you can replace with actual image loading library like
            // Glide or Picasso)
            if (article.getImageResourceId() != 0) {
                ivArticleThumbnail.setImageResource(article.getImageResourceId());
            }

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onArticleClick(article, position);
                }
            });
        }
    }
}
