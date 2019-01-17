package de.ten.tacles.commands;


import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageEvent;

import java.util.Random;

public class SecretSanta extends Command {

    private class Pairs{
        String giver;
        String reciever;
        Pairs(String a, String b)
        {
            giver = a;
            reciever = b;
        }
    }



    private String triggerWords[] = {"secret"};

    @Override
    public String[] getTriggerWords() {
        return triggerWords;
    }


    @Override
    public String getDescription() {
        return "Takes a list of participants, and gives you a secret santa arrangement for them.";
    }

    @Override
    public String[] getArguments() {
        return new String[] {"Names: All participants of your exchange, !MULTIPLE!"};
    }

    @Override
    public String getName() {
        return "SecretSanta";
    }

    @Override
    protected void doCommand(String[] arguments, User user, TextChannel channel, MessageEvent event) throws NotEnoughArgumentsException {

        Random rand = new Random();
        //Ich brauche
        if (arguments.length < 2)
            throw new NotEnoughArgumentsException();
        else if (arguments.length < 3) {
            channel.sendMessage("That would be a really boring secret santa.");
            return;
        }

        //Clean list of command name
        //The last position will be a copy of the first position, set later
        String[] participants = new String[arguments.length];

        int numberOfParticipants = participants.length-1;
        System.arraycopy(arguments, 1, participants, 0, numberOfParticipants);

        //Shuffle
        for (int i = 0; i< numberOfParticipants;i++)
        {
            int pos = rand.nextInt(numberOfParticipants-1);
            if (pos >= i)
                pos++;
            String shuffle = participants[pos];
            participants[pos] = participants[i];
            participants[i] = shuffle;
        }

        //Sometimes it happens that in a secret santa there are "groups" that in a row gift to each other.
        //For shits and giggles I simulate them here

        int groupNumber = rand.nextInt( numberOfParticipants/2 -1)+1;

        //The recipient is the following participant
        //Because the list was shuffled, this guarantees nobody can 100% guess who gifts to them,
        //And due to only using 1 list, nobody will gift to themselves

        Pairs[] pairs = new Pairs[numberOfParticipants];

        if (groupNumber > 1)
        {
            System.out.println("Number of groups = " + groupNumber);
            //Generate group lengths
            //The last groupSize is obvious
            int[] groupSizes = new int[groupNumber];
            int sizeAllocated = 0;
            for (int i = 0; i < groupNumber-1;i++)
            {
                //Remaining groups to fill: ->  groupNumber-i-1
                //Remaining size = numberOfParticipants- sizeAllocted
                //Maximum allowed size = (numberOfParticipants-sizeAllocated)-(groupNumber-i-1)*2

                groupSizes[i] = 2 + rand.nextInt( (numberOfParticipants-sizeAllocated)-(groupNumber-i-1)*2);
                sizeAllocated+= groupSizes[i];
                System.out.println("Size of group " + (i+1) + " = " + groupSizes[i]);
            }
            //Not yet allocated stuff goes into the last group (obviously)
            groupSizes[groupNumber-1] = (numberOfParticipants - sizeAllocated);
            System.out.println("Size of group " + groupNumber + " = " +  (numberOfParticipants - sizeAllocated));

            int currentGroup = 0;
            int playersAllocated = 0;
            for (int i = 0; i < numberOfParticipants;i++)
            {
                if (i - playersAllocated+1 == groupSizes[currentGroup])
                {
                    pairs[i] = new Pairs(participants[i], participants[playersAllocated]);
                    playersAllocated+= groupSizes[currentGroup];
                    currentGroup++;
                }
                else
                {
                    pairs[i] = new Pairs(participants[i], participants[i+1]);
                }
            }
        }
        else
        {
            //1 group case
            participants[numberOfParticipants] = participants[0];
            for (int i = 0; i < numberOfParticipants; i++)
            {
                pairs[i] = new Pairs(participants[i], participants[i+1]);
            }

        }

        StringBuilder message = new StringBuilder();
        message.append("The pairings are: \n");
        for (Pairs pair : pairs)
        {
            message.append(pair.giver).append(" gives to ").append(pair.reciever).append("\n");
        }

        channel.sendMessage(message.toString());
    }

}
