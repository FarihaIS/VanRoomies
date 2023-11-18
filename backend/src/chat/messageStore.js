const mongoose = require('mongoose');
const Message = require('./Message');
const sendPushNotification = require('./firebase');
const User = require('../models/userModel');
const Conversation = require('../models/conversationModel');
class MessageStore {
    async sendMessage(content, fromId, toId) {
        const receivingUser = await User.findById(toId);
        const sendingUser = await User.findById(fromId);
        if (!receivingUser || !sendingUser) {
            return false;
        }
        const conversation = await this.getConversationIfAbsent(fromId, toId);
        const message = new Message(fromId, content);
        const firebaseToken = receivingUser.firebaseToken;
        sendPushNotification(firebaseToken, sendingUser.firstName, content);
        conversation.messages.push(message);
        await conversation.save();
        return true;
    }

    async getConversationIfAbsent(userId1, userId2) {
        const conversation = await Conversation.findOne({
            users: { $all: [userId1, userId2] },
        });
        if (conversation) {
            return conversation;
        }
        const newConversation = new Conversation({ users: [userId1, userId2] });
        return await newConversation.save();
    }

    async getConversationsByUser(userId) {
        const conversations = await Conversation.find({ users: userId });
        return conversations;
    }
}

let messageStore = Object.freeze(new MessageStore());

module.exports = messageStore;
