const express = require('express');
const User = require('../models/userModel');
const mongoose = require('mongoose');
const Listing = require('../models/listingModel');
const Preferences = require('../models/preferencesModel');
const messageStore = require('../chat/messageStore');
const { generateAuthenticationToken } = require('../authentication/jwtAuthentication');
const { BLOCK_THRESHOLD, DEFAULT_IMAGES } = require('../utils/constants');
const router = express.Router();

/**
 * Login user through Google Sign In. This passes the user through a
 * middleware to validate their ID token. The login serves a 2-fold purpose
 * of signing up a new user and creating their record on the database
 * or simply querying their profile otherwise.
 *
 * TODO: Middleware for Google Sign In + JWT ready, FE integration coming soon
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
router.post('/login', async (req, res, next) => {
    const currentUser = await User.findOne({ email: req.body.email });
    if (currentUser) {
        // Set status 200 if user user already exists
        res.status(200).json({ userId: currentUser._id });
    } else {
        const user = new User({
            firstName: req.body.firstName,
            lastName: req.body.lastName,
            email: req.body.email,
            profilePicture: DEFAULT_IMAGES['person'],
        });
        const savedUser = await user.save();
        const userToken = generateAuthenticationToken(savedUser);
        // Set status 201 if a new user was created
        res.status(201).json({ userId: savedUser._id, userToken });
    }
});

/**
 * Get a User object by its ID.
 *
 * Route: GET /api/users/:userId
 * Content-Type: application/json
 */
router.get('/:userId', async (req, res, next) => {
    const user = await User.findById(req.params.userId);
    if (user) {
        res.status(200).json(user);
    } else {
        res.status(404).json({ error: 'User not found' });
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
    const updatedUser = await User.findByIdAndUpdate(req.params.userId, req.body, { new: true });
    if (updatedUser) {
        res.status(200).json(updatedUser);
    } else {
        res.status(404).json({ error: 'User not found' });
    }
});

/**
 * Block a specified user and remove it from the list of possible recommendations of the user who
 * requested the block. As a side effect, if a user is blocked by more than BLOCK_THRESHOLD number
 * of users, then the user account and all relevant information for this user will be deleted.
 *
 * Route: POST /api/users/:userId/block
 *
 * Body: {blockedId: ""}
 */
router.post('/:userId/block', async (req, res, next) => {
    // Either both updates should occur or neither should so first try to get both documents
    let currentUser = await User.findById(req.params.userId);
    let blockedUser = await User.findById(req.body.blockedId);

    if (currentUser && blockedUser) {
        await User.findByIdAndUpdate(
            req.params.userId,
            { $addToSet: { notRecommended: req.body.blockedId } },
            { new: true },
        );

        let updatedBlocked = await User.findByIdAndUpdate(
            req.body.blockedId,
            { $addToSet: { notRecommended: req.params.userId }, $inc: { blockedCount: 1 } },
            { new: true },
        );
        if (updatedBlocked.blockedCount >= BLOCK_THRESHOLD) {
            const deleteUserId = req.body.blockedId;
            await User.findByIdAndDelete(deleteUserId);
            await Listing.deleteMany({ deleteUserId });
            await Preferences.deleteOne({ userId: deleteUserId });
        }

        // If there exists a conversation between users it will be deleted
        await messageStore.deleteConversation(req.params.userId, req.body.blockedId);

        res.status(200).json({ message: 'User blocked successfully!' });
    } else {
        res.status(404).json({ error: 'User blocking failed!' });
    }
});

/**
 * Unmatch two currently matched users. This will delete the conversation between the users
 * and will also remove users from their excluded lists so that users can now appear on each others
 * recommendations again.
 *
 * Route: POST /api/users/:userId/unmatch
 *
 * Body: {unmatchedId: ""}
 */
router.post('/:userId/unmatch', async (req, res, next) => {
    let firstUser = await User.findById(req.params.userId);
    let secondUser = await User.findById(req.body.unmatchedId);

    if (firstUser && secondUser) {
        await User.findByIdAndUpdate(
            req.params.userId,
            { $pull: { notRecommended: req.body.unmatchedId } },
            { new: true },
        );

        await User.findByIdAndUpdate(
            req.body.unmatchedId,
            { $pull: { notRecommended: req.params.userId } },
            { new: true },
        );

        // If there exists a conversation between users it will be deleted
        await messageStore.deleteConversation(req.params.userId, req.body.unmatchedId);

        res.status(200).json({ message: 'Unmatched with user successfully!' });
    } else {
        res.status(404).json({ error: 'User unmatching failed!' });
    }
});

/**
 * Delete an existing User object from the database.
 * Content-Type: application/json
 * Route: DELETE /api/users/:userId
 */
router.delete('/:userId', async (req, res, next) => {
    const session = await mongoose.startSession();

    let deletedUser = null;
    session.startTransaction();

    const userId = req.params.userId;
    deletedUser = await User.findByIdAndDelete(req.params.userId);
    await Listing.deleteMany({ userId });
    await Preferences.deleteOne({ userId });
    await session.commitTransaction();
    if (deletedUser) {
        res.status(200).json(deletedUser);
    } else {
        res.status(404).json({ error: 'User not found' });
    }
});

module.exports = router;
