package com.untamedears.animals;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.plugin.java.JavaPlugin;

public class ImprovedAnimals extends JavaPlugin {
	private static int rangeX = 10;
	private static int rangeY = 5;
	private static int rangeZ = 10;
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player;
		
		if(sender instanceof Player)
			player = (Player) sender;
		else
			player = null;
		
		if(command.getName().equals("sit")) {
			List<Entity> entities = player.getNearbyEntities(rangeX, rangeY, rangeZ);
			
			for(Entity entity : entities) {
				if(entity instanceof Tameable) {
					Tameable tameable = (Tameable) entity;
					
					if(tameable.getOwner().equals(player)) {
						if(tameable instanceof Wolf) {
							((Wolf) tameable).setSitting(true);
						}
						else if(tameable instanceof Ocelot) {
							((Ocelot) tameable).setSitting(true);
						}
					}
				}
			}
		}
		else if(command.getName().equals("stand")) {
			List<Entity> entities = player.getNearbyEntities(rangeX, rangeY, rangeZ);
			
			for(Entity entity : entities) {
				if(entity instanceof Tameable) {
					Tameable tameable = (Tameable) entity;
					
					if(tameable.getOwner().equals(player)) {
						if(tameable instanceof Wolf) {
							((Wolf) tameable).setSitting(false);
						}
						else if(tameable instanceof Ocelot) {
							((Ocelot) tameable).setSitting(false);
						}
					}
				}
			}
		}
		else if(command.getName().equals("down")) {
			List<Entity> entities = player.getNearbyEntities(rangeX, rangeY, rangeZ);
			
			for(Entity entity : entities) {
				if(entity instanceof Tameable) {
					Tameable tameable = (Tameable) entity;
					
					if(tameable.getOwner().equals(player)) {
						if(tameable instanceof Wolf) {
							((Wolf) tameable).setTarget(null);
						}
						else if(tameable instanceof Ocelot) {
							((Ocelot) tameable).setTarget(null);
						}
					}
				}
			}
		}
		
		return true;
	}
}
