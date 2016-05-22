package com.barbarossa.quotesapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.barbarossa.quotesapp.data.QuotesLoader;
import com.barbarossa.quotesapp.data.QuotesProvider;


public class QuotesListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String CATEGORY_KEY = "CATEGORY_KEY";

    private String mCategory;
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private Adapter mQuoteAdapter;

    public QuotesListFragment() {
        // Required empty public constructor
    }

    public static QuotesListFragment newInstance(String category) {
        QuotesListFragment fragment = new QuotesListFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY_KEY, category);
        fragment.setArguments(args);

        Log.e("quotesapp","quote list fragment newInstance() : " + fragment.toString());

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCategory = savedInstanceState.getString(CATEGORY_KEY);
        } else {
            mCategory = getResources().getStringArray(R.array.categories_array)[0].toLowerCase();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(CATEGORY_KEY, mCategory);
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("quotesapp","quote list fragment onCreateView() : " + this.toString());

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_quotes_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);

        mQuoteAdapter = new Adapter();
        mRecyclerView.setAdapter(mQuoteAdapter);

//        if (savedInstanceState == null) {
//            refresh();
//        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.e("quotesapp-loader","init loader... " + this.toString());
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.e("quotesapp","quote list fragment onAttach() : " + this.toString());
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.e("quotesapp","quote list fragment onDetach() : " + this.toString());
        mListener = null;
    }

    public void onCategoryChanged(String category) {
        mCategory = category;
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e("quotesapp-loader","quote list fragment onCreateLoader() : " + this.toString());

        if(mCategory.toLowerCase()
                .equals(getContext()
                        .getString(R.string.categ_favourites).toLowerCase())) {

            return QuotesLoader.newQuotesForCategoryInstance(
                    getContext(),
                    mCategory.toLowerCase());
        }
        return QuotesLoader.newQuotesForCategoryAfterTimestampInstance(
                getContext(),
                mCategory.toLowerCase(),
                Utility.getLastUpdate(getContext()));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e("quotesapp-loader","quote list fragment onLoadFinished() : " + this.toString());
        mQuoteAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e("quotesapp-loader","quote list fragment onLoaderReset() : " + this.toString());
        mQuoteAdapter.swapCursor(null);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onQuoteClick(long id);
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private Cursor mCursor;

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(QuotesLoader.Query._ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.quote_item, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    startActivity(new Intent(Intent.ACTION_VIEW,
//                            ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));

                    mListener.onQuoteClick(getItemId(vh.getAdapterPosition()));
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            mCursor.moveToPosition(position);

            final String quote = mCursor.getString(QuotesLoader.Query.QUOTE_TEXT);
            final String author = mCursor.getString(QuotesLoader.Query.AUTHOR);
            final long quoteId = mCursor.getLong(QuotesLoader.Query._ID);

            holder.quoteView.setText(quote);
            holder.authorView.setText(author);

            holder.shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "\"" + quote + "\" -- " + author);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
            });

            holder.favouriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long favCatId = QuotesProvider.getCategoryIdByName(getContext(),
                        getResources().getString(R.string.categ_favourites).toLowerCase());

                    QuotesProvider.insertQuoteCategoryPair(
                            getContext(),
                            quoteId,
                            favCatId
                    );

                    // update widget
                    Intent dataUpdatedIntent = new Intent(Utility.FAV_QUOTES_UPDATED)
                            .setPackage(getContext().getPackageName());
                    getContext().sendBroadcast(dataUpdatedIntent);
                }
            });
        }

        @Override
        public int getItemCount() {
            if ( null == mCursor ) return 0;
            return mCursor.getCount();

        }

        public void swapCursor(Cursor newCursor) {
            mCursor = newCursor;
            notifyDataSetChanged();
//            mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView quoteView;
        public TextView authorView;
        public ImageView shareBtn;
        public ImageView favouriteBtn;

        public ViewHolder(View view) {
            super(view);
            quoteView = (TextView) view.findViewById(R.id.quote_text);
            authorView = (TextView) view.findViewById(R.id.author_text);
            shareBtn = (ImageView) view.findViewById(R.id.share_btn);
            favouriteBtn = (ImageView) view.findViewById(R.id.favourite_btn);
        }
    }

}
