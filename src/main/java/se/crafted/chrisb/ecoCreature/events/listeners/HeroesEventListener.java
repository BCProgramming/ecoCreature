package se.crafted.chrisb.ecoCreature.events.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;

import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.events.handlers.PluginEventHandler;

public class HeroesEventListener implements Listener
{
    private final PluginEventHandler handler;

    public HeroesEventListener(PluginEventHandler handler)
    {
        this.handler = handler;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHeroChangeLevel(HeroChangeLevelEvent event)
    {
        for (RewardEvent rewardEvent : handler.createRewardEvents(event)) {
            Bukkit.getPluginManager().callEvent(rewardEvent);
        }
    }
}
