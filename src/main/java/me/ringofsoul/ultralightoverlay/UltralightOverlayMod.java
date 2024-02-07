package me.ringofsoul.ultralightoverlay;

import com.labymedia.ultralight.UltralightRenderer;
import com.mojang.blaze3d.platform.InputConstants;
import me.ringofsoul.ultralightoverlay.ultralight.UltraLight;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.Minecraft;
import me.ringofsoul.ultralightoverlay.ultralight.MainGui;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(UltralightOverlayMod.MODID)
public final class UltralightOverlayMod {
   public static final String MODID = "ultralightoverlay";
   public static final String VERSION = "1.0";

   public MainGui MainGui;
   private UltralightRenderer renderer;

   public UltralightOverlayMod() {
      IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
      eventBus.addListener(this::onClientSetup);
   }

   private void onClientSetup(FMLClientSetupEvent event) {
      MinecraftForge.EVENT_BUS.register(this);
      UltraLight.init();
      //UltralightWindow.init();
      renderer = UltraLight.getRenderer();
      MainGui = new MainGui(renderer, "file://ultralight/public/index.html", 1920, 1080);
   }

   @SubscribeEvent
   public void onTick(TickEvent.ClientTickEvent event) {
      renderer.update();
      renderer.render();
      if (isPlayerInGame()) {
         if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_TAB) && Minecraft.getInstance().screen == null) {
            //Minecraft.getInstance().player.playSound(MODID + ":sound.enable", 10, 1);
            Minecraft.getInstance().setScreen(MainGui);
         }
      }
   }
   public static boolean isPlayerInGame() {
      Minecraft mc = Minecraft.getInstance();
      return mc.player != null && mc.level != null;
   }

}