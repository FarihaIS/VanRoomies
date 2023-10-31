const express = require('express');
const User = require('../models/userModel');
const { default: mongoose } = require('mongoose');
const Listing = require('../models/listingModel');
const Preferences = require('../models/preferencesModel');
const validateGoogleIdToken = require('../authentication/googleAuthentication');
const { generateAuthenticationToken } = require('../authentication/jwtAuthentication');
const router = express.Router();

/**
 * Get all User objects from the database.
 *
 * Route: GET /api/users
 * Content-Type: application/json
 */
router.get('/', async (req, res, next) => {
    try {
        const users = await User.find({});
        res.json(users);
    } catch (err) {
        next(err);
    }
});

/**
 * Login user through Google Sign In. This passes the user through a
 * middleware to validate their ID token. The login serves a 2-fold purpose
 * of signing up a new user and creating their record on the database
 * or simply querying their profile otherwise.
 *
 * Route: POST /api/users/login
 * Content-Type: application/json
 * Body: {
 *   "idToken": String
 *   "firstName": "John",
 *   "lastName": "Doe",
 *   "email": "
 * 	  ...
 * }
 * Returns:
 *      status(200): User Object
 *      status(201): {userToken: String, user: User}
 */
router.post('/login', validateGoogleIdToken, async (req, res, next) => {
    try {
        const currentUser = await User.findOne({ email: req.body.email });
        if (currentUser) {
            // Set status 200 if user user already exists
            res.status(200).json({ userId: currentUser._id });
        } else {
            const user = new User({
                firstName: req.body.firstName,
                lastName: req.body.lastName,
                email: req.body.email,
            });
            const savedUser = await user.save();
            const userToken = generateAuthenticationToken(savedUser);
            // Set status 201 if a new user was created
            res.status(201).json({ userId: savedUser._id, userToken: userToken });
        }
    } catch (err) {
        res.status(400).json({ error: err.message });
        next(err);
    }
});

/**
 * Get a User object by its ID.
 *
 * Route: GET /api/users/:userId
 * Content-Type: application/json
 */
router.get('/:userId', async (req, res, next) => {
    try {
        const user = await User.findById(req.params.userId);
        if (user) {
            res.status(200).json(user);
        } else {
            res.status(404).json({ error: 'User not found' });
        }
    } catch (err) {
        next(err);
    }
});

/**
 * Update an existing User object and save changes on the database. This demands that for the fields
 * that need to be updated, information is provided in the body of the request as shown below.
 *
 * Route: PUT /api/users/:userId
 * Content-Type: application/json
 * Body: {
 *     "firstName": "John",
 *     "lastName": "Doe",
 *     "email": "testing@github.com"
 * 		...
 */
router.put('/:userId', async (req, res, next) => {
    try {
        const updatedUser = await User.findByIdAndUpdate(req.params.userId, req.body, { new: true });
        if (updatedUser) {
            res.status(200).json(updatedUser);
        } else {
            res.status(404).json({ error: 'User not found' });
        }
    } catch (err) {
        next(err);
    }
});

/**
 * Delete an existing User object from the database.
 * Content-Type: application/json
 * Route: DELETE /api/users/:userId
 */
router.delete('/:userId', async (req, res, next) => {
    const session = await mongoose.startSession();

    try {
        let deletedUser = null;
        session.startTransaction();

        const userId = req.params.userId;
        deletedUser = await User.findByIdAndDelete(req.params.userId);
        await Listing.deleteMany({ userId: userId });
        await Preferences.deleteOne({ userId: userId });
        await session.commitTransaction();
        if (deletedUser) {
            res.status(200).json(deletedUser);
        } else {
            res.status(404).json({ error: 'User not found' });
        }
    } catch (err) {
        await session.abortTransaction();
        next(err);
    } finally {
        session.endSession();
    }
});

module.exports = router;
