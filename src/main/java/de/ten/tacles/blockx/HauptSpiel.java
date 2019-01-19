package de.ten.tacles.blockx;


import java.util.ArrayList;
import java.util.Vector;

import javafx.scene.paint.Color;
import de.ten.tacles.blockx.KI.ComputerSpieler;
import de.ten.tacles.blockx.KI.Intelligence;


public class HauptSpiel
{
	
	
	
    private Spielfeld spielFeld;
    private Vector<Vector<Zuege>> planungen;
    private boolean teleporterAktiviert[][]; //Gibt an welcher teleporter von welchem Spieler aktieviert wurde
    
    private boolean spielerIsBot[]; //Gibt an ob ein Spieler vom Computer gesteuert wird, die Intelligenz wird im Computer gespeichert.
    private ComputerSpieler KI;
    
    private int spielerAnzahl;
    private int[] zugAnzahlen;


    private boolean[] spielerRaus;
    private boolean[] spielerGradeVerloren;
   
    public HauptSpiel(Spielfeld feldHelfer)
    {
    	
        spielerAnzahl = feldHelfer.getPlayerCount();
        spielFeld = feldHelfer;
        
        KI = new ComputerSpieler(feldHelfer, spielerAnzahl);
        

        planungen = new Vector<>();
        zugAnzahlen = new int[spielerAnzahl];
        for (int i = 0; i < spielerAnzahl; i++)
        {
        	planungen.add( new Vector<>());
        }

        teleporterAktiviert = new boolean[9][spielerAnzahl];
        spielerRaus = new boolean[spielerAnzahl];
        spielerGradeVerloren = new boolean[spielerAnzahl];
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < spielerAnzahl; j++)
            {
                teleporterAktiviert[i][j] = false;
            }
        }

        spielerIsBot = new boolean[spielerAnzahl];

        for (int i = 0; i < spielerAnzahl; i++)
        {
            zugAnzahlen[i] = ZaehleKernFelder(i+1);
            System.out.println("Zuganzahl Spieler " + (i+1) +  " = "+ zugAnzahlen[i]);
        }

    }


    /**
     * Setzt einen Zug für den angegeben Spieler.
     *
     * Mögliche Return Strings:
     * OK/            = Der Zug wurde platziert.
     * NOMOVES/       = Der Spieler kann keine Züge mehr platzieren
     * NOPLACE/       = Der Spieler kann dort keinen Zug vergeben
     * NEWTURN/       = Die Runde wurde beendet, alle Züge wurden ausgeührt
     * PLAYERLOST(X)/ = Der Spieler (X) Hat verloren
     * VICTOR(X)/     = Der Spieler (X) hat gewonnen
     */
    public ArrayList<ReturnStrings> setzeZug(int XCoord, int YCoord, int player)
    {
        //Dieser Wert soll Kommunikation mit der View erlauben
        ArrayList<ReturnStrings> toReturn = new ArrayList<>();

        if (zugAnzahlen[player-1] > 0 && !spielerIsBot[player-1] && PlatzierungMoeglich(XCoord, YCoord, player))
        {
            Zuege neuerZug = new Zuege(player-1, XCoord, YCoord);
            planungen.get(player-1).add(neuerZug);

            //Einige Felder haben besondere Eigenschaften wenn man auf sie zieht
            if (spielFeld.getTileType(XCoord,YCoord) == 4 && spielFeld.getTileVariation(XCoord, YCoord) >= 0 && spielFeld.getTileVariation(XCoord, YCoord) < 9)
                teleporterAktiviert[spielFeld.getTileVariation(XCoord,YCoord)][player-1] = true;
            else if (spielFeld.getTileType(XCoord,YCoord) == 7 && spielFeld.getTileVariation(XCoord, YCoord) == 1)
                //Geladene Kernfelder geben einen zus�tzlichen Zug, statt einen abzuziehen
                zugAnzahlen[player-1]+=2;

            zugAnzahlen[player-1]--;
            toReturn.add(ReturnStrings.OK);
        }

        //Warum kann ein Zug nicht gesetzt werden?
        else {
            if (zugAnzahlen[player - 1] <= 0)
                toReturn.add(ReturnStrings.NOMOVES);
            if (!PlatzierungMoeglich(XCoord, YCoord, player))
                toReturn.add(ReturnStrings.NOPLACE);
        }

        if (unfinishedPlayers() == 0)
            toReturn.addAll(rundenEnde());

        return toReturn;
    }


    public int unfinishedPlayers()
    {
        int toReturn = 0;
        for (int i = 0; i < spielerAnzahl; i++)
        {
            if (zugAnzahlen[i] > 0)
                toReturn++;
        }

        return toReturn;
    }

    /**
     * Jeder Spieler hat alle seine Züge vergeben.
     * Nun werden alle Züge ausgeführt, und die nächste Runde wird ausgelöst.
     */

    public ArrayList<ReturnStrings> rundenEnde()
    {
        ArrayList<ReturnStrings> toReturn = new ArrayList<>();
        toReturn.add(ReturnStrings.NEWTURN);
        FuehreZugeAus();
        resetTeleporters();
        KI.updateHeatmaps();

        //Spieler die keine Kernfelder ber�hren, fliegen hier raus
        for (int i = 1; i <= spielerAnzahl; i++) {
            spielerGradeVerloren[i-1] = false;
            if (ZaehleBlockierteKernFelder(i) == 0 && !spielerRaus[i-1] ) {
                spielerRaus[i - 1] = true;
                spielerGradeVerloren[i-1] = true;
                toReturn.add(ReturnStrings.PLAYERLOST);
            }
        }
        //Gibt es keine oder nur einen Spieler, beendet sich das Spiel.
        int helfer = 0;
        for (boolean spielerRau : spielerRaus) {
            if (spielerRau) {
                helfer++;
            }
        }
        if (helfer >= spielerAnzahl -1)
        {
            toReturn.add(ReturnStrings.END);
            toReturn.remove(ReturnStrings.NEWTURN);
        }

        for (int i = 0; i < spielerAnzahl; i++)
        {
            zugAnzahlen[i] = ZaehleKernFelder(i+1);
        }

        return toReturn;
    }

    public boolean[] getRecentlyLostPlayers()
    {

        return spielerGradeVerloren;
    }

    public int getLastPlayer()
    {
        int toReturn = 0;

        for (int i = 0; i < spielerAnzahl; i++)
        {
            if (!spielerRaus[i])
                toReturn = i+1;

        }
        return  toReturn;
    }


    /**
     * Diese Methode setzt die Teleporter zur�ck.
     */
    private void resetTeleporters()
    {
    
        //teleporterAktiviert TeleporterNummer spielerAnzahl
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < spielerAnzahl; j++) {
                teleporterAktiviert[i][j] = false;
            }
        }
        
    }


    private class TriggeredBombs {
        int x, y, player, type;
        boolean bigExplosion;

        private TriggeredBombs(int x, int y, int player, int type, boolean bigExplosion)
        {
            this.x = x;
            this.y = y;
            this.type = type;
            this.player = player;
            this.bigExplosion = bigExplosion;
        }
    }

    
    /**
     * Diese Methode Führt alle Zuege aus.
     *
     * TODO Rewrite it
     */
    private void FuehreZugeAus()
    {

        ArrayList<TriggeredBombs> bombs = new ArrayList<>();

        for (int i = 0; i < spielFeld.getWidth() ; i++)
        {
            for (int j = 0; j < spielFeld.getHeight() ; j++)
            {
                //wenn es ein Blockiertes oder Teleporterfeld ist, passiert nichts.
                if (spielFeld.getTileType(i, j) == 1 || spielFeld.getTileType(i, j) == 4)
                {
                    // Da es ein Blockiertes spielFeld ist, wird es jetzt reduziert
                    if (spielFeld.getTileType(i, j) == 1 && spielFeld.getTileVariation(i, j) == 1) { spielFeld.setTileType(i, j, 1, 2); }
                    else if (spielFeld.getTileType(i, j) == 1 && spielFeld.getTileVariation(i, j) == 2) { spielFeld.setTileType(i, j, 1, 3); }
                    else if (spielFeld.getTileType(i, j) == 1 && spielFeld.getTileVariation(i, j) == 3) { spielFeld.setTileType(i, j, 0, 0); }
                    continue;

                }
                //Counts the number of times a player attacked a tile
                int attackList[] = new int[spielerAnzahl];
                //Who is the player with the most attacks?
                int highestAttacker = 0;
                //How often did he attack?
                int highestAttacked = 0;
                //What is the advantage on the second highest attacker?
                int highestAdvantage = 0;

                //Welche Spieler greifen das spielFeld an, und wie oft?
                for (int p = 0; p < spielerAnzahl; p++){
                    attackList[p] = 0;
                    for (Zuege hilfsZug : planungen.get(p))
                    {
                        if (hilfsZug.GibXCoord() == i && hilfsZug.GibYCoord() == j )
                        {
                            attackList[p]++;
                        }
                    }
                    //If this player has more attacks than the current highestAttacks, he's the new highestAttacker
                    if (attackList[p] > highestAttacked)
                    {
                        highestAdvantage = attackList[p] - highestAttacked;
                        highestAttacked = attackList[p];
                        highestAttacker = p+1;
                    }
                    //If they are equal, reset the advantage and attack, but leave the highest attack
                    else if (attackList[p] == highestAttacked)
                    {
                        highestAttacker = 0;
                        highestAdvantage = 0;
                    }
                    //If I don't have more attacks, I might lower the advantage however
                    else if (highestAttacked < highestAdvantage + attackList[p])
                    {
                        highestAdvantage = highestAttacked - attackList[p];
                    }
                }
                //if highestAttacked == 0, nobody attacked
                if (highestAttacked > 0)
                {
                    //Ist es ein Leeres spielFeld? Typ = 0
                    if (spielFeld.getTileType(i, j) == 0)
                    {
                        //Nobody has the highest attack, thus the tile is now blocked
                        if (highestAttacker == 0)
                            spielFeld.setTileType(i, j, 1, 1);
                        //One player has an advantage of 1, and will conquer the tile
                        else if ( highestAdvantage == 1)
                            spielFeld.setTileType(i, j, 2, highestAttacker);
                        //One player has an advantage of 2 or more and will conquer and upgrade this tile
                        else
                            spielFeld.setTileType(i, j, 3, highestAttacker);
                    }
                    //Ist es ein KernFeld?
                    else if (spielFeld.getTileType(i, j) == 7)
                    {
                        switch (spielFeld.getTileVariation(i, j))
                        {

                        //normales Kernfeld
                            case 0:
                                spielFeld.setTileVariant(i,j,1);
                                break;
                        //geladenes Kernfeld
                            case 1:
                                spielFeld.setTileVariant(i,j,0);
                                break;
                            //Zerst�rtes Kernfeld
                            case 2:
                                //Greift ein Spieler das spielFeld 2-mal an? Wenn ja, wird es zu einem normalen Kernfeld
                                if (highestAttacked >= 2){
                                        spielFeld.setTileVariant(i, j,  0);

                                }
                                break;
                        }
                    }
                    // wenn es ein SpielerFeld ist (2)
                    else if (spielFeld.getTileType(i, j) == 2)
                    {
                        //Ausrechnen ob �fter verteidigt wird als angegriffen (die Zuege aller feindlichen Spieler z�hlen zusammen)
                        int defendedVsAttacked = 0; //Z�hlt die Angriffe und Verteidigungen
                        for (int Z = 0; Z < spielerAnzahl; Z++){
                            if (Z+1 == spielFeld.getTileVariation(i, j)) {
                                defendedVsAttacked = defendedVsAttacked+attackList[Z] ;
                            }
                            else    {
                                defendedVsAttacked = defendedVsAttacked-attackList[Z] ;
                            }
                        }

                        if (defendedVsAttacked < 0) {
                            spielFeld.setTileType(i, j, 1, 1);
                        }
                        else if (defendedVsAttacked > 0)
                        {
                            spielFeld.setTileType(i, j, 3);
                        }
                    }
                     // wenn es ein befestigtes SpielerFeld ist (3)
                    else if (spielFeld.getTileType(i, j) == 3)
                    {
                        //Ausrechnen ob �fter verteidigt wird als angegriffen (die Zuege aller feindlichen Spieler z�hlen zusammen)
                        int defendedVsAttacked = 0; //Z�hlt die Angriffe und Verteidigungen
                        for (int Z = 0; Z < spielerAnzahl; Z++){
                            if (Z+1 == spielFeld.getTileVariation(i, j)) {
                                defendedVsAttacked = defendedVsAttacked+attackList[Z] ;
                            }
                            else    {
                                defendedVsAttacked = defendedVsAttacked-attackList[Z] ;
                            }
                        }
                        //It was attacked once, and thus only downgraded
                        if (defendedVsAttacked == -1) {
                            spielFeld.setTileType(i, j, 2);
                        }
                        //It was attacked twice, and thus turned into a blocked tile
                        else if (defendedVsAttacked < -1) {
                            spielFeld.setTileType(i, j, 1, 1);
                        }
                    }
                    //Wenn es eine Bombe ist
                    else if (spielFeld.getTileType(i, j) == 5)
                    {
                        //It explodes if it was attacked twice.
                        //If one player has attacked twice or more, good
                        //If 2 players each attacked once, neither has an advantage, so that's how we know it was attacked twice
                        if (highestAttacked > 1 || highestAdvantage == 0)
                            bombs.add(new TriggeredBombs(i,j,highestAttacker,spielFeld.getTileVariation(i, j),highestAttacked>1));
                        else
                            bombs.add(new TriggeredBombs(i,j,0,spielFeld.getTileVariation(i, j),highestAttacked>1));
                    }
                }
            }
        }

        for (TriggeredBombs bomb : bombs) {
            BombenLos(bomb.x,bomb.y,bomb.player,bomb.bigExplosion, bomb.type);
        }
        for (int i = 0; i < spielerAnzahl; i++){
            planungen.get(i).clear();
        }
    }

    /**
     * Handles the explosion of bombs after all other changes happened, due to multiple explosions perhaps overriding each other
     *
     * @param xCoord Coordinate where the explosion happened
     * @param yCoord Coordinate where the explosion happened
     * @param spieler The player that activated it (if needed)
     * @param explosion Whether it's a big or small boom
     * @param bombenTyp The type of bomb
     */
    private void BombenLos(int xCoord, int yCoord, int spieler, boolean explosion, int bombenTyp)
    {
        //Wenn es eine Populations Bombe ist
        if (bombenTyp == 0) 
        {   
            //Die Bombe geht hoch, ver�ndert dabei alle Felder um sie herum in Felder des Spielers mit den meisten Angriffen
            if (spieler > 0 && explosion)
            {
                for (int x = -1; x < 2; x++)
                {
                    for (int y = -1; y < 2; y++){
                        spielFeld.setTileType(xCoord+x, yCoord+y, 2, spieler);
                    }
                }
                
            }
            //Die Bombe geht hoch, ver�ndert dabei alle Felder um sie herum in blockierte Felder
            else if (spieler == 0 && explosion)
            {
                for (int x = -1; x < 2; x++)
                {
                    for (int y = -1; y < 2; y++){
                        spielFeld.setTileType(xCoord+x, yCoord+y, 1, 1);
                    }
                }
            }
            //Die Bombe wurde aktiviert, und attakiert alle Felder um sie herum im NAmen des SPielers der sie aktivierte
            else if (spieler > 0)
            {
                for (int x = -1; x < 2; x++)
                {
                    for (int y = -1; y < 2; y++){
                        //leeres spielFeld
                        if (spielFeld.getTileType(xCoord+x, yCoord+y) == 0 ){
                            spielFeld.setTileType(xCoord+x, yCoord+y, 2, spieler);
                        }
                        //spielFeld eines Feindlichen SPielers
                        else if (spielFeld.getTileType(xCoord+x, yCoord+y) == 2 && spielFeld.getTileVariation(xCoord+x, yCoord+y) != spieler)
                        {
                            spielFeld.setTileType(xCoord+x, yCoord+y, 1, 1);
                        }
                        //Befestiges spielFeld eines Feindlichen Spielers
                        else if (spielFeld.getTileType(xCoord+x, yCoord+y) == 3 && spielFeld.getTileVariation(xCoord+x, yCoord+y) != spieler)
                        {
                            spielFeld.setTileType(xCoord+x, yCoord+y, spielFeld.getTileType(xCoord+x, yCoord+y)-1);
                        }
                    }
                }
                            
            }
            //Die Bombe wurde aktiviert, und blockiert oder reduziert alle Felder um sie herum
            else if (spieler == 0)
            {
                for (int x = -1; x < 2; x++)
                {
                    for (int y = -1; y < 2; y++){
                        //leeres spielFeld
                        if (spielFeld.getTileType(xCoord+x, yCoord+y) == 0){
                            spielFeld.setTileType(xCoord+x, yCoord+y, 1, 1);
                        }
                        //Blockierte Felder
                        else if (spielFeld.getTileType(xCoord+x, yCoord+y) == 1 && spielFeld.getTileVariation(xCoord+x, yCoord+y) != 0)
                        {
                            spielFeld.setTileType(xCoord+x, yCoord+y, 1, 1);
                        }
                        //spielFeld eines SPielers
                        else if (spielFeld.getTileType(xCoord+x, yCoord+y) == 2)
                        {
                            spielFeld.setTileType(xCoord+x, yCoord+y, 1, 1);
                        }
                        //Befestiges spielFeld eines Spielers
                        else if (spielFeld.getTileType(xCoord+x, yCoord+y) == 3)
                        {
                            spielFeld.setTileType(xCoord+x, yCoord+y, spielFeld.getTileType(xCoord+x, yCoord+y)-1);
                        }
                    }
                }
                
            }
        } 
                        
                        
                        
                        
        //Wenn es eine Destruktions Bombe ist
        else if (bombenTyp == 1) 
        {
            //Die Bombe wurde aktiviert, und blockiert alle Felder um sie herum
            if (explosion)
            {
                for (int x = -1; x < 2; x++){
                    for (int y = -1; y < 2; y++){
                        spielFeld.setTileType(xCoord+x, yCoord+y, 1, 1);
                    }
                }
            }
            //Die Bombe wurde aktiviert, und blockiert oder reduziert alle Felder um sie herum
            if (!explosion)
            {
                for (int x = -1; x < 2; x++)
                {
                    for (int y = -1; y < 2; y++){
                        //leeres spielFeld
                        if (spielFeld.getTileType(xCoord+x, yCoord+y) == 0){
                            spielFeld.setTileType(xCoord+x, yCoord+y, 1, 1);
                        }
                        //Blockierte Felder
                        else if (spielFeld.getTileType(xCoord+x, yCoord+y) == 1 && spielFeld.getTileVariation(xCoord+x, yCoord+y) != 0)
                        {
                            spielFeld.setTileType(xCoord+x, yCoord+y, 1, 1);
                        }
                        //spielFeld eines SPielers
                        else if (spielFeld.getTileType(xCoord+x, yCoord+y) == 2)
                        {
                            spielFeld.setTileType(xCoord+x, yCoord+y, 1, 1);
                        }
                        //Befestiges spielFeld eines Spielers
                        else if (spielFeld.getTileType(xCoord+x, yCoord+y) == 3)
                        {
                            spielFeld.setTileType(xCoord+x, yCoord+y, spielFeld.getTileType(xCoord+x, yCoord+y)-1);
                        }
                    }
                }
            }
        }
        //Wenn es eine Void Bombe ist
        else if (bombenTyp == 2) 
        {
            //Die Bombe geht hoch, ver�ndert dabei alle Felder um sie herum in leere Felder
            if (explosion)
            {
                for (int x = -1; x < 2; x++){
                    for (int y = -1; y < 2; y++){
                        spielFeld.setTileType(xCoord+x, yCoord+y, 0, 0);
                    }
                }
            }
            //Die Bombe wurde aktiviert, und blockiert oder reduziert alle Felder um sie herum
            else
            {
                for (int x = -1; x < 2; x++)
                {
                    for (int y = -1; y < 2; y++){
                        //leicht und Mittelblockierte Felder
                        if (spielFeld.getTileType(xCoord+x, yCoord+y) == 1 && spielFeld.getTileVariation(xCoord+x, yCoord+y) > 1 ){
                            spielFeld.setTileType(xCoord+x, yCoord+y, 0, 0);
                        }
                        //Schwerblockierte Felder
                        else if (spielFeld.getTileType(xCoord+x, yCoord+y) == 1 && spielFeld.getTileVariation(xCoord+x, yCoord+y) == 1 ){
                            spielFeld.setTileType(xCoord+x, yCoord+y, 1, 3);
                        }
                        //spielFeld eines SPielers
                        else if (spielFeld.getTileType(xCoord+x, yCoord+y) == 2)
                        {
                            spielFeld.setTileType(xCoord+x, yCoord+y, 0, 0);
                        }
                        //Befestiges spielFeld eines Spielers
                        else if (spielFeld.getTileType(xCoord+x, yCoord+y) == 3)
                        {
                            spielFeld.setTileType(xCoord+x, yCoord+y, spielFeld.getTileType(xCoord+x, yCoord+y)-1);
                        }
                    }
                }
            }
        }
        
    }
    
    /**
     * Diese Methode z�hlt die Kernfelder die von einem Spieler besitzt werden.
     */
    private int ZaehleKernFelder(int Spieler)
    {
        int Zahl = 0;
        for (int i = 0; i < spielFeld.getWidth() ; i++)
        {
            for (int j = 0; j < spielFeld.getHeight() ; j++)
            {   // Sucht nach Kernfeldern.
                if (spielFeld.getTileType(i, j) == 7 && (spielFeld.getTileVariation(i, j) == 0 || spielFeld.getTileVariation(i, j) == 1) && KernFeldBesitzer(i, j) == Spieler)
                Zahl++;
            }
        }
        int zahl = 0;
        int helfer = 0;
        while (Zahl > 0)
        {
        	
        	Zahl = Zahl - (helfer/3 +1);
        	if (Zahl >= 0)
        		zahl++;
        	helfer++;
        	
        }
        return zahl;
    }
    
    /**
     * Diese Methode z�hlt die Kernfelder die von einem Spieler besitzt werden k�nnten, sofern sie nicht blockiert w�ren
     */
    private int ZaehleBlockierteKernFelder(int spieler)
    {
        int Zahl = 0;
        for (int i = 0; i < spielFeld.getWidth() ; i++)
        {
            for (int j = 0; j < spielFeld.getHeight() ; j++)
            {   // Sucht nach Kernfeldern.
            	boolean hilfe = false;
                if (spielFeld.getTileType(i, j) == 7 && (spielFeld.getTileVariation(i, j) == 0 || spielFeld.getTileVariation(i, j) == 1))
                {
                	
                	
                	if ((spielFeld.getTileType(i-1, j) == 2 || spielFeld.getTileType(i-1, j) == 3) && spielFeld.getTileVariation(i-1, j) == spieler)
                	{
                		hilfe = true;
                	}
                	if ((spielFeld.getTileType(i+1, j) == 2 || spielFeld.getTileType(i+1, j) == 3) && spielFeld.getTileVariation(i+1, j) == spieler)
                	{
                		hilfe = true;
                	}
                	if ((spielFeld.getTileType(i, j-1) == 2 || spielFeld.getTileType(i, j-1) == 3) && spielFeld.getTileVariation(i, j-1) == spieler)
                	{
                		hilfe = true;
                	}
                	if ((spielFeld.getTileType(i, j+1) == 2 || spielFeld.getTileType(i, j+1) == 3) && spielFeld.getTileVariation(i, j+1) == spieler)
                	{
                		hilfe = true;
                	}
                	
                }
                if (hilfe)
                {
                	Zahl++;
                }
            }
        }
        return Zahl;
    }

    
    /**
     * Diese Methode untersucht ob ein Spieler dort seinen Zug platzieren kann.
     */
    
    public boolean PlatzierungMoeglich(int x, int y, int Spieler)
    {
        boolean Moeglichkeit = false;
        //Zuege ZugHilfe;
        
        //Ist es ein Wandfeld?
        if (spielFeld.getTileType(x,y) == 6)
        {
        	return false;
        }
        
        //Ist es ein halbes SpielerFeld?
        if (spielFeld.getTileType(x, y) == 0 && spielFeld.getTileVariation(x, y) == Spieler)
        {
        	//Ein halbes Spielerfeld kann nur einmal frei betreten werden
        	if (!platzierungDone(x,y, Spieler))
        		return true;
        }
        
        //Kern Felder und geladene Kernfelder k�nnen nur einmal betreten werden
        if (spielFeld.getTileType(x, y) == 7 && (spielFeld.getTileVariation(x, y) == 1  || spielFeld.getTileVariation(x, y) == 0) )
        {
            if (platzierungDone(x,y, Spieler))
            		return false;
        }
        
        //Untersuchen ob er neben einem eigenen spielFeld platzieren will.
        boolean helferBool;
        helferBool = (spielFeld.getTileType(x, y-1) == 2 && spielFeld.getTileVariation(x, y-1) == Spieler && spielFeld.getTileBorders(x,y)%2 == 0 );
        helferBool = (helferBool || (spielFeld.getTileType(x, y+1) == 2 && spielFeld.getTileVariation(x, y+1) == Spieler && (spielFeld.getTileBorders(x,y)/4)%2 == 0 ));
        helferBool = (helferBool || (spielFeld.getTileType(x-1, y) == 2 && spielFeld.getTileVariation(x-1, y) == Spieler && (spielFeld.getTileBorders(x,y)/8)%2 == 0 ));
        helferBool = (helferBool || (spielFeld.getTileType(x+1, y) == 2 && spielFeld.getTileVariation(x+1, y) == Spieler && (spielFeld.getTileBorders(x,y)/2)%2 == 0 ));
        if (helferBool){ return true;}
        //Untersuchen ob er neben einem eigenen Befestigten spielFeld platzieren will.
        helferBool = (spielFeld.getTileType(x, y-1) == 3 && spielFeld.getTileVariation(x, y-1) == Spieler && spielFeld.getTileBorders(x,y)%2 == 0  );
        helferBool = (helferBool || (spielFeld.getTileType(x, y+1) == 3 && spielFeld.getTileVariation(x, y+1) == Spieler) && (spielFeld.getTileBorders(x,y)/4)%2 == 0 );
        helferBool = (helferBool || (spielFeld.getTileType(x-1, y) == 3 && spielFeld.getTileVariation(x-1, y) == Spieler) && (spielFeld.getTileBorders(x,y)/8)%2 == 0 );
        helferBool = (helferBool || (spielFeld.getTileType(x+1, y) == 3 && spielFeld.getTileVariation(x+1, y) == Spieler) && (spielFeld.getTileBorders(x,y)/2)%2 == 0 );
        if (helferBool){ return true;}
        
        //Untersuchen ob bereits geplante Zuege es erlauben
        if (planungen.get(Spieler-1).size() > 0)
        {
            //ZugHilfe = planungen[Spieler-1];
            for (Zuege ZugHilfe : planungen.get(Spieler-1))
            {
                if (x-1 == ZugHilfe.GibXCoord() && y == ZugHilfe.GibYCoord() && 
                		spielFeld.getTileType(x-1,y) != 7 &&
                		spielFeld.getTileType(x-1,y) != 3 && (spielFeld.getTileBorders(x,y)/8)%2 == 0 )
                { 
                	//Ein halbes Spielerfeld gibt keine Bewegung auf Nachbarfelder, wenn es alleine ist
                	if (!(spielFeld.getTileType(x-1,y) == 0 && spielFeld.getTileVariation(x-1,y) == Spieler
                			&& !PlatzierungMoeglich(x-1,y, Spieler) && platzierungDone(x-1,y, Spieler)))
                	Moeglichkeit = true;
                } 
                
                else if (x+1 == ZugHilfe.GibXCoord() && y == ZugHilfe.GibYCoord() && 
                		spielFeld.getTileType(x+1,y) != 7 &&
                		spielFeld.getTileType(x+1,y) != 3 && (spielFeld.getTileBorders(x,y)/2)%2 == 0 )
                { 
                	if (!(spielFeld.getTileType(x+1,y) == 0 && spielFeld.getTileVariation(x+1,y) == Spieler
                			&& !PlatzierungMoeglich(x+1,y,Spieler) && platzierungDone(x+1,y, Spieler)))
                	Moeglichkeit = true;
                } 
                
                else if (x == ZugHilfe.GibXCoord() && y-1 == ZugHilfe.GibYCoord() && 
                		spielFeld.getTileType(x,y-1) != 7 &&
                		spielFeld.getTileType(x,y-1) != 3 && spielFeld.getTileBorders(x,y)%2 == 0 )
                { 
                	if (!(spielFeld.getTileType(x,y-1) == 0 && spielFeld.getTileVariation(x,y-1) == Spieler
                			&& !PlatzierungMoeglich(x,y-1, Spieler) && platzierungDone(x,y-1, Spieler)))
                	Moeglichkeit = true;
                } 
                
                else if (x == ZugHilfe.GibXCoord() && y+1 == ZugHilfe.GibYCoord() && 
                		spielFeld.getTileType(x,y+1) != 7 &&
                		spielFeld.getTileType(x,y+1) != 3 &&(spielFeld.getTileBorders(x,y)/4)%2 == 0 )
                { 
                	if (!(spielFeld.getTileType(x,y+1) == 0 && spielFeld.getTileVariation(x,y+1) == Spieler
                			&& !PlatzierungMoeglich(x,y+1, Spieler) && platzierungDone(x,y+1, Spieler)))
                	Moeglichkeit = true;
               } 
                
            }
        }
            
        //Untersuchen, ob er sich neben einem Teleporter befindet den er benutzen kann
        //links
        if (spielFeld.getTileType(x-1,y) == 4 && teleporterAktiviert[spielFeld.getTileVariation(x-1,y)][Spieler-1] && (spielFeld.getTileBorders(x,y)/8)%2 == 0 )
        {
            Moeglichkeit = true;
        }
        //rechts
        if (spielFeld.getTileType(x+1,y) == 4 && teleporterAktiviert[spielFeld.getTileVariation(x+1,y)][Spieler-1] && (spielFeld.getTileBorders(x,y)/2)%2 == 0 )
        {
            Moeglichkeit = true;
        }
        //oben
        if (spielFeld.getTileType(x,y-1) == 4 && teleporterAktiviert[spielFeld.getTileVariation(x,y-1)][Spieler-1] && spielFeld.getTileBorders(x,y)%2 == 0 )
        {
            Moeglichkeit = true;
        }
        //unten
        if (spielFeld.getTileType(x,y+1) == 4 && teleporterAktiviert[spielFeld.getTileVariation(x,y+1)][Spieler-1] &&(spielFeld.getTileBorders(x,y)/4)%2 == 0 )
        {
            Moeglichkeit = true;
        }
       
        
        return Moeglichkeit;
    }
    
    
    /**
     * Diese Methode erlaubt es, die Kontrolle des aktiven Spielers einem Computer zu geben, oder sie wieder weg zu nehmen.
     */
    public void SetzeBot(int intelligence, int spieler)
    {
    	
    	switch (intelligence)
    	{
    	case 1:
    		spielerIsBot[spieler-1] = true;
    		KI.setIntelligence(Intelligence.RANDOM1, spieler-1);
    		break;
    	case 2:
    		spielerIsBot[spieler-1] = true;
    		KI.setIntelligence(Intelligence.AGGRESIVE1, spieler-1);
    		break;
    	case 3:
    		spielerIsBot[spieler-1] = true;
    		KI.setIntelligence(Intelligence.DEFENSIVE1, spieler-1);
    		break;
    	case 4:
    		spielerIsBot[spieler-1] = true;
    		KI.setIntelligence(Intelligence.EXPLORATIVE1, spieler-1);
    		break;
    	case 5:
    		spielerIsBot[spieler-1] = true;
    		KI.setIntelligence(Intelligence.RANDOM2, spieler-1);
    		break;
    	case 6:
    		spielerIsBot[spieler-1] = true;
    		KI.setIntelligence(Intelligence.AGGRESIVE2, spieler-1);
    		break;
    	case 7:
    		spielerIsBot[spieler-1] = true;
    		KI.setIntelligence(Intelligence.DEFENSIVE2, spieler-1);
    		break;
    	case 8:
    		spielerIsBot[spieler-1] = true;
    		KI.setIntelligence(Intelligence.EXPLORATIVE2, spieler-1);
    		break;
    	case 9:
    		spielerIsBot[spieler-1] = true;
    		KI.setIntelligence(Intelligence.LEVEL3, spieler-1);
    		break;
    	case 0:
    	default:
    		spielerIsBot[spieler-1] = false;
    		break;
    	}
    	
    }

    public Spielfeld getSpielFeld()
    {
    	return spielFeld;
    }
    
    /**
     * �berpr�ft ob der Spieler auf diesem spielFeld einen Zug platziert hat.
     * 
     * 
     * @param x Koordinate des Feldes
     * @param y Koordinate des Feldes
     * @param Spieler der Spieler
     * @return Ja oder Nein
     */

    
    public boolean platzierungDone(int x, int y, int Spieler)
    {
    	boolean isPlaziert = false;
    	
    	
    	for (Zuege hilfsZug : planungen.get(Spieler-1))
    	{
    		if (hilfsZug.GibXCoord() == x && hilfsZug.GibYCoord() == y)
    			isPlaziert = true;
    	}
    	
    	return isPlaziert;
    }
    
    public int zaehleFelder(int spieler)
    {
    	int hilfsZahl = 0;
    	for (int i = 0; i < spielFeld.getWidth(); i++)
    	{
    		for (int j = 0; j < spielFeld.getWidth(); j++)
    		{
    			if ((spielFeld.getTileType(i, j) == 2 || spielFeld.getTileType(i, j) == 3) && spielFeld.getTileVariation(i, j)== spieler)
    			{
    				hilfsZahl++;
    			}
    		}
    	}
    	return hilfsZahl;
    }
    
    
    
    public int KernFeldBesitzer(int x, int y)
    {
    	if (!(spielFeld.getTileType(x, y) == 7 && (spielFeld.getTileVariation(x, y) == 0 || spielFeld.getTileVariation(x, y) == 1)))
    	{
    		return 0;
    	}
        int Besitzer = 0;
        int HelferZahl = 0;
        boolean[] feldHerum = new boolean[spielerAnzahl]; //Gibt an ob ein Spieler ein spielFeld um das Kernfeld besitzt
        //Befindet sich ein Spielerfeld um das spielFeld? (Feldtyp >= 6)
        // 2 sind Spieler Felder, 3 sind Befestigte SpielerFelder
        boolean helferBool = (spielFeld.getTileType(x, y-1) == 2 || spielFeld.getTileType(x, y+1) == 2 );
        helferBool = (helferBool || spielFeld.getTileType(x-1, y) == 2 || spielFeld.getTileType(x+1, y) == 2);
        helferBool = (helferBool || spielFeld.getTileType(x, y-1) == 3 || spielFeld.getTileType(x, y+1) == 3);
        helferBool = (helferBool || spielFeld.getTileType(x-1, y) == 3 || spielFeld.getTileType(x+1, y) == 3);
        if (helferBool) 
        { 
            //Wieviele Spieler befinden sich um das spielFeld?
            for (int i = 0; i <= spielerAnzahl; i++)
            {
                helferBool = (spielFeld.getTileType(x, y-1) == 2 && spielFeld.getTileVariation(x, y-1) == i );
                helferBool = (helferBool || (spielFeld.getTileType(x, y+1) == 2 && spielFeld.getTileVariation(x, y+1) == i));
                helferBool = (helferBool || (spielFeld.getTileType(x-1, y) == 2 && spielFeld.getTileVariation(x-1, y) == i));
                helferBool = (helferBool || (spielFeld.getTileType(x+1, y) == 2 && spielFeld.getTileVariation(x+1, y) == i));
                helferBool = (helferBool || (spielFeld.getTileType(x, y-1) == 3 && spielFeld.getTileVariation(x, y-1) == i));
                helferBool = (helferBool || (spielFeld.getTileType(x, y+1) == 3 && spielFeld.getTileVariation(x, y+1) == i));
                helferBool = (helferBool || (spielFeld.getTileType(x-1, y) == 3 && spielFeld.getTileVariation(x-1, y) == i));
                helferBool = (helferBool || (spielFeld.getTileType(x+1, y) == 3 && spielFeld.getTileVariation(x+1, y) == i));
                if (helferBool) 
                {
                    HelferZahl = HelferZahl+1;
                    if(i != 0)
                    feldHerum[i-1]= true;
                }
            }
            //Wenn es nur ein einziger ist, geh�rt das kernfeld jemandem. 
            if (HelferZahl == 1) 
            {
                for (int i = 1; i <= spielerAnzahl; i++)
                {
                    
                    if (feldHerum[i-1]) 
                    {
                          Besitzer = i;
                    }
                }
            }
        }
        // Wenn sich ein schwer oder mittel blockiertes spielFeld um das Kernfeld befindet, geh�rt es keinem!
        helferBool = (spielFeld.getTileType(x,y-1) == 1 && spielFeld.getTileVariation(x, y-1) != 0 && spielFeld.getTileVariation(x, y-1) != 3 );
        helferBool = (helferBool || (spielFeld.getTileType(x,y+1) == 1 && spielFeld.getTileVariation(x, y+1) != 0 && spielFeld.getTileVariation(x, y+1) != 3));
        helferBool = (helferBool || (spielFeld.getTileType(x-1,y) == 1 && spielFeld.getTileVariation(x-1, y) != 0 && spielFeld.getTileVariation(x-1, y) != 3));
        helferBool = (helferBool || (spielFeld.getTileType(x+1,y) == 1 && spielFeld.getTileVariation(x+1, y) != 0 && spielFeld.getTileVariation(x+1, y) != 3));
        if (helferBool)
        {  
            Besitzer = 0;
        }
        
        return Besitzer;
        
    }


	public int getSpielerAnzahl() 
	{
		return spielFeld.getPlayerCount();
	}

	/**
	 * Gibt die Farbe des Spielers aus.
	 * 
	 * @param spieler Der Spieler dessen Farbe gesucht wird.
	 * @return  Die Farbe des Spielers.
	 */
	public Color getFarbe(int spieler)
	{
		return spielFeld.getFarbe(spieler);
	}

	public String getFarbenName(int spieler)
    {
        return spielFeld.getColourNames()[spieler-1];
    }

	public void setColour(double value, int spieler)
	{
		spielFeld.setFarbe(spieler, Color.hsb(value, 1, 1));
	}

    public boolean[] getSpielerRaus() {
        return spielerRaus;
    }

    public int[] getZugAnzahlen(){
	    return zugAnzahlen;
    }
}
