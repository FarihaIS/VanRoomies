package com.chads.vanroomies;

public class Constants {
    // Endpoints
    final static String baseServerURL = "https://20.121.136.167:3000";
    final static String helloWorldEndpoint = "/"; // GET
    final static String listingByUserIdEndpoint = "/api/listings/user/"; // GET, needs user_id appended
    final static String listingByListingIdEndpoint = "/api/listings/"; // GET/PUT, needs listing_id appended. POST does not.
    final static String userEndpoint = "/api/users/"; // GET/PUT/DELETE needs user_id appended. POST does not.
    final static String chatsByUserIdEndpoint = "/api/chat/conversations/user/"; // GET needs user_id appended.
    final static String blockByUserIdEndpoint = "/block"; // POST needs user_id appended beforehand and request body.
    final static String matchesByUserIdEndpoint = "/recommendations/users"; // GET needs user_id appended beforehand. POST needs request body.
    final static String unmatchByUserIdEndpoint = "/unmatch"; // POST needs user_id appended beforehand and request body.
    final static String loginEndpoint = "/api/users/login"; // POST
    final static String firebaseTokenEndpoint = "/api/firebase_token";

    // Default chat message
    final static String defaultFirstMessage = "Hello!";
    final static String privateMessageEvent = "private message";

    // Authentication
    final static String clientId = "49876246970-nvcs6gekl0ng450q1ujb4dfmhcufu3tp.apps.googleusercontent.com";
    final static String userData = "userData";
    final static String userIdKey = "userId";
    final static String userTokenKey = "userToken";
    final static String userDefault = "DEFAULT";
    final static String testEmail = "bayleefshoo";
    final static String testPassword = "TeamChadsBurner123@";

    // Verification
    final static String base64Regex = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$";


    // Functions
    public static String listingByRecommendationsEndpoint(String user_id) { return String.format("/api/users/%s/recommendations/listings", user_id); } // GET, needs userId
    public static String userPreferencesEndpoint(String user_id) { return String.format("/api/users/%s/preferences", user_id);} // GET/POST/PUT, need userId
    public static String reportListingEndpoint(String listing_id) { return String.format("/api/listings/%s/report", listing_id);} // POST, needs listing_id.

}
