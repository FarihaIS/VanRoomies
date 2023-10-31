const express = require('express');
var mongoose = require('mongoose');
const Preferences = require('../models/preferencesModel');
const User = require('../models/userModel');
const Listing = require('../models/listingModel');
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
    try {
        const preferences = await Preferences.findOne({ userId: req.params.userId });
        if (preferences) {
            res.status(200).json(preferences);
        } else {
            res.status(404).json({ error: 'Did not match any user!' });
        }
    } catch (error) {
        res.status(400).json({ error: error.message });
        next(error);
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
    const preferenceData = { userId: new mongoose.Types.ObjectId(req.params.userId.trim()), ...req.body };
    const preferences = new Preferences(preferenceData);
    try {
        await preferences.save();
        res.status(201).json(preferences);
    } catch (error) {
        res.status(400).json({ error: error.message });
        next(error);
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
    try {
        const userId = new mongoose.Types.ObjectId(req.params.userId);
        const updatedPreferences = await Preferences.findOneAndUpdate({ userId: userId }, req.body, { new: true });
        if (updatedPreferences) {
            res.status(200).json(updatedPreferences);
        } else {
            res.status(404).json({ error: 'Did not match any user!' });
        }
    } catch (error) {
        res.status(400).json({ error: error.message });
        next(error);
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
    try {
        const userPreferences = await Preferences.findOne({ userId: req.params.userId }).lean();
        if (!userPreferences) {
            let error = 'No preferences found for given user!';
            res.status(404).json({ error: error });
            next(new Error(error));
        }

        const tentativeMatchPreferences = await Preferences.find({ userId: { $ne: req.params.userId } }).lean();
        if (tentativeMatchPreferences) {
            let scores = generateUserScores(userPreferences, tentativeMatchPreferences);

            // TODO: This REQUIRES optimization for further milestones - too transactionally-heavy
            let rankedUsers = [];
            for (const id of generateRecommendations(scores)) {
                const currUser = await User.findById(id).lean();
                rankedUsers.push(currUser);
            }
            res.status(200).json(rankedUsers);
        } else {
            let error = 'No preferences found for given user!';
            res.status(404).json({ error: error });
            next(new Error(error));
        }
    } catch (error) {
        res.status(400).json({ error: error.message });
        next(error);
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
    try {
        const userPreferences = await Preferences.findOne({ userId: req.params.userId }).lean();
        if (!userPreferences) {
            let error = 'No preferences found for given user!';
            res.status(404).json({ error: error });
            next(new Error(error));
        }

        const tentativeMatchListings = await Listing.find({ userId: { $ne: req.params.userId } }).lean();
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
            let error = 'No matching listings available!';
            res.status(404).json({ error: error });
            next(new Error(error));
        }
    } catch (error) {
        res.status(400).json({ error: error.message });
        next(error);
    }
});

module.exports = router;
