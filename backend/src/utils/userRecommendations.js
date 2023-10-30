const { NONSHAREABLE, TWOBEDROOM, HABITS, NEUTRAL, POSINF, NEGINF } = require('./constants');

/**
 * Rank a set of users based on their corresponding aggregate score.
 *
 * @param {Array<Array<Object>>} userScores - [userId, score] pair
 * @returns {Array<ObjectID>} - user IDs ranked based on their score
 */
const generateUserRecommendations = (userScores) => {
    const userRanking = userScores.sort((user1, user2) => user2[1] - user1[1]);
    return userRanking.map((user) => user[0]);
};

/**
 * Calculate compatibility score for each possible match with the current user
 *
 * @param {User Object} currentUserPreferences - preferences of current user seeking roommates
 * @param {User Object} potentialMatchesPreferences - preferences of potential roommates
 * @returns {Array<Object>} - list of user id and score pairs
 */
const generateUserScores = (currentUserPreferences, potentialMatchesPreferences) => {
    let userScores = [];
    const minMaxRanges = calculateMinMaxRanges(currentUserPreferences, potentialMatchesPreferences);
    potentialMatchesPreferences.forEach((matchPreferences) => {
        let currentMatchScore = 0;

        const categoricalMetrics = [aggregateCategorialPreferenceScores, petFriendlinessScore, housingTypeScore];
        const numericalMetrics = [roommateCountScore, leaseLengthScore, priceRangeScore];

        categoricalMetrics.forEach((metric) => {
            currentMatchScore += metric(currentUserPreferences, matchPreferences);
        });

        numericalMetrics.forEach((metric) => {
            currentMatchScore += metric(currentUserPreferences, matchPreferences, minMaxRanges);
        });

        userScores.push([matchPreferences.userId, currentMatchScore]);
    });
    return userScores;
};

/**
 * This helper will aggrerate scores for the following preferences of a user:
 * [smoking, partying, drinking, noise]
 *
 * @param {User Object} currentUser - preferences of current user seeking roommates
 * @param {User Object} possibleMatch - preferences of potential roommates
 * @returns {Number} - matching score
 */
const aggregateCategorialPreferenceScores = (currentUser, possibleMatch) => {
    let score = 0;

    HABITS.forEach((preference) => {
        if (currentUser[preference] === possibleMatch[preference]) {
            score += 1;
        } else if (currentUser[preference] === NEUTRAL || possibleMatch[preference] === NEUTRAL) {
            score += 0.5;
        }
    });

    return score;
};

/**
 * This helper will aggregrate whether the users have the same pet preferences or not
 *
 * @param {User Object} currentUser - preferences of current user seeking roommates
 * @param {User Object} possibleMatch - preferences of potential roommates
 * @returns {Number} - matching score
 */
const petFriendlinessScore = (currentUser, possibleMatch) => {
    return currentUser.petFriendly === possibleMatch.petFriendly;
};

/**
 * This helper will aggregrate scores based on the type of housing the user prefers:
 * ['studio', '1-bedroom', '2-bedroom', 'other']
 *
 * @param {User Object} currentUser - preferences of current user seeking roommates
 * @param {User Object} possibleMatch - preferences of potential roommates
 * @returns {Number} - matching score
 */
const housingTypeScore = (currentUser, possibleMatch) => {
    if (currentUser.housingType in NONSHAREABLE || possibleMatch.housingType in NONSHAREABLE) {
        return 0;
    } else if (currentUser.housingType === TWOBEDROOM && possibleMatch.housingType === TWOBEDROOM) {
        return 1;
    } else {
        return 0.5;
    }
};

/**
 * This helper scores users based on how close the desired roommate count is
 *
 * @param {User Object} currentUser - preferences of current user seeking roommates
 * @param {User Object} possibleMatch - preferences of potential roommates
 * @returns {Number} - matching score normalized between 0 and 1
 */
const roommateCountScore = (currentUser, possibleMatch, minMaxRanges) => {
    // Min-max normalization to score the roommate such that the difference between the match is minimized
    const minRange = minMaxRanges.minRoomMateCountRange,
        maxRange = minMaxRanges.maxRoomMateCountRange;
    const headcountDiff = Math.abs(currentUser.roommateCount - possibleMatch.roommateCount);
    const normalizedDiff = (headcountDiff - minRange) / (maxRange - minRange);

    // Need to minimize difference
    return 1 - normalizedDiff;
};

/**
 * This helper scores users based on how similar the desired lease lengths are
 *
 * @param {User Object} currentUser - preferences of current user seeking roommates
 * @param {User Object} possibleMatch - preferences of potential roommates
 * @returns {Number} - matching score
 */
const leaseLengthScore = (currentUser, possibleMatch, minMaxRanges) => {
    // This needs max-min normalization across all differences
    const minRange = minMaxRanges.minLeaseLengthRange,
        maxRange = minMaxRanges.maxLeaseLengthRange;
    const leaseLengthDiff = Math.abs(currentUser.leaseLength - possibleMatch.leaseLength);
    const normalizedDiff = (leaseLengthDiff - minRange) / (maxRange - minRange);

    // Need to minimize difference
    return 1 - normalizedDiff;
};

/**
 * This helper scores users based on how much overlap there is in their price ranges.
 * It aims to rewards better for higher overlaps as it means users are more likely
 * to reach on a roommate agreement based on this.
 *
 * @param {User Object} currentUser - preferences of current user seeking roommates
 * @param {User Object} possibleMatch - preferences of potential roommates
 * @returns {Number} - matching score
 */
const priceRangeScore = (currentUser, possibleMatch, minMaxRanges) => {
    // This needs max-min normalization across all differences
    let overlapPriceRange =
        Math.min(currentUser.maxPrice, possibleMatch.maxPrice) - Math.max(currentUser.minPrice, possibleMatch.minPrice);

    // Score the match as 0 if there is no overlap at all
    if (overlapPriceRange <= 0) {
        return 0;
    }

    const minRange = minMaxRanges.minPriceOverlapRange,
        maxRange = minMaxRanges.maxPriceOverlapRange;
    const normalizedOverlap = (overlapPriceRange - minRange) / (maxRange - minRange);

    // Need to maximize overlap
    return normalizedOverlap;
};

/**
 * This helper calculates the min-max ranges of the following properties from a set of matches:
 * [roommateCount, leaseLength, priceOverlapRange]
 *
 * @param {User Object} currentUserPreferences - preferences of current user seeking roommates
 * @param {User Object} potentialMatchesPreferences - preferences of potential roommates
 */
const calculateMinMaxRanges = (currentUser, potentialMatches) => {
    let minRoomMateCountRange = POSINF,
        maxRoomMateCountRange = NEGINF;
    let minLeaseLengthRange = POSINF,
        maxLeaseLengthRange = NEGINF;
    let minPriceOverlapRange = POSINF,
        maxPriceOverlapRange = NEGINF;

    potentialMatches.forEach((potentialMatch) => {
        // Roommate Count Range
        let roommateCountRange = Math.abs(currentUser.roommateCount - potentialMatch.roommateCount);
        if (roommateCountRange >= maxRoomMateCountRange) {
            maxRoomMateCountRange = roommateCountRange;
        }
        if (roommateCountRange < minRoomMateCountRange) {
            minRoomMateCountRange = roommateCountRange;
        }

        // Lease Length Range
        let leaseLengthRange = Math.abs(currentUser.leaseLength - potentialMatch.leaseLength);
        if (leaseLengthRange >= maxLeaseLengthRange) {
            maxLeaseLengthRange = leaseLengthRange;
        }
        if (leaseLengthRange < minLeaseLengthRange) {
            minLeaseLengthRange = leaseLengthRange;
        }

        // Price Range
        let priceOverlapRange =
            Math.min(currentUser.maxPrice, potentialMatch.maxPrice) -
            Math.max(currentUser.minPrice, potentialMatch.minPrice);
        priceOverlapRange = priceOverlapRange > 0 ? priceOverlapRange : 0;
        if (priceOverlapRange >= maxPriceOverlapRange) {
            maxPriceOverlapRange = priceOverlapRange;
        }
        if (priceOverlapRange < minPriceOverlapRange) {
            minPriceOverlapRange = priceOverlapRange;
        }
    });
    return {
        minRoomMateCountRange: minRoomMateCountRange,
        maxRoomMateCountRange: maxRoomMateCountRange,
        minLeaseLengthRange: minLeaseLengthRange,
        maxLeaseLengthRange: maxLeaseLengthRange,
        minPriceOverlapRange: minPriceOverlapRange,
        maxPriceOverlapRange: maxPriceOverlapRange,
    };
};

module.exports = { generateUserRecommendations, generateUserScores };
