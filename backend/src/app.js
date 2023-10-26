const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const https = require('https');
const fs = require('fs');
const path = require('path');
const { Server } = require('socket.io');
const mongoSanitize = require('express-mongo-sanitize');
const validator = require('validator');
ObjectId = require('mongodb').ObjectId;

const app = express();
const httpsServer = https.createServer(credentials, app);
const io = new Server(httpsServer);

const messageStore = require('./chat/messageStore');
const User = require('./models/userModel');

const sanitize = (message) => {
	return validator.escape(validator.trim(message));
}

// Configure environment variable path
require('dotenv').config({ path: `./.env.${process.env.NODE_ENV}` });

const credentials = {
    key: fs.readFileSync(path.resolve(process.env.KEY_PATH)),
    cert: fs.readFileSync(path.resolve(process.env.CERT_PATH)),
};

const port = process.env.PORT || 3000;

function logErrors(err, req, res, next) {
    console.error(err.message);
    next(err);
}

function errorHandler(err, req, res, next) {
    if (res.headersSent) {
        return next(err);
    }
    res.status(500);
    res.json({ error: err.message });
}

// Connect to MongoDB server through URI from environment variable
mongoose.connect(process.env.MONGODB_TEST_URI || 'mongodb://localhost:27017/vanroomies');

const db = mongoose.connection;
db.on('error', console.error.bind(console, 'Connection error! Make sure MongoDB server is running! '));
db.once('open', () => {
    console.log('Connection successful!');
});

// middleware
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.raw());

app.use(mongoSanitize());

// routes
app.use('/api/users', require('./routes/users'));
app.use('/api/listings', require('./routes/listings'));
app.use('/api/users', require('./routes/preferences'));
app.use('/api/chat', require('./routes/chat'));

app.get('/', (req, res) => {
    res.send('Hello World!');
});

app.use(logErrors);
app.use(errorHandler);

// websockets for chat
io.use(async (socket, next) => {
	try {
		let userId = socket.handshake.auth.userId;
		if (!userId) {
			return next(new Error('No userId'));
		}
		if (!(userId instanceof mongoose.Types.ObjectId)) {
			userId = new ObjectId(userId)
		}

		const user = await User.findById(userId);
		if (!user) {
			return next(new Error('Invalid userId'));
		}
		socket.emit('user connected', `Welcome ${user.firstName} ${user.lastName}!`);
		socket.userId = socket.handshake.auth.userId;
		next();
	} catch (error) {
		return next(error);
	}
});

io.on('connection', async (socket) => {
	// socket.on('users', async (callback) => {
	// 	const users = await User.find({});
	// 	callback(users);
	// });

	socket.join(socket.userId);
	
    socket.on('private message', async ({ content, to }, callback) => {
		try {
			const user = await User.findById(to);
			if (!user) {
				next(new Error('Invalid userId'));
			}
			const sanitizedMessage = sanitize(content);
			const conversationId = await messageStore.getConversationId(socket.userId, to);
			await messageStore.saveMessage(socket.userId, sanitizedMessage, conversationId);
			socket.timeout(5000).to(to).to(socket.userId).emit('private message', {
				content: sanitizedMessage,
				from: socket.userId,
				to,
			}, () => {
				callback({ status: 'success' });
			});
		} catch (error) {
			callback({ status: 'error', message: 'User not found' });
		}
    });
});
const port = process.env.PORT || 3000;

httpsServer.listen(port, () => {
    console.log(`Example app listening on port ${port}`);
});

// John: 6539fad1e7b746a2b1fefba6
// Denis: 6539fb1ee7b746a2b1fefba9
