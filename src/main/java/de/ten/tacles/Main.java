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
    public static Server testServer;

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

        for (Server server: api.getServers())
        {
            System.out.println(server.getName());
            if (server.getName().equals("BlockX Testing Station"))
                testServer = server;
        }

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



        testServer.getChannelById(537938534703693837L).get().asServerTextChannel().get().sendMessage(
                Main.testServer.getRolesByName("ActiveTesterRole").get(0).getMentionTag() + " I am awake and ready for playing!");


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
