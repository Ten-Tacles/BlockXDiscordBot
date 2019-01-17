package de.ten.tacles.blockx.DiscordLogic;

import java.io.*;
import java.util.ArrayList;

import de.ten.tacles.blockx.Spielfeld;
import javafx.scene.paint.Color;


public class MapReader
{


    public Spielfeld KartenLeser(int KartenNummer)
    {
        int height;
        int Breite;
        int Spieler;
        Spielfeld spielfeld;
        try {
        	
        	
            File file = new File("Maperingos123.txt");
            BufferedReader Leser;
            if (!(file.exists() )){
                InputStream is = getClass().getResourceAsStream("/Maperingos123.txt");
                InputStreamReader isr = new InputStreamReader(is);
                Leser = new BufferedReader(isr);
            }
            else {
                Leser = new BufferedReader(new FileReader(file));
            }
            String Test = null;
            Test = Leser.readLine();
            while (!(Test.contains("[" + (KartenNummer) + "]")))
            {
                Test = null;
                Test = Leser.readLine();
                if (Test == null) break;
            }
            Test = Leser.readLine();
            String[] in = Test.split("/");
            Breite = Integer.parseInt(in[0]);
            height = Integer.parseInt(in[1]);
            Spieler = Integer.parseInt(in[2]);
            Test = Leser.readLine();
            in = Test.split("/");
            Color farben[] = new Color[in.length];
            for (int i = 0; i < in.length; i++)
            {
            	farben[i] = Color.hsb(Integer.parseInt(in[i]), 0.9, 0.9);
            }
            
            spielfeld = new Spielfeld(Breite, height, Spieler, farben, null);
            for (int i = 0; i < height; i++)
            {
                Test = null;
                Test = Leser.readLine();
                String[] ex = Test.split(";");
                for (int j = 0; j < Breite; j++)
                {
                    String helfer = ex[j];
                    String[] helferString = helfer.split(",");
                    if (helferString.length >2)
                    spielfeld.setTileType(j, i, Integer.parseInt(helferString[0]),Integer.parseInt(helferString[1]),Integer.parseInt(helferString[2]));
                    else 
                    spielfeld.setTileType(j, i, Integer.parseInt(helferString[0]),Integer.parseInt(helferString[1]));
                }
                
            }

            
            Leser.close();
            return spielfeld;
        } catch( IOException ex ) {
            System.out.println("Error while reading file!");
        }
        return null;
    }

    public Spielfeld KartenLeser2(String mapName) throws MapReadException
    {

        Spielfeld spielfeld;
        try {
            //Grabbing File
            File file = new File("Maperingos123.txt");
            BufferedReader reader;
            if (!(file.exists() )){
                InputStream is = getClass().getResourceAsStream("/Maperingos123.txt");
                InputStreamReader isr = new InputStreamReader(is);
                reader = new BufferedReader(isr);
            }
            else {
                reader = new BufferedReader(new FileReader(file));
            }
            String currentLine;
            currentLine = reader.readLine();
            //Find map
            while (!(currentLine.equals("[" + (mapName) + "]{")))
            {
                currentLine = reader.readLine();
                if (currentLine == null) throw new MapReadException(MapReadProblem.NOT_FOUND);
            }

            //Beginn reading it
            int height = 0;
            int width = 0;
            int playerCount = 0;
            ArrayList<String> rows = new ArrayList<>();
            Color[] colours = null;
            //Read all lines
            currentLine = reader.readLine();
            while(!currentLine.equals("}"))
            {
                //Store them
                if (currentLine.startsWith("Row:"))
                {
                    String[] temp = currentLine.split(":");
                    rows.add(Integer.parseInt(temp[1]), temp[2]);
                }
                else if (currentLine.startsWith("Colours:"))
                {
                    String[] temp = currentLine.split(":")[1].split("/");
                    colours = new Color[temp.length];
                    for (int i = 0; i < colours.length;i++)
                    {
                        colours[i] = Color.hsb(Integer.parseInt(temp[i]), 0.9, 0.9);
                    }

                }
                else if (currentLine.startsWith("Stats"))
                {
                    String[] temp = currentLine.split(":")[1].split("/");
                    height   = Integer.parseInt(temp[1]);
                    width  = Integer.parseInt(temp[0]);
                    playerCount = Integer.parseInt(temp[2]);
                }
                else
                    //I got something I don't understand
                System.out.println("Got a line I don't understand: " + currentLine);
                currentLine = reader.readLine();
            }

            //Did I get enough data for a proper map?
            if (height <= 3 || width <= 3 )
                throw new MapReadException(MapReadProblem.SIZE_TOO_SMALL_OR_MISSING);
            if (playerCount < 2)
                throw new MapReadException(MapReadProblem.NOT_ENOUGH_PLAYERS);
            if (rows.size() < height)
                throw new MapReadException(MapReadProblem.NOT_ENOUGH_ROWS);


            //Probably
            spielfeld = new Spielfeld(width, height, playerCount, colours, null);
            for (int i = 0; i < height; i++)
            {
                if (rows.get(i) == null)
                    throw new MapReadException(MapReadProblem.NOT_ENOUGH_ROWS);
                String[] row = rows.get(i).split(";");
                if (row.length < width)
                    throw new MapReadException(MapReadProblem.NOT_ENOUGH_COLLUMNS);
                for (int j = 0; j < width; j++)
                {
                    String helfer = row[j];
                    String[] helferString = helfer.split(",");
                    if (helferString.length >2)
                        spielfeld.setTileType(j, i, Integer.parseInt(helferString[0]),Integer.parseInt(helferString[1]),Integer.parseInt(helferString[2]));
                    else
                        spielfeld.setTileType(j, i, Integer.parseInt(helferString[0]),Integer.parseInt(helferString[1]));
                }
            }
            reader.close();
            return spielfeld;
        } catch( IOException ex ) {
            System.out.println("Error while reading file!");
            System.out.println(ex.getMessage());
        }
        return null;
    }

   
}
