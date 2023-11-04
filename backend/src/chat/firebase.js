const { getMessaging } = require('firebase-admin/messaging');

const sendPushNotification = async (token, senderName, message) => {
    const messageObj = {
        notification: {
            title: `VanRoomies message from: ${senderName}`,
            body: message,
        },
        token,
    };
    getMessaging()
        .send(messageObj)
        .then((response) => {
            console.log('Successfully sent push notification message:', response);
        })
        .catch((error) => {
            console.log('Error sending push notification message:', error);
        });
};

module.exports = sendPushNotification;
