const mongoose = require('mongoose');
const Message = require('./Message');
const sendPushNotification = require('./firebase');
const User = require('../models/userModel');

const conversationSchema = new mongoose.Schema({
    users: { type: [String], required: true },
    messages: { type: [Object], default: [] },
});

const Conversation = mongoose.model('Conversation', conversationSchema);

class MessageStore {
    constructor() {
        if (!MessageStore.instance) {
            MessageStore.instance = this;
        }
        return MessageStore.instance;
    }

    async sendMessage(content, fromId, toId) {
        const conversation = await this.getConversationIfAbsent(fromId, toId);
        const message = new Message(fromId, content);
        const receivingUser = await User.findById(toId);
        const sendingUser = await User.findById(fromId);
        if (receivingUser) {
            const firebaseToken = receivingUser.firebaseToken;
            sendPushNotification(firebaseToken, sendingUser.firstName, content);
            conversation.messages.push(message);
            await conversation.save();
        }
    }

    async getConversationIfAbsent(userId1, userId2) {
        try {
            const conversation = await Conversation.findOne({
                users: { $all: [userId1, userId2] },
            });
            if (conversation) {
                return conversation;
            }

            const newConversation = new Conversation({ users: [userId1, userId2] });
            const result = await newConversation.save();

            return result;
        } catch (error) {
            console.error(error);
        }
    }

    async getConversationsByUser(userId) {
        try {
            const conversations = await Conversation.find({ users: userId });
            return conversations;
        } catch (error) {
            console.error(error);
        }
    }
}

let messageStore = Object.freeze(new MessageStore());

module.exports = messageStore;
