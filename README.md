# Chess

## Download latest release
Go to the [release page](https://github.com/yuvasaro/chess/releases) to get the latest build.

### Steps to run
1. Create a new directory and place the downloaded jar file in it.
2. Create the file `.env` in the new directory you just created.
3. Add your Discord bot token to `.env` like so: `TOKEN=<YOUR-TOKEN>`. (You must have a Discord bot application set up prior.)
4. Open a terminal window and navigate to your new directory containing the `.jar` file and `.env` file.
5. Run the jar file with the `bot` argument: `java -jar chess-<version>.jar bot`.

### Discord bot commands
`!play @player1 @player2` starts a game with `player1` as White and `player2` as Black.

`!move <move>` plays a move. The move should be in standard chess notation.

- The command `!move resign` will resign the game.
- The command `!move draw` will offer a draw.
- The command `!move <yes|no>` will accept or decline a draw offer.

`!help` displays all the available bot commands.

---

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
