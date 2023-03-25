# Chess

## Download latest release
Go to the [release page](https://github.com/yuvasaro/chess/releases) to get the latest build.

## Development setup

### Requirements
Make sure you have Maven installed on your computer.

### Steps
1. Clone this repository and `cd` into it.
2. Run `mvn package` to build the jar.
3. Create a file `.env` in the project root folder containing the bot token like so: `TOKEN=<YOUR-TOKEN>`.
4. Run the jar file to test: `java -jar target/chess-<version>.jar bot`.

Rebuild the jar as needed when changes are made to the code.

## Run the game from the project (console or bot)

Console: `java -jar target/chess-<version>.jar console <player1> <player2>`

Bot: `java -jar target/chess-<version>.jar bot`
