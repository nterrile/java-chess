/**
 * Board.java
 * 
 * @author terrilen
 * @version  2015-1-29
 */
import java.lang.Math;
import java.util.ArrayList;
public class Board
{
    private Piece[][] board;
    private Piece[][] previousBoard;
    private ArrayList<String> listOfBoards;
    private int colOfAvailableEnPassant;
    private String playersTurn;
    private int fiftyMoveRule;

    public Board()
    {
        board = new Piece[8][8];
        previousBoard = new Piece[8][8];
        listOfBoards = new ArrayList<String>();
        colOfAvailableEnPassant = -1;
        playersTurn = "white";
        fiftyMoveRule = 0;

        //set pieces
        board[0][0] = new Piece("black", "rook");
        board[0][1] = new Piece("black", "knight");
        board[0][2] = new Piece("black", "bishop");
        board[0][3] = new Piece("black", "queen");
        board[0][4] = new Piece("black", "king");
        board[0][5] = new Piece("black", "bishop");
        board[0][6] = new Piece("black", "knight");
        board[0][7] = new Piece("black", "rook");
        for (int col = 0; col < 8; col++)
        {
            board[1][col] = new Piece("black", "pawn");
            for (int row = 2; row < 6; row++)
            {
                board[row][col] = new Piece("", "blank");
            }
            board[6][col] = new Piece("white", "pawn");
        }
        board[7][0] = new Piece("white", "rook");
        board[7][1] = new Piece("white", "knight");
        board[7][2] = new Piece("white", "bishop");
        board[7][3] = new Piece("white", "queen");
        board[7][4] = new Piece("white", "king");
        board[7][5] = new Piece("white", "bishop");
        board[7][6] = new Piece("white", "knight");
        board[7][7] = new Piece("white", "rook");

        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                previousBoard[row][col] = board[row][col];
            }
        }

        this.addBoardConfiguration();
    }

    public Piece get(int row, int col)
    {
        return board[row][col];
    }

    public void move(String coordinates)
    {
        if (this.moveIsValid(coordinates))
        {
            int colFrom = getValueFromCoordinates(coordinates, 0);
            int rowFrom = getValueFromCoordinates(coordinates, 1);
            int colTo = getValueFromCoordinates(coordinates, 2);
            int rowTo = getValueFromCoordinates(coordinates, 3);

            if (board[rowFrom][colFrom].getType().equals("pawn") || 
            !board[rowTo][colTo].getColor().equals(""))
            {
                fiftyMoveRule = 0;
            }
            else
            {
                fiftyMoveRule++;
            }

            this.move(rowFrom, colFrom, rowTo, colTo);
            board[rowTo][colTo].setHasMoved();

            this.addBoardConfiguration();

            if (playersTurn.equals("white"))
            {
                playersTurn = "black";
            }
            else
            {
                playersTurn = "white";
            }
        }
    }

    public void move(int rowFrom, int colFrom, int rowTo, int colTo)
    {
        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                previousBoard[row][col] = board[row][col];
            }
        }

        String typeFrom = board[rowFrom][colFrom].getType();
        String colorFrom = board[rowFrom][colFrom].getColor();

        board[rowTo][colTo] = board[rowFrom][colFrom];
        board[rowFrom][colFrom] = new Piece("", "blank");

        //castling kingside
        if (typeFrom.equals("king") && colTo - colFrom == 2)
        {
            board[rowFrom][5] = board[rowFrom][7];
            board[rowFrom][7] = new Piece("", "blank");
        }
        //castling queenside
        if (typeFrom.equals("king") && colFrom - colTo == 2)
        {
            board[rowFrom][3] = board[rowFrom][0];
            board[rowFrom][0] = new Piece("", "blank");
        }

        //en passant
        if (typeFrom.equals("pawn") && colorFrom.equals("white") &&
        colOfAvailableEnPassant == colTo && rowFrom == 3)
        {
            board[3][colOfAvailableEnPassant] = new Piece("", "blank");
        }
        if (typeFrom.equals("pawn") && colorFrom.equals("black") &&
        colOfAvailableEnPassant == colTo && rowFrom == 4)
        {
            board[4][colOfAvailableEnPassant] = new Piece("", "blank");
        }

        //set en passant variable
        if (typeFrom.equals("pawn") && Math.abs(rowFrom - rowTo) == 2)
        {
            colOfAvailableEnPassant = colFrom;
        }
        else
        {
            colOfAvailableEnPassant = -1;
        }
    }

    public boolean moveIsValid(String coordinates)
    {
        //game is not over
        if (this.isGameOver())
        {
            return false;
        }
        //coordinates are in proper notation
        if (!isProperNotation(coordinates))
        {
            return false;
        }

        int colFrom = getValueFromCoordinates(coordinates, 0);
        int rowFrom = getValueFromCoordinates(coordinates, 1);
        int colTo = getValueFromCoordinates(coordinates, 2);
        int rowTo = getValueFromCoordinates(coordinates, 3);

        //player is moving the right color piece
        if (!playersTurn.equals(board[rowFrom][colFrom].getColor()))
        {
            return false;
        }

        if (moveIsValid(rowFrom, colFrom, rowTo, colTo))
        {
            //make sure move doesn't put the player's own king in check
            if (this.doesMovePutPlayerInCheck(rowFrom, colFrom, rowTo, colTo))
            {
                return false;
            }
        }
        else
        {
            return false;
        }
        return true;
    }

    public boolean moveIsValid(int rowFrom, int colFrom, int rowTo, int colTo)
    {
        //piece is moved to a different square than the one it is on
        if (colFrom == colTo && rowFrom == rowTo)
        {
            return false;
        }

        String typeFrom = board[rowFrom][colFrom].getType();
        String colorFrom = board[rowFrom][colFrom].getColor();
        String typeTo = board[rowTo][colTo].getType();
        String colorTo = board[rowTo][colTo].getColor();

        //piece is not blank
        if (typeFrom.equals("blank"))
        {
            return false;
        }
        //piece is moved to a blank square or a square of the opposite color
        if (colorFrom.equals(colorTo))
        {
            return false;
        }

        if (typeFrom.equals("king"))
        {
            if (colTo - colFrom == 2 && !this.getPlayerInCheck().equals(colorFrom))
            {
                if (!(!board[rowFrom][colFrom].getHasMoved() &&
                    board[rowFrom][7].getType().equals("rook") && !board[rowFrom][7].getHasMoved()
                    && this.isHorizontalClear(rowFrom, colFrom, rowFrom, 7)))
                {
                    return false;
                }
            }
            else if (colFrom - colTo == 2 && !this.getPlayerInCheck().equals(colorFrom))
            {
                if (!(!board[rowFrom][colFrom].getHasMoved() &&
                    board[rowFrom][0].getType().equals("rook") && !board[rowFrom][0].getHasMoved()
                    && this.isHorizontalClear(rowFrom, 0, rowFrom, colFrom)))
                {
                    return false;
                }
            }
            else if (Math.abs(rowFrom - rowTo) > 1 || Math.abs(colFrom - colTo) > 1)
            {
                return false;
            }
        }
        else if (typeFrom.equals("queen"))
        {
            if (colFrom != colTo && rowFrom != rowTo && 
            Math.abs(colFrom - colTo) != Math.abs(rowFrom - rowTo))
            {
                return false;
            }
            else if (colFrom == colTo)
            {
                if (!this.isVerticalClear(rowFrom, colFrom, rowTo, colTo))
                {
                    return false;
                }
            }
            else if (rowFrom == rowTo)
            {
                if (!this.isHorizontalClear(rowFrom, colFrom, rowTo, colTo))
                {
                    return false;
                }
            }
            else
            {
                if (!this.isDiagonalClear(rowFrom, colFrom, rowTo, colTo))
                {
                    return false;
                }
            }
        }
        else if (typeFrom.equals("rook"))
        {
            if (colFrom != colTo && rowFrom != rowTo)
            {
                return false;
            }
            else if (colFrom == colTo)
            {
                if (!this.isVerticalClear(rowFrom, colFrom, rowTo, colTo))
                {
                    return false;
                }
            }
            else if (rowFrom == rowTo)
            {
                if (!this.isHorizontalClear(rowFrom, colFrom, rowTo, colTo))
                {
                    return false;
                }
            }
        }
        else if (typeFrom.equals("bishop"))
        {
            if (Math.abs(colFrom - colTo) != Math.abs(rowFrom - rowTo))
            {
                return false;
            }
            else
            {
                if (!this.isDiagonalClear(rowFrom, colFrom, rowTo, colTo))
                {
                    return false;
                }
            }
        }
        else if (typeFrom.equals("knight"))
        {
            if (!(Math.abs(rowFrom - rowTo) == 1 && Math.abs(colFrom - colTo) == 2) &&
            !(Math.abs(rowFrom - rowTo) == 2 && Math.abs(colFrom - colTo) == 1))
            {
                return false;
            }
        }
        else if (typeFrom.equals("pawn"))
        {
            if (colorFrom.equals("white"))
            {
                if (colFrom == colTo)
                {
                    if (!typeTo.equals("blank"))
                    {
                        return false;
                    }
                    if (board[rowFrom][colFrom].getHasMoved() && rowFrom - rowTo != 1)
                    {
                        return false;
                    }
                    else if (!board[rowFrom][colFrom].getHasMoved() && rowFrom - rowTo > 2)
                    {
                        return false;
                    }
                    else if (rowFrom - rowTo == 2 &&
                    !board[rowFrom - 1][colFrom].getType().equals("blank"))
                    {
                        return false;
                    }
                }
                else if (Math.abs(colFrom - colTo) == 1 && rowFrom - rowTo == 1)
                {
                    if (colorTo.equals(colorFrom))
                    {
                        return false;
                    }
                    else if (typeTo.equals("blank"))
                    {
                        if (!(colOfAvailableEnPassant == colTo && rowFrom == 3))
                        {
                            return false;
                        }
                    }
                }
                else
                {
                    return false;
                }
            }
            else
            {
                if (colFrom == colTo)
                {
                    if (!typeTo.equals("blank"))
                    {
                        return false;
                    }
                    if (board[rowFrom][colFrom].getHasMoved() && rowTo - rowFrom != 1)
                    {
                        return false;
                    }
                    else if (!board[rowFrom][colFrom].getHasMoved() && rowTo - rowFrom > 2)
                    {
                        return false;
                    }
                    else if (rowTo - rowFrom == 2 &&
                    !board[rowFrom + 1][colFrom].getType().equals("blank"))
                    {
                        return false;
                    }
                }
                else if (Math.abs(colFrom - colTo) == 1 && rowTo - rowFrom == 1)
                {
                    if (colorTo.equals(colorFrom))
                    {
                        return false;
                    }
                    else if (typeTo.equals("blank"))
                    {
                        if (!(colOfAvailableEnPassant == colTo && rowFrom == 4))
                        {
                            return false;
                        }
                    }
                }
                else
                {
                    return false;
                }
            }
        }

        return true;
    }

    public void addBoardConfiguration()
    {
        String configuration = "";
        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                configuration += board[row][col].getColor() + " " + board[row][col].getType() + ", ";
            }
        }
        if (board[0][4].getColor().equals("black") && board[0][4].getType().equals("king") &&
        !board[0][4].getHasMoved())
        {
            if (board[0][0].getColor().equals("black") && board[0][0].getType().equals("rook") &&
            !board[0][0].getHasMoved())
            {
                configuration += "black can castle queenside, ";
            }
            if (board[0][7].getColor().equals("black") && board[0][7].getType().equals("rook") &&
            !board[0][7].getHasMoved())
            {
                configuration += "black can castle kingside, ";
            }
        }
        if (board[7][4].getColor().equals("white") && board[7][4].getType().equals("king") &&
        !board[7][4].getHasMoved())
        {
            if (board[7][0].getColor().equals("white") && board[7][0].getType().equals("white") &&
            !board[7][0].getHasMoved())
            {
                configuration += "white can castle queenside, ";
            }
            if (board[7][7].getColor().equals("white") && board[7][7].getType().equals("rook") &&
            !board[7][7].getHasMoved())
            {
                configuration += "white can castle kingside, ";
            }
        }
        configuration += "en passant: " + colOfAvailableEnPassant;
        listOfBoards.add(configuration);
    }

    public String getPlayerInCheck()
    {
        int whiteKingRow = 0;
        int whiteKingCol = 0;
        int blackKingRow = 0;
        int blackKingCol = 0;
        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                if (board[row][col].getType().equals("king"))
                {
                    if (board[row][col].getColor().equals("white"))
                    {
                        whiteKingRow = row;
                        whiteKingCol = col;
                    }
                    else
                    {
                        blackKingRow = row;
                        blackKingCol = col;
                    }
                }
            }
        }

        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                if (!board[row][col].getType().equals("king"))
                {
                    if (this.moveIsValid(row, col, whiteKingRow, whiteKingCol))
                    {
                        return "white";
                    }
                    else if (this.moveIsValid(row, col, blackKingRow, blackKingCol))
                    {
                        return "black";
                    }
                }
            }
        }
        return "none";
    }

    public boolean doesMovePutPlayerInCheck(int rowFrom, int colFrom, int rowTo, int colTo)
    {
        this.move(rowFrom, colFrom, rowTo, colTo);
        String playerInCheck = this.getPlayerInCheck();
        this.undo();
        return playerInCheck.equals(playersTurn);
    }

    public boolean isHorizontalClear(int rowFrom, int colFrom, int rowTo, int colTo)
    {
        int lower = 0;
        int upper = 0;
        if (colFrom < colTo)
        {
            lower = colFrom;
            upper = colTo;
        }
        else
        {
            lower = colTo;
            upper = colFrom;
        }
        for (int col = lower + 1; col < upper; col++)
        {
            if (!board[rowFrom][col].getType().equals("blank"))
            {
                return false;
            }
        }
        return true;
    }

    public boolean isVerticalClear(int rowFrom, int colFrom, int rowTo, int colTo)
    {
        int lower = 0;
        int upper = 0;
        if (rowFrom < rowTo)
        {
            lower = rowFrom;
            upper = rowTo;
        }
        else
        {
            lower = rowTo;
            upper = rowFrom;
        }
        for (int row = lower + 1; row < upper; row++)
        {
            if (!board[row][colFrom].getType().equals("blank"))
            {
                return false;
            }
        }
        return true;
    }

    public boolean isDiagonalClear(int rowFrom, int colFrom, int rowTo, int colTo)
    {
        for (int num = 1; num < Math.abs(rowFrom - rowTo); num++)
        {
            if (rowFrom < rowTo && colFrom < colTo)
            {
                if (!board[rowFrom + num][colFrom + num].getType().equals("blank"))
                {
                    return false;
                }
            }
            else if (rowFrom < rowTo && colFrom > colTo)
            {
                if (!board[rowFrom + num][colFrom - num].getType().equals("blank"))
                {
                    return false;
                }
            }
            else if (rowFrom > rowTo && colFrom < colTo)
            {
                if (!board[rowFrom - num][colFrom + num].getType().equals("blank"))
                {
                    return false;
                }
            }
            else
            {
                if (!board[rowFrom - num][colFrom - num].getType().equals("blank"))
                {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isCheckmate()
    {
        if (!this.getPlayerInCheck().equals("none"))
        {
            boolean checkmate = true;
            for (int row = 0; row < 8; row++)
            {
                for (int col = 0; col < 8; col++)
                {
                    if (board[row][col].getColor().equals(this.getPlayerInCheck()))
                    {
                        for (String coordinates : this.getPossibleMoves(row, col))
                        {
                            //if move is valid, checkmate = false
                            int colFrom = getValueFromCoordinates(coordinates, 0);
                            int rowFrom = getValueFromCoordinates(coordinates, 1);
                            int colTo = getValueFromCoordinates(coordinates, 2);
                            int rowTo = getValueFromCoordinates(coordinates, 3);
                            if (this.moveIsValid(rowFrom, colFrom, rowTo, colTo) &&
                            !this.doesMovePutPlayerInCheck(rowFrom, colFrom, rowTo, colTo))
                            {
                                checkmate = false;
                                break;
                            }
                        }
                    }
                    if (!checkmate)
                    {
                        break;
                    }
                }
                if (!checkmate)
                {
                    break;
                }
            }
            return checkmate;
        }
        return false;
    }

    public boolean isStalemate()
    {
        if (this.getPlayerInCheck().equals("none"))
        {
            boolean hasPossibleMove = false;
            for (int row = 0; row < 8; row++)
            {
                for (int col = 0; col < 8; col++)
                {
                    if (board[row][col].getColor().equals(playersTurn))
                    {
                        for (String coordinates : this.getPossibleMoves(row, col))
                        {
                            //if move is valid, whiteHasPossibleMove = true
                            int colFrom = getValueFromCoordinates(coordinates, 0);
                            int rowFrom = getValueFromCoordinates(coordinates, 1);
                            int colTo = getValueFromCoordinates(coordinates, 2);
                            int rowTo = getValueFromCoordinates(coordinates, 3);
                            if (this.moveIsValid(rowFrom, colFrom, rowTo, colTo) &&
                            !this.doesMovePutPlayerInCheck(rowFrom, colFrom, rowTo, colTo))
                            {
                                hasPossibleMove = true;
                                break;
                            }
                        }
                    }
                    if (hasPossibleMove)
                    {
                        break;
                    }
                }
                if (hasPossibleMove)
                {
                    break;
                }
            }
            if (!hasPossibleMove)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public boolean isGameOver()
    {
        if (this.isCheckmate() || this.isStalemate() || this.isThreefoldRepetition())
        {
            return true;
        }
        return false;
    }

    public ArrayList<String> getPossibleMoves(int rowFrom, int colFrom)
    {
        String typeFrom = board[rowFrom][colFrom].getType();
        String colorFrom = board[rowFrom][colFrom].getColor();

        String[] letters = new String[] {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] numbers = new String[] {"8", "7", "6", "5", "4", "3", "2", "1"};

        ArrayList<String> possibilities = new ArrayList<String>();
        String coordinates = letters[colFrom] + numbers[rowFrom];

        if (typeFrom.equals("king"))
        {
            for (int row = rowFrom - 1; row <= rowFrom + 1; row++)
            {
                for (int col = colFrom - 1; col <= colFrom + 1; col++)
                {
                    if (row >= 0 && row < 8 && col >= 0 && col < 8 && 
                    !(row == rowFrom && col == colFrom))
                    {
                        possibilities.add(coordinates + letters[col] + numbers[row]);
                    }
                }
            }
        }
        else if (typeFrom.equals("queen"))
        {
            for (int row = 0; row < 8; row++)
            {
                if (row != rowFrom)
                {
                    possibilities.add(coordinates + letters[colFrom] + numbers[row]);
                }
            }
            for (int col = 0; col < 8; col++)
            {
                if (col != colFrom)
                {
                    possibilities.add(coordinates + letters[col] + numbers[rowFrom]);
                }
            }
            for (int num = 1; num < 8; num++)
            {
                if (rowFrom + num < 8 && colFrom + num < 8)
                {
                    possibilities.add(coordinates + letters[colFrom + num] + 
                        numbers[rowFrom + num]);
                }
                if (rowFrom - num >= 0 && colFrom + num < 8)
                {
                    possibilities.add(coordinates + letters[colFrom + num] + 
                        numbers[rowFrom - num]);
                }
                if (rowFrom + num < 8 && colFrom - num >= 0)
                {
                    possibilities.add(coordinates + letters[colFrom - num] + 
                        numbers[rowFrom + num]);
                }
                if (rowFrom - num >= 0 && colFrom - num >= 0)
                {
                    possibilities.add(coordinates + letters[colFrom - num] + 
                        numbers[rowFrom - num]);
                }
            }
        }
        else if (typeFrom.equals("rook"))
        {
            for (int row = 0; row < 8; row++)
            {
                if (row != rowFrom)
                {
                    possibilities.add(coordinates + letters[colFrom] + numbers[row]);
                }
            }
            for (int col = 0; col < 8; col++)
            {
                if (col != colFrom)
                {
                    possibilities.add(coordinates + letters[col] + numbers[rowFrom]);
                }
            }
        }
        else if (typeFrom.equals("bishop"))
        {
            for (int num = 1; num < 8; num++)
            {
                if (rowFrom + num < 8 && colFrom + num < 8)
                {
                    possibilities.add(coordinates + letters[colFrom + num] + 
                        numbers[rowFrom + num]);
                }
                if (rowFrom - num >= 0 && colFrom + num < 8)
                {
                    possibilities.add(coordinates + letters[colFrom + num] + 
                        numbers[rowFrom - num]);
                }
                if (rowFrom + num < 8 && colFrom - num >= 0)
                {
                    possibilities.add(coordinates + letters[colFrom - num] + 
                        numbers[rowFrom + num]);
                }
                if (rowFrom - num >= 0 && colFrom - num >= 0)
                {
                    possibilities.add(coordinates + letters[colFrom - num] + 
                        numbers[rowFrom - num]);
                }
            }
        }
        else if (typeFrom.equals("knight"))
        {
            for (int i = -1; i <= 1; i += 2)
            {
                for (int j = -2; j <= 2; j += 4)
                {
                    if (rowFrom + i >= 0 && rowFrom + i < 8 && 
                    colFrom + j >= 0 && colFrom + j < 8)
                    {
                        possibilities.add(coordinates + letters[colFrom + j] + 
                            numbers[rowFrom + i]);
                    }
                    if (rowFrom + j >= 0 && rowFrom + j < 8 && 
                    colFrom + i >= 0 && colFrom + i < 8)
                    {
                        possibilities.add(coordinates + letters[colFrom + i] + 
                            numbers[rowFrom + j]);
                    }
                }
            }
        }
        else if (typeFrom.equals("pawn"))
        {
            if (colorFrom.equals("white"))
            {
                if (rowFrom - 1 >= 0)
                {
                    possibilities.add(coordinates + letters[colFrom] + numbers[rowFrom - 1]);
                    if (colFrom - 1 >= 0)
                    {
                        possibilities.add(coordinates + 
                            letters[colFrom - 1] + numbers[rowFrom - 1]);
                    }
                    if (colFrom + 1 < 8)
                    {
                        possibilities.add(coordinates + 
                            letters[colFrom + 1] + numbers[rowFrom - 1]);
                    }
                }
                if (rowFrom - 2 >= 0)
                {
                    possibilities.add(coordinates + letters[colFrom] + numbers[rowFrom - 2]);
                }
            }
            else
            {
                if (rowFrom + 1 < 8)
                {
                    possibilities.add(coordinates + letters[colFrom] + numbers[rowFrom + 1]);
                    if (colFrom - 1 >= 0)
                    {
                        possibilities.add(coordinates + 
                            letters[colFrom - 1] + numbers[rowFrom + 1]);
                    }
                    if (colFrom + 1 < 8)
                    {
                        possibilities.add(coordinates + 
                            letters[colFrom + 1] + numbers[rowFrom + 1]);
                    }
                }
                if (rowFrom + 2 < 8)
                {
                    possibilities.add(coordinates + letters[colFrom] + numbers[rowFrom + 2]);
                }
            }
        }

        return possibilities;
    }

    public String getWinner()
    {
        if (this.isCheckmate() && this.getPlayerInCheck().equals("black"))
        {
            return "white";
        }
        else if (this.isCheckmate() && this.getPlayerInCheck().equals("white"))
        {
            return "black";
        }
        else if (this.isStalemate() || this.isThreefoldRepetition())
        {
            return "draw";
        }
        else
        {
            return "none";
        }
    }

    public boolean isProperNotation(String coordinates)
    {
        if (coordinates.length() != 4)
        {
            return false;
        }

        int colFrom = getValueFromCoordinates(coordinates, 0);
        int rowFrom = getValueFromCoordinates(coordinates, 1);
        int colTo = getValueFromCoordinates(coordinates, 2);
        int rowTo = getValueFromCoordinates(coordinates, 3);

        if (colFrom == -1 || rowFrom == -1 || colTo == -1 || rowTo == -1)
        {
            return false;
        }

        return true;
    }

    public int getValueFromCoordinates(String coordinates, int index)
    {
        String coordinate = coordinates.substring(index, index + 1);
        if (index == 0 || index == 2)
        {
            String[] letters = new String[] {"a", "b", "c", "d", "e", "f", "g", "h"};
            for (int i = 0; i < 8; i++)
            {
                if (coordinate.equalsIgnoreCase(letters[i]))
                {
                    return i;
                }
            }
        }
        else
        {
            String[] numbers = new String[] {"8", "7", "6", "5", "4", "3", "2", "1"};
            for (int i = 0; i < 8; i++)
            {
                if (coordinate.equals(numbers[i]))
                {
                    return i;
                }
            }
        }
        return -1;
    }

    public String toString()
    {
        String boardString = "    a   b   c   d   e   f   g   h\n  ,———,———,———,———,———,———,———,———,\n";
        for (int i = 0; i < 8; i++)
        {
            boardString += (8 - i) + " | " + 
            board[i][0].getIcon() + " | " +
            board[i][1].getIcon() + " | " + 
            board[i][2].getIcon() + " | " + 
            board[i][3].getIcon() + " | " + 
            board[i][4].getIcon() + " | " + 
            board[i][5].getIcon() + " | " + 
            board[i][6].getIcon() + " | " + 
            board[i][7].getIcon() + " | " + (8 - i) + "\n";
            if (i < 7)
            {
                boardString += "  |———|———|———|———|———|———|———|———|  \n";
            }
        }
        boardString += "  '———'———'———'———'———'———'———'———'\n    a   b   c   d   e   f   g   h";
        return boardString;
    }

    public void undo()
    {
        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                board[row][col] = previousBoard[row][col];
            }
        }
    }

    public boolean isFiftyMoveRule()
    {
        if (fiftyMoveRule >= 100)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isThreefoldRepetition()
    {
        int occurrences = 0;
        for (String configuration : listOfBoards)
        {
            if (configuration.equals(listOfBoards.get(listOfBoards.size() - 1)))
            {
                occurrences++;
            }
        }
        if (occurrences >= 3)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void reset()
    {
        colOfAvailableEnPassant = -1;
        playersTurn = "white";
        fiftyMoveRule = 0;

        int listOfBoardsSize = listOfBoards.size();
        for (int i = listOfBoardsSize - 1; i >= 0; i--)
        {
            listOfBoards.remove(i);
        }

        //set pieces
        board[0][0] = new Piece("black", "rook");
        board[0][1] = new Piece("black", "knight");
        board[0][2] = new Piece("black", "bishop");
        board[0][3] = new Piece("black", "queen");
        board[0][4] = new Piece("black", "king");
        board[0][5] = new Piece("black", "bishop");
        board[0][6] = new Piece("black", "knight");
        board[0][7] = new Piece("black", "rook");
        for (int col = 0; col < 8; col++)
        {
            board[1][col] = new Piece("black", "pawn");
            for (int row = 2; row < 6; row++)
            {
                board[row][col] = new Piece("", "blank");
            }
            board[6][col] = new Piece("white", "pawn");
        }
        board[7][0] = new Piece("white", "rook");
        board[7][1] = new Piece("white", "knight");
        board[7][2] = new Piece("white", "bishop");
        board[7][3] = new Piece("white", "queen");
        board[7][4] = new Piece("white", "king");
        board[7][5] = new Piece("white", "bishop");
        board[7][6] = new Piece("white", "knight");
        board[7][7] = new Piece("white", "rook");

        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                previousBoard[row][col] = board[row][col];
            }
        }

        this.addBoardConfiguration();
    }
}
