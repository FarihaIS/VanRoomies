const request = require('supertest');
const app = require('../../src/app');
const User = require('../../src/models/userModel');
jest.mock('../../src/models/userModel');
const jwt = require('jsonwebtoken');
jest.mock('jsonwebtoken');
const Preferences = require('../../src/models/preferencesModel');
jest.mock('../../src/models/preferencesModel');
const Listing = require('../../src/models/listingModel');
jest.mock('../../src/models/listingModel');
const mongoose = require('mongoose');
jest.mock('mongoose', () => {
    const original = jest.requireActual('mongoose');
    return {
        ...original,
        startSession: jest.fn(),
    };
});
const mockedSession = {
    startTransaction: jest.fn(),
    commitTransaction: jest.fn(),
    abortTransaction: jest.fn(),
};

// Interface POST /api/users/login
describe('POST user login', () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    // Input: email already exists in the database
    // Expected status code: 200
    // Expected behavior: return User object that matches the email
    // Expected output: User object
    test('Valid and existing email', async () => {
        const email = 'user@github.com';
        User.findOne.mockResolvedValue({
            _id: 'someUserId',
            firstName: 'John',
            lastName: 'Doe',
            email,
        });
        const res = await request(app).post(`/api/users/login`).send({ email });
        expect(res.statusCode).toStrictEqual(200);
    });

    // Input: email does not exist in the database
    // Expected status code: 201
    // Expected behavior: create a new User in the database
    // Expected output: {userToken: String, user: User}
    test('Valid and new email', async () => {
        const email = 'existing@github.com';
        User.findOne.mockResolvedValue(null);
        User.prototype.save.mockResolvedValue({
            _id: 'someUserId',
            firstName: 'John',
            lastName: 'Doe',
            email,
        });
        jwt.sign.mockReturnValue('someToken');

        const res = await request(app).post(`/api/users/login`).send({ email });
        expect(res.statusCode).toStrictEqual(201);
        expect(res.body).toStrictEqual({ userId: 'someUserId', userToken: 'someToken' });
    });

    // Input: invalid email format
    // Expected status code: 400
    // Expected behavior: return an error message
    // Expected output: { error: "Invalid email format" }
    // test('Invalid email format', async () => {
    // 	const email = 'invalid';
    // 	User.findOne.mockImplementationOnce(() => Promise.resolve(null));
    // 	const res = await request(app).post(`/api/users/login`).send({ email });
    // 	expect(res.statusCode).toStrictEqual(400);
    // 	expect(res.body).toStrictEqual({ error: 'Invalid email format' });
    // });
});

// Interface GET /api/users/:userId
describe('GET user by id', () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    // Input: userId is a valid id
    // Expected status code: 200
    // Expected behavior: return User object that matches the userId
    // Expected output: User object
    test('Valid userId', async () => {
        const id = 'someUserId';
        User.findById.mockResolvedValue({
            _id: id,
            firstName: 'John',
            lastName: 'Doe',
            email: 'test@github.com',
        });
        const res = await request(app).get(`/api/users/${id}`);
        expect(res.statusCode).toStrictEqual(200);
    });

    // Input: userId is not a valid id
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: "User not found" }
    test('Invalid userId', async () => {
        const id = 'invalid';
        User.findById.mockResolvedValue(null);
        const res = await request(app).get(`/api/users/${id}`);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'User not found' });
    });
});

// Interface PUT /api/users/:userId
describe('PUT user by id', () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    // Input: userId is a valid id
    // Expected status code: 200
    // Expected behavior: update User object that matches the userId
    // Expected output: User object
    test('Valid userId', async () => {
        const id = 'someUserId';
        const updatedUser = {
            firstName: 'John',
            lastName: 'Doe',
            email: 'new@github.com',
        };
        User.findByIdAndUpdate.mockResolvedValue({ _id: id, ...updatedUser });
        const res = await request(app).put(`/api/users/${id}`).send(updatedUser);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body).toStrictEqual({ _id: id, ...updatedUser });
    });

    // Input: userId is not a valid id
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: "User not found" }
    test('Invalid userId', async () => {
        const id = 'invalid';
        User.findByIdAndUpdate.mockResolvedValue(null);
        const res = await request(app).put(`/api/users/${id}`).send({});
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'User not found' });
    });
});

// Interface DELETE /api/users/:userId
describe('DELETE user by id', () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    beforeEach(() => {
        mongoose.startSession.mockReturnValueOnce(mockedSession);
    });

    // Input: userId is a valid id
    // Expected status code: 200
    // Expected behavior: delete User object that matches the userId
    // Expected output: User object
    test('Valid userId', async () => {
        const id = 'someUserId';
        User.findByIdAndDelete.mockResolvedValue({ _id: id });
        const res = await request(app).delete(`/api/users/${id}`);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body).toStrictEqual({ _id: id });
    });

    // Input: userId is not a valid id
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: "User not found" }
    test('Invalid userId', async () => {
        const id = 'invalid';
        User.findByIdAndDelete.mockResolvedValue(null);
        const res = await request(app).delete(`/api/users/${id}`);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'User not found' });
    });
});
