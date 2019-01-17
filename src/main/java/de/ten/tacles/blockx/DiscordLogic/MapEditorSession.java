package de.ten.tacles.blockx.DiscordLogic;


import de.ten.tacles.blockx.Tile;
import de.ten.tacles.blockx.Spielfeld;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;

public class MapEditorSession
{
    private static ArrayList<MapEditorSession> listOfActiveEditorSessions = new ArrayList<>();

    private User user;
    private Spielfeld currentMap;

    public MapEditorSession(User user, int width, int height, int playerCount)
    {
        this.user = user;

        currentMap = new Spielfeld(width, height, playerCount);
        listOfActiveEditorSessions.add(this);
    }

    public boolean setTile(int x, int y, int type, int variant)
    {
        if (!Tile.isValidTile(type,variant, currentMap.getPlayerCount()))
            return false;
        currentMap.setTileType(x, y, type, variant);
        return true;
    }

    public boolean setTileType(int x, int y, int type)
    {
        if (!Tile.isValidTile(type,currentMap.getTileVariation(x,y), currentMap.getPlayerCount()))
            return false;

        currentMap.setTileType(x,y, type);
        return true;
    }

    public boolean setTileVariant(int x, int y, int variant)
    {
        if (!Tile.isValidTile(currentMap.getTileType(x,y),variant, currentMap.getPlayerCount()))
            return false;

        currentMap.setTileVariant(x,y, variant);
        return true;
    }

    public void setTileBorder(int x, int y, int borders)
    {
        currentMap.setTileBorder(x,y, borders);
    }

    public void dropSession()
    {
        //Go commit garbagecollection
        listOfActiveEditorSessions.remove(this);
    }


    public static boolean userHasSession (User user)
    {
        for (MapEditorSession session : listOfActiveEditorSessions) {
             if (session.getUser() == user)
                return true;
        }
        return false;
    }

    public static MapEditorSession getSessionByUser(User user)
    {
        for (MapEditorSession session :
                listOfActiveEditorSessions) {
            if (session.getUser() == user)
                return session;

        }
        return null;
    }

    public User getUser() {
        return user;
    }
}
