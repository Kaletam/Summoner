package com.chrissquared.awesomepants.Summoner;

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
import org.bukkit.entity.CreatureType;

// Other imports
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.croemmich.serverevents.ServerEvents; // TODO: Update the namespace whenever it's updated.
import org.bukkit.croemmich.serverevents.Messages.Type;

// TODO: Implement console command execution.
// TODO: Implement spawn threads - these will probably be anchored to a given Location, around which it'll spawn mobs according to its instructions.
//	Plan for this is, say, setting a region of the world to be an "arena", into which can periodically/continually be spawned [mobs|groups]_of_choice.
// TODO: Implement custom groupings?
// TODO: Further develop ServerEvents hooking/usage. Just rudimentary implementation now, little testing.
// TODO: Further comment on stuff.
// TODO: Investigate some way to do some fun stuff that was discussed.
// TODO: Tutorialize certain parts of this - either move parts to Tutorial, or do extended write ups on some of this.
// TODO: Actually implement JavaDocs.
/**
 * Summoner for Bukkit
 *
 * @author Kaletam
 *
 */
public class Summoner extends JavaPlugin
{
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>(); // Not a clue what this is generated for. *shrug*
    public static final Logger log = Logger.getLogger("Minecraft"); // Get the Minecraft logger for, er, logging purposes.
    private Permissions permissions = null;
    private boolean permissionsEnabled = false; // Indicates whether the Permissions plugin is present and enabled.
    private boolean messagesEnabled = false; // Indicates whether the ServerEvents plugin is present and enabled. TODO: Rename.

    public Summoner(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader)
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

	// Check whether ServerEvents is present.
	this.setupMessages();

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
    // Marginally implemented... this is still a pre-v1.0, after all! - Oh, but, now all Mob types are implemented, and a couple groupings.
    // Lots of optimization still to do, though.
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
	    Random r = new Random();

	    switch (sub)
	    {
		// I'm sure we can combine the animals together, simply using the subcommand to indicate permission, message, and CreatureType. Later.
		// For that matter, I should be able to consolidate all the single mob spawns.
		case COW:
		    if (!hasPermission(p, "summoner.animal.cow"))
		    {
			p.sendMessage("You don't have permission to summon a cow.");

			return true;
		    }

		    // If (self), we're targetting the sending Player. If (!self), we're targetting another Player (well, or the sending player explicitly).
		    tp = (self) ? p : getServer().getPlayer(targetName);

		    l = tp.getLocation();
		    w = tp.getWorld();

		    w.spawnCreature(GenerateRandomLocation(tp, 3, 3), CreatureType.COW);

		    return true;
		case SHEEP:
		    if (!hasPermission(p, "summoner.animal.sheep"))
		    {
			p.sendMessage("You don't have permission to summon a sheep.");

			return true;
		    }

		    // If (self), we're targetting the sending Player. If (!self), we're targetting another Player (well, or the sending player explicitly).
		    tp = (self) ? p : getServer().getPlayer(targetName);

		    l = tp.getLocation();
		    w = tp.getWorld();

		    w.spawnCreature(GenerateRandomLocation(tp, 3, 3), CreatureType.SHEEP);

		    return true;
		case CHICKEN:
		    if (!hasPermission(p, "summoner.animal.chicken"))
		    {
			p.sendMessage("You don't have permission to summon a chicken.");

			return true;
		    }

		    // If (self), we're targetting the sending Player. If (!self), we're targetting another Player (well, or the sending player explicitly).
		    tp = (self) ? p : getServer().getPlayer(targetName);

		    l = tp.getLocation();
		    w = tp.getWorld();

		    w.spawnCreature(GenerateRandomLocation(tp, 3, 3), CreatureType.CHICKEN);

		    return true;
		case PIG:
		    if (!hasPermission(p, "summoner.animal.pig"))
		    {
			p.sendMessage("You don't have permission to summon a pig.");

			return true;
		    }

		    // If (self), we're targetting the sending Player. If (!self), we're targetting another Player (well, or the sending player explicitly).
		    tp = (self) ? p : getServer().getPlayer(targetName);

		    l = tp.getLocation();
		    w = tp.getWorld();

		    w.spawnCreature(GenerateRandomLocation(tp, 3, 3), CreatureType.PIG);

		    return true;
		case SQUID:
		    if (!hasPermission(p, "summoner.animal.squid"))
		    {
			p.sendMessage("You don't have permission to summon a squid.");

			return true;
		    }

		    // If (self), we're targetting the sending Player. If (!self), we're targetting another Player (well, or the sending player explicitly).
		    tp = (self) ? p : getServer().getPlayer(targetName);

		    l = tp.getLocation();
		    w = tp.getWorld();

		    w.spawnCreature(GenerateRandomLocation(tp, 3, 3), CreatureType.SQUID);

		    return true;
		case PIGZOMBIE:
		    if (!hasPermission(p, "summoner.special.pigzombie"))
		    {
			p.sendMessage("You don't have permission to summon a pig zombie.");

			return true;
		    }

		    // If (self), we're targetting the sending Player. If (!self), we're targetting another Player (well, or the sending player explicitly).
		    tp = (self) ? p : getServer().getPlayer(targetName);

		    w = tp.getWorld();

		    w.spawnCreature(GenerateRandomLocation(tp), CreatureType.PIG_ZOMBIE);

		    return true;
		case CREEPER:
		    if (!hasPermission(p, "summoner.monster.creeper"))
		    {
			p.sendMessage("You don't have permission to summon a creeper.");

			return true;
		    }

		    // If (self), we're targetting the sending Player. If (!self), we're targetting another Player (well, or the sending player explicitly).
		    tp = (self) ? p : getServer().getPlayer(targetName);

		    w = tp.getWorld();

		    w.spawnCreature(GenerateRandomLocation(tp), CreatureType.CREEPER);

		    return true;
		case ZOMBIE:
		    if (!hasPermission(p, "summoner.monster.zombie"))
		    {
			p.sendMessage("You don't have permission to summon a zombie.");

			return true;
		    }

		    // If (self), we're targetting the sending Player. If (!self), we're targetting another Player (well, or the sending player explicitly).
		    tp = (self) ? p : getServer().getPlayer(targetName);

		    w = tp.getWorld();

		    w.spawnCreature(GenerateRandomLocation(tp), CreatureType.ZOMBIE);

		    return true;
		case SKELETON:
		    if (!hasPermission(p, "summoner.monster.skeleton"))
		    {
			p.sendMessage("You don't have permission to summon a skeleton.");

			return true;
		    }

		    // If (self), we're targetting the sending Player. If (!self), we're targetting another Player (well, or the sending player explicitly).
		    tp = (self) ? p : getServer().getPlayer(targetName);

		    w = tp.getWorld();

		    w.spawnCreature(GenerateRandomLocation(tp), CreatureType.SKELETON);

		    return true;
		case SPIDER:
		    if (!hasPermission(p, "summoner.monster.spider"))
		    {
			p.sendMessage("You don't have permission to summon a spider.");

			return true;
		    }

		    // If (self), we're targetting the sending Player. If (!self), we're targetting another Player (well, or the sending player explicitly).
		    tp = (self) ? p : getServer().getPlayer(targetName);

		    w = tp.getWorld();

		    w.spawnCreature(GenerateRandomLocation(tp), CreatureType.SPIDER);

		    return true;
		// Ghast spawning doesn't appear to work properly - at least, in my server overworld.
		case GHAST:
		    if (!hasPermission(p, "summoner.monster.ghast"))
		    {
			p.sendMessage("You don't have permission to summon a ghast.");

			return true;
		    }

		    // If (self), we're targetting the sending Player. If (!self), we're targetting another Player (well, or the sending player explicitly).
		    tp = (self) ? p : getServer().getPlayer(targetName);

		    w = tp.getWorld();

		    w.spawnCreature(GenerateRandomLocation(tp), CreatureType.GHAST);

		    return true;
		case CREEPERGEDDON:
		    if (!hasPermission(p, "summoner.group.creepergeddon"))
		    {
			p.sendMessage("You don't have permission to trigger a creeper armageddon.");

			return true;
		    }

		    // If (self), we're targetting the sending Player. If (!self), we're targetting another Player (well, or the sending player explicitly).
		    tp = (self) ? p : getServer().getPlayer(targetName);

		    l = tp.getLocation();
		    w = tp.getWorld();

		    for (int i = 0; i <= 6; i++)
		    {
			// Spawn a creeper!
			w.spawnCreature(GenerateRandomLocation(tp), CreatureType.CREEPER);
		    }

		    if (messagesEnabled)
		    {
			ServerEvents.displayMessage(Type.CUSTOM, "CREEPER-GEDDON!");
		    }
		    else
		    {
			getServer().broadcastMessage("CREEPER-GEDDON!");
		    }

		    return true;
		case UNDEADARMY:
		    if (!hasPermission(p, "summoner.group.undeadarmy"))
		    {
			p.sendMessage("You don't have permission to summon Evil Dead.");

			return true;
		    }

		    // If (self), we're targetting the sending Player. If (!self), we're targetting another Player (well, or the sending player explicitly).
		    tp = (self) ? p : getServer().getPlayer(targetName);

		    l = tp.getLocation();
		    w = tp.getWorld();

		    int numSkel = r.nextInt(2) + 1;
		    int numZomb = r.nextInt(4) + 2;

		    // Skeleton spawn loop.
		    for (int i = 0; i <= numSkel; i++)
		    {
			w.spawnCreature(GenerateRandomLocation(tp), CreatureType.SKELETON);
		    }

		    // Zombie spawn loop.
		    for (int i = 0; i <= numSkel; i++)
		    {
			w.spawnCreature(GenerateRandomLocation(tp), CreatureType.ZOMBIE);
		    }

		    boolean ghast = false;

		    // Ghast spawning doesn't appear to work properly - at least, in my server overworld.
//		    // 5% chance of a Ghast being spawned.
//		    if (r.nextInt(100) > 95)
//		    {
//			ghast = true;
//			w.spawnCreature(GenerateRandomLocation(tp), CreatureType.GHAST);
//		    }

		    if (messagesEnabled)
		    {
			ServerEvents.displayMessage(Type.CUSTOM, "An Evil Army of the Undead has been unleashed upon the world!");

			if (ghast)
			{
			    ServerEvents.displayMessage(Type.CUSTOM, "It is led by a ghast!");
			}
		    }
		    else
		    {
			getServer().broadcastMessage("An Evil Army of the Undead has been unleashed upon the world!");

			if (ghast)
			{
			    getServer().broadcastMessage("It is led by a ghast!");
			}
		    }

		    return true;
		default:
		    return false;
	    }
	}
	else
	{
	    // Don't do anything right now.
	    return false; // Right now, we don't actually succeed or fail, but for the console, let's output usage for testing purposes.
	}
    }

    // Abstract permissions checking!
    // First check whether Permissions are enabled, and then that the Player has the specified permission. If not enabled, check if the Player is an op.
    // TODO: Start using this approach to permissions checking in the other plugins.
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

    public void setupMessages()
    {
	Plugin serverevents = getServer().getPluginManager().getPlugin("ServerEvents");

	if (serverevents != null)
	{
	    if (!serverevents.isEnabled())
	    {
		getServer().getPluginManager().enablePlugin(serverevents);
		serverevents = getServer().getPluginManager().getPlugin("ServerEvents");
	    }
	}

	if (serverevents != null)
	{
	    messagesEnabled = true;
	}
    }

    private Location GenerateRandomLocation(Player p)
    {
	return GenerateRandomLocation(p, 5, 5);
    }

    // Abstract away random Location generation for the command handling routines above.
    // Take a Player as the target location, and generate a Location (x, z) where x and z are both between minDist and (minDist + range) away from the Player's (x, z).
    private Location GenerateRandomLocation(Player p, int minDist, int range)
    {
	Random r = new Random();

	World w = p.getWorld();

	int x = p.getLocation().getBlockX();
	int z = p.getLocation().getBlockZ();
	int dx, dz;

	// Get a (x, z) between minDist and (minDist + range) squares away in each dimension.
	dx = r.nextInt(minDist) + range;
	dz = r.nextInt(minDist) + range;

	// Randomly choose whether the delta is positive or negative.
	dx = dx * ((r.nextBoolean()) ? 1 : -1);
	dz = dz * ((r.nextBoolean()) ? 1 : -1);

	// Apply the deltas.
	x += dx;
	z += dz;

	// Get the highest y coordinate for (x, z).
	int y = w.getHighestBlockYAt(x, z);

	// Create a Location with our calculated coordinates.
	return new Location(w, x, y, z);
    }

    private enum SubCommands
    {
	COW, PIG, CHICKEN, SHEEP, UNDEADARMY, CREEPERGEDDON, CREEPER, PIGZOMBIE, GHAST, ZOMBIE, SKELETON, SPIDER, SQUID
    }
}
