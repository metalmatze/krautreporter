package de.metalmatze.krautreporter.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.entities.Article;
import de.metalmatze.krautreporter.viewholders.ArticleViewHolder;

public class RecyclerAdapter extends RecyclerView.Adapter<ArticleViewHolder> {

    private List<Article> articles;

    public RecyclerAdapter(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.article_list, viewGroup, false);

        return new ArticleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder articleViewHolder, int position) {
        Article article = this.articles.get(position);

        articleViewHolder.article_date.setText(article.getDate());
        articleViewHolder.article_headline.setText(article.getHeadline());
    }

    @Override
    public int getItemCount() {
        return this.articles.size();
    }
}
