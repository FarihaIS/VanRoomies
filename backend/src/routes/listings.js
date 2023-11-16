const express = require('express');
const { default: mongoose } = require('mongoose');
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
 * Body: {listerID: String, title: String .... images: [String]}
 */
router.post('/', async (req, res, next) => {
    const listing = new Listing(req.body);
    await listing.save();
    res.location(`/api/listing/${listing._id}`);
    res.status(201).json(listing);
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
    // Wrap inside transaction, either all occur or neither one does - atomicity
    let currentUser;
    let reportedListing;
    const session = await mongoose.startSession();
    session.startTransaction();

    // Add list to the set of reported listings for given user
    currentUser = await User.updateOne(
        { _id: req.body.reporterId },
        { $push: { reportedScam: req.params.listingId } },
        { new: true },
    );
    
    // Update listing counter for blocking
    reportedListing = await Listing.updateOne(
        { _id: req.body.listingId },
        { $inc: { scamReportCount: 1 } },
        { new: true },
    );
    
    // Delete listing if its count of scam reports exceeds the threshold
    if (reportedListing && reportedListing.scamReportCount >= SCAM_THRESHOLD){
        // Error handling should not happen here considering we already know the listing exists
        await Listing.findByIdAndDelete(req.params.listingId);
    }

    await session.commitTransaction();
    if (currentUser && reportedListing) {
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
