# VanRoomies
A match-making housing app built using ExpressJS and Javascript for the backend web server and Android Java for the mobile application front end built for CPEN 321 - Software Engineering. The application features live chatting functionality, Tinder-like swipe on the UI side, and a custom recommandation built based on shared preferences between users.

## Backend
The backend code mostly follows the MVC pattern and is organized under `/backend/src/` into the following:
  1. `models` declares the logical blocks that mostly correspond to the database entities declared for our backend
  2. `routes` declares the REST APIs that provide a means of communication for the mobile application with the above models
  3. `chat` contains the firebase and miscellaneous code required to provide live chatting functionality
  4. `authentication` provides the middlewares that prototype the JWT authorization and Google OAuth
  5. `utils` mostly consists of helpers related to the custom recommendation built for user matching

You can find the tests under `/backend/test` which are configured to run in regression testing mode as per our [jest-testing.yml](https://github.com/dlalaj/VanRoomies/blob/main/.github/workflows/jest-testing.yml) workflow

## Frontend
Contains all code built for the mobile application that is served by the ExpressJS server.
  1. The frontend code is mostly placed under `frontend/app/src/main`. 
  2. The fronted tests are writted using Espresso and placed under `/frontend/app/src/androidTest`.

## CI/CD Pipeline
Check the backend workflows configured for linting and running our tests in regression mode on each push under [.github/workflows](https://github.com/dlalaj/VanRoomies/blob/main/.github/workflows/jest-testing.yml)
  1. Linter workflow found [here](https://github.com/dlalaj/VanRoomies/blob/main/.github/workflows/backend-code-style.yaml)
  2. Jest workflow found [here](https://github.com/dlalaj/VanRoomies/blob/main/.github/workflows/jest-testing.yml)
