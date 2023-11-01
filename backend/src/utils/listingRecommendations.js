const { NONSHAREABLE, POSINF, NEGINF } = require('./constants');
const { calculatePetFriendlinessScore } = require('./utils');

/**
 * Calculate compatibility score for each possible listing with the current user
 *
 * @param {User Object} userPreferences - preferences of current user seeking housing
 * @param {User Object} listings - collection of possible listings that could be a match
 * @returns {Array<Object>} - list of listing id and score pairs
 */
const generateListingScores = (userPreferences, listings) => {
    let listingScores = [];

    const [closestListing, farthestListing] = getClosestFarthestMoveDates(userPreferences, listings);

    listings.forEach((listing) => {
        let listingScore = 0;

        // Need to perform score calculation based on: [housingType, rentalPrice, petFriendly, moveInDate]
        const metrics = [calculateHousingTypeScore, calculatePetFriendlinessScore, calculatePriceScore];

        metrics.forEach((metric) => {
            listingScore += metric(userPreferences, listing);
        });

        listingScore += calculateMoveInDateScore(listing, closestListing, farthestListing);

        listingScores.push([listing._id, listingScore]);
    });

    return listingScores;
};

/**
 * Compute match score for type of housing between user preferences and given listing
 *
 * @param {Object} userPreferences - preferences of current user seeking roommates
 * @param {Object} listing - potential listing match
 * @returns {Number} - matching score normalized between 0 and 1
 */
const calculateHousingTypeScore = (userPreferences, listing) => {
    if (userPreferences.housingType === listing.housingType) {
        return 1;
    } else if (userPreferences.housingType in NONSHAREABLE && listing.housingType in NONSHAREABLE) {
        return 0.5;
    }
    return 0;
};

/**
 * Compute match score for user price range and listing rental price. This max-min normalizes
 * the listing price on the given user range and rewards disproportionately so that prices
 * close to minPrice are awarded closer to a perfect score of 1.
 *
 * @param {Object} userPreferences - preferences of current user seeking roommates
 * @param {Object} listing - potential listing match
 * @returns {Number} - matching score normalized between 0 and 1
 */
const calculatePriceScore = (userPreferences, listing) => {
    if (listing.rentalPrice > userPreferences.maxPrice || listing.rentalPrice < userPreferences.minPrice) {
        return 0;
    }
    return 1 - (listing.rentalPrice - userPreferences.minPrice) / (userPreferences.maxPrice - userPreferences.minPrice);
};

const calculateMoveInDateScore = (listing, closestListing, farthestListing) => {
    const normalizedTimeWindow = (listing.moveInDate - closestListing) / (farthestListing - closestListing);
    return 1 - normalizedTimeWindow;
};

const getClosestFarthestMoveDates = (userPreferences, listings) => {
    let minimalWindow = POSINF,
        maximalWindow = NEGINF;
    let closestListing;
    let farthestListing;
    listings.forEach((listing) => {
        let timeDifference = Math.abs(userPreferences.moveInDate - listing.moveInDate);
        if (timeDifference > maximalWindow) {
            maximalWindow = timeDifference;
            farthestListing = listing.moveInDate;
        }
        if (timeDifference <= minimalWindow) {
            minimalWindow = timeDifference;
            closestListing = listing.moveInDate;
        }
    });
    return [closestListing, farthestListing];
};

module.exports = { generateListingScores };