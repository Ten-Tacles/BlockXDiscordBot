package de.ten.tacles.commands.blockXRelated;

import de.ten.tacles.Main;
import de.ten.tacles.blockx.DiscordLogic.BlockXSession;
import de.ten.tacles.commands.Command;
import de.ten.tacles.commands.NotEnoughArgumentsException;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.MessageEvent;


public class Register extends Command {

    private String[] triggerWords =  new String[] {"register", "join", "reg"};

    @Override
    public String[] getTriggerWords() {
        return triggerWords;
    }

    @Override
    public String getDescription() {
        return "Join an active game.";
    }

    @Override
    public String[] getArguments() {
        return new String[] {"Name: Name of the game to join.",
                            "Positon: Number of the player you want to control"};
    }

    @Override
    public String getName() {
        return "Join";
    }

    @Override
    protected void doCommand(String[] arguments, User user, TextChannel channel, MessageEvent event) throws NotEnoughArgumentsException {
        if (arguments.length < 3)
            throw new NotEnoughArgumentsException();
        try
        {
            BlockXSession session = BlockXSession.findGameByName(arguments[1]);
            if (session == null)
                throw new  NoGameFoundException();

            int position = session.findPositionByArgument(arguments[2]);
            if (position == -1)
                throw new NoPlayerFoundException();

            session.registerPlayer(user,position,channel);
        }
        catch (NoGameFoundException e)
        {
            channel.sendMessage("I can't find that game.");
        }catch (NoPlayerFoundException e)
        {
            channel.sendMessage("I don't know which player this is for!");
        }

    }

}
