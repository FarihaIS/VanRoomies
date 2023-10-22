const express = require('express');
const Listing = require('../models/listingModel');
const router = express.Router();

/**
 * Get a Listing from Listing Collection by id
 * 
 * Route: GET /api/listing/:id where id is the ID of the listing in the database
 */
router.get('/:id', async (req, res) => {
    try {
        const listing = await Listing.findById(req.params.id);
        if(listing){
            res.status(200).json(listing);
        }else{
            res.status(404).send();
        }
    } catch (error) {
        console.log(error);
        res.status(500).json({ error: 'Internal Server Error' });
    }
});

/**
 * Create a new Listing and save it on the database. This demands that the listingID 
 * information is provided in the body of the request as shown below.
 * 
 * Route: POST /api/listing/
 * 
 * Body: {listerID: ObjectId, title: String .... images: [String]}
 */
router.post('/', async (req, res) => {
    const listing = new Listing(req.body);
    try {
        await listing.save();
        res.location(`/api/listing/${listing._id}`);
        res.status(201).json(listing);
    } catch (error) {
        console.log(error);
        res.status(500).json({ error: 'Internal Server Error' });
    }
});

/**
 * Update an existing Listing and save it on the database. This demands that for the fields 
 * that need to be updated, information is provided in the body of the request as shown below.
 * 
 * Route: PUT /api/listing/
 * 
 * Body: {title: String <new_title>, rentalPrice: Number<new_price> ....}
 */
router.put('/:id', async (req, res) => {
    try {
        const updatedListing = await Listing.findByIdAndUpdate(req.params.id, req.body, { new: true });
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
 * Delete a listing from the database based on the listingID
 * 
 * Route: DELETE /api/listing/:id where id is the ID of the listing in the database
 */
router.delete('/:id', async (req, res) => {
    try {
        const deletedListing = Listing.findByIdAndDelete(req.params.id);
        if(deletedListing){
            res.status(200).json();
        }else{
            res.status(404).send();
        }
    } catch (error) {
        console.log(error);
        res.status(500).json({ error: 'Internal Server Error' });
    }
});

module.exports = router;
