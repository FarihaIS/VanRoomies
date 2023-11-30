const request = require('supertest');
const app = require('../../src/app');

jest.mock('../../src/models/conversationModel.js');
const Conversation = require('../../src/models/conversationModel.js');
jest.mock('../../src/models/userModel.js');
const User = require('../../src/models/userModel.js');
const { getMessaging } = require('firebase-admin/messaging');

jest.mock('firebase-admin/messaging', () => ({
    getMessaging: jest.fn(),
}));

const getMockConversations = (userId) => [
    {
        users: [userId, 'alice123'],
        messages: [
            {
                sender: userId,
                content: 'Hello!',
                timestamp: Date.now(),
            },
        ],
    },
    {
        users: [userId, 'bob456'],
        messages: [
            {
                sender: userId,
                content: 'Hello!',
                timestamp: Date.now(),
            },
        ],
    },
];
const mockMessaging = {
    send: jest.fn(),
};

// Interface GET /api/chat/conversations/user/:userId
describe('GET conversations from user', () => {
    afterEach(() => {
        jest.clearAllMocks();
    });

    // Input: userId is valid id for a User that exists in the database
    // Expected status code: 200
    // Expected behavior: return a list of Conversations that the User is a part of
    // Expected output: Array of Conversation objects
    // ChatGPT Usage: No
    test('Valid userId', async () => {
        const id = 'someUserId';
        Conversation.find.mockResolvedValueOnce(getMockConversations(id));
        const res = await request(app).get(`/api/chat/conversations/user/${id}`);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body.every((obj) => obj.users && obj.messages && obj.users.includes(id))).toBe(true);
    });

    // Input: userId is not a valid id
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: "User not found" }
    // ChatGPT Usage: No
    test('Invalid userId', async () => {
        const id = 'invalid';
        Conversation.find.mockResolvedValueOnce(null);
        const res = await request(app).get(`/api/chat/conversations/user/${id}`);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'User not found' });
    });

    // Input: userId is valid id for a User that exists in the database, but the User has no Conversations  (empty array)
    // Expected status code: 200
    // Expected behavior: return an empty array
    // Expected output: []
    // ChatGPT Usage: No
    test('User has no conversations', async () => {
        const id = 'someUserId';
        Conversation.find.mockResolvedValueOnce([]);
        const res = await request(app).get(`/api/chat/conversations/user/${id}`);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body).toStrictEqual([]);
    });
});

// Interface POST /api/chat/conversations/user/:userId
describe('POST a new conversation between users ', () => {
    const user = { firstName: 'John', lastName: 'Doe', firebaseToken: 'someToken', _id: 'someUserId' };
    afterEach(() => {
        jest.clearAllMocks();
    });

    beforeAll(() => {
        Conversation.findOne.mockResolvedValue({
            messages: [],
            save: jest.fn(),
        });
        Conversation.prototype.save.mockResolvedValue({
            users: [12345, 23456],
            messages: [],
        });
        getMessaging.mockReturnValue(mockMessaging);
    });

    // Input: userId and to are valid ids for Users that exist in the database; content exists and is defined
    // Expected status code: 201
    // Expected behavior: create a new Conversation in the database
    // Expected output: none
    // ChatGPT Usage: No
    test('Valid input', async () => {
        User.findById.mockResolvedValue(user);
        const id = 'someUserId';
        const res = await request(app).post(`/api/chat/conversations/user/${id}`).send({
            to: 'alice123',
            content: 'Hello!',
        });
        expect(res.statusCode).toStrictEqual(201);
        expect(res.body).toStrictEqual({});
    });

    // Input: A provided userId is not a valid id
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: "Conversation could not be started" }
    // ChatGPT Usage: No
    test('Invalid userId', async () => {
        const id = 'invalid';
        User.findById.mockResolvedValue(null);
        const res = await request(app).post(`/api/chat/conversations/user/${id}`).send({
            to: 'alice123',
            content: 'Hello!',
        });
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'Conversation could not be started' });
    });

    // Input: receiving userId (to) is not provided
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: "to was not provided" }
    // ChatGPT Usage: No
    test('Missing receiving userId', async () => {
        const id = 'someUserId';
        const res = await request(app).post(`/api/chat/conversations/user/${id}`).send({
            content: 'Hello!',
        });
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'to was not provided' });
    });

    // Input: content is not provided
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: "content was not provided" }
    // ChatGPT Usage: No
    test('Content not provided', async () => {
        const id = 'someUserId';
        const res = await request(app).post(`/api/chat/conversations/user/${id}`).send({
            to: 'alice123',
        });
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'content was not provided' });
    });

    // Input: params valid, but conversation already exists
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: "Conversation already exists" }
    // ChatGPT Usage: No
    test('Conversation already exists', async () => {
        const id = 'someUserId';
        User.findById.mockResolvedValue(user);
        Conversation.exists.mockResolvedValueOnce(true);
        const res = await request(app).post(`/api/chat/conversations/user/${id}`).send({
            to: 'alice123',
            content: 'Hello!',
        });
        expect(res.statusCode).toStrictEqual(409);
        expect(res.body).toStrictEqual({ error: 'Conversation already exists' });
    });
});
