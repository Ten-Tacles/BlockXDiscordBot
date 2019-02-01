package de.ten.tacles.commands;

import org.javacord.api.entity.server.Server;

import java.util.Collection;

public abstract class ServerSpecificCommand extends Command
{
    public abstract Collection<Server> getServers();
}
