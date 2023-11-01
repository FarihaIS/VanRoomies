const Conversation = require('../models/conversationModel');
const Message = require('./Message');

class UserRecord {
    // userId: ObjectId, inactive: Boolean
    constructor(userId, inactive) {
        this.userId = userId;
        this.inactive = inactive ? inactive : false;
    }
}

class MessageStore {
    constructor() {
        if (!MessageStore.instance) {
            MessageStore.instance = this;
        }
        return MessageStore.instance;
    }

    async sendMessage(content, fromId, toId) {
        try {
            const conversation = await this.getConversationIfAbsent(fromId, toId);
            const message = new Message(fromId, content);
            conversation.messages.push(message);
            await conversation.save();
        } catch (error) {
            console.error(error);
        }
    }

    async getConversation(userId1, userId2) {
        try {
            return await Conversation.findOne({
                $and: [
                    {
                        users: { $elemMatch: { userId: userId1 } },
                    },
                    {
                        users: { $elemMatch: { userId: userId2 } },
                    },
                ],
            });
        } catch (error) {
            console.error(error);
        }
    }

    async getConversationIfAbsent(userId1, userId2) {
        try {
            const conversation = await this.getConversation(userId1, userId2);
            if (conversation) {
                return conversation;
            }
            const user1 = new UserRecord(userId1);
            const user2 = new UserRecord(userId2);
            const newConversation = new Conversation({ users: [user1, user2] });
            return await newConversation.save();
        } catch (error) {
            console.error(error);
        }
    }

    async getConversationsByUser(userId) {
        try {
            const conversations = await Conversation.find({
                users: { $elemMatch: { userId: userId } },
            });
            return conversations;
        } catch (error) {
            console.error(error);
        }
    }
}

let messageStore = Object.freeze(new MessageStore());

module.exports = messageStore;
