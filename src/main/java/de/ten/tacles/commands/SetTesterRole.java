package de.ten.tacles.commands;


import de.ten.tacles.Main;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class SetTesterRole extends ServerSpecificCommand {
    private String triggerWords[] = {"setTesterRole", "testRole", "addMe"};
    private Role activeTesterRole;
    private ArrayList<Server> toReturn = new ArrayList<>(1);

    public SetTesterRole()
    {
        toReturn.add(Main.testServer);
        activeTesterRole = Main.testServer.getRolesByName("ActiveTesterRole").get(0);
    }

    @Override
    public String[] getTriggerWords() {
        return triggerWords;
    }

    @Override
    public String getDescription() {
        return "Gives or removes the activeTesterRole. All activeTesters will be notified when the bot comes online!\n-Use responsibly. Only available on the test server.";
    }

    @Override
    public String[] getArguments() {
        return new String[] {};
    }

    @Override
    public String getName() {
        return "SetTesterRole";
    }

    @Override
    protected void doCommand(String[] arguments, User user, TextChannel channel, MessageEvent event)
    {
        if (user.getRoles(Main.testServer).contains(activeTesterRole))
        {
            user.removeRole(activeTesterRole);
            channel.sendMessage("I have removed the activeTesterRole from you.");
        }
        else
        {
            user.addRole(activeTesterRole);
            channel.sendMessage("I have added the activeTesterRole to you, I will now notify you when I go online!");
        }
    }


    @Override
    public Collection<Server> getServers() {
        return toReturn;
    }
}
