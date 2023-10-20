const mongoose = require('mongoose');

const ListingSchema = new mongoose.Schema({
    listerID: { type: mongoose.Schema.Types.ObjectId, required: true },
    name: { type: String, required: true, minlength: 5, maxlength: 30 },
    description: { type: String },
    rental_price: { type: Number, required: true },
    date: { type: Date, required: true },
    pet_friendly: { type: Boolean },
    active: { type: Boolean, default: true, required: true },
    location: { type: String, coordinates: [Number], required: true },
});

const Listing = mongoose.model('Listing', ListingSchema);

module.exports = Listing;
