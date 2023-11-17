const request = require("supertest");
const app = require("../../src/app");

jest.mock("../../src/chat/messageStore");
const messageStore = require("../../src/chat/messageStore");

// Interface GET /api/conversations/user/:userId
describe("GET conversations from user", () => {
  // Input: userId is valid id for a User that exists in the database
  // Expected status code: 200
  // Expected behavior: return a list of Conversations that the User is a part of
  // Expected output: Array of Conversation objects
  test("Valid userId", async () => {
    const id = "someUserId";
    const res = await request(app).get(`/api/chat/conversations/user/${id}`);
    expect(res.statusCode).toStrictEqual(200);
	expect(res.body.every(obj => obj.users && obj.messages && obj.users.includes(id))).toBe(true);
  });

  // Input: userId is not a valid id
  // Expected status code: 404
  // Expected behavior: return an error message
  // Expected output: { error: "User not found" }
  test("Invalid userId", async () => {
	const id = "invalid";
	const res = await request(app).get(`/api/chat/conversations/user/${id}`);
	expect(res.statusCode).toStrictEqual(404);
	expect(res.body).toStrictEqual({ error: "User not found" });
  });
});

// Interface POST /api/conversations/user/:userId
describe("POST a new conversation between users ", () => {
  // Input: userId and to are valid ids for Users that exist in the database; content exists and is defined
  // Expected status code: 201
  // Expected behavior: create a new Conversation in the database
  // Expected output: none
  test("Valid input", async () => {
	const id = "someUserId";
	const res = await request(app).post(`/api/chat/conversations/user/${id}`).send(
		{
			to: "alice123",
			content: "Hello!"
		}
	);
	expect(res.statusCode).toStrictEqual(201);
	expect(res.body).toStrictEqual({});
  });

  // Input: userId is not a valid id
  // Expected status code: 404
  // Expected behavior: return an error message
  // Expected output: { error: "Conversation could not be started" }
  test("Invalid userId", async () => {
	const id = "invalid";
	const res = await request(app).post(`/api/chat/conversations/user/${id}`).send(
		{
			to: "alice123",
			content: "Hello!"
		}
	);
	expect(res.statusCode).toStrictEqual(404);
	expect(res.body).toStrictEqual({ error: "Conversation could not be started" });
  });

  // Input: receiving userId (to) is not a valid id
  // Expected status code: 404
  // Expected behavior: return an error message
  // Expected output: { error: "Conversation could not be started" }
  test("Invalid receiving userId", async () => {
	const id = "someUserId";
	const res = await request(app).post(`/api/chat/conversations/user/${id}`).send(
		{
			to: "invalid",
			content: "Hello!"
		}
	);
	expect(res.statusCode).toStrictEqual(404);
	expect(res.body).toStrictEqual({ error: "Conversation could not be started" });
  });

  // Input: receiving userId (to) is not provided
  // Expected status code: 404
  // Expected behavior: return an error message
  // Expected output: { error: "to was not provided" }
  test("Missing receiving userId", async () => {
	const id = "someUserId";
	const res = await request(app).post(`/api/chat/conversations/user/${id}`).send(
		{
			content: "Hello!"
		}
	);
	expect(res.statusCode).toStrictEqual(404);
	expect(res.body).toStrictEqual({ error: "to was not provided" });
  });
	

  // Input: content is not provided
  // Expected status code: 404
  // Expected behavior: return an error message
  // Expected output: { error: "content was not provided" }
  test("Content not provided", async () => {
	const id = "someUserId";
	const res = await request(app).post(`/api/chat/conversations/user/${id}`).send(
		{
			to: "alice123",
		}
	);
	expect(res.statusCode).toStrictEqual(404);
	expect(res.body).toStrictEqual({ error: "content was not provided" });
  });
});
