/**
 * Copyright 2014 Tomáš Ligenza
 *
 * This file is part of Firebird Visualization Tool.
 *
 * Firebird Visualization Tool is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * TinyUML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Firebird Visualization Tool; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.tinyuml.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tomáš Ligenza
 */
public class TerminalDebug {
	
	/**
	 * Debug object properties to system terminal
	 * @param o 
	 */
	public static void debug(Object o) {
		
		for (Method method : o.getClass().getDeclaredMethods()) {
			if (Modifier.isPublic(method.getModifiers())
				&& method.getParameterTypes().length == 0
				&& method.getReturnType() != void.class
				&& (method.getName().startsWith("get") || method.getName().startsWith("is"))
			) {
				Object value = new Object();
				try {
					value = method.invoke(o);
				} catch (IllegalAccessException ex) {
					Logger.getLogger(TerminalDebug.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IllegalArgumentException ex) {
					Logger.getLogger(TerminalDebug.class.getName()).log(Level.SEVERE, null, ex);
				} catch (InvocationTargetException ex) {
					Logger.getLogger(TerminalDebug.class.getName()).log(Level.SEVERE, null, ex);
				}
				if (value != null) {
					System.out.println(method.getName() + "=" + value);
				}
			}
		}
	}
}
