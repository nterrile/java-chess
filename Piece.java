/**
 * Piece.java
 * 
 * @author terrilen
 * @version 2015-1-29
 */
public class Piece
{
    private String color;
    private String type;
    private String icon;
    private boolean hasMoved = false;
    private String whiteKingIcon = "\u2654";
    private String whiteQueenIcon = "\u2655";
    private String whiteRookIcon = "\u2656";
    private String whiteBishopIcon = "\u2657";
    private String whiteKnightIcon = "\u2658";
    private String whitePawnIcon = "\u2659";
    private String blackKingIcon = "\u265A";
    private String blackQueenIcon = "\u265B";
    private String blackRookIcon = "\u265C";
    private String blackBishopIcon = "\u265D";
    private String blackKnightIcon = "\u265E";
    private String blackPawnIcon = "\u265F";

    public Piece(String color, String type)
    {
        this.color = color;
        this.type = type;
        if (type.equals("king"))
        {
            if (color.equals("white"))
                icon =  whiteKingIcon;
            else if (color.equals("black"))
                icon =  blackKingIcon;
        }
        else if (type.equals("queen"))
        {
            if (color.equals("white"))
                icon =  whiteQueenIcon;
            else if (color.equals("black"))
                icon =  blackQueenIcon;
        }
        else if (type.equals("rook"))
        {
            if (color.equals("white"))
                icon =  whiteRookIcon;
            else if (color.equals("black"))
                icon = blackRookIcon;
        }
        else if (type.equals("bishop"))
        {
            if (color.equals("white"))
                icon = whiteBishopIcon;
            else if (color.equals("black"))
                icon = blackBishopIcon;
        }
        else if (type.equals("knight"))
        {
            if (color.equals("white"))
                icon = whiteKnightIcon;
            else if (color.equals("black"))
                icon = blackKnightIcon;
        }
        else if (type.equals("pawn"))
        {
            if (color.equals("white"))
                icon = whitePawnIcon;
            else if (color.equals("black"))
                icon = blackPawnIcon;
        }
        else
        {
            icon = " ";
        }
    }

    public String getColor()
    {
        return color;
    }

    public String getType()
    {
        return type;
    }

    public String getIcon()
    {
        return icon;
    }
    
    public void setHasMoved()
    {
        hasMoved = true;
    }
    
    public boolean getHasMoved()
    {
        return hasMoved;
    }
}
