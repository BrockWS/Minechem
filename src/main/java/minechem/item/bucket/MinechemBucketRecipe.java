package minechem.item.bucket;

import minechem.MinechemItemsRegistration;
import minechem.item.MinechemChemicalType;
import minechem.item.element.ElementItem;
import minechem.item.molecule.MoleculeItem;
import minechem.radiation.RadiationEnum;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class MinechemBucketRecipe implements IRecipe {

	public final MinechemBucketItem output;
	public final MinechemChemicalType chemicalType;
	
	public MinechemBucketRecipe(MinechemBucketItem output,MinechemChemicalType chemicalType) {
		this.output=output;
		this.chemicalType=chemicalType;
	}
	
	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		int chemicals=0;
		boolean hasBucket=false;
		for (int i=0;i<inv.getSizeInventory();i++){
			ItemStack stack=inv.getStackInSlot(i);
			if (stack==null){
				continue;
			}
			
			if (stack.getItem()==Items.bucket){
				if (hasBucket){
					return false;
				}
				hasBucket=true;
				continue;
			}
			
			MinechemChemicalType anotherType;
			if (stack.getItem()==MinechemItemsRegistration.element){
				anotherType=ElementItem.getElement(stack);
			}else if(stack.getItem()==MinechemItemsRegistration.molecule){
				anotherType=MoleculeItem.getMolecule(stack);
			}else{
				return false;
			}
			
			if (anotherType==chemicalType){
				chemicals++;
			}else{
				return false;
			}
		}
		
		return hasBucket&&chemicals==8;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		if (chemicalType.radioactivity()==RadiationEnum.stable){
			return getRecipeOutput();
		}
		
		long time=Long.MAX_VALUE;
		boolean hasCompared=false;
		for (int i=0;i<inv.getSizeInventory();i++){
			ItemStack stack=inv.getStackInSlot(i);
			if (stack==null){
				continue;
			}

			if (stack.getItem()==MinechemItemsRegistration.element||stack.getItem()==MinechemItemsRegistration.molecule){
				if (stack.stackTagCompound!=null){
					long anotherTime=stack.stackTagCompound.getLong("decayStart");
					if (anotherTime<time){
						time=anotherTime;
						hasCompared=true;
					}
				}
			}
		}
		
		ItemStack result=getRecipeOutput();
		if (!hasCompared){
			return result;
		}
		
		NBTTagCompound comp=new NBTTagCompound();
		comp.setLong("decayStart", time);
		comp.setLong("lastUpdate", time);
		comp.setInteger("dimensionID", 0);
		result.stackTagCompound=comp;
		return result;
	}

	@Override
	public int getRecipeSize() {
		return 3;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(output, 1);
	}
	
}
