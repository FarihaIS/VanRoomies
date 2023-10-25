const dotenv = require('dotenv');
const mongoose = require('mongoose');
const users = require('./sample_users.json');
const preferences = require('./sample_preferences.json');
const listings = require('./sample_listings.json');

dotenv.config({ path: '../.env' });

// Database setup stuff go under here
mongoose.connect(process.env.MONGODB_TEST_URI, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
});
console.log('TESTING CONNECTION!!');
const db = mongoose.connection;
db.on('error', console.error.bind(console, 'Connection error! Make sure MongoDB server is running! '));
db.once('open', function () {
    console.log('Connection successful! ');
});

console.log(users);
console.log(preferences);
console.log(listings);
