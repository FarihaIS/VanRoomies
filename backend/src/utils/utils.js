const validator = require('validator');
const fs = require('fs');

/**
 * Rank a set of IDs based on their corresponding aggregate score.
 * Generalized for both user scoring and listing scoring.
 *
 * @param {Array<Array<Object>>} scoreIdPair - (ID, score) pair
 * @returns {Array<ObjectID>} - IDs ranked based on their score
 */
// ChatGPT Usage: No
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
// ChatGPT Usage: No
const calculatePetFriendlinessScore = (user, other) => {
    return user.petFriendly === other.petFriendly ? 1 : 0;
};

// ChatGPT Usage: No
const sanitize = (message) => {
    return validator.escape(validator.trim(message));
};

/** Encode an image to base64 String, help from this article
 * https://stackoverflow.com/questions/24523532/how-do-i-convert-an-image-to-a-base64-encoded-data-url-in-sails-js-or-generally
 *
 * @param {String} fileName - Name of file to read from
 * @returns {String} - base64 encoding of string
 */
const base64Encoding = (fileName) => {
    var bitmap = fs.readFileSync(fileName);
    return Buffer.from(bitmap).toString('base64');
};

module.exports = { generateRecommendations, calculatePetFriendlinessScore, sanitize, base64Encoding };
