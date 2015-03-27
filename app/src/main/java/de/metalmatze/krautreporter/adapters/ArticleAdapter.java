package de.metalmatze.krautreporter.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.models.Article;
import de.metalmatze.krautreporter.models.Author;
import io.realm.Realm;
import io.realm.RealmResults;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    protected final Context context;
    private Realm realm;

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

        realm = Realm.getInstance(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.adapter_article, parent, false);

        return ViewHolder.newInstance(context, itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final Article article = this.articles.get(position);

        Author author = realm.where(Author.class)
                            .equalTo("id", article.getAuthor())
                            .findFirst();

        viewHolder.setTitle(article.getTitle());
        viewHolder.setAuthor(author.getName());

        viewHolder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(article);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView articleTitle;
        private final TextView articleAuthor;


        public static ViewHolder newInstance(Context context, View view)
        {
            TextView title = (TextView) view.findViewById(R.id.article_title);
            TextView author = (TextView) view.findViewById(R.id.article_author);

            Typeface typefaceTisaSans = Typeface.createFromAsset(context.getAssets(), "fonts/TisaSans.otf");
            Typeface typefaceTisaSansBold = Typeface.createFromAsset(context.getAssets(), "fonts/TisaSans-Bold.otf");

            title.setTypeface(typefaceTisaSansBold);
            author.setTypeface(typefaceTisaSans);

            return new ViewHolder(view, title, author);
        }

        public ViewHolder(View itemView, TextView title, TextView articleAuthor) {
            super(itemView);

            this.articleTitle = title;
            this.articleAuthor = articleAuthor;
        }

        public void setTitle(String title) {
            articleTitle.setText(title);
        }

        public void setAuthor(String author) {
            articleAuthor.setText(author);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
        }
    }
}
