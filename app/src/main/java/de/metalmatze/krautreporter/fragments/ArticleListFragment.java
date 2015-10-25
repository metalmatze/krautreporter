package de.metalmatze.krautreporter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.adapters.ArticleAdapter;
import de.metalmatze.krautreporter.api.Api;
import de.metalmatze.krautreporter.helpers.DividerItemDecoration;
import de.metalmatze.krautreporter.helpers.Mixpanel;
import de.metalmatze.krautreporter.models.Article;
import de.metalmatze.krautreporter.services.ArticleService;
import de.metalmatze.krautreporter.services.AuthorService;
import rx.Observable;

public class ArticleListFragment extends Fragment implements ArticleAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String ADAPTER_SELECTED_ITEM = "ADAPTER_SELECTED_ITEM";
    private static final int ARTICLES_BEFORE_MORE = 3;

    private ArticleService articleService;
    private AuthorService authorService;

    public interface FragmentCallback {
        void onItemSelected(int id);

        boolean isTwoPane();
    }

    /**
     * The fragment's current OnItemSelectedCallback object, which is notified of list item
     * clicks.
     */
    private FragmentCallback fragmentCallback;

    /**
     * The RecyclerView adapter that has all the articles.
     */
    private ArticleAdapter adapter;

    /**
     * The LinearLayoutManager for the RecyclerView.
     */
    private LinearLayoutManager layoutManager;

    /**
     * The list of all articles.
     */
    private List<Article> articles;

    /**
     * This indicates if more older articles are currently loading
     */
    private boolean isLoadingMore = false;

    @Bind(R.id.superRecyclerView)
    SuperRecyclerView recyclerView;

    public ArticleListFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Activities containing this fragment must implement its callbacks.
        if (!(context instanceof FragmentCallback)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        fragmentCallback = (FragmentCallback) context;

        Api api = Api.with(context);
        articleService = new ArticleService(context, api);
        authorService = new AuthorService(context, api);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        articleService.getArticles()
                .subscribe(articles -> this.articles = articles);

        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        adapter = new ArticleAdapter(getActivity().getApplicationContext(), this.articles, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View fragmentView = inflater.inflate(R.layout.fragment_article_list, container, false);

        ButterKnife.bind(this, fragmentView);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));

        recyclerView.setRefreshListener(this);
        recyclerView.setRefreshingColorResources(
                R.color.refresh1,
                R.color.refresh2,
                R.color.refresh3,
                R.color.refresh3
        );

        recyclerView.getSwipeToRefresh().post(() -> recyclerView.getSwipeToRefresh().setRefreshing(true));
        authorService.updateAuthors()
                .subscribe(authors -> {
                    Observable.merge(
                            articleService.getArticles(),
                            articleService.updateArticles())
                            .subscribe(articles -> {
                                this.articles = articles;
                                adapter.notifyDataSetChanged();
                                recyclerView.getSwipeToRefresh().setRefreshing(false);
                            });
                });

        recyclerView.setupMoreListener((numberOfItems, numberBeforeMore, currentItemPos) -> {
            Article lastArticle = adapter.getLastArticle();
            if (lastArticle.getOrder() > 0 && !isLoadingMore) {
                isLoadingMore = true;
                articleService.getArticlesOlderThan(lastArticle.getId())
                        .subscribe(articles -> {
                            this.articles = articles;
                            adapter.notifyDataSetChanged();
                            recyclerView.hideMoreProgress();
                            isLoadingMore = false;
                        });
            } else {
                recyclerView.hideMoreProgress();
            }
        }, ARTICLES_BEFORE_MORE);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            adapter.setSelectedItem(savedInstanceState.getInt(ADAPTER_SELECTED_ITEM, -1));
        }

        adapter.setTwoPane(fragmentCallback.isTwoPane());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(ADAPTER_SELECTED_ITEM, adapter.getSelectedItem());
    }

    @Override
    public void onItemClick(Article article) {
        fragmentCallback.onItemSelected(article.getId());
    }

    @Override
    public void onRefresh() {
        authorService.updateAuthors()
                .subscribe(authors -> {
                    articleService.updateArticles()
                            .subscribe(articles -> {
                                this.articles = articles;
                                adapter.notifyDataSetChanged();
                                recyclerView.getSwipeToRefresh().setRefreshing(false);
                                Mixpanel.getInstance(getActivity()).track(getString(R.string.mixpanel_articles_refreshed), null);
                            });
                });
    }
}
