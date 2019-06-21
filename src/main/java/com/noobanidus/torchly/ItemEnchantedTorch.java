package com.noobanidus.torchly;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

@SuppressWarnings("deprecation")
public class ItemEnchantedTorch extends Item {
  public ItemEnchantedTorch() {
    setTranslationKey("enchanted_torch");
    setRegistryName(Torchly.MODID, "enchanted_torch");
    setMaxStackSize(1);
    setCreativeTab(Torchly.TAB);
  }

  @Override
  public EnumRarity getRarity(ItemStack stack) {
    return EnumRarity.EPIC;
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return true;
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
    return true;
  }

  @Override
  public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
    if (world.isRemote) return EnumActionResult.SUCCESS;
    TileEntity te = world.getTileEntity(pos);
    if (te != null) {
      IItemHandler cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
      if (cap != null) {
        StringJoiner items = new StringJoiner(",", "[", "]");
        int x = 0;
        for (int i = 0; i < cap.getSlots(); i++) {
          ItemStack stack = cap.getStackInSlot(i);
          if (stack.isEmpty()) continue;
          Item item = stack.getItem();
          FancyTagCompound ftc = new FancyTagCompound();
          ftc.setString("item", item.getRegistryName().toString());
          ftc.setInteger("count", stack.getCount());
          ftc.setInteger("meta", stack.getMetadata());
          if (stack.getTagCompound() != null && !stack.getTagCompound().isEmpty()) {
            ftc.setString("tag", stack.getTagCompound().toString());
          }
          items.add(ftc.toString());
          x++;
        }
        if (x > 0) {
          Network.CopyToClipboard message = new Network.CopyToClipboard(items.toString());
          Network.instance.sendTo(message, (EntityPlayerMP) player);
          player.sendMessage(new TextComponentString("Transferred " + x + " items to your clipboard.").setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)));
        } else {
          player.sendMessage(new TextComponentString("This chest or container is empty.").setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)));
        }
      } else {
        player.sendMessage(new TextComponentString("This tile entity does not provide an item handler or contain an accessible inventory.").setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)));
      }
    } else {
      player.sendMessage(new TextComponentString("This block does not have a tile entity attached.").setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)));
    }

    return EnumActionResult.SUCCESS;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);

    tooltip.add("");
    tooltip.add(TextFormatting.LIGHT_PURPLE + "Copies chest contents to clipboard as a properly-formatted JSON string (nbt embedded as a string).");
  }

  public class FancyTagCompound extends NBTTagCompound {
    @Override
    public String toString() {
      StringBuilder stringbuilder = new StringBuilder("{");
      Collection<String> collection = this.getKeySet();

      for (String s : collection) {
        if (stringbuilder.length() != 1) {
          stringbuilder.append(',');
        }

        stringbuilder.append("\"").append(handleEscape(s)).append("\"").append(':').append(getTag(s));
      }

      return stringbuilder.append('}').toString();
    }
  }
}
