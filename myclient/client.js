const io = require("socket.io-client");

const socket = io("https://localhost:3000", { autoConnect: false, rejectUnauthorized: false });
const readline = require("readline");

async function sendPrivateMessage(message, to) {
	const response = await socket.emitWithAck('private message', { content: message, to: to });
	console.log(response);
}
socket.on('user connected', (msg) => {
	console.log(msg);
});
// Add a disconnect listener
socket.on('disconnect', () => {
  console.log('The client has disconnected!');
});

socket.on("connect_error", (err) => {
	if (err.message === "invalid username") {
		console.error("invalid username");
	} else {
		console.error(err);
	}
});

socket.on('private message', async ({ content, from }) => {
	console.log(`${from} sent you a message: ${content}`);
});

// socket.onAny((eventName, ...args) => {
//   console.log(eventName);
//   console.log(args);
// });

const rl = readline.createInterface({
    input: process.stdin, 
    output: process.stdout,
	terminal: false,
})

function ask(question) {
    rl.question(question, (answer) => {
        rl.write(`The answer received:  ${answer}\n`);
		socket.auth = { userId: answer };
		socket.connect();
    })
}

function startMessaging() {
	rl.question("Who are you sending this to: \n", (answer) => {
		const userId = answer;
		rl.question("What is your message: \n", async (msg) => {
			await sendPrivateMessage(msg, userId);
			startMessaging();
		});
	});
}

socket.on('connect', async () => {
	console.log('Client has connected to the server!');
	// const users = getUsers();
	startMessaging();
});

ask("What's your userId: ");
