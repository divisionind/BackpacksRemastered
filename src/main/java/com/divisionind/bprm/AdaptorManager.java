/*
 * BackpacksRemastered - remastered version of the popular Backpacks plugin
 * Copyright (C) 2019 - 2020, Andrew Howard, <divisionind.com>
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
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class AdaptorManager {

    private final Plugin plugin;
    private final Map<String, PluginAdaptor> pluginAdaptors;
    private final Map<String, PluginAdaptorLoader> pluginAdaptorLoaders;
    private final Map<String, AdaptorAbility> adaptorAbilities;

    public AdaptorManager(Plugin plugin) {
        this.plugin = plugin;
        this.pluginAdaptors = new HashMap<>();
        this.pluginAdaptorLoaders = new HashMap<>();
        this.adaptorAbilities = new HashMap<>();
    }

    public void registerAdaptors(String... adaptors) {
        for (String adaptorName : adaptors) {
            try {
                pluginAdaptorLoaders.put(adaptorName, new PluginAdaptorLoader(adaptorName));
            } catch (ClassNotFoundException e) {
                plugin.getLogger().severe("Failed to find plugin adaptor: " + adaptorName);
                e.printStackTrace();
            }
        }
    }

    public void loadAdaptors() {
        // initialize plugin adaptors
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {

            PluginAdaptorLoader loader = pluginAdaptorLoaders.get(plugin.getName());
            // an adaptor existed
            if (loader != null) {
                // attempt to load adaptor instance
                PluginAdaptor adaptor;
                try {
                    adaptor = loader.load(this);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    plugin.getLogger().severe("Failed to create plugin adaptor instance for: " + plugin.getName());
                    e.printStackTrace();
                    continue;
                }

                // perform setup for the plugin adaptor
                try {
                    adaptor.init(plugin);
                } catch (Exception e) {
                    plugin.getLogger().severe("Failed to load plugin adaptor for: " + plugin.getName());
                    e.printStackTrace();
                    continue;
                }

                // add adaptor to the registry of loaded adaptors
                pluginAdaptors.put(plugin.getName(), adaptor);
            }
        }
    }

    public PluginAdaptor getAdaptor(String name) {
        return pluginAdaptors.get(name);
    }

    public void registerAbility(String name, AdaptorAbility ability) {
        adaptorAbilities.put(name, ability);
    }

    public AdaptorAbility getAbility(String name) {
        return adaptorAbilities.get(name);
    }
}
