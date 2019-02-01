package de.ten.tacles.commands.blockXRelated;


import de.ten.tacles.Main;
import de.ten.tacles.blockx.DiscordLogic.BlockXSession;
import de.ten.tacles.commands.Command;
import de.ten.tacles.commands.NotEnoughArgumentsException;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageEvent;


public class Kick extends Command {

    private String[] triggerWords =  new String[] {"kick", "k"};
    @Override

    public String[] getTriggerWords() {
        return triggerWords;
    }

    @Override
    public String getDescription() {
        return "Kick a person from a game.";
    }

    @Override
    public String[] getArguments() {
        return new String[] {"Name: Name of the game to leave.",
                            "Person: The person/user you want to kick. OR The player slot you want to free."};
    }

    @Override
    public String getName() {
        return "Kick";
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

            if (session.getCreator() != user)
            {
                channel.sendMessage("You are not the creator of this game!");
                return;
            }

            //Freeing up a playerslot
            if (arguments[2].length() < 4)
            {
                try
                {
                    int player = Integer.parseInt(arguments[2]);
                    session.freePlayer(player, channel);
                }
                catch (NumberFormatException e)
                {
                    if (session.findPositionByColour(arguments[2]) > 0)
                        session.freePlayer(session.findPositionByColour(arguments[2]), channel);
                    else
                        throw new NoPlayerFoundException();
                }
                return;
            }
            System.out.println(arguments[2].substring(2,arguments[2].length()-2));

            if (Main.getApi().getCachedUserById(arguments[2].substring(2,arguments[2].length()-1)).isPresent())
                user = Main.getApi().getCachedUserById( arguments[2].substring(2,arguments[2].length()-1)).get();
            else
            {
                if (session.findPositionByColour(arguments[2]) > 0)
                    session.freePlayer(session.findPositionByColour(arguments[2]), channel);
                else
                    throw new NoPlayerFoundException();
            }

            session.kickPlayer(user, channel);
        }catch (NoGameFoundException e) {
            channel.sendMessage("I can't find that game.");
        } catch (NoPlayerFoundException e) {
            channel.sendMessage("I can't find that person.");
        }
    }
}
