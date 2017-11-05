package com.project.MovieTime;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NowPlayingActivity extends AppCompatActivity {


    //Variables for the recycler view
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private List<Movie> movieList;
    private String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nowplaying);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();    //Creating collapsing toolbar.


        //Getting the recycler view from the layout.
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        //Creating a new movie list..
        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movieList);

            //Making the recycler view a width of 2.
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        //Grid spacing for the cards
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        //Giving the cards the default build in animation.
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //Giving the recycler view the movies
        recyclerView.setAdapter(movieAdapter);

        //Preparing the movies from the Webservice
        prepareMovies();

        //Giving the toolbar background an image.
        try {
            Glide.with(this).load(R.drawable.cover).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Method that created the collapsing toolbar.
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    //Getting the movies.
    public void prepareMovies() {

        //Making a new JsonTask used to get the webservice and build objects
        JsonTask jsonTask = new JsonTask();

        //Giving the URL to the webservice.
        jsonTask.execute("http://api.themoviedb.org/3/movie/now_playing?api_key=14ba62e6605d5d58484c75389bc924be&language=en-US&page=1");

        //movieAdapter.notifyDataSetChanged();
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration { // method for syling the Card views and spacing. https://www.androidhive.info/2016/05/android-working-with-card-view-and-recycler-view/

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }
        //https://www.androidhive.info/2016/05/android-working-with-card-view-and-recycler-view/
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }


    private int dpToPx(int dp) {        //Method that will convert DP to PX.
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    //Asyc Task that will take the URL.
    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            String result = getJson (params[0]);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            movieAdapter.notifyDataSetChanged(); //Telling the adapter that there is a new list of movies.
        }
    }

    public String getJson(String urlString){        //Passing in the URL.
        String jsonString = "";
        HttpURLConnection movieConnection = null;
        try {
            URL url = new URL(urlString);
            movieConnection = (HttpURLConnection) url.openConnection(); //Opening the connection to the API.

            BufferedReader reader = new BufferedReader(new InputStreamReader(movieConnection.getInputStream())); //Reding the JSON
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line+"\n"); //Building the String that will be used to make the JSON
            }
            reader.close();
            jsonString = builder.toString();

            JSONObject jsonObj = new JSONObject(jsonString.toString()); //Making a JSONObject.

            JSONArray jsonArray = jsonObj.getJSONArray("results"); //Making the object into an Array of JSON

            for(int i =0; i< jsonArray.length(); i++){

                Gson gson = new Gson();         //Going through each object and mapping it to the Moview Class.
                Movie movie = gson.fromJson(jsonArray.getJSONObject(i).toString(), Movie.class);

                movieList.add(movie); //Adding the movie to the list, to be displayed.

                }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally{
            movieConnection.disconnect();
        }

        return jsonString; //Returning the string.
    }
}
