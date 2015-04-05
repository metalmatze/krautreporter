package de.metalmatze.krautreporter.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.adapters.ArticleAdapter;
import de.metalmatze.krautreporter.api.Api;
import de.metalmatze.krautreporter.helpers.DividerItemDecoration;
import de.metalmatze.krautreporter.models.Article;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ArticleListFragment extends Fragment implements ArticleAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    public interface FragmentCallback {
        public void onItemSelected(int id);
        public boolean isTwoPane();
    }
    public static final String LOG_TAG = ArticleListFragment.class.getSimpleName();

    /**
     * The fragment's current OnItemSelectedCallback object, which is notified of list item
     * clicks.
     */
    private FragmentCallback fragmentCallback;

    /**
     * The RecyclerView adapter that has all the articles
     */
    private ArticleAdapter adapter;

    /**
     * The LinearLayoutManager for the RecyclerView
     */
    private LinearLayoutManager layoutManager;

    private Realm realm;

    private RealmResults<Article> articles;

    private boolean twoPange;

    /**
     * This indicates if more older articles are currently loading
     */
    private boolean isLoading = false;

    @InjectView(R.id.swipeRefreshArticles)
    SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    public ArticleListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getInstance(getActivity().getApplicationContext());

        articles = realm.where(Article.class).findAll();
        articles.sort("order", RealmResults.SORT_ORDER_DESCENDING);

        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        adapter = new ArticleAdapter(getActivity().getApplicationContext(), articles, this, twoPange);

        Api.with(getActivity()).updateAuthors(new Api.ApiCallback() {
            @Override
            public void finished() {
                Api.with(getActivity()).updateArticles(null);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View fragmentView = inflater.inflate(R.layout.fragment_article_list, container, false);

        ButterKnife.inject(this, fragmentView);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh1,
                R.color.refresh2,
                R.color.refresh3
        );

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (!isLoading) {
                    int itemCountVisible = layoutManager.getChildCount();
                    int itemCountTotal = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((itemCountVisible + firstVisibleItemPosition) >= itemCountTotal - 2) {
                        Article lastArticle = adapter.getLastArticle();

                        if (lastArticle.getOrder() > 0) {
                            isLoading = true;

                            Api.with(getActivity()).updateArticlesOlderThan(lastArticle.getId(), new Api.ApiCallback() {
                                @Override
                                public void finished() {
                                    isLoading = false;
                                }
                            });
                        }

                    }
                }
            }
        });

        setProgressBarVisibility();

        realm.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                setProgressBarVisibility();
                adapter.notifyDataSetChanged();
            }
        });

        return fragmentView;
    }

    private void setProgressBarVisibility() {
        if (articles.size() > 0) {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof FragmentCallback)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        fragmentCallback = (FragmentCallback) activity;
        twoPange = fragmentCallback.isTwoPane();
    }

    @Override
    public void onItemClick(Article article) {
        fragmentCallback.onItemSelected(article.getId());
    }

    @Override
    public void onRefresh() {
        Api.with(getActivity()).updateArticles(new Api.ApiCallback() {
            @Override
            public void finished() {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }
}
