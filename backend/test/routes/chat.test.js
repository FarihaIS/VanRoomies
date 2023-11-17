const app = require("../../src/app");

// Interface GET /api/conversations/:conversationId
describe("Retrieve a conversation", () => {
	// Input: conversationId is valid id for a Conversation that exists in the database
	// Expected status code: 200
	// Expected behavior: return the Conversation object
	// Expected output: Conversation object
	test("Valid conversationId", async () => {
		const id = "someId";
		const res = await app.get("/api/conversations/5f9d3b2b9d3b2b9d3b2b9d3b");
	});
	// test("Valid Photo", async () => {
	// const res = await app.post("/photo/")
	// .attach("photo", "test/res/test_photo.png");
	// expect(res.status).toStrictEqual(201);
	// expect(await Photo.getAllPhotos()
	// .toEqual(expect.arrayContaining(["test_photo.png"])));
	// });



	// Input: no photo
	// Expected status code: 401
	// Expected behavior: database is unchanged
	// Expected output: None
	// test("No Photo", async () => {
	// //...
	// });
});
