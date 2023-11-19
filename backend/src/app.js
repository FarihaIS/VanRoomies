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
app.use('', require('./routes/generic'));

// error handling
app.use(logErrors);
app.use(errorHandler);

module.exports = app;
