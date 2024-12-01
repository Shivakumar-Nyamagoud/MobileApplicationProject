package com.example.newsnow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private List<Article> articleList = new ArrayList<>();
    private NewsRecyclerAdapter adapter;
    private LinearProgressIndicator progressIndicator;
    private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7;
    private SearchView searchView;
    private String currentCategory = "GENERAL";
    private final NewsApiClient newsApiClient = new NewsApiClient("0322e0d87076463e90aef813ca1e5616");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupSearchView();
        fetchNews(currentCategory, null);
    }

    /**
     * Initialize all views and set up click listeners.
     */
    private void initViews() {
        recyclerView = findViewById(R.id.news_recycler_view);
        progressIndicator = findViewById(R.id.progress_bar);
        searchView = findViewById(R.id.search_view);

        btn1 = findViewById(R.id.btn_1);
        btn2 = findViewById(R.id.btn_2);
        btn3 = findViewById(R.id.btn_3);
        btn4 = findViewById(R.id.btn_4);
        btn5 = findViewById(R.id.btn_5);
        btn6 = findViewById(R.id.btn_6);
        btn7 = findViewById(R.id.btn_7);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
    }

    /**
     * Set up the RecyclerView with an adapter and layout manager.
     */
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsRecyclerAdapter(articleList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Set up the SearchView to handle search queries.
     */
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchNews(currentCategory, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    /**
     * Show or hide the progress indicator.
     */
    private void setProgressIndicatorVisibility(boolean isVisible) {
        progressIndicator.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Fetch news articles based on the selected category and query.
     */
    private void fetchNews(String category, String query) {
        setProgressIndicatorVisibility(true);

        newsApiClient.getTopHeadlines(
                new TopHeadlinesRequest.Builder()
                        .language("en")
                        .category(category)
                        .q(query)
                        .build(),
                new NewsApiClient.ArticlesResponseCallback() {
                    @Override
                    public void onSuccess(ArticleResponse response) {
                        runOnUiThread(() -> {
                            setProgressIndicatorVisibility(false);
                            articleList.clear(); // Clear the existing list
                            if (response.getArticles() != null) {
                                articleList.addAll(response.getArticles());
                            }
                            adapter.notifyDataSetChanged(); // Notify adapter about changes
                        });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        runOnUiThread(() -> {
                            setProgressIndicatorVisibility(false);
                            Toast.makeText(MainActivity.this, "Failed to fetch news: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                        Log.e("NewsAPI Error", "Error fetching news", throwable);
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button) v;
        currentCategory = btn.getText().toString(); // Update the current category
        fetchNews(currentCategory, null);
    }
}
