package de.ten.tacles.blockx.DiscordLogic;

public class MapReadException extends Exception{
    public MapReadProblem problem;

    public MapReadException(MapReadProblem problem)
    {
        this.problem = problem;
    }
}
