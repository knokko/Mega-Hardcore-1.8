package nl.knokko.megahardcore.ai;

import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import nl.knokko.megahardcore.main.MegaHardcoreEventHandler;

public class EntityAISkeletonSnipe extends EntityAIBase {
	
	public static final UUID snipeSpeedID = new UUID(6294, 8172);
	public static final AttributeModifier snipeSpeed = new AttributeModifier(snipeSpeedID, "Snipe Speed", -1, 1);
	
	private byte snipeStatus;
	private boolean shouldExplode;
	
	public final EntitySkeleton skelly;
	
	public EntityPlayer target;

	public EntityAISkeletonSnipe(EntitySkeleton skeleton) {
		skelly = skeleton;
	}

	@Override
	public boolean shouldExecute() {
		if(skelly.getAttackTarget() instanceof EntityPlayer){
			target = (EntityPlayer) skelly.getAttackTarget();
			return true;
		}
		else
			return false;
	}
	
	@Override
	public void resetTask(){
		target = null;
	}
	
	@Override
	public boolean continueExecuting(){
		return shouldExecute();
	}
	
	@Override
	public void startExecuting(){}
	
	@Override
	public void updateTask(){
		if(snipeStatus < 0)
			++snipeStatus;
		else if(snipeStatus == 0){
			if(skelly.getDistanceToEntity(target) > 20)
				startSniping();
		}
		else {
			++snipeStatus;
			if(snipeStatus >= 100)
				shoot();
		}
	}
	
	protected void shoot(){
		if(skelly.getDistanceToEntity(target) > 20){
			EntityArrow arrow = new EntityArrow(skelly.worldObj, skelly, 5);
			double distance = arrow.getDistanceToEntity(target);
			double distanceX = target.posX - arrow.posX;
			double distanceY = target.getEntityBoundingBox().maxY - arrow.posY;
			double distanceZ = target.posZ - arrow.posZ;
			double speed = distance * 0.15;
			arrow.motionX = speed * distanceX / distance;
			arrow.motionY = speed * distanceY / distance;
			arrow.motionZ = speed * distanceZ / distance;
			skelly.rotationPitch = arrow.prevRotationPitch = arrow.rotationPitch = (float) Math.toDegrees(Math.atan2(arrow.motionY, Math.hypot(arrow.motionX, arrow.motionY)));
			skelly.rotationYaw = skelly.rotationYawHead = arrow.prevRotationYaw = arrow.rotationYaw = (float) Math.toDegrees(Math.atan2(arrow.motionX, arrow.motionZ));
			arrow.setDamage(1);
			if(!skelly.canEntityBeSeen(target) && shouldExplode){
				MegaHardcoreEventHandler.explosiveArrows.add(arrow);
				snipeStatus = -100;
			}
			skelly.worldObj.spawnEntityInWorld(arrow);
			snipeStatus = 0;
		}
		snipeStatus = -20;
		skelly.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(snipeSpeed);
	}
	
	protected void startSniping(){
		shouldExplode = !skelly.canEntityBeSeen(target);
		if(skelly.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getModifier(snipeSpeedID) == null)
			skelly.getEntityAttribute(SharedMonsterAttributes.movementSpeed).applyModifier(snipeSpeed);
		snipeStatus = 1;
	}
}
