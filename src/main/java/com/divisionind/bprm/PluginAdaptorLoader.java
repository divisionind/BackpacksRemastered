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

    private static final String ADAPTOR_PREFIX = "com.divisionind.bprm.adapters.Adaptor";

    private final Class<?> adaptorClass;

    /**
     * Creates an adaptor loader using the specified adaptor name.
     * @param adaptorName
     * @throws ClassNotFoundException
     */
    public PluginAdaptorLoader(String adaptorName) throws ClassNotFoundException {
        this(Class.forName(ADAPTOR_PREFIX + adaptorName));
    }

    public PluginAdaptorLoader(Class<?> adaptorClass) {
        this.adaptorClass = adaptorClass;
    }

    /**
     * Creates a new instance of the adaptor using the supplied manager.
     * @param manager
     * @return
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PluginAdaptor load(AdaptorManager manager) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        PluginAdaptor adaptor = (PluginAdaptor) adaptorClass.getConstructor().newInstance();
        adaptor.setManager(manager);
        adaptor.setLoader(this);

        return adaptor;
    }

    /**
     * Gets the class of the actual adaptor implementation.
     * @return
     */
    public Class<?> getAdaptorClass() {
        return adaptorClass;
    }
}
