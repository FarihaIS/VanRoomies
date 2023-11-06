const express = require('express');
const router = express.Router();

const messageStore = require('../chat/messageStore');

router.get('/conversations/:conversationId', async (req, res, next) => {
    const messages = await messageStore.getConversation(req.params.conversationId);
    res.json(messages);
    next();
});

router.get('/conversations/user/:userId', async (req, res, next) => {
    const conversations = await messageStore.getConversationsByUser(req.params.userId);
    res.json(conversations);
    next();
});

router.post('/conversations/user/:userId', async (req, res, next) => {
    const receivingUserId = req.body.to;
    const sendingUserId = req.params.userId;
    const conversation = await messageStore.sendMessage(req.body.content, sendingUserId, receivingUserId);
    res.status(201).json(conversation);
    next();
});

module.exports = router;
