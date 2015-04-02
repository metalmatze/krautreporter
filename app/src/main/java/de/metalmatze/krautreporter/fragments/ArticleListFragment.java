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

    public interface Callbacks {

        public void onItemSelected(int id);
    }

    public static final String TAG = ArticleListFragment.class.getSimpleName();

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks callbacks;

    private ArticleAdapter adapter;

    private LinearLayoutManager layoutManager;

    @InjectView(R.id.swipeRefreshArticles)
    SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;

    public ArticleListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm realm = Realm.getInstance(getActivity().getApplicationContext());

        final RealmResults<Article> articles = realm.where(Article.class).findAll();
        articles.sort("order", RealmResults.SORT_ORDER_DESCENDING);

        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        adapter = new ArticleAdapter(getActivity().getApplicationContext(), articles, this);

        realm.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                adapter.notifyDataSetChanged();
            }
        });

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

        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        callbacks = (Callbacks) activity;
    }

    @Override
    public void onItemClick(Article article) {
        callbacks.onItemSelected(article.getId());
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
