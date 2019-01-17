package de.ten.tacles.commands.blockXRelated;


import de.ten.tacles.blockx.DiscordLogic.BlockXSession;
import de.ten.tacles.blockx.DiscordLogic.MapEditorSession;
import de.ten.tacles.blockx.DiscordLogic.MapReader;
import de.ten.tacles.blockx.HauptSpiel;
import de.ten.tacles.blockx.Spielfeld;
import de.ten.tacles.commands.Command;
import de.ten.tacles.commands.NotEnoughArgumentsException;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageEvent;


public class StartMapEditor extends Command {

    private String triggerWords[] = {"startMapEditor", "createMap", "cm", "startMap"};


    @Override
    public String[] getTriggerWords() {
        return triggerWords;
    }

    @Override
    public String getDescription() {
        return "Starts a mapeditor session for you, allowing you to create your own custom map!.";
    }

    @Override
    public String[] getArguments() {
        return new String[] {
                "Width: Initial Width of the map.",
                "Height: Initial Height of the map.",
                "Playercount: Initial Number of players your map has."};
    }

    @Override
    public String getName() {
        return "StartMapEditor";
    }

    @Override
    protected void doCommand(String[] arguments, User user, TextChannel channel, MessageEvent event) throws NotEnoughArgumentsException {
        if (arguments.length < 4)
            throw new NotEnoughArgumentsException();
        try {

            int width = Integer.parseInt(arguments[1]);
            if (width < 3)
            {
                channel.sendMessage("That map is too thin!");
                return;
            }
            int height = Integer.parseInt(arguments[2]);
            if (height < 3)
            {
                channel.sendMessage("That map is too short!");
                return;
            }
            int playerCount = Integer.parseInt(arguments[3]);
            if (playerCount < 2)
            {
                channel.sendMessage("Maps need at least 2 players!");
                return;
            }
            else if (playerCount > 12)
            {
                channel.sendMessage("A maximum number of 12 players is currently possible. \n12 Players is a lot anyway.");
                return;
            }

            new MapEditorSession(user, width, height, playerCount);

        }
        catch (NumberFormatException exp)
        {
            channel.sendMessage("There was a problem with your input!");
        }
    }
}
