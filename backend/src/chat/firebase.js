const { getMessaging } = require('firebase-admin/messaging');

const sendPushNotification = async (token, senderName, message) => {
    if (!token) {
        console.log('No token provided');
        return;
    }
    const messageObj = {
        notification: {
            title: `VanRoomies message from: ${senderName}`,
            body: message,
        },
        token,
    };
    await getMessaging().send(messageObj);
};

module.exports = sendPushNotification;
