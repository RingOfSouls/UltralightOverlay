package me.ringofsoul.ultralightoverlay.ultralight;

import com.labymedia.ultralight.UltralightRenderer;
import com.labymedia.ultralight.UltralightView;
import com.labymedia.ultralight.config.UltralightViewConfig;
import com.labymedia.ultralight.javascript.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MainGui extends Screen {

   private ViewController viewController;
   private float zoom = 1f;
   private float width = 100f;


   //file path should be under the run directory
   // relative to <modID>/ultralight
   public MainGui(UltralightRenderer renderer, String filePath, int width, int height) {
      super(Component.empty());
      this.minecraft = Minecraft.getInstance();
      UltralightViewConfig config = new UltralightViewConfig();
      config.isTransparent(true);

      UltralightView view = renderer.createView(width, height, config);
      viewController = new ViewController(renderer, view);

      view.loadURL(filePath);
      view.focus();
   }

   @Override
   public void resize(Minecraft minecraft, int x, int y) {
      super.resize(minecraft, x, y);
      //do not resize view controller here
   }

   @Override
   protected void init() {
      super.init();
   }

   @Override
   public void render(PoseStack poseStack, int x, int y, float ticks) {
      viewController.update();
      viewController.render(poseStack);
      viewController.onMouseMove(x, y);


      if (this.minecraft.getWindow().getWidth() != viewController.getView().width() || this.minecraft.getWindow().getHeight() != viewController.getView().height()) {
         viewController.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
         try {
            resizeZoom(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
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
   public boolean mouseClicked(double x, double y, int mouseButton) {
      viewController.onMouseClick((int)x, (int)y, mouseButton, true);
      return true;
   }

   @Override
   public boolean mouseReleased(double x, double y, int mouseButton) {
      viewController.onMouseClick((int)x, (int)y, mouseButton, false);
      return true;
   }

   @Override
   public boolean keyPressed(int key, int scan, int i) {
      if (key==0) return true;
      //esc
      if (key==1) {
         this.minecraft.setScreen(null);
         return true;
      }
      viewController.onKeyDown((char) key, key);
      return true;
   }

   @Override
   public boolean mouseScrolled(double x, double y, double wheelDelta) {
      if (wheelDelta != 0) {
         viewController.onMouseScroll(0, (int)wheelDelta);
      }
      return true;
   }

   @Override
   public void onClose() {
      super.onClose();
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

}
