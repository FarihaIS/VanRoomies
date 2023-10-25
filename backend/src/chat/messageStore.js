const mongoose = require('mongoose');

// Define a Message schema
// const messageSchema = new mongoose.Schema({
//   sender: { type: String, required: true },
//   message: { type: String, required: true },
//   timestamp: { type: Date, default: Date.now },
//   conversationId: { type: mongoose.Schema.Types.ObjectId, required: true },
// });

// // Define the Message model
// const Message = mongoose.model('Message', messageSchema);

class Message {
    constructor(sender, message) {
        this.sender = sender;
        this.message = message;
        this.timestamp = Date.now();
    }
}

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

    async getConversationId(user1, user2) {
        try {
            const conversation = await Conversation.findOne({
                users: { $all: [user1, user2] },
            });
            if (conversation) {
                return conversation._id;
            }

            const newConversation = new Conversation({ users: [user1, user2] });
            const result = await newConversation.save();

            return result._id;
        } catch (error) {
			console.error(error);
        }
    }

    async saveMessage(sender, content, conversationId) {
        try {
            const message = new Message(sender, content);
            const conversation = await Conversation.findById(conversationId);
            conversation.messages.push(message);
            await conversation.save();
        } catch (error) {
			console.error(error);
        }
    }

	async getMessages(conversationId) {
		try {
			const conversation = await Conversation.findById(conversationId);
			return conversation.messages;
		} catch (error) {
			console.error(error);
		}
	}
}

let messageStore = Object.freeze(new MessageStore());

module.exports = messageStore;
