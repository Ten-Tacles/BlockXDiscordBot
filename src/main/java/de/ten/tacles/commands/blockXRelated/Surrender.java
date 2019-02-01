package de.ten.tacles.commands.blockXRelated;

import de.ten.tacles.blockx.DiscordLogic.BlockXSession;
import de.ten.tacles.commands.Command;
import de.ten.tacles.commands.NotEnoughArgumentsException;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageEvent;


public class Surrender extends Command {

    private String[] triggerWords =  new String[] {"surrender", "giveup"};

    @Override
    public String[] getTriggerWords() {
        return triggerWords;
    }

    @Override
    public String getDescription() {
        return "Surrender a player in an active game.";
    }

    @Override
    public String[] getArguments() {
        return new String[] {" Player = Which player to surrender, Optional" ,
                "Name = Name of the game in which to surrender, Optional"};
    }

    @Override
    public String getName() {
        return "Surrender";
    }

    @Override
    protected void doCommand(String[] arguments, User user, TextChannel channel, MessageEvent event) throws NotEnoughArgumentsException {
        if (arguments.length < 1)
            throw new NotEnoughArgumentsException();
        try
        {
            BlockXSession session;
            if (arguments.length < 3)
                session = BlockXSession.findGameByUser(user);
            else
            session = BlockXSession.findGameByName(arguments[2]);

            if (session == null)
                throw new  NoGameFoundException();

            int player;
            if (arguments.length < 2)
                player = session.findPositionByUser(user);
            else
                player = Integer.parseInt(arguments[1]);


            session.SurrenderPlayer(player,user,channel);
        }
        catch (NumberFormatException exp)
        {
            channel.sendMessage("The player has to be a number!");
        } catch (NoGameFoundException e) {
            channel.sendMessage("I can't find that game.");
        }

    }
}
