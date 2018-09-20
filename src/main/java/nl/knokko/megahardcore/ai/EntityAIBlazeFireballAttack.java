package nl.knokko.megahardcore.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class EntityAIBlazeFireballAttack extends EntityAIBase {

	public final EntityBlaze blaze;
    private int fireballs;
    private int timeUntilFireWave;

    public EntityAIBlazeFireballAttack(EntityBlaze blaze){
        this.setMutexBits(3);
        this.blaze = blaze;
    }

    public boolean shouldExecute(){
        EntityLivingBase entitylivingbase = this.blaze.getAttackTarget();
        return entitylivingbase != null && entitylivingbase.isEntityAlive();
    }

    public void startExecuting(){
        this.fireballs = 0;
    }

    public void resetTask(){
        this.blaze.func_70844_e(false);
    }

    public void updateTask(){
        --this.timeUntilFireWave;
        EntityLivingBase target = this.blaze.getAttackTarget();
        double dSq = this.blaze.getDistanceSqToEntity(target);
        double range = blaze.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();
        if (dSq < 4.0D){
            if (this.timeUntilFireWave <= 0){
                this.timeUntilFireWave = 20;
                this.blaze.attackEntityAsMob(target);
            }
            this.blaze.getMoveHelper().setMoveTo(target.posX, target.posY, target.posZ, 1.0D);
        }
        else if (dSq < range * range){
            double d1 = target.posX - this.blaze.posX;
            double d2 = target.getEntityBoundingBox().minY + (double)(target.height / 2.0F) - (this.blaze.posY + (double)(this.blaze.height / 2.0F));
            double d3 = target.posZ - this.blaze.posZ;
            if (this.timeUntilFireWave <= 0){
                ++this.fireballs;
                if (this.fireballs == 1){
                    this.timeUntilFireWave = 40;
                    this.blaze.func_70844_e(true);
                }
                else if (this.fireballs <= 25){
                    this.timeUntilFireWave = 4;
                }
                else {
                    this.timeUntilFireWave = 40;
                    this.fireballs = 0;
                    this.blaze.func_70844_e(false);
                }
                if(this.fireballs > 1){
                    float f = MathHelper.sqrt_float(MathHelper.sqrt_double(dSq)) * 0.5F;
                    this.blaze.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1009, new BlockPos((int)this.blaze.posX, (int)this.blaze.posY, (int)this.blaze.posZ), 0);
                    for (int i = 0; i < 1; ++i){
                        EntitySmallFireball entitysmallfireball = new EntitySmallFireball(this.blaze.worldObj, this.blaze, d1 + this.blaze.getRNG().nextGaussian() * (double)f, d2, d3 + this.blaze.getRNG().nextGaussian() * (double)f);
                        entitysmallfireball.posY = this.blaze.posY + (double)(this.blaze.height / 2.0F) + 0.5D;
                        this.blaze.worldObj.spawnEntityInWorld(entitysmallfireball);
                    }
                }
            }
            this.blaze.getLookHelper().setLookPositionWithEntity(target, 10.0F, 10.0F);
        }
        else {
            this.blaze.getNavigator().clearPathEntity();
            this.blaze.getMoveHelper().setMoveTo(target.posX, target.posY, target.posZ, 1.0D);
        }
        super.updateTask();
    }
}
