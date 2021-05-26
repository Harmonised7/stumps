package harmonised.stumps.events;

import harmonised.stumps.util.Util;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkDataEvent;

import java.util.*;

public class ChunkDataHandler
{
    private static Map<ResourceLocation, Map<ChunkPos, Set<BlockPos>>> placedMap = new HashMap<>();

    public static void init()
    {
        placedMap = new HashMap<>();
    }

    public static void handleChunkDataLoad( ChunkDataEvent.Load event )
    {
        CompoundNBT chunkNBT = event.getData();
        if( chunkNBT != null )
        {
            CompoundNBT levelNBT = chunkNBT.getCompound( "Level" );
            if( levelNBT.contains( "placedPos" ) )
            {
                World world = (World) event.getWorld();
                ResourceLocation dimResLoc = Util.getDimensionResLoc( world );
                ChunkPos chunkPos = event.getChunk().getPos();

                if( !placedMap.containsKey( dimResLoc ) )
                    placedMap.put( dimResLoc, new HashMap<>() );

                CompoundNBT placedPosNBT = ( (CompoundNBT) levelNBT.get( "placedPos" ) );
                if( placedPosNBT == null )
                    return;
                Map<ChunkPos, Set<BlockPos>> chunkMap = placedMap.get( dimResLoc );
                Set<BlockPos> blockMap = new HashSet<>();
                Set<String> keySet = placedPosNBT.getAllKeys();

                keySet.forEach( key ->
                {
                    CompoundNBT entry = placedPosNBT.getCompound( key );
                    blockMap.add( NBTUtil.readBlockPos( entry.getCompound( "pos" ) ) );
                });

                chunkMap.remove( chunkPos );
                chunkMap.put( chunkPos, blockMap );
            }
        }
    }

    public static void handleChunkDataSave( ChunkDataEvent.Save event )
    {
        World world = (World) event.getWorld();
        ResourceLocation dimResLoc = Util.getDimensionResLoc( world );
        if( placedMap.containsKey( dimResLoc ) )
        {
            ChunkPos chunkPos = event.getChunk().getPos();
            if( placedMap.get( dimResLoc ).containsKey( chunkPos ) )
            {
                CompoundNBT levelNBT = (CompoundNBT) event.getData().get( "Level" );
                if( levelNBT == null )
                    return;

                CompoundNBT newPlacedNBT = new CompoundNBT();
                CompoundNBT insidesNBT;

                int i = 0;

                for( BlockPos entry : placedMap.get( dimResLoc ).get( chunkPos ) )
                {
                    insidesNBT = new CompoundNBT();
                    insidesNBT.put( "pos", NBTUtil.writeBlockPos( entry ) );
                    newPlacedNBT.put( i++ + "", insidesNBT );
                }

                levelNBT.put( "placedPos", newPlacedNBT );
            }
        }
    }

    public static void addPos( ResourceLocation dimResLoc, BlockPos blockPos )
    {
        ChunkPos chunkPos = new ChunkPos( blockPos );

        if( !placedMap.containsKey( dimResLoc ) )
            placedMap.put( dimResLoc, new HashMap<>() );

        Map<ChunkPos, Set<BlockPos>> chunkMap = placedMap.get( dimResLoc );

        if( !chunkMap.containsKey( chunkPos ) )
            chunkMap.put( chunkPos, new HashSet<>() );

        Set<BlockPos> blockMap = chunkMap.get( chunkPos );

        blockMap.add( blockPos );
    }

    public static void delPos( ResourceLocation dimResLoc, BlockPos blockPos )
    {
        ChunkPos chunkPos = new ChunkPos( blockPos );

        if( !placedMap.containsKey( dimResLoc ) )
            placedMap.put( dimResLoc, new HashMap<>() );

        Map<ChunkPos, Set<BlockPos>> chunkMap = placedMap.get( dimResLoc );

        if( !chunkMap.containsKey( chunkPos ) )
            chunkMap.put( chunkPos, new HashSet<>() );

        Set<BlockPos> blockMap = chunkMap.get( chunkPos );

        blockMap.remove( blockPos );
    }

    public static boolean checkPos( World world, BlockPos pos )
    {
        return checkPos( Util.getDimensionResLoc( world ), pos );
    }

    public static boolean checkPos( ResourceLocation dimResLoc, BlockPos blockPos )
    {
        return placedMap.getOrDefault( dimResLoc, new HashMap<>() ).getOrDefault( new ChunkPos( blockPos ), new HashSet<>() ).contains( blockPos );
    }
}