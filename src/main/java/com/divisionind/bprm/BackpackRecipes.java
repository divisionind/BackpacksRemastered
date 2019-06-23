/*
 * BackpacksRemastered - remastered version of the popular Backpacks plugin
 * Copyright (C) 2019 Division Industries LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.divisionind.bprm;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BackpackRecipes {

    public static void registerRecipes(FileConfiguration config, Logger log) {
        // load backpacks
        for (BackpackItem backpack : BackpackItem.values()) {
            String backpack_name = backpack.name().toLowerCase();
            loadRecipeFromConfig(config, log, backpack_name, backpack.item);
        }

        // create backpack key
        ItemStack backpack_key = new ItemStack(Material.FEATHER);
        ItemMeta backpack_key_meta = backpack_key.getItemMeta();
        backpack_key_meta.setDisplayName(Backpacks.translate("&aBackpack Key"));
        backpack_key.setItemMeta(backpack_key_meta);

        try {
            backpack_key = NMSReflector.setNBTOnce(backpack_key, NBTType.BOOLEAN, "backpack_key", true);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            return;
        }

        loadRecipeFromConfig(config, log, "backpack_key", backpack_key);
    }

    private static void loadRecipeFromConfig(FileConfiguration config, Logger log, String recipeName, ItemStack item) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Backpacks.getInstance(), recipeName), item);

        // ensures section exists in config
        ConfigurationSection section = config.getConfigurationSection(String.format("recipes.%s.ingredients", recipeName));
        if (section == null) {
            log.warning(String.format("The ingredients section for %s did not exist. Could not create recipe.", recipeName));
            return;
        }

        // reads shape from config
        List<String> shapeList = config.getStringList(String.format("recipes.%s.shape", recipeName));
        recipe.shape(shapeList.toArray(new String[shapeList.size()]));

        // parses ingredients from config
        Map<String, Object> ingredients = section.getValues(false);
        ingredients.forEach((k, v) -> {
            Material m = Material.getMaterial((String)v);
            if (m == null) {
                log.warning(String.format("The material %s does not exist. Please use a valid spigot material.", v));
                return;
            }
            recipe.setIngredient(k.charAt(0), m);
        });

        Bukkit.addRecipe(recipe);
    }

    private static ItemStack getBackpack(Color color, int type, String name) {
        ItemStack backpack = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta)backpack.getItemMeta();
        meta.setColor(color);
        meta.setDisplayName(Backpacks.translate(name));
        backpack.setItemMeta(meta);

        // apply backpack_type nbt data
        try {
            return NMSReflector.setNBTOnce(backpack, NBTType.INT, "backpack_type", type);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            return null;
        }
    }

    public enum BackpackItem {

        SMALL(Color.BLACK, 0, "&aSmall Backpack", "backpacks.craft.small"),
        LARGE(Color.MAROON, 1, "&aLarge Backpack", "backpacks.craft.large"),
        LINKED(Color.BLUE, 2, "&aLinked Backpack", "backpacks.craft.linked");

        private ItemStack item;
        private int type_id;
        private String permission;

        BackpackItem(Color color, int type_id, String name, String permission) {
            this.item = getBackpack(color, type_id, name);
            this.type_id = type_id;
            this.permission = permission;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getTypeId() {
            return type_id;
        }

        public boolean hasCraftPermission(HumanEntity entity) {
            return entity.hasPermission(permission);
        }

        public static BackpackItem getById(int id) {
            for (BackpackItem bp : values()) {
                if (bp.type_id == id) return bp;
            }
            return null;
        }
    }
}
