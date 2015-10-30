package de.metalmatze.krautreporter.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.malinskiy.superrecyclerview.swipe.BaseSwipeAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.models.Author;
import de.metalmatze.krautreporter.models.Image;

public class AuthorAdapter extends BaseSwipeAdapter {

    private final Context context;

    private List<Author> authors;

    public AuthorAdapter(Context context, List<Author> authors) {
        if (authors == null) {
            throw new IllegalArgumentException("authors cannot be null");
        }

        this.context = context;
        this.authors = authors;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.context).inflate(R.layout.adapter_author, parent, false);

        return ViewHolder.newInstance(context, itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        final Author author = this.authors.get(position);

        Image image = author.getImages().where().equalTo("width", 260).findFirst();
        if (image != null) {
            viewHolder.setAuthorImage(image.getSrc());
        }
        viewHolder.setAuthorName(author.getName());
        viewHolder.setAuthorTitle(author.getTitle());

    }

    @Override
    public int getItemCount() {
        return this.authors.size();
    }

    public static class ViewHolder extends BaseSwipeableViewHolder {
        private final Context context;
        private final ImageView authorImage;
        private final TextView authorName;
        private final TextView authorTitle;

        public static ViewHolder newInstance(Context context, View itemView) {
            ImageView authorImage = (ImageView) itemView.findViewById(R.id.author_image);
            TextView authorName = (TextView) itemView.findViewById(R.id.author_name);
            TextView authorTitle = (TextView) itemView.findViewById(R.id.author_title);

            return new ViewHolder(context, itemView, authorImage, authorName, authorTitle);
        }

        public ViewHolder(Context context, View itemView, ImageView authorImage, TextView authorName, TextView authorTitle) {
            super(itemView);
            this.context = context;
            this.authorImage = authorImage;
            this.authorName = authorName;
            this.authorTitle = authorTitle;
        }

        public void setAuthorName(String name) {
            this.authorName.setText(name);
        }

        public void setAuthorTitle(String title) {
            this.authorTitle.setText(title);
        }

        public void setAuthorImage(String url) {
            Picasso.with(this.context).load(url).into(this.authorImage);
        }
    }
}
