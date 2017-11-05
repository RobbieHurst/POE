package com.example.robert.questiontwo;

/**
 * Created by robert on 2017/08/27.
 */
public class Review {

    private String Id;
    private String message;
    private double latitude;
    private double longitude;
    private float rating;

    /**
     * Instantiates a new Review.
     */
    public Review(){

    }

    /**
     * Instantiates a new Review.
     *
     * @param Id        the id
     * @param message   the message
     * @param latitude  the latitude
     * @param longitude the longitude
     * @param rating    the rating
     */
    public Review(String Id, String message, double latitude, double longitude, float rating){

        this.Id = Id;
        this.message = message;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;

    }


    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets latitude.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets latitude.
     *
     * @param latitude the latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets longitude.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets longitude.
     *
     * @param longitude the longitude
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets rating.
     *
     * @return the rating
     */
    public double getRating() {
        return rating;
    }

    /**
     * Sets rating.
     *
     * @param rating the rating
     */
    public void setRating(float rating) {
        this.rating = rating;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getID() {
        return Id;
    }

    /**
     * Sets id.
     *
     * @param Id the id
     */
    public void setID(String Id) {
        this.Id = Id;
    }
}
