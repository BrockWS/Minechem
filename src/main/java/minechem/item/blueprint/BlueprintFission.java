package minechem.item.blueprint;

import minechem.init.ModBlocks;
import net.minecraft.block.state.IBlockState;

public class BlueprintFission extends MinechemBlueprint {

	private static IBlockState w = air;
	private static IBlockState A = ModBlocks.reactor_core.getDefaultState();
	private static IBlockState C = ModBlocks.tungsten_plating.getDefaultState();
	//@formatter:off
	private static IBlockState[][][] structure = {
			{
					{C,C,C,C,C},
					{C,C,C,C,C},
					{C,C,C,C,C},
					{C,C,C,C,C},
					{C,C,C,C,C}
			},
			{
					{C,C,C,C,C},
					{C,w,w,w,C},
					{C,w,w,w,C},
					{C,w,w,w,C},
					{C,C,C,C,C}
			},
			{
					{C,C,C,C,C},
					{C,w,w,w,C},
					{C,w,A,w,C},
					{C,w,w,w,C},
					{C,C,C,C,C}
			},
			{
					{C,C,C,C,C},
					{C,w,w,w,C},
					{C,w,w,w,C},
					{C,w,w,w,C},
					{C,C,C,C,C}
			},
			{
					{C,C,C,C,C},
					{C,C,C,C,C},
					{C,C,C,C,C},
					{C,C,C,C,C},
					{C,C,C,C,C}
			}

	};
	//@formatter:on

	public BlueprintFission() {
		super("blueprint.fission.desc", structure[0][0].length, structure.length, structure[0].length);
		name = "blueprint_fission";
	}

	@Override
	public IBlockState[][][] getStructure() {
		return structure;
	}

	@Override
	public int getManagerPosX() {
		return 2;
	}

	@Override
	public int getManagerPosY() {
		return 2;
	}

	@Override
	public int getManagerPosZ() {
		return 2;
	}

	@Override
	public int getRenderScale() {
		return 9;
	}

	@Override
	public int getXOffset() {
		return 22;
	}

	@Override
	public int getYOffset() {
		return 60;
	}

}
