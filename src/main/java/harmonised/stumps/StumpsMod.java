package harmonised.stumps;

import harmonised.stumps.events.EventHandler;
import harmonised.stumps.network.NetworkHandler;
import harmonised.stumps.util.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod( Reference.MOD_ID )
public class StumpsMod
{
    private static final String PROTOCOL_VERSION = "1";
    private static final Logger LOGGER = LogManager.getLogger();

    public static SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named( new ResourceLocation( Reference.MOD_ID, "main_channel" ) )
            .clientAcceptedVersions( PROTOCOL_VERSION::equals )
            .serverAcceptedVersions( PROTOCOL_VERSION::equals )
            .networkProtocolVersion( () -> PROTOCOL_VERSION )
            .simpleChannel();


    public StumpsMod()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener( this::modsLoading );
//        FMLJavaModLoadingContext.get().getModEventBus().addListener( this::clientLoading );
    }

    private void modsLoading( FMLCommonSetupEvent event )
    {
        NetworkHandler.registerPackets();
        MinecraftForge.EVENT_BUS.register( EventHandler.class );
    }

//    private void clientLoading( FMLClientSetupEvent event )
//    {
//        ClientHandler.init();
//    }
}
