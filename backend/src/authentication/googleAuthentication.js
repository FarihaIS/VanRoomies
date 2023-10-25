const { OAuth2Client } = require('google-auth-library');
const dotenv = require('dotenv');

/**
 * Client Id refers to the CLIENT_ID set on the Google Developer
 * Console for our front end. This is used to validate Google Id
 * Token sent by a user after their frontend login attempt.
 */
dotenv.config();

/**
 * Middleware function used to check that the provided ID token is
 * valid and produced as a result of a login from Google Sign In API.
 * Successful execution of this middleware shall then redirect to the
 * redirect URI set by the client ID
 *
 * @param {Object} req - Request, contains authentication token
 * @param {Object} res - Response is used to send back status
 * @param {Function} next - Callback to pass control to next middleware
 */
const validateGoogleIdToken = async (req, res, next) => {
    const client = new OAuth2Client(process.env.GCP_CLIENT_ID, process.env.GCP_CLIENT_SECRET);
    const ticket = await client.verifyIdToken({
        idToken: req.body.idToken,
        audience: process.env.GCP_CLIENT_ID,
    });

    if (ticket == null) return res.sendStatus(401);

    next();
};

module.exports = validateGoogleIdToken;
