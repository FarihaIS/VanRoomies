const getConversationsByUser = jest.fn((userId) => {
	if (!userId || userId === "invalid") {
		return null;
	}
	return [{ 
		users: [ userId, "alice123" ],
		messages: [],
	},
	{
		users: [ userId, "bob456" ],
		messages: [],
	}];
  });

const sendMessage = jest.fn((_, fromId, toId) => {
	if (!fromId || !toId || fromId === "invalid" || toId === "invalid") {
		return false;
	}
	return true;
});

module.exports = {
	getConversationsByUser,
	sendMessage,
};
