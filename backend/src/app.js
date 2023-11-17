const express = require('express');
const bodyParser = require('body-parser');
const mongoSanitize = require('express-mongo-sanitize');
const { logErrors, errorHandler } = require('./middlewares');

const app = express();

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

app.post('/api/firebase_token', async (req, res) => {
    const token = req.body.token;
    const userId = req.body.userId;
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

module.exports = app;
