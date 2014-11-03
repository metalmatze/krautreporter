package de.metalmatze.krautreporter.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.metalmatze.krautreporter.R;

public class ArticleViewHolder extends RecyclerView.ViewHolder {

    public TextView article_date;
    public TextView article_headline;

    public ArticleViewHolder(View itemView) {
        super(itemView);

        this.article_date = (TextView) itemView.findViewById(R.id.article_list_date);
        this.article_headline = (TextView) itemView.findViewById(R.id.article_list_headline);
    }

}
