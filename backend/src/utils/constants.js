const { base64_encoding } = require("./utils");

// House Type related constants
const NONSHAREABLE = ['studio', '1-bedroom'];
const TWOBEDROOM = '2-bedroom';

// Limit related constants
const POSINF = Number.POSITIVE_INFINITY;
const NEGINF = Number.NEGATIVE_INFINITY;

// Habits and enum related constants
const HABITS = ['smoking', 'partying', 'drinking', 'noise'];
const NEUTRAL = 'neutral';

// Blocking behavior
const BLOCK_THRESHOLD = 5;

// Scam reporing behavior
const SCAM_THRESHOLD = 5;

// Default images
const DEFAULT_IMAGES = {
    'studio': base64_encoding('./src/utils/imgs/studio.png'),
    '1-bedroom': base64_encoding('./src/utils/imgs/1-bed.png'),
    '2-bedroom': base64_encoding('./src/utils/imgs/2-bed.png'),
    'other': base64_encoding('./src/utils/imgs/other.png'),
    'person': base64_encoding('./src/utils/imgs/person.png')
}

module.exports = { NONSHAREABLE, TWOBEDROOM, POSINF, NEGINF, HABITS, NEUTRAL, BLOCK_THRESHOLD, SCAM_THRESHOLD, DEFAULT_IMAGES };
