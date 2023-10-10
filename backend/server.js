/**
 * Imports go here
 */
const dotenv = require("dotenv"); 
const express = require('express');
const mongoose = require('mongoose');

const app = express()
const port = 3000

// Configure environment variable path
dotenv.config({path: ".env"});

// Connect to MongoDB server through URI from environment variable
mongoose.connect(process.env.MONGODB_TEST_URI,
  {
    useNewUrlParser: true,
    useUnifiedTopology: true
  }
);

const db = mongoose.connection;
db.on("error", console.error.bind(console, "Connection error! Make sure MongoDB server is running! "));
db.once("open", function () {
  console.log("Connection successful! ");
});

app.get('/', (req, res) => {
  res.send('Hello World!')
})

app.listen(port, () => {
  console.log(`Example app listening on port ${port}`)
})