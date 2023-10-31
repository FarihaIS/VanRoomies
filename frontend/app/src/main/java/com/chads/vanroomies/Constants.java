package com.chads.vanroomies;

public class Constants {
    // Endpoints
    final static String baseServerURL = "https://10.0.2.2:3000";
    // final static String baseServerURL = "Insert VM Server URL";
    final static String helloWorldEndpoint = "/"; // GET
    final static String listingByUserIdEndpoint = "/api/listings/user/"; // GET, needs user_id appended
    final static String listingByListingIdEndpoint = "/api/listings/"; // GET/PUT, needs listing_id appended. POST does not.
    final static String userEndpoint = "/api/users/"; // GET/PUT/DELETE needs user_id appended. POST does not.
    final static String chatsByUserIdEndpoint = "/api/chat/conversations/user/"; // GET needs user_id appended.
    final static String matchesByUserIdEndpoint = "/recommendations/users"; // GET needs user_id appended beforehand. POST needs request body.

    // Functions
    public static String listingByRecommendationsEndpoint(String user_id) { return String.format("/api/users/%s/recommendations/listings", user_id); } // GET, needs userId
    public static String userPreferencesEndpoint(String user_id) { return String.format("/api/users/%s/preferences", user_id);} // GET/POST/PUT, need userId

    // Default chat message
    final static String defaultFirstMessage = "Hello!";
    final static String loginEndpoint = "/api/users/login"; // POST

    // Authentication
    final static String clientId = "49876246970-nvcs6gekl0ng450q1ujb4dfmhcufu3tp.apps.googleusercontent.com";
    final static String userData = "userData";
    final static String userIdKey = "userId";
    final static String userTokenKey = "userToken";
}
