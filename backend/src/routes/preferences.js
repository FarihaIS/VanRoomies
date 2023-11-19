const express = require('express');
const Preferences = require('../models/preferencesModel');
const User = require('../models/userModel');
const Listing = require('../models/listingModel');
// const { authenticateJWT } = require('../authentication/jwtAuthentication');
const router = express.Router();
const { generateUserScores } = require('../utils/userRecommendations');
const { generateListingScores } = require('../utils/listingRecommendations');
const { generateRecommendations } = require('../utils/utils');

/**
 * Get Preferences Object from Preferences Collection by Id of respective user
 *
 * Route: GET /api/users/:userId/preferences where userId is the ID of user
 */
router.get('/:userId/preferences', async (req, res, next) => {
    const preferences = await Preferences.findOne({ userId: req.params.userId });
    if (preferences) {
        res.status(200).json(preferences);
    } else {
        res.status(404).json({ error: 'Did not match any user!' });
    }
});

/**
 * Create a new Preferences object and save it on the database. This demands that the userId
 * information is provided in the body of the request as shown below.
 *
 * Route: POST /api/users/:userId/preferences where userId is the ID of user
 *
 * Body: {userId: ObjectId, minPrice: Number, maxPrice: Number ....}
 */
router.post('/:userId/preferences', async (req, res, next) => {
    const user = await User.findById(req.params.userId);
    if(user){
        const preferenceData = { userId: req.params.userId, ...req.body };
        const preferences = new Preferences(preferenceData);
        await preferences.save();
        res.status(201).json(preferences);
    }else{
        res.status(404).json({ error: 'Did not match any user!' });
    }
});

/**
 * Update an existing Preferences object and save changes on the database. This demands that for the fields
 * that need to be updated, information is provided in the body of the request as shown below.
 *
 * Route: PUT /api/users/:userId/preferences where userId is the ID of user
 *
 * Body: {title: String <new_title>, rentalPrice: Number<new_price> ....}
 */
router.put('/:userId/preferences', async (req, res, next) => {
    const userId = req.params.userId;
    const updatedPreferences = await Preferences.findOneAndUpdate({ userId }, req.body, { new: true });
    if (updatedPreferences) {
        res.status(200).json(updatedPreferences);
    } else {
        res.status(404).json({ error: 'Did not match any user!' });
    }
});

/**
 * Recommend a list of best roommate matches for the user noted by userId based on their set of preferences.
 * Might permit for filters here and those will go through the body of the request.
 *
 * Route: GET /api/users/:userId/recommendations/users
 *
 * Body: {....filters???}
 */
router.get('/:userId/recommendations/users', async (req, res, next) => {
    const userPreferences = await Preferences.findOne({ userId: req.params.userId }).lean();
    if (!userPreferences) {
        let error = 'No preferences found for given user!';
        return res.status(404).json({ error });
    }
    const currUser = await User.findById(req.params.userId);
    const excluded = [req.params.userId, ...currUser.notRecommended];
    const tentativeMatchPreferences = await Preferences.find({ userId: { $nin: excluded } }).lean();
    if (tentativeMatchPreferences) {
        let scores = generateUserScores(userPreferences, tentativeMatchPreferences);

        // TODO: This REQUIRES optimization for further milestones - too transactionally-heavy
        let rankedUsers = [];
        for (const id of generateRecommendations(scores)) {
            const currUser = await User.findById(id).select('firstName lastName profilePicture bio').lean();
            rankedUsers.push(currUser);
        }
        res.status(200).json(rankedUsers);
    } else {
        let error = 'No preferences found for given user!';
        res.status(404).json({ error });
    }
});

/**
 * This endpoint pushes a new userId on the list of excluded users that are
 * not to be recommended for a given user via the recommendation routes
 *
 * Route: PUT /api/users/:userId/recommendations/users
 *
 * Body: {excludedId: ""}
 */
router.put('/:userId/recommendations/users', async (req, res, next) => {
    // Get both users first, and only proceed if both are found
    let currentUser = await User.findById(req.params.userId);
    let matchUser = await User.findById(req.body.excludedId);
    
    if (currentUser && matchUser){
        let updatedUser = await User.findByIdAndUpdate(
            req.params.userId,
            { $push: { notRecommended: req.body.excludedId } },
            { new: true },
        );

        await User.findByIdAndUpdate(
            req.body.excludedId,
            { $push: { notRecommended: req.params.userId } },
            { new: true },
        );
        res.status(200).json(updatedUser);
    } else {
        res.status(404).json({ error: 'Incorrect provided user ID' });
    }
});

/**
 * Recommend a list of best housing matches for the user noted by userId based on their set of preferences.
 * Might permit for filters here and those will go through the body of the request.
 *
 * Route: GET /api/users/:userId/recommendations/listings
 *
 * Body: {....filters???}
 */
router.get('/:userId/recommendations/listings', async (req, res, next) => {
    const userPreferences = await Preferences.findOne({ userId: req.params.userId }).lean();
    if (!userPreferences) {
        return res.status(404).json({ error: 'No preferences found for given user!' });
    }
    const loggedInUser = await User.findById(req.params.userId);
    const tentativeMatchListings = await Listing.find({
        userId: { $ne: req.params.userId },
        _id: { $nin: loggedInUser.reportedScam },
    }).lean();
    if (tentativeMatchListings) {
        let scores = generateListingScores(userPreferences, tentativeMatchListings);

        // TODO: This REQUIRES optimization for further milestones - too transactionally-heavy
        let rankedListings = [];
        for (const id of generateRecommendations(scores)) {
            const currListing = await Listing.findById(id).lean();
            rankedListings.push(currListing);
        }
        res.status(200).json(rankedListings);
    } else {
        res.status(404).json({ error: 'No matching listings available!' });
    }
});

module.exports = router;
