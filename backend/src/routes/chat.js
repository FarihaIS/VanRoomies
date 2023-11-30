const express = require('express');
const router = express.Router();

const messageStore = require('../chat/messageStore');
const Conversation = require('../models/conversationModel');

router.get('/conversations/user/:userId', async (req, res, next) => {
    const conversations = await messageStore.getConversationsByUser(req.params.userId);
    if (!conversations) {
        res.status(404).json({ error: 'User not found' });
        return;
    }
    // sort conversations by latest message
    conversations.sort((a, b) => {
        const latestA = a.messages[a.messages.length - 1].timestamp;
        const latestB = b.messages[b.messages.length - 1].timestamp;
        return latestB - latestA;
    });
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
    const exists = await Conversation.exists({ users: [userId, to] });
    if (exists) {
        res.status(409).json({ error: 'Conversation already exists' });
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
