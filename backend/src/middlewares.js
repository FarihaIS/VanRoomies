// ChatGPT Usage: No
function logErrors(err, req, res, next) {
    console.error(err.message);
    next(err);
}

// ChatGPT Usage: No
function errorHandler(err, req, res, next) {
    if (res.headersSent) {
        return next(err);
    }
    res.status(500);
    res.json({ error: err.message });
}

module.exports = {
    logErrors,
    errorHandler,
};
