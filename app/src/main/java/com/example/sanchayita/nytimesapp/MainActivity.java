package com.example.sanchayita.nytimesapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.sanchayita.nytimesapp.Adapter.ArticleAdapter;
import com.example.sanchayita.nytimesapp.Listener.EndlessRecyclerViewScrollListener;
import com.example.sanchayita.nytimesapp.Listener.RecyclerViewItemClickSupport;
import com.example.sanchayita.nytimesapp.Model.ArticleItem;
import com.example.sanchayita.nytimesapp.Utils.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String ARTICLE_URL = "ArticleUrl";
    static final String QUERY_KEY = "query key";
    public static String mQuery = "new york times";

    ArrayList<ArticleItem> mArticles;
    ArticleAdapter mAdapter;
    ProgressDialog mProgressDialog;
    EndlessRecyclerViewScrollListener mScrollListener;
    RecyclerView mRecyclerView;
    private NetworkChangeReceiver receiver = null;
    static boolean isConnected = false;
    TextView mTxtView;
    RelativeLayout relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mArticles = new ArrayList<>();
        mProgressDialog = setupProgressDialog();
        mAdapter = new ArticleAdapter(mArticles, this);

        mRecyclerView = findViewById(R.id.recyclerView);
        mTxtView =  findViewById(R.id.text_list_empty);
        relativeLayout = findViewById(R.id.activity_search);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                loadData(page);
            }
        };

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(mScrollListener);

        CheckConn();
        isConnected = Utility.isNetworkAvailable(getApplicationContext());
        //setRetainInstance(true);


        if(savedInstanceState != null ) {
            if (savedInstanceState.containsKey(QUERY_KEY)) {
                mQuery = savedInstanceState.getString(QUERY_KEY);

                loadData(0);

            }
        }


        RecyclerViewItemClickSupport.addTo(mRecyclerView).setOnItemClickListener((recyclerView, position, v) -> {

            if (isConnected) {
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra(ARTICLE_URL, mArticles.get(position).getWebUrl());
                startActivity(intent);
            } else {
                updateEmptyView();
                networkSnackbar();
            }
        });
    }


    public void CheckConn() {

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver() {
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {

                    Bundle extras = intent.getExtras();
                    NetworkInfo info =  extras.getParcelable("networkInfo");
                    NetworkInfo.State state = info.getState();
                    Log.d("TEST Internet", info.toString() + " " + state.toString());

                    if (state == NetworkInfo.State.CONNECTED) {
                        isConnected = true;
                        mTxtView.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);

                    } else {
                        isConnected = false;
                        updateEmptyView();
                        networkSnackbar();
                    }
                }
            }
        };
      registerReceiver(receiver, filter);

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

            savedInstanceState.putString(QUERY_KEY, mQuery);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //  super.onRestoreInstanceState(savedInstanceState);
        mQuery = savedInstanceState.getString(QUERY_KEY);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et =  searchView.findViewById(searchEditId);
        et.setTextColor(Color.BLACK);
        et.setHintTextColor(Color.BLACK);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.clearArticles();
                mArticles.clear();
                mAdapter.notifyDataSetChanged();
                mScrollListener.resetState();

                mQuery = query;
               loadData(0);
                //searchArticle(query, mPage);

                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        return true;
    }


    private ProgressDialog setupProgressDialog() {
        ProgressDialog p =  new ProgressDialog(this);
        p.setIndeterminate(true);
        p.setMessage(getString(R.string.progress_loading));
        return p;
    }

    private void loadData(int page) {

        if (page > 99) {
            return; // limit to 100 pages
        }

          if(isConnected) {
              mTxtView.setVisibility(View.GONE);
             mRecyclerView.setVisibility(View.VISIBLE);


              searchArticle(mQuery, page);
              int curSize = mAdapter.getItemCount();
              if(curSize > 0) {


                  mAdapter.notifyItemRangeInserted(curSize, mArticles.size() - 1);
              }

          }
          else
          {
              updateEmptyView();
              networkSnackbar();
          }

    }


    public void searchArticle(String query , int page){
     mProgressDialog.show();


        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        Log.v("URL: ", url);

        String api_key =  getResources().getString(R.string.nytimes_api_key);

        RequestParams params = new RequestParams();
      //  params.put("api-key", "d31fe793adf546658bd67e2b6a7fd11a");
        params.put("api-key",api_key);
        params.put("page", page);
        params.put("q", query);

        Log.v("Generated URL: ", url);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {


                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");

                    mAdapter.appendArticles(ArticleItem.fromJSONArray(articleJsonResults));

                } catch (JSONException e){
                    e.printStackTrace();
                }

                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response,
                                  Throwable throwable) {
                Log.w("AsyncHttpClient", "HTTP Request failure: " + statusCode + " " +
                        throwable.getMessage());
                mProgressDialog.dismiss();

                if(!isConnected){
                    updateEmptyView();
                    networkSnackbar();
                }
            }


        });

    }


    public void networkSnackbar() {
        final Snackbar snackbar = Snackbar
                .make(relativeLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isConnected) {
                            networkSnackbar();
                        }
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);

        if (!isConnected)
            snackbar.show();
        else
            snackbar.dismiss();
    }


    private void updateEmptyView() {
        int message;
        isConnected = Utility.isNetworkAvailable(getApplicationContext());


        if (!isConnected) {
            mRecyclerView.setVisibility(View.GONE);
            message = R.string.empty_list_no_network;
            mTxtView.setVisibility(View.VISIBLE);
            mTxtView.setText(message);
        }
        else
        {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTxtView.setVisibility(View.GONE);
        }
    }

}
