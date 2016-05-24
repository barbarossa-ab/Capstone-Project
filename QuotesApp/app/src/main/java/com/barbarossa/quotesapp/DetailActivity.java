package com.barbarossa.quotesapp;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.ads.AdView;

public class DetailActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private long mQuoteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if(getIntent().hasExtra(Utility.QUOTE_KEY)) {
            mQuoteId = getIntent().getLongExtra(Utility.QUOTE_KEY, -1);
        }

        QuoteDetailFragment qdFragment =
                (QuoteDetailFragment)getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_quote_detail);
        qdFragment.setQuoteId(mQuoteId);

        Utility.setupAdView((AdView)findViewById(R.id.adView));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
