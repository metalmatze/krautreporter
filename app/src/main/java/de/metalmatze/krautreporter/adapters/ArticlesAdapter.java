package de.metalmatze.krautreporter.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.models.ArticleModel;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    public interface OnItemClickListener {
        public void onItemClick(ArticleModel articleModel);
    }

    protected Context context;
    private List<ArticleModel> articles;
    protected OnItemClickListener itemClickListener;

    public ArticlesAdapter(Context context, OnItemClickListener itemClickListener, List<ArticleModel> articles) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.articles = articles;
    }

    @Override
    public ArticlesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.article_card, viewGroup, false);

        return ViewHolder.newInstance(itemView);
    }

    @Override
    public void onBindViewHolder(final ArticlesAdapter.ViewHolder viewHolder, int position) {

        final ArticleModel article = this.articles.get(position);

        DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());

        viewHolder.setDate(dateFormat.format(article.date.getTime()));
        viewHolder.setHeadline(article.title);
        viewHolder.setExcerpt(article.excerpt);

        if (article.image != null)
        {
            ImageRequest imageRequest = new ImageRequest(
                    article.image,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            viewHolder.setImage(bitmap);
                        }
                    },
                    0,0,null,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            volleyError.printStackTrace();
                        }
                    }
            );

            Volley.newRequestQueue(this.context).add(imageRequest);
        }

        viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(article);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.articles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView article_image;
        private final TextView article_headline;
        private final TextView article_date;
        private final TextView article_excerpt;

        public static ViewHolder newInstance(View view)
        {
            ImageView image = (ImageView) view.findViewById(R.id.article_image);
            TextView headline = (TextView) view.findViewById(R.id.article_title);
            TextView date = (TextView) view.findViewById(R.id.article_date);
            TextView excerpt = (TextView) view.findViewById(R.id.article_excerpt);

            return new ViewHolder(view, image, headline, date, excerpt);
        }

        public ViewHolder(View itemView, ImageView image, TextView headline, TextView date, TextView excerpt)
        {
            super(itemView);

            this.article_image = image;
            this.article_headline = headline;
            this.article_date = date;
            this.article_excerpt = excerpt;
        }

        public void setImage(Bitmap bitmap) {
            this.article_image.setImageBitmap(bitmap);
            this.article_image.setVisibility(View.VISIBLE);
        }

        public void setHeadline(String headline) {
            this.article_headline.setText(headline);
        }

        public void setDate(String date) {
            this.article_date.setText(date);
        }

        public void setExcerpt(String excerpt) {
            this.article_excerpt.setText(excerpt);
        }

        public void setOnClickListener(View.OnClickListener listener)
        {
            this.itemView.setOnClickListener(listener);
        }

    }
}
