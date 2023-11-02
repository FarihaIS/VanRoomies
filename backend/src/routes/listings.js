const express = require('express');
const Listing = require('../models/listingModel');
// const { authenticateJWT } = require('../authentication/jwtAuthentication');
const router = express.Router();

/**
 * Get a Listing from Listing Collection by id
 *
 * Route: GET /api/listings/:listingId where listingId is the ID of the listing in the database
 */
router.get('/:listingId', async (req, res, next) => {
    try {
        const listing = await Listing.findById(req.params.listingId);
        if (listing) {
            res.status(200).json(listing);
        } else {
            res.status(404).json({ error: 'Listing not found' });
        }
    } catch (error) {
        next(error);
    }
});

/**
 * Get all Listings under a given userId
 *
 * Route: GET /api/listings/user/:userId where userId is the ID of the user
 */
router.get('/user/:userId', async (req, res, next) => {
    try {
        let listings = await Listing.find({ userId: req.params.userId });
        if (listings) {
            res.status(200).json(listings);
        } else {
            res.status(404).json({ error: 'No listing found for given user ID' });
        }
    } catch (error) {
        next(error);
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
    try {
        await listing.save();
        res.location(`/api/listing/${listing._id}`);
        res.status(201).json(listing);
    } catch (error) {
        next(error);
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
    try {
        const updatedListing = await Listing.findByIdAndUpdate(req.params.listingId, req.body, { new: true });
        if (updatedListing) {
            res.status(200).json(updatedListing);
        } else {
            res.status(404).json({ error: 'Listing not found' });
        }
    } catch (error) {
        next(error);
    }
});

/**
 * Delete a listing from the database based on the listingID
 *
 * Route: DELETE /api/listings/:listingId where listingId is the ID of the listing in the database
 */
router.delete('/:listingId', async (req, res, next) => {
    try {
        const deletedListing = await Listing.findByIdAndDelete(req.params.listingId);
        if (deletedListing) {
            res.status(200).json(deletedListing);
        } else {
            res.status(404).json({ error: 'Listing not found' });
        }
    } catch (error) {
        next(error);
    }
});

module.exports = router;
