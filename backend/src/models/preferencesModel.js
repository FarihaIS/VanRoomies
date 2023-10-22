const mongoose = require('mongoose');

const PreferencesSchema = new mongoose.Schema({
    userID: { type: mongoose.Schema.Types.ObjectId, required: true },
    minPrice: {type: Number, default: 0, required: true},
    maxPrice: {type: Number, required: true},
    housingType: {type: String, enum: ['studio', '1-bedroom', '2-bedroom', 'other'], required: true},
    roommateCount: {type: Number, required: true},
    petFriendly: { type: Boolean, required: true },
    smoker: {type: String, enum: ['non-smoker', 'socially', 'regular'], required: true},
    partying: {type: String, enum: ['no-party', 'neutral', 'pro-party'], required: true},
    drinking: {type: String, enum: ['no-drinking', 'neutral', 'pro-drinking'], required: true},
    noise: {type: String, enum: ['quiet', 'neutral', 'noise-friendly'], required: true},
    gender: {type: String, enum: ['male', 'female', 'neutral'], required: true},
    moveInDate: { type: Date, required: true },
    leaseLength: { type: Number, required: true },
    location: { type: String, coordinates: [Number]},
});

const Preferences = mongoose.model('Preferences', PreferencesSchema);

module.exports = Preferences;
