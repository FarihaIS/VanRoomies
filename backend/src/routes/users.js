const express = require('express');
const User = require('../models/userModel');
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
 * Create a new User object and save it to the database. This demands that all fields are provided
 * in the body of the request as shown below.
 *
 * Route: POST /api/users
 * Content-Type: application/json
 * Body: {
 *    "firstName": "John",
 *   "lastName": "Doe",
 *   "email": "
 * 	  ...
 * }
 */
router.post('/', async (req, res, next) => {
    try {
        const user = new User(req.body);
        const savedUser = await user.save();
        res.status(201).json(savedUser);
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
    try {
        const deletedUser = await User.findByIdAndDelete(req.params.userId);
        if (deletedUser) {
            res.status(200).json(deletedUser);
        } else {
            res.status(404).json({ error: 'User not found' });
        }
    } catch (err) {
        next(err);
    }
});

module.exports = router;
