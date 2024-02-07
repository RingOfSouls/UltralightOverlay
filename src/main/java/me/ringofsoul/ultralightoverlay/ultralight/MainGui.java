package me.ringofsoul.ultralightoverlay.ultralight;

import com.labymedia.ultralight.UltralightRenderer;
import com.labymedia.ultralight.UltralightView;
import com.labymedia.ultralight.config.UltralightViewConfig;
import com.labymedia.ultralight.javascript.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import org.lwjgl.input.Mouse;

public class MainGui extends GuiScreen {

   private ViewController viewController;
   private float zoom = 1f;
   private float width = 100f;


   //file path should be under the run directory
   // relative to <modID>/ultralight
   public MainGui(UltralightRenderer renderer, String filePath, int width, int height) {
      UltralightViewConfig config = new UltralightViewConfig();
      config.isTransparent(true);

      UltralightView view = renderer.createView(width, height, config);
      viewController = new ViewController(renderer, view);

      view.loadURL(filePath);
      view.focus();
   }

   @Override
   public void setWorldAndResolution(Minecraft p_146280_1_, int p_146280_2_, int p_146280_3_) {
      super.setWorldAndResolution(p_146280_1_, p_146280_2_, p_146280_3_);
      //do not resize view controller here
   }

   @Override
   public void initGui() {
      super.initGui();
   }

   @Override
   public void drawScreen(int x, int y, float p) {
      viewController.update();
      viewController.render();
      viewController.onMouseMove(x, y);

      if (mc.displayWidth != viewController.getView().width() || mc.displayHeight != viewController.getView().height()) {
         viewController.resize(mc.displayWidth, mc.displayHeight);
         try {
            resizeZoom(mc.displayWidth, mc.displayHeight);
         } catch (JavascriptEvaluationException e) {
            e.printStackTrace();
         }
      }
   }

   public void resizeZoom(int displayWidth, int displayHeight) throws JavascriptEvaluationException {
//      if (displayHeight >= 720 && displayWidth >= 1280) {
//         zoom = 1.3f;
//
//      } else {
//         zoom = 0.75f;
//      }
//      width = 100 / zoom;
//
//      viewController.getView().evaluateScript("document.body.style.transformOrigin = \"left top\"");
//      viewController.getView().evaluateScript("document.body.style.transform = \"scale(\" + {} + \")\"".replace("{}", zoom + ""));
//      viewController.getView().evaluateScript("document.body.style.width = {} + \"%\"".replace("{}", width + ""));
   }

   @Override
   public void mouseClicked(int x, int y, int mouseButton) {
      viewController.onMouseClick(x, y, mouseButton, true);
   }

   @Override
   protected void mouseMovedOrUp(int x, int y, int mouseButton) {
      if (mouseButton==0) {
         viewController.onMouseClick(x, y, mouseButton, false);
      }
   }

   @Override
   public void keyTyped(char t, int k) {
      if (k==0) return;
      //esc
      if (k==1) {
         this.mc.displayGuiScreen(null);
         return;
      }
      viewController.onKeyDown(t, k);
   }

   @Override
   public void handleMouseInput() {
      int wheelDelta = Mouse.getEventDWheel();
      if (wheelDelta != 0) {
         viewController.onMouseScroll(0, wheelDelta);
      }
      super.handleMouseInput();
   }

   @Override
   public void onGuiClosed() {}

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

}
