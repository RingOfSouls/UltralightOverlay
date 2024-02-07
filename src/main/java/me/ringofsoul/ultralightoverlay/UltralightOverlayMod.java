package me.ringofsoul.ultralightoverlay;

import com.labymedia.ultralight.UltralightRenderer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import me.ringofsoul.ultralightoverlay.ultralight.UltraLight;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import me.ringofsoul.ultralightoverlay.ultralight.MainGui;

@Mod(
        modid = UltralightOverlayMod.MODID,
        name = "UltralightOverlay",
        version = UltralightOverlayMod.VERSION,
        acceptedMinecraftVersions = "[1.7.10]",
        acceptableRemoteVersions = "*"
)
public final class UltralightOverlayMod {
   public static final String MODID = "ultralightoverlay";
   public static final String VERSION = "1";

   public MainGui MainGui;
   private UltralightRenderer renderer;

   @Mod.EventHandler
   public void init(FMLInitializationEvent event) {
      FMLCommonHandler.instance().bus().register(this);
      UltraLight.init();
      renderer = UltraLight.getRenderer();
      MainGui = new MainGui(renderer, "https://forum.corecraft.ru/forums/3/", 1920, 1080);
   }

   @SubscribeEvent
   public void onTick(TickEvent.ClientTickEvent event) {
      renderer.update();
      renderer.render();
      if (isPlayerInGame()) {
         //right shift
         if (Keyboard.isKeyDown(Keyboard.KEY_TAB) && Minecraft.getMinecraft().currentScreen == null) {
            Minecraft.getMinecraft().thePlayer.playSound(MODID + ":sound.enable", 10, 1);
            Minecraft.getMinecraft().displayGuiScreen(MainGui);
         }
      }
   }
   public static boolean isPlayerInGame() {
      Minecraft mc = Minecraft.getMinecraft();
      return mc.thePlayer != null && mc.theWorld != null;
   }

}