const express = require('express');
const router = express.Router();
const User = require('../models/userModel');

// ChatGPT Usage: No
router.get('/', (req, res) => {
    res.send('Hello World!');
});

// ChatGPT Usage: No
router.post('/api/firebase_token', async (req, res) => {
    const token = req.body.token;
    const userId = req.body.userId;
    if (!token || !userId) {
        res.status(400).json({ error: 'Token or userId is missing' });
        return;
    }
    const user = await User.findById(userId);
    if (user) {
        const update = { firebaseToken: token };
        await user.updateOne(update);
        res.status(200).json({ message: 'Token saved' });
    } else {
        res.status(404).json({ error: 'User not found' });
    }
});

module.exports = router;
