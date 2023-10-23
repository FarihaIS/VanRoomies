const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const https = require('https');
const fs = require('fs');
const path = require('path');
const { Server } = require('socket.io');

const app = express();
const httpsServer = https.createServer(credentials, app);
const io = new Server(httpsServer);

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

// routes
app.use('/api/listings', require('./routes/listings'));
app.use('/api/users', require('./routes/preferences'));
app.use('/api/users', require('./routes/users'));

app.get('/', (req, res) => {
    res.send('Hello World!');
});

app.use(logErrors);
app.use(errorHandler);

// socketio middleware
const authMiddleware = (socket, next) => {
	const username = socket.handshake.auth.username;
	if (!username) {
		console.error("invalid username: " + username);
		return next(new Error("invalid username"));
	}
	socket.username = username;
	next();
}

io.use(authMiddleware);

io.on('connection', (socket) => {
	const users = [];
	for (let [id, socket] of io.of("/").sockets) {
		users.push({
			userID: id,
			username: socket.username,
		});
	}
	socket.emit("users", users);
});


// function to perdiocoally send messages to all socketio clients
// setInterval(() => {
// 	console.log("interval")
// 	io.emit('chat message', "hello world");
// }, 1000);

httpsServer.listen(port, () => {
    console.log(`Example app listening on port ${port}`);
});
