const mongoose = require('mongoose');

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
    },
    phoneNumber: {
        type: String,
        trim: true,
    },
    profilePicture: {
        type: String,
        default: 'base64encoding',
    },
    bio: {
        type: String,
        default: "Hi, I'm new to VanRoomies!",
    },
    notRecommended: {
        type: [{ type: mongoose.Types.ObjectId }],
        default: [],
    },
    firebaseToken: {
        type: String,
    },  
});

const User = mongoose.model('User', UserSchema);
module.exports = User;
