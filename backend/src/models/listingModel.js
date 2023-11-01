const mongoose = require('mongoose');

const ListingSchema = new mongoose.Schema({
    userId: {
        type: mongoose.Schema.Types.ObjectId,
        required: true,
    },
    title: {
        type: String,
        required: true,
        minlength: 5,
        maxlength: 100,
    },
    description: {
        type: String,
    },
    housingType: {
        type: String,
        enum: ['studio', '1-bedroom', '2-bedroom', 'other'],
        required: true,
    },
    rentalPrice: {
        type: Number,
        required: true,
    },
    listingDate: {
        type: Date,
        required: true,
    },
    moveInDate: {
        type: Date,
        required: true,
    },
    petFriendly: {
        type: Boolean,
        required: true,
    },
    status: {
        type: String,
        enum: ['active', 'pending', 'inactive'],
        required: true,
        default: 'active',
    },
    location: {
        type: {
            latitude: { type: Number, required: true },
            longitude: { type: Number, required: true },
        }
    },
    images: [{ type: String }],
});

const Listing = mongoose.model('Listing', ListingSchema);

module.exports = Listing;
