const dotenv = require('dotenv');
const mongoose = require('mongoose');
const userModel = require('./model');

dotenv.config({ path: '../.env' });

async function addDummyUser(userInfo) {
    const user = new userModel(userInfo);
    try {
        await user.save();
        console.log(user);
    } catch (error) {
        console.log(error);
    }
}

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

console.log('TESTING INSERT');
addDummyUser({ first_name: 'Denis', last_name: 'Lalaj', age: 23 });
addDummyUser({ first_name: 'Matt', last_name: 'Anderson', age: 36 });

// curl --request POST --header 'Content-Type: application/json' --data '{"minPrice":800,"maxPrice":1600,"housingType":"studio","roommateCount":2,"petFriendly":"true","smoker":"non-smoker","partying":"no-party","drinking":"neutral","noise":"neutral","gender":"neutral","moveInDate":"2023-10-22","leaseLength":6}' localhost:3000/api/user/0123456789abcdefABCDEF
