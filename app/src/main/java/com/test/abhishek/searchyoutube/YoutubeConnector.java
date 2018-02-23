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

	    //Youtube object for executing api related queries through Youtube Data API
		private YouTube youtube;

	    //custom list of youtube which gets returned when searched for keyword
	    //Returns a collection of search results that match the query parameters specified in the API request
	    //By default, a search result set identifies matching video, channel, and playlist resources,
	    //but you can also configure queries to only retrieve a specific type of resource
		private YouTube.Search.List query;

	    //Developer API key a developer can obtain after creating a new project in google developer console
	    //Developer has to enable YouTube Data API v3 in the project
	    //Add credentials and then provide the Application's package name and SHA fingerprint
		public static final String KEY = "API_KEY_FROM_GOOGLE_CLOUD_CONSOLE";

	    //Package name of the app that will call the YouTube Data API
		public static final String PACKAGENAME = "com.test.abhishek.searchyoutube";

	    //SHA1 fingerprint of APP can be found by double clicking on the app signing report on right tab called gradle
		public static final String SHA1 = "SHA1_FINGERPRINT_STRING";

	    //maximum results that should be downloaded via the YouTube data API at a time
		private static final long MAXRESULTS = 25;

	    //Constructor to properly initialize Youtube's object
		public YoutubeConnector(Context context) {

	        //Youtube.Builder returns an instance of a new builder
	        //Parameters:
	        //transport - HTTP transport
	        //jsonFactory - JSON factory
	        //httpRequestInitializer - HTTP request initializer or null for none
	        // This object is used to make YouTube Data API requests. The last
			// argument is required, but since we don't need anything
			// initialized when the HttpRequest is initialized, we override
			// the interface and provide a no-op function.
			youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {

				//initialize method helps to add any extra details that may be required to process the query
				@Override
				public void initialize(HttpRequest request) throws IOException {

	                //setting package name and sha1 certificate to identify request by server
					request.getHeaders().set("X-Android-Package", PACKAGENAME);
					request.getHeaders().set("X-Android-Cert",SHA1);
				}
			}).setApplicationName("SearchYoutube").build();

			try {

				// Define the API request for retrieving search results.
				query = youtube.search().list("id,snippet");

	            //setting API key to query
	            // Set your developer key from the {{ Google Cloud Console }} for
            	// non-authenticated requests. See:
            	// {{ https://cloud.google.com/console }}
				query.setKey(KEY);

	            // Restrict the search results to only include videos. See:
            	// https://developers.google.com/youtube/v3/docs/search/list#type
				query.setType("video");

	            //setting fields which should be returned
	            //setting only those fields which are required
	            //for maximum efficiency
	            //here we are retreiving fiels:
	            //-kind of video
	            //-video ID
	            //-title of video
	            //-description of video
	            //high quality thumbnail url of the video
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

	            //executing prepared query and calling Youtube API
				SearchListResponse response = query.execute();

	            //retrieving list from response received
	            //getItems method returns a list from the response which is originally in the form of JSON
				List<SearchResult> results = response.getItems();

	            //list of type VideoItem for saving all data individually
				List<VideoItem> items = new ArrayList<VideoItem>();

				//check if result is found and call our setItemsList method
				if (results != null) {

					//iterator method returns a Iterator instance which can be used to iterate through all values in list
					items = setItemsList(results.iterator());
				}

				return items;

			} catch (IOException e) {

				//catch exception and print on console
				Log.d("YC", "Could not search: " + e);
				return null;
			}
		}

	    //method for filling our array list
		private static List<VideoItem> setItemsList(Iterator<SearchResult> iteratorSearchResults) {

	        //temporary list to store the raw data from the returned results
			List<VideoItem> tempSetItems = new ArrayList<>();

	        //if no result then printing appropriate output
			if (!iteratorSearchResults.hasNext()) {
				System.out.println(" There aren't any results for your query.");
			}

	        //iterating through all search results
	        //hasNext() method returns true until it has no elements left to iterate
			while (iteratorSearchResults.hasNext()) {

	            //next() method returns single instance of current video item
	            //and returns next item everytime it is called
	            //SearchResult is Youtube's custom result type which can be used to retrieve data of each video item
				SearchResult singleVideo = iteratorSearchResults.next();

				//getId() method returns the resource ID of one video in the result obtained
				ResourceId rId = singleVideo.getId();

	            // Confirm that the result represents a video. Otherwise, the
	            // item will not contain a video ID.
	            //getKind() returns which type of resource it is which can be video, playlist or channel
				if (rId.getKind().equals("youtube#video")) {

	                //object of VideoItem class that can be added to array list
					VideoItem item = new VideoItem();

	                //getting High quality thumbnail object
	                //URL of thumbnail is in the heirarchy snippet/thumbnails/high/url
					Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getHigh();

	                //retrieving title,description,thumbnail url, id from the heirarchy of each resource
	                //Video ID - id/videoId
	                //Title - snippet/title
	                //Description - snippet/description
	                //Thumbnail - snippet/thumbnails/high/url
					item.setId(singleVideo.getId().getVideoId());
					item.setTitle(singleVideo.getSnippet().getTitle());
					item.setDescription(singleVideo.getSnippet().getDescription());
					item.setThumbnailURL(thumbnail.getUrl());

	                //adding one Video item to temporary array list
					tempSetItems.add(item);

	                //for debug purpose printing one by one details of each Video that was found
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