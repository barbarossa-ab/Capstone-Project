package com.barbarossa.quotesapp;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.barbarossa.quotesapp.data.QuotesContract;
import com.barbarossa.quotesapp.model.EndpointInterface;
import com.barbarossa.quotesapp.model.QuoteResponse;
import com.barbarossa.quotesapp.model.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements QuotesListFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        QuotesListFragment forecastFragment =  ((QuotesListFragment)getSupportFragmentManager()
//                .findFragmentById(R.id.fragment_quotes_list));

        getContentResolver().delete(QuotesContract.CONTENT_URI, null, null);

        final String BASE_URL = Utility.BASE_URL;
        final String API_KEY = Utility.API_KEY;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EndpointInterface apiService = retrofit.create(EndpointInterface.class);

//        for(int i = 0 ; i < 20; i++) {
            Call<QuoteResponse> quoteResponseCall = apiService.getQuoteResponse(API_KEY);

            quoteResponseCall.enqueue(new Callback<QuoteResponse>() {
                @Override
                public void onResponse(Call<QuoteResponse> call, Response<QuoteResponse> response) {
                    if(response.isSuccessful()) {
                        QuoteResponse q = response.body();

                        ContentValues vals = new ContentValues();
                        vals.put(QuotesContract.QUOTE_TEXT, q.getContents().getQuote());
                        vals.put(QuotesContract.AUTHOR, q.getContents().getAuthor());
                        vals.put(QuotesContract.QUOTE_ID, q.getContents().getId());

                        getContentResolver().insert(QuotesContract.CONTENT_URI, vals);
                    }
                }

                @Override
                public void onFailure(Call<QuoteResponse> call, Throwable t) {
                }
            });
//        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
