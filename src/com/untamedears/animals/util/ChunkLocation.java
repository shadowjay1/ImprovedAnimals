package com.untamedears.animals.util;

import org.bukkit.Chunk;

public final class ChunkLocation {
	private final int x;
	private final int z;
	
	public ChunkLocation(int x, int z) {
		this.x = x;
		this.z = z;
	}
	
	public ChunkLocation(Chunk c) {
		this.x = c.getX();
		this.z = c.getZ();
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return z;
	}
	
	@Override
	public int hashCode() {
		return x * 37 + z;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof ChunkLocation) {
			ChunkLocation other = (ChunkLocation) o;
			
			return this.x == other.x && this.z == other.z;
		}
		
		return false;
	}
}
