/*
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * Copyright 2011, 2012 Peter Güttinger
 * 
 */

package ch.njol.skript.command;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.log.SubLog;
import ch.njol.skript.util.Utils;

/**
 * Represents an argument of a command
 * 
 * @author Peter Güttinger
 */
public class Argument<T> {
	
	private final Expression<? extends T> def;
	private final Class<T> type;
	private final boolean single;
	private final int index;
	
	private final boolean optional;
	
	private final Map<Event, T[]> current = new WeakHashMap<Event, T[]>();
	
	public Argument(final Expression<? extends T> def, final Class<T> type, final boolean single, final int index, final boolean optional) {
		this.def = def;
		this.type = type;
		this.single = single;
		this.index = index;
		this.optional = optional;
	}
	
	public static <T> Argument<T> newInstance(final Class<T> type, final String def, final int index, final boolean single, final boolean forceOptional) {
		Expression<? extends T> d = null;
		if (def != null) {
			if (def.startsWith("%") && def.endsWith("%")) {
				final Expression<?> e = (Expression<?>) SkriptParser.parse(def.substring(1, def.length() - 1), Skript.getExpressions().iterator(), false, true, "Can't understand the expression '" + def + "'");
				if (e == null)
					return null;
				final SubLog log = SkriptLogger.startSubLog();
				d = e.getConvertedExpression(type);
				SkriptLogger.stopSubLog(log);
				if (d == null) {
					log.printErrors("'" + def + "' is not " + Utils.a(Skript.getExactClassName(type)));
					return null;
				}
			} else {
				final SubLog log = SkriptLogger.startSubLog();
				d = SkriptParser.parseLiteral(def, type, ParseContext.DEFAULT);
				SkriptLogger.stopSubLog(log);
				if (d == null) {
					log.printErrors("'" + def + "' is not " + Utils.a(Skript.getExactClassName(type)));
					return null;
				}
			}
		}
		return new Argument<T>(d, type, single, index, def != null || forceOptional);
	}
	
	@Override
	public String toString() {
		return "<" + Utils.toPlural(Skript.getExactClassName(type), !single) + (def == null ? "" : " = " + def.toString()) + ">";
	}
	
	public boolean isOptional() {
		return optional;
	}
	
	public void setToDefault(final SkriptCommandEvent event) {
		if (def != null)
			current.put(event, def.getArray(event));
	}
	
	@SuppressWarnings("unchecked")
	public void set(final SkriptCommandEvent e, final Object[] o) {
		if (o == null || !(type.isAssignableFrom(o.getClass().getComponentType())))
			throw new IllegalArgumentException();
		current.put(e, (T[]) o);
	}
	
	public T[] getCurrent(final Event e) {
		return current.get(e);
	}
	
	public Class<T> getType() {
		return type;
	}
	
	public int getIndex() {
		return index;
	}
	
	public boolean isSingle() {
		return single;
	}
	
}
