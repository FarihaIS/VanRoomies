const express = require('express');
const Listing = require('../models/listingModel');
const router = express.Router();

router.get('/', (req, res) => {
    Listing.find({}).then((listings) => {
        res.json(listings);
    });
});

router.post('/', (req, res) => {
    const listing = new Listing(req.body);
    listing.save().then((listing) => {
        res.location(`/api/listings/${listing._id}`);
        res.status(201).json(listing);
    });
});

module.exports = router;
