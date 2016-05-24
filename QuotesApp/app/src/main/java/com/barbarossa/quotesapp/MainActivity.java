package com.barbarossa.quotesapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.barbarossa.quotesapp.sync.QuotesSyncAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity
        implements QuotesListFragment.OnFragmentInteractionListener {

    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private String mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("quotesapp","main activity onCreate()");

        MobileAds.initialize(getApplicationContext(), getString(R.string.application_admob_id));

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if(mDrawer != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                        this,  mDrawer, mToolbar,
                        R.string.open_drawer_desc, R.string.close_drawer_desc
            );

            mDrawer.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }

        setupDrawerContent((NavigationView)findViewById(R.id.quotes_categories_nav));
        QuotesSyncAdapter.initializeSyncAdapter(this);

        Utility.setupAdView((AdView)findViewById(R.id.adView));
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        mCategory = (String)menuItem.getTitle();
        QuotesListFragment qlFragment = (QuotesListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_quote_list);

        qlFragment.onCategoryChanged(mCategory);

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        if(mDrawer != null) {
            mDrawer.closeDrawers();
        }
    }

    @Override
    public void onQuoteClick(long quoteId) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Utility.QUOTE_KEY, quoteId);
        startActivity(intent);
    }

}
