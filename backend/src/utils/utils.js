const validator = require('validator');

/**
 * Rank a set of IDs based on their corresponding aggregate score.
 * Generalized for both user scoring and listing scoring.
 *
 * @param {Array<Array<Object>>} scoreIdPair - (ID, score) pair
 * @returns {Array<ObjectID>} - IDs ranked based on their score
 */
const generateRecommendations = (scoreIdPair) => {
    const rankedPairs = scoreIdPair.sort((item1, item2) => item2[1] - item1[1]);
    return rankedPairs.map((pair) => pair[0]);
};

/**
 * This helper will aggregrate scores based on pet preferences for a user against
 * another entity from the database collection
 *
 * @param {User Object} user - preferences of user
 * @param {User Object} other - otherEntity
 * @returns {Number} - matching score
 */
const calculatePetFriendlinessScore = (user, other) => {
    return user.petFriendly === other.petFriendly ? 1 : 0;
};

const sanitize = (message) => {
    return validator.escape(validator.trim(message));
};

module.exports = { generateRecommendations, calculatePetFriendlinessScore, sanitize };
