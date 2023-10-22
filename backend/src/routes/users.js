const express = require('express');
const User = require('../models/userModel');
const router = express.Router();

router.get('/', (req, res) => {
    User.find({}).then((users) => {
        res.json(users);
    });
});

router.post('/', (req, res) => {
    const user = new User(req.body);
    user.save().then((user) => {
        res.location(`/api/users/${user._id}`);
        res.status(201).json(user);
    });
});

router.get('/:userId', (req, res) => {
    User.findById(req.params.userId)
        .then((user) => {
            if (user) {
                res.json(user);
            } else {
                res.status(404).json({ error: 'User not found' });
            }
        })
        .catch((err) => {
            res.status(400).json({ error: err.message });
        });
});

router.put('/:userId', (req, res) => {
    User.findByIdAndUpdate(req.params.userId, req.body, { new: true })
        .then((user) => {
            if (user) {
                res.json(user);
            } else {
                res.status(404).json({ error: 'User not found' });
            }
        })
        .catch((err) => {
            res.status(400).json({ error: err.message });
        });
});

router.delete('/:userId', (req, res) => {
    User.findByIdAndRemove(req.params.userId)
        .then((user) => {
            if (user) {
                res.json(user);
            } else {
                res.status(404).json({ error: 'User not found' });
            }
        })
        .catch((err) => {
            res.status(400).json({ error: err.message });
        });
});

module.exports = router;
