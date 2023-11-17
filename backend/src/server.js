const mongoose = require('mongoose');
const https = require('https');
const fs = require('fs');
const { initializeApp, applicationDefault } = require('firebase-admin/app');

// Configure environment variable path
require('dotenv').config({ path: `./.env.${process.env.NODE_ENV}` });

const credentials = {
    key: fs.readFileSync('certs/key.pem'),
    cert: fs.readFileSync('certs/cert.pem'),
};

const app = require('./app');

const httpsServer = https.createServer(credentials, app);
const port = process.env.PORT || 3000;

// Connect to MongoDB server through URI from environment variable
mongoose.connect(process.env.MONGODB_TEST_URI);

const db = mongoose.connection;
db.on('error', console.error.bind(console, 'Connection error! Make sure MongoDB server is running! '));
db.once('open', () => {
    console.log('Connection successful!');
});

// use socket.io
require('./sock.js')(httpsServer);

// use firebase admin sdk
initializeApp({
    credential: applicationDefault(),
});

httpsServer.listen(port, () => {
    console.log(`VanRoomies server at https://localhost:${port}`);
});
