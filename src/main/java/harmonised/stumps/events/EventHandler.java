package harmonised.stumps.events;

import harmonised.stumps.util.Reference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = Reference.MOD_ID )
public class EventHandler
{
    @SubscribeEvent
    public static void deathEvent( PlayerEvent.BreakSpeed event )
    {
        LivingEntity livingEntity = event.getEntityLiving();
        World world = livingEntity.getCommandSenderWorld();
        if( world.isClientSide() )
        {
//            Renderer.hpBars.remove( livingEntity );
        }
        else
        {

        }
    }
}