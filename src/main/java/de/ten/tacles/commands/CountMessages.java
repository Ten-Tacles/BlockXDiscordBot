package de.ten.tacles.commands;


import de.ten.tacles.Main;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageEvent;

import java.util.HashSet;
import java.util.List;

public class CountMessages extends Command {
    private String triggerWords[] = {"analyze", "analyse"};
   // private Analyzer analyzer = new Analyzer();


    @Override
    public String[] getTriggerWords() {
        return triggerWords;
    }


    @Override
    public String getDescription() {
        return "Gives you a list of all commands you can use with this bot! Or information about a specific command.";
    }

    @Override
    public String[] getArguments() {
        return new String[] {"Type: What I should analyze",
                "Serverwide: True or false, channel or server only"};
    }

    @Override
    public String getName() {
        return "CountMessages";
    }

    @Override
    protected void doCommand(String[] arguments, User user, TextChannel channel, MessageEvent event) throws NotEnoughArgumentsException{

        if (!user.equals(Main.owner))
        {
            channel.sendMessage("You can't use that command!");
            return;
        }
        if (arguments.length < 3)
            throw new NotEnoughArgumentsException();

        AnalyzeTask taske = AnalyzeTask.COUNT;
        switch (arguments[1])
        {
            case "count":
                taske = AnalyzeTask.COUNT;
                break;
            case "users":
                taske = AnalyzeTask.UNIQUEUSERS;
                break;
            case "usermessages":
            taske = AnalyzeTask.UNIQUEUSERS_WITH_MESSAGECOUNT;
            break;
            case "words":
                taske = AnalyzeTask.WORDS;
                break;

        }

        Analyzer analyzer = new Analyzer(channel, taske, event.getServer().orElse(null), arguments[2].equalsIgnoreCase("true"));
        analyzer.start();

    }

    private enum AnalyzeTask{
        COUNT, UNIQUEUSERS, UNIQUEUSERS_WITH_MESSAGECOUNT, WORDS
    }

    private class Analyzer extends Thread
    {
        private  TextChannel channel;
        private AnalyzeTask task;
        private Server server;
        private boolean serverWide;
        private long start;
        Analyzer(TextChannel channel, AnalyzeTask task, Server server, boolean serverWide)
        {
            this.task = task;
            this.channel = channel;
            this.server = server;
            this.serverWide = serverWide;

            start = System.currentTimeMillis();
        }
        @Override
        public void run() {

            switch (task) {
                case COUNT:
                    countMessages();
                    break;
                case UNIQUEUSERS:
                    uniqueUsers();
                    break;
            }
            start = System.currentTimeMillis() - start;
            System.out.println("This thread took " + start +" milliseconds to complete.");

        }

        void countMessages()
        {
            channel.sendMessage("Beginning Count");
            long messages = 0;
            if (serverWide)
            {
                List<ServerTextChannel> channelList = server.getTextChannels();

                for (ServerTextChannel textChannel : channelList)
                {
                    System.out.println("Looking at channel " + textChannel.getName());
                    long prior = messages;
                    if (textChannel.canYouReadMessageHistory())
                        messages += textChannel.getMessagesAsStream().count();
                    System.out.println("Found " + (messages-prior) + " messages");

                }
            }
            else
            {
                messages = channel.getMessagesAsStream().count();
            }

            channel.sendMessage("#= " + messages);
        }

        void uniqueUsers()
        {

            channel.sendMessage("Beginning User Count");
            HashSet<User> users;
            if (server != null)
                users = new HashSet<>(server.getMemberCount());
            else
                return;

            if (serverWide)
            {
                List<ServerTextChannel> channelList = server.getTextChannels();

                for (ServerTextChannel textChannel : channelList)
                {

                    System.out.println("Looking at channel " + textChannel.getName());
                    if (textChannel.canYouReadMessageHistory())
                        textChannel.getMessagesAsStream().forEach(message -> message.getUserAuthor().ifPresent(users::add));

                }
            }
            else
            {
                channel.getMessagesAsStream().forEach(message -> message.getUserAuthor().ifPresent(users::add));
            }
            channel.sendMessage("" + users.size() + " unique Users have posted here.\nThat means "
                    + (server.getMemberCount() - users.size()) + " users have never posted!");


        }
    }



}
