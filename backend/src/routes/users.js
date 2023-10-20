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

module.exports = router;
