package de.metalmatze.krautreporter.adapters;

import android.content.Context;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.List;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.activities.ArticleActivity;
import de.metalmatze.krautreporter.models.ArticleModel;

public class ArticleRecyclerViewAdapter extends RecyclerView.Adapter<ArticleRecyclerViewAdapter.ViewHolder> {

    protected Context context;
    private List<ArticleModel> articles;

    public ArticleRecyclerViewAdapter(Context context, List<ArticleModel> articles) {
        this.context = context;
        this.articles = articles;
    }

    @Override
    public ArticleRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.article_card, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ArticleRecyclerViewAdapter.ViewHolder viewHolder, int position) {

        ArticleModel article = this.articles.get(position);

        String articleDate = new SimpleDateFormat("dd.MM.yyyy").format(article.date.getTime());
        viewHolder.article_date.setText(articleDate);
        viewHolder.article_headline.setText(article.title);

        if (article.image != null)
        {
            viewHolder.article_image.setVisibility(View.VISIBLE);

            ImageRequest imageRequest = new ImageRequest(
                    article.image,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            viewHolder.article_image.setImageBitmap(bitmap);
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

    }

    @Override
    public int getItemCount() {
        return this.articles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView article_image;
        public TextView article_headline;
        public TextView article_date;
        public TextView article_excerpt;

        public ViewHolder(final View itemView) {
            super(itemView);

            this.article_image = (ImageView) itemView.findViewById(R.id.article_image);
            this.article_headline = (TextView) itemView.findViewById(R.id.article_title);
            this.article_date = (TextView) itemView.findViewById(R.id.article_date);
            this.article_excerpt = (TextView) itemView.findViewById(R.id.article_excerpt);

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
