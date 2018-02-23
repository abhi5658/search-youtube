package com.test.abhishek.searchyoutube;

/**
 * Created by Abhishek on 14-Feb-18.
 */

//Bean class for a single video item
public class VideoItem {
    
    //stores id of a video
    private String id;

    //stores title of the video
    private String title;

    //stores the description of video
    private String description;

    //stores the url of thumbnail of video
    private String thumbnailURL;


    //getter and setter methods for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    //getter and setter methods for video Title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    //getter and setter methods for video description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    //getter and setter methods for thumbnail url
    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnail) {
        this.thumbnailURL = thumbnail;
    }

}
