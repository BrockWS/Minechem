package minechem.inventory.slot;

import minechem.init.ModItems;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotChemistJournal extends Slot
{

    public SlotChemistJournal(IInventory par1iInventory, int par2, int par3, int par4)
    {
        super(par1iInventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid(ItemStack itemstack)
    {
        return itemstack.getItem() == ModItems.journal;
    }

    @Override
    public int getSlotStackLimit()
    {
        return 1;
    }

}
