package org.bukkit.awesomepants.Spawner;

// Java imports
import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

// org.bukkit imports
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.Server;
import org.bukkit.Location;
import org.bukkit.World;

// Other imports
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.entity.CreatureType;

// TODO: Add more quotes.
// TODO: Add support for text file and/or SQL DB storage of quotes.
// TODO: Distinguish between a quote and its author?
// TODO: Implement console command execution.

/**
 * QuoteGen for Bukkit
 *
 * @author Kaletam
 * Some fun with pull requests, part two
 */
public class Spawner extends JavaPlugin
{
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>(); // Not a clue what this is generated for. *shrug*
    public static final Logger log = Logger.getLogger("Minecraft"); // Get the Minecraft logger for, er, logging purposes.
    private Permissions permissions = null;
    private boolean permissionsEnabled = false;

    public Spawner(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader)
    {
	super(pluginLoader, instance, desc, folder, plugin, cLoader);

	// Plugin generator tells us this. Dunno, but we'll keep it for reference.
	// NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
    }

    @Override
    public void onEnable()
    {
	// Register our events
	PluginManager pm = getServer().getPluginManager();

	// Load Permissions settings.
	this.setupPermissions();

	PluginDescriptionFile pdfFile = this.getDescription();
	log.log(Level.INFO, String.format("[%s] version [%s] enabled.", pdfFile.getName(), pdfFile.getVersion()));
    }

    @Override
    public void onDisable()
    {
	PluginDescriptionFile pdfFile = this.getDescription();
	log.log(Level.INFO, String.format("[%s] version [%s] signing off!", pdfFile.getName(), pdfFile.getVersion()));
    }

    public boolean isDebugging(final Player player)
    {
	if (debugees.containsKey(player))
	{
	    return debugees.get(player);
	}
	else
	{
	    return false;
	}
    }

    public void setDebugging(final Player player, final boolean value)
    {
	debugees.put(player, value);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
    {
	// This *should* be superfluous, since we're only registering /quotegen and /qg, but we'll keep it for now.
	String commandName = command.getName().toLowerCase();

	if ((sender instanceof Player)) // If executed by the player.
	{
	    Player p = (Player) sender;
	    Location l = p.getLocation();
	    World w = p.getWorld();

	    w.spawnCreature(l, CreatureType.COW);

//	    // This method for getting subcommands stolen from HotSwap.
//	    SubCommands sub = null;
//
//	    try
//	    {
//		sub = SubCommands.valueOf(args[0].toUpperCase());
//	    }
//	    catch (Exception ex) // Don't actually do anything, just return false (triggering display of usage as per plugin.yml).
//	    {
//		return false;
//	    }
//
//	    Random r = new Random();
//
//	    int qi = r.nextInt(quotes.length);
//
//	    String targetName = "";
//
//	    if (args.length > 1)
//	    {
//		targetName = args[1];
//	    }
//
//	    String quote = "This space for rent.";
//
//	    // This was for testing. Shouldn't need it, but I'm not taking it out right now.
//	    try
//	    {
//		quote = this.quotes[qi];
//	    }
//	    catch (Exception ex)
//	    {
//		log.log(Level.WARNING, String.format("[%s] threw an exception: %s.", this.getDescription().getName(), ex.getMessage()));
//	    }
//
//	    switch (sub)
//	    {
//		case QUOTE:
//		    p.sendMessage(quote);
//
//		    return true;
//		case SEND:
//		    // This all *should* mean that a player without permissions, or lacking Permissions, without OP privileges, should not be able to run this subcommand.
//		    if (this.permissionsEnabled && !this.permissions.Security.permission(p, "qg.send"))
//		    {
//			p.sendMessage("You don't have permission to send a quote.");
//
//			return true;
//		    }
//		    else if (!this.permissionsEnabled)
//		    {
//			if (!p.isOp())
//			{
//			    p.sendMessage("You don't have permission to send a quote.");
//
//			    return true;
//			}
//		    }
//
//		    if (targetName == null ? "" != null : !targetName.equals(""))
//		    {
//			Player target = getServer().getPlayer(targetName);
//
//			if (target == null)
//			{
//			    p.sendMessage("Could not find a player by the name of " + targetName + ".");
//			}
//			else
//			{
//			    target.sendMessage(p.getName() + " has sent you a quote.");
//			    target.sendMessage(quote);
//			}
//
//			return true;
//		    }
//		    else
//		    {
//			p.sendMessage("Must indicate a player name!");
//
//			return true;
//		    }
//		case BROADCAST:
//		    // This all *should* mean that a player without permissions, or lacking Permissions, without OP privileges, should not be able to run this subcommand.
//		    if (this.permissionsEnabled && !this.permissions.Security.permission(p, "qg.broadcast"))
//		    {
//			p.sendMessage("You don't have permission to broadcast a quote.");
//
//			return true;
//		    }
//		    else if (!this.permissionsEnabled)
//		    {
//			if (!p.isOp())
//			{
//			    p.sendMessage("You don't have permission to broadcast a quote.");
//
//			    return true;
//			}
//		    }
//
//		    getServer().broadcastMessage(p.getName() + " has sent a broadcast quote.");
//		    int players = getServer().broadcastMessage(quote);
//		    p.sendMessage("You have sent a quote to " + players + " players.");
//
//		    return true;
//		default:
//		    return false;
//	    }
//	}
//	else // TODO: Figure out if !(sender instanceof Player) implies a console executed command.
//	{
//	    // Don't do anything right now.
//	    //log.log(Level.INFO, "We're in onCommand, !(sender instanceof Player).");
//	    //System.out.println("Console test!");
//	    return false; // Right now, we don't actually succeed or fail, but for the console, let's output usage for testing purposes.
	}

	return false;
    }

    public void setupPermissions()
    {
	Plugin p = this.getServer().getPluginManager().getPlugin("Permissions");

	if (this.permissions == null)
	{
	    if (p != null)
	    {
		this.permissions = (Permissions) p;
		this.permissionsEnabled = true;
	    }
	}
    }

    private enum SubCommands
    {
	NULL
    }
}