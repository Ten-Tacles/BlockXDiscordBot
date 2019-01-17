package de.ten.tacles.commands.blockXRelated;


import de.ten.tacles.blockx.DiscordLogic.BlockXSession;
import de.ten.tacles.blockx.DiscordLogic.MapReader;
import de.ten.tacles.blockx.HauptSpiel;
import de.ten.tacles.blockx.Spielfeld;
import de.ten.tacles.commands.Command;
import de.ten.tacles.commands.NotEnoughArgumentsException;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageEvent;


public class Startgame extends Command {

    private String triggerWords[] = {"startGame", "createGame", "sg"};


    @Override
    public String[] getTriggerWords() {
        return triggerWords;
    }

    @Override
    public String getDescription() {
        return "Starts a new game of BlockX.";
    }

    @Override
    public String[] getArguments() {
        return new String[] {"Name: The name to identify this game.",
                             "Map: Number of the of the map to start."};
    }

    @Override
    public String getName() {
        return "startGame";
    }

    @Override
    protected void doCommand(String[] arguments, User user, TextChannel channel, MessageEvent event) throws NotEnoughArgumentsException {
        if (arguments.length < 3)
            throw new NotEnoughArgumentsException();
        try {
            MapReader reader = new MapReader();
            String name = arguments[1];
            int number = Integer.parseInt(arguments[2]);
            if (number > 12 || number < 1)
                throw new NumberFormatException();
            Spielfeld spielfeld = reader.KartenLeser(number);

            new BlockXSession(user ,channel, new HauptSpiel(spielfeld), name);

        }
        catch (NumberFormatException exp)
        {
            channel.sendMessage("That's not a map!");
        }
    }
}
