package de.ten.tacles.commands.blockXRelated;

import de.ten.tacles.Main;
import de.ten.tacles.blockx.DiscordLogic.BlockXSession;
import de.ten.tacles.commands.Command;
import de.ten.tacles.commands.NotEnoughArgumentsException;
import jdk.nashorn.internal.runtime.regexp.joni.constants.Arguments;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.MessageEvent;


public class Info extends Command {

    private String[] triggerWords =  new String[] {"info"};
    @Override

    public String[] getTriggerWords() {
        return triggerWords;
    }

    @Override
    public String getDescription() {
        return "Get some info about a game.";
    }

    @Override
    public String[] getArguments() {
        return new String[] {"Name: Name of the game."};
    }

    @Override
    public String getName() {
        return "Info";
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

            session.getInfo(channel);
        }catch (NoGameFoundException e) {
            channel.sendMessage("I can't find that game.");
        }


    }
}
