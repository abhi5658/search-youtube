package com.test.abhishek.searchyoutube;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;



/**
 * Created by Abhishek on 14-Feb-18.
 */

public class YoutubeConnector {

    //youtube object for executing api related queries
    private YouTube youtube;
    
    //custom list of youtube which gets returned when searched for keyword
    private YouTube.Search.List query;

    //Developer API key
    public static final String KEY = "API_KEY_FROM_CONSOLE";

    //Package name of the app that will call the API
    public static final String PACKAGENAME = "com.test.abhishek.searchyoutube";
    
    //SHA1 fingerprint of APP
    public static final String SHA1 = "SHA1_FINGERPRINT";
    
    //maximum results that should be downloaded at a time
    private static final long MAXRESULTS = 25;

    public YoutubeConnector(Context context) {


        //instantiate the youtube object with builder
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {

                //setting package name and sha1 certificate to identify request by server
                request.getHeaders().set("X-Android-Package", PACKAGENAME);
                request.getHeaders().set("X-Android-Cert",SHA1);
            }
        }).setApplicationName("SearchYoutube").build();

        try {
            query = youtube.search().list("id,snippet");

            //setting API key to query
            query.setKey(KEY);

            //setting type to video so that only videos are returned
            query.setType("video");

            //setting fields which should be returned
            //setting only those fields which are required
            //for maximum efficiency
            query.setFields("items(id/kind,id/videoId,snippet/title,snippet/description,snippet/thumbnails/high/url)");

        } catch (IOException e) {
            //printing stack trace if error occurs
            Log.d("YC", "Could not initialize: " + e);
        }
    }

    public List<VideoItem> search(String keywords) {

        //setting keyword to query
        query.setQ(keywords);

        //max results that should be returned
        query.setMaxResults(MAXRESULTS);

        try {
            //executing prepared query
            SearchListResponse response = query.execute();

            //retrieving list from response received
            List<SearchResult> results = response.getItems();

            //list of type VideoItem to save all data individually
            List<VideoItem> items = new ArrayList<VideoItem>();

            if (results != null) {
                items = setItemsList(results.iterator());
            }

            return items;

        } catch (IOException e) {
            Log.d("YC", "Could not search: " + e);
            return null;
        }
    }

    //method for filling array list
    private static List<VideoItem> setItemsList(Iterator<SearchResult> iteratorSearchResults) {

        //temporary list
        List<VideoItem> tempSetItems = new ArrayList<>();

        //if no result then printing no results
        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        //iterating through all search results
        while (iteratorSearchResults.hasNext()) {

            //current video item
            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {

                //object of VideoItem class that can be added to array list
                VideoItem item = new VideoItem();

                //getting High quality thumbnail object
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getHigh();

                //retrieving title,description,thumbnail url, id
                item.setTitle(singleVideo.getSnippet().getTitle());
                item.setDescription(singleVideo.getSnippet().getDescription());
                item.setThumbnailURL(thumbnail.getUrl());
                item.setId(singleVideo.getId().getVideoId());

                //adding item to temp array list
                tempSetItems.add(item);

                //for debug purpose
                System.out.println(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println(" Description: "+ singleVideo.getSnippet().getDescription());
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }
        return tempSetItems;
    }
}