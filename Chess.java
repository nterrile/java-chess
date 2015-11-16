/**
 * Chess.java
 * 
 * @author terrilen 
 * @version 2015-1-30
 */
import java.util.Scanner;
public class Chess
{
    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        Board board = new Board();
        System.out.println("CHESS\n------------------------------");

        boolean playComputer = false;
        boolean isWhite = true;
        boolean hasQuitGame = false;
        boolean playerOfferedDraw = false;
        boolean playerDeclinedDraw = false;
        boolean drawByAgreement = false;
        boolean drawByFiftyMoveRule = false;
        String playerResigned = "none";

        while (true)
        {
            hasQuitGame = false;
            drawByAgreement = false;
            drawByFiftyMoveRule = false;
            playerResigned = "none";
            board.reset();
            System.out.print("Choose your gamemode:\n1. Single Player\n2. Multiplayer\n-> ");
            String input = in.nextLine();

            if (input.equals("1"))
            {
                System.out.print("Choose your color (white/black):\n-> ");
                input = in.nextLine();
                if (input.equals("white"))
                {
                    isWhite = true;
                }
                else
                {
                    isWhite = false;
                }
                playComputer = true;
            }
            else if (input.equals("2"))
            {
                playComputer = false;
            }
            else
            {
                System.out.println("Goodbye.");
                break;
            }

            System.out.print("Would you like instructions on how to play Chess?\n-> ");
            input = in.nextLine();

            if (input.equals("") || input.substring(0,1).equalsIgnoreCase("y"))
            {
                System.out.println("------------------------------");
                System.out.println("Instructions:");
                System.out.println("1. Enter moves by typing the position of a piece\n" + 
                    "   followed by the position of its destination.\n" +
                    "   For example, to move a knight one might enter\n   'g1f3'.");
                System.out.println("2. Offer a draw at any time by typing 'draw'.");
                System.out.println("3. Claim a draw via the fifty-move rule by typing\n" +
                    "   'draw' after 50 consecutive moves have been\n" +
                    "   played without a capture or pawn move by either\n   player.");
                System.out.println("4. Resign at any time by typing 'resign'.\n" +
                    "   This results in a win for the other player.");
                System.out.println("5. Quit the game at any time by typing 'Q'.");
            }

            int move = 0;
            while (true)
            {
                if (!playerOfferedDraw && !playerDeclinedDraw)
                {
                    move++;
                }
                
                System.out.println("------------------------------\n" + board.toString() + "\n");

                if (board.isGameOver() || hasQuitGame || drawByAgreement || 
                drawByFiftyMoveRule || !playerResigned.equals("none"))
                {
                    break;
                }

                if (!playComputer)
                {
                    if (playerOfferedDraw)
                    {
                        System.out.print("Black has offered a draw. Does white accept?\n-> ");
                        input = in.nextLine();
                        if (!input.substring(0,1).equalsIgnoreCase("y"))
                        {
                            playerDeclinedDraw = true;
                            playerOfferedDraw = false;
                        }
                        else
                        {
                            drawByAgreement = true;
                        }
                    }
                    else
                    {
                        if (playerDeclinedDraw)
                        {
                            System.out.println("Black has declined white's offer to draw.");
                            playerDeclinedDraw = false;
                        }

                        System.out.println(move + ". White's turn:");

                        while (true)
                        {
                            System.out.print("-> ");
                            input = in.nextLine();
                            if (input.equalsIgnoreCase("q"))
                            {
                                hasQuitGame = true;
                                break;
                            }
                            else if (input.equalsIgnoreCase("draw"))
                            {
                                if (board.isFiftyMoveRule())
                                {
                                    drawByFiftyMoveRule = true;
                                    break;
                                }
                                else
                                {
                                    playerOfferedDraw = true;
                                    break;
                                }
                            }
                            else if (input.equalsIgnoreCase("resign"))
                            {
                                playerResigned = "white";
                                break;
                            }
                            else if (board.moveIsValid(input))
                            {
                                break;
                            }
                            else if (!board.isProperNotation(input))
                            {
                                System.out.println("            Improper Notation");
                            }
                            else
                            {
                                System.out.println("            Illegal move");
                            }
                        }
                        board.move(input);
                    }
                }
                else if (playComputer && isWhite)
                {
                    System.out.print("Your turn:\n-> ");
                    input = in.nextLine();
                }
                else
                {
                    //computer moves
                }

                System.out.println("------------------------------\n" + board.toString() + "\n");

                if (board.isGameOver() || hasQuitGame || drawByAgreement || 
                drawByFiftyMoveRule || !playerResigned.equals("none"))
                {
                    break;
                }

                if (!playComputer)
                {
                    if (playerOfferedDraw)
                    {
                        System.out.print("White has offered a draw. Does black accept?\n-> ");
                        input = in.nextLine();
                        if (!input.substring(0,1).equalsIgnoreCase("y"))
                        {
                            playerDeclinedDraw = true;
                            playerOfferedDraw = false;
                        }
                        else
                        {
                            drawByAgreement = true;
                        }
                    }
                    else
                    {
                        if (playerDeclinedDraw)
                        {
                            System.out.println("White has declined black's offer to draw.");
                            playerDeclinedDraw = false;
                        }

                        System.out.println(move + ". Black's turn:");

                        while (true)
                        {
                            System.out.print("-> ");
                            input = in.nextLine();
                            if (input.equalsIgnoreCase("q"))
                            {
                                hasQuitGame = true;
                                break;
                            }
                            else if (input.equalsIgnoreCase("draw"))
                            {
                                if (board.isFiftyMoveRule())
                                {
                                    drawByFiftyMoveRule = true;
                                    break;
                                }
                                else
                                {
                                    playerOfferedDraw = true;
                                    break;
                                }
                            }
                            else if (input.equalsIgnoreCase("resign"))
                            {
                                playerResigned = "black";
                                break;
                            }
                            else if (board.moveIsValid(input))
                            {
                                break;
                            }
                            else if (!board.isProperNotation(input))
                            {
                                System.out.println("            Improper Notation");
                            }
                            else
                            {
                                System.out.println("            Illegal move");
                            }
                        }
                        board.move(input);
                    }
                }
                else if (playComputer && isWhite)
                {
                    //computer moves
                }
                else
                {
                    System.out.print("Your turn:\n-> ");
                    input = in.nextLine();
                }
            }

            System.out.print("GAME OVER: "); //agreement, three-fold repetition, 50move rule
            if (hasQuitGame)
            {
                System.out.println("match aborted.");
            }
            else if (drawByAgreement)
            {
                System.out.println("draw by agreement.");
            }
            else if (drawByFiftyMoveRule)
            {
                System.out.println("draw by fifty-move rule.");
            }
            else if (!playerResigned.equals("none"))
            {
                if (playerResigned.equals("white"))
                {
                    System.out.println("white resigned.");
                }
                else
                {
                    System.out.println("black resigned.");
                }
            }
            else if (board.isThreefoldRepetition())
            {
                System.out.println("draw by threefold repetition.");
            }
            else if (board.isStalemate())
            {
                System.out.println("draw by stalemate.");
            }
            else
            {
                if (board.getWinner().equals("white"))
                {
                    System.out.println("white wins by checkmate!");
                }
                else
                {
                    System.out.println("black wins by checkmate!");
                }
            }

            System.out.print("\nWould you like to play again?\n-> ");
            input = in.nextLine();
            System.out.println("------------------------------");
            if (input.length() == 0)
            {
                //this is to avoid bounds errors in the following else if statement
            }
            else if (input.substring(0,1).equalsIgnoreCase("n"))
            {
                //reports total wins and losses
                System.out.println("Goodbye.");
                break;
            }
        }
    }
}
