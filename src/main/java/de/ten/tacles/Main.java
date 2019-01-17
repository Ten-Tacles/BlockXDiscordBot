package de.ten.tacles;
import de.ten.tacles.blockx.DiscordLogic.drawGameImage;
import de.ten.tacles.commands.Command;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class Main {

    //The prefix that a message needs for the bot to care for it
    //The bot will ignore it, if the message was send in a private chat
    public static final String prefix = "blx";
    public static String inviteLink;
    public static User owner;

    private static DiscordApi api;


    /**
     * The entrance point of our program.
     *
     * @param args The arguments for the program. The first element should be the bot's token.
     */

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Please provide a valid token as the first argument!");
            return;
        }

        String token = args[0];
        api = new DiscordApiBuilder().setToken(token).login().join();

        api.updateActivity("Write blx help for info!");

        Command.registerCommands();
        for (Command command : Command.listOfCommands)
        {
            api.addMessageCreateListener(command);
        }

        try {
            new drawGameImage();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            owner = api.getOwner().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("My name is " + api.getYourself().getName());

        for (Server server: api.getServers())
        {
            System.out.println(server.getName());

        }


        // Print the invite url of your bot

        inviteLink = "You can invite the bot by using the following url: "
                +api.createBotInvite(
                        new PermissionsBuilder().setAllowed(PermissionType.READ_MESSAGES, PermissionType.ATTACH_FILE,
                                PermissionType.SEND_MESSAGES, PermissionType.CONNECT, PermissionType.MANAGE_MESSAGES).build());

        inviteLink = inviteLink.concat("\nAlternatively without the ability to delete messages: "
                +api.createBotInvite(
                new PermissionsBuilder().setAllowed(PermissionType.READ_MESSAGES, PermissionType.ATTACH_FILE,
                        PermissionType.SEND_MESSAGES, PermissionType.CONNECT).build()));

        System.out.println(inviteLink);
    }


    public static DiscordApi getApi()
    {
        return api;
    }


}
