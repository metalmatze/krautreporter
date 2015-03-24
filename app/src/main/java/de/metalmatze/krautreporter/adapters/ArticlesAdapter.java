package de.metalmatze.krautreporter.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashSet;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.models.Article;
import io.realm.RealmResults;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    public interface OnItemClickListener {

        public void onItemClick(Article article);
    }

    public static final String LOG_TAG = ArticlesAdapter.class.getSimpleName();
    public static final int IMAGE_FADEIN_DURATION = 300;

    protected Context context;
    protected OnItemClickListener onItemClickListener;
    protected Picasso picasso;

    private RealmResults<Article> articles;
    private HashSet<Target> picassoTargets;

    public ArticlesAdapter(Context context, OnItemClickListener onItemClickListener, RealmResults<Article> articles) {
        if (articles == null) {
            throw new IllegalArgumentException("articles cannot be null");
        }

        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.articles = articles;

        this.picasso = Picasso.with(context);
        this.picassoTargets = new HashSet<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.article_card, parent, false);

        return ViewHolder.newInstance(context, itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final Article article = this.articles.get(position);

        viewHolder.setTitle(article.getTitle());
        viewHolder.setDate(article.getDate());
        viewHolder.setExcerpt(article.getExcerpt());

        viewHolder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(article);
            }
        });

        if (article.getImage() != null) {
            viewHolder.setImageVisibility(View.INVISIBLE);

            Target picassoTarget = new Target() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    viewHolder.setImage(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    viewHolder.setImageVisibility(View.GONE);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            picasso.load(context.getString(R.string.url_krautreporter) + article.getImage()).into(picassoTarget);
            picassoTargets.add(picassoTarget);
        }
        else {
            viewHolder.setImageVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView articleImage;
        private final TextView articleTitle;
        private final TextView articleDate;
        private final TextView articleExcerpt;

        public static ViewHolder newInstance(Context context, View view)
        {
            ImageView image = (ImageView) view.findViewById(R.id.article_image);
            TextView title = (TextView) view.findViewById(R.id.article_title);
            TextView date = (TextView) view.findViewById(R.id.article_date);
            TextView excerpt = (TextView) view.findViewById(R.id.article_excerpt);

            Typeface typefaceTisaSans = Typeface.createFromAsset(context.getAssets(), "fonts/TisaSans.otf");
            Typeface typefaceTisaSansBold = Typeface.createFromAsset(context.getAssets(), "fonts/TisaSans-Bold.otf");

            title.setTypeface(typefaceTisaSansBold);
            date.setTypeface(typefaceTisaSans);
            excerpt.setTypeface(typefaceTisaSans);

            return new ViewHolder(view, image, title, date, excerpt);
        }

        public ViewHolder(View itemView, ImageView image, TextView title, TextView date, TextView excerpt) {
            super(itemView);

            this.articleImage = image;
            this.articleTitle = title;
            this.articleDate = date;
            this.articleExcerpt = excerpt;
        }

        public void setImage(Bitmap bitmap) {
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new AccelerateInterpolator());
            fadeIn.setDuration(IMAGE_FADEIN_DURATION);
            fadeIn.setFillAfter(true);

            articleImage.setImageBitmap(bitmap);
            articleImage.setAnimation(fadeIn);
        }

        public void setImageVisibility(int visibility) {
            articleImage.setVisibility(visibility);
        }

        public void setTitle(String title) {
            articleTitle.setText(title);
        }

        public void setDate(String date) {
            articleDate.setText(date);
        }

        public void setExcerpt(String excerpt) {
            articleExcerpt.setText(excerpt);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
        }
    }
}
