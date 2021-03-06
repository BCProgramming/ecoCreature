/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
 * ecoCreature is licensed under the GNU Lesser General Public License.
 *
 * ecoCreature is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ecoCreature is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.crafted.chrisb.ecoCreature.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import se.crafted.chrisb.ecoCreature.rewards.Reward;

public class RewardEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();

    private String player;
    private Reward reward;

    private boolean isCancelled;

    public RewardEvent(Player player, Reward reward)
    {
        this(player.getName(), reward);
    }

    public RewardEvent(String player, Reward reward)
    {
        this.player = player;
        this.reward = reward;
    }

    public Player getPlayer()
    {
        return Bukkit.getPlayer(player);
    }

    public void setPlayer(String player)
    {
        this.player = player;
    }

    public Reward getReward()
    {
        return reward;
    }

    public void setReward(Reward reward)
    {
        this.reward = reward;
    }

    @Override
    public boolean isCancelled()
    {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled)
    {
        this.isCancelled = isCancelled;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
