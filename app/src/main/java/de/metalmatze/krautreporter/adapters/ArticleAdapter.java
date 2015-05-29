package de.metalmatze.krautreporter.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.malinskiy.superrecyclerview.swipe.BaseSwipeAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.models.Article;
import de.metalmatze.krautreporter.models.Image;
import io.realm.RealmResults;

public class ArticleAdapter extends BaseSwipeAdapter {

    protected final Context context;
    private boolean twoPane;

    private int selectedItem = -1;

    public interface OnItemClickListener {
        public void onItemClick(Article article);
    }

    public static final String LOG_TAG = ArticleAdapter.class.getSimpleName();

    protected OnItemClickListener onItemClickListener;

    private RealmResults<Article> articles;

    public ArticleAdapter(@NonNull Context context, RealmResults<Article> articles, OnItemClickListener onItemClickListener) {
        if (articles == null) {
            throw new IllegalArgumentException("articles cannot be null");
        }

        this.context = context;
        this.articles = articles;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.adapter_article, parent, false);

        return ViewHolder.newInstance(context, itemView);
    }

    @Override
    public void onBindViewHolder(BaseSwipeableViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        final Article article = this.articles.get(position);

        if (article.isPreview()) {
            Image image = article.getImages().where().equalTo("width", 1000).findFirst();
            if (image != null) {
                viewHolder.setArticleImage(image.getSrc());
            }
        } else {
            viewHolder.hideArticleImage();
        }

        viewHolder.setArticleTitle(article.getTitle());

        if (article.getAuthor().getName() != null) {
            viewHolder.setArticleAuthor(article.getAuthor().getName());
        }

        if (twoPane) {
            if (selectedItem == position) {
                viewHolder.setSelected(true);
            } else {
                viewHolder.setSelected(false);
            }
        }

        viewHolder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (twoPane) {
                    int oldPosition = selectedItem;
                    viewHolder.setSelected(true);
                    selectedItem = position;
                    notifyItemChanged(oldPosition);
                }
                onItemClickListener.onItemClick(article);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public Article getLastArticle() {
        return articles.last();
    }

    public void setTwoPane(boolean twoPane) {
        this.twoPane = twoPane;
        Log.d(LOG_TAG, String.format("setTwoPane : %b", this.twoPane));
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    public static class ViewHolder extends BaseSwipeableViewHolder {

        private final ImageView articleImage;
        private final TextView articleTitle;
        private final TextView articleAuthor;
        private Context context;

        public static ViewHolder newInstance(Context context, View view) {
            ImageView articleImage = (ImageView) view.findViewById(R.id.article_image);
            TextView articleTitle = (TextView) view.findViewById(R.id.article_title);
            TextView articleAuthor = (TextView) view.findViewById(R.id.article_author);

            Typeface typefaceTisaSans = Typeface.createFromAsset(context.getAssets(), "fonts/TisaSans.otf");
            Typeface typefaceTisaSansBold = Typeface.createFromAsset(context.getAssets(), "fonts/TisaSans-Bold.otf");

            articleTitle.setTypeface(typefaceTisaSansBold);
            articleAuthor.setTypeface(typefaceTisaSans);

            return new ViewHolder(context, view, articleImage, articleTitle, articleAuthor);
        }

        public ViewHolder(Context context, View itemView, ImageView articleImage, TextView articleTitle, TextView articleAuthor) {
            super(itemView);
            this.context = context;

            this.articleImage = articleImage;
            this.articleTitle = articleTitle;
            this.articleAuthor = articleAuthor;
        }

        public void setArticleImage(String url) {
            Picasso.with(context).load(url).into(articleImage, new Callback() {
                @Override
                public void onSuccess() {
                    articleImage.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    articleImage.setVisibility(View.GONE);
                }
            });
        }

        public void hideArticleImage() {
            articleImage.setVisibility(View.GONE);
        }

        public void setArticleTitle(String title) {
            articleTitle.setText(title);
        }

        public void setArticleAuthor(String author) {
            articleAuthor.setText(author.toUpperCase());
        }

        public void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
        }

        public void setSelected(boolean isSelected) {

            int color = context.getResources().getColor(android.R.color.transparent);

            if (isSelected) {
                color = context.getResources().getColor(android.R.color.white);
            }

            itemView.setBackgroundColor(color);
        }
    }
}
