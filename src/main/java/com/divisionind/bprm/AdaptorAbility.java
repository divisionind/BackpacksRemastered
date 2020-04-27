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

import com.divisionind.bprm.exceptions.AdaptorAbilityCallException;
import com.divisionind.bprm.exceptions.InvalidAdaptorAbilityException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AdaptorAbility {

    private final PluginAdaptor pluginAdaptor;
    private final Method ability;

    public AdaptorAbility(PluginAdaptor pluginAdaptor, String method, Class<?>... params) throws NoSuchMethodException, InvalidAdaptorAbilityException {
        this.pluginAdaptor = pluginAdaptor;
        this.ability = pluginAdaptor.getLoader().getAdaptorClass().getDeclaredMethod(method, params);

        if (!this.ability.isAnnotationPresent(AdaptorAbilityAction.class)) {
            throw new InvalidAdaptorAbilityException("The supplied adaptor ability does not have the correct annotations.");
        }
    }

    public Object call(Object... params) {
        try {
            return ability.invoke(pluginAdaptor, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AdaptorAbilityCallException();
        }
    }

    public <T> T call(Class<T> returnType, Object... params) {
        return (T) call(params);
    }
}
