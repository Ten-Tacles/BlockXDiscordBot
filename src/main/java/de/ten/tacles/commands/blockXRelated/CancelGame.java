package de.ten.tacles.commands.blockXRelated;

import de.ten.tacles.blockx.DiscordLogic.BlockXSession;
import de.ten.tacles.commands.Command;
import de.ten.tacles.commands.NotEnoughArgumentsException;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageEvent;


public class CancelGame extends Command {

    private String[] triggerWords =  new String[] {"stop","cancel"};

    @Override
    public String[] getTriggerWords() {
        return triggerWords;
    }

    @Override
    public String getDescription() {
        return "Cancel an active game you created.";
    }

    @Override
    public String[] getArguments() {
        return new String[] {"Name: Name of the game to cancel."};
    }

    @Override
    public String getName() {
        return "CancelGame";
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


            session.stopSession(user,channel);
        } catch (NoGameFoundException e) {
            channel.sendMessage("I can't find that game.");
        }

    }

}
