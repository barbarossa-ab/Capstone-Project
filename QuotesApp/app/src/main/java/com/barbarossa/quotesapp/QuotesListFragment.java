package com.barbarossa.quotesapp;

import android.content.Context;
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
import android.widget.TextView;

import com.barbarossa.quotesapp.data.QuotesLoader;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuotesListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuotesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuotesListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String CATEGORY_KEY = "CATEGORY_KEY";

    private String mCategory;
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;


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
        if (getArguments() != null) {
            mCategory = getArguments().getString(CATEGORY_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("quotesapp","quote list fragment onCreateView() : " + this.toString());

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_quotes_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        getLoaderManager().initLoader(0, null, this);

//        if (savedInstanceState == null) {
//            refresh();
//        }

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e("quotesapp","quote list fragment onCreateLoader() : " + this.toString());

        return QuotesLoader.newQuotesForCategoryInstance(getContext(), mCategory);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e("quotesapp","quote list fragment onLoadFinished() : " + this.toString());

        Adapter adapter = new Adapter(data);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e("quotesapp","quote list fragment onLoaderReset() : " + this.toString());
        mRecyclerView.setAdapter(null);
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

        public Adapter(Cursor cursor) {
            mCursor = cursor;
        }

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
            holder.quoteView.setText(mCursor.getString(QuotesLoader.Query.QUOTE_TEXT));
            holder.authorView.setText(mCursor.getString(QuotesLoader.Query.AUTHOR));
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView quoteView;
        public TextView authorView;

        public ViewHolder(View view) {
            super(view);
            quoteView = (TextView) view.findViewById(R.id.quote_text);
            authorView = (TextView) view.findViewById(R.id.author_text);
        }
    }

}
