package com.barbarossa.quotesapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.barbarossa.quotesapp.data.QuotesLoader;

/**
 * Created by barbarossa on 16/05/16.
 */
public class QuoteDetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String ID_KEY = "ID_KEY";

    private long mQuoteId;
    private String mQuoteText;
    private String mAuthor;

    private TextView mQuoteTv;
    private TextView mAuthorTv;
    private WebView  mArticleWebView;

    public QuoteDetailFragment() {
        // Required empty public constructor
    }

    public static QuoteDetailFragment newInstance(long id) {
        QuoteDetailFragment fragment = new QuoteDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ID_KEY, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuoteId = getArguments().getLong(ID_KEY);
        }
    }

    public void setQuoteId(long quoteId) {
        mQuoteId = quoteId;
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_quote_detail, container, false);

        mQuoteTv = (TextView)rootView.findViewById(R.id.quote_text);
        mAuthorTv = (TextView)rootView.findViewById(R.id.author_text);
        mArticleWebView = (WebView)rootView.findViewById(R.id.detail_webview);

        mArticleWebView.getSettings().setJavaScriptEnabled(true);
        mArticleWebView.setWebViewClient(new WebViewClient());

        webViewContainerMarginFix(rootView.findViewById(R.id.webview_container));

//        getLoaderManager().initLoader(0, null, this);

        return rootView;
    }

    private void webViewContainerMarginFix(View viewById) {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(
                    tv.data,
                    getResources().getDisplayMetrics()
            );
        }

        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        lParams.setMargins(
                0,  // left
                -actionBarHeight,  // top
                0,  // right
                0   // bottom
        );

        viewById.setLayoutParams(lParams);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return QuotesLoader.newQuoteByIdInstance(getContext(), mQuoteId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()) {
            mQuoteText = data.getString(QuotesLoader.Query.QUOTE_TEXT);
            mAuthor = data.getString(QuotesLoader.Query.AUTHOR);

            mQuoteTv.setText(mQuoteText);
            mAuthorTv.setText(mAuthor);

            mArticleWebView.loadUrl(getWikiLink());
        }
    }

    private String getWikiLink() {
        return "https://en.m.wikipedia.org/w/index.php?search=" + mAuthor;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
