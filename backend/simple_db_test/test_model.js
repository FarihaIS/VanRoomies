const mongoose = require('mongoose');
const userModel = require("./model");

async function addDummyUser(userInfo){
    const user = new userModel(userInfo);
    try {
        await user.save();
        console.log(user);
      } catch (error) {
        console.log(error);
      }
}

mongoose.connect('mongodb://localhost:27017/testdb',
    {
      useNewUrlParser: true,
      useUnifiedTopology: true
    }
  );

console.log("TESTING CONNECTION!!");
const db = mongoose.connection;
db.on("error", console.error.bind(console, "Connection error! Make sure MongoDB server is running! "));
db.once("open", function () {
    console.log("Connection successful! ");
  });

console.log("TESTING INSERT");
addDummyUser({'first_name': 'Denis', 'last_name': 'Lalaj', 'age': 23});
addDummyUser({'first_name': 'Matt', 'last_name': 'Anderson', 'age': 36});