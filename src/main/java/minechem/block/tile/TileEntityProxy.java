package minechem.block.tile;

import minechem.init.ModBlocks;
import minechem.init.ModConfig;
import minechem.tileentity.multiblock.fission.FissionTileEntity;
import minechem.tileentity.multiblock.fusion.FusionTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class TileEntityProxy extends TileMinechemEnergyBase implements ISidedInventory {

	public TileEntityProxy() {
		super(ModConfig.energyPacketSize);
	}

	public TileEntity manager;
	int managerXOffset;
	int managerYOffset;
	int managerZOffset;

	@Override
	public void update() {
		if (manager != null) {
			int ammountReceived = ((TileMinechemEnergyBase) manager).receiveEnergy(getEnergyStored(), true);
			if (ammountReceived > 0) {
				((TileMinechemEnergyBase) manager).receiveEnergy(ammountReceived, false);
				useEnergy(ammountReceived);
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		if (manager != null) {
			nbtTagCompound.setInteger("managerXOffset", manager.getPos().getX());
			nbtTagCompound.setInteger("managerYOffset", manager.getPos().getY());
			nbtTagCompound.setInteger("managerZOffset", manager.getPos().getZ());
		}

		return nbtTagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		managerXOffset = nbtTagCompound.getInteger("managerXOffset");
		managerYOffset = nbtTagCompound.getInteger("managerYOffset");
		managerZOffset = nbtTagCompound.getInteger("managerZOffset");
		if (world != null) {
			manager = world.getTileEntity(new BlockPos(pos.getX() + managerXOffset, pos.getY() + managerYOffset, pos.getZ() + managerZOffset));
		}

	}

	public void setManager(TileEntity managerTileEntity) {

		manager = managerTileEntity;
		if (managerTileEntity != null) {
			managerXOffset = managerTileEntity.getPos().getX() - pos.getX();
			managerYOffset = managerTileEntity.getPos().getY() - pos.getY();
			managerZOffset = managerTileEntity.getPos().getZ() - pos.getZ();
		}
	}

	public TileEntity getManager() {
		// Return the next block in sequence but never the TileEntityProxy.
		if (world.getTileEntity(new BlockPos(pos.getX() + managerXOffset, pos.getY() + managerYOffset, pos.getZ() + managerZOffset)) != null && !(world.getTileEntity(new BlockPos(pos.getX() + managerXOffset, pos.getY() + managerYOffset, pos.getZ() + managerZOffset)) instanceof TileEntityProxy)) {
			return world.getTileEntity(new BlockPos(pos.getX() + managerXOffset, pos.getY() + managerYOffset, pos.getZ() + managerZOffset));
		}

		// Return the entire fusion generator as a whole (indicating the structure is complete).
		if (world.getBlockState(new BlockPos(pos.getX() + managerXOffset, pos.getY() + managerYOffset, pos.getZ() + managerZOffset)) == ModBlocks.FUSION) {
			manager = buildManagerBlock();
			return manager;
		}

		return null;

	}

	private TileEntity buildManagerBlock() {
		IBlockState state = world.getBlockState(new BlockPos(pos.getX() + managerXOffset, pos.getY() + managerYOffset, pos.getZ() + managerZOffset));

		if (state.getBlock().getMetaFromState(state) == 2) {
			FusionTileEntity fusion = new FusionTileEntity();
			fusion.setWorld(world);
			fusion.setPos(new BlockPos(managerXOffset + pos.getX(), managerYOffset + pos.getY(), managerZOffset + pos.getZ()));

			fusion.setBlockType(ModBlocks.FUSION);
			world.setTileEntity(new BlockPos(pos.getX() + managerXOffset, pos.getY() + managerYOffset, pos.getZ() + managerZOffset), fusion);
		}

		state = world.getBlockState(new BlockPos(pos.getX() + managerXOffset, pos.getY() + managerYOffset, pos.getZ() + managerZOffset));
		if (state.getBlock().getMetaFromState(state) == 3) {
			FissionTileEntity fission = new FissionTileEntity();
			fission.setWorld(world);
			fission.setPos(new BlockPos(managerXOffset + pos.getX(), managerYOffset + pos.getY(), managerZOffset + pos.getZ()));
			fission.setBlockType(ModBlocks.FUSION);
			world.setTileEntity(new BlockPos(pos.getX() + managerXOffset, pos.getY() + managerYOffset, pos.getZ() + managerZOffset), fission);
		}
		return world.getTileEntity(new BlockPos(pos.getX() + managerXOffset, pos.getY() + managerYOffset, pos.getZ() + managerZOffset));

	}

	@Override
	public int getSizeInventory() {
		if (manager != null && manager != this) {
			return ((ISidedInventory) manager).getSizeInventory();
		}
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (getManager() != null && getManager() instanceof ISidedInventory) {
			return ((ISidedInventory) getManager()).getStackInSlot(i);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (getManager() != null && getManager() instanceof ISidedInventory) {
			return ((ISidedInventory) getManager()).decrStackSize(i, j);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int i) {
		if (getManager() != null && getManager() instanceof ISidedInventory) {
			return ((ISidedInventory) getManager()).removeStackFromSlot(i);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (getManager() != null && getManager() instanceof ISidedInventory) {
			((ISidedInventory) getManager()).setInventorySlotContents(i, itemstack);
		}
	}

	@Override
	public String getName() {
		return "Multiblock Minechem proxy";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		if (manager != null && manager != this) {
			return ((ISidedInventory) getManager()).getInventoryStackLimit();
		}
		return 0;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (manager != null && manager != this) {
			return ((ISidedInventory) getManager()).isItemValidForSlot(i, itemstack);
		}
		return false;
	}

	@Override
	public int getField(int i) {
		return 0;
	}

	@Override
	public void setField(int i, int i1) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public int[] getSlotsForFace(EnumFacing enumFacing) {
		if (manager != null && manager != this) {
			return ((ISidedInventory) getManager()).getSlotsForFace(enumFacing);
		}
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, EnumFacing facing) {
		// Cannot insert items into reactor with automation disabled.
		return ModConfig.AllowAutomation && isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, EnumFacing facing) {
		// Cannot extract items from reactor with automation disabled.
		// Can only extract from the bottom.
		return ModConfig.AllowAutomation && facing.getIndex() == 0 && slot == 2;
	}

	@Override
	public int getEnergyRequired() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < inventory.size(); i++) {
			if (!inventory.get(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}
}