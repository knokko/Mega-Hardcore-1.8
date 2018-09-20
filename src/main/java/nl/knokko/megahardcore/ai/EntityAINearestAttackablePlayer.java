package nl.knokko.megahardcore.ai;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Predicates;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAINearestAttackablePlayer extends EntityAINearestAttackableTarget {

	public EntityAINearestAttackablePlayer(EntityCreature monster) {
		this(monster, -1);
	}
	
	public EntityAINearestAttackablePlayer(EntityCreature monster, double maxYDifference){
		super(monster, EntityPlayer.class, false);
	}
	
	@Override
	public boolean shouldExecute(){
        double d0 = getTargetDistance();
        List list = taskOwner.worldObj.playerEntities;
        EntityPlayer nearest = null;
        double distance = 0;
        int i = 0;
        while(i < list.size()){
        	EntityPlayer player = (EntityPlayer) list.get(i);
        	if(targetEntitySelector.apply(player)){
        		if(nearest == null){
        			nearest = player;
        			distance = taskOwner.getDistanceToEntity(player);
        		}
        		else {
        			double dis = taskOwner.getDistanceToEntity(player);
        			if(dis < distance){
        				nearest = player;
        				distance = dis;
        			}
        		}
        	}
        	++i;
        }
        if(nearest == null)
            return false;
        else {
            targetEntity = nearest;
            return true;
        }
    }
}
