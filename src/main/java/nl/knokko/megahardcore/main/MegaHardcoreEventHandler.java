package nl.knokko.megahardcore.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import nl.knokko.megahardcore.ai.*;

public class MegaHardcoreEventHandler {
	
	public static HashMap<EntityZombie, BlockBreakProgress> breakProgress = new HashMap<EntityZombie, BlockBreakProgress>();
	public static ArrayList<EntityArrow> explosiveArrows = new ArrayList<EntityArrow>();
	
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event){
		if(event.entity instanceof EntityLivingBase && event.entity instanceof IMob){
			((EntityLivingBase) event.entity).getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(90);
		}
		if(event.entity instanceof EntityCreeper){
			EntityCreeper creeper = (EntityCreeper) event.entity;
			creeper.getDataWatcher().updateObject(17, Byte.valueOf((byte) 1));
			creeper.targetTasks.taskEntries.remove(0);
			creeper.targetTasks.addTask(10, new EntityAINearestAttackablePlayer(creeper));
			creeper.tasks.taskEntries.remove(4);
			creeper.tasks.addTask(10, new EntityAIAttackOnCollide(creeper, 1.0D, true));
		}
		if(event.entity instanceof EntityZombie){
			EntityZombie zombie = (EntityZombie) event.entity;
			int random = new Random().nextInt(4);
			ItemStack weapon = new ItemStack(Items.iron_sword);
			if(random == 0)
				weapon = new ItemStack(Items.iron_pickaxe);
			else if(random == 1)
				weapon = new ItemStack(Items.iron_axe);
			else if(random == 2)
				weapon = new ItemStack(Items.iron_shovel);
			zombie.setCurrentItemOrArmor(0, weapon);
			zombie.setCurrentItemOrArmor(1, new ItemStack(Items.iron_boots));
			zombie.setCurrentItemOrArmor(2, new ItemStack(Items.iron_leggings));
			zombie.setCurrentItemOrArmor(3, new ItemStack(Items.iron_chestplate));
			zombie.setCurrentItemOrArmor(4, new ItemStack(Items.iron_helmet));
			zombie.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3);
			zombie.targetTasks.taskEntries.remove(1);
			zombie.tasks.taskEntries.remove(1);
			zombie.targetTasks.addTask(10, new EntityAINearestAttackablePlayer(zombie));
			zombie.tasks.addTask(10, new EntityAIAttackOnCollide(zombie, EntityPlayer.class, 1.0D, true));
		}
		if(event.entity instanceof EntitySkeleton){
			EntitySkeleton skelly = (EntitySkeleton) event.entity;
			ItemStack bow = new ItemStack(Items.bow);
			ItemStack sword = new ItemStack(Items.stone_sword);
			sword.addEnchantment(Enchantment.fireAspect, 2);
			sword.addEnchantment(Enchantment.sharpness, 4);
			bow.addEnchantment(Enchantment.punch, 1);
			bow.addEnchantment(Enchantment.flame, 1);
			skelly.targetTasks.taskEntries.remove(1);
			skelly.targetTasks.addTask(2, new EntityAINearestAttackablePlayer(skelly, 256));
			if(skelly.getSkeletonType() == 0){
				skelly.setCurrentItemOrArmor(0, bow);
				skelly.tasks.addTask(4, new EntityAISkeletonSnipe(skelly));
			}
			if(skelly.getSkeletonType() == 1)
				skelly.setCurrentItemOrArmor(0, sword);
			skelly.setCurrentItemOrArmor(1, new ItemStack(Items.iron_boots));
			skelly.setCurrentItemOrArmor(2, new ItemStack(Items.iron_leggings));
			skelly.setCurrentItemOrArmor(3, new ItemStack(Items.iron_chestplate));
			skelly.setCurrentItemOrArmor(4, new ItemStack(Items.iron_helmet));
			skelly.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3);
		}
		if(event.entity instanceof EntitySpider){
			((EntityLivingBase) event.entity).getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.4);
		}
		if(event.entity instanceof EntityPigZombie){
			EntityPigZombie zombie = (EntityPigZombie) event.entity;
			int random = new Random().nextInt(4);
			ItemStack weapon = new ItemStack(Items.golden_sword);
			if(random == 0)
				weapon = new ItemStack(Items.golden_pickaxe);
			else if(random == 1)
				weapon = new ItemStack(Items.golden_axe);
			else if(random == 2)
				weapon = new ItemStack(Items.golden_shovel);
			event.entity.setCurrentItemOrArmor(0, weapon);
			event.entity.setCurrentItemOrArmor(1, new ItemStack(Items.golden_boots));
			event.entity.setCurrentItemOrArmor(2, new ItemStack(Items.golden_leggings));
			event.entity.setCurrentItemOrArmor(3, new ItemStack(Items.golden_chestplate));
			event.entity.setCurrentItemOrArmor(4, new ItemStack(Items.golden_helmet));
			zombie.targetTasks.addTask(10, new EntityAINearestAttackablePlayer(zombie));
			zombie.tasks.addTask(10, new EntityAIAttackOnCollide(zombie, EntityPlayer.class, 1.0D, true));
		}
		if(event.entity instanceof EntityDragon){
			EntityDragon dragon = (EntityDragon) event.entity;
			dragon.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(400);
		}
		if(event.entity instanceof EntityGhast){
			EntityGhast ghast = (EntityGhast) event.entity;
			NBTTagCompound nbt = new NBTTagCompound();
			ghast.writeToNBT(nbt);
			nbt.setInteger("ExplosionPower", 3);
			ghast.readFromNBT(nbt);
			ghast.tasks.taskEntries.remove(2);
			ghast.tasks.addTask(7, new EntityAIGhastFireballAttack(ghast));
			ghast.targetTasks.taskEntries.remove(0);
			ghast.targetTasks.addTask(1, new EntityAIFindNearestPlayer(ghast));
		}
		if(event.entity instanceof EntityBlaze){
			EntityBlaze blaze = (EntityBlaze) event.entity;
			blaze.tasks.taskEntries.remove(0);
			blaze.tasks.addTask(4, new EntityAIBlazeFireballAttack(blaze));
		}
		if(event.entity instanceof EntitySilverfish){
			EntitySilverfish silver = (EntitySilverfish) event.entity;
			silver.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.4);
			silver.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3);
		}
		if(event.entity instanceof EntityEndermite){
			EntityEndermite mite = (EntityEndermite) event.entity;
			mite.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.4);
			mite.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(5);
		}
		if(event.entity instanceof EntityGuardian){
			EntityGuardian guard = (EntityGuardian) event.entity;
			guard.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6);
			guard.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(8);
			guard.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40);
		}
	}
	
	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event){
		if(event.source.getEntity() instanceof EntitySpider){
			event.entityLiving.addPotionEffect(new PotionEffect(Potion.poison.id, 200));
			event.entityLiving.addPotionEffect(new PotionEffect(Potion.blindness.id, 200));
			event.entityLiving.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200));
			event.entityLiving.addPotionEffect(new PotionEffect(Potion.weakness.id, 200));
			event.entityLiving.addPotionEffect(new PotionEffect(Potion.confusion.id, 200));
			event.entityLiving.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 200));
			event.entityLiving.addPotionEffect(new PotionEffect(Potion.hunger.id, 200));
		}
		if(event.source.getEntity() instanceof EntitySlime){
			event.entityLiving.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 2));
			event.entityLiving.addPotionEffect(new PotionEffect(Potion.weakness.id, 100, 2));
			event.entityLiving.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 100, 2));
			if(event.source.getEntity() instanceof EntityMagmaCube)
				event.entityLiving.setFire(5);
		}
		if(event.entityLiving instanceof EntitySlime && event.source.getSourceOfDamage() instanceof EntityLivingBase){
			EntityLivingBase att = (EntityLivingBase) event.source.getSourceOfDamage();
			att.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 2));
			att.addPotionEffect(new PotionEffect(Potion.weakness.id, 100, 2));
			att.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 100, 2));
			if(event.entityLiving instanceof EntityMagmaCube)
				att.setFire(5);
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event){
		if(event.entity instanceof EntityMagmaCube){
			AxisAlignedBB aabb = event.entity.getEntityBoundingBox();
			double x = aabb.minX;
			while(x <= aabb.maxX){
				double y = aabb.minY;
				while(y <= aabb.maxY){
					double z = aabb.minZ;
					while(z <= aabb.maxZ){
						if(event.entity.worldObj.getBlockState(new BlockPos(x, y - 1, z)).getBlock().isFullBlock())
							event.entity.worldObj.setBlockState(new BlockPos(x, y, z), Blocks.fire.getDefaultState());
						++z;
						if(z > aabb.maxZ && z < aabb.maxZ + 1)
							z = aabb.maxZ;
					}
					++y;
					if(y > aabb.maxY && y < aabb.maxY + 1)
						y = aabb.maxY;
				}
				++x;
				if(x > aabb.maxX && x < aabb.maxX + 1)
					x = aabb.maxX;
			}
		}
		if(event.entity instanceof EntityCreeper){
			EntityCreeper creeper = (EntityCreeper) event.entity;
			if(creeper.func_146078_ca())
				return;
			EntityLivingBase target = creeper.getAttackTarget();
			if(target != null && target.getDistanceSqToEntity(creeper) < 9 && !target.isInvisible() && (!(target instanceof EntityPlayer && ((EntityPlayer)target).capabilities.disableDamage))){
				creeper.func_146079_cb();
			}
		}
		if(event.entity instanceof EntityZombie && !event.entity.worldObj.isRemote && event.entity.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")){
			EntityZombie zombie = (EntityZombie) event.entity;
			if(zombie.getAttackTarget() != null){
				MovingObjectPosition target = zombie.worldObj.rayTraceBlocks(zombie.getPositionVector(), zombie.getAttackTarget().getPositionVector());
				if(target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && target.hitVec.distanceTo(zombie.getPositionVector().addVector(0, 1.7, 0)) < 3){
					BlockPos pos = target.getBlockPos();
					if(getBreakingBlock(zombie) != null && pos.equals(getBreakingBlock(zombie)))
						breakProgress.get(zombie).makeProgress();
					else
						breakProgress.put(zombie, new BlockBreakProgress(pos, zombie));
				}
				else {
					target = zombie.worldObj.rayTraceBlocks(zombie.getPositionVector().addVector(0, 1.8, 0), zombie.getAttackTarget().getPositionVector().addVector(0, 1.8, 0));
					if(target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && target.hitVec.distanceTo(zombie.getPositionVector().addVector(0, 1.7, 0)) < 3){
						BlockPos pos = target.getBlockPos();
						if(getBreakingBlock(zombie) != null && pos.equals(getBreakingBlock(zombie)))
							breakProgress.get(zombie).makeProgress();
						else
							breakProgress.put(zombie, new BlockBreakProgress(pos, zombie));
					}
					else
						breakProgress.put(zombie, null);
				}
			}
			else
				breakProgress.put(zombie, null);
		}
	}
	
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event){
		int i = 0;
		while(i < explosiveArrows.size()){
			EntityArrow arrow = explosiveArrows.get(i);
			if(arrow.posX == arrow.prevPosX && arrow.posY == arrow.prevPosY && arrow.posZ == arrow.prevPosZ && !event.world.isRemote){
				event.world.createExplosion(arrow, arrow.posX, arrow.posY, arrow.posZ, 2.5f, event.world.getGameRules().getGameRuleBooleanValue("mobGriefing"));
				explosiveArrows.remove(i);
				arrow.setDead();
				--i;
			}
			++i;
		}
	}
	
	public static BlockPos getBreakingBlock(EntityZombie zombie){
		return breakProgress.get(zombie) != null ? breakProgress.get(zombie).pos : null;
	}
	
	private static class BlockBreakProgress {
		
		private int progress;
		private int goal;
		
		private BlockPos pos;
		private World world;
		private EntityZombie zombie;
		
		private BlockBreakProgress(BlockPos pos, EntityZombie zombie){
			this.pos = pos;
			this.world = zombie.worldObj;
			this.zombie = zombie;
			ItemStack weapon = zombie.getHeldItem();
			Block block = world.getBlockState(pos).getBlock();
			if(block.getBlockHardness(world, pos) < 0){
				goal = -1;
			}
			goal = (int) (100 * block.getBlockHardness(world, pos));
			if(weapon != null)
				goal /= weapon.getStrVsBlock(block);
		}
		
		private void makeProgress(){
			if(goal < 0)
				breakProgress.put(zombie, null);
			zombie.swingItem();
			progress++;
			if(progress >= goal && goal >= 0){
				world.destroyBlock(pos, true);
				breakProgress.remove(zombie);
			}
		}
	}
}
