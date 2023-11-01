const mongoose = require('mongoose');

const PreferencesSchema = new mongoose.Schema({
    userId: {
        type: mongoose.Schema.Types.ObjectId,
        required: true,
    },
    minPrice: {
        type: Number,
        default: 0,
        required: true,
    },
    maxPrice: {
        type: Number,
        required: true,
    },
    housingType: {
        type: String,
        enum: ['studio', '1-bedroom', '2-bedroom', 'other'],
        required: true,
    },
    roommateCount: {
        type: Number,
        required: true,
    },
    petFriendly: {
        type: Boolean,
        required: true,
    },
    smoking: {
        type: String,
        enum: ['no-smoking', 'neutral', 'regular'],
        required: true,
    },
    partying: {
        type: String,
        enum: ['no-partying', 'neutral', 'regular'],
        required: true,
    },
    drinking: {
        type: String,
        enum: ['no-drinking', 'neutral', 'regular'],
        required: true,
    },
    noise: {
        type: String,
        enum: ['no-noise', 'neutral', 'regular'],
        required: true,
    },
    gender: {
        type: String,
        enum: ['male', 'female', 'neutral'],
        required: true,
    },
    moveInDate: {
        type: Date,
        required: true,
    },
    leaseLength: {
        type: Number,
        required: true,
    },
    location: {
        type: {
            latitude: { type: Number, required: true },
            longitude: { type: Number, required: true },
        },
    },
});

const Preferences = mongoose.model('Preferences', PreferencesSchema);

module.exports = Preferences;
