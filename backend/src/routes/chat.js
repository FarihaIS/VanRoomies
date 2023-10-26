const express = require('express');
const router = express.Router();

const messageStore = require('../chat/messageStore');

router.get('/conversations/:conversationId', async (req, res, next) => {
	try {
		const messages = await messageStore.getMessages(req.params.conversationId);
		res.json(messages);
	} catch (error) {
		console.error(error);
		res.status(404).json({ error: error });
	}
});

router.get('/conversations/user/:userId', async (req, res, next) => {
	try {
		const conversations = await messageStore.getMessagesFromUser(req.params.userId);
		res.json(conversations);
	} catch (error) {
		console.error(error);
		res.status(404).json({ error: error });
	}
});

module.exports = router;
