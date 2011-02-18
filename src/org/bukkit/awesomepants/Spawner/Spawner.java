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

    // So...
    // /spawn subcommand [target=(Player) sender]
    // So in all cases, we just default the the executing Player (well, presuming the command is from a Player, but I'm slacking and not implementing console commands yet).
    // But if a target is specified, we check to see if the target is valid.
    // For now, only accept player names as targets. Eventually add in support for Location-s.
    // Marginally implemented... this is still a pre-v1.0, after all!
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
    {
	// This *should* be superfluous, since we're only registering /quotegen and /qg, but we'll keep it for now.
	String commandName = command.getName().toLowerCase();

	if ((sender instanceof Player)) // If executed by the player.
	{
	    Player p = (Player) sender;
	    boolean self = true;

	    SubCommands sub = null;

	    try
	    {
		sub = SubCommands.valueOf(args[0].toUpperCase());
	    }
	    catch (Exception ex) // Don't actually do anything, just return false (triggering display of usage as per plugin.yml).
	    {
		return false;
	    }

	    String targetName = "";

	    if (args.length > 1)
	    {
		targetName = args[1];
	    }

	    // If the targetName isn't empty, we're being told to target a Player. So verify that the name is that of an actual Player.
	    if (!targetName.equals(""))
	    {
		self = false;

		if (getServer().getPlayer(targetName) == null)
		{
		    p.sendMessage(String.format("%s is not a valid player.", targetName));

		    return true;
		}
	    }

	    Player tp;
	    Location l;
	    World w;

	    switch (sub)
	    {
		case MOO:
		    // We can simplify this method of permissions checking. Later.
		    if (this.permissionsEnabled && !this.permissions.Security.permission(p, "spawner.cow"))
		    {
			p.sendMessage("You don't have permission to spawn a cow.");

			return true;
		    }
		    else if (!this.permissionsEnabled)
		    {
			if (!p.isOp())
			{
			    p.sendMessage("You don't have permission to spawn a cow.");

			    return true;
			}
		    }

		    // If (self), we're targetting the sending Player. If (!self), we're targetting another Player (well, or the sending player explicitly).
		    tp = (self) ? p : getServer().getPlayer(targetName);

		    l = tp.getLocation();
		    w = tp.getWorld();

		    w.spawnCreature(l, CreatureType.COW);

		    return true;
		case OINKBRAINS:
		    // We can simplify this method of permissions checking. Later.
		    if (this.permissionsEnabled && !this.permissions.Security.permission(p, "spawner.pigzombie"))
		    {
			p.sendMessage("You don't have permission to spawn a pig zombie.");

			return true;
		    }
		    else if (!this.permissionsEnabled)
		    {
			if (!p.isOp())
			{
			    p.sendMessage("You don't have permission to spawn a pig zombie.");

			    return true;
			}
		    }

		    // If (self), we're targetting the sending Player. If (!self), we're targetting another Player (well, or the sending player explicitly).
		    tp = (self) ? p : getServer().getPlayer(targetName);

		    l = tp.getLocation();
		    w = tp.getWorld();

		    w.spawnCreature(l, CreatureType.PIG_ZOMBIE);

		    return true;
		case CREEPER:
		    // We can simplify this method of permissions checking. Later.
		    if (this.permissionsEnabled && !this.permissions.Security.permission(p, "spawner.creeper"))
		    {
			p.sendMessage("You don't have permission to spawn a creeper.");

			return true;
		    }
		    else if (!this.permissionsEnabled)
		    {
			if (!p.isOp())
			{
			    p.sendMessage("You don't have permission to spawn a creeper.");

			    return true;
			}
		    }

		    // If (self), we're targetting the sending Player. If (!self), we're targetting another Player (well, or the sending player explicitly).
		    tp = (self) ? p : getServer().getPlayer(targetName);

		    l = tp.getLocation();
		    w = tp.getWorld();

		    w.spawnCreature(l, CreatureType.CREEPER);

		    return true;
		case CREEPERGEDDON:
		    if (!hasPermission(p, "spawner.creepergeddon"))
		    {
			p.sendMessage("You don't have permission to trigger a creeper armageddon.");

			return true;
		    }

		    // If (self), we're targetting the sending Player. If (!self), we're targetting another Player (well, or the sending player explicitly).
		    tp = (self) ? p : getServer().getPlayer(targetName);

		    l = tp.getLocation();
		    w = tp.getWorld();

		    Random r = new Random();

		    for (int i = 0; i <= 6; i++)
		    {
			int x = tp.getLocation().getBlockX();
			int z = tp.getLocation().getBlockZ();

			x = x + (r.nextInt(10) - 5);
			z = z + (r.nextInt(10) - 5);
			int y = w.getHighestBlockYAt(x, z);

			Location l2 = new Location(w, x, y, z);

			w.spawnCreature(l2, CreatureType.CREEPER);
		    }

		    return true;
		default:
		    return false;
	    }
	}
	else // TODO: Figure out if !(sender instanceof Player) implies a console executed command.
	{
	    // Don't do anything right now.
	    //log.log(Level.INFO, "We're in onCommand, !(sender instanceof Player).");
	    //System.out.println("Console test!");
	    return false; // Right now, we don't actually succeed or fail, but for the console, let's output usage for testing purposes.
	}
    }

    // Abstract permissions checking!
    private boolean hasPermission(Player p, String perm)
    {
	if (this.permissionsEnabled && this.permissions.Security.permission(p, perm))
	{
	    return true;
	}
	else if (!this.permissionsEnabled)
	{
	    if (p.isOp())
	    {
		return true;
	    }
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

    // Not all implemented.
    private enum SubCommands
    {
	MOO, UNDEADARMY, CREEPERGEDDON, CREEPER, OINKBRAINS, GHAST
    }
}
