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

import com.divisionind.bprm.exceptions.InvalidAdaptorException;

import java.lang.reflect.InvocationTargetException;

public class PluginAdaptorLoader {

    private final Class<? extends PluginAdaptor> adaptorClass;

    /**
     * Creates an adaptor loader using the specified adapter class.
     * @param adaptorClass
     * @throws InvalidAdaptorException
     */
    public PluginAdaptorLoader(Class<? extends PluginAdaptor> adaptorClass) throws InvalidAdaptorException {
        this.adaptorClass = adaptorClass;

        if (!adaptorClass.isAnnotationPresent(PluginAdaptorMeta.class)) {
            throw new InvalidAdaptorException(
                    String.format("The class \"%s\" does not have the @PluginAdaptorMeta annotation but requires it.",
                            adaptorClass.getName()));
        }
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
    public PluginAdaptor load(AdaptorManager manager)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        PluginAdaptor adaptor = adaptorClass.getConstructor().newInstance();
        adaptor.init(manager, this);

        return adaptor;
    }

    /**
     * Gets the class of the actual adaptor implementation.
     * @return
     */
    public Class<? extends PluginAdaptor> getAdaptorClass() {
        return adaptorClass;
    }

    public PluginAdaptorMeta getMeta() {
        return adaptorClass.getAnnotation(PluginAdaptorMeta.class);
    }
}
