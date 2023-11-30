const mongoose = require('mongoose');
const User = require('./models/userModel');
const messageStore = require('./chat/messageStore');
const { Server } = require('socket.io');
const { sanitize } = require('./utils/utils');

// ChatGPT Usage: No
module.exports = function (server) {
    const io = new Server(server);
    io.use(async (socket, next) => {
        let userId = socket.handshake.auth.userId;
        if (!userId) {
            return next(new Error('No userId'));
        }
        userId = new mongoose.mongo.ObjectId(userId);

        const user = await User.findById(userId);
        if (!user) {
            return next(new Error('Invalid userId'));
        }
        socket.emit('user connected', `Welcome ${user.firstName} ${user.lastName}!`);
        socket.userId = socket.handshake.auth.userId;
        next();
    });

    io.on('connection', async (socket) => {
        socket.join(socket.userId);

        socket.on('private message', async ({ content, to }, fn) => {
            const sanitizedMessage = sanitize(content);
            const sent = await messageStore.sendMessage(sanitizedMessage, socket.userId, to);
            if (!sent) {
                return fn({ status: 'error', message: 'User not supplied' });
            }
            socket
                .timeout(5000)
                .to(to)
                .to(socket.userId)
                .emit(
                    'private message',
                    {
                        content: sanitizedMessage,
                        from: socket.userId,
                        to,
                    },
                    () => {
                        fn({ status: 'success' });
                    },
                );
        });
    });

    return io;
};
