const request = require('supertest');
const app = require('../../src/app');
const User = require('../../src/models/userModel');
jest.mock('../../src/models/userModel');
const Preferences = require('../../src/models/preferencesModel');
jest.mock('../../src/models/preferencesModel');

const createDummyPreferences = (id, extra) => {
    const pref = {
        _id: 'preferenceObjectId',
        userId: id,
        minPrice: 1000,
        maxPrice: 2000,
        housingType: 'studio',
        roommateCount: 0,
        petFriendly: true,
        smoking: 'neutral',
        partying: 'neutral',
        drinking: 'neutral',
        noise: 'neutral',
        gender: 'male',
        moveInDate: '2023-11-01',
        leaseLength: 10
    };

    if (extra) {
        return {...pref, ...extra};
    } else {
        return pref;
    }
};

// Interface GET /api/users/:userId/preferences
describe('GET preferences by user id', () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    // Input: userId is a valid id
    // Expected status code: 200
    // Expected behavior: return Preference object that matches the userId
    // Expected output: Preference object
    test('Valid userId for Preferences', async () => {
        const id = 'validId';
        Preferences.findOne.mockResolvedValue(createDummyPreferences(id));
        const res = await request(app).get(`/api/users/${id}/preferences`);
        expect(res.statusCode).toStrictEqual(200);
    });

    // Input: userId is not a valid id
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: "User not found" }
    test('Invalid userId', async () => {
        const id = 'invalidId';
        Preferences.findOne.mockResolvedValue(null);
        const res = await request(app).get(`/api/users/${id}/preferences`);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'Did not match any user!' });
    });
});

// Interface POST /api/users/:userId/preferences
describe('POST preferences for a user id', () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    // Input: userId is a valid id
    // Expected status code: 201
    // Expected behavior: Create Preference object for the given user ID
    // Expected output: Preference object
    test('Valid userId for Preferences', async () => {
        const id = 'validId';
        User.findById.mockResolvedValue({_id: id});
        Preferences.prototype.save.mockResolvedValue(createDummyPreferences(id));
        const res = await request(app).post(`/api/users/${id}/preferences`);
        expect(res.statusCode).toStrictEqual(201);
    });

    // Input: userId is not a valid id
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: "User not found" }
    test('Invalid userId', async () => {
        const id = 'invalidId';
        User.findById.mockResolvedValue(null);
        const res = await request(app).post(`/api/users/${id}/preferences`);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'Did not match any user!' });
    });
});

// Interface PUT /api/users/:userId/preferences
describe('PUT preferences for a user id', () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    // Input: userId is a valid id
    // Expected status code: 200
    // Expected behavior: Update Preference object for the given user ID
    // Expected output: Preference object
    test('Update Preferences for Valid User Id', async () => {
        const id = 'validId';
        const updates = {
            minPrice: 500,
            maxPrice: 1000
        }
        Preferences.findOneAndUpdate.mockResolvedValue(createDummyPreferences(id, updates));
        const res = await request(app).put(`/api/users/${id}/preferences`).send(updates);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body.minPrice).toBe(500);
        expect(res.body.maxPrice).toBe(1000);
    });

    // Input: userId is not a valid id
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: 'Did not match any user! }
    test('Update Preferences for Invalid User Id', async () => {
        const id = 'invalidId';
        Preferences.findOneAndUpdate.mockResolvedValue(null);
        const res = await request(app).put(`/api/users/${id}/preferences`);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'Did not match any user!' });
    });
});

// Interface GET /api/users/:userId/recommendations/users
describe('GET other user recommendations for a user', () => {

});
