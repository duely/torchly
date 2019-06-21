package com.noobanidus.torchly;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
@Mod(modid = Torchly.MODID, name = Torchly.MODNAME, version = Torchly.VERSION)
@SuppressWarnings("WeakerAccess")
public class Torchly {
  public static final String MODID = "torchly";
  public static final String MODNAME = "Torchly";
  public static final String VERSION = "GRADLE:VERSION";
  public static final CreativeTabs TAB = new CreativeTabs(CreativeTabs.getNextID(), "Torchly") {
    @Override
    public ItemStack createIcon() {
      return new ItemStack(torch);
    }
  };

  public final static Logger LOG = LogManager.getLogger(MODID);

  @Mod.Instance(MODID)
  public static Torchly instance;

  @Mod.EventHandler
  public static void preInit (FMLPreInitializationEvent event) {
    Network.registerPackets();
  }

  @Mod.EventHandler
  public static void loadComplete(FMLLoadCompleteEvent event) {
    LOG.info("RecipeDropper loaded.");
  }

  public static final Item torch = new ItemEnchantedTorch();

  @SubscribeEvent
  public static void onItemRegistration(RegistryEvent.Register<Item> event) {
    event.getRegistry().register(torch);
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public static void onModelRegistration(ModelRegistryEvent event) {
    ModelLoader.setCustomModelResourceLocation(torch, 0, new ModelResourceLocation(new ResourceLocation("minecraft", "torch"), "inventory"));
  }
}
