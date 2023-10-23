const express = require('express');
var mongoose = require('mongoose');
const Preferences = require("../models/preferencesModel");
const router = express.Router();

/**
 * Get Preferences Object from Preferences Collection by Id of respective user
 * 
 * Route: GET /api/users/:userId/preferences where userId is the ID of user
 */
router.get('/:userId/preferences', async (req, res, next) => {
    try {
        const preferences = await Preferences.findOne({userID: req.params.userId});
        if(preferences){
            res.status(200).json(preferences);
        }else{
            res.status(404).json({error: "Did not match any user!"});
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
router.post('/:userId/preferences', async (req, res) => {
    const preferenceData = {"userID": new mongoose.Types.ObjectId(req.params.userId.trim()), ...req.body};
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
router.put('/:userId/preferences', async (req, res) => {
    try {
        const userId = new mongoose.Types.ObjectId(req.params.userId);
        const updatedPreferences = await Preferences.findOneAndUpdate({userID: userId}, req.body, {new: true});
        if(updatedPreferences){
            res.status(200).json(updatedPreferences);
        }else{
            res.status(404).json({error: "Did not match any user!"});
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
    // TODO: Implement recommendation algorithm here
    res.send("I will give you recommendations for user matches here");
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
    // TODO: Implement recommendation algorithm here
    res.send("I will give you recommendations for best fit housing options");
});

module.exports = router;
