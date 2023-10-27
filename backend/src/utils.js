const validator = require('validator');

const sanitize = (message) => {
    return validator.escape(validator.trim(message));
};

module.exports = {
    sanitize,
};
