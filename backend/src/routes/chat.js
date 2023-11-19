const express = require('express');
const router = express.Router();

const messageStore = require('../chat/messageStore');

router.get('/conversations/user/:userId', async (req, res, next) => {
    const conversations = await messageStore.getConversationsByUser(req.params.userId);
    if (!conversations) {
        res.status(404).json({ error: 'User not found' });
        return;
    }
    res.json(conversations);
    next();
});

router.post('/conversations/user/:userId', async (req, res, next) => {
    const content = req.body.content;
    const to = req.body.to;
    const userId = req.params.userId;
    if (!content || !to) {
        const missing = !content ? 'content' : 'to';
        res.status(404).json({ error: `${missing} was not provided` });
        return;
    }
    const status = await messageStore.sendMessage(req.body.content, userId, to);
    if (!status) {
        res.status(404).json({ error: 'Conversation could not be started' });
        return;
    }
    res.status(201).send();
    next();
});

module.exports = router;
