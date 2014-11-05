package de.metalmatze.krautreporter.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.List;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.entities.Article;
import de.metalmatze.krautreporter.viewholders.ArticleViewHolder;

public class ArticleRecyclerViewAdapter extends RecyclerView.Adapter<ArticleViewHolder> {

    private List<Article> articles;

    public ArticleRecyclerViewAdapter(List<Article> articles) {
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

        String articleDate = new SimpleDateFormat("dd.MM.yyyy").format(article.getDate().getTime());
        articleViewHolder.article_date.setText(articleDate);
        articleViewHolder.article_headline.setText(article.getTitle());
    }

    @Override
    public int getItemCount() {
        return this.articles.size();
    }
}
