const mongoose = require('mongoose');
const Listing = require('./listingModel');

const UserSchema = new mongoose.Schema({
    firstName: {
        type: String,
        required: true,
        trim: true,
        minength: 2,
        maxlength: 30,
    },
    lastName: {
        type: String,
        required: true,
        trim: true,
        minength: 2,
        maxlength: 30,
    },
    email: {
        type: String,
        required: true,
        trim: true,
        match: /.+@.+\..+/,
        unique: true,
        validate: {
            validator: async function (email) {
                const user = await User.findOne({ email: email });
                if (user) {
                    return false;
                }
                return true;
            },
            message: 'Email already exists',
        },
    },
    gender: {
        type: String,
        enum: ['male', 'female', 'non-binary', 'other'],
    },
    birthday: {
        type: Date,
        required: true,
    },
    phoneNumber: {
        type: String,
        required: true,
        trim: true,
    },
    profilePicture: {
        type: String,
    },
    bio: {
        type: String,
    },
    // password: { type: String, required: true, minength: 8, maxlength: 30 },
});

UserSchema.pre('remove', (next) => {
    Listing.deleteMany({ user: this._id }).exec();
    next();
});

const User = mongoose.model('User', UserSchema);
module.exports = User;
