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

import com.divisionind.bprm.nms.NBTType;
import com.divisionind.bprm.nms.NMSReflector;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BackpackRecipes {

    public static ItemStack BACKPACK_KEY;

    public static void registerRecipes(FileConfiguration config, Logger log) {
        // load backpacks
        for (BackpackObject backpack : BackpackObject.values()) {
            String backpack_name = backpack.name().toLowerCase();
            loadRecipeFromConfig(config, log, backpack_name, backpack.getItem());
        }

        // create backpack key
        BACKPACK_KEY = new ItemStack(Material.FEATHER);
        ItemMeta backpack_key_meta = BACKPACK_KEY.getItemMeta();
        backpack_key_meta.setDisplayName(Backpacks.translate("&aBackpack Key"));
        BACKPACK_KEY.setItemMeta(backpack_key_meta);
        try {
            BACKPACK_KEY = NMSReflector.setNBTOnce(BACKPACK_KEY, NBTType.BOOLEAN, "backpack_key", true);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            return;
        }
        loadRecipeFromConfig(config, log, "backpack_key", BACKPACK_KEY);
    }

    private static void loadRecipeFromConfig(FileConfiguration config, Logger log, String recipeName, ItemStack item) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Backpacks.getInstance(), recipeName), item); // to add pre 1.12 version backwards compatibility, change this namespaced key stuff

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
}
