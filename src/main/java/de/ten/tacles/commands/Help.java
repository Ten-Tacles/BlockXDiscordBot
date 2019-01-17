package de.ten.tacles.commands;


import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageEvent;

public class Help extends Command {
    private String triggerWords[] = {"help"};


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
        return new String[] {"Name: Name of the command to see indepth OPTIONAL"};
    }

    @Override
    public String getName() {
        return "Help";
    }

    @Override
    protected void doCommand(String[] arguments, User user, TextChannel channel, MessageEvent event) {


        //General list of commands
        if (arguments.length == 1)
        {
            StringBuilder resultMessage = new StringBuilder();
            resultMessage.append("Here is a list of all my commands:\nWrite \"help COMMANDNDNAME\" " +
                    "if you want too look at a specific command.\n\n");
            resultMessage.append("__GeneralCommands__\n");
            for (Command command: Command.generalCommands)
            {
                resultMessage.append("_");
                resultMessage.append(command.getName());
                resultMessage.append(":_ ");
                resultMessage.append(command.getDescription());
                resultMessage.append("\n");
            }

            resultMessage.append("__BlockXCommands__\n");
            for (Command command: Command.blockXCommands)
            {
                resultMessage.append("_");
                resultMessage.append(command.getName());
                resultMessage.append(":_ ");
                resultMessage.append(command.getDescription());
                resultMessage.append("\n");
            }
            channel.sendMessage(resultMessage.toString());
        }
        //Indepth look at specific command
        else {
            Command wantedCommand = null;
            for (Command command: listOfCommands)
            {
                if (command.getName().equalsIgnoreCase(arguments[1]))
                    wantedCommand = command;
            }

            if (wantedCommand != null)
            {
                StringBuilder resultMessage = new StringBuilder();
                resultMessage.append(wantedCommand.getName());
                resultMessage.append(":\n");
                resultMessage.append(wantedCommand.getDescription());
                resultMessage.append("\nTriggerwords:\n");
                for (String trigger : wantedCommand.getTriggerWords())
                {
                    resultMessage.append(trigger);
                    resultMessage.append(" - ");
                }
                //Delete the last " - "
                resultMessage.delete(resultMessage.length()-3, resultMessage.length());


                if (wantedCommand.getArguments().length > 0)
                {
                    resultMessage.append("\n Arguments: \n");

                    for (String argument : wantedCommand.getArguments())
                    {
                        resultMessage.append(argument);
                        resultMessage.append("\n");
                    }
                }
                channel.sendMessage(resultMessage.toString());
            }
            else
            {
                channel.sendMessage("I don't know that command.");
            }
        }
    }


}