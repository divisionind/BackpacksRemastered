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

import java.lang.reflect.InvocationTargetException;

public class PluginAdaptorLoader {

    private final Class<?> adaptorClass;

    public PluginAdaptorLoader(String adaptorName) throws ClassNotFoundException {
        this.adaptorClass = Class.forName("com.divisionind.bprm.adapters.Adaptor" + adaptorName);
    }

    public PluginAdaptor load(AdaptorManager manager) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return (PluginAdaptor) adaptorClass
                .getConstructor(AdaptorManager.class, PluginAdaptorLoader.class)
                .newInstance(manager, this);
    }

    public Class<?> getAdaptorClass() {
        return adaptorClass;
    }
}
