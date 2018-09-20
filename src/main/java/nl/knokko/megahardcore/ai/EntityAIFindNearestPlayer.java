package nl.knokko.megahardcore.ai;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;

public class EntityAIFindNearestPlayer extends EntityAIFindEntityNearestPlayer {
	
	public final EntityLiving living;
	public EntityLivingBase target;
	public EntityAINearestAttackableTarget.Sorter sorter;
	public Predicate predicate;

	public EntityAIFindNearestPlayer(EntityLiving living) {
		super(living);
		this.living = living;
		this.sorter = new EntityAINearestAttackableTarget.Sorter(living);
		this.predicate = new Predicate()
        {
            public boolean func_179880_a(Entity entity){
                if(!(entity instanceof EntityPlayer))
                    return false;
                else {
                    double d0 = EntityAIFindNearestPlayer.this.func_179431_f();
                    if(entity.isSneaking())
                        d0 *= 0.800000011920929D;
                    if(entity.isInvisible()){
                        float f = ((EntityPlayer)entity).getArmorVisibility();
                        if (f < 0.1F)
                            f = 0.1F;
                        d0 *= (double)(0.7F * f);
                    }
                    return (double)entity.getDistanceToEntity(EntityAIFindNearestPlayer.this.living) > d0 ? false : EntityAITarget.func_179445_a(EntityAIFindNearestPlayer.this.living, (EntityLivingBase)entity, false, false);
                }
            }
            public boolean apply(Object entity)
            {
                return this.func_179880_a((Entity)entity);
            }
        };
	}
	
	@Override
	public boolean shouldExecute(){
        double d0 = this.func_179431_f();
        List list = living.worldObj.playerEntities;
        Collections.sort(list, this.sorter);

        if (list.isEmpty())
        {
            return false;
        }
        else
        {
            this.target = (EntityLivingBase)list.get(0);
            return true;
        }
    }
	
	@Override
	public void startExecuting(){
        super.startExecuting();
        this.living.setAttackTarget(this.target);
    }
	
	@Override
	protected double func_179431_f()
    {
        IAttributeInstance iattributeinstance = this.living.getEntityAttribute(SharedMonsterAttributes.followRange);
        return iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
    }
}
