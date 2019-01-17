package de.ten.tacles.blockx.KI;


import java.util.Vector;

import de.ten.tacles.blockx.Spielfeld;
import de.ten.tacles.blockx.Zuege;

public class ComputerSpieler 
{
	
	
	Heatmap heatmaps[];
	Spielfeld feld;
	Intelligence intelligence[]; //Enth?lt den Schwierigkeits Grad des Spielers. 
	
	
	
	public ComputerSpieler (Spielfeld feld, int SpielerAnzahl)
	{
		this.feld = feld;
		heatmaps = new Heatmap[SpielerAnzahl];
		intelligence = new Intelligence[SpielerAnzahl];
		
		for (int i = 0; i < SpielerAnzahl; i++)
		{
			heatmaps[i] = new Heatmap(feld.getWidth(), feld.getHeight());
			intelligence[i] = Intelligence.RANDOM1;
			
		}
		
		updateHeatmaps();
	}
	
	/**
	 * Updated die Heatmaps von allen Spielern.
	 * 
	 * Heatmaps enthalten Nummern die aussagen wieviele Zuege ein Spieler br?uchte um auf dieses Feld zu ziehen.
	 */
	
	public void updateHeatmaps()
	{
		for (int i = 0; i < heatmaps.length; i++)
		{
			heatmaps[i].resetHeat();
			Vector<Zuege> toUpdate = new Vector<Zuege>();
			
			System.out.println("Starte Heatmap f?r Spieler " + i);
			//findet alle Spielerfelder dieses Spielers, und f?gt sie toUpdate hinzu.
			for (int b = 0; b < feld.getWidth(); b++)
			{
				for (int h = 0; h < feld.getHeight(); h++)
				{
					if ( (feld.getTileType(b, h) == 2 || feld.getTileType(b, h) == 3) && feld.getTileVariation(b, h) == i+1)
					{
						Zuege temp = new Zuege(b, h);
						toUpdate.add(temp);
					}
				}
			}
			
			System.out.println("Intialisierung fertig");
			
			while (toUpdate.isEmpty() == false)
			{
				//System.out.println("Vektor enth?lt " + toUpdate.size());
				Zuege temp = toUpdate.remove(0);
				int x = temp.GibXCoord();
				int y = temp.GibYCoord();
				
				//Wenn das Ziel direkt besser erreichbar ist als indirekt
				//K?nnte sein das das ?berfl?ssig ist
				if(Heatmap.heatIsBetter(heatmaps[i].getHeat0(x, y), heatmaps[i].getHeat1(x, y)))
					heatmaps[i].setHeat0(x, y, heatmaps[i].getHeat0(x, y));
				
				//Kern und Bombenfelder werden nicht genutzt um Felder zu ?berspringen, sie geben ihren Wert nicht weiter.
				if (feld.getTileType(x, y) != 5)
				{
					for (int k = -1; k < 2; k +=2 )
					{
						for (int j = 0; j < 2; j++)
						{
							int oX = x+(k*j);
							int oY = y+(k*(1-j));
							//Wandfelder k?nnen nicht betreten werden.
							//Kernfelder k?nnen auch nicht betreten werden
							// Es darf auch keine Mauer im Weg sein
							if (!(feld.getTileType(oX, oY) ==  6 || feld.getTileType(x, y) == 7)
							&& ((feld.getTileBorders(oX, oY)/1)%2 == 0  || !(k == -1 && j == 1))// Mauer Oben
							&& ((feld.getTileBorders(oX, oY)/2)%2 == 0  || !(k == 1 && j == 0))// Mauer Rechts
							&& ((feld.getTileBorders(oX, oY)/4)%2 == 0  || !(k == 1 && j == 1))// Mauer Unten
							&& ((feld.getTileBorders(oX, oY)/8)%2 == 0  || !(k == -1 && j == 0))// Mauer Links
							)
							{
								//Bin ich ein Spielerfeld des Spielers den ich grade bearbeite?
								if ( (feld.getTileType(x, y) ==  2 || feld.getTileType(x, y) ==  3 ) && feld.getTileVariation(x, y) == i+1 )
								{
									if (heatmaps[i].getHeat0(oX, oY) != 1)
									{
										heatmaps[i].setHeat0(oX, oY, 1);
										heatmaps[i].setHeat1(oX, oY, 1);
										toUpdate.add(new Zuege(oX, oY));
									}
								}
								//Bin ich ein gegnerisches Bef Feld?
								//Sie erlauben keinen Zug hinter sich, aber machen es nicht unm?glich
								else if (feld.getTileType(x, y) ==  3 && !(feld.getTileVariation(x, y) == i+1 ) )
								{
									//Der direkte Wert des Feldes ist besser als der indirekte Wert des angrenzenden Feldes und von sich selbst.
									if (Heatmap.heatIsBetter(heatmaps[i].getHeat0(x, y)+1, heatmaps[i].getHeat1(oX, oY)) && 
											Heatmap.heatIsBetter(heatmaps[i].getHeat0(x, y), heatmaps[i].getHeat1(x, y)))
									{
										heatmaps[i].setHeat1(oX, oY, heatmaps[i].getHeat0(x, y)+1);
										toUpdate.add(new Zuege(oX, oY));
									}
									//Der indirekte Wert des Feldes ist besser als der direkte Wert des angrenzenden Feldes
									else if (Heatmap.heatIsBetter(heatmaps[i].getHeat1(x, y), heatmaps[i].getHeat1(oX, oY)))
									{
										heatmaps[i].setHeat1(oX, oY, heatmaps[i].getHeat1(x, y)+1);
										toUpdate.add(new Zuege(oX, oY));
									}
									
									
								}
								//Ein leeres Feld, ein Feld eines anderen Spielers, ein blockiertes Feld, ein Teleporter Feld, usw.
								else
								{
									//Ich gebe meinen erreichbaren Wert weiter
									if (Heatmap.heatIsBetter(heatmaps[i].getHeat0(x, y)+1, heatmaps[i].getHeat0(oX, oY)) &&
											heatmaps[i].getHeat0(x, y) > 0)
									{
										heatmaps[i].setHeat0(oX, oY, heatmaps[i].getHeat0(x, y)+1);
										toUpdate.add(new Zuege(oX, oY));
									}
									//Ich gebe meinen unerreichbaren Wert weiter
									if (Heatmap.heatIsBetter(heatmaps[i].getHeat1(x, y)+1, heatmaps[i].getHeat1(oX, oY)) &&
											heatmaps[i].getHeat1(x, y) > 0 )
									{
										heatmaps[i].setHeat1(oX, oY, heatmaps[i].getHeat1(x, y)+1);
										toUpdate.add(new Zuege(oX, oY));
									}
									
								}
								
							}
						}
						
					}
				}	
				
				//Bin ich ein Teleporter Feld? Dann setze ich alle anderen auf meinen Wert, wenn dieser kleiner ist.
				if (feld.getTileType(x, y) ==  4)
				{
					for (int xC = 0; xC < feld.getWidth(); xC++)
					{
						for (int yC = 0; yC < feld.getHeight(); yC++)
						{
							if (feld.getTileType(xC, yC) == 4 && feld.getTileVariation(xC, yC) == feld.getTileVariation(x, y))
							{
								//Ich gebe meinen erreichbaren Wert weiter
								if (Heatmap.heatIsBetter(heatmaps[i].getHeat0(x, y), heatmaps[i].getHeat0(xC, yC))
									&& heatmaps[i].getHeat0(x,y) > 0)
								{
									heatmaps[i].setHeat0(xC, yC, heatmaps[i].getHeat0(x, y));
									toUpdate.add(new Zuege(xC, yC));
								}
								//Ich gebe meinen unerreichbaren Wert weiter
								if (Heatmap.heatIsBetter(heatmaps[i].getHeat1(x, y), heatmaps[i].getHeat1(xC, yC)) 
										&& heatmaps[i].getHeat1(x,y) > 0)
								{
									heatmaps[i].setHeat1(xC, yC, heatmaps[i].getHeat1(x, y));
									toUpdate.add(new Zuege(xC, yC));
										
								}
								
							}
						}
					}
				}
				//Bin ich eine Bombe? Dann setze ich allen Feldern die ich nicht direkt betreten kann (wie W?nde), einen besseren Wert
				else if (feld.getTileType(x, y) ==  5)
				{
					for (int xC = -1; xC < 2; xC++)
					{
						for (int yC = -1; yC < 2; yC++)
						{
							//Mauern k?nnen zerst?rt werden.
							if (feld.getTileType(x+xC, y+yC) == 6)
							{
								if (Heatmap.heatIsBetter(heatmaps[i].getHeat0(x, y), heatmaps[i].getHeat1(x, y)))
									heatmaps[i].setHeat1(x+xC, y+yC, heatmaps[i].getHeat0(x,y) + Math.abs(xC) + Math.abs(yC));
								else if (Heatmap.heatIsBetter(heatmaps[i].getHeat1(x, y), heatmaps[i].getHeat1(x, y)))
									heatmaps[i].setHeat1(x+xC, y+yC, heatmaps[i].getHeat1(x,y) + Math.abs(xC) + Math.abs(yC));
								toUpdate.add(new Zuege(x+xC, y+yC));
							}
						}
					}
				}
				
				//Hier ist die Schleife zu Ende.
				
			}
			
		}
		
		
	}
	
	/**
	 * ?ndert den Heatwert eines Feldes f?r einen Spieler und gibt diese Ver?nderung weiter.
	 * 
	 * @param xCoord Die xKoordinate des Feldes.
	 * @param yCoord Die yKoordinate des Feldes.
	 * @param heat Der neue Heatwert des Feldes.
	 * @param player Der Spieler dessen Heatwerte geupdated werden.
	 */
	public void quickUpdate(int xCoord, int yCoord, int heat, int player)
	{
		heatmaps[player].setHeat0(xCoord, yCoord, heat);
		Vector<Zuege> toUpdate = new Vector<Zuege>();
		toUpdate.add(new Zuege(xCoord, yCoord));
		while (toUpdate.isEmpty() == false)
		{
			//System.out.println("Vektor enth?lt " + toUpdate.size());
			Zuege temp = toUpdate.remove(0);
			int x = temp.GibXCoord();
			int y = temp.GibYCoord();
			
			//Wenn das Ziel direkt besser erreichbar ist als indirekt
			//K?nnte sein das das ?berfl?ssig ist
			if(Heatmap.heatIsBetter(heatmaps[player].getHeat0(x, y), heatmaps[player].getHeat1(x, y)))
				heatmaps[player].setHeat0(x, y, heatmaps[player].getHeat0(x, y));
			
			//Kern und Bombenfelder werden nicht genutzt um Felder zu ?berspringen, sie geben ihren Wert nicht weiter.
			if (feld.getTileType(x, y) != 5)
			{
				for (int k = -1; k < 2; k +=2 )
				{
					for (int j = 0; j < 2; j++)
					{
						int oX = x+(k*j);
						int oY = y+(k*(1-j));
						//Wandfelder k?nnen nicht betreten werden.
						//Kernfelder k?nnen auch nicht betreten werden
						// Es darf auch keine Mauer im Weg sein
						if (!(feld.getTileType(oX, oY) ==  6 || feld.getTileType(x, y) == 7)
						&& ((feld.getTileBorders(oX, oY)/1)%2 == 0  || !(k == -1 && j == 1))// Mauer Oben
						&& ((feld.getTileBorders(oX, oY)/2)%2 == 0  || !(k == 1 && j == 0))// Mauer Rechts
						&& ((feld.getTileBorders(oX, oY)/4)%2 == 0  || !(k == 1 && j == 1))// Mauer Unten
						&& ((feld.getTileBorders(oX, oY)/8)%2 == 0  || !(k == -1 && j == 0))// Mauer Links
						)
						{
							//Bin ich ein Spielerfeld des Spielers den ich grade bearbeite?
							if ( (feld.getTileType(x, y) ==  2 || feld.getTileType(x, y) ==  3 ) && feld.getTileVariation(x, y) == player+1 )
							{
								if (heatmaps[player].getHeat0(oX, oY) != 1)
								{
									heatmaps[player].setHeat0(oX, oY, 1);
									heatmaps[player].setHeat1(oX, oY, 1);
									toUpdate.add(new Zuege(oX, oY));
								}
							}
							//Bin ich ein gegnerisches Bef Feld?
							//Sie erlauben keinen Zug hinter sich, aber machen es nicht unm?glich
							else if (feld.getTileType(x, y) ==  3 && !(feld.getTileVariation(x, y) == player+1 ) )
							{
								//Der direkte Wert des Feldes ist besser als der indirekte Wert des angrenzenden Feldes und von sich selbst.
								if (Heatmap.heatIsBetter(heatmaps[player].getHeat0(x, y)+1, heatmaps[player].getHeat1(oX, oY)) && 
										Heatmap.heatIsBetter(heatmaps[player].getHeat0(x, y), heatmaps[player].getHeat1(x, y)))
								{
									heatmaps[player].setHeat1(oX, oY, heatmaps[player].getHeat0(x, y)+1);
									toUpdate.add(new Zuege(oX, oY));
								}
								//Der indirekte Wert des Feldes ist besser als der direkte Wert des angrenzenden Feldes
								else if (Heatmap.heatIsBetter(heatmaps[player].getHeat1(x, y), heatmaps[player].getHeat1(oX, oY)))
								{
									heatmaps[player].setHeat1(oX, oY, heatmaps[player].getHeat1(x, y)+1);
									toUpdate.add(new Zuege(oX, oY));
								}
								
								
							}
							//Ein leeres Feld, ein Feld eines anderen Spielers, ein blockiertes Feld, ein Teleporter Feld, usw.
							else
							{
								//Ich gebe meinen erreichbaren Wert weiter
								if (Heatmap.heatIsBetter(heatmaps[player].getHeat0(x, y)+1, heatmaps[player].getHeat0(oX, oY)) &&
										heatmaps[player].getHeat0(x, y) > 0)
								{
									heatmaps[player].setHeat0(oX, oY, heatmaps[player].getHeat0(x, y)+1);
									toUpdate.add(new Zuege(oX, oY));
								}
								//Ich gebe meinen unerreichbaren Wert weiter
								if (Heatmap.heatIsBetter(heatmaps[player].getHeat1(x, y)+1, heatmaps[player].getHeat1(oX, oY)) &&
										heatmaps[player].getHeat1(x, y) > 0 )
								{
									heatmaps[player].setHeat1(oX, oY, heatmaps[player].getHeat1(x, y)+1);
									toUpdate.add(new Zuege(oX, oY));
								}
								
							}
							
						}
					}
					
				}
			}	
			
			//Bin ich ein Teleporter Feld? Dann setze ich alle anderen auf meinen Wert, wenn dieser kleiner ist.
			if (feld.getTileType(x, y) ==  4)
			{
				for (int xC = 0; xC < feld.getWidth(); xC++)
				{
					for (int yC = 0; yC < feld.getHeight(); yC++)
					{
						if (feld.getTileType(xC, yC) == 4 && feld.getTileVariation(xC, yC) == feld.getTileVariation(x, y))
						{
							//Ich gebe meinen erreichbaren Wert weiter
							if (Heatmap.heatIsBetter(heatmaps[player].getHeat0(x, y), heatmaps[player].getHeat0(xC, yC))
								&& heatmaps[player].getHeat0(x,y) > 0)
							{
								heatmaps[player].setHeat0(xC, yC, heatmaps[player].getHeat0(x, y));
								toUpdate.add(new Zuege(xC, yC));
							}
							//Ich gebe meinen unerreichbaren Wert weiter
							if (Heatmap.heatIsBetter(heatmaps[player].getHeat1(x, y), heatmaps[player].getHeat1(xC, yC)) 
									&& heatmaps[player].getHeat1(x,y) > 0)
							{
								heatmaps[player].setHeat1(xC, yC, heatmaps[player].getHeat1(x, y));
								toUpdate.add(new Zuege(xC, yC));
									
							}
							
						}
					}
				}
			}
			//Bin ich eine Bombe? Dann setze ich allen Feldern die ich nicht direkt betreten kann (wie W?nde), einen besseren Wert
			else if (feld.getTileType(x, y) ==  5)
			{
				for (int xC = -1; xC < 2; xC++)
				{
					for (int yC = -1; yC < 2; yC++)
					{
						//Mauern k?nnen zerst?rt werden.
						if (feld.getTileType(x+xC, y+yC) == 6)
						{
							if (Heatmap.heatIsBetter(heatmaps[player].getHeat0(x, y), heatmaps[player].getHeat1(x, y)))
								heatmaps[player].setHeat1(x+xC, y+yC, heatmaps[player].getHeat0(x,y) + Math.abs(xC) + Math.abs(yC));
							else if (Heatmap.heatIsBetter(heatmaps[player].getHeat1(x, y), heatmaps[player].getHeat0(x, y)))
								heatmaps[player].setHeat1(x+xC, y+yC, heatmaps[player].getHeat1(x,y) + Math.abs(xC) + Math.abs(yC));
							toUpdate.add(new Zuege(x+xC, y+yC));
						}
					}
				}
			}
			
			//Hier ist die Schleife zu Ende.
			
		}
	}
	
	
	
	/**
	 * Fragt nach den Zuegen die der Spieler player machen wird.
	 * @param player Der Spieler f?r den Zuege gesucht werden
	 * @param zuege Die Anzahl an Zuegen die er zur Verf?gung hat
	 * @return Eine Anzahl von Zuegen
	 */
	public Vector<Zuege> getTurns(int player, int zuege)
	{
		Vector<Zuege> result = new Vector<Zuege>();;
		
		switch(intelligence[player])
		{
		case LEVEL3:
			result = getLevel3Intelligence(player, zuege);
			break;
		case AGGRESIVE1:
			result = getAggresive1(player, zuege);
			break;
		case AGGRESIVE2:
			System.out.println("Finde aggressive zuege von spieler " + (player+1));
			result = getLevel2Intelligence(player, zuege, 0);
			break;
		case DEFENSIVE1:
			result = getDefensive1(player, zuege);
			break;
		case DEFENSIVE2:
			result = getLevel2Intelligence(player, zuege, 1);
			break;
		case EXPLORATIVE1:
			result = getExplorative1(player, zuege);
			break;
		case EXPLORATIVE2:
			result = getLevel2Intelligence(player, zuege, 2);
			break;
		case RANDOM2:
			result = getLevel2Intelligence(player, zuege, -1);
			break;
		case RANDOM1:
		default:
			result = getRandom1(player, zuege);
			break;
		}
		
		return result;
	}
	
	/**
	 * Birgt den internen Algorithmus f?r die Level 3 Intelligenzen.
	 * 
	 * 
	 * @param player Der Spieler f?r den Zuege gesucht werden
	 * @param zuege Die Anzahl an Zuegen die er zur Verf?gung hat
	 * @return Eine Anzahl von Zuegen
	 */
	private Vector<Zuege> getLevel3Intelligence(int player, int zuege)
	{
		Vector <Zuege> result = new Vector<Zuege>();
		Vector<Zuege> directTargets = findCoreFields();
		Vector<Zuege> inDirectTargets = new Vector<Zuege>();
		Vector<Zuege> toRemove = new Vector<Zuege>();
		
		
		//Felder die ich nicht direkt erreichen kann, werden aus directTargets entfernt, und zu inDirectTargets hinzugef?gt
		//Felder die ich indirekt schneller erreichen kann, werden zu indirectTargets hinzugef?gt, aber NICHT aus directTargets entfernt
		for (Zuege temp: directTargets)
		{
			int x = temp.GibXCoord();
			int y = temp.GibYCoord();
			
			if (Heatmap.heatIsBetter(heatmaps[player].getHeat1(x, y) , heatmaps[player].getHeat0(x, y))
					&& !(feld.getTileVariation(x, y) == player+1 && (feld.getTileType(x, y) == 2 || feld.getTileType(x, y) == 3)))
			{
				
				inDirectTargets.add(temp);
				if (heatmaps[player].getHeat0(x, y) == 0)
					toRemove.add(temp);
			}
			else if (heatmaps[player].getHeat0(x, y) == 0 || (feld.getTileVariation(x, y) == player+1 && (feld.getTileType(x, y) == 2 || feld.getTileType(x, y) == 3)))
				toRemove.add(temp);
		}
		
		for (Zuege temp: toRemove)
		{
			directTargets.remove(temp);
		}
		
		
		if (directTargets.size()>0)
		directTargets = sortForDistance(player, directTargets, 0, directTargets.size()-1);
		for (int i = 0; i < directTargets.size(); i++)
		{
			int x = directTargets.get(i).GibXCoord();
			int y = directTargets.get(i).GibYCoord();
			
			System.out.println( "Dir: x="+ x + ", y=" + y + " : d=" +  heatmaps[player].getHeat0(x, y));
		}
		if (inDirectTargets.size()>0)
		inDirectTargets = sortForIndirectDistance(player, inDirectTargets, 0, inDirectTargets.size()-1);
		
		for (int i = 0; i < inDirectTargets.size(); i++)
		{
			int x = inDirectTargets.get(i).GibXCoord();
			int y = inDirectTargets.get(i).GibYCoord();
			
			System.out.println("inDir: x="+ x + ", y=" + y + " : d=" + heatmaps[player].getHeat1(x, y));
		}
		
		
		System.out.println("Suche nach direkten Aktionen");
		//Finde m?gliche Pfade.
		Vector<Vector<Zuege>> possibleDirectActions = new Vector<Vector<Zuege>>();
		
		for (Zuege temp : directTargets)
		{
			int x = temp.GibXCoord();
			int y = temp.GibYCoord();
			boolean notOkay = true;
			int heat = heatmaps[player].getHeat0(x,y);
			while (notOkay && heat <= heatmaps[player].getHeat0(x,y)+3)
			{
				Vector<Vector<Zuege>> foundPaths = findPossibleDirectPaths(x, y, player, heat);
				System.out.println(heat + "  " + heatmaps[player].getHeat0(x,y)+3);
				
				for (Vector<Zuege> temp2 : foundPaths)
				{
					//Wenn ich nicht den ganzen Weg gehen kann.
					
					if (zuege < heat)
					{
						int x2 = temp2.get(zuege-1).GibXCoord();
						int y2 = temp2.get(zuege-1).GibYCoord();
						
						if (feld.getTileType(x2, y2) != 1 && feld.getTileType(x2, y2) != 4)
						{
							//Ein weiteres Feld wird hinzuge?gt, um es als direkten Pfad zu erkennen
							temp2.add(new Zuege(-1, -1));
							possibleDirectActions.add(temp2);
							notOkay = false;
						}
					}
					else 
					{
						int x2 = temp2.lastElement().GibXCoord();
						int y2 = temp2.lastElement().GibYCoord();
						
						if (feld.getTileType(x2, y2) != 1 && feld.getTileType(x2, y2) != 4)
						{
							temp2.add(new Zuege(-1, -1));
							possibleDirectActions.add(temp2);
							notOkay = false;
						}
					}
					
				}
				
				//Wenn ich keinen guten Weg gefunden hab, suche ich auch Wege die ein bischen l?nger sind.
				heat++;
			}
		}
		
		for (int i = 0; i < possibleDirectActions.size() ; i++)
		{
			for (Zuege temp : possibleDirectActions.get(i))
			{
				int x = temp.GibXCoord();
				int y = temp.GibYCoord();
					
				System.out.println("Possible move: " + i + " | x = " + x + " | y = " + y);
				
			}
		}
		
		
		System.out.println("Suche nach indirekten Aktionen");
		Vector<Vector<Zuege>> possibleIndirectActions = new Vector<Vector<Zuege>>();
		
		//Finde m?gliche Pfade.
		System.out.println("Suche nach indirekten Zielen");
		//Gehe durch alle Indirekten Ziele durch
		for (Zuege temp : inDirectTargets)
		{
			int x = temp.GibXCoord();
			int y = temp.GibYCoord();
			System.out.println("Suche nach Ziel " + x + "/" + y);
			
			boolean notOkay = true;
			int heat = heatmaps[player].getHeat1(x,y);
			while (notOkay && heat <= heatmaps[player].getHeat1(x,y) +3)
			{
				//Finde alle Pfade zu dem aktuellen Ziel
				Vector<Vector<Zuege>> foundPaths = findPossibleIndirectPaths(x, y, player, heat);
				
				for (Vector<Zuege> temp2 : foundPaths)
				{
					Vector<Zuege> indirectPath = new Vector<Zuege>();
					boolean toContinue = true;
					//Da ich nicht den ganzen Weg gehen kann, nehme ich nur die Strecke die ich auch in einer Runde gehen kann.
					for (Zuege temp3 : temp2)
					{
						
						int x2 = temp3.GibXCoord();
						int y2 = temp3.GibYCoord();
						
						if (feld.getTileType(x2, y2) != 3 && feld.getTileType(x2, y2) != 5 && toContinue)
						{
							indirectPath.add(temp3);
						}
						else if (feld.getTileType(x2, y2) == 3 && toContinue)
						{
							//Ein gegnerisches Bef feld beendet den Weg den ich nehmen kann
							indirectPath.add(temp3);
							toContinue = false;
						}
						else if (feld.getTileType(x2, y2) == 5 && toContinue)
						{
							//Eine Bombe muss 2mal aktiviert werden um dadurch weitere Wege frei zu machen.
							indirectPath.add(temp3);
							indirectPath.add(temp3);
							toContinue = false;
						}
					}
					
					
					int x2 ;
					int y2 ;
					boolean canGoFull = false;
					
					if (zuege < heatmaps[player].getHeat0(indirectPath.lastElement().GibXCoord(), indirectPath.lastElement().GibYCoord()))
					{
						x2 = temp2.get(zuege-1).GibXCoord();
						y2 = temp2.get(zuege-1).GibYCoord();
						
					}
					else 
					{
						x2 = temp2.lastElement().GibXCoord();
						y2 = temp2.lastElement().GibYCoord();
						canGoFull = true;
					}
						
					//Ist das letzte Feld in der Zugfolge ein Feld das ich ver?ndern kann?
					//Wenn ich auf dem Bombenfeld lande, kann ich es auch explodieren?
					if (feld.getTileType(x2, y2) != 1 && feld.getTileType(x2, y2) != 4
							&& (canGoFull || feld.getTileType(x2, y2) != 5))
					{
						//Ich reserviere den letzen Zug von indirectPath f?r das Ziel das es erreichen will
						indirectPath.add(temp2.lastElement());
						possibleIndirectActions.add(indirectPath);
						//Ich habe einen Zug gefunden denn ich nehmen will
						notOkay = false;
						
					}
				}
				
				//Wenn ich keinen guten Weg gefunden hab, suche ich auch Wege die ein bischen l?nger sind.
				heat++;
			}
		}
		
		for (int i = 0; i < possibleIndirectActions.size() ; i++)
		{
			for (Zuege temp : possibleIndirectActions.get(i))
			{
				int x = temp.GibXCoord();
				int y = temp.GibYCoord();
					
				System.out.println("Possible Indirect move: " + i + " | x = " + x + " | y = " + y);
				
			}
		}
		
		
		//Nun w?hle ich das Ziel aus das ich haben will
		
		
		Vector<Vector<Zuege>> finalActions = new Vector<Vector<Zuege>>();
		if (possibleDirectActions.size() > 0)
			finalActions.addAll(possibleDirectActions);
		//Wenn der k?rzeste Pfad den ich habe, l?nger ist als meine Zuganzahl, lohnt es sich auch indirekte Wege anzugucken
		int length = 0;
		for (Vector <Zuege> temp : finalActions)
		{
			if (length == 0 || temp.size()-1 < length)
			{
				length = temp.size()-1;
			}
		}
		
		if (length == 0 || length > zuege)
		{
			finalActions.addAll(possibleIndirectActions);
		}
		
		//W?hle eine Strecke aus die kleiner als meine Zuganzahl ist, gibt es keine Strecke die kleiner ist als meine Zuganzahl, 
		//suche eine Strecke die ich in 2 Z?gen machen, gibt es auch keine, nehme ich eine die ich in 3 z?gen machen kann, usw
		
		boolean targetNotFound = true;
		int multi = 1;
		while (targetNotFound)
		{
			Vector<Vector<Zuege>> possibleChoices = new Vector<Vector<Zuege>>();
			
			
			for (Vector<Zuege> temp : finalActions)
			{
				
				//Direkter Zug
				if (temp.get(  temp.size() - 1  ).GibXCoord() == -1)
				{
					if (temp.size()-1 <= zuege * multi)
					{
						//Er ist gut und ich kann ihn nehmen
						possibleChoices.add(temp);
					}
					
				}
				//Indirekter Zug
				else
				{
					if (heatmaps[player].getHeat1(temp.get(temp.size()-1).GibXCoord(), temp.get(temp.size()-1).GibYCoord()) <= zuege * multi)
					{
						//Er ist gut und ich kann ihn nehmen
						possibleChoices.add(temp);
					}
				}
			}
			
			System.out.println("Zuganzahl gesucht " + (zuege*multi));
			System.out.println(possibleChoices.size() + " Zuege gefunden");
			
			
			multi++;
			//Habe ich m?gliche Z?ge gefunden, w?hle ich zuf?llig einen aus
			if (possibleChoices.size() > 0)
			{
				targetNotFound = false;
				
				
				int z = (int) Math.floor(Math.random()*possibleChoices.size());
				//Ich gehe solange durch die ausgew?hlten Z?ge durch, bis ich keine Zuege mehr habe, oder ich meine erlaubte Zuganzahl ?berschreite
				for (int i = 0; i < possibleChoices.get(z).size()-1 && zuege > 0; i++)
				{
					result.add(possibleChoices.get(z).get(i));
					zuege--;
				}
			}
			//Es kann keine Zuege finden
			else if (multi > Math.sqrt( feld.getWidth()*feld.getHeight()))
			{
				
				int z = (int) Math.floor(Math.random()*4)-1;
				targetNotFound = false;
				//Mach einen "d?mmeren" Zug
				result.addAll(getLevel2Intelligence(player, zuege, z));
				zuege = 0;
			}
		}
		
		//System.out.println(zuege);
		
		//Habe ich noch Z?ge ?brig, finde ich f?r diese auch eine verwendung
		if (zuege > 0)
		{
			for (Zuege temp : result)
			{
				this.quickUpdate(temp.GibXCoord(), temp.GibYCoord(), 1, player);
				
			}
			result.addAll( getLevel3Intelligence(player, zuege));
		}
		
		return result;
	}
	
	
	
	private Vector<Zuege> sortForDistance(int player, Vector<Zuege> toSort, int lowerIndex, int higherIndex)
	{
		int i = lowerIndex;
		int j = higherIndex;
		// calculate pivot number, I am taking pivot as middle index number
		int x = toSort.get(lowerIndex+(higherIndex-lowerIndex)/2).GibXCoord();
		int y = toSort.get(lowerIndex+(higherIndex-lowerIndex)/2).GibYCoord();
		int pivot = heatmaps[player].getHeat0(x, y);
		// Divide into two arrays
		while (i <= j) {
			/**
			 * In each iteration, we will identify a number from left side which 
	         * is greater then the pivot value, and also we will identify a number 
	         * from right side which is less then the pivot value. Once the search 
	         * is done, then we exchange both numbers.
	         */
	        while (heatmaps[player].getHeat0(toSort.get(i).GibXCoord(), toSort.get(i).GibYCoord()) < pivot) {
	            i++;
	        }
	        while (heatmaps[player].getHeat0(toSort.get(j).GibXCoord(), toSort.get(j).GibYCoord()) > pivot) {
	            j--;
	        }
	        if (i <= j) {
	        	Zuege temp = toSort.get(i), temp2 = toSort.get(j);
	        	toSort.set(i, temp2);
	        	toSort.set(j, temp);
	        	
	            //move index to next position on both sides
	            i++;
	            j--;
	        }
	    }
	    // call quickSort() method recursively
	    if (lowerIndex < j)
	    	toSort = sortForDistance(player, toSort, lowerIndex, j);
	    if (i < higherIndex)
	    	toSort = sortForDistance(player, toSort, i, higherIndex);
	    return toSort;
	}
	
	
	private Vector<Zuege> sortForIndirectDistance(int player, Vector<Zuege> toSort, int lowerIndex, int higherIndex)
	{
		int i = lowerIndex;
		int j = higherIndex;
		// calculate pivot number, I am taking pivot as middle index number
		int x = toSort.get(lowerIndex+(higherIndex-lowerIndex)/2).GibXCoord();
		int y = toSort.get(lowerIndex+(higherIndex-lowerIndex)/2).GibYCoord();
		int pivot = heatmaps[player].getHeat1(x, y);
		// Divide into two arrays
		while (i <= j) {
			/**
			 * In each iteration, we will identify a number from left side which 
	         * is greater then the pivot value, and also we will identify a number 
	         * from right side which is less then the pivot value. Once the search 
	         * is done, then we exchange both numbers.
	         */
	        while (heatmaps[player].getHeat1(toSort.get(i).GibXCoord(), toSort.get(i).GibYCoord()) < pivot) {
	            i++;
	        }
	        while (heatmaps[player].getHeat1(toSort.get(j).GibXCoord(), toSort.get(j).GibYCoord()) > pivot) {
	            j--;
	        }
	        if (i <= j) {
	        	Zuege temp = toSort.get(i), temp2 = toSort.get(j);
	        	toSort.set(i, temp2);
	        	toSort.set(j, temp);
	        	
	            //move index to next position on both sides
	            i++;
	            j--;
	        }
	    }
	    // call quickSort() method recursively
	    if (lowerIndex < j)
	    	toSort = sortForIndirectDistance(player, toSort, lowerIndex, j);
	    if (i < higherIndex)
	    	toSort = sortForIndirectDistance(player, toSort, i, higherIndex);
	    return toSort;
	}
	
	
	/**
	 * Birgt den internen Algorithmus f?r die Level 2 Intelligenzen.
	 * Sucht zuerst alle Felder die es mit seinen Zuegen erreichen k?nnte, und w?hlt dann daraus eine Anzahl Z?gen aus.
	 * @param player Der Spieler f?r den Zuege gesucht werden
	 * @param zuege Die Anzahl an Zuegen die er zur Verf?gung hat
	 * @param disposition Random = -1, Aggressiv = 0, Defensiv = 1, Explorative = 2
	 * @return Eine Anzahl von Zuegen
	 */
	private Vector<Zuege> getLevel2Intelligence(int player, int zuege, int disposition)
	{
		Vector<Zuege> result = new Vector<Zuege>();;
		Vector<Zuege> gegnerFelder = new Vector<Zuege>();
		Vector<Zuege> leereFelder = new Vector<Zuege>();
		Vector<Zuege> eigeneFelder = new Vector<Zuege>();
		Vector<Zuege> restFelder = new Vector<Zuege>();
		for (int x = 0; x < feld.getWidth(); x++)
		{
			for (int y = 0; y < feld.getHeight(); y++)
			{
				if (Heatmap.heatIsBetter(heatmaps[player].getHeat0(x,y), zuege+1))// && (feld.getType(x, y) == 3 || feld.getType(x, y) == 2))
				{
					if (feld.getTileType(x, y) == 3 || feld.getTileType(x, y) == 2)
					{
						if (feld.getTileVariation(x, y) != player+1)
						{
							//System.out.println("Fuege Ziel gefunden");
							gegnerFelder.add(new Zuege(player, x, y));
						}
						else if (feld.getTileType(x, y) != 3)
						{
							//System.out.println("Fuege Ziel gefunden");
							eigeneFelder.add(new Zuege(player, x, y));
						}
					}
					
					else if (feld.getTileType(x, y) == 0)
					{
						leereFelder.add(new Zuege(player, x, y));
					}
					else 
					{ 
						restFelder.add(new Zuege(player, x, y));
					}
				}
				
			}
		}
		
		
		double imp = 0.9, sec = 0.5, lst = 0.2, unimp = 0.1, ran = 0.3;
		
		double randomBase = 0;
		double randomGegner = 0;
		double randomLeere = 0;
		double randomEigene = 0;
		double randomRest = 0;
		switch (disposition)
		{
		case 0:
			randomGegner = gegnerFelder.size() > 0 ? imp   : 0;
			randomLeere  = leereFelder.size()  > 0 ? sec   : 0;
			randomEigene = eigeneFelder.size() > 0 ? lst   : 0;
			randomRest   = restFelder.size()   > 0 ? unimp : 0;
			break;
		case 1: 
			randomGegner = gegnerFelder.size() > 0 ? sec   : 0;
			randomLeere  = leereFelder.size()  > 0 ? lst   : 0;
			randomEigene = eigeneFelder.size() > 0 ? imp   : 0;
			randomRest   = restFelder.size()   > 0 ? unimp : 0;
			break;
		case 2: 
			randomGegner = gegnerFelder.size() > 0 ? lst : 0;
			randomLeere  = leereFelder.size()  > 0 ? imp : 0;
			randomEigene = eigeneFelder.size() > 0 ? sec : 0;
			randomRest   = restFelder.size()   > 0 ? unimp : 0;
			break;
		default:
			randomGegner = gegnerFelder.size() > 0 ? ran : 0;
			randomLeere  = leereFelder.size()  > 0 ? ran : 0;
			randomEigene = eigeneFelder.size() > 0 ? ran : 0;
			randomRest = restFelder.size()     > 0 ? ran : 0;
			break;
		}
		
		randomBase = randomBase + randomGegner;
		randomBase = randomBase + randomLeere;
		randomBase = randomBase + randomEigene;
		randomBase = randomBase + randomRest;
		
		double random = Math.random()*randomBase;
			
			
		if (random < randomGegner )//gegnerFelder.size() > 0 )// && result.size() < gegnerFelder.size()*2)//random < randomGegner)
		{
			int x = (int) Math.floor(Math.random()*gegnerFelder.size());
			//System.out.println("Ziel " + i + " gefunden");
			int xC = gegnerFelder.get(x).GibXCoord();
			int yC = gegnerFelder.get(x).GibYCoord();
			Vector<Vector<Zuege>> temp = findPossibleDirectPaths(xC, yC, player, heatmaps[player].getHeat0(xC,  yC));
			int y = (int) Math.floor(temp.size() * Math.random());
			result.addAll(temp.get(y));
			

			System.out.println(  temp.get(y).size() + " Disposition " + disposition +  " Ziele von Spieler " + player+1 + " gefunden, " + (zuege - heatmaps[player].getHeat0(
					gegnerFelder.get(x).GibXCoord(), gegnerFelder.get(x).GibYCoord())) + "fehlen");
			
			
			if (heatmaps[player].getHeat0(gegnerFelder.get(x).GibXCoord(), gegnerFelder.get(x).GibYCoord()) < zuege)
			{
				result.addAll(getLevel2Intelligence(player, zuege - heatmaps[player].getHeat0(
								gegnerFelder.get(x).GibXCoord(), gegnerFelder.get(x).GibYCoord()
								), disposition));
			}
			
		}
		else if (random < randomGegner + randomLeere)//leereFelder.size() > 0)//random < randomGegner + randomLeere)
		{
			int x = (int) Math.floor(Math.random()*leereFelder.size());
			//System.out.println("Ziel " + i + " gefunden");
			int xC = leereFelder.get(x).GibXCoord();
			int yC = leereFelder.get(x).GibYCoord();
			Vector<Vector<Zuege>> temp = findPossibleDirectPaths(xC, yC, player, heatmaps[player].getHeat0(xC,  yC));
			int y = (int) Math.floor(temp.size() * Math.random());
			result.addAll(temp.get(y));
			

			System.out.println(  temp.get(y).size()  + " Disposition " + disposition +  " Ziele von Spieler " + player+1 + " gefunden, " + (zuege - heatmaps[player].getHeat0(
					leereFelder.get(x).GibXCoord(), leereFelder.get(x).GibYCoord())) + "fehlen");
			
			
			if (heatmaps[player].getHeat0(leereFelder.get(x).GibXCoord(), leereFelder.get(x).GibYCoord()) < zuege)
			{
				result.addAll(getLevel2Intelligence(player, zuege - heatmaps[player].getHeat0(
						leereFelder.get(x).GibXCoord(), leereFelder.get(x).GibYCoord())
						, disposition));
			}
			
		}
		else if (random < randomGegner + randomLeere + randomEigene) //if (eigeneFelder.size() > 0)
		{
			
			int x = (int) Math.floor(Math.random()*eigeneFelder.size());
			//System.out.println("Ziel " + i + " gefunden");
			int xC = eigeneFelder.get(x).GibXCoord();
			int yC = eigeneFelder.get(x).GibYCoord();
			Vector<Vector<Zuege>> temp = findPossibleDirectPaths(xC, yC, player, heatmaps[player].getHeat0(xC,  yC));
			int y = (int) Math.floor(temp.size() * Math.random());
			result.addAll(temp.get(y));
			

			System.out.println( temp.get(y).size()  + " Disposition " + disposition +  " Ziele von Spieler " +  player+1 + " gefunden, " + (zuege - heatmaps[player].getHeat0(
					eigeneFelder.get(x).GibXCoord(), eigeneFelder.get(x).GibYCoord())) + "fehlen");
			
			
			
			if (heatmaps[player].getHeat0(eigeneFelder.get(x).GibXCoord(), eigeneFelder.get(x).GibYCoord()) < zuege)
			{
				result.addAll(getLevel2Intelligence(player, zuege - heatmaps[player].getHeat0(
								eigeneFelder.get(x).GibXCoord(), eigeneFelder.get(x).GibYCoord())
								, disposition));
			}
		}
		
		else if (random < randomGegner + randomLeere + randomEigene + randomRest) //if (eigeneFelder.size() > 0)
		{
			
			int x = (int) Math.floor(Math.random()*restFelder.size());
			//System.out.println("Ziel " + i + " gefunden");
			int xC = restFelder.get(x).GibXCoord();
			int yC = restFelder.get(x).GibYCoord();
			Vector<Vector<Zuege>> temp = findPossibleDirectPaths(xC, yC, player, heatmaps[player].getHeat0(xC,  yC));
			int y = (int) Math.floor(temp.size() * Math.random());
			result.addAll(temp.get(y));
			

			System.out.println( temp.get(y).size()  + " Disposition " + disposition +  " Ziele von Spieler " +  player+1 + " gefunden, " + (zuege - heatmaps[player].getHeat0(
					restFelder.get(x).GibXCoord(), restFelder.get(x).GibYCoord())) + "fehlen");
			
			
			
			if (heatmaps[player].getHeat0(restFelder.get(x).GibXCoord(), restFelder.get(x).GibYCoord()) < zuege)
			{
				result.addAll(getLevel2Intelligence(player, zuege - heatmaps[player].getHeat0(
								restFelder.get(x).GibXCoord(), restFelder.get(x).GibYCoord())
								, disposition));
			}
		}
		
		return result;
	}
	
	
	/**
	 * Birgt den internen Algorithmus f?r Random1 
	 * Random1 w?hlt zuf?llig Felder die es in einem Zug erreichen kann
	 * @param player Der Spieler f?r den Zuege gesucht werden
	 * @param zuege Die Anzahl an Zuegen die er zur Verf?gung hat
	 * @return Eine Anzahl von Zuegen
	 */
	
	private Vector<Zuege> getRandom1(int player, int zuege)
	{
		Vector<Zuege> result = new Vector<Zuege>();;
		Vector<Zuege> zugMoeglich = new Vector<Zuege>();
		
		for (int x = 0; x < feld.getWidth(); x++)
		{
			for (int y = 0; y < feld.getHeight(); y++)
			{
				if (heatmaps[player].getHeat0(x,y) == 1)
				{
					zugMoeglich.add(new Zuege(player, x, y));
				}
			}
		}
		
		
		
		for (int i = 0; i < zuege && zugMoeglich.size() > 0; i++)
		{
			int x = (int) Math.floor(Math.random()*zugMoeglich.size());
			result.add(zugMoeglich.get(x));
		}
		
		return result;
	}
	
	/**
	 * Birgt den internen Algorithmus f?r AGGRESIVE1
	 * Aggresive1 w?hlt zuerest ein angrezendes gegnerisches Feld, dann ein leeres, 
	 * und erst wenn es nichts anderes erreichen kann (zb blockierte felder im weg), verteidigt es
	 * @param player Der Spieler f?r den Zuege gesucht werden
	 * @param zuege Die Anzahl an Zuegen die er zur Verf?gung hat
	 * @return
	 */
	private Vector<Zuege> getAggresive1(int player, int zuege)
	{
		Vector<Zuege> result = new Vector<Zuege>();;
		Vector<Zuege> gegnerFelder = new Vector<Zuege>();
		Vector<Zuege> leereFelder = new Vector<Zuege>();
		Vector<Zuege> eigeneFelder = new Vector<Zuege>();
		//System.out.println("Suche aggressive Ziele");
		for (int x = 0; x < feld.getWidth(); x++)
		{
			for (int y = 0; y < feld.getHeight(); y++)
			{
				if (heatmaps[player].getHeat0(x,y) == 1 && (feld.getTileType(x, y) == 3 || feld.getTileType(x, y) == 2))
				{
					if (feld.getTileVariation(x, y) != player+1)
					{
						//System.out.println("Fuege Ziel gefunden");
						gegnerFelder.add(new Zuege(player, x, y));
					}
					else if (feld.getTileType(x, y) != 3)
					{
						//System.out.println("Fuege Ziel gefunden");
						eigeneFelder.add(new Zuege(player, x, y));
					}
				}
				else if (heatmaps[player].getHeat0(x,y) == 1 && feld.getTileType(x, y) == 0 )
				{
					//System.out.println("Fuege Ziel gefunden");
					leereFelder.add(new Zuege(player, x, y));
				}
			}
		}
		
		
		
		for (int i = 0; i < zuege; i++)
		{
			/*
			double randomBase = 0;
			double randomGegner = gegnerFelder.size() > 0 ? 0.5 : 0;
			double randomLeere  = leereFelder.size() > 0 ? 0.3 : 0;
			double randomEigene = eigeneFelder.size() > 0 ? 0.2 : 0;
			randomBase = randomBase + randomGegner;
			randomBase = randomBase + randomLeere;
			randomBase = randomBase + randomEigene;
			
			double random = Math.random()*randomBase;
			*/
			
			if (gegnerFelder.size() > 0 )// && result.size() < gegnerFelder.size()*2)//random < randomGegner)
			{
				int x = (int) Math.floor(Math.random()*gegnerFelder.size());
				//System.out.println("Ziel " + i + " gefunden");
				result.add(gegnerFelder.get(x));
				
			}
			else if (leereFelder.size() > 0)//random < randomGegner + randomLeere)
			{
				int x = (int) Math.floor(Math.random()*leereFelder.size());
				//System.out.println("Ziel " + i + " gefunden");
				result.add(leereFelder.get(x));
				
			}
			else if (eigeneFelder.size() > 0)
			{
				int x = (int) Math.floor(Math.random()*eigeneFelder.size());
				//System.out.println("Ziel " + i + " gefunden");
				result.add(eigeneFelder.get(x));
			}
			
		}
		
		return result;
	}
	
	/**
	 * Birgt den internen Algorithmus f?r DEFENSIVE1
	 * Defensive1 w?hlt zuerset ein eigenes Feld, sind alle Befestigt dann gegnerische, und zuletzt leere.
	 * @param player Der Spieler f?r den Zuege gesucht werden
	 * @param zuege Die Anzahl an Zuegen die er zur Verf?gung hat
	 * @return
	 */
	private Vector<Zuege> getDefensive1(int player, int zuege)
	{
		
		Vector<Zuege> result = new Vector<Zuege>();;
		Vector<Zuege> gegnerFelder = new Vector<Zuege>();
		Vector<Zuege> leereFelder = new Vector<Zuege>();
		Vector<Zuege> eigeneFelder = new Vector<Zuege>();
		
		for (int x = 0; x < feld.getWidth(); x++)
		{
			for (int y = 0; y < feld.getHeight(); y++)
			{
				if (heatmaps[player].getHeat0(x,y) == 1 && (feld.getTileType(x, y) == 3 || feld.getTileType(x, y) == 2))
				{
					if (feld.getTileVariation(x, y) != player+1)
					{
						gegnerFelder.add(new Zuege(player, x, y));
					}
					else if (feld.getTileType(x, y) != 3)
					{
						eigeneFelder.add(new Zuege(player, x, y));
					}
				}
				else if (heatmaps[player].getHeat0(x,y) == 1 && feld.getTileType(x, y) == 0 )
				{
					leereFelder.add(new Zuege(player, x, y));
				}
			}
		}
		
		
		
		for (int i = 0; i < zuege; i++)
		{
			/*
			double randomBase = 0;
			double randomEigene = eigeneFelder.size() > 0 ? 0.5 : 0;
			double randomGegner = gegnerFelder.size() > 0 ? 0.3 : 0;
			double randomLeere  = leereFelder.size() > 0 ? 0.2 : 0;
			randomBase = randomBase + randomEigene;
			randomBase = randomBase + randomGegner;
			randomBase = randomBase + randomLeere;
			
			double random = Math.random()*randomBase;
			*/
			if (eigeneFelder.size() > 0)//random < randomEigene)
			{
				int x = (int) Math.floor(Math.random()*eigeneFelder.size());
				result.add(eigeneFelder.get(x));
			}
			else if (gegnerFelder.size() > 0)//random < randomEigene+randomGegner)
			{
				int x = (int) Math.floor(Math.random()*gegnerFelder.size());
				result.add(gegnerFelder.get(x));
			}
			else if (leereFelder.size() > 0)
			{
				int x = (int) Math.floor(Math.random()*leereFelder.size());
				result.add(leereFelder.get(x));
			}
			
			
		}
		
		return result;
	}
	
	
	/**
	 * Birgt den internen Algorithmus f?r EXPLORATIVE1
	 * Explorative1 w?hlt zuerst ein leeres Feld, dann sich selbst und dann Gegner.
	 * @param player Der Spieler f?r den Zuege gesucht werden
	 * @param zuege Die Anzahl an Zuegen die er zur Verf?gung hat
	 * @return
	 */
	private Vector<Zuege> getExplorative1(int player, int zuege)
	{
		Vector<Zuege> result = new Vector<Zuege>();;
		Vector<Zuege> gegnerFelder = new Vector<Zuege>();
		Vector<Zuege> leereFelder = new Vector<Zuege>();
		Vector<Zuege> eigeneFelder = new Vector<Zuege>();
		
		for (int x = 0; x < feld.getWidth(); x++)
		{
			for (int y = 0; y < feld.getHeight(); y++)
			{
				if (heatmaps[player].getHeat0(x,y) == 1 && (feld.getTileType(x, y) == 3 || feld.getTileType(x, y) == 2))
				{
					if (feld.getTileVariation(x, y) != player+1)
					{
						gegnerFelder.add(new Zuege(player, x, y));
					}
					else if (feld.getTileType(x, y) != 3)
					{
						eigeneFelder.add(new Zuege(player, x, y));
					}
				}
				else if (heatmaps[player].getHeat0(x,y) == 1 && feld.getTileType(x, y) == 0 )
				{
					leereFelder.add(new Zuege(player, x, y));
				}
			}
		}
		
		
		
		for (int i = 0; i < zuege; i++)
		{
			/*
			double randomBase = 0;
			double randomLeere  = leereFelder.size() > 0 ? 0.5 : 0;
			double randomEigene = eigeneFelder.size() > 0 ? 0.3 : 0;
			double randomGegner = gegnerFelder.size() > 0 ? 0.2 : 0;

			randomBase = randomBase + randomLeere;
			randomBase = randomBase + randomEigene;
			randomBase = randomBase + randomGegner;
			
			double random = Math.random()*randomBase;
			*/
			if (leereFelder.size() > 0)//random < randomLeere)
			{
				int x = (int) Math.floor(Math.random()*leereFelder.size());
				result.add(leereFelder.get(x));
			}
			else if (eigeneFelder.size() > 0)//random < randomLeere + randomEigene)
			{
				int x = (int) Math.floor(Math.random()*eigeneFelder.size());
				result.add(eigeneFelder.get(x));
			}
			else if (gegnerFelder.size() > 0)
			{
				int x = (int) Math.floor(Math.random()*gegnerFelder.size());
				result.add(gegnerFelder.get(x));
			}
			
		}
		
		return result;
	}
	
	/**
	 * Findet alle Felder, die an Kernfelder angrenzen.
	 * 
	 * @return Eine Liste von Feldern, die an Kernfelder angrenzen.
	 */
	private Vector<Zuege> findCoreFields()
	{
		Vector<Zuege> result = new Vector<Zuege>();
		
		for (int i = 0; i < feld.getWidth(); i++)
		{
			for (int j = 0; j < feld.getHeight(); j++)
			{
				if ((feld.getTileType(i+1, j) == 7 || feld.getTileType(i-1, j) == 7 ||  feld.getTileType(i, j+1) == 7 || feld.getTileType(i, j-1) == 7)
						&&(feld.getTileType(i, j) == 0 || feld.getTileType(i, j) == 2 || feld.getTileType(i, j) == 3))
				{
					result.add(new Zuege(i, j));
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Sucht alle m?glichen Pfade zum Feld mit den Zielkoordinaten x und y die eine L?nge von maxDistance nicht ?berschreiten.
	 * Ist maxDistance = der Heatwert des Feldes, findet es die k?rzesten Strecken.
	 * 
	 * @param x xKoordinate des angezielten Feldes
	 * @param y yKoordinate des angezielten Feldes
	 * @param player Der Spieler f?r den Zuege gesucht werden
	 * @param maxDistance Die maximale Distanz die die Pfade haben k?nnen.
	 * @return Eine Liste von m?glichen Pfaden die genohmen werden k?nnen
	 */
	private Vector<Vector<Zuege>> findPossibleDirectPaths(int x, int y, int player, int maxDistance)
	{
		Vector<Vector<Zuege>> result = new Vector<Vector<Zuege>>();
		if (heatmaps[player].getHeat0(x, y)== 1)
		{
			result.add(new Vector<Zuege>());
			result.get(0).add(new Zuege(player+1, x, y));
			return result;
		}
		for (int k = -1; k < 2; k +=2 )
		{
			for (int j = 0; j < 2; j++)
			{
				int oX = x+(k*j);
				int oY = y+(k*(1-j));
				
				
				if (Heatmap.heatIsBetter(heatmaps[player].getHeat0(oX, oY), maxDistance)
						&& feld.getTileType(oX, oY) != 3 && feld.getTileType(oX, oY) != 7 && feld.getTileType(oX, oY) != 5)
				{
					Vector<Vector<Zuege>> temp = findPossibleDirectPaths(oX, oY, player, maxDistance-1);
					for (Vector<Zuege> temp2: temp)
					{
						temp2.add( new Zuege(player+1, x, y));
					}
					result.addAll(temp);
				}
			}
		}
		
		// Bin ich ein Teleporter Feld
		if (feld.getTileType(x, y) == 4)
		{
			Vector<Zuege> teleporters = findTeleporters(feld.getTileVariation(x, y));
			
			for (Zuege zug: teleporters)
			{
				for (int k = -1; k < 2; k +=2 )
				{
					for (int l = 0; l < 2; l++)
					{
						int oX = zug.GibXCoord()+(k*l);
						int oY = zug.GibYCoord()+(k*(1-l));
						if (Heatmap.heatIsBetter(heatmaps[player].getHeat0(oX, oY), maxDistance)
								&& feld.getTileType(oX, oY) != 3 && feld.getTileType(oX, oY) != 7 && feld.getTileType(oX, oY) != 5)
						{
							Vector<Vector<Zuege>> temp = findPossibleDirectPaths(oX, oY, player, maxDistance-1);
							for (Vector<Zuege> temp2: temp)
							{
								temp2.add( new Zuege(player+1, zug.GibXCoord(), zug.GibYCoord()));
							}
							result.addAll(temp);
						}
					}
				}
			}
		}
		
		for (Vector<Zuege> temp2: result)
		{
			int xC = temp2.get(0).GibXCoord();
			int yC = temp2.get(0).GibYCoord();
			if (heatmaps[player].getHeat0(xC, yC) != 1)
			{
				result.remove(temp2);
			}
		}
		
		return result;
	}
	
	/**
	 * Findet alle Teleporter Felder mit dem Wert x.
	 * 
	 * @param x Wert des Teleporter Feldes.
	 * @return
	 */
	
	private Vector<Zuege> findTeleporters(int x)
	{
		Vector<Zuege> result = new Vector<Zuege>();
		for (int i = 0; i < feld.getWidth(); i++)
		{
			for (int j = 0; j < feld.getHeight(); j++)
			{
				
				if (feld.getTileType(i, j) == 4 && feld.getTileVariation(i, j) == x)
				{
					result.add(new Zuege(i, j));
				}
			}
		}
		return result;
	}
	
	/**
	 * Sucht alle m?glichen indirekten Pfade zum Feld mit den Zielkoordinaten x und y  und der maximalen Distanz maxDistance.
	 * 
	 * @param x xKoordinate des angezielten Feldes
	 * @param y yKoordinate des angezielten Feldes
	 * @param player Der Spieler f?r den Zuege gesucht werden
	 * @param maxDistance Die maximale L?nge eines Pfades.
	 * @return Eine Liste von m?glichen Pfaden zum Ziel.
	 */
	private Vector<Vector<Zuege>> findPossibleIndirectPaths(int x, int y, int player, int maxDistance)
	{
		Vector<Vector<Zuege>> result = new Vector<Vector<Zuege>>();
		if (heatmaps[player].getHeat0(x, y)== 1)
		{
			result.add(new Vector<Zuege>());
			result.get(0).add(new Zuege(player, x, y));
			return result;
		}
		for (int k = -1; k < 2; k +=2 )
		{
			for (int j = 0; j < 2; j++)
			{
				int oX = x+(k*j);
				int oY = y+(k*(1-j));
				
				
				if (Heatmap.heatIsBetter(heatmaps[player].getHeat0(oX, oY), maxDistance)
						&& feld.getTileType(oX, oY) != 7)
				{
					Vector<Vector<Zuege>> temp = findPossibleDirectPaths(oX, oY, player, maxDistance-1);
					for (Vector<Zuege> temp2: temp)
					{
						temp2.add(new Zuege(player, x, y));
					}
					result.addAll(temp);
				}
				else if (Heatmap.heatIsBetter(heatmaps[player].getHeat1(oX, oY), maxDistance)
						&& feld.getTileType(oX, oY) != 7 && feld.getTileType(oX, oY) != 5)
				{
					Vector<Vector<Zuege>> temp = findPossibleIndirectPaths(oX, oY, player, maxDistance-1);
					for (Vector<Zuege> temp2: temp)
					{
						temp2.add( new Zuege(player, x, y));
					}
					result.addAll(temp);
				}
			}
		}
		
		// Bin ich ein Teleporter Feld
		if (feld.getTileType(x, y) == 4)
		{
			Vector<Zuege> teleporters = findTeleporters(feld.getTileVariation(x, y));
			
			for (Zuege zug: teleporters)
			{
				for (int k = -1; k < 2; k +=2 )
				{
					for (int l = 0; l < 2; l++)
					{
						int oX = zug.GibXCoord()+(k*l);
						int oY = zug.GibYCoord()+(k*(1-l));


						if (Heatmap.heatIsBetter(heatmaps[player].getHeat0(oX, oY), maxDistance)
								&& feld.getTileType(oX, oY) != 7 && feld.getTileType(oX, oY) != 5)
						{
							Vector<Vector<Zuege>> temp = findPossibleDirectPaths(oX, oY, player, maxDistance-1);
							for (Vector<Zuege> temp2: temp)
							{
								temp2.add( new Zuege(player, zug.GibXCoord(), zug.GibYCoord()));
							}
							result.addAll(temp);
						}
						else if (Heatmap.heatIsBetter(heatmaps[player].getHeat1(oX, oY), maxDistance-1)
								&& feld.getTileType(oX, oY) != 7 && feld.getTileType(oX, oY) != 5)
						{
							Vector<Vector<Zuege>> temp = findPossibleIndirectPaths(oX, oY, player, maxDistance-1);
							for (Vector<Zuege> temp2: temp)
							{
								temp2.add( new Zuege(player, zug.GibXCoord(), zug.GibYCoord()));
							}
							result.addAll(temp);
						}
					}
				}
			}
		}
		
		for (Vector<Zuege> temp2: result)
		{
			int xC = temp2.get(0).GibXCoord();
			int yC = temp2.get(0).GibYCoord();
			if (heatmaps[player].getHeat0(xC, yC) != 1)
			{
				result.remove(temp2);
			}
		}
		
		return result;
	}
	
	public void setIntelligence(Intelligence level, int player)
	{
		intelligence[player] = level;
		
	}
	
	public Intelligence getIntelligence(int player)
	{
		return intelligence[player];
		
	}
	
}
