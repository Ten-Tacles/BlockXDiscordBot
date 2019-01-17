package de.ten.tacles.blockx.KI;

public enum Intelligence 
{
	RANDOM1, AGGRESIVE1, DEFENSIVE1, EXPLORATIVE1,
	RANDOM2, AGGRESIVE2, DEFENSIVE2, EXPLORATIVE2,
	LEVEL3; 
	
	
	
	
	// Random1 w�hlt zuf�llig Felder die es in einem Zug erreichen kann
	//Aggresive1 w�hlt zuerest ein angrezendes gegnerisches Feld, dann ein leeres, 
	//und erst wenn es nichts anderes erreichen kann (zb blockierte felder im weg), verteidigt es
	//Defensive1 w�hlt zuerset ein eigenes Feld, sind alle Befestigt, dann gegnerische, und zuletzt leere.
	//Explorative1 w�hlt zuerst ein leeres Feld, dann sich selbst und dann Gegner.
	
	//Aggressive2 w�hlt ein zuf�lliges Feld das es mit all seinen Z�gen (oder einem Teil) erreichen kann, wobei es lieber Gegner angreift, 
	//dann neutral und am seltesten seine eigenen Felder.
	//Defensive2 und Explorative2 verhalten sich �hnlich Aggressive2, haben aber andere Priorit�ten.
}
