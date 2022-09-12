/*
 * BackpacksRemastered - remastered version of the popular Backpacks plugin
 * Copyright (C) 2019, Andrew Howard, <divisionind.com>
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

package com.divisionind.bprm.nms.reflect;

import com.divisionind.bprm.exceptions.FuzzyClassLookupException;
import io.netty.util.internal.ConcurrentSet;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.zip.ZipFile;

public class FuzzyClassResolver {

    private final Set<String> bukkitClasses;

    public FuzzyClassResolver() throws FuzzyClassLookupException {
        File serverJarFile;
        ZipFile serverZip;

        try {
            serverJarFile = getServerJar();
        } catch (URISyntaxException e) {
            throw new FuzzyClassLookupException(e);
        }

        // verify the server jar exists
        if (!serverJarFile.exists())
            throw new FuzzyClassLookupException(String.format("Expected MC server jar at: %s but it wasn't there.", serverJarFile.getAbsolutePath()));

        // treat it as a zip and build a map of entries
        try {
            serverZip = new ZipFile(serverJarFile);
        } catch (IOException e) {
            throw new FuzzyClassLookupException("Failed to load server jar.", e);
        }

        bukkitClasses = new ConcurrentSet<>();
        serverZip.stream().parallel().forEach(zipEntry -> {
            String name = zipEntry.getName();

            if (name.endsWith(".class") && !name.contains("$")) {
                name = name.replace('/', '.');
                bukkitClasses.add(name.substring(0, name.length() - 6));
            }
        });

        // cleanup
        try {
            serverZip.close();
        } catch (IOException e) {
            throw new FuzzyClassLookupException("A problem occurred closing the server jar resource.", e);
        }
    }

    private boolean namesMatch(String path, String name) {
        String pathName;

        if (path.contains(".")) {
            String[] parts = path.split("\\.");
            pathName = parts[parts.length - 1];
        } else
            pathName = path;

        return pathName.equals(name);
    }

    /**
     * Finds a class in the MC server jar by name. This method is intended to be used as a fallback if normal class path
     * resolution fails.
     *
     * @param name of the class
     * @return path to the class
     * @throws FuzzyClassLookupException if the class could not be found
     */
    public String lookup(String name) throws FuzzyClassLookupException {
        for (String clazz : bukkitClasses) {
            if (namesMatch(clazz, name))
                return clazz;
        }

        throw new FuzzyClassLookupException(String.format("Failed to find class by name %s. Server has changed too significantly.", name));
    }

    /**
     * Finds a class in the MC server jar by name. This method is intended to be used as a fallback if normal class path
     * resolution fails.
     *
     * @param name of the class
     * @param partialPath tries to find a class with this partial first, then falls-back to just name lookup
     * @return path to the class
     * @throws FuzzyClassLookupException if the class could not be found
     */
    public String lookup(String name, String partialPath) throws FuzzyClassLookupException {
        for (String clazz : bukkitClasses) {
            if (namesMatch(clazz, name) && clazz.contains(partialPath))
                return clazz;
        }

        return lookup(name);
    }

    private static File getServerJar() throws URISyntaxException {
        return new File(Bukkit.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
    }
}
