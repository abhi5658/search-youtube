package com.test.abhishek.searchyoutube;


import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import android.os.Handler;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    //input text bar
    private EditText searchInput;
    //Adapter class for filling recycler view
    private YoutubeAdapter youtubeAdapter;
    //list of searched videos
    private RecyclerView mRecyclerView;
    //progress to show while loading
    private ProgressDialog mProgressDialog;
    //handler for filling view
    private Handler handler;
    //results list after searching
    private List<VideoItem> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initailising the objects
        mProgressDialog = new ProgressDialog(this);
        searchInput = (EditText)findViewById(R.id.search_input);
        mRecyclerView = (RecyclerView) findViewById(R.id.videos_recycler_view);

        //setting title and and style for progress
        mProgressDialog.setTitle("Searching...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        //setting manager and size for recycler view
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        handler = new Handler();

        //setting listener for search button pressed
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //checking search action
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    
                    //setting progress message to show and activation
                    mProgressDialog.setMessage("Finding videos for "+v.getText().toString());

                    //showing downloading data from server
                    mProgressDialog.show();

                    //calling out search method with input text
                    searchOnYoutube(v.getText().toString());

                    //hiding the keyboard once search button is clicked
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);

                    return false;
                }
                return true;
            }
        });

    }

    //the search method
    private void searchOnYoutube(final String keywords){
        //starting new thread
        new Thread(){
            public void run(){
                //initialising YoutubeConnector object
                YoutubeConnector yc = new YoutubeConnector(MainActivity.this);
                
                //calling the search method and saving the results in Youtube Search List
                searchResults = yc.search(keywords);

                handler.post(new Runnable(){
                    public void run(){
                        //filling the card views
                        fillYoutubeVideos();
                        //once done hiding the progress
                        mProgressDialog.dismiss();
                    }
                });
            }
        }.start();
    }

    //method for creating adapter and setting it to recycler view
    private void fillYoutubeVideos(){
        youtubeAdapter = new YoutubeAdapter(getApplicationContext(),searchResults);
        mRecyclerView.setAdapter(youtubeAdapter);
        youtubeAdapter.notifyDataSetChanged();
    }
}