package com.untamedears.animals;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.plugin.java.JavaPlugin;

import com.untamedears.animals.util.BitUtils;

public class ImprovedAnimals extends JavaPlugin {
	public static ImprovedAnimals instance;
	
	private EntityDatabase entityDatabase;
	private EntityManager entityManager;
	
	private static int rangeX = 10;
	private static int rangeY = 5;
	private static int rangeZ = 10;
	
	public void onLoad() {
		instance = this;
	}
	
	public void onEnable() {
		String databaseIp = this.getConfig().getString("database.ip", "localhost");
		int databasePort = this.getConfig().getInt("database.port", 3306);
		String databaseName = this.getConfig().getString("database.name", "improved_animals");
		String databaseUser = this.getConfig().getString("database.user", "bukkit");
		String databasePassword = this.getConfig().getString("database.password", "");
		boolean databaseEnabled = this.getConfig().getBoolean("database.enabled", false);
		int untameMaterial = this.getConfig().getInt("untame.material", Material.COOKIE.getId());
		double untameChance = this.getConfig().getDouble("untame.chance", 0.2);
		rangeX = this.getConfig().getInt("range.x", 10);
		rangeY = this.getConfig().getInt("range.y", 5);
		rangeZ = this.getConfig().getInt("range.z", 10);
		
		this.getConfig().set("database.ip", databaseIp);
		this.getConfig().set("database.port", databasePort);
		this.getConfig().set("database.name", databaseName);
		this.getConfig().set("database.user", databaseUser);
		this.getConfig().set("database.password", databasePassword);
		this.getConfig().set("database.enabled", databaseEnabled);
		this.getConfig().set("untame.material", untameMaterial);
		this.getConfig().set("untame.chance", untameChance);
		this.getConfig().set("range.x", rangeX);
		this.getConfig().set("range.y", rangeY);
		this.getConfig().set("range.z", rangeZ);
		
		this.saveConfig();
		
		if(!databaseEnabled) {
			this.getLogger().severe("ImprovedAnimals requires the database to be enabled to function!");
			
			return;
		}
		
		String url = String.format("jdbc:mysql://%s:%d/%s", databaseIp, databasePort, databaseName);
		
		entityDatabase = new EntityDatabase(url, databaseUser, databasePassword);
		entityManager = new EntityManager(entityDatabase);
		
		for(World w : Bukkit.getWorlds()) {
			for(Chunk c : w.getLoadedChunks()) {
				entityManager.loadChunk(c);
			}
		}
		
		Bukkit.getPluginManager().registerEvents(new AnimalListener(), this);
	}
	
	public void onDisable() {
		for(World w : Bukkit.getWorlds()) {
			for(Chunk c : w.getLoadedChunks()) {
				entityManager.unloadChunk(c);
			}
		}
	}
	
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
					
					if(tameable.isTamed() && tameable.getOwner().equals(player)) {
						if(tameable instanceof Wolf) {
							((Wolf) tameable).setSitting(true);
						}
						else if(tameable instanceof Ocelot) {
							((Ocelot) tameable).setSitting(true);
						}
					}
				}
			}
			
			sender.sendMessage(ChatColor.YELLOW + "Nearby wolves have been ordered to sit.");
		}
		else if(command.getName().equals("stand")) {
			List<Entity> entities = player.getNearbyEntities(rangeX, rangeY, rangeZ);
			
			for(Entity entity : entities) {
				if(entity instanceof Tameable) {
					Tameable tameable = (Tameable) entity;
					
					if(tameable.isTamed() && tameable.getOwner().equals(player)) {
						if(tameable instanceof Wolf) {
							((Wolf) tameable).setSitting(false);
						}
						else if(tameable instanceof Ocelot) {
							((Ocelot) tameable).setSitting(false);
						}
					}
				}
			}
			
			sender.sendMessage(ChatColor.YELLOW + "Nearby pets have been ordered to stand.");
		}
		else if(command.getName().equals("down")) {
			List<Entity> entities = player.getNearbyEntities(rangeX, rangeY, rangeZ);
			
			for(Entity entity : entities) {
				if(entity instanceof Tameable) {
					Tameable tameable = (Tameable) entity;
					
					if(tameable.isTamed() && tameable.getOwner().equals(player)) {
						if(tameable instanceof Wolf) {
							((Wolf) tameable).setTarget(null);
						}
					}
				}
			}
			
			sender.sendMessage(ChatColor.YELLOW + "Nearby wolves have been ordered to stop attacking.");
		}
		else if(command.getName().equals("guard") || command.getName().equals("unguard")) {
			boolean setGuarding = command.getName().equals("guard");
			
			List<Entity> entities = player.getNearbyEntities(rangeX, rangeY, rangeZ);
			
			for(Entity entity : entities) {
				if(entity instanceof Wolf) {
					Wolf wolf = (Wolf) entity;
					
					if(wolf.isTamed() && wolf.getOwner().equals(player)) {
						int data = entityManager.getData(wolf);
						
						entityManager.setData(wolf, BitUtils.set(data, 0, setGuarding));
						
						System.out.println(entityManager.getData(wolf));
					}
				}
			}
			
			if(setGuarding) {
				sender.sendMessage(ChatColor.YELLOW + "Nearby wolves have been ordered to guard reinforcements.");
			}
			else {
				sender.sendMessage(ChatColor.YELLOW + "Nearby wolves have been ordered to stop guarding reinforcements.");
			}
		}
		
		return true;
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	public static int getRangeX() {
		return rangeX;
	}
	
	public static int getRangeY() {
		return rangeY;
	}
	
	public static int getRangeZ() {
		return rangeZ;
	}
	
	public static FileConfiguration getPluginConfig() {
		return instance.getConfig();
	}
}
