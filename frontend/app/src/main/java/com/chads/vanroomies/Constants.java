package com.chads.vanroomies;

public class Constants {
    // Endpoints
    final static String baseServerURL = "https://10.0.2.2:3000";
    // final static String baseServerURL = "Insert VM Server URL";
    final static String helloWorldEndpoint = "/"; // GET
    final static String listingByUserIdEndpoint = "/api/listings/user/"; // GET, needs user_id appended
    final static String listingByListingIdEndpoint = "/api/listings/"; // GET/PUT, needs listing_id appended


}
