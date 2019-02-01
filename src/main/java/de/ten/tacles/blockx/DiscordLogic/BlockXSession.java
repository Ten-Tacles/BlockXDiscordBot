package de.ten.tacles.blockx.DiscordLogic;

import de.ten.tacles.blockx.HauptSpiel;
import de.ten.tacles.blockx.ReturnStrings;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;



public class BlockXSession
{
    private HauptSpiel        spiel;
    private String            name;
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<User>   spectators = new ArrayList<>();
    private User              creator;
    private ArrayList<TextChannel> channels = new ArrayList<>();

    private BufferedImage currentGameState;

    private static List<BlockXSession> activeGames = new ArrayList<>();

    public BlockXSession (User creator, TextChannel creationPlace, HauptSpiel spiel, String name)

    {

        this.creator = creator;
        this.spiel = spiel;
        if (creationPlace.getType() == ChannelType.SERVER_TEXT_CHANNEL)
        channels.add(creationPlace);
        players.add(new Player(creator, new int[] {1}));
        //spectators.add(creator);
        activeGames.add(this);
        this.name = name;

        StringBuilder message = new StringBuilder();
        message.append("Created game ").append(name).append("\n");
        for (int i = 0; i < spiel.getSpielerAnzahl();i++)
        {
            if (!spiel.getSpielerRaus()[i])
            {
                message.append("Player ").append(spiel.getFarbenName(i+1));
                message.append(" (").append(i+1).append(") ");
                message.append(" has ").append(spiel.getZugAnzahlen()[i]).append(" moves.\n");
            }
        }

        currentGameState = drawGameImage.active.drawView(spiel);
        EmbedBuilder embedded = new EmbedBuilder();
        embedded.setImage(currentGameState);
        creationPlace.sendMessage(message.toString(), embedded);

       // new MessageBuilder().addAttachment(drawGameImage.active.drawView(spiel), "currentState.png")
         //       .append("Created game " + name).send(creationPlace);


    }
    public static BlockXSession findGameByName(String name)
    {
        for (BlockXSession game : activeGames)
        {
            if (game.name.equals(name))
                return game;
        }
        return null;
    }


    /**
     * This method returns a game if, and only if, one game contains this user.
     * If the user doesn't participate in a game, or is in more than 1, it returns null
     *
     * @param user the user you are looking for
     * @return The only game this user participates in
     */
    public static BlockXSession findGameByUser(User user)
    {
        //If you find more than 2, you have a problem;
        BlockXSession gamesFound = null;

        for (BlockXSession game : activeGames)
        {
            if (game.isPartOfTheGame(user) || game.creator.equals(user))
                if (gamesFound == null)
                {
                    gamesFound = game;
                }
                else
                    return null; //If I find a second game, I can't get a definite case.
        }

        return gamesFound;

    }

    public static void listAllGames(TextChannel channel)
    {
        StringBuilder returnMessage = new StringBuilder();
        returnMessage.append("There are ").append(activeGames.size()).append(" active games.\n");

        for (BlockXSession session: activeGames
             ) {
            returnMessage.append("Game: ").append(session.name).append("\n");
            returnMessage.append("Owner: ").append(session.creator.getName()).append("\n");

            if (activeGames.size()< 5)
            {
                for (int i = 0; i < session.spiel.getSpielerAnzahl();i++)
                {
                    returnMessage.append("Player ").append(session.getSpiel().getFarbenName(i+1));
                    returnMessage.append(" (").append(i+1).append(") ");
                    returnMessage.append(" has ").append(session.spiel.getZugAnzahlen()[i]).append(" moves");
                    if (session.findUsersByPosition(i+1).length == 0)
                        returnMessage.append(" and is not controlled by anyone.");
                    else
                    {
                        returnMessage.append(" and is controlled by: ");
                        for (Player player: session.findUsersByPosition(i+1) ) {
                            returnMessage.append(player.getUser().getName()).append(", ");
                        }
                        returnMessage.deleteCharAt(returnMessage.length()-1);
                        returnMessage.deleteCharAt(returnMessage.length()-1);
                        returnMessage.append(".");
                    }
                    returnMessage.append("\n");
                }
            }

            returnMessage.append("-------\n");
        }

        channel.sendMessage(returnMessage.toString());
    }


    /**
     * Returns a position if, and only if, this user only controls a single position.
     * Returns -1 otherwise.
     *
     * @param user the user
     * @return The position this user controls, -1 if several.
     */
    public int findPositionByUser(User user)
    {
        for (Player player : players)
        {
            if (player.getUser().equals(user))
            {
                if (player.getPositions().length == 1)
                {
                    return player.getPositions()[0];
                }
                else
                    return -1;
            }
        }
        return -1;
    }

    /**
     * Returns a position via the name of a colour.
     *
     * Returns -1 otherwise.
     *
     * @param colour the user
     * @return The position that has this colour, -1 if it's not part of this game
     */
    public int findPositionByColour(String colour)
    {
        System.out.println(colour);
        for (int i = 1; i <= spiel.getSpielerAnzahl(); i++)
        {
            System.out.println(spiel.getFarbenName(i) + " " + i);
            if (spiel.getFarbenName(i).equalsIgnoreCase(colour))
            {
                return i;
            }
        }
        return -1;
    }

    private Player[] findUsersByPosition(int position)
    {
        ArrayList<Player> toReturn = new ArrayList<>();

        for (Player player1 : players)
        {
            if (player1.hasPosition(position))
                toReturn.add(player1);
        }

        return toReturn.toArray(new Player[toReturn.size()]);
    }

    private Player findPlayerByUser(User user)
    {
        for (Player player : players)
        {
            if (player.getUser().equals(user))
                return player;
        }
        return null;
    }

    private boolean isPartOfTheGame(User user)
    {
        for (Player player : players)
        {
            if(player.getUser().equals(user))
            return true;
        }
        return false;
    }

    private boolean canControlThisPosition(User user, int position)
    {
        if (user == creator)
            return true;


        if (isPartOfTheGame(user))
        {
            int[] playerList = findPlayerByUser(user).getPositions();
            for (int aPlayer : playerList) {
                if (aPlayer == position)
                    return true;
            }
        }

        return false;
    }

    public HauptSpiel getSpiel()
    {
        return spiel;
    }


    public void registerPlayer(User user, int position, TextChannel channel)
    {

        if (position < 1 || position > spiel.getSpielerAnzahl())
        {
            channel.sendMessage("That player positon doesn't exist!" );
            return;
        }
        //The player is not yet registered
        if (!isPartOfTheGame(user))
        {
            if (findUsersByPosition(position).length != 0)
            {
                channel.sendMessage("Somebody already controls that player!" );
                return;
            }
            Player newPlayer = new Player(user, new int[] {position});
            players.add(newPlayer);
            channel.sendMessage("You have joined this game for player "
                    + spiel.getFarbenName(position) + " ("+ position + ")!" );

        }
        //The player is registerd
        else
        {
            Player player = findPlayerByUser(user);
            if (player.hasPosition(position))
                channel.sendMessage("You already control that player!");
            else if (findUsersByPosition(position).length != 0)
                channel.sendMessage("Somebody already controls that player!" );
            else {
                channel.sendMessage("You have added player "
                        + spiel.getFarbenName(position) + " (" + position + ") to your control." );
                player.addPosition(position);
            }
        }
    }

    public void unregisterPlayer(User user, int position, TextChannel channel)
    {

        if (!isPartOfTheGame(user))
        {
            channel.sendMessage("You aren't part of this game." );
        }
        else
        {
            Player player1 = findPlayerByUser(user);
            if (!player1.hasPosition(position))
            {
                channel.sendMessage("You do not control that player." );
                return;
            }
            player1.removePosition(position);
            if (player1.getPositions().length > 1)
            {
                channel.sendMessage("You have removed player "
                        + spiel.getFarbenName(position) + " (" + position + ") from your control." );
                for (User spectator : spectators)
                {
                    if (!spectator.equals(user))
                        channel.sendMessage("Player "
                                + spiel.getFarbenName(position) + " (" + position + ") is no longer controlled by " +user.getName() +"." );
                }
            }
            else
            {
                players.remove(player1);
                channel.sendMessage("You have been removed from the game." );
            }
        }
    }

    public void kickPlayer(User user, TextChannel channel)
    {
        if (players.remove(findPlayerByUser(user)))
        {
            channel.sendMessage("Removed " + user.getMentionTag() +  " from the game.");
        }
        else
        {
            channel.sendMessage("That person is not part of this game.");
        }
    }

    public void getInfo(TextChannel channel)
    {
        StringBuilder infos = new StringBuilder();

        infos.append("Game: ").append(name).append("\n");
        infos.append("Owner: ").append(creator.getName()).append("\n");
        for (int i = 0; i < spiel.getSpielerAnzahl();i++)
        {
            infos.append("Player ").append(spiel.getFarbenName(i+1));
            infos.append(" (").append(i+1).append(") ");
            infos.append(" has ").append(spiel.getZugAnzahlen()[i]).append(" moves");
            if (findUsersByPosition(i+1).length == 0)
                infos.append(" and is not controlled by anyone.");
            else
                infos.append(".");
            infos.append("\n");
        }

        EmbedBuilder embedded = new EmbedBuilder();
        embedded.setImage(currentGameState);
        channel.sendMessage(infos.toString(), embedded);

    }


    public int findPositionByArgument(String argument)
    {
        int position;

        try 
        {
            position = Integer.parseInt(argument);
        }catch (NumberFormatException exp)
        {
           position = findPositionByColour(argument);
        }

        return position;
    }

    public void registerMove(int x, int y, int player, User user, TextChannel channel)
    {
        if (!isPartOfTheGame(user))
        {
            channel.sendMessage("You are not part of this game!\nRegister first.");
            return;
        }
        else if (!canControlThisPosition(user, player)){
            channel.sendMessage("You are not allowed to control this player!\nRegister it first.");
            return;
        }


        System.out.println("Registering Move " + x + "/" + y );
        ArrayList<ReturnStrings> returnValues = spiel.setzeZug(x,y,player);
        String message = "";
        boolean useEmbed = false;

        for (ReturnStrings returnValue: returnValues)
        {
            System.out.println(returnValue);
            switch (returnValue)
            {
                case NEWTURN:
                    useEmbed = true;
                    message = "A new round begins. \n".concat(message);
                    for (int i = 0; i < spiel.getSpielerAnzahl();i++)
                    {
                        if (!spiel.getSpielerRaus()[i])
                        {
                            message = message.concat("Player " + (i+1) + " has " + spiel.getZugAnzahlen()[i] + " moves.\n");
                        }
                    }
                    break;
                case NOPLACE:
                        System.out.println("Sending Can't Place There to channel:" + channel.getId());
                        message = message.concat("Player " + player + " can't place there!\n");
                    break;
                case OK:
                    break;
                case LOST:
                    message = message.concat("Player " + player + " can't place any moves, because they lost!\n");
                    break;
                case NOMOVES:
                    System.out.println("Sending Has No Moves to channel:" + channel.getId());
                    message = message.concat("Player " + player + " has no moves left!\n");
                    break;
                case PLAYERLOST:
                    System.out.println("Sending PlayerLost to channel:" + channel.getId());
                    boolean[] spielerGradeVerloren = spiel.getRecentlyLostPlayers();
                    for (int i = 0; i < spiel.getSpielerAnzahl();i++)
                    {
                        if (spielerGradeVerloren[i])
                        message = message.concat("Player " + (i+1) + " has lost!\n");
                    }

                    break;
                case END:
                    useEmbed = true;
                    message = message.concat("The game has ended.\n");
                    int winner = spiel.getLastPlayer();
                    if (winner == 0)
                        message = message.concat("It ended in a draw.\n");
                    else
                        message = message.concat("Player " + winner +  " is the winner!\n");
                    //This game now commits garbagecollection.
                    activeGames.remove(this);
                    break;
            }
        }

        if (useEmbed)
        {
            currentGameState = drawGameImage.active.drawView(spiel);
            EmbedBuilder embedded = new EmbedBuilder();
            embedded.setImage(currentGameState);
            for (TextChannel channel1 : channels)
            {
                System.out.println("Sending update to channel:" + channel1.getId());
                channel1.sendMessage(message, embedded);
            }
            for (User spectator: spectators)
            {
                System.out.println("Sending update to user:" + spectator.getName());
                spectator.sendMessage(message, embedded);
            }
        }
        else
        {
            channel.sendMessage(message);
        }
    }

    public void spectate(User user)
    {
        if (spectators.contains(user))
        {
            user.sendMessage("I will no longer inform you about this game.");
            spectators.remove(user);
        }
        else
        {
            EmbedBuilder embedded = new EmbedBuilder();
            embedded.setImage(currentGameState);
            user.sendMessage("You are now spectating this game.",embedded);

            spectators.add(user);

        }
    }


    public void stopSession(User user, TextChannel channel)
    {
        if (!user.equals(creator))
        {
            channel.sendMessage("You are not allowed to stop other peoples games! \nRude.");
        }
        else
        {
            for (User spectator: spectators)
                spectator.sendMessage("The game " + name + " you were watching, has been cancelled.");
            for (TextChannel channel1: channels)
            {
                channel1.sendMessage("The game " + name + " has been cancelled.");
            }

            if (!channels.contains(channel))
                channel.sendMessage("The game " + name + " has been successfully cancelled");

            //This game now commits garbagecollection.
            activeGames.remove(this);
        }
    }

    public void freePlayer(int player, TextChannel channel)
    {
        if (findUsersByPosition(player).length == 0)
        {
            channel.sendMessage("Nobody controls that player!");
            return;
        }
        channel.sendMessage("Freeing up player " + player + ".");

        for (Player user : players)
        {
            user.removePosition(player);
            if (user.getPositions().length == 0)
            {
                kickPlayer(user.getUser(), channel);
            }
            else
            {
                channel.sendMessage("Player " + player +  " is no longer controlled by " + user.getUser().getName() +  ".");
            }

        }

    }

    public void SurrenderPlayer(int position, User user, TextChannel channel)
    {
        if (!isPartOfTheGame(user))
        {
            channel.sendMessage("You are not part of this game!");
        }
        else if (!findPlayerByUser(user).hasPosition(position))
        {
            channel.sendMessage("You do not control that player!");
        }
        else
        {
            channel.sendMessage("That player is now marked as defeated at the start of the next turn.");
            spiel.SurrenderPlayer(position);
        }
    }

    public User getCreator()
    {
        return creator;
    }
}
