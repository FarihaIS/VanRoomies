const dotenv = require('dotenv');
const mongoose = require('mongoose');

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
