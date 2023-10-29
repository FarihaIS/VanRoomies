/**
 * Rank a set of users based on their corresponding aggregate score.
 *
 * @param {Array<Array<Object>>} userScores - (userId, score) pair
 */
const generateRecommendations = (listingScores) => {
    const listingRanking = listingScores.sort((listing1, listing2) => listing2[1] - listing1[1]);
    return listingRanking.map((listing) => listing[0]);
};
