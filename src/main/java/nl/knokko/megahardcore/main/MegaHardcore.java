package nl.knokko.megahardcore.main;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = MegaHardcore.MODID, name = MegaHardcore.NAME, version = MegaHardcore.VERSION)
public class MegaHardcore
{
    public static final String MODID = "knokkomegahardcore";
    public static final String VERSION = "1.0";
    public static final String NAME = "Mega Hardcore";
    
    @EventHandler
    public void init(FMLInitializationEvent event){
        MinecraftForge.EVENT_BUS.register(new MegaHardcoreEventHandler());
        FMLCommonHandler.instance().bus().register(new MegaHardcoreEventHandler());
    }
}
