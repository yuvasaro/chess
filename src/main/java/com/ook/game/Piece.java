package com.ook.game;

import java.util.ArrayList;
import java.awt.Point;
import java.util.Arrays;
import java.util.Map;

/**
 * Piece class
 */
public class Piece {
    // Teams
    public static final int WHITE = 0;
    public static final int BLACK = 1;

    // Pieces
    public static final int PAWN = 1;
    public static final int KNIGHT = 2;
    public static final int BISHOP = 3;
    public static final int ROOK = 4;
    public static final int QUEEN = 5;
    public static final int KING = 6;
    public static final int KING_ENDGAME = 7;
    public static final Map<String, Integer> letterPieceMapping = Map.of(
            "P", Piece.PAWN,
            "N", Piece.KNIGHT,
            "B", Piece.BISHOP,
            "R", Piece.ROOK,
            "Q", Piece.QUEEN,
            "K", Piece.KING
    );

    // Instance variables
    private int type;
    private final int team;
    private Point location;

    /**
     * Returns a list of moves for the given piece
     * @param board the chessboard
     * @param piece the piece
     * @return a list of moves for the piece
     */
    public static ArrayList<Move> getMoves(Board board, Piece piece) {
        int type = piece.getType();
        Point location = piece.getLocation();

        // Return list of moves corresponding to which piece the piece is
        return switch (type) {
            case PAWN -> getPawnMoves(board, piece, location.x, location.y);
            case KNIGHT -> getKnightMoves(board, piece, location.x, location.y);
            case BISHOP -> getBishopMoves(board, piece, location.x, location.y);
            case ROOK -> getRookMoves(board, piece, location.x, location.y);
            case QUEEN -> getQueenMoves(board, piece, location.x, location.y);
            case KING -> getKingMoves(board, piece, location.x, location.y);
            default -> null;
        };

    }

    /**
     * Gets a list of moves in the given direction for the piece
     * @param board the chessboard
     * @param piece the piece
     * @param x the piece's x coordinate
     * @param y the piece's y coordinate
     * @param dx the x direction to search
     * @param dy the y direction to search
     * @return a list of possible moves in a direction for the piece
     */
    public static ArrayList<Move> getMovesInDirection(Board board, Piece piece, int x, int y, int dx, int dy) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        Point initialCoords = piece.getLocation();

        x += dx;
        y += dy;
        Piece otherPiece = board.get(x, y);
        Point destination = new Point(x, y);

        // Add all squares in path while there are no pieces in the way
        while (otherPiece == null && board.isInBounds(x, y)) {
            possibleMoves.add(new Move(piece, initialCoords, destination, null, null));

            // Get next point and piece in the current direction
            x += dx;
            y += dy;
            otherPiece = board.get(x, y);
            destination = new Point(x, y);
        }

        // Check the piece that the while loop encountered
        if (board.isInBounds(x, y) && otherPiece != null) {
            // Add possible move if other piece is on the opposite team
            if (piece.getTeam() != otherPiece.getTeam()) {
                possibleMoves.add(new Move(piece, initialCoords, destination, otherPiece, destination));
            }
        }

        return possibleMoves;
    }

    /**
     * Gets moves for pawn
     * @param board the chessboard
     * @param pawn the pawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @return a list of possible moves
     */
    public static ArrayList<Move> getPawnMoves(Board board, Piece pawn, int x, int y) {
        ArrayList<Move> pawnMoves = new ArrayList<>();
        Point initialCoords = pawn.getLocation();

        int team = pawn.getTeam();
        boolean isWhite = (team == WHITE);
        int moveDir = isWhite ? 1 : -1; // Move direction
        int whitePawnStartY = 1;
        int blackPawnStartY = 6;

        // One square ahead
        if (board.get(x, y + moveDir) == null) {
            Point destination = new Point(x, y + moveDir);
            pawnMoves.add(new Move(pawn, initialCoords, destination, null, null));
        }

        // Two squares ahead
        if (pawnMoves.size() == 1) { // 1 square ahead was valid
            if ((isWhite && y == whitePawnStartY && board.get(x, y + 2 * moveDir) == null) ||
                    (!isWhite && y == blackPawnStartY && board.get(x, y + 2 * moveDir) == null)) {
                Point destination = new Point(x, y + 2 * moveDir);
                pawnMoves.add(new Move(pawn, initialCoords, destination, null, null));
            }
        }

        // Capture
        Point leftDiagonal = new Point(x - 1, y + moveDir);
        Piece leftDiagonalPiece = board.get(x - 1, y + moveDir);
        Point rightDiagonal = new Point(x + 1, y + moveDir);
        Piece rightDiagonalPiece = board.get(x + 1, y + moveDir);

        if (leftDiagonalPiece != null && leftDiagonalPiece.getTeam() != team) {
            pawnMoves.add(new Move(pawn, initialCoords, leftDiagonal, leftDiagonalPiece, leftDiagonal));
        }
        if (rightDiagonalPiece != null && rightDiagonalPiece.getTeam() != team) {
            pawnMoves.add(new Move(pawn, initialCoords, rightDiagonal, rightDiagonalPiece, rightDiagonal));
        }

        // En passant
        if ((isWhite && y == 4) || (!isWhite && y == 3)) { // Check if on correct rank
            Point leftEP = new Point(x - 1, y);
            Piece leftPiece = board.get(leftEP);
            Point rightEP = new Point(x + 1, y);
            Piece rightPiece = board.get(rightEP);

            if (leftPiece != null && leftPiece.getTeam() != team && leftPiece.getType() == PAWN &&
                    board.getMoveCounter().get(leftPiece) == 1) {
                pawnMoves.add(new Move(pawn, initialCoords, leftDiagonal, leftPiece, leftEP));
            }
            if (rightPiece != null && rightPiece.getTeam() != team && rightPiece.getType() == PAWN &&
                    board.getMoveCounter().get(rightPiece) == 1) {
                pawnMoves.add(new Move(pawn, initialCoords, rightDiagonal, rightPiece, rightEP));
            }
        }

        return pawnMoves;
    }

    /**
     * Gets moves for knight
     * @param board the chessboard
     * @param knight the knight
     * @param x the x coordinate
     * @param y the y coordinate
     * @return a list of possible moves
     */
    public static ArrayList<Move> getKnightMoves(Board board, Piece knight, int x, int y) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        int team = knight.getTeam();
        Point initialCoords = knight.getLocation();

        // All 8 possible squares
        ArrayList<Point> possibleSquares = new ArrayList<>(Arrays.asList(
                new Point(x - 2, y + 1),
                new Point(x - 2, y - 1),
                new Point(x + 2, y + 1),
                new Point(x + 2, y - 1),
                new Point(x - 1, y + 2),
                new Point(x + 1, y + 2),
                new Point(x - 1, y - 2),
                new Point(x + 1, y - 2)
        ));

        for (int i = 0; i < possibleSquares.size(); i++) {
            Point square = possibleSquares.get(i);

            // Remove out of bounds moves
            if (!board.isInBounds(square.x, square.y)) {
                possibleSquares.remove(i);
                i--;
                continue;
            }

            Piece otherPiece = board.get(square);

            // Empty square is valid
            if (otherPiece == null) {
                continue;
            }

            // Cannot go to square with piece of same team
            if (otherPiece.getTeam() == team) {
                possibleSquares.remove(i);
                i--;
            }
        }

        // Create move objects for all possible destinations
        for (Point destination : possibleSquares) {
            possibleMoves.add(new Move(knight, initialCoords, destination, board.get(destination), destination));
        }

        return possibleMoves;
    }

    /**
     * Gets moves for bishop
     * @param board the chessboard
     * @param bishop the bishop
     * @param x the x coordinate
     * @param y the y coordinate
     * @return a list of possible moves
     */
    public static ArrayList<Move> getBishopMoves(Board board, Piece bishop, int x, int y) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        // Northwest
        possibleMoves.addAll(getMovesInDirection(board, bishop, x, y, -1, 1));
        // Northeast
        possibleMoves.addAll(getMovesInDirection(board, bishop, x, y, 1, 1));
        // Southwest
        possibleMoves.addAll(getMovesInDirection(board, bishop, x, y, -1, -1));
        // Southeast
        possibleMoves.addAll(getMovesInDirection(board, bishop, x, y, 1, -1));

        return possibleMoves;
    }

    /**
     * Gets moves for rook
     * @param board the chessboard
     * @param rook the rook
     * @param x the x coordinate
     * @param y the y coordinate
     * @return a list of possible moves
     */
    public static ArrayList<Move> getRookMoves(Board board, Piece rook, int x, int y) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        // North
        possibleMoves.addAll(getMovesInDirection(board, rook, x, y, 0, 1));
        // East
        possibleMoves.addAll(getMovesInDirection(board, rook, x, y, 1, 0));
        // South
        possibleMoves.addAll(getMovesInDirection(board, rook, x, y, 0, -1));
        // West
        possibleMoves.addAll(getMovesInDirection(board, rook, x, y, -1, 0));

        return possibleMoves;
    }

    /**
     * Gets moves for queen
     * @param board the chessboard
     * @param queen the queen
     * @param x the x coordinate
     * @param y the y coordinate
     * @return a list of possible moves
     */
    public static ArrayList<Move> getQueenMoves(Board board, Piece queen, int x, int y) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        // North
        possibleMoves.addAll(getMovesInDirection(board, queen, x, y, 0, 1));
        // East
        possibleMoves.addAll(getMovesInDirection(board, queen, x, y, 1, 0));
        // South
        possibleMoves.addAll(getMovesInDirection(board, queen, x, y, 0, -1));
        // West
        possibleMoves.addAll(getMovesInDirection(board, queen, x, y, -1, 0));
        // Northwest
        possibleMoves.addAll(getMovesInDirection(board, queen, x, y, -1, 1));
        // Northeast
        possibleMoves.addAll(getMovesInDirection(board, queen, x, y, 1, 1));
        // Southwest
        possibleMoves.addAll(getMovesInDirection(board, queen, x, y, -1, -1));
        // Southeast
        possibleMoves.addAll(getMovesInDirection(board, queen, x, y, 1, -1));

        return possibleMoves;
    }

    /**
     * Gets moves for king
     * @param board the chessboard
     * @param king the king
     * @param x the x coordinate
     * @param y the y coordinate
     * @return a list of possible moves
     */
    public static ArrayList<Move> getKingMoves(Board board, Piece king, int x, int y) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        int team = king.getTeam();
        int oppTeam = (team == Piece.WHITE) ? Piece.BLACK : Piece.WHITE;
        Point initialCoords = king.getLocation();
        int kingStartX = 4;
        int whiteKingStartY = 0;
        int blackKingStartY = 7;

        // All 8 squares
        ArrayList<Point> possibleSquares = new ArrayList<>(Arrays.asList(
                new Point(x, y + 1),
                new Point(x + 1, y),
                new Point(x, y - 1),
                new Point(x - 1, y),
                new Point(x - 1, y + 1),
                new Point(x + 1, y + 1),
                new Point(x - 1, y - 1),
                new Point(x + 1, y - 1)
        ));

        // Remove illegal moves
        for (int i = 0; i < possibleSquares.size(); i++) {
            Point square = possibleSquares.get(i);

            // Remove out of bounds moves
            if (!board.isInBounds(square.x, square.y)) {
                possibleSquares.remove(i);
                i--;
                continue;
            }

            Piece otherPiece = board.get(square);

            // Empty square is valid
            if (otherPiece == null) {
                continue;
            }

            // Cannot go to square with piece of same team
            if (otherPiece.getTeam() == team) {
                possibleSquares.remove(i);
                i--;
            }
        }

        for (Point destination : possibleSquares) {
            possibleMoves.add(new Move(king, initialCoords, destination, board.get(destination), destination));
        }

        // Castle
        if (x == kingStartX && board.getMoveCounter().get(king) == 0) {
            // Check if king is in check
            boolean inCheck = false;
            for (Piece opp : board.getTeamPieces(oppTeam)) {
                if (opp.getType() != Piece.KING) {
                    for (Move move : Piece.getMoves(board, opp)) {
                        if (move.getDestination().equals(king.getLocation())) {
                            inCheck = true;
                            break;
                        }
                    }
                }
            }

            if (!inCheck && ((team == WHITE && y == whiteKingStartY) || (team == BLACK && y == blackKingStartY))) {
                // Short castle
                int checkX = x + 1;
                while (checkX < 7) {
                    if (board.get(checkX, y) != null) {
                        break;
                    }
                    checkX++;
                }
                // Check for rook
                Piece checkRook;
                if (checkX == 7) {
                    checkRook = board.get(checkX, y);
                    if (checkRook != null && checkRook.getTeam() == team && checkRook.getType() == ROOK &&
                            board.getMoveCounter().get(checkRook) == 0) {
                        possibleMoves.add(new Move(king, initialCoords, new Point(x + 2, y), null, null));
                    }
                }

                // Long castle
                checkX = x - 1;
                while (checkX > 0) {
                    if (board.get(checkX, y) != null) {
                        break;
                    }
                    checkX--;
                }
                // Check for rook
                if (checkX == 0) {
                    checkRook = board.get(checkX, y);
                    if (checkRook != null && checkRook.getTeam() == team && checkRook.getType() == ROOK &&
                            board.getMoveCounter().get(checkRook) == 0) {
                        possibleMoves.add(new Move(king, initialCoords, new Point(x - 2, y), null, null));
                    }
                }
            }
        }

        return possibleMoves;
    }

    /**
     * Piece constructor
     * @param team the team of the piece
     * @param type the integer representation of the piece
     * @param location the location of the piece
     */
    public Piece(int team, int type, Point location) {
        this.team = team;
        this.type = type;
        this.location = location;
    }

    /**
     * Getter for team
     * @return the team
     */
    public int getTeam() {
        return team;
    }

    /**
     * Gets the type of the piece
     * @return the piece integer value
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the piece integer value
     * @param type the new piece integer value
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Getter for location
     * @return the location of the piece
     */
    public Point getLocation() {
        return location;
    }

    /**
     * Setter for location
     * @param location the new location of th piece
     */
    public void setLocation(Point location) {
        this.location = location;
    }
}
