const request = require('supertest');
const app = require('../../src/app');
const User = require('../../src/models/userModel');
jest.mock('../../src/models/userModel');
const Preferences = require('../../src/models/preferencesModel');
jest.mock('../../src/models/preferencesModel');
const Listing = require('../../src/models/listingModel');
jest.mock('../../src/models/listingModel');

// ChatGPT Usage: No
const createDummyPreferences = (id, extras) => {
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
        leaseLength: 6,
    };

    if (extras) {
        return { ...pref, ...extras };
    } else {
        return pref;
    }
};

// ChatGPT Usage: No
const createDummyUser = (userId, extras) => {
    const newUser = {
        _id: userId,
        firstName: 'First',
        lastName: 'Last',
        email: 'email@gmail.com',
        notRecommended: [],
    };
    if (extras) {
        return { ...newUser, ...extras };
    } else {
        return newUser;
    }
};

// ChatGPT Usage: No
const createDummyListing = (userId, extra) => {
    const listing = {
        _id: 'preferenceObjectId',
        userId,
        title: 'Dummy listing',
        housingType: 'studio',
        rentalPrice: 1500,
        listingDate: '2023-11-01',
        moveInDate: '2023-11-01',
        petFriendly: true,
        status: 'active',
        scamReportCount: 0,
    };

    if (extra) {
        return { ...listing, ...extra };
    } else {
        return listing;
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
    // ChatGPT Usage: No
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
    // ChatGPT Usage: No
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
    // ChatGPT Usage: No
    test('Valid userId for Preferences', async () => {
        const id = 'validId';
        User.findById.mockResolvedValue({ _id: id });
        Preferences.prototype.save.mockResolvedValue(createDummyPreferences(id));
        const res = await request(app).post(`/api/users/${id}/preferences`);
        expect(res.statusCode).toStrictEqual(201);
    });

    // Input: userId is not a valid id
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: "User not found" }
    // ChatGPT Usage: No
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
    // ChatGPT Usage: No
    test('Update Preferences for Valid User Id', async () => {
        const id = 'validId';
        const updates = {
            minPrice: 500,
            maxPrice: 1000,
        };
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
    // ChatGPT Usage: No
    test('Update Preferences for Invalid User Id', async () => {
        const id = 'invalidId';
        Preferences.findOneAndUpdate.mockResolvedValue(null);
        const res = await request(app).put(`/api/users/${id}/preferences`);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'Did not match any user!' });
    });
});

// Interface PUT /api/users/:userId/recommendations/users
describe('PUT user match exclusion for pair of users', () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    // Input: userId is a valid id of currently logged in user
    // Body: excludedId is a valid id
    // Expected status code: 200
    // Expected behavior: Add excluded user to list of notRecommended
    // Expected output: User object
    // ChatGPT Usage: No
    test('Successful exclusion - both user IDs are correct', async () => {
        const id = 'validId1';
        const body = { excludedId: 'validId2' };
        User.findById.mockResolvedValueOnce({ _id: id, notRecommended: [] });
        User.findById.mockResolvedValueOnce({ _id: body.excludedId, notRecommended: [] });
        User.findByIdAndUpdate.mockResolvedValueOnce({ _id: id, notRecommended: [body.excludedId] });
        User.findByIdAndUpdate.mockResolvedValueOnce({ _id: body.excludedId, notRecommended: [id] });
        const res = await request(app).put(`/api/users/${id}/recommendations/users`).send(body);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body.notRecommended).toStrictEqual([body.excludedId]);
    });

    // Input: userId is not a valid id
    // Body: excludedId is a valid id
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: 'Incorrect provided user ID' }
    // ChatGPT Usage: No
    test('Invalid userId for request parameters', async () => {
        const id = 'invalidId';
        const body = { excludedId: 'validId2' };
        User.findById.mockResolvedValueOnce(null);
        User.findById.mockResolvedValueOnce({ _id: body.excludedId, notRecommended: [] });
        const res = await request(app).put(`/api/users/${id}/recommendations/users`).send(body);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'Incorrect provided user ID' });
    });

    // Input: userId is a valid id
    // Body: excludedId is not a valid id
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: 'Incorrect provided user ID' }
    // ChatGPT Usage: No
    test('Invalid userId for request parameters', async () => {
        const id = 'invalidId';
        const body = { excludedId: 'validId2' };
        User.findById.mockResolvedValueOnce({ _id: id, notRecommended: [] });
        User.findById.mockResolvedValueOnce(null);
        const res = await request(app).put(`/api/users/${id}/recommendations/users`).send(body);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'Incorrect provided user ID' });
    });
});

// Interface GET /api/users/:userId/recommendations/users
describe('GET other user recommendations for a user', () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    // Input: userId is the invalid
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: 'Did not match any user!' }
    // ChatGPT Usage: No
    test('Invalid User ID provided', async () => {
        const id = 'userId';
        User.findById.mockResolvedValue(null);
        const res = await request(app).get(`/api/users/${id}/recommendations/users`);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'Did not match any user!' });
    });

    // Input: userId is the ID of the user whose matches we retrieve
    // Expected status code: 200
    // Expected behavior: return users without any specific order
    // Expected output: list User objects
    // ChatGPT Usage: No
    test('No set preferences - get all users', async () => {
        const id = 'userId';
        const userList = [createDummyUser('id1'), createDummyUser('id2'), createDummyUser('id3')];
        User.findById.mockResolvedValue(createDummyUser(id));
        Preferences.findOne.mockImplementation(() => ({
            lean: jest.fn().mockResolvedValue(null),
        }));
        User.find.mockResolvedValue(userList);
        const res = await request(app).get(`/api/users/${id}/recommendations/users`);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body.length).toStrictEqual(3);
    });

    // Input: userId is the ID of the user whose matches we retrieve
    // Expected status code: 200
    // Expected behavior: returns empty list because no tentative matches exist
    // Expected output: []
    // ChatGPT Usage: No
    test('Preferences set - No tentative matches exist!', async () => {
        const id = 'userId';
        User.findById.mockResolvedValue(createDummyUser(id));
        Preferences.findOne.mockImplementation(() => ({
            lean: jest.fn().mockResolvedValue(createDummyPreferences(id)),
        }));
        Preferences.find.mockImplementation(() => ({
            lean: jest.fn().mockResolvedValue([]),
        }));
        const res = await request(app).get(`/api/users/${id}/recommendations/users`);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body.length).toStrictEqual(0);
        expect(res.body).toStrictEqual([]);
    });

    // Input: userId is the ID of the user whose matches we retrieve
    // Expected status code: 200
    // Expected behavior: returns list of users sorted in order of best match
    // Expected output: list User objects
    // ChatGPT Usage: No
    test('Preferences set - Tentative matches exist!', async () => {
        const id = 'userId';
        const tentativeMatches = [
            createDummyPreferences('userid2', {
                minPrice: 1000,
                maxPrice: 2000,
                noise: 'regular',
                housingType: '2-bedroom',
                petFriendly: true,
                leaseLength: 10,
                roommateCount: 1,
            }),
            createDummyPreferences('userid3', {
                minPrice: 1200,
                maxPrice: 1900,
                noise: 'regular',
                drinking: 'regular',
                smoking: 'regular',
                housingType: '1-bedroom',
                petFriendly: false,
            }),
            createDummyPreferences('userid4', {
                minPrice: 2000,
                maxPrice: 3000,
                noise: 'no-noise',
                drinking: 'no-drinking',
                housingType: 'studio',
            }),
            createDummyPreferences('userid5', {
                minPrice: 800,
                maxPrice: 1200,
                noise: 'no-noise',
                drinking: 'no-drinking',
                housingType: 'other',
                roommateCount: 5,
            }),
        ];
        User.findById.mockResolvedValueOnce(createDummyUser(id));
        Preferences.findOne.mockImplementation(() => ({
            lean: jest
                .fn()
                .mockResolvedValue(
                    createDummyPreferences(id, { housingType: '2-bedroom', smoking: 'no-smoking', roommateCount: 0 }),
                ),
        }));
        Preferences.find.mockImplementation(() => ({
            lean: jest.fn().mockResolvedValue(tentativeMatches),
        }));
        User.findById.mockImplementation((id) => ({
            select: jest.fn().mockReturnThis(),
            lean: jest.fn().mockResolvedValue({
                id,
                firstName: 'Test',
                lastName: 'Test',
                profilePicture: 'Test',
                bio: 'Test',
            }),
        }));
        const res = await request(app).get(`/api/users/${id}/recommendations/users`);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body.length).toStrictEqual(4);

        /**
         * Scores at the time of writing test as below:
         * id2 -> 6.8, id3 -> 4.7, id4 -> 5.5, id5 -> 5.2
         * ################ Order ought to be 2 -> 4 -> 5 -> 3 ################
         */
        expect(res.body[0].id).toStrictEqual('userid2');
        expect(res.body[1].id).toStrictEqual('userid4');
        expect(res.body[2].id).toStrictEqual('userid5');
        expect(res.body[3].id).toStrictEqual('userid3');
    });
});

// Interface GET /api/users/:userId/recommendations/listings
describe('GET listing recommendations for a user', () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    // Input: userId is the invalid
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: 'Did not match any user!' }
    // ChatGPT Usage: No
    test('Invalid User ID provided', async () => {
        const id = 'userId';
        User.findById.mockResolvedValue(null);
        const res = await request(app).get(`/api/users/${id}/recommendations/listings`);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'Did not match any user!' });
    });

    // Input: userId is the ID of the user whose recommended listings we retrieve
    // Expected status code: 200
    // Expected behavior: return users without any specific order
    // Expected output: list Listing objects
    // ChatGPT Usage: No
    test('No set preferences - get all listings', async () => {
        const id = 'userId';
        const listings = [createDummyListing('id1'), createDummyListing('id2'), createDummyListing('id3')];
        User.findById.mockResolvedValue(createDummyUser(id));
        Preferences.findOne.mockImplementation(() => ({
            lean: jest.fn().mockResolvedValue(null),
        }));
        Listing.find.mockImplementation(() => ({
            lean: jest.fn().mockResolvedValue(listings),
        }));
        const res = await request(app).get(`/api/users/${id}/recommendations/listings`);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body.length).toStrictEqual(3);
    });

    // Input: userId is the ID of the user whose recommended listings we retrieve
    // Expected status code: 200
    // Expected behavior: returns empty list because no tentative listings exist
    // Expected output: []
    // ChatGPT Usage: No
    test('Preferences set - No tentative listings exist!', async () => {
        const id = 'userId';
        User.findById.mockResolvedValue(createDummyUser(id));
        Preferences.findOne.mockImplementation(() => ({
            lean: jest.fn().mockResolvedValue(createDummyPreferences(id)),
        }));
        Listing.find.mockImplementation(() => ({
            lean: jest.fn().mockResolvedValue([]),
        }));
        const res = await request(app).get(`/api/users/${id}/recommendations/listings`);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body.length).toStrictEqual(0);
        expect(res.body).toStrictEqual([]);
    });

    // Input: userId is the ID of the user whose recommended listings we retrieve
    // Expected status code: 200
    // Expected behavior: returns list of Listings objects sorted in order of best match
    // Expected output: list Listing objects
    // ChatGPT Usage: No
    test('Preferences set - Tentative listings exist!', async () => {
        const id = 'userId';
        const tentativeMatchListings = [
            createDummyListing('userid2', {
                _id: 'listing2',
                housingType: 'studio',
                rentalPrice: 2000,
                moveInDate: '2023-11-01',
                petFriendly: true,
            }),
            createDummyListing('userid3', {
                _id: 'listing3',
                housingType: '1-bedroom',
                rentalPrice: 2500,
                moveInDate: '2024-01-01',
                petFriendly: true,
            }),
            createDummyListing('userid4', {
                _id: 'listing4',
                housingType: '2-bedroom',
                rentalPrice: 1500,
                moveInDate: '2023-12-01',
                petFriendly: false,
            }),
            createDummyListing('userid5', {
                _id: 'listing5',
                housingType: 'other',
                rentalPrice: 800,
                moveInDate: '2024-11-01',
                petFriendly: false,
            }),
        ];
        User.findById.mockResolvedValueOnce(createDummyUser(id));
        Preferences.findOne.mockImplementation(() => ({
            lean: jest
                .fn()
                .mockResolvedValue(createDummyPreferences(id, { housingType: '1-bedroom', roommateCount: 0 })),
        }));
        Listing.find.mockImplementation(() => ({
            lean: jest.fn().mockResolvedValue(tentativeMatchListings),
        }));
        Listing.findById.mockImplementation((id) => ({
            lean: jest.fn().mockResolvedValue({ id }),
        }));
        const res = await request(app).get(`/api/users/${id}/recommendations/listings`);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body.length).toStrictEqual(4);
        /**
         * Scores at the time of writing test as below:
         * id2 -> 2.5, id3 -> 2.8..., id4 -> 1.4..., id5 -> 0
         * ################ Order ought to be 3 -> 2 -> 4 -> 5 ################
         */
        expect(res.body[0].id).toStrictEqual('listing3');
        expect(res.body[1].id).toStrictEqual('listing2');
        expect(res.body[2].id).toStrictEqual('listing4');
        expect(res.body[3].id).toStrictEqual('listing5');
    });
});
