/**
 * Rank a set of users based on their corresponding aggregate score.
 *
 * @param {Array<Array<Object>>} userScores - [userId, score] pair
 */
const generateRecommendations = (userScores) => {
    const userRanking = userScores.sort((user1, user2) => user2[1] - user1[1]);
    return userRanking.map((user) => user[0]);
};

/**
 * Calculate compatibility score for each possible match with the current user
 *
 * @param {User Object} currentUserPreferences - preferences of current user seeking roommates
 * @param {User Object} potentialMatchesPreferences - preferences of potential roommates
 */
const generateUserScores = (currentUserPreferences, potentialMatchesPreferences) => {
    let userScores = [];
    potentialMatchesPreferences.forEach((matchPreferences) => {
        let currentMatchScore = 0;

        const metrics = [aggregateCategorialPreferenceScores, petFriendlinessScore, housingTypeScore];

        metrics.forEach((metric) => {
            currentMatchScore += metric(currentUserPreferences, matchPreferences);
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

    const categories = ['smoking', 'partying', 'drinking', 'noise'];
    categories.forEach((preference) => {
        if (currentUser[preference] === possibleMatch[preference]) {
            score += 1;
        } else if (currentUser[preference] === 'neutral' || possibleMatch[preference] === 'neutral') {
            score += 0.5;
        } else {
            score += 0;
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
 * This helper will aggregrate whether the users based on the housing type they seek:
 * ['studio', '1-bedroom', '2-bedroom', 'other']
 *
 * @param {User Object} currentUser - preferences of current user seeking roommates
 * @param {User Object} possibleMatch - preferences of potential roommates
 * @returns {Number} - matching score
 */
const housingTypeScore = (currentUser, possibleMatch) => {
    const nonShareable = ['studio', '1-bedroom'];
    if (currentUser.housingType in nonShareable || possibleMatch.housingType in nonShareable) {
        return 0;
    } else if (currentUser.housingType === '2-bedroom' && possibleMatch.housingType === '2-bedroom') {
        return 1;
    } else {
        return 0.5;
    }
};

/**
 * This helper scores users based on how much overlap there is in their desired roommate count
 *
 * @param {User Object} currentUser - preferences of current user seeking roommates
 * @param {User Object} possibleMatch - preferences of potential roommates
 * @returns {Number} - matching score
 */
const roommateCountScore = (currentUser, possibleMatch) => {
    // This needs max-min normalization across all differences
    const headcountDiff = Math.abs(currentUser.roommateCount - possibleMatch.roommateCount);
};

/**
 * This helper scores users based on how much overlap there is in their desired lease lengths
 *
 * @param {User Object} currentUser - preferences of current user seeking roommates
 * @param {User Object} possibleMatch - preferences of potential roommates
 * @returns {Number} - matching score
 */
const leaseLengthScore = (currentUser, possibleMatch) => {
    // This needs max-min normalization across all differences
    const leaseLengthDiff = Math.abs(currentUser.leaseLength - possibleMatch.leaseLength);
};

/**
 * This helper scores users based on how much overlap there is in their price ranges
 *
 * @param {User Object} currentUser - preferences of current user seeking roommates
 * @param {User Object} possibleMatch - preferences of potential roommates
 * @returns {Number} - matching score
 */
const priceRangeScore = (currentUser, possibleMatch) => {
    // This needs max-min normalization across all differences
    let overlapPriceRange =
        Math.min(currentUser.maxPrice, possibleMatch.maxPrice) - Math.max(currentUser.minPrice, possibleMatch.minPrice);
    overlapPriceRange = overlapPriceRange >= 0 ? overlapPriceRange : 0;
};

module.exports = { generateUserScores, generateRecommendations };
