package com.untamedears.animals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;

public class EntityDatabase {
	private Connection connection;
	
	public EntityDatabase(String url, String username, String password) {
		try {
			connection = DriverManager.getConnection(url, username, password);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		updateDatabase();
	}
	
	private void updateDatabase() {
		try {
			Statement statement = connection.createStatement();
			statement.execute("CREATE TABLE IF NOT EXISTS entity_data (uuid varchar(36) NOT NULL, chunk_x int NOT NULL, chunk_z int NOT NULL, data int NOT NULL DEFAULT 0, UNIQUE (uuid))");
			statement.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addEntity(Entity entity) {
		try {
			PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO entity_data VALUES (?,?,?,?)");
			
			Chunk c = entity.getLocation().getChunk();
			
			statement.setString(1, entity.getUniqueId().toString());
			statement.setInt(2, c.getX());
			statement.setInt(3, c.getZ());
			statement.setInt(4, 0);
			
			statement.execute();
			statement.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeEntity(Entity entity) {
		this.removeEntity(entity.getUniqueId());
	}
	
	public void removeEntity(UUID uuid) {
		try {
			PreparedStatement statement = connection.prepareStatement("DELETE FROM entity_data WHERE uuid=?");
			
			statement.setString(1, uuid.toString());
			
			statement.execute();
			statement.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setLocation(Entity entity, int chunkX, int chunkZ) {
		try {
			PreparedStatement statement = connection.prepareStatement("UPDATE entity_data SET chunk_x=? AND chunk_z=? WHERE uuid=?");
			
			statement.setInt(1, chunkX);
			statement.setInt(2, chunkZ);
			statement.setString(3, entity.getUniqueId().toString());
			
			statement.execute();
			statement.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setData(Entity entity, int data) {
		try {
			PreparedStatement statement = connection.prepareStatement("UPDATE entity_data SET data=? WHERE uuid=?");
			
			statement.setInt(1, data);
			statement.setString(2, entity.getUniqueId().toString());
			
			statement.execute();
			statement.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getData(Entity entity) {
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT data FROM entity_data WHERE uuid=? AND chunk_x=? AND chunk_z=?");
			
			Chunk c = entity.getLocation().getChunk();
			
			statement.setString(1, entity.getUniqueId().toString());
			statement.setInt(2, c.getX());
			statement.setInt(3, c.getZ());
			
			ResultSet results = statement.executeQuery();
			
			int data;
			
			if(results.next())
				data = results.getInt(1);
			else
				data = 0;
			
			results.close();
			statement.close();
			
			return data;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public Map<UUID, Integer> getEntitiesInChunk(int x, int z) {
		Map<UUID, Integer> map = new HashMap<UUID, Integer>();
		
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT uuid,data FROM entity_data WHERE chunk_x=? AND chunk_z=?");
			
			statement.setInt(1, x);
			statement.setInt(2, z);
			
			ResultSet results = statement.executeQuery();

			while(results.next()) {
				String uuidStr = results.getString(1);
				int data = results.getInt(2);

				try {
					UUID uuid = UUID.fromString(uuidStr);

					map.put(uuid, data);
				}
				catch(IllegalArgumentException e) {
					results.deleteRow();
				}
			}

			results.close();
			statement.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		return map;
	}
	
	public Map<UUID, Integer> validateChunk(Chunk c) {
		Map<UUID, Integer> map = getEntitiesInChunk(c.getX(), c.getZ());
		Map<UUID, Integer> real = new HashMap<UUID, Integer>();
		
		if(c.getX() == 10 && c.getZ() == 16) {
			System.out.println(map.size());
		}
		
		for(Entity entity : c.getEntities()) {
			UUID uuid = entity.getUniqueId();
			
			if(map.containsKey(uuid)) {
				real.put(uuid, map.get(uuid));

				map.remove(uuid);
			}
		}
		
		EntityManager entityManager = ImprovedAnimals.instance.getEntityManager();
		
		for(UUID uuid : map.keySet()) {
			Entity entity = entityManager.getLoadedEntity(uuid);
			
			if(entity != null) {
				Chunk c2 = entity.getLocation().getChunk();
				
				setLocation(entity, c2.getX(), c2.getZ());
			}
			else {
				removeEntity(uuid);
			}
		}
		
		return real;
	}
	
	public void close() {
		try {
			connection.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
