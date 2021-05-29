package harmonised.stumps.events;

import harmonised.stumps.util.Reference;
import harmonised.stumps.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber( modid = Reference.MOD_ID )
public class EventHandler
{
    public static final ResourceLocation logsTag = new ResourceLocation( "minecraft:logs" );
    public static final ResourceLocation leavesTag = new ResourceLocation( "minecraft:leaves" );
    public static final ResourceLocation dirtTag = new ResourceLocation( "forge:dirt" );
    public static final ResourceLocation sandTag = new ResourceLocation( "forge:sand" );
    public static final ResourceLocation bambooGrowableTag = new ResourceLocation( "minecraft:bamboo_plantable_on" );

    @SubscribeEvent
    public static void breakSpeedEvent( BlockEvent.BreakEvent event )
    {
        World world = (World) event.getWorld();
        BlockPos pos = event.getPos();
        if( !( event.getPlayer() instanceof FakePlayer ) )
        {
            if( !ChunkDataHandler.checkPos( world, pos ) && !isToolSufficient( event.getPlayer().getMainHandItem().getItem() ) && isLog( world, pos ) )
            {
                detectStumps( world, pos );
                if( ChunkDataHandler.checkPos( world, pos ) )
                {
                    event.setCanceled( true );
                    return;
                }
            }
        }

        ChunkDataHandler.delPos( Util.getDimensionResLoc( world ), pos );
    }

    @SubscribeEvent
    public static void breakSpeedEvent( PlayerEvent.BreakSpeed event )
    {
        PlayerEntity player = event.getPlayer();
        World world = player.getCommandSenderWorld();
        BlockPos pos = event.getPos();
        Item mainHandItem = player.getMainHandItem().getItem();

        if( !ChunkDataHandler.checkPos( world, pos ) && isLog( world, pos ) )
            detectStumps( world, pos );

        if( ChunkDataHandler.checkPos( world, pos ) )
        {
            if( !isToolSufficient( mainHandItem ) )
            {
                event.setNewSpeed(0);
                event.setCanceled( true );
            }
        }
    }

    @SubscribeEvent
    public static void growTreeEvent( SaplingGrowTreeEvent event )
    {
        detectStumps( (World) event.getWorld(), event.getPos() );
    }

    public static boolean isToolSufficient( Item tool )
    {
        return tool instanceof PickaxeItem && ((PickaxeItem) tool).getTier().getLevel() > 2;
    }

    public static boolean isSoilBlock( Block block )
    {
        Set<ResourceLocation> tags = block.getTags();
        return tags.contains( dirtTag ) || tags.contains( sandTag ) || tags.contains( bambooGrowableTag );
    }

    public static void detectStumps(World world, BlockPos pos )
    {
        BlockState state = world.getBlockState( pos );
        Block block = state.getBlock();
        Set<BlockPos> stumpBlocks = new HashSet<>();
        for( int x = -1; x <= 1; x++ )
        {
            for( int z = -1; z <= 1; z++ )
            {
                BlockPos thisPos = pos.north(x).east(z);
                BlockState thisState = world.getBlockState( thisPos );
                Block thisBlock = thisState.getBlock();
                if( thisBlock.equals( block ) )
                {
                    int y = 0;
                    while( world.getBlockState( pos.below(y+1) ).getBlock().equals( block ) )
                    {
                        y++;
                    };
                    if( isSoilBlock( world.getBlockState( thisPos.below( y+1 ) ).getBlock() ) )
                        stumpBlocks.add( thisPos.below(y) );
                }
                else if( !thisBlock.equals( block ) )
                {
                    if( thisState.canOcclude() && !isSoilBlock( thisBlock ) )
                        return;
                }
            }
        }

        ResourceLocation resLoc = Util.getDimensionResLoc( world );
        for( BlockPos stumpPos : stumpBlocks )
        {
            ChunkDataHandler.addPos( resLoc, stumpPos );
        }
    }

    public static boolean isLog( World world, BlockPos pos )
    {
        Block block = world.getBlockState( pos ).getBlock();
        Set<ResourceLocation> tags = block.getTags();
        return tags.contains( logsTag );
    }
}