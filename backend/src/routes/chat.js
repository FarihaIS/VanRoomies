const express = require('express');
const router = express.Router();

const messageStore = require('../chat/messageStore');

router.get('/:conversationId', async (req, res, next) => {
    messageStore.getMessages(req.params.conversationId).then((messages) => {
		res.json(messages);
	});
});

module.exports = router;
