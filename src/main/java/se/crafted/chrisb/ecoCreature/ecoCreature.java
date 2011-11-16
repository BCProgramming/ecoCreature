package se.crafted.chrisb.ecoCreature;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.simiancage.DeathTpPlus.DeathTpPlus;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.MobArenaHandler;

import se.crafted.chrisb.ecoCreature.listeners.ecoBlockListener;
import se.crafted.chrisb.ecoCreature.listeners.ecoEntityListener;
import se.crafted.chrisb.ecoCreature.listeners.ecoDeathListener;
import se.crafted.chrisb.ecoCreature.listeners.ecoStreakListener;
import se.crafted.chrisb.ecoCreature.managers.ecoConfigManager;
import se.crafted.chrisb.ecoCreature.managers.ecoMessageManager;
import se.crafted.chrisb.ecoCreature.managers.ecoRewardManager;
import se.crafted.chrisb.ecoCreature.utils.ecoLogger;

public class ecoCreature extends JavaPlugin
{
    public static final File dataFolder = new File("plugins" + File.separator + "ecoCreature");

    public static Permission permission = null;
    public static Economy economy = null;
    public static DeathTpPlus deathTpPlusPlugin = null;
    public static MobArenaHandler mobArenaHandler = null;

    private final ecoBlockListener blockListener = new ecoBlockListener(this);
    private final ecoEntityListener entityListener = new ecoEntityListener(this);
    private final ecoDeathListener rewardListener = new ecoDeathListener(this);

    private ecoLogger logger;
    private ecoMessageManager messageManager;
    private ecoRewardManager rewardManager;
    private ecoConfigManager configManager;

    private Boolean setupDependencies()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
            logger.info("Found permissions provider.");
        }
        else {
            logger.severe("Failed load permission provider.");
        }

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
            logger.info("Economy enabled.");
        }
        else {
            logger.warning("Economy disabled.");
        }

        return (permission != null);
    }

    private void setupDeathTpPlus()
    {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("DeathTpPlus");
        if (plugin != null && plugin instanceof DeathTpPlus) {
            deathTpPlusPlugin = ((DeathTpPlus) plugin);
            logger.info("Successfully hooked " + plugin.getDescription().getName());
        }
    }

    private void setupMobArenaHandler()
    {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("MobArena");
        if (plugin != null && plugin instanceof MobArena) {
            mobArenaHandler = new MobArenaHandler();
            logger.info("Successfully hooked " + plugin.getDescription().getName());
        }
    }

    private void registerEvents()
    {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.CUSTOM_EVENT, rewardListener, Priority.Normal, this);
        if (deathTpPlusPlugin != null) {
            pluginManager.registerEvent(Event.Type.CUSTOM_EVENT, new ecoStreakListener(this), Priority.Normal, this);
        }
    }

    public void onEnable()
    {
        Locale.setDefault(Locale.US);

        logger = new ecoLogger(this);
        messageManager = new ecoMessageManager(this);
        rewardManager = new ecoRewardManager(this);

        try {
            configManager = new ecoConfigManager(this);
            configManager.load();
            if (!configManager.isEnabled()) {
                logger.severe("Edit ecoCreature config to enable the plugin.");
                getPluginLoader().disablePlugin(this);
                return;
            }
        }
        catch (IOException e) {
            logger.severe("Failed to load config: " + e.toString());
            getPluginLoader().disablePlugin(this);
            return;
        }

        setupDependencies();
        setupDeathTpPlus();
        setupMobArenaHandler();
        registerEvents();

        logger.info(getDescription().getVersion() + " enabled.");
    }

    public void onDisable()
    {
        permission = null;
        economy = null;
        configManager = null;
        rewardManager = null;
        messageManager = null;
        logger.info(getDescription().getVersion() + " is disabled.");
    }

    public void onLoad()
    {
        dataFolder.mkdirs();
    }

    public ecoLogger getLogger()
    {
        return logger;
    }

    public ecoMessageManager getMessageManager()
    {
        return messageManager;
    }

    public ecoRewardManager getRewardManager()
    {
        return rewardManager;
    }

    public ecoConfigManager getConfigManager()
    {
        return configManager;
    }

    public Boolean hasEconomy()
    {
        return economy != null;
    }
}