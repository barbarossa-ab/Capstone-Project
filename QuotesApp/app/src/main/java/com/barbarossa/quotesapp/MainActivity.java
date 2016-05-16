package com.barbarossa.quotesapp;

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
import android.widget.FrameLayout;

import com.barbarossa.quotesapp.sync.QuotesSyncAdapter;

public class MainActivity extends AppCompatActivity
        implements QuotesListFragment.OnFragmentInteractionListener {

    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private FrameLayout mContainer;

    private String mCategory;

    private static final String CATEGORY_KEY = "CATEGORY_KEY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("quotesapp","main activity onCreate()");

        setContentView(R.layout.activity_main);

        if(savedInstanceState != null && savedInstanceState.containsKey(CATEGORY_KEY)) {
            mCategory = savedInstanceState.getString(CATEGORY_KEY);
        } else {
            mCategory = getResources().getStringArray(R.array.categories_array)[0];
        }

        mContainer = (FrameLayout) findViewById(R.id.fragment_container);

        if(mContainer != null) {
            QuotesListFragment quoteListFragment = QuotesListFragment.newInstance(mCategory);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.fragment_container, quoteListFragment);
            fragmentTransaction.commit();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,  mDrawer, mToolbar,
                R.string.open_drawer_desc, R.string.close_drawer_desc
        );
        mDrawer.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        setupDrawerContent((NavigationView)findViewById(R.id.quotes_categories_nav));

        QuotesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CATEGORY_KEY, mCategory);

        super.onSaveInstanceState(outState);
    }

    //    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // The action bar home/up action should open or close the drawer.
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                mDrawer.openDrawer(GravityCompat.START);
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

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

        QuotesListFragment quotesListFragment = QuotesListFragment.newInstance(mCategory);

//        if (mContainer != null) {
//            mContainer.removeAllViews();
//        }

        // Create new fragment and transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, quotesListFragment);
        transaction.commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
//        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public void onQuoteClick(long quoteId) {
        QuoteDetailFragment quoteDetailFragment = QuoteDetailFragment.newInstance(quoteId);

//        if (mContainer != null) {
//            mContainer.removeAllViews();
//        }

        // Create new fragment and transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, quoteDetailFragment);
        transaction.commit();
    }
}
