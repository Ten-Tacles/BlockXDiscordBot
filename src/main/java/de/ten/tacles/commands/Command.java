package de.ten.tacles.commands;

import de.ten.tacles.Main;
import de.ten.tacles.commands.blockXRelated.*;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.MessageEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.ArrayList;

public abstract class Command implements MessageCreateListener {

    /**
     * A list of all commands this bot has
     */
    public static ArrayList<Command> listOfCommands = new ArrayList<>();
    static ArrayList<Command> blockXCommands = new ArrayList<>();
    static ArrayList<Command> generalCommands = new ArrayList<>();
    /**
     * Hidden, dev specific commands.
     * These are not listed by the Help command, no matter how much you beg
     */
    private static ArrayList<Command> devCommands = new ArrayList<>();

    /**
     * Triggered.
     * @return A list of all words that will trigger this command
     */
    public abstract String[] getTriggerWords();

    /**
     * Will be shown when help is called with this as its first argument
     * @return A short description of what this command does
     */
    public abstract String getDescription();

    /**
     * A list of all arguments this command needs, or accepts.
     * Will list both mandatory and optional arguments, in that order.
     * @return The list of needed and accepted arguments
     */
    public abstract String[] getArguments();

    /**
     * All commands have a name they are identified in Help.
     * Though it might not be a triggerword.
     * @return the name of this command
     */
    public abstract String getName();

    /**
     * This abstract method is called by onMessageCreate and will actually do the stuff the command is used for.
     * Each subclass implements this (obviously).
     *
     * @param arguments The extracted arguments from the message. [0] is always the triggerword used
     * @param user Whoever dared trigger this bot
     * @param channel Where the scoundrel did it
     * @throws NotEnoughArgumentsException Interprogramm Communication via Errorthrowing
     */
    protected abstract void doCommand(String[] arguments, User user, TextChannel channel, MessageEvent event) throws NotEnoughArgumentsException;


    /**
     * This message is called when a message is posted in a channel the bot has access to.
     * It will then check if it should care about it, if it does it will clean it up, and send it to the doCommand method.
     * This message is defined here, so I avoid writing the same code everywhere.
     * It may be a bit small, but I don't care.
     *
     * @param event  The message that triggered this.
     */
    @Override
    public void onMessageCreate(MessageCreateEvent event) {

        String message = event.getMessage().getContent();

        //Checking if this message is something I should care about
        //AKA It begins with prefix + method triggerword
        //OR It begins with a triggerword and is a private message
        {
            boolean shouldIgnore = true;
            for (String string : getTriggerWords()) {
                if (message.startsWith(Main.prefix + " " + string) || (event.isPrivateMessage() && (message.startsWith(string))))
                    shouldIgnore = false;
            }
            if (shouldIgnore)
                return;
        }

        //Cleaning prefix
        if (message.startsWith(Main.prefix))
            message = message.substring(message.indexOf(" ")).trim().toLowerCase();


        try {
            System.out.println("Attempting " + getName() + " command");
            this.doCommand(message.split(" "), event.getMessageAuthor().asUser().get(), event.getChannel(), event);
            System.out.println(getName() + " command succesful (Probably)");
        }
        catch (NotEnoughArgumentsException e){
            event.getChannel().sendMessage("I need more arguments for that command!");
        }
        catch (Exception e)
        {
            System.out.println("Message:"  + message);
            event.getChannel().sendMessage("Something went wrong with your command, sorry :/");
            throw e;
        }
    }

    /**
     * This method registers all commands this bot can execute.
     * It's run once when the bot starts, because I am too lazy to learn how to load them from a file or something.
     *
     * All commands have to be added in here.
     *
     */
    public static void registerCommands()
    {
        //Register Help Command
        listOfCommands.add(new Help());
        generalCommands.add(listOfCommands.get(listOfCommands.size()-1));

        //Register Secret Santa Command
        listOfCommands.add(new SecretSanta());
        devCommands.add(listOfCommands.get(listOfCommands.size()-1));

        //Register count Command
        listOfCommands.add(new CountMessages());
        devCommands.add(listOfCommands.get(listOfCommands.size()-1));


        //Register createGame Command
        listOfCommands.add(new Startgame());
        blockXCommands.add(listOfCommands.get(listOfCommands.size()-1));

        //Register placeTurn Command
        listOfCommands.add(new PlaceTurns());
        blockXCommands.add(listOfCommands.get(listOfCommands.size()-1));

        //Register Register Command
        listOfCommands.add(new Register());
        blockXCommands.add(listOfCommands.get(listOfCommands.size()-1));

        //Register Unregister Command
        listOfCommands.add(new Leave());
        blockXCommands.add(listOfCommands.get(listOfCommands.size()-1));

        //Register Kick Command
        listOfCommands.add(new Kick());
        blockXCommands.add(listOfCommands.get(listOfCommands.size()-1));

        //Register Spectate Command
        listOfCommands.add(new Spectate());
        blockXCommands.add(listOfCommands.get(listOfCommands.size()-1));

        //Register info Command
        listOfCommands.add(new Info());
        blockXCommands.add(listOfCommands.get(listOfCommands.size()-1));

        //Register cancel Command
        listOfCommands.add(new CancelGame());
        blockXCommands.add(listOfCommands.get(listOfCommands.size()-1));

        //Register list Command
        listOfCommands.add(new List());
        blockXCommands.add(listOfCommands.get(listOfCommands.size()-1));

        //Register list Command
        listOfCommands.add(new Rules());
        blockXCommands.add(listOfCommands.get(listOfCommands.size()-1));


        // It's probably really bad if 2 commands have the same triggerword
        for(int i = 0; i < listOfCommands.size()-1; i++)
        {
            for (int j = i+1; j < listOfCommands.size(); j++)
            {
                String[] triggerWords1 = listOfCommands.get(i).getTriggerWords();
                String[] triggerWords2 = listOfCommands.get(j).getTriggerWords();

                for (String aTriggerWords1 : triggerWords1) {
                    for (String aTriggerWords2 : triggerWords2) {
                        if (aTriggerWords1.startsWith(aTriggerWords2) || aTriggerWords2.startsWith(aTriggerWords1)) {
                            System.out.println("WARNING: " + listOfCommands.get(i).getName()
                                    + " and " + listOfCommands.get(j).getName()
                                    + " share the same triggerword " + aTriggerWords1);
                        }
                    }
                }
            }
        }
    }
}
