package de.metalmatze.krautreporter.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.fragments.ArticleListFragment;
import de.metalmatze.krautreporter.fragments.AuthorListFragment;

public class TabsAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<String> fragmentsTitles = new ArrayList<>();

    public TabsAdapter(FragmentManager fm, Context context) {
        super(fm);

        this.fragments.add(new ArticleListFragment());
        this.fragmentsTitles.add(context.getString(R.string.articles));

        this.fragments.add(new AuthorListFragment());
        this.fragmentsTitles.add(context.getString(R.string.authors));
    }


    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.fragmentsTitles.get(position);
    }
}
