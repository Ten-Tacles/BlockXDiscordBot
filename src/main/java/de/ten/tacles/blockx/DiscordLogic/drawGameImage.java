package de.ten.tacles.blockx.DiscordLogic;

import de.ten.tacles.blockx.HauptSpiel;
import de.ten.tacles.blockx.Spielfeld;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class drawGameImage
{

	static drawGameImage active;
	private  BufferedImage leer;
	private  BufferedImage leerSpieler[];

	private  BufferedImage[] wandMitte;
	private  BufferedImage[] wandKante;
	private  BufferedImage[] wandEckeInnen;
    private  BufferedImage[] wandEckeAussen;

    private  BufferedImage kern;
	private  BufferedImage kernZer;
	private  BufferedImage kernGelad;
	private  BufferedImage[] blocked;

	private  BufferedImage[] spielerEcken; //JJ, JN, NJ, NN
	private  BufferedImage[] spielerSeiten; //J, N

	private  BufferedImage[] befSpielerEcken; //JJ, JN, NJ, NN
	private  BufferedImage[] befSpielerSeiten; //J, N

	private  BufferedImage[] bomb;
	private  BufferedImage[] teleporter;

	private  BufferedImage mauer;

//	private  BufferedImage[] zug;
//	private  BufferedImage zugDone;

	public drawGameImage() throws IOException {
	    System.out.println("Create drawGameImage object.");

		active = this;
		leer = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/LeeresFeld.png"));

		leerSpieler = new BufferedImage[12];
		for (int i = 1; i < 13; i++)
		{
			leerSpieler[i-1] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/HalberSpieler" + i + ".png"));
		}

		kern = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/KernFeld.png"));
		kernZer = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/KernFeldZerstoert.png"));
		kernGelad = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/KernFeldGeladen.png"));

		blocked = new BufferedImage[4];
		blocked[0] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/Schlupffeld.png"));
		for (int i = 1; i < 4; i++)
		{
			blocked[i] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/BlockiertesFeld" + i + ".png"));
		}
		spielerEcken = new BufferedImage[4];
		spielerEcken[0] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/SpielerGrafik/SpielerFeldEckeJaJa.png"));
		spielerEcken[1] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/SpielerGrafik/SpielerFeldEckeJaNein.png"));
		spielerEcken[2] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/SpielerGrafik/SpielerFeldEckeNeinJa.png"));
		spielerEcken[3] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/SpielerGrafik/SpielerFeldEckeNeinNein.png"));

		spielerSeiten = new BufferedImage[2];
		spielerSeiten[0] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/SpielerGrafik/SpielerFeldMitteJa.png"));
		spielerSeiten[1] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/SpielerGrafik/SpielerFeldMitteNein.png"));


		befSpielerEcken = new BufferedImage[4];
		befSpielerEcken[0] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/SpielerGrafik/BefSpielerFeldEckeJaJa.png"));
		befSpielerEcken[1] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/SpielerGrafik/BefSpielerFeldEckeJaNein.png"));
		befSpielerEcken[2] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/SpielerGrafik/BefSpielerFeldEckeNeinJa.png"));
		befSpielerEcken[3] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/SpielerGrafik/BefSpielerFeldEckeNeinNein.png"));

		befSpielerSeiten = new BufferedImage[2];
		befSpielerSeiten[0] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/SpielerGrafik/BefSpielerFeldMitteJa.png"));
		befSpielerSeiten[1] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/SpielerGrafik/BefSpielerFeldMitteNein.png"));

		bomb = new BufferedImage[3];
		bomb[0] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/Bomb Population.png"));
		bomb[1] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/Bomb Destruction.png"));
		bomb[2] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/Bomb Void.png"));

		teleporter = new BufferedImage[9];
		for (int i = 1; i < 10; i++)
		{
			teleporter[i-1] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/Teleporter" + i + ".png"));
		}

		mauer  = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/Mauer.png"));



		int waende = 3;
        wandMitte = new BufferedImage[waende];
        wandKante = new BufferedImage[waende];
        wandEckeAussen = new BufferedImage[waende];
        wandEckeInnen = new BufferedImage[waende];

		for (int i = 0; i < waende; i++)
        {
            wandMitte[i]      = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/WandGrafik/WandFeld"+i+"Mitte.png"));
            wandKante[i]      = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/WandGrafik/WandFeld"+i+"Kante.png"));
            wandEckeAussen[i] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/WandGrafik/WandFeld"+i+"Aussen.png"));
            wandEckeInnen[i]  = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/WandGrafik/WandFeld"+i+"Innen.png"));
        }

	//	zug = new BufferedImage[2];
	//	zug[0] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/SpielerZugMoeglich1.png"));
	//	zug[1] = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/SpielerZugMoeglich2.png"));
	//	zugDone = ImageIO.read(getClass().getResourceAsStream("/BlockGrafiken/SpielerZugGemacht.png"));

		
	}



    /**
     * Draws an image on a graphics context.
     * Source: http://stackoverflow.com/questions/18260421/how-to-draw-image-rotated-on-javafx-canvas#18262938
     *
     * The image is drawn at (tlpx, tlpy) rotated by angle pivoted around the point:
     *   (tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2)
     *
     * @param gc the graphics context the image is to be drawn on.
     * @param angle the angle of rotation.
     * @param tlpx the top left x co-ordinate where the image will be plotted (in canvas co-ordinates).
     * @param tlpy the top left y co-ordinate where the image will be plotted (in canvas co-ordinates).
     */
    private void drawRotatedImage(Graphics2D gc, BufferedImage image, double angle, int tlpx, int tlpy)
    {
        double rotationRequired = Math.toRadians (angle);
        double locationX = image.getWidth() / 2.0;
        double locationY = image.getHeight() / 2.0;
        AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

    // Drawing the rotated image at the required drawing locations
        gc.drawImage(op.filter(image, null), tlpx, tlpy, null);


    }

    /**
     * Draws the supplied game into a BufferedImage, and returns it.
     * It will return an image slightly larger than the game would suggest, as it
     * adds 2 rows and collumns to make it look nicer.
     *
     * @param hauptSpiel The game
     * @return An image representing the current state of the game.
     */
     BufferedImage drawView(HauptSpiel hauptSpiel) {

        Spielfeld feld = hauptSpiel.getSpielFeld();
        int fX = feld.getWidth()+2;
        int fY = feld.getHeight()+2;

        BufferedImage toReturn = new BufferedImage(fX*32, fY*32, BufferedImage.TYPE_INT_RGB);
        //Canvas canvas = new Canvas(fX * 32, fY * 32);


        int typ, var;


        Graphics2D gc = toReturn.createGraphics();

        gc.setPaint(new Color(0.5f, 0.5f, 0.5f));
        gc.fillRect(32, 32, toReturn.getWidth(), toReturn.getHeight());


        for (int i = -1; i < fX; i++) {
            for (int j = -1; j < fY; j++) {


                typ = feld.getTileType(i, j);
                var = feld.getTileVariation(i, j);

                //Linke obere Ecke dieses Feldes auf dem Canvas
                int x = ((i+1) * 32);
                int y = ((j+1) * 32);

               // AffineTransform transform = new AffineTransform(1,1,1,1,i*32, j*32);

                boolean top, bottom, left, right;
                //Boolean topL, topR, botL, botR;


                switch (typ) {
                    case 0://Leerefelder
                        if (var == 0)
                            gc.drawImage(leer, null, x, y);
                        else
                            gc.drawImage(leerSpieler[var - 1], null, x, y);
                        break;
                    case 1://Blockierte Felder
                        gc.drawImage(blocked[var], null, x, y);
                        break;
                    case 2://Spielerfelder
                        //gc.drawImage(spieler[var], x, y);

                        gc.setPaint(new Color((float)feld.getFarbe(var).getRed(), (float) feld.getFarbe(var).getGreen(), (float) feld.getFarbe(var).getBlue()));
                        gc.fillRect(x, y, 32, 32);

                        //Boolean top, bottom, left, right;
                        top = (feld.getTileType(i, j - 1) == typ && feld.getTileVariation(i, j - 1) == var);
                        bottom = (feld.getTileType(i, j + 1) == typ && feld.getTileVariation(i, j + 1) == var);
                        left = (feld.getTileType(i - 1, j) == typ && feld.getTileVariation(i - 1, j) == var);
                        right = (feld.getTileType(i + 1, j) == typ && feld.getTileVariation(i + 1, j) == var);

                        //Obere Mitte
                        if (!top)
                            gc.drawImage(spielerSeiten[0], null, x, y);
                        else
                            gc.drawImage(spielerSeiten[1], null, x, y);
                        //Untere Mitte
                        if (!bottom)
                            drawRotatedImage(gc, spielerSeiten[0], 180, x, y);
                        else
                            drawRotatedImage(gc, spielerSeiten[1], 180, x, y);
                        //Linke Mitte
                        if (!left)
                            drawRotatedImage(gc, spielerSeiten[0], 270, x, y);
                        else
                            drawRotatedImage(gc, spielerSeiten[1], 270, x, y);
                        //Rechte Mitte
                        if (!right)
                            drawRotatedImage(gc, spielerSeiten[0], 90, x, y);
                        else
                            drawRotatedImage(gc, spielerSeiten[1], 90, x, y);


                        //Oben Links
                        if (!top) {
                            if (!left)
                                gc.drawImage(spielerEcken[0], null, x, y);
                            else
                                gc.drawImage(spielerEcken[2], null, x, y);
                        } else if (!left)
                            gc.drawImage(spielerEcken[1], null, x, y);
                        else
                            gc.drawImage(spielerEcken[3], null, x, y);

                        //Oben Rechts
                        if (!right) {
                            if (!top)
                                drawRotatedImage(gc, spielerEcken[0], 90, x, y);
                            else
                                drawRotatedImage(gc, spielerEcken[2], 90, x, y);
                        } else if (!top)
                            drawRotatedImage(gc, spielerEcken[1], 90, x, y);
                        else
                            drawRotatedImage(gc, spielerEcken[3], 90, x, y);

                        //Unten Rechts
                        if (!bottom) {
                            if (!right)
                                drawRotatedImage(gc, spielerEcken[0], 180, x, y);
                            else
                                drawRotatedImage(gc, spielerEcken[2], 180, x, y);
                        } else if (!right)
                            drawRotatedImage(gc, spielerEcken[1], 180, x, y);
                        else
                            drawRotatedImage(gc, spielerEcken[3], 180, x, y);

                        //Unten Links
                        if (!left) {
                            if (!bottom)
                                drawRotatedImage(gc, spielerEcken[0], 270, x, y);
                            else
                                drawRotatedImage(gc, spielerEcken[2], 270, x, y);
                        } else if (!bottom)
                            drawRotatedImage(gc, spielerEcken[1], 270, x, y);
                        else
                            drawRotatedImage(gc, spielerEcken[3], 270, x, y);

                        break;
                    case 3://Befestigte Spielerfelder
                        gc.setPaint(new Color((float)feld.getFarbe(var).getRed(),(float)feld.getFarbe(var).getGreen(), (float)feld.getFarbe(var).getBlue()));
                        gc.fillRect(x, y, 32, 32);

                        //Boolean top, bottom, left, right;
                        top = (feld.getTileType(i, j - 1) == typ && feld.getTileVariation(i, j - 1) == var);
                        bottom = (feld.getTileType(i, j + 1) == typ && feld.getTileVariation(i, j + 1) == var);
                        left = (feld.getTileType(i - 1, j) == typ && feld.getTileVariation(i - 1, j) == var);
                        right = (feld.getTileType(i + 1, j) == typ && feld.getTileVariation(i + 1, j) == var);

                        //Obere Mitte
                        if (!top)
                            gc.drawImage(befSpielerSeiten[0], null, x, y);
                        else
                            gc.drawImage(befSpielerSeiten[1], null, x, y);
                        //Untere Mitte
                        if (!bottom)
                            drawRotatedImage(gc, befSpielerSeiten[0], 180, x, y);
                        else
                            drawRotatedImage(gc, befSpielerSeiten[1], 180, x, y);
                        //Linke Mitte
                        if (!left)
                            drawRotatedImage(gc, befSpielerSeiten[0], 270, x, y);
                        else
                            drawRotatedImage(gc, befSpielerSeiten[1], 270, x, y);
                        //Rechte Mitte
                        if (!right)
                            drawRotatedImage(gc, befSpielerSeiten[0], 90, x, y);
                        else
                            drawRotatedImage(gc, befSpielerSeiten[1], 90, x, y);


                        //Oben Links
                        if (!top) {
                            if (!left)
                                gc.drawImage(befSpielerEcken[0], null, x, y);
                            else
                                gc.drawImage(befSpielerEcken[2], null, x, y);
                        } else if (!left)
                            gc.drawImage(befSpielerEcken[1], null, x, y);
                        else
                            gc.drawImage(befSpielerEcken[3], null, x, y);

                        //Oben Rechts
                        if (!right) {
                            if (!top)
                                drawRotatedImage(gc, befSpielerEcken[0], 90, x, y);
                            else
                                drawRotatedImage(gc, befSpielerEcken[2], 90, x, y);
                        } else if (!top)
                            drawRotatedImage(gc, befSpielerEcken[1], 90, x, y);
                        else
                            drawRotatedImage(gc, befSpielerEcken[3], 90, x, y);

                        //Unten Rechts
                        if (!bottom) {
                            if (!right)
                                drawRotatedImage(gc, befSpielerEcken[0], 180, x, y);
                            else
                                drawRotatedImage(gc, befSpielerEcken[2], 180, x, y);
                        } else if (!right)
                            drawRotatedImage(gc, befSpielerEcken[1], 180, x, y);
                        else
                            drawRotatedImage(gc, befSpielerEcken[3], 180, x, y);

                        //Unten Links
                        if (!left) {
                            if (!bottom)
                                drawRotatedImage(gc, befSpielerEcken[0], 270, x, y);
                            else
                                drawRotatedImage(gc, befSpielerEcken[2], 270, x, y);
                        } else if (!bottom)
                            drawRotatedImage(gc, befSpielerEcken[1], 270, x, y);
                        else
                            drawRotatedImage(gc, befSpielerEcken[3], 270, x, y);

                        break;
                    case 4://Teleporter
                        gc.drawImage(teleporter[var], null, x, y);
                        break;
                    case 5://Bomben
                        gc.drawImage(bomb[var], null, x, y);
                        break;
                    case 6: //Wandfelder
                        {
                        //True wenn es nicht das selbe Feld ist, false wenn es ist
                        boolean[][] neighbours = new boolean[3][3];
                        for (int iN=-1; iN < 2;iN++)
                        {
                            //System.out.print("Line " + (i+iN) + ": ");
                            for (int jN=-1; jN<2; jN++)
                            {
                               // System.out.print(feld.getType(i+iN,j+jN) + "," + feld.getVariation(i+iN,j+jN) + ";");
                                neighbours[iN+1][jN+1] = !(feld.getTileType(i + iN, j + jN) == typ && feld.getTileVariation(i + iN, j + jN) == var);
                            }

                          //  System.out.print("\n");
                        }
                        gc.drawImage(wandMitte[var], null, x, y);

                        /*
                        00 10 20
                        01 11 21
                        02 12 22
                         */

                        //Kanten
                        if (neighbours[1][0])
                            drawRotatedImage(gc, wandKante[var], 0, x, y);
                        if (neighbours[2][1])
                            drawRotatedImage(gc, wandKante[var], 90, x, y);
                        if (neighbours[1][2])
                            drawRotatedImage(gc, wandKante[var], 180, x, y);
                        if (neighbours[0][1])
                            drawRotatedImage(gc, wandKante[var], 270, x, y);

                        //Ecken Außen
                        if (neighbours[1][0] && neighbours[0][1])
                            drawRotatedImage(gc, wandEckeAussen[var], 0, x, y);
                        if (neighbours[1][0] && neighbours[2][1])
                            drawRotatedImage(gc, wandEckeAussen[var], 90, x, y);
                        if (neighbours[2][1] && neighbours[1][2] )
                            drawRotatedImage(gc, wandEckeAussen[var], 180, x, y);
                        if (neighbours[1][2] && neighbours[0][1])
                            drawRotatedImage(gc, wandEckeAussen[var], 270, x, y);

                        //Ecken Innen
                        if (neighbours[0][0] && !(neighbours[0][1] || neighbours[1][0]))
                            drawRotatedImage(gc, wandEckeInnen[var],0,x, y);
                        if (neighbours[2][0] && !(neighbours[1][0] || neighbours[2][1]))
                            drawRotatedImage(gc, wandEckeInnen[var],90,x, y);
                        if (neighbours[2][2] && !(neighbours[2][1] || neighbours[1][2]))
                            drawRotatedImage(gc, wandEckeInnen[var],180,x, y);
                        if (neighbours[0][2] && !(neighbours[1][2] || neighbours[0][1]))
                            drawRotatedImage(gc, wandEckeInnen[var],270,x, y);
                    }
                        break;
                    case 7: //Kernfelder
                        {
                            switch (var)
                            {
                                case 2: //Zerstörte
                                    gc.drawImage(kernZer, null, x, y);
                                    break;
                                case 0: //Normale
                                    gc.setPaint(new Color((float)hauptSpiel.getFarbe(hauptSpiel.KernFeldBesitzer(i, j)).getRed(),
                                            (float)hauptSpiel.getFarbe(hauptSpiel.KernFeldBesitzer(i, j)).getGreen(),
                                            (float)hauptSpiel.getFarbe(hauptSpiel.KernFeldBesitzer(i, j)).getBlue() ));
                                    gc.fillRect(x, y, 32, 32);
                                    gc.drawImage(kern, null, x, y);
                                    break;
                                case 1: //Geladene
                                    gc.setPaint(new Color((float)hauptSpiel.getFarbe(hauptSpiel.KernFeldBesitzer(i, j)).getRed(),
                                            (float)hauptSpiel.getFarbe(hauptSpiel.KernFeldBesitzer(i, j)).getGreen(),
                                            (float)hauptSpiel.getFarbe(hauptSpiel.KernFeldBesitzer(i, j)).getBlue() ));
                                    gc.fillRect(x, y, 32, 32);
                                    gc.drawImage(kernGelad, null, x, y);
                                    break;
                                default://Das soll gar nicht passieren!
                                    break;

                            }
                        }
                        break;
                }

                int hilfe = feld.getTileBorders(i, j);
                if (hilfe > 0) {
                    for (int z = 0; z < 4; z++) {
                        if (hilfe % 2 == 1) {
                            drawRotatedImage(gc, mauer, z*90,x, y);
                        }
                        hilfe /= 2;
                    }
                }


            }

        }

        //Draw coordinates
        gc.setPaint(Color.lightGray);
        gc.setFont(new Font("SansSerif", Font.BOLD, 24));
        for (int i = 1; i < fX-1; i++)
        {
            if (i < 11)
                gc.drawString(""+ (i-1), i*32+8,24);
            else
                gc.drawString(""+ (i-1), i*32+2,24);
        }
        for (int i = 1; i < fY-1; i++)
        {
            if (i < 11)
                gc.drawString(""+ (i-1), 8,i*32-8+32);
            else
                gc.drawString(""+ (i-1), 2,i*32-8+32);
        }
    return toReturn;
    }

}
