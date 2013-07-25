package com.untamedears.animals;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;

public class EntityManager {
	private Map<UUID, Integer> entities = new HashMap<UUID, Integer>();
	private Map<UUID, WeakReference<Entity>> loadedEntities = new HashMap<UUID, WeakReference<Entity>>();
	
	private EntityDatabase database;
	
	public EntityManager(EntityDatabase database) {
		this.database = database;
	}
	
	public void loadChunk(Chunk c) {
		Map<UUID, Integer> map = database.validateChunk(c);
		
		entities.putAll(map);
		
		for(Entity entity : c.getEntities()) {
			UUID uuid = entity.getUniqueId();
			
			if(entities.containsKey(uuid)) {
				loadedEntities.put(uuid, new WeakReference<Entity>(entity));
			}
		}
	}
	
	public void unloadChunk(Chunk c) {
		int x = c.getX();
		int z = c.getZ();
		
		for(Entity entity : c.getEntities()) {
			UUID uuid = entity.getUniqueId();
			
			loadedEntities.remove(uuid);
			
			if(entities.remove(uuid) != null) {
				database.setLocation(entity, x, z);
			}
		}
	}
	
	public void removeEntity(Entity e) {
		UUID uuid = e.getUniqueId();
		
		entities.remove(uuid);
		loadedEntities.remove(uuid);
		
		database.removeEntity(e);
	}
	
	public void setData(Entity e, int data) {
		UUID uuid = e.getUniqueId();
		
		if(!entities.containsKey(uuid))
			database.addEntity(e);
		
		database.setData(e, data);
		
		entities.put(e.getUniqueId(), data);
	}
	
	public int getData(Entity e) {
		UUID uuid = e.getUniqueId();
		
		if(entities.containsKey(uuid)) {
			return entities.get(uuid);
		}
		else {
			return 0;
		}
	}
	
	public Entity getLoadedEntity(UUID uuid) {
		if(loadedEntities.containsKey(uuid)) {
			Entity entity = loadedEntities.get(uuid).get();
			
			if(entity == null)
				loadedEntities.remove(uuid);
			
			return entity;
		}
		
		return null;
	}
}
