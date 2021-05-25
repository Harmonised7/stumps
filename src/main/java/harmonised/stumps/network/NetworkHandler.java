package harmonised.stumps.network;

import harmonised.stumps.StumpsMod;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkHandler
{
    public static final Logger LOGGER = LogManager.getLogger();

    public static void registerPackets()
    {
        int index = 0;

        StumpsMod.HANDLER.registerMessage( index++, MessageIntArray.class, MessageIntArray::encode, MessageIntArray::decode, MessageIntArray::handlePacket );
    }

    public static void sendToPlayer( MessageIntArray packet, ServerPlayerEntity player )
    {
        StumpsMod.HANDLER.sendTo( packet, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT );
    }
}
