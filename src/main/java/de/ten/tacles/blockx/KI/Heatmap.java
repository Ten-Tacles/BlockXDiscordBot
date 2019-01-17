package de.ten.tacles.blockx.KI;

/**
 * 
 * 
 * @author Ten_Tacles
 * 
 * 
 * Diese Klasse speichert die Distanz die ein Spieler �berbr�cken muss, um auf das entsprechene Feld ziehen zu k�nnen.
 *
 */

public class Heatmap 
{
	//private int player; //Zu welchem Spieler diese Heatmap geh�rt.
	private int[][][] distance; 
	//Die erste Klammer ist die x Koordinate, die 2 die y Koordinate, die letze speichert die 
	//Werte f�r erreichbar/Nicht Direkt erreichbar

	
	public Heatmap(int breite, int hoehe)
	{
		distance = new int[breite][hoehe][2];
		for (int i = 0; i < breite; i++)
		{
			for (int j = 0; j < hoehe; j++)
			{
				distance[i][j][0] = 0;
				distance[i][j][1] = 0;
			}
			
		}
	}
	
	public void resetHeat()
	{
		for (int i = 0; i < distance.length; i++)
		{
			for (int j = 0; j < distance[0].length; j++)
			{
				distance[i][j][0] = 0;
				distance[i][j][1] = 0;
			}
			
		}
		
	}
	/**
	 * Setzt den Wert des Feldes, wenn es erreichbar ist.
	 * 
	 * @param x Die x Koordinate des Feldes
	 * @param y Die y Koordinate des Feldes
	 * @param z Der erreichbare wert des Feldes
	 */

	public void setHeat0(int x, int y, int z)
	{
		if (!(x < 0 || y < 0 || x >= distance.length || y >= distance[0].length))
		distance[x][y][0] = z;
		
	}
	
	/**
	 * Setzt den Wert des Feldes, wenn es nicht direkt erreichbar ist
	 * 
	 * @param x Die x Koordinate des Feldes
	 * @param y Die y Koordinate des Feldes
	 * @param z Der nicht direkte Wert des Feldes
	 */

	public void setHeat1(int x, int y, int z)
	{
		if (!(x < 0 || y < 0 || x >= distance.length || y >= distance[0].length))
		distance[x][y][1] = z;
		
	}
	
	public int getHeat0(int x, int y)
	{
		if (x < 0 || y < 0 || x >= distance.length || y >= distance[0].length)
			return 0;
		return distance[x][y][0];
	}
	
	public int getHeat1(int x, int y)
	{
		if (x < 0 || y < 0 || x >= distance.length || y >= distance[0].length)
			return 0;
		return distance[x][y][1];
	}
	/**
	 * Diese Funktion �berpr�ft ob der erste Wert "besser" ist als der zweite.
	 * Er ist besser falls er um mindestens 2 kleiner ist als der Zweite, aber gr��er ist als 0, oder der zweite Wert == 0 ist.
	 * @param firstHeat Der erste Heat Wert
	 * @param secondHeat Der zweite Heat Wert.
	 * @return True falls der erste besser ist, false wenn beide gleich oder der Zweite besser ist.
	 */
	
	public static boolean heatIsBetter(int firstHeat, int secondHeat)
	{
		//Heatwerte unter oder gleich 0 wurden noch nicht gesetzt, und sind deshalb automatisch drau�en.
		if (firstHeat <= 0 )
		{
			return false;
		}
		else if (secondHeat <= 0)
		{
			return true;
		}
		//Ist der Heatwert kleiner, aber gr��er als null, ist er besser.
		else if (firstHeat < secondHeat)
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	
}
