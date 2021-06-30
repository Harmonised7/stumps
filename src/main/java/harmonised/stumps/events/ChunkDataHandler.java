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
    private static Map<String, Map<ResourceLocation, Map<ChunkPos, Set<BlockPos>>>> chunkDataMap = new HashMap<>();

    public static void init()
    {
        chunkDataMap = new HashMap<>();
    }

    public static void handleChunkDataLoad( ChunkDataEvent.Load event )
    {
        CompoundNBT chunkNBT = event.getData();
        if( chunkNBT != null )
        {
            CompoundNBT levelNBT = chunkNBT.getCompound( "Level" );
            for( Map.Entry<String, Map<ResourceLocation, Map<ChunkPos, Set<BlockPos>>>> dataEntry : chunkDataMap.entrySet() )
            {
                if( levelNBT.contains( dataEntry.getKey() ) )
                {
                    World world = (World) event.getWorld();
                    ResourceLocation dimResLoc = Util.getDimensionResLoc( world );
                    ChunkPos chunkPos = event.getChunk().getPos();

                    if( !dataEntry.getValue().containsKey( dimResLoc ) )
                        dataEntry.getValue().put( dimResLoc, new HashMap<>() );

                    CompoundNBT placedPosNBT = ( (CompoundNBT) levelNBT.get( dataEntry.getKey() ) );
                    if( placedPosNBT == null )
                        return;
                    Map<ChunkPos, Set<BlockPos>> chunkMap = dataEntry.getValue().get( dimResLoc );
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
    }

    public static void handleChunkDataSave( ChunkDataEvent.Save event )
    {
        World world = (World) event.getWorld();
        ResourceLocation dimResLoc = Util.getDimensionResLoc( world );
        if( chunkDataMap.containsKey( dimResLoc ) )
        {
            ChunkPos chunkPos = event.getChunk().getPos();
            if( chunkDataMap.get( dimResLoc ).containsKey( chunkPos ) )
            {
                CompoundNBT levelNBT = (CompoundNBT) event.getData().get( "Level" );
                if( levelNBT == null )
                    return;

                for( Map.Entry<String, Map<ResourceLocation, Map<ChunkPos, Set<BlockPos>>>> dataEntry : chunkDataMap.entrySet() )
                {
                    CompoundNBT newPlacedNBT = new CompoundNBT();
                    CompoundNBT insidesNBT;

                    int i = 0;

                    for( BlockPos entry : dataEntry.getValue().get( dimResLoc ).get( chunkPos ) )
                    {
                        insidesNBT = new CompoundNBT();
                        insidesNBT.put( "pos", NBTUtil.writeBlockPos( entry ) );
                        newPlacedNBT.put( i++ + "", insidesNBT );
                    }

                    levelNBT.put( dataEntry.getKey(), newPlacedNBT );
                }
            }
        }
    }

    public static void addPos( ResourceLocation dimResLoc, BlockPos blockPos, PosType posType )
    {
        ChunkPos chunkPos = new ChunkPos( blockPos );

        Map<ResourceLocation, Map<ChunkPos, Set<BlockPos>>> theMap = chunkDataMap.get( posType.toString() );

        if( !theMap.containsKey( dimResLoc ) )
            theMap.put( dimResLoc, new HashMap<>() );

        Map<ChunkPos, Set<BlockPos>> chunkMap = theMap.get( dimResLoc );

        if( !chunkMap.containsKey( chunkPos ) )
            chunkMap.put( chunkPos, new HashSet<>() );

        Set<BlockPos> blockMap = chunkMap.get( chunkPos );

        blockMap.add( blockPos );
    }

    public static void delPos( ResourceLocation dimResLoc, BlockPos blockPos, PosType posType )
    {
        ChunkPos chunkPos = new ChunkPos( blockPos );

        Map<ResourceLocation, Map<ChunkPos, Set<BlockPos>>> theMap = chunkDataMap.get( posType.toString() );

        if( !theMap.containsKey( dimResLoc ) )
            theMap.put( dimResLoc, new HashMap<>() );

        Map<ChunkPos, Set<BlockPos>> chunkMap = theMap.get( dimResLoc );

        if( !chunkMap.containsKey( chunkPos ) )
            chunkMap.put( chunkPos, new HashSet<>() );

        Set<BlockPos> blockMap = chunkMap.get( chunkPos );

        blockMap.remove( blockPos );
    }

    public static boolean checkPos( World world, BlockPos pos, PosType posType )
    {
        return checkPos( Util.getDimensionResLoc( world ), pos, posType );
    }

    public static boolean checkPos( ResourceLocation dimResLoc, BlockPos blockPos, PosType posType )
    {
        return chunkDataMap.get( posType.toString() ).getOrDefault( dimResLoc, new HashMap<>() ).getOrDefault( new ChunkPos( blockPos ), new HashSet<>() ).contains( blockPos );
    }

    public enum PosType
    {
        TREE,
        PLACED;

        static
        {
            for( PosType posType : values() )
            {
                if( !chunkDataMap.containsKey( posType.toString() ) )
                    chunkDataMap.put( posType.toString(), new HashMap<>() );
            }
        }

        @Override
        public String toString()
        {
            return name().toLowerCase();
        }
    }
}