const request = require('supertest');
const app = require('../../src/app');
const User = require('../../src/models/userModel');
jest.mock('../../src/models/userModel');
const Listing = require('../../src/models/listingModel');
jest.mock('../../src/models/listingModel');

const createDummyListing = (id, extra) => {
    const listing = {
        _id: 'preferenceObjectId',
        userId: id,
        title: "Dummy listing",
        housingType: 'studio',
        rentalPrice: 1500,
        listingDate: '2023-11-01',
        moveInDate: '2023-11-01',
        petFriendly: true,
        status: 'active',
        scamReportCount: 0
    };

    if (extra) {
        return {...listing, ...extra};
    } else {
        return listing;
    }
};

// Interface GET /api/listings/:listingId
describe('GET Listing by id', () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    // Input: listingId is a valid id
    // Expected status code: 200
    // Expected behavior: return Listing object that matches the parameter Id
    // Expected output: Listing object
    test('Valid listingId', async () => {
        const id = 'validId';
        Listing.findById.mockResolvedValue(createDummyListing(id));
        const res = await request(app).get(`/api/listings/${id}`);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body.userId).toBe(id);
    });

    // Input: userId is not a valid id
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: "User not found" }
    test('Invalid userId', async () => {
        const id = 'invalidId';
        Listing.findById.mockResolvedValue(null);
        const res = await request(app).get(`/api/listings/${id}`);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'Listing not found' });
    });
});

// Interface GET /api/listings/user/:userId
describe('GET All Listings for a user ID', () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    // Input: userId is a valid id
    // Expected status code: 200
    // Expected behavior: return Listing objects that matches the user Id
    // Expected output: collection of Listing objects
    test('Valid user ID', async () => {
        const id = 'validId';
        const setOfHouses = [createDummyListing(id), createDummyListing(id)];
        Listing.find.mockResolvedValue(setOfHouses);
        const res = await request(app).get(`/api/listings/user/${id}`);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body.length).toBe(2);
        expect(res.body[0].userId).toBe(id);
        expect(res.body[1].userId).toBe(id);
    });

    // Input: userId is invalid
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: 'No listing found for given user ID' }
    test('Invalid user ID', async () => {
        const id = 'invalidId';
        Listing.find.mockResolvedValue(null);
        const res = await request(app).get(`/api/listings/user/${id}`);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'No listing found for given user ID' });
    });
});

// Interface POST /api/listings/
describe('Post a new listing for a given userID', () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    // Input: userId in body is a valid user id
    // Expected status code: 201
    // Expected behavior: return Listing objects that was created
    // Expected output: Listing object
    test('Valid lister ID', async () => {
        const id = 'validId';
        const body = createDummyListing(id);
        User.findById.mockResolvedValue({_id: id});
        Listing.prototype.save.mockResolvedValue(createDummyListing(id));
        const res = await request(app).post(`/api/listings/`).send(body);
        expect(res.statusCode).toStrictEqual(201);
    });

    // Input: userId in body is invalid
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: 'Incorrect user ID provided' }
    test('Invalid lister ID', async () => {
        const id = 'invalidId';
        const body = createDummyListing(id);
        User.findById.mockResolvedValue(null);
        const res = await request(app).post(`/api/listings/`).send(body);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'Incorrect user ID provided' });
    });
});
