package minechem.client.gui;

import minechem.inventory.slot.SlotSynthesisOutput;
import minechem.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiContainerBase extends GuiContainer {

	protected final FontRenderer fontRenderer;
	protected RenderItem renderItem;

	public GuiContainerBase(Container container) {
		this(container, null);
	}

	public GuiContainerBase(Container container, RenderItem itemRenderer) {
		super(container);
		inventorySlots = container;
		fontRenderer = Minecraft.getMinecraft().getRenderManager().getFontRenderer();
		renderItem = itemRenderer == null ? Minecraft.getMinecraft().getRenderItem() : itemRenderer;
		//itemRender = renderItem;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		// start replacement super.drawScreen(mouseX, mouseY, partialTicks);
		int i = guiLeft;
		int j = guiTop;
		drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		//super.drawScreen(mouseX, mouseY, partialTicks);
		for (int ii = 0; ii < buttonList.size(); ++ii) {
			buttonList.get(ii).drawButton(mc, mouseX, mouseY, partialTicks);
		}

		for (int jj = 0; jj < labelList.size(); ++jj) {
			labelList.get(jj).drawLabel(mc, mouseX, mouseY);
		}

		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.pushMatrix();
		GlStateManager.translate(i, j, 0.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableRescaleNormal();
		hoveredSlot = null;
		int k = 240;
		int l = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		for (int i1 = 0; i1 < inventorySlots.inventorySlots.size(); ++i1) {
			Slot slot = inventorySlots.inventorySlots.get(i1);

			if (slot.isEnabled()) {
				drawSlotCustom(slot);
			}

			if (isOverSlot(slot, mouseX, mouseY) && slot.isEnabled()) {
				hoveredSlot = slot;
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				int j1 = slot.xPos;
				int k1 = slot.yPos;
				GlStateManager.colorMask(true, true, true, false);
				drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
			}
		}

		RenderHelper.disableStandardItemLighting();
		drawGuiContainerForegroundLayer(mouseX, mouseY);
		RenderHelper.enableGUIStandardItemLighting();
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiContainerEvent.DrawForeground(this, mouseX, mouseY));
		InventoryPlayer inventoryplayer = mc.player.inventory;
		ItemStack itemstack = draggedStack.isEmpty() ? inventoryplayer.getItemStack() : draggedStack;

		if (!itemstack.isEmpty()) {
			int j2 = 8;
			int k2 = draggedStack.isEmpty() ? 8 : 16;
			String s = null;

			if (!draggedStack.isEmpty() && isRightMouseClick) {
				itemstack = itemstack.copy();
				itemstack.setCount(MathHelper.ceil(itemstack.getCount() / 2.0F));
			}
			else if (dragSplitting && dragSplittingSlots.size() > 1) {
				itemstack = itemstack.copy();
				itemstack.setCount(dragSplittingRemnant);

				if (itemstack.isEmpty()) {
					s = "" + TextFormatting.YELLOW + "0";
				}
			}

			drawStack(itemstack, mouseX - i - 8, mouseY - j - k2, s);
		}

		if (!returningStack.isEmpty()) {
			float f = (Minecraft.getSystemTime() - returningStackTime) / 100.0F;

			if (f >= 1.0F) {
				f = 1.0F;
				returningStack = ItemStack.EMPTY;
			}

			int l2 = returningStackDestSlot.xPos - touchUpX;
			int i3 = returningStackDestSlot.yPos - touchUpY;
			int l1 = touchUpX + (int) (l2 * f);
			int i2 = touchUpY + (int) (i3 * f);
			drawStack(returningStack, l1, i2, (String) null);
		}

		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		RenderHelper.enableStandardItemLighting();

		renderHoveredToolTip(mouseX, mouseY);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
	}

	protected boolean isOverSlot(Slot slotIn, int mouseX, int mouseY) {
		return isPointInRegion(slotIn.xPos, slotIn.yPos, 16, 16, mouseX, mouseY);
	}

	protected void drawStack(ItemStack stack, int x, int y, String altText) {
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		zLevel = 200.0F;
		itemRender.zLevel = 200.0F;
		FontRenderer font = stack.getItem().getFontRenderer(stack);
		if (font == null) {
			font = fontRenderer;
		}
		itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		itemRender.renderItemOverlayIntoGUI(font, stack, x, y - (draggedStack.isEmpty() ? 0 : 8), altText);
		zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
	}

	@Override
	public int getXSize() {
		return xSize;
	}

	@Override
	public int getYSize() {
		return ySize;
	}

	@Override
	public int getGuiTop() {
		return guiTop;
	}

	@Override
	public int getGuiLeft() {
		return guiLeft;
	}

	protected void drawSlotCustom(Slot slotIn) {
		int i = slotIn.xPos;
		int j = slotIn.yPos;
		ItemStack itemstack = slotIn.getStack();
		boolean flag = false;
		boolean flag1 = slotIn == clickedSlot && !draggedStack.isEmpty() && !isRightMouseClick;
		ItemStack itemstack1 = mc.player.inventory.getItemStack();
		String s = null;

		if (slotIn == clickedSlot && !draggedStack.isEmpty() && isRightMouseClick && !itemstack.isEmpty()) {
			itemstack = itemstack.copy();
			itemstack.setCount(itemstack.getCount() / 2);
		}
		else if (dragSplitting && dragSplittingSlots.contains(slotIn) && !itemstack1.isEmpty()) {
			if (dragSplittingSlots.size() == 1) {
				return;
			}
			if (Container.canAddItemToSlot(slotIn, itemstack1, true) && inventorySlots.canDragIntoSlot(slotIn)) {
				itemstack = itemstack1.copy();
				flag = true;
				Container.computeStackSize(dragSplittingSlots, dragSplittingLimit, itemstack, slotIn.getStack().isEmpty() ? 0 : slotIn.getStack().getCount());
				int k = Math.min(itemstack.getMaxStackSize(), slotIn.getItemStackLimit(itemstack));
				if (itemstack.getCount() > k) {
					s = TextFormatting.YELLOW.toString() + k;
					itemstack.setCount(k);
				}
			}
			else {
				dragSplittingSlots.remove(slotIn);
				updateDrag();
			}
		}
		zLevel = 100.0F;
		itemRender.zLevel = 100.0F;
		if (itemstack.isEmpty() && slotIn.isEnabled()) {
			TextureAtlasSprite textureatlassprite = slotIn.getBackgroundSprite();
			if (textureatlassprite != null) {
				GlStateManager.disableLighting();
				mc.getTextureManager().bindTexture(slotIn.getBackgroundLocation());
				this.drawTexturedModalRect(i, j, textureatlassprite, 16, 16);
				GlStateManager.enableLighting();
				flag1 = true;
			}
		}

		if (!flag1) {
			if (flag) {
				drawRect(i, j, i + 16, j + 16, -2130706433);
			}
			GlStateManager.enableDepth();
			Slot curSlot = null;
			for (int ii = 0; ii < inventorySlots.inventorySlots.size(); ++ii) {
				Slot slot = inventorySlots.inventorySlots.get(ii);
				if (slot.xPos == i && slot.yPos == j) {
					curSlot = slot;
				}
			}

			if (curSlot != null && curSlot instanceof SlotSynthesisOutput && !itemstack.isEmpty()) {
				GlStateManager.pushMatrix();
				TextureManager textureManager = mc.renderEngine;
				textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
				GlStateManager.enableRescaleNormal();
				GlStateManager.enableAlpha();
				//GlStateManager.alphaFunc(516, 0.1F);
				GlStateManager.enableBlend();
				//GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(0.0F, 1.0F, 1.0F, 1.0F);
				IBakedModel bakedmodel = itemRender.getItemModelWithOverrides(itemstack, (World) null, mc.player);
				//itemRender.setupGuiTransform(i, j, bakedmodel.isGui3d());
				GlStateManager.translate(i, j, 100.0F + zLevel);
				GlStateManager.translate(8.0F, 8.0F, 0.0F);
				GlStateManager.scale(1.0F, -1.0F, 1.0F);
				GlStateManager.scale(16.0F, 16.0F, 16.0F);
				if (bakedmodel.isGui3d()) {
					GlStateManager.enableLighting();
				}
				else {
					GlStateManager.disableLighting();
				}
				bakedmodel = ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
				//itemRender.renderItem(itemstack, bakedmodel);
				//if (!stack.isEmpty())
				// {
				GlStateManager.pushMatrix();
				GlStateManager.translate(-0.5F, -0.5F, -0.5F);

				if (bakedmodel.isBuiltInRenderer()) {
					GlStateManager.enableRescaleNormal();
					itemstack.getItem().getTileEntityItemStackRenderer().renderByItem(itemstack);
				}
				else {
					RenderUtil.render(bakedmodel, 0xFFFFFFFF, itemstack);

					if (itemstack.hasEffect()) {
						//itemRender.renderEffect(bakedmodel);
					}
				}

				GlStateManager.popMatrix();
				//}
				GlStateManager.disableAlpha();
				GlStateManager.disableRescaleNormal();
				GlStateManager.disableLighting();
				GlStateManager.popMatrix();
				textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
			}
			else {
				itemRender.renderItemAndEffectIntoGUI(mc.player, itemstack, i, j);
			}
			itemRender.renderItemOverlayIntoGUI(fontRenderer, itemstack, i, j, s);

		}
		itemRender.zLevel = 0.0F;
		zLevel = 0.0F;
	}

	protected void updateDrag() {
		ItemStack itemstack = mc.player.inventory.getItemStack();

		if (!itemstack.isEmpty() && dragSplitting) {
			if (dragSplittingLimit == 2) {
				dragSplittingRemnant = itemstack.getMaxStackSize();
			}
			else {
				dragSplittingRemnant = itemstack.getCount();

				for (Slot slot : dragSplittingSlots) {
					ItemStack itemstack1 = itemstack.copy();
					ItemStack itemstack2 = slot.getStack();
					int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
					Container.computeStackSize(dragSplittingSlots, dragSplittingLimit, itemstack1, i);
					int j = Math.min(itemstack1.getMaxStackSize(), slot.getItemStackLimit(itemstack1));

					if (itemstack1.getCount() > j) {
						itemstack1.setCount(j);
					}

					dragSplittingRemnant -= itemstack1.getCount() - i;
				}
			}
		}
	}

}
