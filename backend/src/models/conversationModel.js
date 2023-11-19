const mongoose = require('mongoose');

const conversationSchema = new mongoose.Schema({
    users: { type: [String], required: true },
    messages: { type: [Object], default: [] },
});

const Conversation = mongoose.model('Conversation', conversationSchema);

module.exports = Conversation;
