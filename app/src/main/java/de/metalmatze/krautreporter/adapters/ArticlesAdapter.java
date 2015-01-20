package de.metalmatze.krautreporter.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.models.Article;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    public interface OnItemClickListener {
        public void onItemClick(Article article);
    }

    public static final String LOG_TAG = ArticlesAdapter.class.getSimpleName();
    public static final int IMAGE_FADEIN_DURATION = 300;

    protected Context context;
    protected OnItemClickListener itemClickListener;
    protected Picasso picasso;

    private List<Article> articles;
    private List<Target> picassoTargets;

    public ArticlesAdapter(Context context, OnItemClickListener itemClickListener, List<Article> articles) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.picasso = Picasso.with(context);

        this.articles = articles;
        this.picassoTargets = new LinkedList<>();
    }

    @Override
    public ArticlesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.article_card, viewGroup, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(LOG_TAG, String.format("onTouch(%f, %f)", event.getX(), event.getY()));
//                    RippleDrawable rippleDrawable = (RippleDrawable) itemView.getBackground();
//                    rippleDrawable.setHotspot(event.getX(), event.getY());
                    itemView.getBackground().setHotspot(event.getX(), event.getY());
                    return false;
                }
            });
        }

        return ViewHolder.newInstance(context, itemView);
    }

    @Override
    public void onBindViewHolder(final ArticlesAdapter.ViewHolder viewHolder, int position) {

        final Article article = this.articles.get(position);

        DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());

        viewHolder.setDate(dateFormat.format(article.getDate().getTime()));
        viewHolder.setHeadline(article.getTitle());
        viewHolder.setExcerpt(article.getExcerpt());

        if (article.getImage() != null)
        {
            viewHolder.setImageVisibility(View.INVISIBLE);

            Target picassoTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Log.d(ArticlesAdapter.class.getSimpleName(), String.format("Picasso loaded %s", article.getImage()));
                    viewHolder.setImage(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    viewHolder.setImageVisibility(View.GONE);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.d(ArticlesAdapter.class.getSimpleName(), String.format("Picasso prepares %s", article.getImage()));
                }
            };

            picasso.load(article.getImage()).into(picassoTarget);
            picassoTargets.add(picassoTarget);
        }
        else {
            viewHolder.setImageVisibility(View.GONE);
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

        public static ViewHolder newInstance(Context context, View view)
        {
            ImageView image = (ImageView) view.findViewById(R.id.article_image);
            TextView headline = (TextView) view.findViewById(R.id.article_title);
            TextView date = (TextView) view.findViewById(R.id.article_date);
            TextView excerpt = (TextView) view.findViewById(R.id.article_excerpt);

            Typeface typefaceTisaSans = Typeface.createFromAsset(context.getAssets(), "fonts/TisaSans.otf");
            Typeface typefaceTisaSansBold = Typeface.createFromAsset(context.getAssets(), "fonts/TisaSans-Bold.otf");

            headline.setTypeface(typefaceTisaSansBold);
            date.setTypeface(typefaceTisaSans);
            excerpt.setTypeface(typefaceTisaSans);

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
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new AccelerateInterpolator());
            fadeIn.setDuration(IMAGE_FADEIN_DURATION);
            fadeIn.setFillAfter(true);

            article_image.setImageBitmap(bitmap);
            article_image.setAnimation(fadeIn);
        }


        public void setImageVisibility(int visibility) {
            this.article_image.setVisibility(visibility);
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
