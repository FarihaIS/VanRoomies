const { createServer } = require('node:http');
const ioc = require('socket.io-client');
const mysocket = require('../src/sock.js');
jest.mock('../src/models/userModel');
const User = require('../src/models/userModel');
jest.mock('../src/models/conversationModel');
const Conversation = require('../src/models/conversationModel');
const { getMessaging } = require('firebase-admin/messaging');

jest.mock('firebase-admin/messaging', () => ({
    getMessaging: jest.fn(),
}));

const port = 12345;
const firstName = 'John';
const lastName = 'Doe';
const firebaseToken = 'someToken';

describe('Socket.io connection', () => {
    let io, serverSocket, clientSocket;
    const port = 12345;
    beforeAll((done) => {
        const httpServer = createServer();
        io = mysocket(httpServer);
        httpServer.listen(port, () => {
            io.on('connection', async (socket) => {
                serverSocket = socket;
            });
            done();
        });
    });

    afterEach(() => {
        clientSocket.disconnect();
    });

    afterAll(() => {
        io.close();
    });

    // Input: socket does not include a userId
    // Expected behavior: return an error
    // Expected output: { message: "No userId" }
    test('Unsupplied userId', (done) => {
        clientSocket = ioc(`http://localhost:${port}`);
        clientSocket.auth = { userId: null };

        clientSocket.on('connect_error', (err) => {
            expect(err.message).toBe('No userId');
            done();
        });
        clientSocket.connect();
    });

    // Input: socket includes an invalid userId
    // Expected behavior: return an error
    // Expected output: { message: "Invalid userId" }
    test('Invalid userId', (done) => {
        clientSocket = ioc(`http://localhost:${port}`);
        clientSocket.auth = { userId: 12345 };
        User.findById.mockResolvedValue(null);
        clientSocket.on('connect_error', (err) => {
            expect(err.message).toBe('Invalid userId');
            done();
        });
        clientSocket.connect();
    });
});

describe('WebSocket routines', () => {
    let io, serverSocket, clientSocket;

    beforeAll((done) => {
        const httpServer = createServer();
        io = mysocket(httpServer);
        User.findById.mockResolvedValue({
            firstName: firstName,
            lastName: lastName,
            firebaseToken: firebaseToken,
        });

        httpServer.listen(port, () => {
            clientSocket = ioc(`http://localhost:${port}`);
            clientSocket.auth = { userId: 12345 };
            clientSocket.on('connect', async () => done());
            io.on('connection', async (socket) => {
                serverSocket = socket;
            });
        });
    });

    afterAll(() => {
        io.close();
        clientSocket.disconnect();
    });

    afterEach(() => {
        jest.clearAllMocks();
    });

    // Input: socket has a valid userId; content is a string; to is a valid userId
    // Expected behavior: send a message to the user with userId = to
    // Expected output: { status: "success" }
    test('ON private message valid', async () => {
        Conversation.findOne.mockResolvedValue(null);
        Conversation.prototype.save.mockResolvedValue({
            users: [12345, 23456],
            messages: [],
            save: jest.fn(),
        });
        const mockMessaging = {
            send: jest.fn(),
        };
        getMessaging.mockReturnValueOnce(mockMessaging);
        const message = 'hola';

        const response = await clientSocket.emitWithAck('private message', {
            content: message,
            to: 23456,
        });
        expect(response).toEqual({ status: 'success' });
        expect(mockMessaging.send).toHaveBeenCalledWith({
            notification: {
                title: `VanRoomies message from: ${firstName}`,
                body: message,
            },
            token: firebaseToken,
        });
    });

    test('ON private message invalid firebase token', async () => {
        User.findById.mockResolvedValue({ firstName: 'John', lastName: 'Doe', firebaseToken: null });
        Conversation.findOne.mockResolvedValue({
            messages: [],
            save: jest.fn(),
        });
        Conversation.prototype.save.mockResolvedValue({
            users: [12345, 23456],
            messages: [],
        });
        const response = await clientSocket.emitWithAck('private message', {
            content: 'hola',
            to: 23456,
        });
        expect(response).toEqual({ status: 'success' });
    });

    test('ON private message invalid id', async () => {
        User.findById.mockResolvedValue(null);
        const response = await clientSocket.emitWithAck('private message', {
            content: 'hola',
        });
        expect(response).toEqual({ message: 'User not supplied', status: 'error' });
    });

    // test("should work with waitFor()", () => {
    //   clientSocket.emit("baz");

    //   return waitFor(serverSocket, "baz");
    // });
});
