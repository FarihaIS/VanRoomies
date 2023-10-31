const express = require('express');
const router = express.Router();

const messageStore = require('../chat/messageStore');

router.get('/conversations/:conversationId', async (req, res, next) => {
    try {
        const messages = await messageStore.getConversation(req.params.conversationId);
        res.json(messages);
        next();
    } catch (error) {
        res.status(404).json({ error: error });
        next(error);
    }
});

router.get('/conversations/user/:userId', async (req, res, next) => {
    try {
        const conversations = await messageStore.getConversationsByUser(req.params.userId);
        res.json(conversations);
        next();
    } catch (error) {
        res.status(404).json({ error: error });
        next(error);
    }
});

router.post('/conversations/user/:userId', async (req, res, next) => {
    try {
        const receivingUserId = req.body.to;
        const sendingUserId = req.params.userId;
        const conversation = await messageStore.sendMessage(req.body.content, sendingUserId, receivingUserId);
        res.status(201).json(conversation);
        next();
    } catch (error) {
        res.status(404).json({ error: error });
        next(error);
    }
});

module.exports = router;
