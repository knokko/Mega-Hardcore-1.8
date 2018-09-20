package nl.knokko.megahardcore.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAIGhastFireballAttack extends EntityAIBase {
	
	private EntityGhast ghast;
    public int ghastState;
    
    public EntityAIGhastFireballAttack(EntityGhast ghast){
    	this.ghast = ghast;
    }

    public boolean shouldExecute()
    {
        return this.ghast.getAttackTarget() != null;
    }

    public void startExecuting()
    {
        this.ghastState = 0;
    }

    public void resetTask()
    {
        this.ghast.func_175454_a(false);
    }

    public void updateTask()
    {
        EntityLivingBase ghastTarget = this.ghast.getAttackTarget();
        double d0 = 80;

        if (ghastTarget.getDistanceSqToEntity(this.ghast) < d0 * d0)
        {
            World world = this.ghast.worldObj;
            ++this.ghastState;

            if (this.ghastState == 10)
            {
                world.playAuxSFXAtEntity((EntityPlayer)null, 1007, new BlockPos(this.ghast), 0);
            }

            if (this.ghastState == 20)
            {
                double d1 = 4.0D;
                Vec3 vec3 = this.ghast.getLook(1.0F);
                double d2 = ghastTarget.posX - (this.ghast.posX + vec3.xCoord * d1);
                double d3 = ghastTarget.getEntityBoundingBox().minY + (double)(ghastTarget.height / 2.0F) - (0.5D + this.ghast.posY + (double)(this.ghast.height / 2.0F));
                double d4 = ghastTarget.posZ - (this.ghast.posZ + vec3.zCoord * d1);
                world.playAuxSFXAtEntity((EntityPlayer)null, 1008, new BlockPos(this.ghast), 0);
                EntityLargeFireball entitylargefireball = new EntityLargeFireball(world, this.ghast, d2, d3, d4);
                entitylargefireball.explosionPower = this.ghast.func_175453_cd();
                entitylargefireball.posX = this.ghast.posX + vec3.xCoord * d1;
                entitylargefireball.posY = this.ghast.posY + (double)(this.ghast.height / 2.0F) + 0.5D;
                entitylargefireball.posZ = this.ghast.posZ + vec3.zCoord * d1;
                world.spawnEntityInWorld(entitylargefireball);
                this.ghastState = -40;
            }
        }
        else if (this.ghastState > 0)
        {
            --this.ghastState;
        }

        this.ghast.func_175454_a(this.ghastState > 10);
    }
}
