const request = require('supertest');
const app = require('../../src/app');
const User = require('../../src/models/userModel');
jest.mock('../../src/models/userModel');
const Listing = require('../../src/models/listingModel');
jest.mock('../../src/models/listingModel');

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
        Listing.find.mockImplementation(() => ({
            sort: jest.fn().mockResolvedValue(setOfHouses),
        }));
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
        Listing.find.mockImplementation(() => ({
            sort: jest.fn().mockResolvedValue(null),
        }));
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
        User.findById.mockResolvedValue({ _id: id });
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

// Interface PUT /api/listings/:listingId
describe('PUT listing based on listing ID', () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    // Input: listingId is a valid listing id
    // Expected status code: 200
    // Expected behavior: Update Listing object for the given listing ID
    // Expected output: Listing object
    test('Update Listing for valid listing ID', async () => {
        const id = 'validListingId';
        const updates = {
            rentalPrice: 500,
            housingType: '1-bedroom',
        };
        Listing.findByIdAndUpdate.mockResolvedValue(createDummyListing('userId', updates));
        const res = await request(app).put(`/api/listings/${id}`).send(updates);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body.rentalPrice).toBe(500);
        expect(res.body.housingType).toBe('1-bedroom');
    });

    // Input: listingId is an invalid id
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: 'Listing not found' }
    test('Update Preferences for Invalid Listing Id', async () => {
        const id = 'invalidListingId';
        const updates = {
            rentalPrice: 500,
            housingType: '1-bedroom',
        };
        Listing.findByIdAndUpdate.mockResolvedValue(null);
        const res = await request(app).put(`/api/listings/${id}`).send(updates);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'Listing not found' });
    });
});

// Interface POST /api/listings/:listingId/report
describe('Report Listing as scam', () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    // Input: listingId is a valid listing id
    // Input: reportedId is a valid user ID who reported listing
    // Expected status code: 200
    // Expected behavior: return Listing objects that was reported
    // Expected output: Listing object
    test('Successful report', async () => {
        const id = 'validId';
        const body = { reporterId: 'IAmReporter' };
        const dummyListing = createDummyListing('I am User', { _id: id });

        User.findById.mockResolvedValue({ _id: body.reporterId });
        Listing.findById.mockResolvedValue(dummyListing);

        dummyListing.scamReportCount += 1;
        User.findByIdAndUpdate.mockResolvedValue({ _id: body.reporterId });
        Listing.findByIdAndUpdate.mockResolvedValue(dummyListing);

        const res = await request(app).post(`/api/listings/${id}/report`).send(body);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body._id).toStrictEqual(id);
        expect(res.body.userId).toStrictEqual('I am User');
        expect(res.body.scamReportCount).toStrictEqual(1);
    });

    // Input: listingId is a valid listing id
    // Input: reportedId is a valid user ID who reported listing
    // Expected status code: 200
    // Expected behavior: return Listing objects that was reported and delete listing
    // Expected output: Listing object
    test('Successful report - with deletion', async () => {
        const id = 'validId';
        const body = { reporterId: 'IAmReporter' };
        const dummyListing = createDummyListing('I am User', { _id: id, scamReportCount: 4 });

        User.findById.mockResolvedValue({ _id: body.reporterId });
        Listing.findById.mockResolvedValue(dummyListing);

        dummyListing.scamReportCount += 1;
        User.findByIdAndUpdate.mockResolvedValue({ _id: body.reporterId });
        Listing.findByIdAndUpdate.mockResolvedValue(dummyListing);

        const res = await request(app).post(`/api/listings/${id}/report`).send(body);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body._id).toStrictEqual(id);
        expect(res.body.userId).toStrictEqual('I am User');
        expect(res.body.scamReportCount).toStrictEqual(5);
    });

    // Input: listingId is an invalid listing id
    // Input: reportedId is a valid user ID who reported listing
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: 'Listing reporting failed!' }
    test('Unsuccessful report - invalid listing ID', async () => {
        const id = 'validId';
        const body = { reporterId: 'IAmReporter' };

        User.findById.mockResolvedValue({ _id: body.reporterId });
        Listing.findById.mockResolvedValue(null);

        const res = await request(app).post(`/api/listings/${id}/report`).send(body);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'Listing reporting failed!' });
    });

    // Input: listingId is a valid listing id
    // Input: reportedId is an invalid user ID who reported listing
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: 'Listing reporting failed!' }
    test('Unsuccessful report - invalid listing ID', async () => {
        const id = 'validId';
        const body = { reporterId: 'IAmReporter' };
        const dummyListing = createDummyListing('I am User', { _id: id, scamReportCount: 4 });

        User.findById.mockResolvedValue(null);
        Listing.findById.mockResolvedValue(dummyListing);

        const res = await request(app).post(`/api/listings/${id}/report`).send(body);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'Listing reporting failed!' });
    });
});

// Interface DELETE /api/listings/:listingId
describe('DELETE listing based on listing ID', () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    // Input: listingId is a valid listing id
    // Expected status code: 200
    // Expected behavior: Delete given listing based on its ID
    // Expected output: Listing object that was deleted
    test('Delete Listing for valid listing ID', async () => {
        const id = 'validListingId';
        Listing.findByIdAndDelete.mockResolvedValue(createDummyListing('userId'));
        const res = await request(app).delete(`/api/listings/${id}`);
        expect(res.statusCode).toStrictEqual(200);
        expect(res.body.userId).toBe('userId');
    });

    // Input: listingId is an invalid id
    // Expected status code: 404
    // Expected behavior: return an error message
    // Expected output: { error: 'Listing not found' }
    test('Delete Listing for Invalid User Id', async () => {
        const id = 'invalidListingId';
        Listing.findByIdAndDelete.mockResolvedValue(null);
        const res = await request(app).delete(`/api/listings/${id}`);
        expect(res.statusCode).toStrictEqual(404);
        expect(res.body).toStrictEqual({ error: 'Listing not found' });
    });
});
