package de.ten.tacles.blockx.DiscordLogic;

import de.ten.tacles.blockx.Spielfeld;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;

public class CustomMap {

    public static ArrayList<CustomMap> listOfCustomMaps = new ArrayList<>();

    private String name;
    private Spielfeld feld;
    private User creator;

    public CustomMap(String name, Spielfeld feld, User creator) {
        this.name = name;
        this.feld = feld;
        this.creator = creator;
        listOfCustomMaps.add(this);
    }
}
