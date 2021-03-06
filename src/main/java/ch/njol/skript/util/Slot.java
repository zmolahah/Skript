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

package ch.njol.skript.util;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Debuggable;

/**
 * @author Peter Güttinger
 */
public class Slot implements Debuggable {
	
	private final Inventory invi;
	private final int index;
	
	public Slot(final Inventory invi, final int index) {
		this.invi = invi;
		this.index = index;
	}
	
	protected Slot(final PlayerInventory inventory) {
		invi = inventory;
		index = -1;
	}
	
	public Inventory getInventory() {
		return invi;
	}
	
	public int getIndex() {
		return index;
	}
	
	public ItemStack getItem() {
		return invi.getItem(index) == null ? new ItemStack(0, 1) : invi.getItem(index).clone();
	}
	
	@SuppressWarnings("deprecation")
	public void setItem(final ItemStack item) {
		invi.setItem(index, item != null && item.getTypeId() != 0 ? item : null);
		if (invi instanceof PlayerInventory) {
			((Player) invi.getHolder()).updateInventory();
		}
	}
	
	@Override
	public String toString() {
		return Skript.toString(getItem());
	}
	
	@Override
	public String toString(final Event e, final boolean debug) {
		if (!debug)
			Skript.toString(getItem());
		if (invi.getHolder() != null)
			return "slot " + index + " of inventory of " + Skript.toString(invi.getHolder());
		return "slot " + index + " of " + invi.toString();
	}
}
