const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const https = require('https');
const fs = require('fs');
const path = require('path');
const mongoSanitize = require('express-mongo-sanitize');
const { initializeApp, applicationDefault } = require('firebase-admin/app');

const { logErrors, errorHandler } = require('./middlewares');
const User = require('./models/userModel');

// Configure environment variable path
require('dotenv').config({ path: `./.env.${process.env.NODE_ENV}` });

const credentials = {
    key: fs.readFileSync(path.resolve(process.env.KEY_PATH)),
    cert: fs.readFileSync(path.resolve(process.env.CERT_PATH)),
};
const app = express();
const httpsServer = https.createServer(credentials, app);
const port = process.env.PORT || 3000;

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

app.post('/api/firebase_token', async (req, res, next) => {
    const { token, userId } = req.body;
    if (!token || !userId) {
        res.status(400).json({ error: 'Token or userId is missing' });
        return;
    }
    const user = await User.findById(userId);
    if (user) {
        const update = { firebaseToken: token };
        await user.updateOne(update);
        res.status(200).json({ message: 'Token saved' });
    } else {
        const error = new Error('User not found');
        res.status(404).json({ error: error.message });
    }
});

app.use(logErrors);
app.use(errorHandler);

// use socket.io
require('./sock.js')(httpsServer);

// use firebase admin sdk
initializeApp({
    credential: applicationDefault(),
});

httpsServer.listen(port, () => {
    console.log(`VanRoomies server at https://localhost:${port}`);
});
