const express = require('express');
const Listing = require('../models/listingModel');
const User = require('../models/userModel');
// const { authenticateJWT } = require('../authentication/jwtAuthentication');
const { SCAM_THRESHOLD } = require('../utils/constants');
const router = express.Router();

/**
 * Get a Listing from Listing Collection by id
 *
 * Route: GET /api/listings/:listingId where listingId is the ID of the listing in the database
 */
router.get('/:listingId', async (req, res, next) => {
    const listing = await Listing.findById(req.params.listingId);
    if (listing) {
        res.status(200).json(listing);
    } else {
        res.status(404).json({ error: 'Listing not found' });
    }
});

/**
 * Get all Listings under a given userId
 *
 * Route: GET /api/listings/user/:userId where userId is the ID of the user
 */
router.get('/user/:userId', async (req, res, next) => {
    let listings = await Listing.find({ userId: req.params.userId });
    if (listings) {
        res.status(200).json(listings);
    } else {
        res.status(404).json({ error: 'No listing found for given user ID' });
    }
});

/**
 * Create a new Listing and save it on the database. This demands that the listingID
 * information is provided in the body of the request as shown below.
 *
 * Route: POST /api/listings
 *
 * Body: {userID: String, title: String .... images: [String]}
 */
router.post('/', async (req, res, next) => {
    const user = await User.findById(req.body.userId);
    if(user){
        const listing = new Listing(req.body);
        await listing.save();
        res.location(`/api/listing/${listing._id}`);
        res.status(201).json(listing);
    } else {
        res.status(404).json({ error: 'Incorrect user ID provided' });
    }
});

/**
 * Update an existing Listing and save it on the database. This demands that for the fields
 * that need to be updated, information is provided in the body of the request as shown below.
 *
 * Route: PUT /api/listings/:listingId
 *
 * Body: {title: String <new_title>, rentalPrice: Number<new_price> ....}
 */
router.put('/:listingId', async (req, res, next) => {
    const updatedListing = await Listing.findByIdAndUpdate(req.params.listingId, req.body, { new: true });
    if (updatedListing) {
        res.status(200).json(updatedListing);
    } else {
        res.status(404).json({ error: 'Listing not found' });
    }
});

/**
 * Report a specific listing as scam. As a side effect, if a listing is reported by more than BLOCK_THRESHOLD number
 * of users, then the user account and all relevant information for this user will be deleted.
 *
 * Route: POST /api/listings/:listingId/report
 *
 * Body: {reporterId: ""}
 */
router.post('/:listingId/report', async (req, res, next) => {
    // Either both updates should occur or neither should so first try to get both documents
    let currentUser = await User.findById(req.body.reporterId);
    let reportedListing = await Listing.findById(req.params.listingId);

    if (currentUser && reportedListing) {
        // If both objects are non-null can update both safely
        await User.findByIdAndUpdate(
            req.body.reporterId,
            { $push: { reportedScam: req.params.listingId } },
            { new: true },
        );
        let updatedListing = await Listing.findByIdAndUpdate(
            req.params.listingId,
            { $inc: { scamReportCount: 1 } },
            { new: true },
        );
        if (updatedListing.scamReportCount >= SCAM_THRESHOLD) {
            await Listing.findByIdAndDelete(req.params.listingId);
        }
        res.status(200).json({ message: 'Listing reported successfully!' });
    } else {
        res.status(404).json({ error: 'Listing reporting failed!' });
    }
});

/**
 * Delete a listing from the database based on the listingID
 *
 * Route: DELETE /api/listings/:listingId where listingId is the ID of the listing in the database
 */
router.delete('/:listingId', async (req, res, next) => {
    const deletedListing = await Listing.findByIdAndDelete(req.params.listingId);
    if (deletedListing) {
        res.status(200).json(deletedListing);
    } else {
        res.status(404).json({ error: 'Listing not found' });
    }
});

module.exports = router;
