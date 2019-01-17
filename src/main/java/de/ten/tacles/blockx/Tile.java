package de.ten.tacles.blockx;

/**
 * It does stuff.
 * Probably magic 'n' shit.
 */
public class Tile
{
    private int type; //The type of this tile, it controls the general behaviour
    //0 = Empty tiles, halved player tiles : Can be conquered
    //1 = Permanant, Heavy, Medium, light blocked tiles
    //2 = Players
    //3 = Armored Players
    //4 = Teleporter
    //5 = Population, Destruction, Void Bombs ; Changes the tiles around it, to player, blocked or empty tiles, respectively
    //6 = Walls: Block movement, variants are decorative
    //7 = Cores; 0 = normal, 1 = charged, 2 = broken; Normal and Charged grant moves, broken can be repaired
    private int variation;//Felder eines Typs können unterschiedliche Variationen haben, zb SpielerFelder geh�ren einem Spieler, ist hier gespeichert.
    //Eine Liste welche Typen welche Variationen haben, steht oben.
    private int border; //Mauern verhindern den Angriff von bestimmten Seiten, aber verhindern es nicht in diese Richtung anzugreifen.
    //Ist noch nicht implementiert, wird hier aber mal schon geplant.
    // 0000 (0) von jeder Seite kann angegriffen werden
    // 0001 (1) von oben kann nicht angegriffen werden
    // 0010 (2) von rechts
    // 0100 (4) von unten
    // 1000 (8) von links
    // 1001 (9) von oben und links


    /**
     * Returns whether the tile type and variation is a valid tile or not.
     * Incorrect tiles will not cause problems during gameplay to occur.
     * Incorrect types will be treated like type 1, incorrect variants depend on their type.
     *
     * @param type The type
     * @param variation The variation
     * @param playerCount A few types depend on the playercount for proper evalution
     * @return Whether it is a valid tile or not
     */
    public static boolean isValidTile(int type, int variation, int playerCount)
    {
        switch (type)
        {
            //Scale based with playercount
            case 0:
            case 2:
            case 3:
                return variation >= 0 && variation <= playerCount;
            //Have 3 variants
            case 5:
            case 6:
            case 7:
                return variation >= 0 && variation <= 2;
            //Have 4 variants
            case 1:
                return variation >= 0 && variation <= 3;
            //Have 9 variants
            case 4:
                return variation >= 0 && variation <= 8;
            default:
                return false;
        }
    }
    
    /**
     * Erschafft ein neues Feld.
     */
    public Tile(int feldTyp, int feldVariation, int feldMauern)
    {
        type = feldTyp;
        variation = feldVariation;
        border = feldMauern%16;
        
    }

    public Tile()
    {
        this(0, 0, 0);
    }

    /**
     * Verändert den Typ des Feldes.
     */
    public void setType(int feldTyp)
    {
    	
    	type = feldTyp;
    }
    /**
     * Verändert die Variation des Feldes.
     */
    public void setVariation(int feldVariation)
    {
    	
        variation = feldVariation;
    }
    /**
     * Verändert die Mauern eines Feldes.
     */
    public void setBorder(int feldMauern)
    {
    	if (feldMauern >= 0)
        border = feldMauern%16;
    }
    
    
    
    /**
     * Gibt den FeldTyp des Feldes zur�ck.
     */
    public int getType()
    {
        return type;
    }
    /**
     * Gibt die FeldVariation des Feldes zur�ck.
     */
    public int getVariation()
    {
        return variation;
    }
    /**
     * Gibt die Mauern des Feldes zur�ck.
     */
    public int getBorders()
    {
        return border;
    }
}
