package de.ten.tacles.blockx;

/**
 * Diese Klasse speichert Koordinaten von Felder, zb f�r die Spielerz�ge.
 * 
 * @author (Jan-Niklas Neumann) 
 * @version (23X10Y12)
 */
public class Zuege
{
    private int SpielerNummer ;
    private int XCoord ;
    private int YCoord ;
    private boolean zuender;
    private Zuege Next ;

    /**
     * Konstruktor f�r Objekte der Klasse Z�ge
     */
    
    public Zuege()
    {
        XCoord = -1 ;
        YCoord = -1 ;
        //ZugArt = ZugTyp ;
    }
    
    public Zuege(int X, int Y)
    {
        XCoord = X ;
        YCoord = Y ;
        //ZugArt = ZugTyp ;
    }
    
    public Zuege(int Spieler, int X, int Y)
    {
        SpielerNummer = Spieler ;
        XCoord = X ;
        YCoord = Y ;
        //ZugArt = ZugTyp ;
    }
    public Zuege(int Spieler, int X, int Y, boolean boom )
    {
        SpielerNummer = Spieler ;
        XCoord = X ;
        YCoord = Y ;
        zuender = boom;
        //ZugArt = ZugTyp ;
    }

    /**
     * Diese Methoden geben die Daten des Angriffes zur�ck.
     */
    public int GibXCoord()
    {
        return XCoord;
    }
    public int GibYCoord()
    {
        return YCoord;
    }
    public int GibSpieler()
    {
        return SpielerNummer;
    }
    public boolean GibZuender()
    {
        return zuender;
    }
    public Zuege GibNaechstenZug()
    {
        return Next;
    }
    
    public void SetzeNaechstenAngriff(Zuege Naechster)
    {
        Next = Naechster;
    }
}
