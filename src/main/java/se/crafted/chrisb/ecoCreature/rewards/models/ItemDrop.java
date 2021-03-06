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
package se.crafted.chrisb.ecoCreature.rewards.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.NumberRange;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;

public class ItemDrop extends AbstractItemDrop
{
    public ItemDrop(Material material)
    {
        super(material);
    }

    public static List<AbstractItemDrop> parseConfig(ConfigurationSection config)
    {
        List<AbstractItemDrop> drops = Collections.emptyList();

        if (config != null) {
            if (config.getList("Drops") != null) {
                List<String> dropsList = config.getStringList("Drops");
                drops = parseDrops(dropsList);
            }
            else {
                drops = parseDrops(config.getString("Drops"));
            }
        }

        return drops;
    }

    private static List<AbstractItemDrop> parseDrops(String dropsString)
    {
        List<AbstractItemDrop> drops = Collections.emptyList();

        if (dropsString != null && !dropsString.isEmpty()) {
            drops = parseDrops(Arrays.asList(dropsString.split(";")));
        }

        return drops;
    }

    private static List<AbstractItemDrop> parseDrops(List<String> dropsList)
    {
        List<AbstractItemDrop> drops = Collections.emptyList();

        if (dropsList != null && !dropsList.isEmpty()) {
            drops = new ArrayList<AbstractItemDrop>();

            for (String dropString : dropsList) {
                drops.addAll(parseItem(dropString));
            }
        }

        return drops;
    }

    protected static List<AbstractItemDrop> parseItem(String dropString)
    {
        List<AbstractItemDrop> drops = Collections.emptyList();

        if (parseMaterial(dropString) != null) {
            drops = new ArrayList<AbstractItemDrop>();
            drops.add(populateItemDrop(new ItemDrop(parseMaterial(dropString)), dropString));
        }

        return drops;
    }

    protected static AbstractItemDrop populateItemDrop(AbstractItemDrop drop, String dropString)
    {
        drop.setEnchantments(parseEnchantments(dropString));
        drop.setData(parseData(dropString));
        drop.setDurability(parseDurability(dropString));
        drop.setRange(parseRange(dropString));
        drop.setPercentage(parsePercentage(dropString));

        return drop;
    }

    protected static Material parseMaterial(String dropString)
    {
        String[] dropParts = dropString.split(":");
        String[] itemParts = dropParts[0].split(",");
        String[] itemSubParts = itemParts[0].split("\\.");

        Material material = Material.matchMaterial(itemSubParts[0]);
        if (material == null) {
            LoggerUtil.getInstance().debug("No match for type: " + itemParts[0]);
        }

        return material;
    }

    private static Set<ItemEnchantment> parseEnchantments(String dropString)
    {
        Set<ItemEnchantment> enchantments = Collections.emptySet();
        String[] dropParts = dropString.split(":");
        String[] itemParts = dropParts[0].split(",");

        // check for enchantment
        if (itemParts.length > 1) {
            enchantments = new HashSet<ItemEnchantment>();

            for (int i = 1; i < itemParts.length; i++) {
                String[] enchantParts = itemParts[i].split("\\.");
                // check enchantment level and range
                if (enchantParts.length > 1) {
                    String[] levelRange = enchantParts[1].split("-");
                    int minLevel = Integer.parseInt(levelRange[0]);
                    int maxLevel = levelRange.length > 1 ? Integer.parseInt(levelRange[1]) : minLevel;
                    enchantments.add(createEnchantment(enchantParts[0], minLevel, maxLevel));
                }
                else {
                    enchantments.add(createEnchantment(enchantParts[0], 1, 1));
                }
            }
        }

        return enchantments;
    }

    private static ItemEnchantment createEnchantment(String name, int minLevel, int maxLevel)
    {
        if (name == null || Enchantment.getByName(name.toUpperCase()) == null) {
            throw new IllegalArgumentException("Unrecognized enchantment: " + name);
        }
        ItemEnchantment enchantment = new ItemEnchantment(Enchantment.getByName(name.toUpperCase()));
        enchantment.setLevelRange(new NumberRange(minLevel, maxLevel));

        return enchantment;
    }

    private static Byte parseData(String dropString)
    {
        String[] dropParts = dropString.split(":");
        String[] itemParts = dropParts[0].split(",");
        String[] itemSubParts = itemParts[0].split("\\.");

        return itemSubParts.length > 1 ? Byte.parseByte(itemSubParts[1]) : null;
    }

    private static Short parseDurability(String dropString)
    {
        String[] dropParts = dropString.split(":");
        String[] itemParts = dropParts[0].split(",");
        String[] itemSubParts = itemParts[0].split("\\.");

        return itemSubParts.length > 2 ? Short.parseShort(itemSubParts[2]) : null;
    }

    private static NumberRange parseRange(String dropString)
    {
        String[] dropParts = dropString.split(":");
        String[] amountRange = dropParts[1].split("-");

        int min = 0;
        int max = 0;

        if (amountRange.length == 2) {
            min = Integer.parseInt(amountRange[0]);
            max = Integer.parseInt(amountRange[1]);
        }
        else {
            max = Integer.parseInt(dropParts[1]);
        }

        return new NumberRange(min, max);
    }

    private static double parsePercentage(String dropString)
    {
        String[] dropParts = dropString.split(":");

        return Double.parseDouble(dropParts[2]);
    }
}
