package LWJGLPackage;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.*;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import MapKernel.MapWizard;
import OpenGLPackage.mdesl.graphics.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.GLUtessellator;
import sun.java2d.loops.DrawPolygons;

import javax.swing.*;

public class OriginalOpenGLWizard{

    /**
     * position of quad
     */
    float x = 512, y = 384;
    int ScreenWidth = -1;
    int ScreenHeight = -1;
    double OriginLongitude  = -1; /** Left Down */
    double OriginLatitude   = -1; /** Left Down */
    double LongitudeScale   = -1; /** Left Down */
    double LatitudeScale    = -1; /** Left Down */
    /**
     * angle of quad rotation
     */

    /**
     * time at last frame
     */
    long lastFrame;

    /**
     * frames per second
     */
    int fps;
    /**
     * last fps time
     */
    long lastFPS;

    /**
     * is VSync Enabled
     */
    boolean vsync;

    public static Thread SingleItem = null;
    public static void GetInstance(){
        String str_width= JOptionPane.showInputDialog(null, "Screen Width");
        String str_height=JOptionPane.showInputDialog(null,"Screnn Height");
        try{
            double width = Integer.parseInt(str_width);
            double height = Integer.parseInt(str_height);
            GetInstance((int) width, (int) height, MapWizard.SingleItem.Screen.ScreenLongitude,
                    MapWizard.SingleItem.Screen.ScreenLatitude - MapWizard.SingleItem.Screen.LatitudeScale,
                    MapWizard.SingleItem.Screen.LongitudeScale / 600 * width , MapWizard.SingleItem.Screen.LatitudeScale / 600 * height);
        }catch(Exception ex){
            ex.printStackTrace();
            return;
        }
    }
    public static void GetInstance(final int ScreenWidth,final int ScreenHeight,final double OriginLongitude,final double OriginLatitude,final double LongitudeScale,final double LatitudeScale){
        if(SingleItem != null){
            javax.swing.JOptionPane.showMessageDialog(null,"Another Instance is Running");
            return;
        }

        SingleItem =
        new Thread(new Runnable() {
            @Override
            public void run() {
                OriginalOpenGLWizard displayExample = new OriginalOpenGLWizard();
                displayExample.start(ScreenWidth, ScreenHeight, OriginLongitude, OriginLatitude, LongitudeScale, LatitudeScale);
                LWJGLPackage.OriginalOpenGLWizard.SingleItem = null;
            }
        });

        SingleItem.start();
    }

    public void start(int ScreenWidth, int ScreenHeight, double OriginLongitude, double OriginLatitude, double LongitudeScale, double LatitudeScale) {
        this.ScreenWidth        = ScreenWidth;
        this.ScreenHeight       = ScreenHeight;
        this.OriginLongitude    = OriginLongitude;
        this.OriginLatitude     = OriginLatitude;
        this.LongitudeScale     = LongitudeScale;
        this.LatitudeScale      = LatitudeScale;
        try {
            Display.setDisplayMode(new DisplayMode(this.ScreenWidth, this.ScreenHeight));
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        initGL(this.ScreenWidth, this.ScreenHeight); // init OpenGL
        getDelta(); // call once before loop to initialise lastFrame
        lastFPS = getTime(); // call before loop to initialise fps timer

        while (!Display.isCloseRequested()) {
            int delta = getDelta();

            update(delta);
            renderGL();

            Display.update();
            Display.sync(60); // cap fps to 60fps
        }

        Display.destroy();

        if(MapWizard.SingleItem != null)
            if(!MapWizard.SingleItem.isVisible())
                System.exit(0);
    }

    public void update(int delta) {
        // rotate quad

        /*
        rotation += 0.15f * delta;

        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) x -= 0.35f * delta;
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) x += 0.35f * delta;

        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) y -= 0.35f * delta;
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) y += 0.35f * delta;

        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (Keyboard.getEventKey() == Keyboard.KEY_F) {
                    setDisplayMode(1024, 768, !Display.isFullscreen());
                } else if (Keyboard.getEventKey() == Keyboard.KEY_V) {
                    vsync = !vsync;
                    Display.setVSyncEnabled(vsync);
                }
            }
        }

        // keep quad on the screen
        if (x < 0) x = 0;
        if (x > 1024) x = 1024;
        if (y < 0) y = 0;
        if (y > 768) y = 768;
        */
        HandleInput();

        updateFPS(); // update FPS Counter
    }

    /**
     * Set the display mode to be used
     *
     * @param width      The width of the display required
     * @param height     The height of the display required
     * @param fullscreen True if we want fullscreen mode
     */
    public void setDisplayMode(int width, int height, boolean fullscreen) {

        // return if requested DisplayMode is already set
        if ((Display.getDisplayMode().getWidth() == width) &&
                (Display.getDisplayMode().getHeight() == height) &&
                (Display.isFullscreen() == fullscreen)) {
            return;
        }

        try {
            DisplayMode targetDisplayMode = null;

            if (fullscreen) {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                int freq = 0;

                for (int i = 0; i < modes.length; i++) {
                    DisplayMode current = modes[i];

                    if ((current.getWidth() == width) && (current.getHeight() == height)) {
                        if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
                            if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
                                targetDisplayMode = current;
                                freq = targetDisplayMode.getFrequency();
                            }
                        }

                        // if we've found a match for bpp and frequence against the 
                        // original display mode then it's probably best to go for this one
                        // since it's most likely compatible with the monitor
                        if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
                                (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
                            targetDisplayMode = current;
                            break;
                        }
                    }
                }
            } else {
                targetDisplayMode = new DisplayMode(width, height);
            }

            if (targetDisplayMode == null) {
                System.out.println("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
                return;
            }

            Display.setDisplayMode(targetDisplayMode);
            Display.setFullscreen(fullscreen);

        } catch (LWJGLException e) {
            System.out.println("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
        }
    }

    /**
     * Calculate how many milliseconds have passed
     * since last frame.
     *
     * @return milliseconds passed since last frame
     */
    public int getDelta() {
        long time = getTime();
        int delta = (int) (time - lastFrame);
        lastFrame = time;

        return delta;
    }

    /**
     * Get the accurate system time
     *
     * @return The system time in milliseconds
     */
    public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    /**
     * Calculate the FPS and set it in the title bar
     */
    public void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            Display.setTitle("FPS: " + fps);
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
    }

    public void initGL(int Width, int Height) {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, Width, 0, Height, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    public static void drawString(String s, int x, int y) {
        GL11.glPointSize(2);
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        int startX = x;
        GL11.glBegin(GL11.GL_POINTS);
        for (char c : s.toCharArray()) {
            if (c == 'A') {
                for (int i = 0; i < 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                    GL11.glVertex2f(x + 7, y + i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 8);
                    GL11.glVertex2f(x + i, y + 4);
                }
                x += 8;
            } else if (c == 'a'){
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2f(x + i, y + 0);
                    GL11.glVertex2f(x + i, y + 3);
                    GL11.glVertex2f(x + i, y + 6);
                }
                GL11.glVertex2f(x + 1, y + 1);
                GL11.glVertex2f(x + 1, y + 2);
                GL11.glVertex2f(x + 6, y + 0);
                GL11.glVertex2f(x + 6, y + 1);
                GL11.glVertex2f(x + 6, y + 2);
                GL11.glVertex2f(x + 6, y + 3);
                GL11.glVertex2f(x + 6, y + 4);
                GL11.glVertex2f(x + 6, y + 5);
                x += 7;
            } else if (c == 'B') {
                for (int i = 0; i < 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                }
                for (int i = 1; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y);
                    GL11.glVertex2f(x + i, y + 4);
                    GL11.glVertex2f(x + i, y + 8);
                }
                GL11.glVertex2f(x + 7, y + 5);
                GL11.glVertex2f(x + 7, y + 7);
                GL11.glVertex2f(x + 7, y + 6);
                GL11.glVertex2f(x + 7, y + 1);
                GL11.glVertex2f(x + 7, y + 2);
                GL11.glVertex2f(x + 7, y + 3);
                x += 8;
            } else if (c == 'b'){
              for (int i = 0; i <= 8; i++) {
                  GL11.glVertex2f(x + 1, y + i);
              }
                GL11.glVertex2f(x + 2, y + 1);
                GL11.glVertex2f(x + 2, y + 4);
                GL11.glVertex2f(x + 3, y);
                GL11.glVertex2f(x + 3, y + 5);
                GL11.glVertex2f(x + 4, y);
                GL11.glVertex2f(x + 4, y + 5);
                GL11.glVertex2f(x + 5, y);
                GL11.glVertex2f(x + 5, y + 5);
                GL11.glVertex2f(x + 6, y + 1);
                GL11.glVertex2f(x + 6, y + 2);
                GL11.glVertex2f(x + 6, y + 3);
                GL11.glVertex2f(x + 6, y + 4);
                x += 7;
            } else if (c == 'C') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2f(x + i, y);
                    GL11.glVertex2f(x + i, y + 8);
                }
                GL11.glVertex2f(x + 6, y + 1);
                GL11.glVertex2f(x + 6, y + 2);

                GL11.glVertex2f(x + 6, y + 6);
                GL11.glVertex2f(x + 6, y + 7);

                x += 8;
            } else if (c == 'c') {
                for (int i = 1; i < 5; i++){
                    GL11.glVertex2f(x + 1, y + i);
                }
                for (int i = 2; i < 6; i++){
                    GL11.glVertex2f(x + i, y);
                    GL11.glVertex2f(x + i, y + 5);
                }
                GL11.glVertex2f(x + 6, y + 1);
                x += 7;
            } else if (c == 'D') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2f(x + i, y);
                    GL11.glVertex2f(x + i, y + 8);
                }
                GL11.glVertex2f(x + 6, y + 1);
                GL11.glVertex2f(x + 6, y + 2);
                GL11.glVertex2f(x + 6, y + 3);
                GL11.glVertex2f(x + 6, y + 4);
                GL11.glVertex2f(x + 6, y + 5);
                GL11.glVertex2f(x + 6, y + 6);
                GL11.glVertex2f(x + 6, y + 7);
                x += 8;
            } else if (c == 'd') {
                for(int dy = 1; dy <= 4; dy++){
                   GL11.glVertex2f(x + 1, y + dy);
                }
                for(int dx = 2; dx <= 4; dx++){
                    GL11.glVertex2f(x + dx, y);
                    GL11.glVertex2f(x + dx, y + 5);
                }
                GL11.glVertex2f(x + 5, y + 1);
                GL11.glVertex2f(x + 5, y + 4);
                for(int dy = 0; dy <= 8; dy++){
                    GL11.glVertex2f(x + 6, y + dy);
                }
                x += 7;
            } else if (c == 'E') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                }
                for (int i = 1; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 0);
                    GL11.glVertex2f(x + i, y + 8);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2f(x + i, y + 4);
                }
                x += 8;
            } else if (c == 'e'){
                for (int dy = 1; dy <= 5; dy++){
                    GL11.glVertex2f(x + 1, y + dy);
                }
                for (int dx = 2; dx <= 5; dx++){
                    GL11.glVertex2f(x + dx, y);
                    GL11.glVertex2f(x + dx, y + 3);
                    GL11.glVertex2f(x + dx, y + 6);
                }
                GL11.glVertex2f(x + 6, y + 4);
                GL11.glVertex2f(x + 6, y + 5);
                GL11.glVertex2f(x + 6, y + 1);
                x += 7;
            } else if (c == 'F') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                }
                for (int i = 1; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 8);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2f(x + i, y + 4);
                }
                x += 8;
            } else if(c == 'f'){
                GL11.glVertex2f(x + 1, y + 5);
                GL11.glVertex2f(x + 2, y + 5);
                for (int dy = 0; dy <= 7; dy++){
                    GL11.glVertex2f(x + 3, y + dy);
                }
                GL11.glVertex2f(x + 4, y + 5);
                GL11.glVertex2f(x + 5, y + 5);
                GL11.glVertex2f(x + 4, y + 8);
                GL11.glVertex2f(x + 5, y + 8);
                GL11.glVertex2f(x + 6, y + 7);
                x += 7;
            } else if (c == 'G') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2f(x + i, y);
                    GL11.glVertex2f(x + i, y + 8);
                }
                GL11.glVertex2f(x + 6, y + 1);
                GL11.glVertex2f(x + 6, y + 2);
                GL11.glVertex2f(x + 6, y + 3);
                GL11.glVertex2f(x + 5, y + 3);
                GL11.glVertex2f(x + 7, y + 3);

                GL11.glVertex2f(x + 6, y + 6);
                GL11.glVertex2f(x + 6, y + 7);

                x += 8;
            } else if (c == 'g') {
                GL11.glVertex2f(x + 1, y + 4);
                GL11.glVertex2f(x + 1, y + 5);
                for (int dx = 2; dx <= 5; dx++){
                    GL11.glVertex2f(x + dx, y);
                    GL11.glVertex2f(x + dx, y + 3);
                    GL11.glVertex2f(x + dx, y + 6);
                }
                for (int dy =1; dy <= 6; dy++){
                    GL11.glVertex2f(x + 6, y + dy);
                }
                x += 7;
            } else if (c == 'H') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                    GL11.glVertex2f(x + 7, y + i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 4);
                }
                x += 8;
            } else if (c == 'h'){
                for (int dy = 0; dy <= 8; dy++){
                    GL11.glVertex2f(x + 1, y + dy);
                }
                GL11.glVertex2f(x + 2, y + 4);
                GL11.glVertex2f(x + 3, y + 5);
                GL11.glVertex2f(x + 4, y + 5);
                GL11.glVertex2f(x + 5, y + 5);
                for (int dy =0; dy <= 4; dy++){
                    GL11.glVertex2f(x + 6, y + dy);
                }
                x += 7;
            } else if (c == 'I') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2f(x + 3, y + i);
                }
                for (int i = 1; i <= 5; i++) {
                    GL11.glVertex2f(x + i, y + 0);
                    GL11.glVertex2f(x + i, y + 8);
                }
                x += 7;
            } else if (c == 'i'){
                GL11.glVertex2f(x + 2, y);
                GL11.glVertex2f(x + 2, y + 5);
                GL11.glVertex2f(x + 3, y + 7);
                for (int dy = 0; dy <= 5; dy++){
                    GL11.glVertex2f(x + 3, y + dy);
                }
                GL11.glVertex2f(x + 4, y);
                x += 6;
            } else if (c == 'J') {
                for (int i = 1; i <= 8; i++) {
                    GL11.glVertex2f(x + 6, y + i);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2f(x + i, y + 0);
                }
                GL11.glVertex2f(x + 1, y + 3);
                GL11.glVertex2f(x + 1, y + 2);
                GL11.glVertex2f(x + 1, y + 1);
                x += 8;
            } else if (c == 'j'){
                GL11.glVertex2f(x + 2, y + 1);
                GL11.glVertex2f(x + 3, y);
                GL11.glVertex2f(x + 4, y);
                GL11.glVertex2f(x + 4, y + 6);
                GL11.glVertex2f(x + 3, y + 6);
                GL11.glVertex2f(x + 5, y + 8);
                for (int dy = 1; dy <= 6; dy++){
                    GL11.glVertex2f(x + 5, y + dy);
                }
                x += 7;
            } else if (c == 'K') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                }
                GL11.glVertex2f(x + 6, y + 8);
                GL11.glVertex2f(x + 5, y + 7);
                GL11.glVertex2f(x + 4, y + 6);
                GL11.glVertex2f(x + 3, y + 5);
                GL11.glVertex2f(x + 2, y + 4);
                GL11.glVertex2f(x + 2, y + 3);
                GL11.glVertex2f(x + 3, y + 4);
                GL11.glVertex2f(x + 4, y + 3);
                GL11.glVertex2f(x + 5, y + 2);
                GL11.glVertex2f(x + 6, y + 1);
                GL11.glVertex2f(x + 7, y);
                x += 8;
            } else if (c == 'k') {
                for (int dy = 0; dy <= 8; dy++){
                    GL11.glVertex2f(x + 1, y + dy);
                }
                GL11.glVertex2f(x + 2, y + 3);
                GL11.glVertex2f(x + 3, y + 2);
                GL11.glVertex2f(x + 3, y + 4);
                GL11.glVertex2f(x + 4, y + 1);
                GL11.glVertex2f(x + 4, y + 5);
                GL11.glVertex2f(x + 5, y);
                GL11.glVertex2f(x + 5, y + 6);
                x += 6;
            } else if (c == 'L') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                }
                for (int i = 1; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y);
                }
                x += 7;
            } else if (c == 'l') {
                GL11.glVertex2f(x + 1, y);
                GL11.glVertex2f(x + 1, y + 8);
                for (int dy = 0; dy <= 8; dy++){
                    GL11.glVertex2f(x + 2, y + dy);
                }
                GL11.glVertex2f(x + 3, y);
                GL11.glVertex2f(x + 4, y);
                GL11.glVertex2f(x + 5, y);
                GL11.glVertex2f(x + 5, y + 1);
                GL11.glVertex2f(x + 5, y + 2);
                x += 6;
            } else if (c == 'M') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                    GL11.glVertex2f(x + 7, y + i);
                }
                GL11.glVertex2f(x + 3, y + 6);
                GL11.glVertex2f(x + 2, y + 7);
                GL11.glVertex2f(x + 4, y + 5);

                GL11.glVertex2f(x + 5, y + 6);
                GL11.glVertex2f(x + 6, y + 7);
                GL11.glVertex2f(x + 4, y + 5);
                x += 8;
            } else if (c == 'm') {
                for (int dy = 0; dy <= 6; dy++){
                    GL11.glVertex2f(x + 1, y + dy);
                }
                GL11.glVertex2f(x + 2, y + 6);
                GL11.glVertex2f(x + 3, y + 6);
                for (int dy = 2; dy <= 5; dy++){
                    GL11.glVertex2f(x + 4, y + dy );
                }
                GL11.glVertex2f(x + 5, y + 6);
                GL11.glVertex2f(x + 6, y + 6);
                for (int dy =0; dy <= 5; dy++){
                    GL11.glVertex2f(x + 7, y + dy);
                }
                x += 8;
            } else if (c == 'N') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                    GL11.glVertex2f(x + 7, y + i);
                }
                GL11.glVertex2f(x + 2, y + 7);
                GL11.glVertex2f(x + 2, y + 6);
                GL11.glVertex2f(x + 3, y + 5);
                GL11.glVertex2f(x + 4, y + 4);
                GL11.glVertex2f(x + 5, y + 3);
                GL11.glVertex2f(x + 6, y + 2);
                GL11.glVertex2f(x + 6, y + 1);
                x += 8;
            } else if (c == 'n') {
                for (int dy = 0; dy <= 6; dy++){
                    GL11.glVertex2f(x + 1, y + dy);
                }
                GL11.glVertex2f(x + 2, y + 5);
                GL11.glVertex2f(x + 3, y + 6);
                GL11.glVertex2f(x + 4, y + 6);
                GL11.glVertex2f(x + 5, y + 6);
                GL11.glVertex2f(x + 6, y + 6);
                for (int dy = 0; dy <= 5; dy++){
                    GL11.glVertex2f(x + 7, y + dy);
                }
                x += 8;
            } else if (c == 'O') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                    GL11.glVertex2f(x + 7, y + i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 8);
                    GL11.glVertex2f(x + i, y + 0);
                }
                x += 8;
            } else if (c == 'o') {
                for (int dx = 2; dx <= 5; dx++){
                    GL11.glVertex2f(x + dx, y);
                    GL11.glVertex2f(x + dx, y + 5);
                }
                for (int dy = 1; dy <= 4; dy++){
                    GL11.glVertex2f(x + 1, y + dy);
                    GL11.glVertex2f(x + 6, y + dy);
                }
                x += 7;
            } else if (c == 'P') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2f(x + i, y + 8);
                    GL11.glVertex2f(x + i, y + 4);
                }
                GL11.glVertex2f(x + 6, y + 7);
                GL11.glVertex2f(x + 6, y + 5);
                GL11.glVertex2f(x + 6, y + 6);
                x += 8;
            } else if (c == 'p') {
                for (int dy = 0; dy <= 6; dy++){
                    GL11.glVertex2f(x + 1, y + dy);
                }
                for (int dx = 2; dx <= 5; dx++){
                    GL11.glVertex2f(x + dx, y + 3);
                    GL11.glVertex2f(x + dx, y + 6);
                }
                GL11.glVertex2f(x + 6, y + 4);
                GL11.glVertex2f(x + 6, y + 5);
                x += 7;
            } else if (c == 'Q') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                    if (i != 1)
                        GL11.glVertex2f(x + 7, y + i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 8);
                    if (i != 6)
                        GL11.glVertex2f(x + i, y + 0);
                }
                GL11.glVertex2f(x + 4, y + 3);
                GL11.glVertex2f(x + 5, y + 2);
                GL11.glVertex2f(x + 6, y + 1);
                GL11.glVertex2f(x + 7, y);
                x += 8;
            } else if (c == 'q') {
                GL11.glVertex2f(x + 1, y + 4);
                GL11.glVertex2f(x + 1, y + 5);
                for (int dx = 2; dx <= 5; dx++){
                    GL11.glVertex2f(x + dx, y + 3);
                    GL11.glVertex2f(x + dx, y + 6);
                }
                for (int dy = 0; dy <= 6; dy++){
                    GL11.glVertex2f(x + 6, y + dy);
                }
                x += 7;
            } else if (c == 'R') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2f(x + i, y + 8);
                    GL11.glVertex2f(x + i, y + 4);
                }
                GL11.glVertex2f(x + 6, y + 7);
                GL11.glVertex2f(x + 6, y + 5);
                GL11.glVertex2f(x + 6, y + 6);

                GL11.glVertex2f(x + 4, y + 3);
                GL11.glVertex2f(x + 5, y + 2);
                GL11.glVertex2f(x + 6, y + 1);
                GL11.glVertex2f(x + 7, y);
                x += 8;
            } else if (c == 'r') {
                for (int dy = 0; dy <= 6; dy++){
                    GL11.glVertex2f(x + 1, y + dy);
                }
                GL11.glVertex2f(x + 2, y + 5);
                GL11.glVertex2f(x + 3, y + 6);
                GL11.glVertex2f(x + 4, y + 6);
                GL11.glVertex2f(x + 5, y + 6);
                GL11.glVertex2f(x + 6, y + 5);
                x += 7;
            } else if (c == 'S') {
                for (int i = 2; i <= 7; i++) {
                    GL11.glVertex2f(x + i, y + 8);
                }
                GL11.glVertex2f(x + 1, y + 7);
                GL11.glVertex2f(x + 1, y + 6);
                GL11.glVertex2f(x + 1, y + 5);
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 4);
                    GL11.glVertex2f(x + i, y);
                }
                GL11.glVertex2f(x + 7, y + 3);
                GL11.glVertex2f(x + 7, y + 2);
                GL11.glVertex2f(x + 7, y + 1);
                GL11.glVertex2f(x + 1, y + 1);
                GL11.glVertex2f(x + 1, y + 2);
                x += 8;
            } else if (c == 's') {
                GL11.glVertex2f(x + 6, y + 1);
                GL11.glVertex2f(x + 6, y + 2);
                GL11.glVertex2f(x + 6, y + 5);
                GL11.glVertex2f(x + 1, y + 4);
                GL11.glVertex2f(x + 1, y + 5);
                GL11.glVertex2f(x + 1, y + 1);
                for (int dx = 2; dx <= 5; dx++){
                    GL11.glVertex2f(x + dx, y);
                    GL11.glVertex2f(x + dx, y + 3);
                    GL11.glVertex2f(x + dx, y + 6);
                }
                x += 7;
            } else if (c == 'T') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2f(x + 4, y + i);
                }
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2f(x + i, y + 8);
                }
                x += 7;
            } else if (c == 't') {
                GL11.glVertex2f(x + 1, y + 5);
                GL11.glVertex2f(x + 2, y + 5);
                for (int dy = 1; dy <= 7; dy++){
                    GL11.glVertex2f(x + 3, y + dy);
                }
                GL11.glVertex2f(x + 4, y + 5);
                GL11.glVertex2f(x + 5, y + 5);
                GL11.glVertex2f(x + 4, y);
                GL11.glVertex2f(x + 5, y);
                GL11.glVertex2f(x + 6, y + 1);
                x += 7;
            } else if (c == 'U') {
                for (int i = 1; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                    GL11.glVertex2f(x + 7, y + i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 0);
                }
                x += 8;
            } else if (c == 'u') {
                for (int dy = 1; dy <= 5; dy++){
                    GL11.glVertex2f(x + 1, y + dy);
                }
                for (int dx = 2; dx <= 4; dx++){
                    GL11.glVertex2f(x + dx, y);
                }
                GL11.glVertex2f(x + 5, y + 1);
                for (int dy = 0; dy <= 5; dy++){
                    GL11.glVertex2f(x + 6, y + dy);
                }
                x += 7;
            } else if (c == 'V') {
                for (int i = 2; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                    GL11.glVertex2f(x + 6, y + i);
                }
                GL11.glVertex2f(x + 2, y + 1);
                GL11.glVertex2f(x + 5, y + 1);
                GL11.glVertex2f(x + 3, y);
                GL11.glVertex2f(x + 4, y);
                x += 7;
            } else if (c == 'v') {
                for (int dy =3; dy <= 5; dy ++){
                    GL11.glVertex2f(x + 1, y + dy);
                    GL11.glVertex2f(x + 6, y + dy);
                }
                GL11.glVertex2f(x + 3, y);
                GL11.glVertex2f(x + 4, y);
                GL11.glVertex2f(x + 2, y + 1);
                GL11.glVertex2f(x + 2, y + 2);
                GL11.glVertex2f(x + 5, y + 1);
                GL11.glVertex2f(x + 5, y + 2);
                x += 7;
            } else if (c == 'W') {
                for (int i = 1; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                    GL11.glVertex2f(x + 7, y + i);
                }
                GL11.glVertex2f(x + 2, y);
                GL11.glVertex2f(x + 3, y);
                GL11.glVertex2f(x + 5, y);
                GL11.glVertex2f(x + 6, y);
                for (int i = 1; i <= 6; i++) {
                    GL11.glVertex2f(x + 4, y + i);
                }
                x += 8;
            } else if (c == 'w') {
                for (int dy = 0; dy <= 6; dy++){
                    GL11.glVertex2f(x + 1, y + dy);
                    GL11.glVertex2f(x + 7, y + dy);
                }
                GL11.glVertex2f(x + 2, y + 1);
                GL11.glVertex2f(x + 3, y + 2);
                GL11.glVertex2f(x + 4, y + 3);
                GL11.glVertex2f(x + 4, y + 4);
                GL11.glVertex2f(x + 5, y + 2);
                GL11.glVertex2f(x + 6, y + 1);
                x += 8;
            } else if (c == 'X') {
                for (int i = 1; i <= 7; i++)
                    GL11.glVertex2f(x + i, y + i);
                for (int i = 7; i >= 1; i--)
                    GL11.glVertex2f(x + i, y + 8 - i);
                GL11.glVertex2f(x + 1, y);
                GL11.glVertex2f(x + 1, y + 8);
                GL11.glVertex2f(x + 7, y);
                GL11.glVertex2f(x + 7, y + 8);
                x += 8;
            } else if (c == 'x') {
                for (int i = 1; i <= 5; i++){
                    GL11.glVertex2f(x + i, y + i);
                }
                for (int i = 5; i >= 1; i--){
                    GL11.glVertex2f(x + i, y + 6 - i);
                }
                GL11.glVertex2f(x + 1, y);
                GL11.glVertex2f(x + 1, y + 6);
                GL11.glVertex2f(x + 5, y);
                GL11.glVertex2f(x + 5, y + 6);
                x += 6;
            } else if (c == 'Y') {
                GL11.glVertex2f(x + 4, y);
                GL11.glVertex2f(x + 4, y + 1);
                GL11.glVertex2f(x + 4, y + 2);
                GL11.glVertex2f(x + 4, y + 3);
                GL11.glVertex2f(x + 4, y + 4);

                GL11.glVertex2f(x + 3, y + 5);
                GL11.glVertex2f(x + 2, y + 6);
                GL11.glVertex2f(x + 1, y + 7);
                GL11.glVertex2f(x + 1, y + 8);

                GL11.glVertex2f(x + 5, y + 5);
                GL11.glVertex2f(x + 6, y + 6);
                GL11.glVertex2f(x + 7, y + 7);
                GL11.glVertex2f(x + 7, y + 8);
                x += 8;
            } else if (c == 'y') {
                for (int dy = 4; dy <= 7; dy++){
                    GL11.glVertex2f(x + 1, y + dy);
                }
                for (int dy = 1; dy <= 7; dy++){
                    GL11.glVertex2f(x + 6, y + dy);
                }
                for (int dx = 2; dx <= 5; dx++){
                    GL11.glVertex2f(x + dx, y);
                    GL11.glVertex2f(x + dx, y + 3);
                }
                GL11.glVertex2f(x + 1, y + 1);
                x += 7;
            } else if (c == 'Z') {
                for (int i = 1; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y);
                    GL11.glVertex2f(x + i, y + 8);
                    GL11.glVertex2f(x + i, y + i);
                }
                GL11.glVertex2f(x + 6, y + 7);
                x += 8;
            } else if (c == 'z') {
                for (int dx = 1; dx <= 5; dx++){
                    GL11.glVertex2f(x + dx, y);
                    GL11.glVertex2f(x + dx, y + 6);
                }
                for (int i = 1; i <= 5; i++){
                    GL11.glVertex2f(x + i, y + i);
                }
                x += 6;
            } else if (c == '0') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                    GL11.glVertex2f(x + 7, y + i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 8);
                    GL11.glVertex2f(x + i, y + 0);
                }
                GL11.glVertex2f(x + 2, y + 1);
                GL11.glVertex2f(x + 3, y + 2);
                GL11.glVertex2f(x + 4, y + 3);
                GL11.glVertex2f(x + 4, y + 4);
                GL11.glVertex2f(x + 4, y + 5);
                GL11.glVertex2f(x + 5, y + 6);
                GL11.glVertex2f(x + 6, y + 7);
                x += 8;
            } else if (c == '1') {
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y);
                }
                for (int i = 1; i <= 8; i++) {
                    GL11.glVertex2f(x + 4, y + i);
                }
                GL11.glVertex2f(x + 3, y + 7);
                x += 8;
            } else if (c == '2') {
                for (int i = 1; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2f(x + i, y + 8);
                }
                GL11.glVertex2f(x + 1, y + 7);
                GL11.glVertex2f(x + 1, y + 6);

                GL11.glVertex2f(x + 6, y + 7);
                GL11.glVertex2f(x + 6, y + 6);
                GL11.glVertex2f(x + 6, y + 5);
                GL11.glVertex2f(x + 5, y + 4);
                GL11.glVertex2f(x + 4, y + 3);
                GL11.glVertex2f(x + 3, y + 2);
                GL11.glVertex2f(x + 2, y + 1);
                x += 8;
            } else if (c == '3') {
                for (int i = 1; i <= 5; i++) {
                    GL11.glVertex2f(x + i, y + 8);
                    GL11.glVertex2f(x + i, y);
                }
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2f(x + 6, y + i);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2f(x + i, y + 4);
                }
                x += 8;
            } else if (c == '4') {
                for (int i = 2; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                }
                for (int i = 2; i <= 7; i++) {
                    GL11.glVertex2f(x + i, y + 1);
                }
                for (int i = 0; i <= 4; i++) {
                    GL11.glVertex2f(x + 4, y + i);
                }
                x += 8;
            } else if (c == '5') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2f(x + i, y + 8);
                }
                for (int i = 4; i <= 7; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                }
                GL11.glVertex2f(x + 1, y + 1);
                GL11.glVertex2f(x + 2, y);
                GL11.glVertex2f(x + 3, y);
                GL11.glVertex2f(x + 4, y);
                GL11.glVertex2f(x + 5, y);
                GL11.glVertex2f(x + 6, y);

                GL11.glVertex2f(x + 7, y + 1);
                GL11.glVertex2f(x + 7, y + 2);
                GL11.glVertex2f(x + 7, y + 3);

                GL11.glVertex2f(x + 6, y + 4);
                GL11.glVertex2f(x + 5, y + 4);
                GL11.glVertex2f(x + 4, y + 4);
                GL11.glVertex2f(x + 3, y + 4);
                GL11.glVertex2f(x + 2, y + 4);
                x += 8;
            } else if (c == '6') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2f(x + i, y + 4);
                    GL11.glVertex2f(x + i, y + 8);
                }
                GL11.glVertex2f(x + 7, y + 1);
                GL11.glVertex2f(x + 7, y + 2);
                GL11.glVertex2f(x + 7, y + 3);
                GL11.glVertex2f(x + 6, y + 4);
                x += 8;
            } else if (c == '7') {
                for (int i = 0; i <= 7; i++)
                    GL11.glVertex2f(x + i, y + 8);
                GL11.glVertex2f(x + 7, y + 7);
                GL11.glVertex2f(x + 7, y + 6);

                GL11.glVertex2f(x + 6, y + 5);
                GL11.glVertex2f(x + 5, y + 4);
                GL11.glVertex2f(x + 4, y + 3);
                GL11.glVertex2f(x + 3, y + 2);
                GL11.glVertex2f(x + 2, y + 1);
                GL11.glVertex2f(x + 1, y);
                x += 8;
            } else if (c == '8') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                    GL11.glVertex2f(x + 7, y + i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 8);
                    GL11.glVertex2f(x + i, y + 0);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 4);
                }
                x += 8;
            } else if (c == '9') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2f(x + 7, y + i);
                }
                for (int i = 5; i <= 7; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 8);
                    GL11.glVertex2f(x + i, y + 0);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 4);
                }
                GL11.glVertex2f(x + 1, y + 0);
                x += 8;
            } else if (c == '.') {
                GL11.glVertex2f(x + 1, y);
                x += 2;
            } else if (c == ',') {
                GL11.glVertex2f(x + 1, y);
                GL11.glVertex2f(x + 1, y + 1);
                x += 2;
            } else if (c == '\n') {
                y -= 14;
                x = startX;
            } else if (c == ' ') {
                x += 8;
            } else if (c == '_') {
                for (int i = 2; i <= 8; i++) {
                    GL11.glVertex2f(x + i, y + 0);
                }
                x += 10;
            } else if (c == '-') {
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 4);
                }
                x += 8;
            } else if (c == ':') {
                GL11.glVertex2f(x + 4, y + 2);
                GL11.glVertex2f(x + 4, y + 6);
                GL11.glVertex2f(x + 5, y + 2);
                GL11.glVertex2f(x + 5, y + 6);
                x += 8;
            } else if (c == '\\') {
                for (int i = 1; i <= 7; i++){
                    GL11.glVertex2f(x + i, y + 8 - i);
                }
                x += 9;
            } else if (c == '/') {
                for (int i = 1; i <= 7; i++){
                    GL11.glVertex2f(x + i, y + i);
                }
                x += 9;
            } else {
                x += 8;
            }

            x += 1;
        }
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    private void HandleInput() { /** For Processing Mouse and Keyboard */
        while (Mouse.next()) {
            if (!Mouse.getEventButtonState()) {
                continue;
            }

            if(Mouse.getEventButton() == 0){//Left Button
                System.out.println("0");
            }

            if(Mouse.getEventButton() == 1){//Right Button
                System.out.println("1");
            }

            if(Mouse.getEventButton() == 2){//Middle Button
                System.out.println("2");
            }
        }
        while (Keyboard.next()) {

            // we only want key down events
            if (!Keyboard.getEventKeyState()) {
                continue;
            }

        }
    }

    public int GetOpenGLLineThickness(String info){
        try{
            if(info==null) return 1;
            int p1;
            if((p1 = info.indexOf("[LineWidth:")) != -1)
                return Integer.parseInt(info.substring(p1 + 11,info.indexOf(']', p1 + 11)));
            else return 1;
        }catch(Exception ex){
            System.err.println("GetVisualLineStroke_Err");
            ex.printStackTrace();
            return 1;
        }
    }
    public int GetOpenGLPointThickness(String info){
        try {
            if (info == null)
                return 4;
            int p1;
            if ((p1 = info.indexOf("[PointSize:")) != -1)
                return Integer.parseInt(info.substring(p1 + 11,
                        info.indexOf(']', p1 + 11)));
            else
                return 4;
        } catch (Exception ex) {
            System.err.println("GetVisualPointSize_Err");
            ex.printStackTrace();
            return 4;
        }
    }

    public void setVisualDashLine(String info){
        if(info.indexOf("[DashLine:") != -1){
            GL11.glEnable(GL11.GL_LINE_STIPPLE);
            GL11.glLineStipple(2, (short) 0x6666);
        }
        else GL11.glDisable(GL11.GL_LINE_STIPPLE);
    }

    public int GetScreenX(double LogicalX){
        return (int) ((LogicalX - OriginLongitude) / LongitudeScale * ScreenWidth);
    }

    public int GetScreenY(double LogicalY){
        return (int) ((LogicalY - OriginLatitude) / LatitudeScale * ScreenHeight);
    }

    public void drawLine(float x1, float y1, float x2, float y2, float thickness){
        GL11.glLineWidth(thickness);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void drawPoint(float x, float y, float thickness){
        GL11.glPointSize(thickness);
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        GL11.glBegin(GL11.GL_POINTS);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void drawPolygon(int counter, int[] X, int[] Y) {
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
        GL11.glBegin(GL11.GL_POLYGON);
        for(int Ptr = 0; Ptr < counter; Ptr++){
            GL11.glVertex2f(X[Ptr], Y[Ptr]);
        }
        GL11.glEnd();
        GL11.glPopMatrix();
    }
    static BufferedImage BackgroundImage = null;
    static String        BackGroundPath  = null;
    public void renderGL() { /** For Drawing Elements */
        // Clear The Screen And The Depth Buffer
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        //GL11.glClearColor(0.5f,0.5f,0.5f,1f);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        //-------------------------------------------------------------------------------
        /** Draw Background */
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        if (MapWizard.SingleItem.DIR == null) return;
        if (MapWizard.SingleItem.Screen.image == null) {
            GL11.glClearColor(0f, 0f, 0f, 1f);
        } else if (MapWizard.SingleItem.Screen.ShowBackGround) {
            // ------------------------------------------------------------------------------------------
            // ScreenBackGroundMove----------------------------------------------------------------------
            double MoveX = 0;
            double MoveY = 0;
            if (MapWizard.SingleItem.BackGroundMoveVectorNum != 0) {
                int min_1 = -1;
                double min_1_dis = 1e100;
                int min_2 = -1;
                double min_2_dis = 1e100;
                int min_3 = -1;
                double min_3_dis = 1e100;
                double dis = 1e100;
                double center_x = OriginLongitude + LongitudeScale / 2;
                double center_y = OriginLatitude + LatitudeScale / 2;
                for (int i = 0; i < MapWizard.SingleItem.BackGroundMoveVectorNum; i++) {
                    dis = Math.abs(center_x - MapWizard.SingleItem.BackGroundMoveX[i])
                            + Math.abs(center_y - MapWizard.SingleItem.BackGroundMoveY[i]);
                    if (dis < min_1_dis) {
                        min_3 = min_2;
                        min_3_dis = min_2_dis;
                        min_2 = min_1;
                        min_2_dis = min_1_dis;
                        min_1 = i;
                        min_1_dis = dis;
                    } else if (dis < min_2_dis) {
                        min_3 = min_2;
                        min_3_dis = min_2_dis;
                        min_2 = i;
                        min_2_dis = dis;
                    } else if (dis < min_3_dis) {
                        min_3 = i;
                        min_3_dis = dis;
                    }
                }
                double dis_sum = 1 / min_1_dis + 1 / min_2_dis + 1
                        / min_3_dis;
                MoveX = MapWizard.SingleItem.BackGroundMoveDx[min_1] / min_1_dis
                        + MapWizard.SingleItem.BackGroundMoveDx[min_2] / min_2_dis
                        + MapWizard.SingleItem.BackGroundMoveDx[min_3] / min_3_dis;
                MoveX /= dis_sum;
                MoveY = MapWizard.SingleItem.BackGroundMoveDy[min_1] / min_1_dis
                        + MapWizard.SingleItem.BackGroundMoveDy[min_2] / min_2_dis
                        + MapWizard.SingleItem.BackGroundMoveDy[min_3] / min_3_dis;
                MoveY /= dis_sum;
            }
            // ------------------------------------------------------------------------------------------
            double Xst = ((OriginLongitude - MoveX) - MapWizard.SingleItem.LongitudeStart)
                    / (MapWizard.SingleItem.LongitudeEnd - MapWizard.SingleItem.LongitudeStart) * MapWizard.SingleItem.Screen.image.getWidth(null);
            double Yst = (MapWizard.SingleItem.LatitudeEnd - (OriginLatitude + LatitudeScale - MoveY))
                    / (MapWizard.SingleItem.LatitudeEnd - MapWizard.SingleItem.LatitudeStart) * MapWizard.SingleItem.Screen.image.getHeight(null);
            double Xlen = (LongitudeScale)
                    / (MapWizard.SingleItem.LongitudeEnd - MapWizard.SingleItem.LongitudeStart) * MapWizard.SingleItem.Screen.image.getWidth(null);
            double Ylen = (LatitudeScale)
                    / (MapWizard.SingleItem.LatitudeEnd - MapWizard.SingleItem.LatitudeStart) * MapWizard.SingleItem.Screen.image.getHeight(null);
            // 将需要显示的经纬度范围转化为窗口界面中像素值
            if(Xlen + Ylen < 6400){
                if((BackgroundImage == null) || (!MapWizard.SingleItem.Screen.ImagePath.equals(BackGroundPath))){
                    BackgroundImage = TextureLoader.loadImage(MapWizard.SingleItem.Screen.ImagePath);
                    BackGroundPath = MapWizard.SingleItem.Screen.ImagePath;
                }
                int textureID = TextureLoader.loadTexture(BackgroundImage, (int) Xst, (int) Yst, (int) (Xst + Xlen), (int) (Yst + Ylen));
                GL11.glColor4f(1f, 1f, 1f, 1f);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glTexCoord2d(0, 0);
                GL11.glVertex2i(0, 0);
                GL11.glTexCoord2d(1, 0);
                GL11.glVertex2i(ScreenWidth, 0);
                GL11.glTexCoord2d(1, 1);
                GL11.glVertex2i(ScreenWidth, ScreenHeight);
                GL11.glTexCoord2d(0, 1);
                GL11.glVertex2i(0, ScreenHeight);
                GL11.glEnd();
                GL11.glDeleteTextures(textureID);
            }
        }

        //Draw OpenGL Elements
        if (MapWizard.SingleItem.IsAllElementInvisible) return;
        while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(false,true)){
            try{
                Thread.sleep(10);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        };


        if (!MapWizard.SingleItem.IsAllLineInvisible)
            if (MapWizard.SingleItem.LineDatabaseFile != null) {
                int binary, choose, now, p1, p2;
                Line2D line;
                int DrawCount = 0;
                for (int i = 0; i < MapWizard.SingleItem.LineDatabase.LineNum; i++) {
                    binary = MapWizard.SingleItem.LineDatabase.LineVisible[i];
                    if (binary < 0)
                        continue;
                    if (DrawCount < MapWizard.SingleItem.VisualObjectMaxNum)
                        if ((binary & MapWizard.SingleItem.Ox("1")) != 0) {
                           if(!MapWizard.SingleItem.LineDatabase.CheckInRegion(i, OriginLongitude, OriginLatitude, OriginLongitude + LongitudeScale, OriginLatitude + LatitudeScale)) continue;
                            /** Check in Screen */
                            if (((!MapWizard.SingleItem.ShowVisualFeature)&&((binary & MapWizard.SingleItem.Ox("1000")) != 0))
                                    ||(MapWizard.SingleItem.ShowVisualFeature&&(MapWizard.SingleItem.LineDatabase.LineHint[i].indexOf("[LineVisible:]")!=-1)))
                            {// For Line----------------------------
                                choose = (binary >> 10) & MapWizard.SingleItem.Ox("111");
                                java.awt.Color Origin=MapWizard.SingleItem.getChooseColor(choose);
                                GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                int thickness=1;
                                if (MapWizard.SingleItem.ShowVisualFeature) {
                                    setVisualDashLine(MapWizard.SingleItem.LineDatabase.LineHint[i]);
                                    thickness = GetOpenGLLineThickness(MapWizard.SingleItem.LineDatabase.LineHint[i]);
                                    Origin=MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.LineDatabase.LineHint[i], "Line");
                                    GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                }

                                now = MapWizard.SingleItem.LineDatabase.LineHead[i];
                                p1 = now;

                                while (true) {
                                    p2 = MapWizard.SingleItem.LineDatabase.AllPointNext[p1];
                                    if (p2 == -1)
                                        break;

                                    float x1 = GetScreenX(MapWizard.SingleItem.LineDatabase.AllPointX[p1]);
                                    float y1 = GetScreenY(MapWizard.SingleItem.LineDatabase.AllPointY[p1]);
                                    float x2 = GetScreenX(MapWizard.SingleItem.LineDatabase.AllPointX[p2]);
                                    float y2 = GetScreenY(MapWizard.SingleItem.LineDatabase.AllPointY[p2]);

                                    drawLine(x1, y1, x2, y2, thickness);

                                    if ((MapWizard.SingleItem.ShowVisualFeature)
                                            && (MapWizard.SingleItem.Screen.GetVisualArrow(MapWizard.SingleItem.LineDatabase.LineHint[i]))) {
                                        drawLine(x2,y2,
                                                (float) (x2
                                                        + 0.2
                                                        * (0.87 * (x1 - x2) - (y1 - y2) * 0.34)),
                                                (float) (y2
                                                        + 0.2
                                                        * ((y1 - y2) * 0.87 + 0.34 * (x1 - x2))),thickness);
                                        drawLine(x2,y2,
                                                (float) (x2
                                                        + 0.2
                                                        * (0.87 * (x1 - x2) + (y1 - y2) * 0.34)),
                                                (float) (y2
                                                        + 0.2
                                                        * ((y1 - y2) * 0.87 - 0.34 * (x1 - x2))),thickness);
                                    }
                                    DrawCount++;
                                    p1 = p2;
                                }
                            }
                            if ( ((!MapWizard.SingleItem.ShowVisualFeature)&&((binary & MapWizard.SingleItem.Ox("100")) != 0))
                                    ||(MapWizard.SingleItem.ShowVisualFeature&&(MapWizard.SingleItem.LineDatabase.LineHint[i].indexOf("[PointVisible:]")!=-1))
                                    ) {// For Point-------------------------
                                choose = (binary >> 7) & MapWizard.SingleItem.Ox("111");
                                java.awt.Color Origin=MapWizard.SingleItem.getChooseColor(choose);
                                GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                int thickness=1;
                                if (MapWizard.SingleItem.ShowVisualFeature) {
                                    thickness = GetOpenGLPointThickness(MapWizard.SingleItem.LineDatabase.LineHint[i]);
                                    Origin=MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.LineDatabase.LineHint[i],"Point");
                                    GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                }

                                now = MapWizard.SingleItem.LineDatabase.LineHead[i];
                                while (now != -1) {
                                    float xx = GetScreenX(MapWizard.SingleItem.LineDatabase.AllPointX[now]);
                                    float yy = GetScreenY(MapWizard.SingleItem.LineDatabase.AllPointY[now]);
                                    drawPoint(xx,yy,thickness);
                                    DrawCount++;
                                    now = MapWizard.SingleItem.LineDatabase.AllPointNext[now];
                                }
                            }
                            if(!MapWizard.SingleItem.IsAllFontInvisible)
                                if (
                                        ((!MapWizard.SingleItem.ShowVisualFeature)&&((binary & MapWizard.SingleItem.Ox("10")) != 0))
                                                ||
                                                (MapWizard.SingleItem.ShowVisualFeature&&(MapWizard.SingleItem.LineDatabase.LineHint[i].indexOf("[WordVisible:]")!=-1))
                                        ){// For Word--------------------------
                                    choose = (binary >> 4) & MapWizard.SingleItem.Ox("111");
                                    java.awt.Color Origin=MapWizard.SingleItem.getChooseColor(choose);
                                    GL11.glColor4f(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f, 1f);
                                    now = MapWizard.SingleItem.LineDatabase.LineHead[i];
                                    float xx = GetScreenX(MapWizard.SingleItem.LineDatabase.AllPointX[now]);
                                    float yy = GetScreenY(MapWizard.SingleItem.LineDatabase.AllPointY[now]);
                                    DrawCount++;
                                    drawString(MapWizard.SingleItem.LineDatabase.getTitle(i), (int) xx, (int) yy);
                                }
                        }
                }
            }
        if (!MapWizard.SingleItem.IsAllPolygonInvisible)
            if (MapWizard.SingleItem.PolygonDatabaseFile != null) {
                int binary, choose, now, p1, p2;
                int DrawCount = 0;
                for (int i = 0; i < MapWizard.SingleItem.PolygonDatabase.PolygonNum; i++) {
                    binary = MapWizard.SingleItem.PolygonDatabase.PolygonVisible[i];
                    if (binary < 0)
                        continue;
                    if (DrawCount < MapWizard.SingleItem.VisualObjectMaxNum)
                        if ((binary & MapWizard.SingleItem.Ox("1")) != 0) {
                            if(!MapWizard.SingleItem.PolygonDatabase.CheckInRegion(i, OriginLongitude, OriginLatitude, OriginLongitude + LongitudeScale, OriginLatitude + LatitudeScale)) continue;
                            /** Check in Screen */
                            if (
                                    ((!MapWizard.SingleItem.ShowVisualFeature)&&((binary & MapWizard.SingleItem.Ox("1000")) != 0))
                                            ||
                                            (MapWizard.SingleItem.ShowVisualFeature&&(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i].indexOf("[LineVisible:]")!=-1))
                                    ){// For Line----------------------------
                                int thickness=1;
                                choose = (binary >> 10) & MapWizard.SingleItem.Ox("111");
                                java.awt.Color Origin=MapWizard.SingleItem.getChooseColor(choose);
                                GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                if (MapWizard.SingleItem.ShowVisualFeature) {
                                    setVisualDashLine(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i]);
                                    thickness = GetOpenGLLineThickness(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i]);
                                    Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i],"Line");
                                    GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                }

                                now = MapWizard.SingleItem.PolygonDatabase.PolygonHead[i];
                                p1 = now;
                                while (true) {
                                    p2 = MapWizard.SingleItem.PolygonDatabase.AllPointNext[p1];
                                    if (p2 == -1) p2 = now;
                                    float x1 =GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[p1]);
                                    float y1 =GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[p1]);
                                    float x2 =GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[p2]);
                                    float y2 =GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[p2]);
                                    drawLine(x1, y1, x2, y2, thickness);
                                    if ((MapWizard.SingleItem.ShowVisualFeature)
                                            && (MapWizard.SingleItem.Screen.GetVisualArrow(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i]))) {
                                        drawLine(x2,y2,
                                                (float)(x2
                                                        + 0.2
                                                        * (0.87 * (x1 - x2) - (y1 - y2) * 0.34)),
                                                (float)(y2
                                                        + 0.2
                                                        * ((y1 - y2) * 0.87 + 0.34 * (x1 - x2))),thickness);
                                        drawLine(x2,y2,
                                                (float)(x2
                                                        + 0.2
                                                        * (0.87 * (x1 - x2) + (y1 - y2) * 0.34)),
                                                (float)(y2
                                                        + 0.2
                                                        * ((y1 - y2) * 0.87 - 0.34 * (x1 - x2))),thickness);
                                    }
                                    DrawCount++;
                                    p1 = p2;
                                    if (p1 == now) break;
                                }
                            }
                            if (MapWizard.SingleItem.ShowVisualFeature)
                                if(!MapWizard.SingleItem.IsAllPolygonColorInvisible)
                                    if(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i].indexOf("[PolygonVisible:]")!=-1)
                                    {//For Polygon
                                        java.awt.Color Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i],"Polygon");
                                        GL11.glColor4f(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f,Origin.getAlpha()/255f);
                                        now = MapWizard.SingleItem.PolygonDatabase.PolygonHead[i];
                                        p1 = now;
                                        int[] PolygonX = new int[1000];
                                        int[] PolygonY = new int[1000];
                                        int counter = 0;
                                        while (true) {
                                            p2 = MapWizard.SingleItem.PolygonDatabase.AllPointNext[p1];
                                            if (p2 == -1) p2 = now;
                                            PolygonX[counter] = (int) GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[p1]);
                                            PolygonY[counter] = (int) GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[p1]);
                                            counter++;
                                            p1 = p2;
                                            if (p1 == now) break;
                                        }
                                        /** DrawPolygon */
                                        drawPolygon(counter, PolygonX, PolygonY);
                                    }
                            if (
                                    ((!MapWizard.SingleItem.ShowVisualFeature)&&((binary & MapWizard.SingleItem.Ox("100")) != 0))
                                            ||
                                            (MapWizard.SingleItem.ShowVisualFeature&&(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i].indexOf("[PointVisible:]")!=-1))
                                    ){// For Point-------------------------
                                choose = (binary >> 7) & MapWizard.SingleItem.Ox("111");
                                java.awt.Color Origin=MapWizard.SingleItem.getChooseColor(choose);
                                GL11.glColor4f(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f,Origin.getAlpha()/255f);
                                int thickness = 1;
                                if (MapWizard.SingleItem.ShowVisualFeature) {
                                    thickness = GetOpenGLPointThickness(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i]);
                                    Origin=MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i],"Point");
                                    GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                }
                                now = MapWizard.SingleItem.PolygonDatabase.PolygonHead[i];
                                while (now != -1) {
                                    float xx =GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[now]);
                                    float yy =GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[now]);
                                    drawPoint(xx, yy, thickness);
                                    DrawCount++;
                                    now = MapWizard.SingleItem.PolygonDatabase.AllPointNext[now];
                                }

                            }
                            if(!MapWizard.SingleItem.IsAllFontInvisible)
                                if (
                                        ((!MapWizard.SingleItem.ShowVisualFeature)&&((binary & MapWizard.SingleItem.Ox("10")) != 0))
                                                ||
                                                (MapWizard.SingleItem.ShowVisualFeature&&(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i].indexOf("[WordVisible:]")!=-1))
                                        ){// For Word--------------------------
                                    choose = (binary >> 4) & MapWizard.SingleItem.Ox("111");
                                    java.awt.Color Origin=MapWizard.SingleItem.getChooseColor(choose);
                                    GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, 1f);
                                    now = MapWizard.SingleItem.PolygonDatabase.PolygonHead[i];
                                    float xx =GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[now]);
                                    float yy =GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[now]);
                                    DrawCount++;
                                    drawString(MapWizard.SingleItem.PolygonDatabase.getTitle(i), (int) xx, (int) yy);
                                }
                        }
                }
            }
        if ((!MapWizard.SingleItem.IsAllPointInvisible) && (!MapWizard.SingleItem.IsShowAlphaDistribution))
            if (MapWizard.SingleItem.PointDatabaseFile != null) {
                int binary, choose, now, p1, p2;
                Point2D Point;
                int DrawCount = 0;
                for (int i = 0; i < MapWizard.SingleItem.PointDatabase.PointNum; i++) {
                    binary = MapWizard.SingleItem.PointDatabase.PointVisible[i];
                    if (DrawCount > MapWizard.SingleItem.VisualObjectMaxNum) break;
                    if ((binary & MapWizard.SingleItem.Ox("1")) != 0) {
                        if (binary < 0) continue;
                        if (
                                ((!MapWizard.SingleItem.ShowVisualFeature)&&((binary & MapWizard.SingleItem.Ox("100")) != 0))
                                        ||
                                        (MapWizard.SingleItem.ShowVisualFeature&&(MapWizard.SingleItem.PointDatabase.PointHint[i].indexOf("[PointVisible:]")!=-1))
                                ){// For Point-------------------------
                            choose = (binary >> 7) & MapWizard.SingleItem.Ox("111");
                            java.awt.Color Origin=MapWizard.SingleItem.getChooseColor(choose);
                            GL11.glColor4f(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f,Origin.getAlpha()/255f);

                            int thickness = 1;
                            if (MapWizard.SingleItem.ShowVisualFeature) {
                                thickness = GetOpenGLPointThickness(MapWizard.SingleItem.PointDatabase.PointHint[i]);
                                Origin=MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PointDatabase.PointHint[i],"Point");
                                GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                            }
                            float xx =GetScreenX(MapWizard.SingleItem.PointDatabase.AllPointX[i]);
                            float yy =GetScreenY(MapWizard.SingleItem.PointDatabase.AllPointY[i]);
                            if (MapWizard.SingleItem.IsEngravePointShape) thickness=1;
                            drawPoint(xx,yy,thickness);
                            DrawCount++;
                        }
                        if(!MapWizard.SingleItem.IsAllFontInvisible)
                            if (
                                    ((!MapWizard.SingleItem.ShowVisualFeature)&&((binary & MapWizard.SingleItem.Ox("10")) != 0))
                                            ||
                                            (MapWizard.SingleItem.ShowVisualFeature&&(MapWizard.SingleItem.PointDatabase.PointHint[i].indexOf("[WordVisible:]")!=-1))
                                    ) {// For Word--------------------------
                                choose = (binary >> 4) & MapWizard.SingleItem.Ox("111");
                                java.awt.Color Origin=MapWizard.SingleItem.getChooseColor(choose);
                                GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, 1f);
                                float xx = GetScreenX(MapWizard.SingleItem.PointDatabase.AllPointX[i]);
                                float yy = GetScreenY(MapWizard.SingleItem.PointDatabase.AllPointY[i]);
                                drawString(MapWizard.SingleItem.PointDatabase.getTitle(i), (int) xx, (int) yy);
                            }
                    }
                }
            }

        while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(false, false)){
            try{
                Thread.sleep(10);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        };

        GL11.glColor4f(1f, 0f, 0f, 1f);
        if (MapWizard.SingleItem.Screen.IsTextArea1Visible) {
            drawString(MapWizard.SingleItem.Screen.TextArea1Content, 10, 5);
        }
        if (MapWizard.SingleItem.Screen.IsTextArea2Visible) {
            drawString(MapWizard.SingleItem.Screen.TextArea2Content, Display.getWidth()/2+10, 5);
        }
    }
}
