const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const https = require('https');
const fs = require('fs');
const path = require('path');

// Configure environment variable path
require('dotenv').config({ path: `./.env.${process.env.NODE_ENV}` });

const app = express();
const credentials = {
    key: fs.readFileSync(path.resolve(process.env.KEY_PATH)),
    cert: fs.readFileSync(path.resolve(process.env.CERT_PATH)),
};

const httpsServer = https.createServer(credentials, app);
const port = 3000;

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
mongoose.connect(process.env.MONGODB_TEST_URI, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
});

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

httpsServer.listen(port, () => {
    console.log(`Example app listening on port ${port}`);
});
