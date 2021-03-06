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

package ch.njol.skript.effects;

import org.bukkit.Location;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.ItemType;
import ch.njol.skript.util.Offset;

/**
 * 
 * @author Peter Güttinger
 */
public class EffDrop extends Effect {
	
	static {
		Skript.registerEffect(EffDrop.class, "drop %integer% ([e]xp|experience) [orb[s]] [%offsets% %locations%]", "drop %itemtypes% [%offsets% %locations%]");
	}
	
	private Expression<Integer> xp = null;
	private Expression<ItemType> items = null;
	private Expression<Offset> offsets;
	private Expression<Location> locations;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(final Expression<?>[] vars, final int matchedPattern, final int isDelayed, final ParseResult parser) {
		if (matchedPattern == 0)
			xp = (Expression<Integer>) vars[0];
		else
			items = (Expression<ItemType>) vars[0];
		offsets = (Expression<Offset>) vars[1];
		locations = (Expression<Location>) vars[2];
		return true;
	}
	
	@Override
	public void execute(final Event e) {
		final ItemType[] types = items == null ? null : items.getArray(e);
		if (locations.isDefault() && e instanceof EntityDeathEvent && !Delay.isDelayed(e)) {
			if (xp != null) {
				final Integer exp = xp.getSingle(e);
				if (exp != null)
					((EntityDeathEvent) e).setDroppedExp(((EntityDeathEvent) e).getDroppedExp() + exp);
				return;
			}
			for (final ItemType type : types) {
				type.addTo(((EntityDeathEvent) e).getDrops());
			}
			return;
		}
		for (final Location l : Offset.setOff(offsets.getArray(e), locations.getArray(e))) {
			if (xp != null) {
				final Integer exp = xp.getSingle(e);
				if (exp != null) {
					final ExperienceOrb orb = l.getWorld().spawn(l, ExperienceOrb.class);
					orb.setExperience(exp);
				}
				continue;
			}
			for (final ItemType type : types) {
				for (final ItemStack is : type.getItem().getAll()) {
					if (is.getTypeId() != 0)
						l.getWorld().dropItemNaturally(l, is);
				}
			}
		}
	}
	
	@Override
	public String toString(final Event e, final boolean debug) {
		return "drop " + items.toString(e, debug) + " " + offsets.toString(e, debug) + " " + locations.toString(e, debug);
	}
	
}
