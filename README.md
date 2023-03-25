# Chess

## Development setup

### Requirements
Make sure you have Maven installed on your computer.

### Steps
1. Clone this repository and `cd` into it.
2. Run `mvn package` to build the jar.
3. Change directory into the target folder: `cd target`.
4. Create a file `config.json` with the bot token like so: `{ "token":"<YOUR-TOKEN>" }`.
5. Run the jar file to test: `java -jar chess-<version>.jar bot`.

Rebuild the jar as needed when changes are made to the code.

## Run the game (console or bot)

Console: `java -jar chess-<version>.jar console <player1> <player2>`

Bot: `java -jar chess-<version>.jar bot`
