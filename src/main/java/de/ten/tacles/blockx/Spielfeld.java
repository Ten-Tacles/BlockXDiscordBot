package de.ten.tacles.blockx;

import com.sun.istack.internal.Nullable;
import javafx.scene.paint.Color;


public class Spielfeld
{
    
    private Tile tiles[][];
    private int width;
    private int height;
    private int playerCount;
    private Color farben[];
    private String farbenNamen[];


    private final static String[] defaultFarbenNamen = {
            "Red",
            "Blue",
            "Green",
            "Yellow",
            "Cyan",
            "Magenta",
            "Orange",
            "Bright Green",
            "Purple",
            "Pink",
            "Bright Blue",
            "Yellow Green"
    };

    private final static Color defaultFarben[] = {
            Color.hsb(0, 0.9, 0.9),
            Color.hsb(240, 0.9, 0.9),
            Color.hsb(120, 0.9, 0.9),
            Color.hsb(60, 0.9, 0.9),
            Color.hsb(180, 0.9, 0.9),
            Color.hsb(300, 0.9, 0.9),
            Color.hsb(30, 0.9, 0.9),
            Color.hsb(150, 0.9, 0.9),
            Color.hsb(270, 0.9, 0.9),
            Color.hsb(329, 0.9, 0.9),
            Color.hsb(209, 0.9, 0.9),
            Color.hsb(89, 0.9, 0.9)
    };
    
    
    
    public Spielfeld(int Breite, int Hoehe, int playerCount, @Nullable Color farben[], @Nullable String farbenNamen[])
    {
        this.tiles = new Tile[Breite][Hoehe];
        this.width = Breite;
        this.height = Hoehe;
        this.playerCount = playerCount;
        if (farben != null)
            this.farben = farben;
        else
            System.arraycopy(defaultFarben, 0,this.farben = new Color[playerCount],0, playerCount);
        if (farbenNamen != null)
            this.farbenNamen = farbenNamen;
        else
            System.arraycopy(defaultFarbenNamen, 0,this.farbenNamen = new String[playerCount],0, playerCount);
        for(int i = 0; i < Breite; i++)
        {
            for(int j = 0; j < Hoehe; j++)
            tiles[i][j] = new Tile();
        }
    }

    public Spielfeld(int breite, int hoehe, int playerCount)
    {
        this(breite, hoehe, playerCount, defaultFarben, defaultFarbenNamen);
    }


    public Spielfeld(Spielfeld feld)
    {
    	width = feld.getWidth();
    	height = feld.getHeight();
    	tiles = new Tile[width][height];
    	playerCount = feld.getPlayerCount();
    	farben = feld.getColours();
    	farbenNamen = feld.getColourNames();
    	for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            tiles[i][j] = new Tile(feld.getTileType(i, j), feld.getTileVariation(i, j), feld.getTileBorders(i, j));
            
        }
    }

    
    
    public Color[] getColours()
    {
		return farben;
	}
    public String[] getColourNames()
    {
        return farbenNamen;
    }
    
    public Color getFarbe(int spieler)
    {
    	if (spieler > 0 && spieler <= playerCount)
    	{
    		return farben[spieler-1];
    	}
    	else
    		return Color.WHITE;
    }
    
    public void setFarbe(int spieler, Color color)
    {
    	farben[spieler-1] = color;
    }

    //Why do I have this function???
    public void swapTile(int x1, int y1, int x2, int y2)
    {
    	int type = getTileType(x1, y1);
    	int variant = getTileVariation(x1, y1);
    	int mauer = getTileBorders(x1, y1);
    	
    	setTileType(x1, y1, getTileType(x2, y2), getTileVariation(x2, y2), getTileBorders(x2, y2) );
    	setTileType(x2, y2, type, variant, mauer );

    }

    public void setTileType(int Breite, int Hoehe, int NeuerTyp, int NeueVariation, int NeueMauer)
    {
        if (!(Breite < 0 || Breite >= this.width || Hoehe < 0 || Hoehe >= this.height))
        {
            tiles[Breite][Hoehe].setType(NeuerTyp);
            tiles[Breite][Hoehe].setVariation(NeueVariation);
            tiles[Breite][Hoehe].setBorder(NeueMauer);
        }
    }
    public void setTileType(int Breite, int Hoehe, int NeuerTyp, int NeueVariation)
    {
        if (!(Breite < 0 || Breite >= this.width || Hoehe < 0 || Hoehe >= this.height))
        {
            tiles[Breite][Hoehe].setType(NeuerTyp);
            tiles[Breite][Hoehe].setVariation(NeueVariation);
        }
    }
    public void setTileType(int Breite, int Hoehe, int NeuerTyp)
    {
        if (!(Breite < 0 || Breite >= this.width || Hoehe < 0 || Hoehe >= this.height))
        {
            tiles[Breite][Hoehe].setType(NeuerTyp);
        }
    }
    
    public void setTileVariant(int Breite, int Hoehe, int NeueVariation)
    {
        if (!(Breite < 0 || Breite >= this.width || Hoehe < 0 || Hoehe >= this.height))
        {
            tiles[Breite][Hoehe].setVariation(NeueVariation);
        }
    }
    public void setTileBorder(int Breite, int Hoehe, int NeueMauer)
    {
        if (!(Breite < 0 || Breite >= this.width || Hoehe < 0 || Hoehe >= this.height))
        {
            tiles[Breite][Hoehe].setBorder(NeueMauer);
        }
    }
    
    /**
     * Gibt den Inhalt des angefragten Feldes zur�ck.
     * Sollte das tiles nicht existieren, wird Typ 6 (MauerFelder) und Variante 1 zur�ckgegeben.
     */
    public int getTileType(int Breite, int Hoehe)
    {
        if (Breite < 0 || Breite >= this.width || Hoehe < 0 || Hoehe >= this.height)
        {
            return 6;
        }
        else return tiles[Breite][Hoehe].getType();
    }
    public int getTileVariation(int Breite, int Hoehe)
    {
       if (Breite <= -1 || Breite >= this.width || Hoehe <= -1 || Hoehe >= this.height)
        {
            return 1;
        }
        else return tiles[Breite][Hoehe].getVariation();
    }
    public int getTileBorders(int Breite, int Hoehe)
    {
        if (Breite < 0 || Breite >= this.width || Hoehe < 0 || Hoehe >= this.height)
        {
            return 0;
        }
        else return tiles[Breite][Hoehe].getBorders();
    }
    
    /**
     * Gibt die Breite des Spielfeldes zur�ck.
     */
    public int getWidth()
    {
        return width;
    }
    /**
     * Gibt die Hoehe des Spielfeldes zur�ck.
     */
    public int getHeight()
    {
        return height;
    }
    
    public int getPlayerCount()
    {
    	return playerCount;
    }
    
    /**
     * Gibt das Spielfeld als ein String zur�ck welcher gespeichert und vom Kartenleser zur�ck in ein SpielFeld Objekt wandeln kann.
     */
    @Override
    public String toString()
    {
        StringBuilder toReturn = new StringBuilder();
        toReturn.append("Stats").append(width).append("/").append(height).append("/").append(playerCount);
        toReturn.append("\n");
        for (int i = 0; i < farben.length-1; i++)
        {
            toReturn.append(farben[i].getHue()).append("/");
        }
        toReturn.append(farben[farben.length-1].getHue()).append("\n");

        for(int i = 0; i < height; i++)
        {
            toReturn.append("Row:").append(i).append(":");
            for(int j = 0; j < width; j++)
            {
                toReturn.append(tiles[j][i].getType()).append(",").append(tiles[j][i].getVariation());
                if (tiles[j][i].getBorders() > 0)
                    toReturn.append(",").append(tiles[j][i].getBorders()).append(";");
                else
                    toReturn.append(";");
            }
            toReturn.append("\n");
        }
        return toReturn.toString();
    }
}
