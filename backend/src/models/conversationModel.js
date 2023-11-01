const { default: mongoose } = require('mongoose');

const conversationSchema = new mongoose.Schema({
    users: {
        type: [Object],
        required: true,
    },
    messages: {
        type: [Object],
        default: [],
    },
});

const Conversation = mongoose.model('Conversation', conversationSchema);

module.exports = Conversation;
