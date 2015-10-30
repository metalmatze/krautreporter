package de.metalmatze.krautreporter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.adapters.AuthorAdapter;
import de.metalmatze.krautreporter.api.Api;
import de.metalmatze.krautreporter.helpers.DividerItemDecoration;
import de.metalmatze.krautreporter.models.Author;
import de.metalmatze.krautreporter.services.AuthorService;

public class AuthorListFragment extends Fragment {

    private AuthorService authorService;
    private List<Author> authors;

    private AuthorAdapter authorAdapter;
    private LinearLayoutManager layoutManager;

    @Bind(R.id.author_list)
    SuperRecyclerView recyclerView;

    public AuthorListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Api api = Api.with(getContext());
        this.authorService = new AuthorService(getContext(), api);

        this.authorService.getAuthors()
                .subscribe(authors -> this.authors = authors);

        this.layoutManager = new LinearLayoutManager(getContext());
        this.layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        this.authorAdapter = new AuthorAdapter(getContext(), this.authors);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View fragmentView = inflater.inflate(R.layout.fragment_author_list, container, false);

        ButterKnife.bind(this, fragmentView);

        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.authorAdapter);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), null));

        return fragmentView;
    }
}
