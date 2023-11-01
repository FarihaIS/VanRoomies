class Message {
    constructor(sender, message) {
        this.sender = sender;
        this.message = message;
        this.timestamp = Date.now();
    }
}

module.exports = Message;
