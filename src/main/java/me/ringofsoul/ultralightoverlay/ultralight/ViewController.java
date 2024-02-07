package me.ringofsoul.ultralightoverlay.ultralight;

import com.labymedia.ultralight.UltralightPlatform;
import com.labymedia.ultralight.UltralightRenderer;
import com.labymedia.ultralight.UltralightView;
import com.labymedia.ultralight.bitmap.UltralightBitmap;
import com.labymedia.ultralight.bitmap.UltralightBitmapSurface;
import com.labymedia.ultralight.input.*;
import com.labymedia.ultralight.javascript.JavascriptContextLock;
import com.labymedia.ultralight.math.IntRect;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import me.ringofsoul.ultralightoverlay.ultralight.opengl.js.JavaScriptBridge;
import me.ringofsoul.ultralightoverlay.ultralight.opengl.listener.ModLoadListener;
import me.ringofsoul.ultralightoverlay.ultralight.opengl.util.UltralightKeyMapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

/**
 * Class used for controlling the WebGUI rendered on top of the OpenGL GUI.
 */
public class ViewController {
    private final UltralightPlatform platform;
    private final UltralightRenderer renderer;
    private final UltralightView view;

    private final ModLoadListener modLoadListener;
    private final JavaScriptBridge bridge;

    private int glTexture;
    private long lastJavascriptGarbageCollections;

    /**
     * Constructs a new {@link ViewController} and retrieves the platform.
     */
    public ViewController(UltralightRenderer renderer, UltralightView view) {
        this.platform = UltralightPlatform.instance();

        this.renderer = renderer;


        this.view = view;
        this.bridge = new JavaScriptBridge(view);
        this.modLoadListener = new ModLoadListener(view);
        this.view.setLoadListener(modLoadListener);

        this.glTexture = -1;
        this.lastJavascriptGarbageCollections = 0;
    }


    /**
     * Loads the specified URL into this controller.
     *
     * @param url The URL to load
     */
    public void loadURL(String url) {
        this.view.loadURL(url);
    }

    public JavaScriptBridge getJSBridge() {
        return bridge;
    }

    /**
     * Updates and renders the renderer
     */
    public void update() {
        this.renderer.update();
        this.renderer.render();

        if(lastJavascriptGarbageCollections == 0) {
            lastJavascriptGarbageCollections = System.currentTimeMillis();
        } else if(System.currentTimeMillis() - lastJavascriptGarbageCollections > 1000) {
            System.out.println("Garbage collecting Javascript...");
            try(JavascriptContextLock lock = this.view.lockJavascriptContext()) {
                lock.getContext().garbageCollect();
            }
            lastJavascriptGarbageCollections = System.currentTimeMillis();
        }
    }

    /**
     * Resizes the web view.
     *
     * @param width  The new view width
     * @param height The new view height
     */
    public void resize(int width, int height) {
        this.view.resize(width, height);
    }

    /**
     * Render the current image using OpenGL
     */
//==========================OLD METHODS FROM 1.7.10======================
//    public void render() {
//        //UltralightWindow.getInstance().makeCurrent();
//        if(glTexture == -1) {
//            createGLTexture();
//        }
//
//        UltralightBitmapSurface surface = (UltralightBitmapSurface) this.view.surface();
//        UltralightBitmap bitmap = surface.bitmap();
//
//        int width = (int) view.width();
//        int height = (int) view.height();
//
//        // Prepare OpenGL for 2D textures and bind our texture
//        glEnable(GL_TEXTURE_2D);
//        glBindTexture(GL_TEXTURE_2D, this.glTexture);
//
//
//        IntRect dirtyBounds = surface.dirtyBounds();
//
//        if(dirtyBounds.isValid()) {
//            ByteBuffer imageData = bitmap.lockPixels();
//            glPixelStorei(GL_UNPACK_ROW_LENGTH, (int) bitmap.rowBytes() / 4);
//            if(dirtyBounds.width() == width && dirtyBounds.height() == height) {
//                // Update full image
//                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, imageData);
//                glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
//            } else {
//                // Update partial image
//                int x = dirtyBounds.x();
//                int y = dirtyBounds.y();
//                int dirtyWidth = dirtyBounds.width();
//                int dirtyHeight = dirtyBounds.height();
//                int startOffset = (int) ((y * bitmap.rowBytes()) + x * 4);
//
//                glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, dirtyWidth, dirtyHeight, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, (ByteBuffer) imageData.position(startOffset));
//            }
//            glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
//
//            bitmap.unlockPixels();
//            surface.clearDirtyBounds();
//        }
//
//        // Set up the OpenGL state for rendering of a fullscreen quad
//        glPushAttrib(GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT | GL_TRANSFORM_BIT);
//        glMatrixMode(GL_PROJECTION);
//        glPushMatrix();
//        glLoadIdentity();
//        glOrtho(0, this.view.width(), this.view.height(), 0, -1, 1);
//        glMatrixMode(GL_MODELVIEW);
//        glPushMatrix();
//
//        // Disable lighting and scissoring, they could mess up th renderer
//        glLoadIdentity();
//        glDisable(GL_LIGHTING);
//        glDisable(GL_SCISSOR_TEST);
//        glEnable(GL_BLEND);
//        glEnable(GL_TEXTURE_2D);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//
//        // Make sure we draw with a neutral color
//        // (so we don't mess with the color channels of the image)
//        glColor4f(1, 1, 1, 1f);
//
//        glBegin(GL_QUADS);
//
//        // Lower left corner, 0/0 on the screen space, and 0/0 of the image UV
//        glTexCoord2f(0, 0);
//        glVertex2f(0, 0);
//
//        // Upper left corner
//        glTexCoord2f(0, 1);
//        glVertex2i(0, height);
//
//        // Upper right corner
//        glTexCoord2f(1, 1);
//        glVertex2i(width, height);
//
//        // Lower right corner
//        glTexCoord2f(1, 0);
//        glVertex2i(width, 0);
//
//        glEnd();
//
//        glBindTexture(GL_TEXTURE_2D, 0);
//
//        // Restore OpenGL state
//        glPopMatrix();
//        glMatrixMode(GL_PROJECTION);
//        glPopMatrix();
//        glMatrixMode(GL_MODELVIEW);
//
//        glDisable(GL_TEXTURE_2D);
//        glPopAttrib();
//        //UltralightWindow.getInstance().unmakeCurrent();
//    }
//    private void createGLTexture() {
//        glEnable(GL_TEXTURE_2D);
//        this.glTexture = glGenTextures();
//        glBindTexture(GL_TEXTURE_2D, this.glTexture);
//
//        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
//        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//        glBindTexture(GL_TEXTURE_2D, 0);
//        glDisable(GL_TEXTURE_2D);
//    }

    public void render(PoseStack poseStack) {
        if(glTexture == -1) {
            createGLTexture();
        }

        UltralightBitmapSurface surface = (UltralightBitmapSurface) this.view.surface();
        UltralightBitmap bitmap = surface.bitmap();

        int width = (int) view.width();
        int height = (int) view.height();

        // Prepare OpenGL for 2D textures and bind our texture
        RenderSystem.enableTexture(); //glEnable(GL_TEXTURE_2D);
        RenderSystem.bindTexture(this.glTexture); //GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.glTexture);

        IntRect dirtyBounds = surface.dirtyBounds();

        Logger.getLogger("TEST").info("IM HERE!");
        Logger.getLogger("TEST").info(dirtyBounds.x() + ", " + dirtyBounds.y() + ", " + dirtyBounds.width() + ", " + dirtyBounds.height());
        Logger.getLogger("TEST").info(new File(".").getAbsolutePath());

        //Генерация картинки. Она должна соответствовать сайту, который указан в UltralightOverlayMod.
        // На 1.7.10 картинка корректно генерируется в соответствии с содержанием сайта.
        //Код, библиотеки и зависимости Ultralight идентичные тому, что на 1.7.10
        bitmap.writePNG(new File(".").getAbsolutePath() + File.separator + "test.png");

        //Добавил сюда, чтобы после генерации картинки оно сразу завершило работу Minecraft.
        //Весь последующий рендер не может быть протестирован, пока Ultralight не будет возвращать корректное изображение
        throw new NullPointerException();








//        if(dirtyBounds.isValid()) {
//            ByteBuffer imageData = bitmap.lockPixels();
//
//            RenderSystem.pixelStore(GL_UNPACK_ROW_LENGTH, (int) bitmap.rowBytes() / 4); //glPixelStorei(GL_UNPACK_ROW_LENGTH, (int) bitmap.rowBytes() / 4);
//            if(dirtyBounds.width() == width && dirtyBounds.height() == height) {
//                // Update full image
//                //GlStateManager._texImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, imageData.asIntBuffer());
//                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, imageData.asIntBuffer());
//                RenderSystem.pixelStore(GL_UNPACK_ROW_LENGTH, 0); //glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
//            } else {
//                // Update partial image
//                int x = dirtyBounds.x();
//                int y = dirtyBounds.y();
//                int dirtyWidth = dirtyBounds.width();
//                int dirtyHeight = dirtyBounds.height();
//                int startOffset = (int) ((y * bitmap.rowBytes()) + x * 4);
//
//                //GlStateManager._texSubImage2D(GL_TEXTURE_2D, 0, x, y, dirtyWidth, dirtyHeight, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, imageData.position(startOffset))
//                glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, dirtyWidth, dirtyHeight, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, imageData.position(startOffset));
//            }
//            glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
//
//            bitmap.unlockPixels();
//            surface.clearDirtyBounds();
//        }
//        // Set up the OpenGL state for rendering of a fullscreen quad
//        //glPushAttrib(GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT | GL_TRANSFORM_BIT);
//        RenderSystem.backupProjectionMatrix(); //glMatrixMode(GL_PROJECTION);
//        poseStack.pushPose(); //glPushMatrix();
//        poseStack.setIdentity(); //glLoadIdentity();
//        poseStack.mulPoseMatrix(Matrix4f.orthographic(0.0F, this.view.width(), this.view.height(), 0.0F, -1.0F, 1.0F)); //glOrtho(0.0D, this.view.width(), this.view.height(), 0.0D, -1.0D, 1.0D);
//        RenderSystem.applyModelViewMatrix(); //glMatrixMode(GL_MODELVIEW);
//        poseStack.pushPose(); //glPushMatrix();
//
//        // Disable lighting and scissoring, they could mess up th renderer
//        poseStack.setIdentity(); //glLoadIdentity();
//        //glDisable(GL_LIGHTING);
//        RenderSystem.disableScissor(); //glDisable(GL_SCISSOR_TEST);
//        RenderSystem.enableBlend(); //glEnable(GL_BLEND);
//        RenderSystem.enableTexture(); //glEnable(GL_TEXTURE_2D);
//        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA); //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//
//        // Make sure we draw with a neutral color
//        // (so we don't mess with the color channels of the image)
//        RenderSystem.setShaderTexture(0, this.glTexture);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); //glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//        //RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
//
//        poseStack.translate(0.0F, 0.0F, 1.0F);
//        Matrix4f mat = poseStack.last().pose();
//        Tesselator tesselator = Tesselator.getInstance();
//        BufferBuilder bufferbuilder = tesselator.getBuilder();
//        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
//
//        bufferbuilder.vertex(mat, 0, height, 0).uv(0, 1).color(255, 255, 255, 255).endVertex();
//        bufferbuilder.vertex(mat, width, height, 0).uv(1, 1).color(255, 255, 255, 255).endVertex();
//        bufferbuilder.vertex(mat, width, 0, 0).uv(1, 0).color(255, 255, 255, 255).endVertex();
//        bufferbuilder.vertex(mat, 0, 0, 0).uv(0, 0).color(255, 255, 255, 255).endVertex();
//        tesselator.end();
//
//        RenderSystem.bindTexture(0); //glBindTexture(GL_TEXTURE_2D, 0);
//
//        // Restore OpenGL state
//        poseStack.popPose(); //glPopMatrix();
//        RenderSystem.restoreProjectionMatrix(); //glMatrixMode(GL_PROJECTION);
//        poseStack.popPose(); //glPopMatrix();
//        RenderSystem.applyModelViewMatrix(); //glMatrixMode(GL_MODELVIEW);
//
//        RenderSystem.disableTexture(); //glDisable(GL_TEXTURE_2D);
    }
    public UltralightView getView() {
        return view;
    }

    /**
     * Sets up the OpenGL texture for rendering
     */
    private void createGLTexture() {
        RenderSystem.enableTexture(); //glEnable(GL_TEXTURE_2D);
        this.glTexture = glGenTextures();
        RenderSystem.bindTexture(this.glTexture); //glBindTexture(GL_TEXTURE_2D, this.glTexture);

        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        RenderSystem.bindTexture(0); //glBindTexture(GL_TEXTURE_2D, 0);
        RenderSystem.disableTexture(); //glDisable(GL_TEXTURE_2D);
    }

    public void onMouseClick(int x, int y, int mouseButton, boolean buttonDown) {
        UltralightMouseEvent event = new UltralightMouseEvent();
        UltralightMouseEventButton button;
        switch (mouseButton) {
            case 0:
                button = UltralightMouseEventButton.LEFT;
                break;
            case 1:
                button = UltralightMouseEventButton.RIGHT;
                break;
            case 3:
            default:
                button = UltralightMouseEventButton.MIDDLE;
                break;

        }
        event.button(button);
        event.x(x * (int)Minecraft.getInstance().getWindow().getGuiScale());
        event.y(y * (int)Minecraft.getInstance().getWindow().getGuiScale());
        event.type(buttonDown ? UltralightMouseEventType.DOWN : UltralightMouseEventType.UP);

        view.fireMouseEvent(event);
    }

    public void onMouseMove(int x, int y) {
        UltralightMouseEvent event = new UltralightMouseEvent();
        event.x(x * (int)Minecraft.getInstance().getWindow().getGuiScale());
        event.y(y * (int)Minecraft.getInstance().getWindow().getGuiScale());
        event.type(UltralightMouseEventType.MOVED);
        view.fireMouseEvent(event);
    }

    public void onMouseScroll(int deltaX, int deltaY) {
        UltralightScrollEvent scrollEvent = new UltralightScrollEvent();
        scrollEvent.deltaY(deltaY);
        scrollEvent.deltaX(deltaX);
        scrollEvent.type(UltralightScrollEventType.BY_PIXEL);
        view.fireScrollEvent(scrollEvent);
    }

    public void onKeyDown(char c, int key) {
        UltralightKeyEvent event = new UltralightKeyEvent();
        event.virtualKeyCode(UltralightKeyMapper.getKey(key));
        event.unmodifiedText(String.valueOf(c));

        UltralightKeyMapper.KeyType keyType = UltralightKeyMapper.getKeyType(key);

        if (keyType == UltralightKeyMapper.KeyType.ACTION) {
            event.type(UltralightKeyEventType.RAW_DOWN);

        } else if (keyType == UltralightKeyMapper.KeyType.CHAR) {
            event.type(UltralightKeyEventType.CHAR);
        }

        event.text(String.valueOf(c));
        event.keyIdentifier(UltralightKeyEvent.getKeyIdentifierFromVirtualKeyCode(UltralightKeyMapper.getKey(key)));

        view.fireKeyEvent(event);
    }
}
