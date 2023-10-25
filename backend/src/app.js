const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const https = require('https');
const fs = require('fs');
const path = require('path');
const { Server } = require('socket.io');
const crypto = require('crypto');
const mongoSanitize = require('express-mongo-sanitize');
const validator = require('validator');
const randomId = () => crypto.randomBytes(8).toString('hex');

const app = express();
const httpsServer = https.createServer(credentials, app);
const io = new Server(httpsServer);

const { InMemorySessionStore } = require('./chat/sessionStore');
const sessionStore = new InMemorySessionStore();
const messageStore = require('./chat/messageStore');

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
app.use('/api/listings', require('./routes/listings'));
app.use('/api/users', require('./routes/preferences'));
app.use('/api/users', require('./routes/users'));

app.get('/', (req, res) => {
    res.send('Hello World!');
});

app.use(logErrors);
app.use(errorHandler);

// websockets for chat
io.use((socket, next) => {
    const sessionID = socket.handshake.auth.sessionID;
    console.log(socket.handshake.auth);
    if (sessionID) {
        const session = sessionStore.findSession(sessionID);
        if (session) {
            socket.sessionID = sessionID;
            socket.userID = session.userID;
            socket.username = session.username;
            return next();
        }
    }
    const username = socket.handshake.auth.username;
    if (!username) {
        return next(new Error('invalid username'));
    }
    socket.sessionID = randomId();
    socket.userID = randomId();
    socket.username = username;
    next();
});

io.on('connection', (socket) => {
    // persist session
    sessionStore.saveSession(socket.sessionID, {
        userID: socket.userID,
        username: socket.username,
        connected: true,
    });

    // emit session details
    socket.emit('session', {
        sessionID: socket.sessionID,
        userID: socket.userID,
    });

    // join the "userID" room
    socket.join(socket.userID);

    // fetch existing users
    const users = [];
    sessionStore.findAllSessions().forEach((session) => {
        users.push({
            userID: session.userID,
            username: session.username,
            connected: session.connected,
        });
    });
    socket.emit('users', users);

    // notify existing users
    socket.broadcast.emit('user connected', {
        userID: socket.userID,
        username: socket.username,
        connected: true,
    });

    // forward the private message to the right recipient (and to other tabs of the sender)
    socket.on('private message', async ({ content, to }) => {
        const sanitizedMessage = sanitize(content);
		const conversationId = await messageStore.getConversationId(socket.userID, to);
		await messageStore.saveMessage(socket.userID, sanitizedMessage, conversationId);
        socket.to(to).to(socket.userID).emit('private message', {
            sanitizedMessage,
            from: socket.userID,
            to,
        });
    });

    // notify users upon disconnection
    socket.on('disconnect', async () => {
        const matchingSockets = await io.in(socket.userID).fetchSockets();
        const isDisconnected = matchingSockets.size === 0;
        if (isDisconnected) {
            // notify other users
            socket.broadcast.emit('user disconnected', socket.userID);
            // update the connection status of the session
            sessionStore.saveSession(socket.sessionID, {
                userID: socket.userID,
                username: socket.username,
                connected: false,
            });
        }
    });
});
const port = process.env.PORT || 3000;

httpsServer.listen(port, () => {
    console.log(`Example app listening on port ${port}`);
});
