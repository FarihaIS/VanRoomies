const request = require('supertest');
const app = require('../../src/app');
const { logErrors, errorHandler } = require('../../src/middlewares');
const User = require('../../src/models/userModel');
jest.mock('../../src/models/userModel');

// Interface GET /
describe('GET home page', () => {
    // Expected status code: 200
    // Expected behavior: return a simple hello world message
    // Expected output: Hello World!
    test('Valid input', async () => {
        const res = await request(app).get('/');
        expect(res.statusCode).toStrictEqual(200);
        expect(res.text).toStrictEqual('Hello World!');
    });
});

// Interface POST /api/firebase_token
describe('POST firebase token', () => {
    beforeAll(() => {
        User.findById.mockImplementation((id) => {
            if (id === 'invalid') {
                return Promise.resolve(null);
            }
            return Promise.resolve({
                _id: id,
                firstName: 'John',
                lastName: 'Doe',
                email: 'test@github.com',
                updateOne: jest.fn(),
            });
        });
    });
    // Input: token and userId are defined and valid
    // Expected status code: 200
    // Expected behavior: save the token on the database
    // Expected output: { message: "Token saved" }
    test('Valid input', async () => {
        const res = await request(app).post('/api/firebase_token').send({
            token: 'someToken',
            userId: 'someUserId',
        });
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body).toStrictEqual({ message: 'Token saved' });
    });

    // Input: token is not defined
    // Expected status code: 400
    // Expected behavior: return an error message
    // Expected output: { error: "Token or userId is missing" }
    test('Missing token', async () => {
        const res = await request(app).post('/api/firebase_token').send({
            userId: 'someUserId',
        });
        expect(res.statusCode).toStrictEqual(400);
        expect(res.body).toStrictEqual({ error: 'Token or userId is missing' });
    });

    // Input: userId is not defined
    // Expected status code: 400
    // Expected behavior: return an error message
    // Expected output: { error: "Token or userId is missing" }
    test('Missing userId', async () => {
        const res = await request(app).post('/api/firebase_token').send({
            token: 'someToken',
        });
        expect(res.statusCode).toStrictEqual(400);
        expect(res.body).toStrictEqual({ error: 'Token or userId is missing' });
    });

    // Input: userId is not a valid id
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: "User not found" }
    test('Invalid userId', async () => {
        const res = await request(app).post('/api/firebase_token').send({
            token: 'someToken',
            userId: 'invalid',
        });
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'User not found' });
    });
});

// Misc middleware.js handlers
describe('Middlewares', () => {
    test('logErrors Function', () => {
        const error = new Error('Test Error Message');
        const next = jest.fn();
        jest.spyOn(console, 'error').mockImplementation();

        logErrors(error, 'req', 'res', next);

        expect(console.error).toHaveBeenCalledWith('Test Error Message');
        expect(next).toHaveBeenCalledWith(error);
    });

    test('errorHandler Function headersSent=>True', () => {
        const error = new Error('Test Error Message');
        const res = { headersSent: true, status: jest.fn(), json: jest.fn() };
        const next = jest.fn();

        errorHandler(error, 'req', res, next);

        expect(next).toHaveBeenCalledWith(error);
    });

    test('errorHandler Function headersSent=>False', () => {
        const error = new Error('Test Error Message');
        const res = { headersSent: false, status: jest.fn(), json: jest.fn() };
        const next = jest.fn();

        errorHandler(error, 'req', res, next);

        expect(res.status).toHaveBeenCalledWith(500);
        expect(res.json).toHaveBeenCalledWith({ error: error.message });
    });
});
