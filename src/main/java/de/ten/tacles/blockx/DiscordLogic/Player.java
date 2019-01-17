package de.ten.tacles.blockx.DiscordLogic;

import org.javacord.api.entity.user.User;
import org.javacord.core.entity.user.UserImpl;

import java.util.ArrayList;


/**
 * This class holds the User participating in a game of BlockX, as well as all which positions the user has access to.
 *
 */
public class Player {

    private User user;
    private ArrayList<Integer> positions = new ArrayList<>();

    public Player (User user, int[] positions )
    {
        this.user = user;

        for (int position : positions) {
            this.positions.add(position);
        }
    }


    public User getUser() {
        return user;
    }

    public int[] getPositions()
    {
        int[] toReturn = new int[this.positions.size()];

        for (int i = 0; i < toReturn.length; i++)
        {
            toReturn[i] = this.positions.get(i);
        }
        return toReturn;
    }

    public boolean hasPosition(int position)
    {
        return positions.contains(position);
    }

    public void addPosition(int position)
    {
        if (!positions.contains(position))
        {
            positions.add(position);
        }
    }

    public void removePosition(int position)
    {
        positions.remove(position);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        else if (obj == this)
            return true;
        else if (obj.getClass() == Player.class)
        {
            Player player = (Player) obj;
            return player.getUser().equals(this.user);
        }
        return false;
    }
}
