package de.ten.tacles.commands.blockXRelated;


import de.ten.tacles.commands.Command;
import de.ten.tacles.commands.NotEnoughArgumentsException;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class Rules extends Command {

    private File files[];

    public Rules()
    {
        try
        {
            files = new File[7];
            files[0] = new File(getClass().getResource("/TutorialGraphics/KernFeld.png").toURI());
            files[1] = new File(getClass().getResource("/TutorialGraphics/SpielerFeld.png").toURI());
            files[2] = new File(getClass().getResource("/TutorialGraphics/LeereFelder.png").toURI());
            files[3] = new File(getClass().getResource("/TutorialGraphics/BlockierteFelder.png").toURI());
            files[4] = new File(getClass().getResource("/TutorialGraphics/WandFelder.png").toURI());
            files[5] = new File(getClass().getResource("/TutorialGraphics/Teleporter.png").toURI());
            files[6] = new File(getClass().getResource("/TutorialGraphics/Bomben.png").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();

        }


    }


    private String[] triggerWords =  new String[] {"rules", "tutorial"};
    @Override

    public String[] getTriggerWords() {
        return triggerWords;
    }

    @Override
    public String getDescription() {
        return "Tells you how to play this game.";
    }

    @Override
    public String[] getArguments() {
        return new String[] {};
    }

    @Override
    public String getName() {
        return "Rules";
    }

    @Override
    protected void doCommand(String[] arguments, User user, TextChannel channel, MessageEvent event) throws NotEnoughArgumentsException {

        if (arguments.length < 1)
            throw new NotEnoughArgumentsException();

        RulesThread rulesThread = new RulesThread(user);
        rulesThread.run();
    }

    private class RulesThread extends Thread
    {
        User user;
        public RulesThread(User user)
        {
            this.user = user;
        }

        @Override
        public void run()
        {
            try {
                sendGeneralRules().get();
                sendCoreRules().get();
                sendPlayerRules().get();
                sendEmptyRules().get();
                sendBrokenRules().get();
                sendWallRules().get();
                sendTeleporterRules().get();
                sendBomvRules().get();

                user.sendMessage("Now that you know the rules, use *createGame* to start a new game you can play with your friends!");
            }
            catch (ExecutionException | InterruptedException e)
            {
                user.sendMessage("Something interrupted my tutorial! Sorry :C");
            }
        }

        public CompletableFuture<Message> sendGeneralRules()
        {
            return user.sendMessage("The goal of the game is simple, simply control the last player that can move.\nTo be that player, " +
                    "you have to *register* it to yourself first." +
                    "\nUsing the command *placeTurn* you can place down the moves your player shall take.  You can place them right next to the tiles of your player, or next to " +
                    "moves you placed this turn, thus moving over tiles!  This means the more moves you have, the further you can go each turn!\n" +
                    "If you are surprised your turn didn't do anything, don't be. Only once all moves for all players have been placed, they all will be evaluated at once!\n" +
                    "The actual effect of each move depends on the specific tiles they were placed on. Experiment a little!\n");
        }
        public CompletableFuture<Message> sendCoreRules()
        {
            return user.sendMessage("This is a **Core Tile**. They are the ones that **grant a player their moves**, " +
                            "the more a player controls, the more turns they have, though they have diminishing returns.\n" +
                            "This core is controlled by the player Red, he does so, by touching it with his own (also red) tiles.\n" +
                            "When you place a move on a normal core, it will get **charged**! A charged core grants the same moves as a normal core " +
                            "though if you move onto a charged core, you will get an extra move, instead of losing one! It will also turn back into a normal core.\n"
                    , files[0]);
        }

        public CompletableFuture<Message> sendPlayerRules()
        {
            return user.sendMessage("These are the **Player Tiles**. They come in two flavours (left and right side) and in each colour.\n" +
                    "The most common is the normal player tile (left), which simply **grants access to the adjacent tiles** (but not itself!.\n" +
                    "The other one is the armored player tile, which does the same things as the normal one, BUT also **prevents other players from moving past it**!\n" +
                    "If you place moves on enemy player tiles, you attack them, " +
                    "a normal player tile will break, but an armored one will be downgraded. Attack that one twice to break it!" +
                    "However, if you move on your own tiles, you defend them from enemies! " +
                    "Each move neutralizes one enemy move, and if defended too much, your normal tile is upgraded!\n", files[1]);
        }

        public CompletableFuture<Message> sendEmptyRules()
        {
            return  user.sendMessage("The most common tiles in the game are **Empty Tiles**. " +
                    "Generally you will see the white variant, but occacionally you see coloured ones too.\n" +
                    "If you place a move onto it, it will **turn into a player tile of your colour**, place two, and it's an armored one!\n" +
                    "Though beware, if other player also attack it, only the **attacker with the most attacks** onto it will get it! " +
                    "And if nobody has the upperhand, the **tile will break**!\n" +
                    "Coloured empty tiles behave the same, but the corresponding player can **move once onto it** over any distance! " +
                    "This move will not grant any adjacency, however.\n", files[2]);
        }

        public CompletableFuture<Message> sendBrokenRules()
        {
            return user.sendMessage("I mentioned breaking a tile a few times, and here are these **Brocken Tiles**. \n" +
                            "Breaking a tile will produce the first one in the list, though " +
                            "they all react the same way to your moves, namely **not at all**. You cannot take over a broken tile, or repair it.\n" +
                            "The two on the left will turn into the one to their right at the end of each turn (after moves are placed)" +
                            " and will also prevent a **player from controlling a core tile**.\n" +
                            "The third one will turn into an empty tile, and not block cores. The last one is permanently blocked, and won't change!\n"
                    , files[3]);
        }
        public CompletableFuture<Message> sendWallRules()
        {
            return user.sendMessage("Rounding out the normal tile set, are **Wall Tiles**.\n" +
                            "They are very simple, you just can't move onto them. That's all there is. " +
                            "The differences between these variants are only decorative.\n" +
                            "The border around the playing field is also made of one them, but same rules still apply."
                    , files[4]);
        }
        public CompletableFuture<Message> sendTeleporterRules()
        {
            return user.sendMessage("This is a **Teleporter Tile**. There are a few more variants with different colours.\n" +
                            "When you move on a teleporter, you **can place moves next to all other teleporter tiles of the same colour**! " +
                            "Use these to cover ground quickly! \n" +
                            "The teleporter themselves aren't affected by any moves placed on them, though.\n"
                    , files[5]);
        }
        public CompletableFuture<Message> sendBomvRules()
        {
            return user.sendMessage("Lastly, we have **Bomb Tiles**. There are a few more variants with different colours.\n" +
                            "Bombs are associated with a tile type, from left to right: **Empty Tiles, Player Tiles, Broken Tiles**. " +
                            "Bombs have also two ways of interaction, activation and explosion.\n" +
                            "A bomb is activated if only one player moved onto it. It will then \"attack\" the eight surrounding tiles " +
                            "with the associated type.\n" +
                            "It will explode however, if two or more moves have been placed on it. Then it will \n" +
                            "replace all surrounding tiles and itself with the target type, regardless of what they were prior.\n"
                    , files[6]);
        }
    }
}
