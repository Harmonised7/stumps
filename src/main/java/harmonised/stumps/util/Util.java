package harmonised.stumps.util;

import harmonised.stumps.network.NetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.UUID;

public class Util
{
    public static ResourceLocation getDimensionResLoc(World world )
    {
        return world.dimension().getRegistryName();
    }

    public static ResourceLocation getResLoc( String regKey )
    {
        try
        {
            return new ResourceLocation( regKey );
        }
        catch( Exception e )
        {
            return new ResourceLocation( "" );
        }
    }

    public static ResourceLocation getResLoc( String firstPart, String secondPart )
    {
        try
        {
            return new ResourceLocation( firstPart, secondPart );
        }
        catch( Exception e )
        {
            return null;
        }
    }

    public static CompoundNBT writeUniqueId(UUID uuid )
    {
        CompoundNBT compoundnbt = new CompoundNBT();
        compoundnbt.putLong("M", uuid.getMostSignificantBits());
        compoundnbt.putLong("L", uuid.getLeastSignificantBits());
        return compoundnbt;
    }
}