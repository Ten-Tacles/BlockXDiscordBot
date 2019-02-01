package de.ten.tacles.commands.blockXRelated;


import de.ten.tacles.blockx.DiscordLogic.BlockXSession;
import de.ten.tacles.commands.Command;
import de.ten.tacles.commands.NotEnoughArgumentsException;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageEvent;


class NoGameFoundException extends Exception {}

public class PlaceTurns extends Command {

    private String[] triggerWords = {"move", "place", "mv", "pt", "placeTurn"};

    @Override
    public String[] getTriggerWords() {

        return triggerWords;
    }

    @Override
    public String getDescription() {
        return "Places a move in a game of BlockX!";

    }

    @Override
    public String[] getArguments() {
        return new String[] {
                "X =  A number representing the X Coordinate" ,
                "Y = A number representing the Y Coordinate" ,
                "Player = For which player to add the turn, optional" ,
                "Name = Name of the game, optional"};
    }

    @Override
    public String getName() {
        return "placeTurn";
    }

    @Override
    protected void doCommand(String[] arguments, User user, TextChannel channel, MessageEvent event) throws NotEnoughArgumentsException {
        if (arguments.length < 3)
            throw new NotEnoughArgumentsException();

        try {
            int x = Integer.parseInt(arguments[1]);
            int y = Integer.parseInt(arguments[2]);
            int position;
            BlockXSession game;


            if (arguments.length > 4)
            {
                game = BlockXSession.findGameByName(arguments[4]);
            }
            else
                game = BlockXSession.findGameByUser(user);

            //Game not specified, message is useless.
            if (game == null)
                throw new NoGameFoundException();

            if (arguments.length > 3)
            {
                try {
                    position = Integer.parseInt(arguments[3]);
                }catch (NumberFormatException exp)
                {
                    position = game.findPositionByColour(arguments[3]);
                }
            }
            else
                position = game.findPositionByUser(user);

            //No player found, message is useless
            if (position < 1 || position > game.getSpiel().getSpielerAnzahl())
                throw new NoPlayerFoundException();

            game.registerMove(x,y,position, user, channel);

            //Delete original message, if I can
            if (channel.canYouManageMessages() && event.getServer().isPresent())
            {
                event.deleteMessage("Hiding the placed turn.");

                boolean test = channel.getMessages(10).join().stream().anyMatch(message
                        -> message.getContent().equalsIgnoreCase(
                                "Accepted your turn placement and deleted your message (for secrecy)!"));
                if (!test)
                 channel.sendMessage("Accepted your turn placement and deleted your message (for secrecy)!");
            }

        }
        catch (NumberFormatException exception)
        {
            channel.sendMessage("The coordinates go first, they were wrong or missing!");
        }
        catch (NoGameFoundException exception)
        {
            channel.sendMessage("The game either doesn't exist, or you haven't specified it!");
        } catch (NoPlayerFoundException e)
        {
            channel.sendMessage("I don't know which player this is for!");
        }
    }
}
