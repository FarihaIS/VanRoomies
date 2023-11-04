const jwt = require('jsonwebtoken');

/**
 * The following functions were implementing following alongside the guide
 * linked below which helped in learning for JWT as an authentication
 * mechanism for requests
 * https://www.digitalocean.com/community/tutorials/nodejs-jwt-expressjs
 */

/**
 * Signing Key that is used to generate authentication token is obtained
 * from environment variable. This can be generated based on the node.js
 * crypto module as following from a node shell:
 * ```require('crypto').randomBytes(64).toString('hex')```
 */

/**
 * Generates an authentication token for a given user. This authentication
 * token can be used by this user to verify the legitimacy of their further
 * requests to the backend server when demanding private data to the user
 *
 * @param {Object} user - User object from mongoDB collection
 * @returns {string} - the Json Web Token String signed through private key
 */
const generateAuthenticationToken = (user) => {
    const payload = {
        userId: user._id,
        fistName: user.firstName,
        lastName: user.lastName,
        email: user.email,
    };

    return jwt.sign(payload, process.env.JWT_SIGNING_KEY);
};

/**
 * Middleware function used to authenticate user based on the token provided
 * by in the request headers. This will be run before passing control
 * to a route handlers. If authentication fails, route handlers are not taken
 * and the response packages status code to send to the client.
 *
 * @param {Object} req - Request, contains authentication token
 * @param {Object} res - Response is used to send back status
 * @param {Function} next - Callback to pass control to next middleware
 * @returns
 */
const authenticateJWT = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];

    if (!token) return res.sendStatus(401);

    jwt.verify(token, process.env.JWT_SIGNING_KEY, (err, user) => {
        if (err) {
            return res.sendStatus(403);
        }

        req.user = user;
        next();
    });
};

module.exports = { generateAuthenticationToken, authenticateJWT };
