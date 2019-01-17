package de.ten.tacles.commands.blockXRelated;


import de.ten.tacles.blockx.DiscordLogic.BlockXSession;
import de.ten.tacles.commands.Command;
import de.ten.tacles.commands.NotEnoughArgumentsException;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageEvent;



public class SetTile extends Command {

    private String[] triggerWords = {"set", "setTile"};

    @Override
    public String[] getTriggerWords() {

        return triggerWords;
    }

    @Override
    public String getDescription() {
        return "Set a tile in your map editor session.";

    }

    @Override
    public String[] getArguments() {
        return new String[] {
                "X =  A number representing the X Coordinate" ,
                "Y = A number representing the Y Coordinate" ,
                "Tile = For which player to add the turn, optional" ,
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
            int player;
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
                player = Integer.parseInt(arguments[3]);
            else
                player = game.findPositionByUser(user);

            //No player found, message is useless
            if (player < 1 || player > game.getSpiel().getSpielerAnzahl())
                throw new NoPlayerFoundException();

            game.registerMove(x,y,player, user, channel);

            //Delete original message, if I can
            event.deleteMessage("Hiding the placed turn.");

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
