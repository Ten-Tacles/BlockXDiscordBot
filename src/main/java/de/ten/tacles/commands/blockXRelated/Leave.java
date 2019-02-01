package de.ten.tacles.commands.blockXRelated;

import de.ten.tacles.Main;
import de.ten.tacles.blockx.DiscordLogic.BlockXSession;
import de.ten.tacles.commands.Command;
import de.ten.tacles.commands.NotEnoughArgumentsException;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.MessageEvent;


public class Leave extends Command {

    private String[] triggerWords =  new String[] {"unregister", "leave"};

    @Override
    public String[] getTriggerWords() {
        return triggerWords;
    }

    @Override
    public String getDescription() {
        return "Leave an active game.";
    }

    @Override
    public String[] getArguments() {
        return new String[] {"Name: Name of the game to leave.",
                            "Positon: Number of the player you want to leave, leave blank to completely drop out"};
    }

    @Override
    public String getName() {
        return "Leave";
    }

    @Override
    protected void doCommand(String[] arguments, User user, TextChannel channel, MessageEvent event) throws NotEnoughArgumentsException {
        if (arguments.length < 2)
            throw new NotEnoughArgumentsException();
        try
        {
            BlockXSession session = BlockXSession.findGameByName(arguments[1]);
            if (session == null)
                throw new  NoGameFoundException();

            if (arguments.length > 3) {
                int player = session.findPositionByArgument(arguments[2]);
                session.unregisterPlayer(user, player, channel);
            }
            else
                session.kickPlayer(user,channel);
        }
        catch (NumberFormatException exp)
        {
            channel.sendMessage("The player has to be a number!");
        } catch (NoGameFoundException e) {
            channel.sendMessage("I can't find that game.");
        }

    }
}
