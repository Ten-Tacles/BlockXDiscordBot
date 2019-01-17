package de.ten.tacles.commands.blockXRelated;

import de.ten.tacles.blockx.DiscordLogic.BlockXSession;
import de.ten.tacles.commands.Command;
import de.ten.tacles.commands.NotEnoughArgumentsException;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageEvent;


public class List extends Command {

    private String[] triggerWords =  new String[] {"list"};
    @Override

    public String[] getTriggerWords() {
        return triggerWords;
    }

    @Override
    public String getDescription() {
        return "List all active games.";
    }

    @Override
    public String[] getArguments() {
        return new String[] {};
    }

    @Override
    public String getName() {
        return "List";
    }

    @Override
    protected void doCommand(String[] arguments, User user, TextChannel channel, MessageEvent event) throws NotEnoughArgumentsException {

        if (arguments.length < 1)
            throw new NotEnoughArgumentsException();

        BlockXSession.listAllGames(channel);


    }
}
