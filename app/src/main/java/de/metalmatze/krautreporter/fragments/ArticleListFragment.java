package de.metalmatze.krautreporter.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.adapters.ArticleAdapter;
import de.metalmatze.krautreporter.api.Api;
import de.metalmatze.krautreporter.helpers.DividerItemDecoration;
import de.metalmatze.krautreporter.helpers.Mixpanel;
import de.metalmatze.krautreporter.models.Article;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ArticleListFragment extends Fragment implements ArticleAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String ADAPTER_SELECTED_ITEM = "ADAPTER_SELECTED_ITEM";

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
     * The realm instance used to fetch the articles.
     */
    private Realm realm;

    /**
     * The list of all articles.
     */
    private RealmResults<Article> articles;

    /**
     * This indicates if more older articles are currently loading
     */
    private boolean isLoading = false;

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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getInstance(getActivity().getApplicationContext());

        articles = realm.where(Article.class).findAll();
        articles.sort("order", RealmResults.SORT_ORDER_DESCENDING);

        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        adapter = new ArticleAdapter(getActivity().getApplicationContext(), articles, this);
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

        recyclerView.getSwipeToRefresh().post(new Runnable() {
            @Override
            public void run() {
                recyclerView.getSwipeToRefresh().setRefreshing(true);
            }
        });

        Api.with(getActivity()).updateAuthors(new Api.ApiCallback() {
            @Override
            public void finished() {
                Api.with(getActivity()).updateArticles(new Api.ApiCallback() {
                    @Override
                    public void finished() {
                        recyclerView.getSwipeToRefresh().setRefreshing(false);
                    }
                });
            }
        });

        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                Article lastArticle = adapter.getLastArticle();

                if (lastArticle.getOrder() > 0) {
                    Api.with(getActivity()).updateArticlesOlderThan(lastArticle.getId(), new Api.ApiCallback() {
                        @Override
                        public void finished() {
                            recyclerView.hideMoreProgress();

                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(getString(R.string.mixpanel_articles_total), articles.size() + 1);
                                Mixpanel.getInstance(getActivity())
                                        .track(getString(R.string.mixpanel_articles_older), jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    recyclerView.hideMoreProgress();
                }
            }
        }, 3);

        realm.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                adapter.notifyDataSetChanged();
            }
        });

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
        Api.with(getActivity()).updateArticles(new Api.ApiCallback() {
            @Override
            public void finished() {
                Mixpanel.getInstance(getActivity()).track(getString(R.string.mixpanel_articles_refreshed), null);
            }
        });
    }
}
