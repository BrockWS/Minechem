package minechem.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import minechem.tileentity.decomposer.DecomposerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;

public class Recipe
{
	public static Map<String, Recipe> recipes;

	public static Map<ItemStack, ItemStack> smelting;
	public static Map<String, String> oreDictionary;
    public static List craftingRecipes;
	public ItemStack output;
	public ItemStack[] inStacks;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Optional.Method(modid = "RotaryCraft")
	public static List getRotaryRecipes()
	{
		try {
			Class worktable = Class.forName("Reika.RotaryCraft.Auxiliary.WorktableRecipes");
			Method instance = worktable.getMethod("getInstance");
			Method list = worktable.getMethod("getRecipeListCopy");
			Class config = Class.forName("Reika.RotaryCraft.Registry.ConfigRegistry");
			Method state = config.getMethod("getState");
			boolean add = !(Boolean) state.invoke(Enum.valueOf(config, "TABLEMACHINES"));
			if (add)
				return (List) list.invoke(instance.invoke(null));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void init()
	{
		recipes = new Hashtable<String, Recipe>();
		smelting = FurnaceRecipes.smelting().getSmeltingList();
		oreDictionary = new Hashtable<String, String>();
		
		craftingRecipes = CraftingManager.getInstance().getRecipeList();
		if (Loader.isModLoaded("RotaryCraft"))
			craftingRecipes.addAll(getRotaryRecipes());
		for (Object recipe : craftingRecipes)
		{
			if (recipe instanceof IRecipe)
			{
				if (((IRecipe) recipe).getRecipeOutput() != null)
				{

					ItemStack output = ((IRecipe) recipe).getRecipeOutput();
					ItemStack[] components = getComponents((IRecipe) recipe);

					String key = DecomposerRecipe.getKey(output);
					if (components != null && key != null)
					{
						boolean badRecipe = false;
						for (ItemStack component : components)
						{
							if (component != null && component.getItem() != null)
							{
								if (isItemEqual(output, component) || checkForLoop(output, component))
								{
									badRecipe = true;
								}
							}
						}
						Recipe currRecipe = recipes.get(key);
						if ((currRecipe == null || output.stackSize < currRecipe.getOutStackSize()) && !badRecipe)
						{
							recipes.put(key, new Recipe(output, components));
						}
					}
				}
			}
		}
		for (ItemStack input : smelting.keySet())
		{
			String key = DecomposerRecipe.getKey(input);
			if (key != null)
			{
				Recipe currRecipe = recipes.get(key);
				if ((currRecipe == null || input.stackSize < currRecipe.getOutStackSize()))
				{
					recipes.put(key, new Recipe(input, new ItemStack[]
					{
						smelting.get(DecomposerRecipe.getKey(input))
					}));
				}
			}
		}
		for (String name : OreDictionary.getOreNames())
		{
			ArrayList<ItemStack> oreDictStacks = OreDictionary.getOres(name);
			for (ItemStack thisStack : oreDictStacks)
			{
				String key = getKey(thisStack);
				if (key != null && DecomposerRecipe.get(key) != null)
				{
					for (ItemStack dictStack : oreDictStacks)
					{
						if (!dictStack.equals(thisStack))
						{
							String fromKey = getKey(dictStack);
							if (fromKey != null)
							{
								oreDictionary.put(fromKey, key);
							}
						}
					}
				}
			}
		}
	}

	public Recipe(ItemStack outStack, ItemStack[] componentsParam)
	{
		output = outStack;
		ItemStack[] components = new ItemStack[componentsParam.length];
		int i = 0;
		for (ItemStack itemStack : componentsParam)
		{
			if (itemStack != null && itemStack.getItem() != null)
			{
				if (itemStack.getItemDamage() == Short.MAX_VALUE)
				{
					components[i] = new ItemStack(itemStack.getItem(), itemStack.stackSize, 0);
				} else
				{
					components[i] = new ItemStack(itemStack.getItem(), itemStack.stackSize, itemStack.getItemDamage());
				}
			} else
			{
				components[i] = null;
			}
			i++;
		}
		inStacks = components;
	}

	public int getOutStackSize()
	{
		return output.stackSize;
	}

	public static String getKey(ItemStack output)
	{
		if (output != null)
		{
			ItemStack result = output.copy();
			result.stackSize = 1;
			if (result.getItemDamage() == Short.MAX_VALUE)
			{
				result.setItemDamage(0);
			}
			if (result.toString().contains("null"))
			{
				return result.stackSize + "x" + result.getItem().getUnlocalizedName(result) + "@" + result.getItemDamage();
			}
			return result.toString();
		}
		return null;
	}

	public static Recipe get(ItemStack output)
	{
		if (output != null)
		{
			if (output.getItem() != null)
			{
				String key = getKey(output);
				if (key != null)
				{
					return get(key);
				}
			}
		}
		return null;
	}

	public static Recipe get(String string)
	{
		return recipes.get(string);
	}

	public String getKey()
	{
		ItemStack result = output.copy();
		result.stackSize = 1;
		if (result.getItemDamage() == Short.MAX_VALUE)
		{
			result.setItemDamage(0);
		}
		if (result.toString().contains("null"))
		{
			return result.stackSize + "x" + result.getItem().getUnlocalizedName(result) + "@" + result.getItemDamage();
		}
		return result.toString();
	}

    private static boolean checkForLoop(ItemStack output, ItemStack input)
    {
        List<IRecipe> recipeList = new ArrayList<IRecipe>();
        for(Object o : CraftingManager.getInstance().getRecipeList())
        {
            if (o instanceof IRecipe)
            {
                if (isItemEqual(((IRecipe)o).getRecipeOutput(), input))
                {
                    recipeList.add((IRecipe) o);
                }
            }
        }
        for (IRecipe recipe : recipeList)
        {
            for (ItemStack component : getComponents(recipe))
            {
                if (isItemEqual(output, component))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if 2 itemStacks contain the same item including the itemDamage
     * @param stack1
     * @param stack2
     * @return true when the 2 stack are the same. False in all other cases
     */
    public static boolean isItemEqual(ItemStack stack1, ItemStack stack2)
    {
        if (stack1 == null || stack2 == null) return false;
        else return (stack1.isItemEqual(stack2) && stack1.getItemDamage() == stack2.getItemDamage());
    }

    private static ItemStack[] getComponents(IRecipe recipe)
    {
        ItemStack[] components = new ItemStack[0];

        if (recipe instanceof ShapelessOreRecipe && ((ShapelessOreRecipe) recipe).getInput().size() > 0)
        {
            ArrayList<ItemStack> inputs = new ArrayList<ItemStack>();
            for (Object o : ((ShapelessOreRecipe) recipe).getInput())
            {
                if (o instanceof ItemStack)
                {
                    inputs.add((ItemStack) o);
                }
            }
            components = inputs.toArray(new ItemStack[inputs.size()]);
        } else if (recipe instanceof ShapedOreRecipe)
        {
            ArrayList<ItemStack> inputs = new ArrayList<ItemStack>();
            for (Object o : ((ShapedOreRecipe) recipe).getInput())
            {

                if (o instanceof ItemStack)
                {
                    inputs.add((ItemStack) o);
                } else if (o instanceof String)
                {
                    inputs.add(OreDictionary.getOres((String) o).get(0));
                } else if (o instanceof ArrayList && !((ArrayList) o).isEmpty())
                {
                    //TODO: pick the most basic results out of oredict - I am not sure if vanilla is always listed first
                    inputs.add((ItemStack) ((ArrayList) o).get(0));
                }
            }
            components = inputs.toArray(new ItemStack[inputs.size()]);

        } else if (recipe instanceof ShapelessRecipes && ((ShapelessRecipes) recipe).recipeItems.toArray() instanceof ItemStack[])
        {
            components = (ItemStack[]) ((ShapelessRecipes) recipe).recipeItems.toArray();
        } else if (recipe instanceof ShapedRecipes && ((ShapedRecipes) recipe).recipeItems instanceof ItemStack[])
        {
            components = ((ShapedRecipes) recipe).recipeItems;
        }

        return components;
    }

}
