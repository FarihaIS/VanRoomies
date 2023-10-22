const express = require('express');
const Preferences = require("../models/preferencesModel");
const router = express.Router();

/**
 * Get Preferences Object from Preferences Collection by Id of respective user
 * 
 * Route: GET /api/user/:userId/preferences where userId is the ID of user
 */
router.get('/:userId/preferences', async (req, res) => {
    try {
        const preferences = await Preferences.findOne({userId: req.params.userId});
        if(preferences){
            res.status(200).json(preferences);
        }else{
            res.status(404).send();
        }
    } catch (error) {
        console.log(error);
        res.status(500).json({ error: 'Internal Server Error' });
    }
});

/**
 * Create a new Preferences object and save it on the database. This demands that the userId
 * information is provided in the body of the request as shown below.
 * 
 * Route: POST /api/user/:userId
 * 
 * Body: {userId: ObjectId, minPrice: Number, maxPrice: Number ....}
 */
router.post('/:userId', async (req, res) => {
    const preferences = new Preferences(req.body);
    try {
        // TODO: Need to figure out path here, gets a little confusing with the user in there
        await preferences.save();
        res.location(`/api/listing/${preferences._id}`);
        res.status(201).json(listing);
    } catch (error) {
        console.log(error);
        res.status(500).json({ error: 'Internal Server Error' });
    }
});


/**
 * Update an existing Preferences object and save changes on the database. This demands that for the fields 
 * that need to be updated, information is provided in the body of the request as shown below.
 * 
 * Route: PUT /api/user/:userId
 * 
 * Body: {title: String <new_title>, rentalPrice: Number<new_price> ....}
 */
router.put('/:userId', async (req, res) => {
    try {
        const updatedListing = await Preferences.findOneAndUpdate({userId: req.params.userId}, req.body, {new: true});
        if(updatedListing){
            res.status(200).json(updatedListing);
        }else{
            res.status(404).send();
        }
    } catch (error) {
        console.log(error);
        res.status(500).json({ error: 'Internal Server Error' });
    }
});

/**
 * Recommend a list of best roommate matches for the user noted by userId based on their set of preferences.
 * Might permit for filters here and those will go through the body of the request.
 * 
 * Route: GET /api/user/:userId/recommendations/users
 * 
 * Body: {....filters???}
 */
router.get('/:userId/recommendations/users', async (req, res) => {
    // TODO: Implement recommendation algorithm here
    res.send("I will give you recommendations for user matches here");
});

/**
 * Recommend a list of best housing matches for the user noted by userId based on their set of preferences.
 * Might permit for filters here and those will go through the body of the request.
 * 
 * Route: GET /api/user/:userId/recommendations/listings
 * 
 * Body: {....filters???}
 */
router.get('/:userId/recommendations/listings', async (req, res) => {
    // TODO: Implement recommendation algorithm here
    res.send("I will give you recommendations for best fit housing options");
});

module.exports = router;
