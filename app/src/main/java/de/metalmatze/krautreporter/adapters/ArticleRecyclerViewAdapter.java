package de.metalmatze.krautreporter.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.activities.ArticleActivity;
import de.metalmatze.krautreporter.models.ArticleModel;

public class ArticleRecyclerViewAdapter extends RecyclerView.Adapter<ArticleRecyclerViewAdapter.ViewHolder> {

    private List<ArticleModel> articles;
    private ArticleModel article;

    public ArticleRecyclerViewAdapter(List<ArticleModel> articles) {
        this.articles = articles;
    }

    @Override
    public ArticleRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.article_list, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ArticleRecyclerViewAdapter.ViewHolder viewHolder, int position) {

        this.article = this.articles.get(position);

        String articleDate = new SimpleDateFormat("dd.MM.yyyy").format(article.date.getTime());
        viewHolder.article_date.setText(articleDate);
        viewHolder.article_headline.setText(article.title);
    }

    @Override
    public int getItemCount() {
        return this.articles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView article_date;
        public TextView article_headline;

        public ViewHolder(final View itemView) {
            super(itemView);

            this.article_date = (TextView) itemView.findViewById(R.id.article_date);
            this.article_headline = (TextView) itemView.findViewById(R.id.article_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = itemView.getContext();

                    ArticleModel articleModel = articles.get(getPosition());

                    Intent intent = new Intent(context, ArticleActivity.class);
                    intent.putExtra("id", articleModel.getId());
                    context.startActivity(intent);
                }
            });
        }

    }
}
