package com.untamedears.animals;

import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import com.untamedears.animals.util.BitUtils;
import com.untamedears.citadel.entity.IReinforcement;
import com.untamedears.citadel.entity.PlayerReinforcement;
import com.untamedears.citadel.events.PlayerDamageReinforcementEvent;

public class AnimalListener implements Listener {
	private Random random = new Random();
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		ImprovedAnimals.instance.getEntityManager().loadChunk(event.getChunk());
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		ImprovedAnimals.instance.getEntityManager().unloadChunk(event.getChunk());
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		ImprovedAnimals.instance.getEntityManager().removeEntity(event.getEntity());
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		
		if(entity instanceof Tameable) {
			Player player = event.getPlayer();
			Tameable tameable = (Tameable) entity;
			
			if(tameable.isTamed() && tameable.getOwner().equals(player)) {
				ItemStack inHand = player.getItemInHand();
				
				if(inHand != null && inHand.getTypeId() == ImprovedAnimals.getPluginConfig().getInt("untame.material", Material.COOKIE.getId())) {
					if(random.nextFloat() <= ImprovedAnimals.getPluginConfig().getDouble("untame.chance", 0.2)) {
						tameable.setTamed(false);
						
						if(tameable instanceof Wolf)
							((Wolf) tameable).setSitting(false);
						else if(tameable instanceof Ocelot)
							((Ocelot) tameable).setSitting(false);
					}
					
					inHand.setAmount(inHand.getAmount() - 1);
					player.setItemInHand(inHand);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDamageReinforcementEvent(PlayerDamageReinforcementEvent event) {
		Player damager = event.getPlayer();
		
		IReinforcement reinforcement = event.getReinforcement();
		
		if(reinforcement instanceof PlayerReinforcement) {
			PlayerReinforcement playerReinforcement = (PlayerReinforcement) reinforcement;
			
			if(!playerReinforcement.isBypassable(damager)) {
				List<Entity> entities = damager.getNearbyEntities(ImprovedAnimals.getRangeX(), ImprovedAnimals.getRangeY(), ImprovedAnimals.getRangeZ());
				
				for(Entity entity : entities) {
					if(entity instanceof Wolf) {
						final Wolf wolf = (Wolf) entity;
						
						if(wolf.isTamed()) {
							boolean guarding = BitUtils.get(ImprovedAnimals.instance.getEntityManager().getData(wolf), 0);
							
							if(!guarding) {
								return;
							}
							
							final AnimalTamer tamer = wolf.getOwner();
							
							if(tamer.equals(damager)) {
								return;
							}
							
							if(playerReinforcement.isAccessible(tamer.getName())) {
								wolf.setOwner(null);
								wolf.setSitting(false);
								wolf.damage(0, damager);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetLivingEntityEvent event) {
		
	}
}
