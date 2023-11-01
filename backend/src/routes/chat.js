const express = require('express');
const router = express.Router();

const messageStore = require('../chat/messageStore');

/**
 * Get a List of all Conversation objects that a user is involved in.
 *
 * Resp: [{conversationId: UserId, users: [UserId], messages: [Message]}]
 */
router.get('/conversations/user/:userId', async (req, res, next) => {
    try {
        const conversations = await messageStore.getConversationsByUser(req.params.userId);
        res.json(conversations);
    } catch (error) {
        res.status(404).json({ error: error.message });
        next(error);
    }
});

/**
 * Update the inactive field in the Conversation object.
 *
 * Params: Path: {userId: UserId}, Body: {to: UserId, inactive: Boolean}
 *
 * Resp: {}
 */
router.put('/conversations/user/:userId', async (req, res, next) => {
    try {
        const receivingUserId = req.body.to;
        const sendingUserId = req.params.userId;
        const conversation = await messageStore.getConversation(sendingUserId, receivingUserId);
        if (!conversation) {
            throw new Error('Conversation not found');
        }
        conversation.users.forEach((user) => {
            if (user.userId == sendingUserId) {
                user.inactive = req.body.inactive;
            }
        });
        const newConversation = await conversation.save();
        res.json(newConversation);
    } catch (error) {
        res.status(404).json({ error: error.message });
        next(error);
    }
});

/**
 * Create a new Conversation from a user to another user.
 *
 * Params: Path: {userId: UserId}, Body: {to: UserId}
 *
 * Resp: {}
 */
router.post('/conversations/user/:userId', async (req, res, next) => {
    try {
        const receivingUserId = req.body.to;
        const sendingUserId = req.params.userId;
        const existing = await messageStore.getConversation(sendingUserId, receivingUserId);
        if (existing) {
            const err = new Error('Conversation already exists');
            res.status(409).json({ error: err.message });
            return next(err);
        }

        const conversation = await messageStore.sendMessage(req.body.content, sendingUserId, receivingUserId);
        res.status(201).json(conversation);
    } catch (error) {
        res.status(404).json({ error: error.message });
        next(error);
    }
});

module.exports = router;
