package LWJGLPackage;

import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.*;
import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

import MapKernel.MapWizard;
import javafx.stage.Screen;
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
import org.lwjgl.util.glu.tessellation.GLUtessellatorImpl;
import sun.java2d.loops.DrawPolygons;

import javax.imageio.ImageIO;
import javax.swing.*;

public class OriginalOpenGLWizard {

    /**
     * position of quad
     */
    float x = 512, y = 384;
    public int ScreenWidth = -1;
    public int ScreenHeight = -1;
    public double OriginLongitude = -1;
    /**
     * Left Down
     */
    public double OriginLatitude = -1;
    /**
     * Left Down
     */
    public double LongitudeScale = -1;
    /**
     * Left Down
     */
    public double LatitudeScale = -1; /** Left Down */
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

    public static OriginalOpenGLWizard SingleItem = null;

    public static void GetInstance() {
        if (SingleItem != null) {
            javax.swing.JOptionPane.showMessageDialog(null, "Another Instance is Running");
            return;
        }

        String str_width = JOptionPane.showInputDialog(null, "Screen Width");
        String str_height = JOptionPane.showInputDialog(null, "Screnn Height");
        try {
            int width = Integer.parseInt(str_width);
            int height = Integer.parseInt(str_height);
            GetInstance(width, height);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    public static boolean SocketGetInstance = false;

    public static String GetInstance(int Width, int Height){
        if (SingleItem != null) {
            if(SocketGetInstance) return "Fail::Another Instance!";
            else javax.swing.JOptionPane.showMessageDialog(null, "Another Instance is Running");
            return "Fail::Another Instance!";
        }

        try {
            GetInstance(Width, Height, MapWizard.SingleItem.Screen.ScreenLongitude,
                    MapWizard.SingleItem.Screen.ScreenLatitude - MapWizard.SingleItem.Screen.LatitudeScale,
                    MapWizard.SingleItem.Screen.LongitudeScale / 600 * Width, MapWizard.SingleItem.Screen.LatitudeScale / 600 * Height);
        } catch (Exception ex) {
            ex.printStackTrace();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            return "Fail::"+sw.toString();
        }
        return "Success::";
    }

    public static void GetInstance(final int ScreenWidth, final int ScreenHeight, final double OriginLongitude, final double OriginLatitude, final double LongitudeScale, final double LatitudeScale) {
        if (SingleItem != null) {
            javax.swing.JOptionPane.showMessageDialog(null, "Another Instance is Running");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LWJGLPackage.OriginalOpenGLWizard.SingleItem = new OriginalOpenGLWizard();
                    LWJGLPackage.OriginalOpenGLWizard.SingleItem.start(ScreenWidth, ScreenHeight, OriginLongitude, OriginLatitude, LongitudeScale, LatitudeScale);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    LWJGLPackage.OriginalOpenGLWizard.SingleItem = null;
                    Display.destroy();
                }
            }
        }).start();
    }

    public void start(int ScreenWidth, int ScreenHeight, double OriginLongitude, double OriginLatitude, double LongitudeScale, double LatitudeScale) {
        this.ScreenWidth = ScreenWidth;
        this.ScreenHeight = ScreenHeight;
        this.OriginLongitude = OriginLongitude;
        this.OriginLatitude = OriginLatitude;
        this.LongitudeScale = LongitudeScale;
        this.LatitudeScale = LatitudeScale;
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
            Display.sync(DefaultFPS); // cap fps to 60fps
        }
        LWJGLPackage.OriginalOpenGLWizard.SingleItem = null;
        Display.destroy();

        if (MapWizard.SingleItem != null)
            if (!MapWizard.SingleItem.isVisible()){
                MapWizard.SingleItem.setVisible(true);
            }
    }
    public static int DefaultFPS = 60;

    public void update(int delta) {

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
            Display.setTitle("FPS: " + fps + " | " + MapWizard.SingleItem.getTitle());
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
        if(UsingDefaultForegroundColor) GL11.glColor4f(DefaultForegroundColor.getRed()/255f, DefaultForegroundColor.getGreen()/255f, DefaultForegroundColor.getBlue()/255f,1f);
        GL11.glPointSize(1);
        GL11.glDisable(GL11.GL_POINT_SMOOTH);
        //GL11.glEnable(GL11.GL_POINT_SMOOTH);
        //GL11.glHint(GL11.GL_POINT_SMOOTH, GL11.GL_NICEST);
        int startX = x;
        GL11.glBegin(GL11.GL_POINTS);
        for (char c : s.toCharArray()) {
//            if (c == 'A') {
//                for (int i = 0; i < 8; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                    GL11.glVertex2f(x + 7, y + i);
//                }
//                for (int i = 2; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y + 8);
//                    GL11.glVertex2f(x + i, y + 4);
//                }
//                x += 8;
//            } else if (c == 'a') {
//                for (int i = 2; i <= 5; i++) {
//                    GL11.glVertex2f(x + i, y + 0);
//                    GL11.glVertex2f(x + i, y + 3);
//                    GL11.glVertex2f(x + i, y + 6);
//                }
//                GL11.glVertex2f(x + 1, y + 1);
//                GL11.glVertex2f(x + 1, y + 2);
//                GL11.glVertex2f(x + 6, y + 0);
//                GL11.glVertex2f(x + 6, y + 1);
//                GL11.glVertex2f(x + 6, y + 2);
//                GL11.glVertex2f(x + 6, y + 3);
//                GL11.glVertex2f(x + 6, y + 4);
//                GL11.glVertex2f(x + 6, y + 5);
//                x += 7;
//            } else if (c == 'B') {
//                for (int i = 0; i < 8; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                }
//                for (int i = 1; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y);
//                    GL11.glVertex2f(x + i, y + 4);
//                    GL11.glVertex2f(x + i, y + 8);
//                }
//                GL11.glVertex2f(x + 7, y + 5);
//                GL11.glVertex2f(x + 7, y + 7);
//                GL11.glVertex2f(x + 7, y + 6);
//                GL11.glVertex2f(x + 7, y + 1);
//                GL11.glVertex2f(x + 7, y + 2);
//                GL11.glVertex2f(x + 7, y + 3);
//                x += 8;
//            } else if (c == 'b') {
//                for (int i = 0; i <= 8; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                }
//                GL11.glVertex2f(x + 2, y + 1);
//                GL11.glVertex2f(x + 2, y + 4);
//                GL11.glVertex2f(x + 3, y);
//                GL11.glVertex2f(x + 3, y + 5);
//                GL11.glVertex2f(x + 4, y);
//                GL11.glVertex2f(x + 4, y + 5);
//                GL11.glVertex2f(x + 5, y);
//                GL11.glVertex2f(x + 5, y + 5);
//                GL11.glVertex2f(x + 6, y + 1);
//                GL11.glVertex2f(x + 6, y + 2);
//                GL11.glVertex2f(x + 6, y + 3);
//                GL11.glVertex2f(x + 6, y + 4);
//                x += 7;
//            } else if (c == 'C') {
//                for (int i = 1; i <= 7; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                }
//                for (int i = 2; i <= 5; i++) {
//                    GL11.glVertex2f(x + i, y);
//                    GL11.glVertex2f(x + i, y + 8);
//                }
//                GL11.glVertex2f(x + 6, y + 1);
//                GL11.glVertex2f(x + 6, y + 2);
//
//                GL11.glVertex2f(x + 6, y + 6);
//                GL11.glVertex2f(x + 6, y + 7);
//
//                x += 8;
//            } else if (c == 'c') {
//                for (int i = 1; i < 5; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                }
//                for (int i = 2; i < 6; i++) {
//                    GL11.glVertex2f(x + i, y);
//                    GL11.glVertex2f(x + i, y + 5);
//                }
//                GL11.glVertex2f(x + 6, y + 1);
//                x += 7;
//            } else if (c == 'D') {
//                for (int i = 0; i <= 8; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                }
//                for (int i = 2; i <= 5; i++) {
//                    GL11.glVertex2f(x + i, y);
//                    GL11.glVertex2f(x + i, y + 8);
//                }
//                GL11.glVertex2f(x + 6, y + 1);
//                GL11.glVertex2f(x + 6, y + 2);
//                GL11.glVertex2f(x + 6, y + 3);
//                GL11.glVertex2f(x + 6, y + 4);
//                GL11.glVertex2f(x + 6, y + 5);
//                GL11.glVertex2f(x + 6, y + 6);
//                GL11.glVertex2f(x + 6, y + 7);
//                x += 8;
//            } else if (c == 'd') {
//                for (int dy = 1; dy <= 4; dy++) {
//                    GL11.glVertex2f(x + 1, y + dy);
//                }
//                for (int dx = 2; dx <= 4; dx++) {
//                    GL11.glVertex2f(x + dx, y);
//                    GL11.glVertex2f(x + dx, y + 5);
//                }
//                GL11.glVertex2f(x + 5, y + 1);
//                GL11.glVertex2f(x + 5, y + 4);
//                for (int dy = 0; dy <= 8; dy++) {
//                    GL11.glVertex2f(x + 6, y + dy);
//                }
//                x += 7;
//            } else if (c == 'E') {
//                for (int i = 0; i <= 8; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                }
//                for (int i = 1; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y + 0);
//                    GL11.glVertex2f(x + i, y + 8);
//                }
//                for (int i = 2; i <= 5; i++) {
//                    GL11.glVertex2f(x + i, y + 4);
//                }
//                x += 8;
//            } else if (c == 'e') {
//                y -= 1;
//                for (int dy = 1; dy <= 5; dy++) {
//                    GL11.glVertex2f(x + 1, y + dy);
//                }
//                for (int dx = 2; dx <= 5; dx++) {
//                    GL11.glVertex2f(x + dx, y);
//                    GL11.glVertex2f(x + dx, y + 3);
//                    GL11.glVertex2f(x + dx, y + 6);
//                }
//                GL11.glVertex2f(x + 6, y + 4);
//                GL11.glVertex2f(x + 6, y + 5);
//                GL11.glVertex2f(x + 6, y + 1);
//                x += 7;
//                y += 1;
//            } else if (c == 'F') {
//                for (int i = 0; i <= 8; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                }
//                for (int i = 1; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y + 8);
//                }
//                for (int i = 2; i <= 5; i++) {
//                    GL11.glVertex2f(x + i, y + 4);
//                }
//                x += 8;
//            } else if (c == 'f') {
//                GL11.glVertex2f(x + 1, y + 5);
//                GL11.glVertex2f(x + 2, y + 5);
//                for (int dy = 0; dy <= 7; dy++) {
//                    GL11.glVertex2f(x + 3, y + dy);
//                }
//                GL11.glVertex2f(x + 4, y + 5);
//                GL11.glVertex2f(x + 5, y + 5);
//                GL11.glVertex2f(x + 4, y + 8);
//                GL11.glVertex2f(x + 5, y + 8);
//                GL11.glVertex2f(x + 6, y + 7);
//                x += 7;
//            } else if (c == 'G') {
//                for (int i = 1; i <= 7; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                }
//                for (int i = 2; i <= 5; i++) {
//                    GL11.glVertex2f(x + i, y);
//                    GL11.glVertex2f(x + i, y + 8);
//                }
//                GL11.glVertex2f(x + 6, y + 1);
//                GL11.glVertex2f(x + 6, y + 2);
//                GL11.glVertex2f(x + 6, y + 3);
//                GL11.glVertex2f(x + 5, y + 3);
//                GL11.glVertex2f(x + 7, y + 3);
//
//                GL11.glVertex2f(x + 6, y + 6);
//                GL11.glVertex2f(x + 6, y + 7);
//
//                x += 8;
//            } else if (c == 'g') {
//                y -= 3;
//                GL11.glVertex2f(x + 1, y + 5);
//                GL11.glVertex2f(x + 1, y + 6);
//                GL11.glVertex2f(x + 1, y + 7);
//                for (int dx = 2; dx <= 5; dx++) {
//                    GL11.glVertex2f(x + dx, y);
//                    GL11.glVertex2f(x + dx, y + 4);
//                    GL11.glVertex2f(x + dx, y + 8);
//                }
//                for (int dy = 1; dy <= 8; dy++) {
//                    GL11.glVertex2f(x + 6, y + dy);
//                }
//                x += 7;
//                y += 3;
//            } else if (c == 'H') {
//                for (int i = 0; i <= 8; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                    GL11.glVertex2f(x + 7, y + i);
//                }
//                for (int i = 2; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y + 4);
//                }
//                x += 8;
//            } else if (c == 'h') {
//                for (int dy = 0; dy <= 8; dy++) {
//                    GL11.glVertex2f(x + 1, y + dy);
//                }
//                GL11.glVertex2f(x + 2, y + 4);
//                GL11.glVertex2f(x + 3, y + 5);
//                GL11.glVertex2f(x + 4, y + 5);
//                GL11.glVertex2f(x + 5, y + 5);
//                for (int dy = 0; dy <= 4; dy++) {
//                    GL11.glVertex2f(x + 6, y + dy);
//                }
//                x += 7;
//            } else if (c == 'I') {
//                for (int i = 0; i <= 8; i++) {
//                    GL11.glVertex2f(x + 3, y + i);
//                }
//                for (int i = 1; i <= 5; i++) {
//                    GL11.glVertex2f(x + i, y + 0);
//                    GL11.glVertex2f(x + i, y + 8);
//                }
//                x += 7;
//            } else if (c == 'i') {
//                GL11.glVertex2f(x + 2, y);
//                GL11.glVertex2f(x + 2, y + 5);
//                GL11.glVertex2f(x + 3, y + 7);
//                for (int dy = 0; dy <= 5; dy++) {
//                    GL11.glVertex2f(x + 3, y + dy);
//                }
//                GL11.glVertex2f(x + 4, y);
//                x += 6;
//            } else if (c == 'J') {
//                for (int i = 1; i <= 8; i++) {
//                    GL11.glVertex2f(x + 6, y + i);
//                }
//                for (int i = 2; i <= 5; i++) {
//                    GL11.glVertex2f(x + i, y + 0);
//                }
//                GL11.glVertex2f(x + 1, y + 3);
//                GL11.glVertex2f(x + 1, y + 2);
//                GL11.glVertex2f(x + 1, y + 1);
//                x += 8;
//            } else if (c == 'j') {
//                y -= 2;
//                GL11.glVertex2f(x + 2, y + 1);
//                GL11.glVertex2f(x + 3, y);
//                GL11.glVertex2f(x + 4, y);
//                GL11.glVertex2f(x + 4, y + 6);
//                GL11.glVertex2f(x + 3, y + 6);
//                GL11.glVertex2f(x + 5, y + 9);
//                for (int dy = 1; dy <= 6; dy++) {
//                    GL11.glVertex2f(x + 5, y + dy);
//                }
//                x += 7;
//                y += 2;
//            } else if (c == 'K') {
//                for (int i = 0; i <= 8; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                }
//                GL11.glVertex2f(x + 6, y + 8);
//                GL11.glVertex2f(x + 5, y + 7);
//                GL11.glVertex2f(x + 4, y + 6);
//                GL11.glVertex2f(x + 3, y + 5);
//                GL11.glVertex2f(x + 2, y + 4);
//                GL11.glVertex2f(x + 2, y + 3);
//                GL11.glVertex2f(x + 3, y + 4);
//                GL11.glVertex2f(x + 4, y + 3);
//                GL11.glVertex2f(x + 5, y + 2);
//                GL11.glVertex2f(x + 6, y + 1);
//                GL11.glVertex2f(x + 7, y);
//                x += 8;
//            } else if (c == 'k') {
//                for (int dy = 0; dy <= 8; dy++) {
//                    GL11.glVertex2f(x + 1, y + dy);
//                }
//                GL11.glVertex2f(x + 2, y + 3);
//                GL11.glVertex2f(x + 3, y + 2);
//                GL11.glVertex2f(x + 3, y + 4);
//                GL11.glVertex2f(x + 4, y + 1);
//                GL11.glVertex2f(x + 4, y + 5);
//                GL11.glVertex2f(x + 5, y);
//                GL11.glVertex2f(x + 5, y + 6);
//                x += 6;
//            } else if (c == 'L') {
//                for (int i = 0; i <= 8; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                }
//                for (int i = 1; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y);
//                }
//                x += 7;
//            } else if (c == 'l') {
//                //GL11.glVertex2f(x + 1, y);
//                GL11.glVertex2f(x + 1, y + 8);
//                for (int dy = 0; dy <= 8; dy++) {
//                    GL11.glVertex2f(x + 2, y + dy);
//                }
//                GL11.glVertex2f(x + 3, y);
//                GL11.glVertex2f(x + 4, y);
//                GL11.glVertex2f(x + 5, y);
//                GL11.glVertex2f(x + 5, y + 1);
//                GL11.glVertex2f(x + 5, y + 2);
//                x += 6;
//            } else if (c == 'M') {
//                for (int i = 0; i <= 8; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                    GL11.glVertex2f(x + 7, y + i);
//                }
//                GL11.glVertex2f(x + 3, y + 6);
//                GL11.glVertex2f(x + 2, y + 7);
//                GL11.glVertex2f(x + 4, y + 5);
//
//                GL11.glVertex2f(x + 5, y + 6);
//                GL11.glVertex2f(x + 6, y + 7);
//                GL11.glVertex2f(x + 4, y + 5);
//                x += 8;
//            } else if (c == 'm') {
//                for (int dy = 0; dy <= 6; dy++) {
//                    GL11.glVertex2f(x + 1, y + dy);
//                }
//                GL11.glVertex2f(x + 2, y + 6);
//                GL11.glVertex2f(x + 3, y + 6);
//                for (int dy = 2; dy <= 5; dy++) {
//                    GL11.glVertex2f(x + 4, y + dy);
//                }
//                GL11.glVertex2f(x + 5, y + 6);
//                GL11.glVertex2f(x + 6, y + 6);
//                for (int dy = 0; dy <= 5; dy++) {
//                    GL11.glVertex2f(x + 7, y + dy);
//                }
//                x += 8;
//            } else if (c == 'N') {
//                for (int i = 0; i <= 8; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                    GL11.glVertex2f(x + 7, y + i);
//                }
//                GL11.glVertex2f(x + 2, y + 7);
//                GL11.glVertex2f(x + 2, y + 6);
//                GL11.glVertex2f(x + 3, y + 5);
//                GL11.glVertex2f(x + 4, y + 4);
//                GL11.glVertex2f(x + 5, y + 3);
//                GL11.glVertex2f(x + 6, y + 2);
//                GL11.glVertex2f(x + 6, y + 1);
//                x += 8;
//            } else if (c == 'n') {
//                for (int dy = 0; dy <= 6; dy++) {
//                    GL11.glVertex2f(x + 1, y + dy);
//                }
//                GL11.glVertex2f(x + 2, y + 5);
//                GL11.glVertex2f(x + 3, y + 6);
//                GL11.glVertex2f(x + 4, y + 6);
//                GL11.glVertex2f(x + 5, y + 6);
//                GL11.glVertex2f(x + 6, y + 6);
//                for (int dy = 0; dy <= 5; dy++) {
//                    GL11.glVertex2f(x + 7, y + dy);
//                }
//                x += 8;
//            } else if (c == 'O') {
//                for (int i = 1; i <= 7; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                    GL11.glVertex2f(x + 7, y + i);
//                }
//                for (int i = 2; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y + 8);
//                    GL11.glVertex2f(x + i, y + 0);
//                }
//                x += 8;
//            } else if (c == 'o') {
//                for (int dx = 2; dx <= 5; dx++) {
//                    GL11.glVertex2f(x + dx, y);
//                    GL11.glVertex2f(x + dx, y + 6);
//                }
//                for (int dy = 1; dy <= 5; dy++) {
//                    GL11.glVertex2f(x + 1, y + dy);
//                    GL11.glVertex2f(x + 6, y + dy);
//                }
//                x += 7;
//            } else if (c == 'P') {
//                for (int i = 0; i <= 8; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                }
//                for (int i = 2; i <= 5; i++) {
//                    GL11.glVertex2f(x + i, y + 8);
//                    GL11.glVertex2f(x + i, y + 4);
//                }
//                GL11.glVertex2f(x + 6, y + 7);
//                GL11.glVertex2f(x + 6, y + 5);
//                GL11.glVertex2f(x + 6, y + 6);
//                x += 8;
//            } else if (c == 'p') {
//                y -= 2;
//                for (int dy = 0; dy <= 7; dy++) {
//                    GL11.glVertex2f(x + 1, y + dy);
//                }
//                for (int dx = 2; dx <= 5; dx++) {
//                    GL11.glVertex2f(x + dx, y + 3);
//                    GL11.glVertex2f(x + dx, y + 7);
//                }
//                GL11.glVertex2f(x + 6, y + 4);
//                GL11.glVertex2f(x + 6, y + 5);
//                GL11.glVertex2f(x + 6, y + 6);
//                x += 7;
//                y += 2;
//            } else if (c == 'Q') {
//                for (int i = 1; i <= 7; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                    if (i != 1)
//                        GL11.glVertex2f(x + 7, y + i);
//                }
//                for (int i = 2; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y + 8);
//                    if (i != 6)
//                        GL11.glVertex2f(x + i, y + 0);
//                }
//                GL11.glVertex2f(x + 4, y + 3);
//                GL11.glVertex2f(x + 5, y + 2);
//                GL11.glVertex2f(x + 6, y + 1);
//                GL11.glVertex2f(x + 7, y);
//                x += 8;
//            } else if (c == 'q') {
//                y -= 2;
//                GL11.glVertex2f(x + 1, y + 4);
//                GL11.glVertex2f(x + 1, y + 5);
//                GL11.glVertex2f(x + 1, y + 6);
//                for (int dx = 2; dx <= 5; dx++) {
//                    GL11.glVertex2f(x + dx, y + 3);
//                    GL11.glVertex2f(x + dx, y + 7);
//                }
//                for (int dy = 0; dy <= 7; dy++) {
//                    GL11.glVertex2f(x + 6, y + dy);
//                }
//                x += 7;
//                y += 2;
//            } else if (c == 'R') {
//                for (int i = 0; i <= 8; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                }
//                for (int i = 2; i <= 5; i++) {
//                    GL11.glVertex2f(x + i, y + 8);
//                    GL11.glVertex2f(x + i, y + 4);
//                }
//                GL11.glVertex2f(x + 6, y + 7);
//                GL11.glVertex2f(x + 6, y + 5);
//                GL11.glVertex2f(x + 6, y + 6);
//
//                GL11.glVertex2f(x + 4, y + 3);
//                GL11.glVertex2f(x + 5, y + 2);
//                GL11.glVertex2f(x + 6, y + 1);
//                GL11.glVertex2f(x + 7, y);
//                x += 8;
//            } else if (c == 'r') {
//                for (int dy = 0; dy <= 6; dy++) {
//                    GL11.glVertex2f(x + 1, y + dy);
//                }
//                GL11.glVertex2f(x + 2, y + 5);
//                GL11.glVertex2f(x + 3, y + 6);
//                GL11.glVertex2f(x + 4, y + 6);
//                GL11.glVertex2f(x + 5, y + 6);
//                GL11.glVertex2f(x + 6, y + 5);
//                x += 7;
//            } else if (c == 'S') {
//                for (int i = 2; i <= 7; i++) {
//                    GL11.glVertex2f(x + i, y + 8);
//                }
//                GL11.glVertex2f(x + 1, y + 7);
//                GL11.glVertex2f(x + 1, y + 6);
//                GL11.glVertex2f(x + 1, y + 5);
//                for (int i = 2; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y + 4);
//                    GL11.glVertex2f(x + i, y);
//                }
//                GL11.glVertex2f(x + 7, y + 3);
//                GL11.glVertex2f(x + 7, y + 2);
//                GL11.glVertex2f(x + 7, y + 1);
//                GL11.glVertex2f(x + 1, y + 1);
//                //GL11.glVertex2f(x + 1, y + 2);
//                x += 8;
//            } else if (c == 's') {
//                GL11.glVertex2f(x + 6, y + 1);
//                GL11.glVertex2f(x + 6, y + 2);
//                GL11.glVertex2f(x + 6, y + 5);
//                GL11.glVertex2f(x + 1, y + 4);
//                GL11.glVertex2f(x + 1, y + 5);
//                GL11.glVertex2f(x + 1, y + 1);
//                for (int dx = 2; dx <= 5; dx++) {
//                    GL11.glVertex2f(x + dx, y);
//                    GL11.glVertex2f(x + dx, y + 3);
//                    GL11.glVertex2f(x + dx, y + 6);
//                }
//                x += 7;
//            } else if (c == 'T') {
//                for (int i = 0; i <= 8; i++) {
//                    GL11.glVertex2f(x + 4, y + i);
//                }
//                for (int i = 1; i <= 7; i++) {
//                    GL11.glVertex2f(x + i, y + 8);
//                }
//                x += 7;
//            } else if (c == 't') {
//                GL11.glVertex2f(x + 1, y + 5);
//                GL11.glVertex2f(x + 2, y + 5);
//                for (int dy = 1; dy <= 7; dy++) {
//                    GL11.glVertex2f(x + 3, y + dy);
//                }
//                GL11.glVertex2f(x + 4, y + 5);
//                GL11.glVertex2f(x + 5, y + 5);
//                GL11.glVertex2f(x + 4, y);
//                GL11.glVertex2f(x + 5, y);
//                GL11.glVertex2f(x + 6, y + 1);
//                x += 7;
//            } else if (c == 'U') {
//                for (int i = 1; i <= 8; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                    GL11.glVertex2f(x + 7, y + i);
//                }
//                for (int i = 2; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y + 0);
//                }
//                x += 8;
//            } else if (c == 'u') {
//                for (int dy = 1; dy <= 5; dy++) {
//                    GL11.glVertex2f(x + 1, y + dy);
//                }
//                for (int dx = 2; dx <= 4; dx++) {
//                    GL11.glVertex2f(x + dx, y);
//                }
//                GL11.glVertex2f(x + 5, y + 1);
//                for (int dy = 0; dy <= 5; dy++) {
//                    GL11.glVertex2f(x + 6, y + dy);
//                }
//                x += 7;
//            } else if (c == 'V') {
//                for (int i = 2; i <= 8; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                    GL11.glVertex2f(x + 6, y + i);
//                }
//                GL11.glVertex2f(x + 2, y + 1);
//                GL11.glVertex2f(x + 5, y + 1);
//                GL11.glVertex2f(x + 3, y);
//                GL11.glVertex2f(x + 4, y);
//                x += 7;
//            } else if (c == 'v') {
//                for (int dy = 3; dy <= 5; dy++) {
//                    GL11.glVertex2f(x + 1, y + dy);
//                    GL11.glVertex2f(x + 6, y + dy);
//                }
//                GL11.glVertex2f(x + 3, y);
//                GL11.glVertex2f(x + 4, y);
//                GL11.glVertex2f(x + 2, y + 1);
//                GL11.glVertex2f(x + 2, y + 2);
//                GL11.glVertex2f(x + 5, y + 1);
//                GL11.glVertex2f(x + 5, y + 2);
//                x += 7;
//            } else if (c == 'W') {
//                for (int i = 1; i <= 8; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                    GL11.glVertex2f(x + 7, y + i);
//                }
//                GL11.glVertex2f(x + 2, y);
//                GL11.glVertex2f(x + 3, y);
//                GL11.glVertex2f(x + 5, y);
//                GL11.glVertex2f(x + 6, y);
//                for (int i = 1; i <= 6; i++) {
//                    GL11.glVertex2f(x + 4, y + i);
//                }
//                x += 8;
//            } else if (c == 'w') {
//                for (int dy = 0; dy <= 6; dy++) {
//                    GL11.glVertex2f(x + 1, y + dy);
//                    GL11.glVertex2f(x + 7, y + dy);
//                }
//                GL11.glVertex2f(x + 2, y + 1);
//                GL11.glVertex2f(x + 3, y + 2);
//                GL11.glVertex2f(x + 4, y + 3);
//                GL11.glVertex2f(x + 4, y + 4);
//                GL11.glVertex2f(x + 5, y + 2);
//                GL11.glVertex2f(x + 6, y + 1);
//                x += 8;
//            } else if (c == 'X') {
//                for (int i = 1; i <= 7; i++)
//                    GL11.glVertex2f(x + i, y + i);
//                for (int i = 7; i >= 1; i--)
//                    GL11.glVertex2f(x + i, y + 8 - i);
//                GL11.glVertex2f(x + 1, y);
//                GL11.glVertex2f(x + 1, y + 8);
//                GL11.glVertex2f(x + 7, y);
//                GL11.glVertex2f(x + 7, y + 8);
//                x += 8;
//            } else if (c == 'x') {
//                for (int i = 1; i <= 5; i++) {
//                    GL11.glVertex2f(x + i, y + i);
//                }
//                for (int i = 5; i >= 1; i--) {
//                    GL11.glVertex2f(x + i, y + 6 - i);
//                }
//                GL11.glVertex2f(x + 1, y);
//                GL11.glVertex2f(x + 1, y + 6);
//                GL11.glVertex2f(x + 5, y);
//                GL11.glVertex2f(x + 5, y + 6);
//                x += 6;
//            } else if (c == 'Y') {
//                GL11.glVertex2f(x + 4, y);
//                GL11.glVertex2f(x + 4, y + 1);
//                GL11.glVertex2f(x + 4, y + 2);
//                GL11.glVertex2f(x + 4, y + 3);
//                GL11.glVertex2f(x + 4, y + 4);
//
//                GL11.glVertex2f(x + 3, y + 5);
//                GL11.glVertex2f(x + 2, y + 6);
//                GL11.glVertex2f(x + 1, y + 7);
//                GL11.glVertex2f(x + 1, y + 8);
//
//                GL11.glVertex2f(x + 5, y + 5);
//                GL11.glVertex2f(x + 6, y + 6);
//                GL11.glVertex2f(x + 7, y + 7);
//                GL11.glVertex2f(x + 7, y + 8);
//                x += 8;
//            } else if (c == 'y') {
//                y -= 2;
//                for (int dy = 4; dy <= 8; dy++) {
//                    GL11.glVertex2f(x + 1, y + dy);
//                }
//                for (int dy = 1; dy <= 8; dy++) {
//                    GL11.glVertex2f(x + 6, y + dy);
//                }
//                for (int dx = 2; dx <= 5; dx++) {
//                    GL11.glVertex2f(x + dx, y);
//                    GL11.glVertex2f(x + dx, y + 3);
//                }
//                GL11.glVertex2f(x + 1, y + 1);
//                x += 7;
//                y += 2;
//            } else if (c == 'Z') {
//                for (int i = 1; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y);
//                    GL11.glVertex2f(x + i, y + 8);
//                    GL11.glVertex2f(x + i, y + i);
//                }
//                GL11.glVertex2f(x + 6, y + 7);
//                x += 8;
//            } else if (c == 'z') {
//                for (int dx = 1; dx <= 5; dx++) {
//                    GL11.glVertex2f(x + dx, y);
//                    GL11.glVertex2f(x + dx, y + 6);
//                }
//                for (int i = 1; i <= 5; i++) {
//                    GL11.glVertex2f(x + i, y + i);
//                }
//                x += 6;
//            } else if (c == '0') {
//                for (int i = 1; i <= 7; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                    GL11.glVertex2f(x + 7, y + i);
//                }
//                for (int i = 2; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y + 8);
//                    GL11.glVertex2f(x + i, y + 0);
//                }
//                GL11.glVertex2f(x + 2, y + 1);
//                GL11.glVertex2f(x + 3, y + 2);
//                GL11.glVertex2f(x + 4, y + 3);
//                GL11.glVertex2f(x + 4, y + 4);
//                GL11.glVertex2f(x + 4, y + 5);
//                GL11.glVertex2f(x + 5, y + 6);
//                GL11.glVertex2f(x + 6, y + 7);
//                x += 8;
//            } else if (c == '1') {
//                for (int i = 2; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y);
//                }
//                for (int i = 1; i <= 8; i++) {
//                    GL11.glVertex2f(x + 4, y + i);
//                }
//                GL11.glVertex2f(x + 3, y + 7);
//                x += 8;
//            } else if (c == '2') {
//                for (int i = 1; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y);
//                }
//                for (int i = 2; i <= 5; i++) {
//                    GL11.glVertex2f(x + i, y + 8);
//                }
//                GL11.glVertex2f(x + 1, y + 7);
//                GL11.glVertex2f(x + 1, y + 6);
//
//                GL11.glVertex2f(x + 6, y + 7);
//                GL11.glVertex2f(x + 6, y + 6);
//                GL11.glVertex2f(x + 6, y + 5);
//                GL11.glVertex2f(x + 5, y + 4);
//                GL11.glVertex2f(x + 4, y + 3);
//                GL11.glVertex2f(x + 3, y + 2);
//                GL11.glVertex2f(x + 2, y + 1);
//                x += 8;
//            } else if (c == '3') {
//                for (int i = 1; i <= 5; i++) {
//                    GL11.glVertex2f(x + i, y + 8);
//                    GL11.glVertex2f(x + i, y);
//                }
//                for (int i = 1; i <= 7; i++) {
//                    GL11.glVertex2f(x + 6, y + i);
//                }
//                for (int i = 2; i <= 5; i++) {
//                    GL11.glVertex2f(x + i, y + 4);
//                }
//                x += 8;
//            } else if (c == '4') {
//                for (int i = 2; i <= 8; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                }
//                for (int i = 2; i <= 7; i++) {
//                    GL11.glVertex2f(x + i, y + 1);
//                }
//                for (int i = 0; i <= 4; i++) {
//                    GL11.glVertex2f(x + 4, y + i);
//                }
//                x += 8;
//            } else if (c == '5') {
//                for (int i = 1; i <= 7; i++) {
//                    GL11.glVertex2f(x + i, y + 8);
//                }
//                for (int i = 4; i <= 7; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                }
//                GL11.glVertex2f(x + 1, y + 1);
//                GL11.glVertex2f(x + 2, y);
//                GL11.glVertex2f(x + 3, y);
//                GL11.glVertex2f(x + 4, y);
//                GL11.glVertex2f(x + 5, y);
//                GL11.glVertex2f(x + 6, y);
//
//                GL11.glVertex2f(x + 7, y + 1);
//                GL11.glVertex2f(x + 7, y + 2);
//                GL11.glVertex2f(x + 7, y + 3);
//
//                GL11.glVertex2f(x + 6, y + 4);
//                GL11.glVertex2f(x + 5, y + 4);
//                GL11.glVertex2f(x + 4, y + 4);
//                GL11.glVertex2f(x + 3, y + 4);
//                GL11.glVertex2f(x + 2, y + 4);
//                x += 8;
//            } else if (c == '6') {
//                for (int i = 1; i <= 7; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                }
//                for (int i = 2; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y);
//                }
//                for (int i = 2; i <= 5; i++) {
//                    GL11.glVertex2f(x + i, y + 4);
//                    GL11.glVertex2f(x + i, y + 8);
//                }
//                GL11.glVertex2f(x + 7, y + 1);
//                GL11.glVertex2f(x + 7, y + 2);
//                GL11.glVertex2f(x + 7, y + 3);
//                GL11.glVertex2f(x + 6, y + 4);
//                x += 8;
//            } else if (c == '7') {
//                for (int i = 0; i <= 7; i++)
//                    GL11.glVertex2f(x + i, y + 8);
//                GL11.glVertex2f(x + 7, y + 7);
//                GL11.glVertex2f(x + 7, y + 6);
//
//                GL11.glVertex2f(x + 6, y + 5);
//                GL11.glVertex2f(x + 5, y + 4);
//                GL11.glVertex2f(x + 4, y + 3);
//                GL11.glVertex2f(x + 3, y + 2);
//                GL11.glVertex2f(x + 2, y + 1);
//                GL11.glVertex2f(x + 1, y);
//                x += 8;
//            } else if (c == '8') {
//                for (int i = 1; i <= 7; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                    GL11.glVertex2f(x + 7, y + i);
//                }
//                for (int i = 2; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y + 8);
//                    GL11.glVertex2f(x + i, y + 0);
//                }
//                for (int i = 2; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y + 4);
//                }
//                x += 8;
//            } else if (c == '9') {
//                for (int i = 1; i <= 7; i++) {
//                    GL11.glVertex2f(x + 7, y + i);
//                }
//                for (int i = 5; i <= 7; i++) {
//                    GL11.glVertex2f(x + 1, y + i);
//                }
//                for (int i = 2; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y + 8);
//                    GL11.glVertex2f(x + i, y + 0);
//                }
//                for (int i = 2; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y + 4);
//                }
//                GL11.glVertex2f(x + 1, y + 0);
//                x += 8;
//            } else if (c == '.') {
//                GL11.glVertex2f(x + 1, y);
//                x += 2;
//            } else if (c == ',') {
//                GL11.glVertex2f(x + 1, y);
//                GL11.glVertex2f(x + 1, y + 1);
//                x += 2;
//            } else if (c == '\n') {
//                y -= 14;
//                x = startX;
//                continue;
//            } else if (c == '\t') {
//                x = (x / 32 + 1) * 32;
//                continue;
//            } else if (c == ' ') {
//                x += 8;
//            } else if (c == '_') {
//                for (int i = 2; i <= 8; i++) {
//                    GL11.glVertex2f(x + i, y + 0);
//                }
//                x += 10;
//            } else if (c == '-') {
//                for (int i = 2; i <= 6; i++) {
//                    GL11.glVertex2f(x + i, y + 4);
//                }
//                x += 8;
//            } else if (c == ':') {
//                GL11.glVertex2f(x + 4, y + 2);
//                GL11.glVertex2f(x + 4, y + 6);
//                GL11.glVertex2f(x + 5, y + 2);
//                GL11.glVertex2f(x + 5, y + 6);
//                x += 8;
//            } else if (c == '\\') {
//                for (int i = 1; i <= 7; i++) {
//                    GL11.glVertex2f(x + i, y + 8 - i);
//                }
//                x += 9;
//            } else if (c == '/') {
//                for (int i = 1; i <= 7; i++) {
//                    GL11.glVertex2f(x + i, y + i);
//                }
//                x += 9;}
            if (c == '\n') {
                y -= 20;
                x = startX;
                continue;
            } else if (c == '\t') {
                x = (x / 32 + 1) * 32;
                continue;
            } else if (c < 128) {  //ASCLL CHAR
                y -= 6;
                try {
                    if (ASCLLFile == null) {
                        ASCLLFile = new File(MapKernel.GeoCityInfo_main.Append_Folder_Prefix("ASC16"));
                        BufferedInputStream in = new BufferedInputStream(new FileInputStream(ASCLLFile));
                        int bytetmp;
                        int bytecount = 0;
                        while((bytetmp = in.read()) != -1){
                            ASCLLMatrix[bytecount] = (byte)bytetmp;
                            bytecount++;
                        }
                    }
                    int offset = (c & 0xff) * 16;
                    byte[] buf =new byte[16];
                    for (int ptr = 0; ptr < 16; ptr++){
                        buf[ptr] = ASCLLMatrix[offset + ptr];
                    }
                    for(int i = 0; i < 16; i++)
                    {
                        for(int j = 0; j < 8; j++)
                        {
                            if((buf[i] & (0x80 >> j)) != 0)
                            {
                                GL11.glVertex2f(x + j, y + 16 - i);
                            }
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    System.out.println(c);
                }finally {
                    x += 8;
                }
                y += 6;
            } else if (InGB2312Char(c)) {
                y -= 4;
                try{
                    String gb2312= URLEncoder.encode(String.valueOf(c), "gb2312");
                    //drawString("\\u"+gb2312, x, y);
                    //System.out.println(gb2312);
                    int area = Integer.parseInt(gb2312.substring(1,3),16) - 0xA0;
                    int index = Integer.parseInt(gb2312.substring(4),16) - 0xA0;
                    int offset = (94 * (area-1) + (index-1)) * 32;

                    byte[] font_rect = new byte[32];
                    if(GB2312File == null) {
                        GB2312File = new File(MapKernel.GeoCityInfo_main.Append_Folder_Prefix("HZK16"));
                        BufferedInputStream in = new BufferedInputStream(new FileInputStream(GB2312File));
                        int bytetmp;
                        int bytecount = 0;
                        while((bytetmp = in.read()) != -1){
                            GB2312Matrix[bytecount] = (byte)bytetmp;
                            bytecount++;
                        }
                    }
                    for(int i = 0; i < 32; i++){
                        font_rect[i] = GB2312Matrix[offset + i];
                    }

                    byte[][] rect_list = new byte[16][16];
                    for(int k = 0; k < 16; k++){
                        byte[] row_list = rect_list[k];
                        for(int j = 0; j < 2; j++){
                            byte asc = font_rect[k * 2 + j];
                            for(int i = 0; i < 8; i++){
                                byte flag = (byte)(asc & KEYS[i]);
                                row_list[j * 8 + i] = flag;
                            }
                        }
                    }

                    for(int ypos = 0; ypos < 16; ypos++){
                        for(int xpos = 0; xpos < 16; xpos++){
                            if(rect_list[ypos][xpos] != 0)
                                GL11.glVertex2f(x + xpos, y + 16 - ypos);
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    System.out.println(c);
                }finally {
                    x += 16;
                }
                y += 4;
            } else {
                x += 8;
            }

            x += 1;
        }
        GL11.glEnd();
        GL11.glPopMatrix();
    }
    private static byte[] KEYS = new byte[]{(byte)0x80, (byte)0x40, (byte)0x20, (byte)0x10, (byte)0x08, (byte)0x04, (byte)0x02, (byte)0x01};
    private static File GB2312File = null;
    private static File ASCLLFile = null;
    private static byte[] ASCLLMatrix = new byte[256 * 16];
    private static byte[] GB2312Matrix = new byte[94 * 94 * 32];
    private static HashSet<Character> GB2312CharTable = new HashSet<Character>();

    private static final boolean InGB2312Char(char c) {
        if(GB2312CharTable.isEmpty()){
            for(int area = 0xA1; area < 0xFF; area++){
                for(int index = 0xA1; index < 0xFF; index++){
                    byte AreaByte = (byte) area;
                    byte IndexByte = (byte) index;
                    try {
                        char UnicodeChar = (new String(new byte[]{AreaByte, IndexByte}, "gb2312")).charAt(0);
                        GB2312CharTable.add(UnicodeChar);
                        //System.out.println(UnicodeChar);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        if(GB2312CharTable.contains(c)) return true; else return false;
    }

    private static final boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public long MouseLeftButtonPressedTime = -1;
    public long MouseLeftButtonReleasedTime = -1;
    public long MouseRightButtonPressedTime = -1;
    public long MouseRightButtonReleasedTime = -1;

    public static String SocketListenClick(){
        return "";
    }

    private void HandleInput() { /** For Processing Mouse and Keyboard */
        /** Mouse Keyboard Controller */
        if (Mouse.isButtonDown(0)) { /** Mouse Drag */
            double DragX = Mouse.getDX();
            double DragY = Mouse.getDY();
            OriginLongitude -= DragX / ScreenWidth * LongitudeScale;
            OriginLatitude -= DragY / ScreenHeight * LatitudeScale;
        }

        if (Math.abs(Mouse.getEventDWheel()) > 100)
            if (Mouse.getDWheel() != 0) { /** Wheel Move */
                double CenterX = GetLogicalX();
                double CenterY = GetLogicalY();
                if (Mouse.getEventDWheel() > 0) {
                    LongitudeScale /= 1.1;
                    LatitudeScale /= 1.1;
                } else {
                    LongitudeScale *= 1.1;
                    LatitudeScale *= 1.1;
                }
                OriginLongitude = CenterX - Mouse.getX() * LongitudeScale / ScreenWidth;
                OriginLatitude = CenterY - Mouse.getY() * LatitudeScale / ScreenHeight;
            }

        while (Mouse.next()) {

            if (Mouse.getEventButtonState()) {
                if (Mouse.getEventButton() == 0) {
                    //System.out.println("Left button pressed");
                    MouseLeftButtonPressedTime = Calendar.getInstance().getTimeInMillis();
                } else if (Mouse.getEventButton() == 1) {
                    //System.out.println("Right button pressed");
                    MouseRightButtonPressedTime = Calendar.getInstance().getTimeInMillis();
                }
            } else {
                if (Mouse.getEventButton() == 0) {
                    //System.out.println("Left button released");
                    MouseLeftButtonReleasedTime = Calendar.getInstance().getTimeInMillis();
                    ;
                } else if (Mouse.getEventButton() == 1) {
                    //System.out.println("Right button released");
                    MouseRightButtonReleasedTime = Calendar.getInstance().getTimeInMillis();
                }
            }

            if (Mouse.getEventButtonState()) { // Ignore the Button Pressed
                continue;
            }

            if (MouseLeftButtonReleasedTime - MouseLeftButtonPressedTime < 200) // If not Drag
                if (Mouse.getEventButton() == 0) {//Left Button Click
                    //System.out.println("Left Button Click at Logical Position "+ GetLogicalX() + " , "+ GetLogicalY());

                    if (MapWizard.SingleItem.BehaviorListener != null) {
                        MapKernel.MapWizard.SingleItem.BehaviorListener.MousePressedListener(GetLogicalX(), GetLogicalY(), org.lwjgl.input.Mouse.getX(), org.lwjgl.input.Mouse.getY(), true);
                    } else if (MapKernel.MapWizard.SingleItem.NowPanel.getString().equals("MapElementsEditorPane")) {
                        ExtendedToolPane.ExtendedToolPaneInterface DBEditor = (ExtendedToolPane.ExtendedToolPaneInterface)
                                (MapKernel.MapWizard.SingleItem.NowPanel);
                        DBEditor.convey(GetLogicalX(), GetLogicalY());
                    } else {
                        if(MapWizard.SingleItem.Screen.IsTextArea1Visible) MapWizard.SingleItem.Screen.TextArea1Content = "You Clicked At Location ( " + GetLogicalX() + " , " + GetLogicalY() + " )";
                        else System.out.println("You Clicked At Location ( " + GetLogicalX() + " , " + GetLogicalY() + " )");
                        ExtendedToolPane.ExtendedToolPaneInterface DBEditor = (ExtendedToolPane.ExtendedToolPaneInterface)
                                (MapKernel.MapWizard.SingleItem.NowPanel);
                        DBEditor.convey(GetLogicalX(), GetLogicalY());
                    }
                }

            if (MouseRightButtonReleasedTime - MouseRightButtonPressedTime < 200) // If not Drag
                if (Mouse.getEventButton() == 1) {//Right Button Click
                    //System.out.println("Right Button Click");

                    if (MapWizard.SingleItem.BehaviorListener != null) {
                        MapKernel.MapWizard.SingleItem.BehaviorListener.MousePressedListener(GetLogicalX(), GetLogicalY(), org.lwjgl.input.Mouse.getX(), org.lwjgl.input.Mouse.getY(), false);;
                    } else if (MapKernel.MapWizard.SingleItem.NowPanel.getString().equals("MapElementsEditorPane")) {
                        ExtendedToolPane.ExtendedToolPaneInterface DBEditor = (ExtendedToolPane.ExtendedToolPaneInterface)
                                (MapKernel.MapWizard.SingleItem.NowPanel);
                        DBEditor.confirm();
                    } else {
                        if(MapWizard.SingleItem.Screen.IsTextArea1Visible) MapWizard.SingleItem.Screen.TextArea1Content = "You Right Clicked At Location ( " + GetLogicalX() + " , " + GetLogicalY() + " )";
                        else System.out.println("You Right Clicked At Location ( " + GetLogicalX() + " , " + GetLogicalY() + " )");
                        ExtendedToolPane.ExtendedToolPaneInterface DBEditor = (ExtendedToolPane.ExtendedToolPaneInterface)
                                (MapKernel.MapWizard.SingleItem.NowPanel);
                        DBEditor.confirm();
                    }
                }

            if (Mouse.getEventButton() == 2) {//Middle Button Click
                System.out.println("Middle Button Click");
            }

        }

        while (Keyboard.next()) {
            if (!Keyboard.getEventKeyState()) {
                continue;
            }
            if(isSocketKeyListen) {
                SocketKeyChar = Keyboard.getEventCharacter();
                SocketKeyID = Keyboard.getEventKey();
                isSocketKeyListen = false;
            }else if (Keyboard.isKeyDown(Keyboard.KEY_C) || Keyboard.isKeyDown(Keyboard.KEY_V)) {
                double CenterX = GetLogicalX();
                double CenterY = GetLogicalY();
                if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
                    LongitudeScale /= 1.1;
                    LatitudeScale /= 1.1;
                } else if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
                    LongitudeScale *= 1.1;
                    LatitudeScale *= 1.1;
                }
                ;
                OriginLongitude = CenterX - Mouse.getX() * LongitudeScale / ScreenWidth;
                OriginLatitude = CenterY - Mouse.getY() * LatitudeScale / ScreenHeight;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                OriginLongitude -= 0.1 * LongitudeScale;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                OriginLongitude += 0.1 * LongitudeScale;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                OriginLatitude += 0.1 * LatitudeScale;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                OriginLatitude -= 0.1 * LatitudeScale;
            } else {
                //System.out.println(Keyboard.getEventCharacter());
            }
        }
    }
    public boolean isSocketKeyListen = false;
    public char SocketKeyChar = '!';
    public int SocketKeyID = -1;

    public int GetOpenGLLineThickness(String info) {
        try {
            if (info == null) return 1;
            int p1;
            if ((p1 = info.indexOf("[LineWidth:")) != -1)
                return Integer.parseInt(info.substring(p1 + 11, info.indexOf(']', p1 + 11)));
            else return 1;
        } catch (Exception ex) {
            System.err.println("GetVisualLineStroke_Err");
            ex.printStackTrace();
            return 1;
        }
    }

    public int GetOpenGLPointThickness(String info) {
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

    public void setVisualDashLine(String info) {
        if (info.indexOf("[DashLine:") != -1) {
            GL11.glEnable(GL11.GL_LINE_STIPPLE);
            GL11.glLineStipple(2, (short) 0x6666);
        } else GL11.glDisable(GL11.GL_LINE_STIPPLE);
    }

    public int GetScreenX(double LogicalX) {
        return (int) ((LogicalX - OriginLongitude) / LongitudeScale * ScreenWidth + 0.5);
    }

    public int GetScreenY(double LogicalY) {
        return (int) ((LogicalY - OriginLatitude) / LatitudeScale * ScreenHeight + 0.5);
    }

    public void drawLine(float x1, float y1, float x2, float y2, float thickness) {
        if(Math.max(x1, x2) < 0) return;
        if(Math.min(x1, x2) > ScreenWidth) return;
        if(Math.max(y1, y2) < 0) return;
        if(Math.min(y1, y2) > ScreenHeight) return;
        GL11.glLineWidth(thickness);
        //GL11.glEnable(GL11.GL_LINE_SMOOTH); // Will make lines invisible in Linux environment
        GL11.glEnable(GL11.GL_LINE_SMOOTH);  //在linux系统下画线可能出现异常
        GL11.glHint(GL11.GL_LINE_SMOOTH, GL11.GL_NICEST);  //在Linux系统下画线可能出现异常
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void drawPoint(float x, float y, float thickness) {
        if(x < 0) return;
        if(x > ScreenWidth) return;
        if(y < 0) return;
        if(y > ScreenHeight) return;
        GL11.glPointSize(thickness);
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        GL11.glHint (GL11.GL_POINT_SMOOTH, GL11.GL_NICEST);
        GL11.glBegin(GL11.GL_POINTS);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void drawArc(float x, float y, double start_angle, double end_angle, double delta_angle, double radius, int thickness, boolean fill)
    {
        if(radius < 4) return;
        if(fill) {
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        }
        else
        {
            GL11.glLineWidth(thickness);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glBegin(GL11.GL_LINE_STRIP);
        }
        for (double i=start_angle; i<=end_angle; i+=delta_angle)
        {
            double vx=x+radius * Math.cos(i);
            double vy=y+radius* Math.sin(i);
            GL11.glVertex2d(vx, vy);
        }
        GL11.glEnd();
    }

    public void drawCircle(float x, float y, double radius, int thickness, boolean fill)
    {
        double pi= Math.acos(-1.0);
        drawArc(x, y, 0, 2 * pi, pi / 180, radius, thickness, fill);
    }

    double[] Coor = new double[300000];

    public void drawPolygon(int counter, double[] Coor) {
        Tessellator tess = new Tessellator();

        tess.gluBeginPolygon();

        tess.gluTessBeginContour();
        for (int Ptr = 0; Ptr < counter / 3; Ptr++) {
            tess.gluTessVertex(Coor, Ptr * 3, Ptr);
        }
        tess.gluTessEndContour();

        tess.gluEndPolygon();
        tess.gluDeleteTess();

        for (int i = 0; i < tess.primitives.size(); i++) {
            Primitive Triangle = tess.primitives.get(i);
            GL11.glBegin(Triangle.type);
            for (int j = 0; j < Triangle.vertices.size(); j++) {
                int Ptr = Triangle.vertices.get(j);
                GL11.glVertex2f((float) Coor[3 * Ptr], (float) Coor[3 * Ptr + 1]);
            }
            GL11.glEnd();
            GL11.glPopMatrix();
        }
    }

    static Vector<BufferedImage> BackgroundImageList = new Vector<BufferedImage>();
    static String BackGroundPath = null;

    public static boolean IsReady(){
        return (MapWizard.SingleItem.Screen.ImagePath.equals(BackGroundPath));
    }

    public double GetLogicalX() {
        return (OriginLongitude + org.lwjgl.input.Mouse.getX() * LongitudeScale / ScreenWidth);
    }

    public double GetLogicalY() {
        return (OriginLatitude + org.lwjgl.input.Mouse.getY() * LatitudeScale / ScreenHeight);
    }

    public void MoveMiddle(double x, double y){
        double dx = x - (OriginLongitude + LongitudeScale / 2);
        double dy = y - (OriginLatitude + LatitudeScale / 2);
        OriginLongitude += dx;
        OriginLatitude += dy;
    }

    public boolean GetScreenShot = false;
    public String ScreenShotFilePath = null;
    public String ScreenShot(String FilePath){
        if(FilePath == null) FilePath = "";
        try {
            GL11.glReadBuffer(GL11.GL_FRONT);
            int width  = Display.getDisplayMode().getWidth();
            int height = Display.getDisplayMode().getHeight();
            int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
            ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

            File file = FilePath.isEmpty() ? new File(MapWizard.SingleItem.ImageDir ,"OpenGL_" + MapWizard.SingleItem.Handle.GetInternationalTimeSignature() + ".png") : new File(FilePath); // The file to save to.
            String format = "PNG"; // Example: "PNG" or "JPG"
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for(int x = 0; x < width; x++)
                for(int y = 0; y < height; y++)
                {
                    int i = (x + (width * y)) * bpp;
                    int r = buffer.get(i) & 0xFF;
                    int g = buffer.get(i + 1) & 0xFF;
                    int b = buffer.get(i + 2) & 0xFF;
                    image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b); //这里的rgb是图形中元素alpha值融合后的结果，已经带有透视效果，因此image中alpha值不用设置为透明
                }

            ImageIO.write(image, format, file);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "OpenGL ScreenShot Exception!";
        }
    }

    public static int DefaultBufferImageNumber = 588;
    public static int DefaultPointSize = 5;
    public static int DefaultLineWidth = 2;
    public static int DefaultScreenCacheLife = 30;
    public static Color DefaultBackgroundColor = new Color(0f , 0f , 0f);
    public static Color DefaultForegroundColor = new Color(1f , 1f , 1f);
    public static boolean UsingDefaultForegroundColor = false;
    public static HashMap<String, BufferedImage> PNGDic = new HashMap<String, BufferedImage>();

    public static boolean PrintlnOpenGLRenderTime = false;
    public void renderGL() { /** For Drawing Elements */
        if(PrintlnOpenGLRenderTime) System.out.println("st\t" + Calendar.getInstance().getTimeInMillis());
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
            GL11.glClearColor(DefaultBackgroundColor.getRed() / 255f, DefaultBackgroundColor.getGreen() / 255f, DefaultBackgroundColor.getBlue() / 255f, 1f);
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
            /** Update Image with Multiple Resolution */
            if ((BackgroundImageList.isEmpty()) || (!MapWizard.SingleItem.Screen.ImagePath.equals(BackGroundPath))) {
                BackgroundImageList.clear();
                BufferedImage BackgroundImage = TextureLoader.loadImage(MapWizard.SingleItem.Screen.ImagePath);
                BackgroundImageList.add(BackgroundImage);
                BufferedImage SourceImage = BackgroundImageList.lastElement();
                while (SourceImage.getWidth() + SourceImage.getHeight() > 1000) {
                    BackgroundImage = new BufferedImage((int) (SourceImage.getWidth() * 0.7), (int) (SourceImage.getHeight() * 0.7), BufferedImage.TYPE_INT_RGB);
                    BackgroundImage.getGraphics().drawImage(SourceImage, 0, 0, BackgroundImage.getWidth(), BackgroundImage.getHeight(),
                            0, 0, SourceImage.getWidth(), SourceImage.getHeight(), null);
                    BackgroundImageList.add(BackgroundImage);
                    SourceImage = BackgroundImageList.lastElement();
                }
                BackGroundPath = MapWizard.SingleItem.Screen.ImagePath;
            }
            int ImageIndex = 0;
            BufferedImage BackgroundImage = null;
            double Xst, Yst, Xlen, Ylen;
            do {
                BackgroundImage = BackgroundImageList.get(ImageIndex);
                ImageIndex++;
                /** 将需要显示的经纬度范围转化为窗口界面中像素值 */
                Xst = ((OriginLongitude - MoveX) - MapWizard.SingleItem.LongitudeStart)
                        / (MapWizard.SingleItem.LongitudeEnd - MapWizard.SingleItem.LongitudeStart) * BackgroundImage.getWidth(null);
                Yst = (MapWizard.SingleItem.LatitudeEnd - (OriginLatitude + LatitudeScale - MoveY))
                        / (MapWizard.SingleItem.LatitudeEnd - MapWizard.SingleItem.LatitudeStart) * BackgroundImage.getHeight(null);
                Xlen = (LongitudeScale)
                        / (MapWizard.SingleItem.LongitudeEnd - MapWizard.SingleItem.LongitudeStart) * BackgroundImage.getWidth(null);
                Ylen = (LatitudeScale)
                        / (MapWizard.SingleItem.LatitudeEnd - MapWizard.SingleItem.LatitudeStart) * BackgroundImage.getHeight(null);
            } while ((ImageIndex < BackgroundImageList.size()) && (Xlen + Ylen > (ScreenWidth + ScreenHeight)));
            int x1 = (int) (Xst + 0.5);
            int x2 = (int) (Xst + Xlen + 0.5);
            int y1 = (int) (Yst + 0.5);
            int y2 = (int) (Yst + Ylen + 0.5);

//            int Tex_x1 = GetScreenX(MapWizard.SingleItem.LongitudeStart);  //显存不足，大图无法一次性load进来，放弃这种原始做法
//            int Tex_x2 = GetScreenX(MapWizard.SingleItem.LongitudeEnd);
//            int Tex_y1 = GetScreenY(MapWizard.SingleItem.LatitudeStart);
//            int Tex_y2 = GetScreenY(MapWizard.SingleItem.LatitudeEnd);
//            int textureID = TextureLoader.loadTexture(BackgroundImage, 0, 0, BackgroundImage.getWidth(), BackgroundImage.getHeight());
//            GL11.glColor4f(1f, 1f, 1f, 1f);
//            GL11.glBegin(GL11.GL_QUADS);
//            GL11.glTexCoord2d(0, 0);
//            GL11.glVertex2i(Tex_x1, Tex_y1);
//            GL11.glTexCoord2d(1, 0);
//            GL11.glVertex2i(Tex_x2, Tex_y1);
//            GL11.glTexCoord2d(1, 1);
//            GL11.glVertex2i(Tex_x2, Tex_y2);
//            GL11.glTexCoord2d(0, 1);
//            GL11.glVertex2i(Tex_x1, Tex_y2);
//            GL11.glEnd();
//            GL11.glDeleteTextures(textureID);
//==================================================================================================================================
            /** x1, y1, x2, y2是屏幕在图片坐标系中的位置 */  //分片读取地图数据和浮点坐标运算
            /** Tex_x, Tex_y 是图片在屏幕坐标系中的位置 */
            if ((x1 >= 0) && (x2 <= BackgroundImage.getWidth()) && (y1 >= 0) && (y2 <= BackgroundImage.getHeight()))
            {
                int textureID = TextureLoader.loadBackgroundTexture(BackgroundImage, x1, y1, x2, y2);
                GL11.glColor4f(1f, 1f, 1f, 1f);
                //GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);   //在loadtexture中重复
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
            } else if(((x1 < BackgroundImage.getWidth()) && (x2 > 0)) && ((y1 < BackgroundImage.getHeight()) && (y2 > 0))){
                /** Visible_Tex 图片可见部分在图片坐标系中的位置 */
                int Visible_Tex_x1, Visible_Tex_x2, Visible_Tex_y1, Visible_Tex_y2;
                /** Visible_Tex_in_Screen 图片可见部分在屏幕坐标系中的位置 */
                int Visible_Tex_in_Screen_x1, Visible_Tex_in_Screen_x2, Visible_Tex_in_Screen_y1, Visible_Tex_in_Screen_y2;
                /** 注意！图片坐标系与屏幕坐标系的Y轴方向相反 */
                if(x1 < 0){
                    Visible_Tex_x1 = 0;
                    Visible_Tex_in_Screen_x1 = GetScreenX(MapWizard.SingleItem.LongitudeStart);
                }else{
                    Visible_Tex_x1 = x1;
                    Visible_Tex_in_Screen_x1 = 0;
                }

                if(x2 > BackgroundImage.getWidth()){
                    Visible_Tex_x2 = BackgroundImage.getWidth();
                    Visible_Tex_in_Screen_x2 = GetScreenX(MapWizard.SingleItem.LongitudeEnd);
                }else{
                    Visible_Tex_x2 = x2;
                    Visible_Tex_in_Screen_x2 = ScreenWidth;
                }

                if(y1 < 0){
                    Visible_Tex_y1 = 0;
                    Visible_Tex_in_Screen_y2 = GetScreenY(MapWizard.SingleItem.LatitudeEnd);
                }else{
                    Visible_Tex_y1 = y1;
                    Visible_Tex_in_Screen_y2 = ScreenHeight;
                }

                if(y2 > BackgroundImage.getHeight()){
                    Visible_Tex_y2 = BackgroundImage.getHeight();
                    Visible_Tex_in_Screen_y1 = GetScreenY(MapWizard.SingleItem.LatitudeStart);
                }else{
                    Visible_Tex_y2 = y2;
                    Visible_Tex_in_Screen_y1 = 0;
                }

                int textureID = TextureLoader.loadBackgroundTexture(BackgroundImage, Visible_Tex_x1, Visible_Tex_y1, Visible_Tex_x2, Visible_Tex_y2);
                GL11.glColor4f(1f, 1f, 1f, 1f);
                //GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); //在loadtexture中重复
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glTexCoord2d(0, 0);
                GL11.glVertex2i(Visible_Tex_in_Screen_x1, Visible_Tex_in_Screen_y1);
                GL11.glTexCoord2d(1, 0);
                GL11.glVertex2i(Visible_Tex_in_Screen_x2, Visible_Tex_in_Screen_y1);
                GL11.glTexCoord2d(1, 1);
                GL11.glVertex2i(Visible_Tex_in_Screen_x2, Visible_Tex_in_Screen_y2);
                GL11.glTexCoord2d(0, 1);
                GL11.glVertex2i(Visible_Tex_in_Screen_x1, Visible_Tex_in_Screen_y2);
                GL11.glEnd();
                GL11.glDeleteTextures(textureID);
            }
        }

        //Draw OpenGL Elements
        if (MapWizard.SingleItem.IsAllElementInvisible) return;
        while (!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(false, true)) {
            try {
                Thread.sleep(1);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        int SwapIter = -1;/**将for循环的下标存储起来，以备还原*/
        BufferedImage PNGImage = null;
        String PNGHint = null;
        int Xst = -1; int Yst = -1;int Xen = -1;int Yen = -1;float xx;float yy;int pngwidth;int pngheight;double ratio;int offsetx;int offsety;String PNGPath = null;

        if(PrintlnOpenGLRenderTime) System.out.println("BeforeAliginedMap\t" + Calendar.getInstance().getTimeInMillis());
        if(MapWizard.SingleItem.Screen.ShowBackGround) {
            for (int PNGPtr = 0; PNGPtr < MapWizard.SingleItem.PointDatabase.PointNum; PNGPtr = SwapIter + 1) {
                SwapIter = PNGPtr;  //准备索引换指针
                if (CacheLife == 0) {
                    if (MapWizard.SingleItem.PointDatabase.PointVisible[PNGPtr] != 0)
                        continue; //PointDB 的三处点输入中有 AlignedMap_ 的 visible已经设置为0
                    PNGHint = MapWizard.SingleItem.PointDatabase.PointHint[PNGPtr];
                    if (PNGHint.indexOf("AlignedMap_") == -1) continue;
                    //MapWizard.SingleItem.PointDatabase.PointVisible[PNGPtr] = 0;//将AlignedMap的Point设置成个位为0，防止二次计算  但已经在PointDB中设置过了
                    int stPos = PNGHint.indexOf("[PNG:");
                    if (stPos == -1) continue;
                    int enPos = PNGHint.indexOf(']', stPos + 5);
                    if ((stPos == -1) || (enPos == -1)) continue;
                    PNGPath = PNGHint.substring(stPos + 5, enPos);
                    if (PNGPath.indexOf("AlignedMap_") == -1) continue;
                    //if (!((PNGDic.containsKey(PNGPath)) || (new File(PNGPath).exists()))) continue;
                    if (DefaultScreenCacheLife != 0) AlignedMapInScreenCache.add(PNGPtr);
                } else {
                    if (AlignedMapCacheIndexArr == null) break;
                    if (PNGPtr >= AlignedMapCacheIndexArr.length) break;
                    PNGPtr = (Integer) AlignedMapCacheIndexArr[PNGPtr];  //换指针
                    if (PNGPtr >= MapWizard.SingleItem.PointDatabase.PointNum) continue; //缓存可能失效

                    if (MapWizard.SingleItem.PointDatabase.PointVisible[PNGPtr] != 0)
                        continue; //PointDB 的三处点输入中有 AlignedMap_ 的 visible已经设置为0
                    //if (!AlignedMapInScreenCache.contains(PNGPtr)) continue;//即使命中缓存，数据库也可能发生更改，为防止报错需要重复判断
                    PNGHint = MapWizard.SingleItem.PointDatabase.PointHint[PNGPtr];
                    if (PNGHint.indexOf("AlignedMap_") == -1) continue;
                    MapWizard.SingleItem.PointDatabase.PointVisible[PNGPtr] = 0;//将AlignedMap的Point设置成个位为0，防止二次计算   但已经在PointDB中设置过了
                    int stPos = PNGHint.indexOf("[PNG:");
                    if (stPos == -1) continue;
                    int enPos = PNGHint.indexOf(']', stPos + 5);
                    if ((stPos == -1) || (enPos == -1)) continue;
                    PNGPath = PNGHint.substring(stPos + 5, enPos);
                    if (PNGPath.indexOf("AlignedMap_") == -1) continue;
                    //if (!((PNGDic.containsKey(PNGPath)) || (new File(PNGPath).exists()))) continue;
                }
                String PNGName = PNGPath.substring(PNGPath.indexOf("AlignedMap_"));
                String[] LocList = PNGName.split("_");
                Xst = GetScreenX(Double.parseDouble(LocList[1]));
                if (Xst > ScreenWidth) {
                    if (CacheLife == 0) AlignedMapInScreenCache.remove(PNGPtr);
                    continue;
                }
                Yst = GetScreenY(Double.parseDouble(LocList[2]));
                if (Yst > ScreenHeight) {
                    if (CacheLife == 0) AlignedMapInScreenCache.remove(PNGPtr);
                    continue;
                }
                Xen = GetScreenX(Double.parseDouble(LocList[3]));
                if (Xen < 0) {
                    if (CacheLife == 0) AlignedMapInScreenCache.remove(PNGPtr);
                    continue;
                }
                Yen = GetScreenY(Double.parseDouble(LocList[4]));
                if (Yen < 0) {
                    if (CacheLife == 0) AlignedMapInScreenCache.remove(PNGPtr);
                    continue;
                }
                xx = (Xst + Xen) / 2 + 0.5f;
                yy = (Yst + Yen) / 2 + 0.5f;
                pngwidth = Xen - Xst;
                pngheight = Yen - Yst;

                ratio = ((double) (pngwidth + pngheight)) / (ScreenWidth + ScreenHeight);
                if (ratio < 0.333f) {
                    if (CacheLife == 0) AlignedMapInScreenCache.remove(PNGPtr);
                    continue;
                }
                if (ratio > 3f) {
                    if (CacheLife == 0) AlignedMapInScreenCache.remove(PNGPtr);
                    continue;
                }

                offsetx = -pngwidth / 2;
                offsety = -pngheight / 2;

                if (!((PNGDic.containsKey(PNGPath)) || (new File(PNGPath).exists()))) {
                    if (CacheLife == 0) AlignedMapInScreenCache.remove(PNGPtr);
                    continue;
                }
                if (PNGDic.containsKey(PNGPath)) PNGImage = PNGDic.get(PNGPath);
                else {
                    PNGImage = TextureLoader.loadImage(PNGPath);
                    if (PNGDic.size() > DefaultBufferImageNumber) PNGDic.clear();
                    PNGDic.put(PNGPath, PNGImage);
                }

                int textureID = TextureLoader.loadTexture(PNGImage, 0, 0, PNGImage.getWidth(), PNGImage.getHeight());
                GL11.glColor4f(1f, 1f, 1f, 1f);
                //GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);  //在loadtexture中重复
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glTexCoord2d(0, 0);
                GL11.glVertex2i((int) (xx + offsetx), (int) (yy + offsety));
                GL11.glTexCoord2d(1, 0);
                GL11.glVertex2i((int) (xx + offsetx + pngwidth), (int) (yy + offsety));
                GL11.glTexCoord2d(1, 1);
                GL11.glVertex2i((int) (xx + offsetx + pngwidth), (int) (yy + offsety + pngheight));
                GL11.glTexCoord2d(0, 1);
                GL11.glVertex2i((int) (xx + offsetx), (int) (yy + offsety + pngheight));
                GL11.glEnd();
                GL11.glDeleteTextures(textureID);
            }
        }
        if((CacheLife == 0) && (DefaultScreenCacheLife != 0)) {
            AlignedMapCacheIndexArr = AlignedMapInScreenCache.toArray();  //缓存复用
            Arrays.sort(AlignedMapCacheIndexArr);
        }
        if(PrintlnOpenGLRenderTime) System.out.println("AlignedMap\t" + SwapIter);
        SwapIter = 0;
        if(PrintlnOpenGLRenderTime) System.out.println("BeforeLine\t" + Calendar.getInstance().getTimeInMillis());
        //---------------------------------------------------------------------------------------------------------------------------------
        if (!MapWizard.SingleItem.IsAllLineInvisible)
            if (MapWizard.SingleItem.LineDatabaseFile != null) {
                int binary, choose, now, p1, p2;
                int DrawCount = 0;
                for (int i = 0; i < MapWizard.SingleItem.LineDatabase.LineNum; i = SwapIter + 1) {
                    SwapIter = i; //准备索引换指针
                    if(CacheLife != 0) {
                        if(LineCacheIndexArr == null) break;
                        if(i >= LineCacheIndexArr.length) break;
                        i = (Integer)LineCacheIndexArr[i];  //换指针
                        if(i >= MapWizard.SingleItem.LineDatabase.LineNum) continue;  //缓存可能失效
                    }
                    binary = MapWizard.SingleItem.LineDatabase.LineVisible[i];
                    if (binary < 0)
                        continue;
                    if (DrawCount > MapWizard.SingleItem.VisualObjectMaxNum) break;
                        if ((binary & MapWizard.SingleItem.Ox("1")) != 0) {
                            if(CacheLife == 0) {
                                if (!MapWizard.SingleItem.LineDatabase.CheckInRegion(i, OriginLongitude, OriginLatitude, OriginLongitude + LongitudeScale, OriginLatitude + LatitudeScale))
                                    continue;
                                if(DefaultScreenCacheLife != 0) LineInScreenCache.add(i);
                            }else {
                                //if(!LineInScreenCache.contains(i)) continue;
                                if (!MapWizard.SingleItem.LineDatabase.CheckInRegion(i, OriginLongitude, OriginLatitude, OriginLongitude + LongitudeScale, OriginLatitude + LatitudeScale))
                                    continue;
                            }
                            /** Check in Screen */
                            if (((!MapWizard.SingleItem.ShowVisualFeature) && ((binary & MapWizard.SingleItem.Ox("1000")) != 0))
                                    || (MapWizard.SingleItem.ShowVisualFeature && (MapWizard.SingleItem.LineDatabase.LineHint[i].indexOf("[LineVisible:]") != -1))) {// For Line----------------------------
                                choose = (binary >> 10) & MapWizard.SingleItem.Ox("111");
                                java.awt.Color Origin = MapWizard.SingleItem.getChooseColor(choose);
                                GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                int thickness = DefaultLineWidth;
                                if (MapWizard.SingleItem.ShowVisualFeature) {
                                    setVisualDashLine(MapWizard.SingleItem.LineDatabase.LineHint[i]);
                                    thickness = GetOpenGLLineThickness(MapWizard.SingleItem.LineDatabase.LineHint[i]);
                                    Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.LineDatabase.LineHint[i], "Line");
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
                                        double Scale = ((Math.abs(x2 - x1) + Math.abs(y2 - y1) > 0.1 * (ScreenWidth + ScreenHeight)) ?
                                                (0.2 / (Math.abs(x2 - x1) + Math.abs(y2 - y1)) * 0.1 * (ScreenWidth + ScreenHeight)) : 0.2);
                                        drawLine(x2, y2,
                                                (float) (x2
                                                        + Scale
                                                        * (0.87 * (x1 - x2) - (y1 - y2) * 0.34)),
                                                (float) (y2
                                                        + Scale
                                                        * ((y1 - y2) * 0.87 + 0.34 * (x1 - x2))), 2);
                                        drawLine(x2, y2,
                                                (float) (x2
                                                        + Scale
                                                        * (0.87 * (x1 - x2) + (y1 - y2) * 0.34)),
                                                (float) (y2
                                                        + Scale
                                                        * ((y1 - y2) * 0.87 - 0.34 * (x1 - x2))), 2);
                                    }
                                    DrawCount++;
                                    p1 = p2;
                                }
                            }
                            if (((!MapWizard.SingleItem.ShowVisualFeature) && ((binary & MapWizard.SingleItem.Ox("100")) != 0))
                                    || (MapWizard.SingleItem.ShowVisualFeature && (MapWizard.SingleItem.LineDatabase.LineHint[i].indexOf("[PointVisible:]") != -1))
                                    ) {// For Point-------------------------
                                choose = (binary >> 7) & MapWizard.SingleItem.Ox("111");
                                java.awt.Color Origin = MapWizard.SingleItem.getChooseColor(choose);
                                GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                int thickness = DefaultPointSize;
                                if (MapWizard.SingleItem.ShowVisualFeature) {
                                    thickness = GetOpenGLPointThickness(MapWizard.SingleItem.LineDatabase.LineHint[i]);
                                    Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.LineDatabase.LineHint[i], "Point");
                                    GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                }

                                now = MapWizard.SingleItem.LineDatabase.LineHead[i];
                                while (now != -1) {
                                    xx = GetScreenX(MapWizard.SingleItem.LineDatabase.AllPointX[now]);
                                    yy = GetScreenY(MapWizard.SingleItem.LineDatabase.AllPointY[now]);
                                    drawPoint(xx, yy, thickness);
                                    DrawCount++;
                                    now = MapWizard.SingleItem.LineDatabase.AllPointNext[now];
                                }
                            }
                            if (!MapWizard.SingleItem.IsAllFontInvisible)
                                if (
                                        ((!MapWizard.SingleItem.ShowVisualFeature) && ((binary & MapWizard.SingleItem.Ox("10")) != 0))
                                                ||
                                                (MapWizard.SingleItem.ShowVisualFeature && (MapWizard.SingleItem.LineDatabase.LineHint[i].indexOf("[WordVisible:]") != -1))
                                        ) {// For Word--------------------------
                                    choose = (binary >> 4) & MapWizard.SingleItem.Ox("111");
                                    java.awt.Color Origin = MapWizard.SingleItem.getChooseColor(choose);
                                    GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, 1f);

                                    if (MapWizard.SingleItem.ShowVisualFeature) {
                                        Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.LineDatabase.LineHint[i], "Word");
                                        GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                    }

                                    now = MapWizard.SingleItem.LineDatabase.LineHead[i];
                                    xx = GetScreenX(MapWizard.SingleItem.LineDatabase.AllPointX[now]);
                                    yy = GetScreenY(MapWizard.SingleItem.LineDatabase.AllPointY[now]);
                                    DrawCount++;
                                    drawString(MapWizard.SingleItem.LineDatabase.getTitle(i), (int)xx, (int)yy);
                                }
                        }
                }
            }
        if((CacheLife == 0) && (DefaultScreenCacheLife != 0)) {
            LineCacheIndexArr = LineInScreenCache.toArray(); //缓存复用
            if(LineCacheIndexArr.length < 100000) Arrays.sort(LineCacheIndexArr);
        }
        if(PrintlnOpenGLRenderTime) System.out.println("Line\t" + SwapIter);
        SwapIter = 0;
        if(PrintlnOpenGLRenderTime) System.out.println("BeforePolygon\t" + Calendar.getInstance().getTimeInMillis());
        //----------------------------------------------------------------------------------------------------------------------------
        if (!MapWizard.SingleItem.IsAllPolygonInvisible)
            if (MapWizard.SingleItem.PolygonDatabaseFile != null) {
                int binary, choose, now, p1, p2;
                int DrawCount = 0;
                for (int i = 0; i < MapWizard.SingleItem.PolygonDatabase.PolygonNum; i = SwapIter + 1) {
                    SwapIter = i;
                    if(CacheLife != 0) {
                        if(PolygonCacheIndexArr == null) break;
                        if(i >= PolygonCacheIndexArr.length) break;
                        i = (Integer)PolygonCacheIndexArr[i];
                        if(i >= MapWizard.SingleItem.PolygonDatabase.PolygonNum) continue;
                    }
                    binary = MapWizard.SingleItem.PolygonDatabase.PolygonVisible[i];
                    if (binary < 0)
                        continue;
                    if (DrawCount > MapWizard.SingleItem.VisualObjectMaxNum) break;
                        if ((binary & MapWizard.SingleItem.Ox("1")) != 0) {
                            if(CacheLife == 0) {
                                if (!MapWizard.SingleItem.PolygonDatabase.CheckInRegion(i, OriginLongitude, OriginLatitude, OriginLongitude + LongitudeScale, OriginLatitude + LatitudeScale))
                                    continue;
                                if(DefaultScreenCacheLife != 0) PolygonInScreenCache.add(i);
                            }else {
                                //if(!PolygonInScreenCache.contains(i)) continue;
                                if (!MapWizard.SingleItem.PolygonDatabase.CheckInRegion(i, OriginLongitude, OriginLatitude, OriginLongitude + LongitudeScale, OriginLatitude + LatitudeScale))
                                    continue;
                            }
                            /** Check in Screen */
                            if (
                                    ((!MapWizard.SingleItem.ShowVisualFeature) && ((binary & MapWizard.SingleItem.Ox("1000")) != 0))
                                            ||
                                            (MapWizard.SingleItem.ShowVisualFeature && (MapWizard.SingleItem.PolygonDatabase.PolygonHint[i].indexOf("[LineVisible:]") != -1))
                                    ) {// For Line----------------------------
                                int thickness = DefaultLineWidth;
                                choose = (binary >> 10) & MapWizard.SingleItem.Ox("111");
                                java.awt.Color Origin = MapWizard.SingleItem.getChooseColor(choose);
                                GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                if (MapWizard.SingleItem.ShowVisualFeature) {
                                    setVisualDashLine(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i]);
                                    thickness = GetOpenGLLineThickness(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i]);
                                    Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i], "Line");
                                    GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                }

                                now = MapWizard.SingleItem.PolygonDatabase.PolygonHead[i];
                                p1 = now;
                                while (true) {
                                    p2 = MapWizard.SingleItem.PolygonDatabase.AllPointNext[p1];
                                    if (p2 == -1) p2 = now;
                                    float x1 = GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[p1]);
                                    float y1 = GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[p1]);
                                    float x2 = GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[p2]);
                                    float y2 = GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[p2]);
                                    drawLine(x1, y1, x2, y2, thickness);
                                    if ((MapWizard.SingleItem.ShowVisualFeature)
                                            && (MapWizard.SingleItem.Screen.GetVisualArrow(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i]))) {
                                        double Scale = ((Math.abs(x2 - x1) + Math.abs(y2 - y1) > 0.1 * (ScreenWidth + ScreenHeight)) ?
                                                (0.2 / (Math.abs(x2 - x1) + Math.abs(y2 - y1)) * 0.1 * (ScreenWidth + ScreenHeight)) : 0.2);
                                        drawLine(x2, y2,
                                                (float) (x2
                                                        + Scale
                                                        * (0.87 * (x1 - x2) - (y1 - y2) * 0.34)),
                                                (float) (y2
                                                        + Scale
                                                        * ((y1 - y2) * 0.87 + 0.34 * (x1 - x2))), 2);
                                        drawLine(x2, y2,
                                                (float) (x2
                                                        + Scale
                                                        * (0.87 * (x1 - x2) + (y1 - y2) * 0.34)),
                                                (float) (y2
                                                        + Scale
                                                        * ((y1 - y2) * 0.87 - 0.34 * (x1 - x2))), 2);
                                    }
                                    DrawCount++;
                                    p1 = p2;
                                    if (p1 == now) break;
                                }
                            }
                            if (MapWizard.SingleItem.ShowVisualFeature)
                                if (!MapWizard.SingleItem.IsAllPolygonColorInvisible)
                                    if (MapWizard.SingleItem.PolygonDatabase.PolygonHint[i].indexOf("[PolygonVisible:]") != -1) {//For Polygon
                                        java.awt.Color Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i], "Polygon");
                                        GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                        now = MapWizard.SingleItem.PolygonDatabase.PolygonHead[i];
                                        p1 = now;
                                        int counter = 0;
                                        while (true) {
                                            p2 = MapWizard.SingleItem.PolygonDatabase.AllPointNext[p1];
                                            if (p2 == -1) p2 = now;
                                            Coor[counter] = GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[p1]);
                                            counter++;
                                            Coor[counter] = GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[p1]);
                                            counter++;
                                            Coor[counter] = 0;
                                            counter++;
                                            p1 = p2;
                                            if (p1 == now) break;
                                        }
                                        /** DrawPolygon */
                                        DrawCount++;
                                        drawPolygon(counter, Coor);
                                    }
                            if (
                                    ((!MapWizard.SingleItem.ShowVisualFeature) && ((binary & MapWizard.SingleItem.Ox("100")) != 0))
                                            ||
                                            (MapWizard.SingleItem.ShowVisualFeature && (MapWizard.SingleItem.PolygonDatabase.PolygonHint[i].indexOf("[PointVisible:]") != -1))
                                    ) {// For Point-------------------------
                                choose = (binary >> 7) & MapWizard.SingleItem.Ox("111");
                                java.awt.Color Origin = MapWizard.SingleItem.getChooseColor(choose);
                                GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                int thickness = DefaultPointSize;
                                if (MapWizard.SingleItem.ShowVisualFeature) {
                                    thickness = GetOpenGLPointThickness(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i]);
                                    Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i], "Point");
                                    GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                }
                                now = MapWizard.SingleItem.PolygonDatabase.PolygonHead[i];
                                while (now != -1) {
                                    xx = GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[now]);
                                    yy = GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[now]);
                                    drawPoint(xx, yy, thickness);
                                    DrawCount++;
                                    now = MapWizard.SingleItem.PolygonDatabase.AllPointNext[now];
                                }

                            }
                            if (!MapWizard.SingleItem.IsAllFontInvisible)
                                if (
                                        ((!MapWizard.SingleItem.ShowVisualFeature) && ((binary & MapWizard.SingleItem.Ox("10")) != 0))
                                                ||
                                                (MapWizard.SingleItem.ShowVisualFeature && (MapWizard.SingleItem.PolygonDatabase.PolygonHint[i].indexOf("[WordVisible:]") != -1))
                                        ) {// For Word--------------------------
                                    choose = (binary >> 4) & MapWizard.SingleItem.Ox("111");
                                    java.awt.Color Origin = MapWizard.SingleItem.getChooseColor(choose);
                                    GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, 1f);

                                    if (MapWizard.SingleItem.ShowVisualFeature) {
                                        Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i], "Word");
                                        GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                    }

                                    now = MapWizard.SingleItem.PolygonDatabase.PolygonHead[i];
                                    xx = GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[now]);
                                    yy = GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[now]);
                                    DrawCount++;
                                    drawString(MapWizard.SingleItem.PolygonDatabase.getTitle(i), (int)xx, (int)yy);
                                }
                        }
                }
            }
        if((CacheLife == 0) && (DefaultScreenCacheLife != 0)) {
            PolygonCacheIndexArr = PolygonInScreenCache.toArray();
            if(PolygonCacheIndexArr.length < 100000) Arrays.sort(PolygonCacheIndexArr);
        }
        if(PrintlnOpenGLRenderTime) System.out.println("Polygon\t" + SwapIter);
        SwapIter = 0;
        if(PrintlnOpenGLRenderTime) System.out.println("BeforePoint\t" + Calendar.getInstance().getTimeInMillis());
        //--------------------------------------------------------------------------------------------------------------------------
        if ((!MapWizard.SingleItem.IsAllPointInvisible) && (!MapWizard.SingleItem.IsShowAlphaDistribution))
            if (MapWizard.SingleItem.PointDatabaseFile != null) {
                int binary, choose, now, p1, p2;
                int DrawCount = 0;
                for (int i = 0; i < MapWizard.SingleItem.PointDatabase.PointNum; i = SwapIter + 1) {
                    SwapIter = i;
                    if(CacheLife != 0){
                        if(PointCacheIndexArr == null) break;
                        if(i >= PointCacheIndexArr.length) break;
                        i = (Integer)PointCacheIndexArr[i];
                        if(i >= MapWizard.SingleItem.PointDatabase.PointNum) continue;
                    }
                    binary = MapWizard.SingleItem.PointDatabase.PointVisible[i];
                    if (binary < 0) continue;
                    if (DrawCount > MapWizard.SingleItem.VisualObjectMaxNum) break;
                    PNGHint = MapWizard.SingleItem.PointDatabase.PointHint[i];
                    if ((binary & MapWizard.SingleItem.Ox("1")) != 0) {
                        if(CacheLife == 0) {
                            if ((!MapWizard.SingleItem.PointDatabase.CheckInRegion(i, OriginLongitude, OriginLatitude, OriginLongitude + LongitudeScale, OriginLatitude + LatitudeScale)) && ((!AllowAbsPixelPoint) || (PNGHint.indexOf("[SetPixel") == -1)))
                                continue;
                            //if (PNGHint.indexOf("AlignedMap_") != -1) continue;  在之前已经将visible个位设0
                            if(DefaultScreenCacheLife != 0) PointInScreenCache.add(i);
                        }else {
                            //if (!PointInScreenCache.contains(i)) continue;//数据可能发生了改变，需要重判
                            if ((!MapWizard.SingleItem.PointDatabase.CheckInRegion(i, OriginLongitude, OriginLatitude, OriginLongitude + LongitudeScale, OriginLatitude + LatitudeScale)) && ((!AllowAbsPixelPoint) || (PNGHint.indexOf("[SetPixel") == -1)))
                                continue;
                            //if (PNGHint.indexOf("AlignedMap_") != -1) continue;  在之前已经将visible个位设0
                        }
                        /** Check in Screen */
                        if (((!MapWizard.SingleItem.ShowVisualFeature) && ((binary & MapWizard.SingleItem.Ox("100")) != 0))
                                ||(MapWizard.SingleItem.ShowVisualFeature && (MapWizard.SingleItem.PointDatabase.PointHint[i].indexOf("[PointVisible:]") != -1)))
                        {// For Point-------------------------
                            choose = (binary >> 7) & MapWizard.SingleItem.Ox("111");
                            java.awt.Color Origin = MapWizard.SingleItem.getChooseColor(choose);
                            GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);

                            int thickness = DefaultPointSize;
                            if (MapWizard.SingleItem.ShowVisualFeature) {
                                thickness = GetOpenGLPointThickness(MapWizard.SingleItem.PointDatabase.PointHint[i]);
                                Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PointDatabase.PointHint[i], "Point");
                                GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                            }
                            xx = GetScreenX(MapWizard.SingleItem.PointDatabase.AllPointX[i]);
                            yy = GetScreenY(MapWizard.SingleItem.PointDatabase.AllPointY[i]);
                            /** Absolute X Y Pixel Setting*/
                            int stPos = -1;
                            int enPos = -1;
                            if(AllowAbsPixelPoint) {
                                stPos = PNGHint.indexOf("[SetPixelX:");
                                if (stPos != -1) enPos = PNGHint.indexOf(']', stPos + 11);
                                if ((stPos != -1) && (enPos != -1))
                                    xx = Integer.parseInt(PNGHint.substring(stPos + 11, enPos));
                                stPos = PNGHint.indexOf("[SetPixelY:");
                                if (stPos != -1) enPos = PNGHint.indexOf(']', stPos + 11);
                                if ((stPos != -1) && (enPos != -1))
                                    yy = Integer.parseInt(PNGHint.substring(stPos + 11, enPos));
                            }

                            if (MapWizard.SingleItem.IsEngravePointShape) thickness = 1;
                            drawPoint(xx, yy, thickness);
                            DrawCount++;

                            /** Use PNG swap points */
                            if(AllowPNGPoint) {
                                xx += 0.5;
                                yy += 0.5; //四舍五入

                                stPos = PNGHint.indexOf("[PNG:");
                                if (stPos != -1) enPos = PNGHint.indexOf(']', stPos + 5);
                                if ((stPos != -1) && (enPos != -1)) {
                                    //BufferedImage PNGImage = null;  大循环中已定义
                                    PNGPath = PNGHint.substring(stPos + 5, enPos);
                                    //if(PNGPath.indexOf("AlignedMap_") != -1) continue; 大循环中已经判断过了
                                    //if((!PNGDic.containsKey(PNGPath)) && (!new File(PNGPath).exists())) PNGPath = "images/layers.png";  File.exist频繁访问硬盘,太低效
                                    if (PNGDic.containsKey(PNGPath)) PNGImage = PNGDic.get(PNGPath);
                                    else {
                                        if (!new File(PNGPath).exists())
                                            PNGImage = TextureLoader.loadImage("images/layers.png");
                                        else PNGImage = TextureLoader.loadImage(PNGPath);
                                        if (PNGDic.size() > DefaultBufferImageNumber) PNGDic.clear();
                                        PNGDic.put(PNGPath, PNGImage);
                                    }

                                    int textureID = TextureLoader.loadTexture(PNGImage, 0, 0, PNGImage.getWidth(), PNGImage.getHeight());

                                    offsetx = -PNGImage.getWidth() / 2;
                                    offsety = -PNGImage.getHeight() / 2;
                                    pngwidth = PNGImage.getWidth();
                                    pngheight = PNGImage.getHeight();
                                    stPos = PNGHint.indexOf("[PNGOffsetX:");
                                    if (stPos != -1) enPos = PNGHint.indexOf(']', stPos + 12);
                                    if ((stPos != -1) && (enPos != -1))
                                        offsetx = Integer.parseInt(PNGHint.substring(stPos + 12, enPos));
                                    stPos = PNGHint.indexOf("[PNGOffsetY:");
                                    if (stPos != -1) enPos = PNGHint.indexOf(']', stPos + 12);
                                    if ((stPos != -1) && (enPos != -1))
                                        offsety = Integer.parseInt(PNGHint.substring(stPos + 12, enPos));
                                    stPos = PNGHint.indexOf("[PNGWidth:");
                                    if (stPos != -1) enPos = PNGHint.indexOf(']', stPos + 10);
                                    if ((stPos != -1) && (enPos != -1))
                                        pngwidth = Integer.parseInt(PNGHint.substring(stPos + 10, enPos));
                                    stPos = PNGHint.indexOf("[PNGHeight:");
                                    if (stPos != -1) enPos = PNGHint.indexOf(']', stPos + 11);
                                    if ((stPos != -1) && (enPos != -1))
                                        pngheight = Integer.parseInt(PNGHint.substring(stPos + 11, enPos));

                                    GL11.glColor4f(1f, 1f, 1f, 1f);
                                    //GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);  //在loadtexture中重复
                                    GL11.glBegin(GL11.GL_QUADS);
                                    GL11.glTexCoord2d(0, 0);
                                    GL11.glVertex2i((int) (xx + offsetx), (int) (yy + offsety));
                                    GL11.glTexCoord2d(1, 0);
                                    GL11.glVertex2i((int) (xx + offsetx + pngwidth), (int) (yy + offsety));
                                    GL11.glTexCoord2d(1, 1);
                                    GL11.glVertex2i((int) (xx + offsetx + pngwidth), (int) (yy + offsety + pngheight));
                                    GL11.glTexCoord2d(0, 1);
                                    GL11.glVertex2i((int) (xx + offsetx), (int) (yy + offsety + pngheight));
                                    GL11.glEnd();
                                    GL11.glDeleteTextures(textureID);
                                }
                            }
                        }
                        if (!MapWizard.SingleItem.IsAllFontInvisible)
                            if (
                                    ((!MapWizard.SingleItem.ShowVisualFeature) && ((binary & MapWizard.SingleItem.Ox("10")) != 0))
                                            ||
                                            (MapWizard.SingleItem.ShowVisualFeature && (MapWizard.SingleItem.PointDatabase.PointHint[i].indexOf("[WordVisible:]") != -1))
                                    ) {// For Word--------------------------
                                choose = (binary >> 4) & MapWizard.SingleItem.Ox("111");
                                java.awt.Color Origin = MapWizard.SingleItem.getChooseColor(choose);
                                GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, 1f);

                                if (MapWizard.SingleItem.ShowVisualFeature) {
                                    Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PointDatabase.PointHint[i], "Word");
                                    GL11.glColor4f(Origin.getRed() / 255f, Origin.getGreen() / 255f, Origin.getBlue() / 255f, Origin.getAlpha() / 255f);
                                }

                                xx = GetScreenX(MapWizard.SingleItem.PointDatabase.AllPointX[i]);
                                yy = GetScreenY(MapWizard.SingleItem.PointDatabase.AllPointY[i]);
                                //String PNGHint = MapWizard.SingleItem.PointDatabase.PointHint[i];
                                if(AllowAbsPixelPoint) {
                                    int stPos = -1;
                                    int enPos = -1;
                                    stPos = PNGHint.indexOf("[SetPixelX:");
                                    if (stPos != -1) enPos = PNGHint.indexOf(']', stPos + 11);
                                    if ((stPos != -1) && (enPos != -1))
                                        xx = Integer.parseInt(PNGHint.substring(stPos + 11, enPos));
                                    stPos = PNGHint.indexOf("[SetPixelY:");
                                    if (stPos != -1) enPos = PNGHint.indexOf(']', stPos + 11);
                                    if ((stPos != -1) && (enPos != -1))
                                        yy = Integer.parseInt(PNGHint.substring(stPos + 11, enPos));
                                }

                                DrawCount++;
                                drawString(MapWizard.SingleItem.PointDatabase.getTitle(i), (int)xx, (int)yy);
                            }
                    }
                }
            }
        if((CacheLife == 0) && (DefaultScreenCacheLife != 0)) {
            PointCacheIndexArr = PointInScreenCache.toArray();
            if(PointCacheIndexArr.length < 100000) Arrays.sort(PointCacheIndexArr);
        }
        if(PrintlnOpenGLRenderTime) System.out.println("Point\t" + SwapIter);
        SwapIter = 0;
        if(PrintlnOpenGLRenderTime) System.out.println("rest\t" + Calendar.getInstance().getTimeInMillis());

        while (!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(false, false)) {
            try {
                Thread.sleep(1);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        //以后可以使用四分树索引库来优化大for循环，局部for一遍的时候用索引只需要lg n 时间，显著提高FPS
        // Draw for Extended Components
        GL11.glLineStipple(2, (short) 0x6666);
        if ((MapWizard.SingleItem.Screen.IsExtendedPointVisible) && (MapWizard.SingleItem.Screen.ExtendedPointCount > 0)) {
            for (int i = 0; i < MapWizard.SingleItem.Screen.ExtendedPointCount; i++) {
                xx = GetScreenX(MapWizard.SingleItem.Screen.ExtendedPointX[i]);
                yy = GetScreenY(MapWizard.SingleItem.Screen.ExtendedPointY[i]);
                xx += MapWizard.SingleItem.Screen.ScreenDeltaX;
                yy += MapWizard.SingleItem.Screen.ScreenDeltaY;
                GL11.glColor4f(1f, 1f, 0f, 1f);
                drawPoint(xx, yy, 8);
                if (MapWizard.SingleItem.Screen.IsExtendedPointHintVisible) {
                    drawString(MapWizard.SingleItem.Screen.ExtendedPointHint[i], (int)xx + 6, (int)yy + 6);
                }
            }
            GL11.glColor4f(0f, 1f, 0f, 1f);
            if (MapWizard.SingleItem.Screen.IsConsecutiveLink)
                for (int i = 1; i < MapWizard.SingleItem.Screen.ExtendedPointCount; i++) {
                    int x1 = GetScreenX(MapWizard.SingleItem.Screen.ExtendedPointX[i - 1]);
                    int y1 = GetScreenY(MapWizard.SingleItem.Screen.ExtendedPointY[i - 1]);
                    int x2 = GetScreenX(MapWizard.SingleItem.Screen.ExtendedPointX[i]);
                    int y2 = GetScreenY(MapWizard.SingleItem.Screen.ExtendedPointY[i]);
                    x1 += MapWizard.SingleItem.Screen.ScreenDeltaX;
                    y1 += MapWizard.SingleItem.Screen.ScreenDeltaY;
                    x2 += MapWizard.SingleItem.Screen.ScreenDeltaX;
                    y2 += MapWizard.SingleItem.Screen.ScreenDeltaY;
                    GL11.glEnable(GL11.GL_LINE_STIPPLE);
                    drawLine(x1, y1, x2, y2, 2);
                    GL11.glDisable(GL11.GL_LINE_STIPPLE);

                    int x3 = (x1 + x2) / 2 + (y2 - y1) / 4;
                    int y3 = (y1 + y2) / 2 - (x2 - x1) / 4;
                    x3 = x3 + 3 * (x2 - x3) / 4;
                    y3 = y3 + 3 * (y2 - y3) / 4;
                    drawLine(x3, y3, x2, y2, 2);

                    x3 = (x1 + x2) / 2 - (y2 - y1) / 4;
                    y3 = (y1 + y2) / 2 + (x2 - x1) / 4;
                    x3 = x3 + 3 * (x2 - x3) / 4;
                    y3 = y3 + 3 * (y2 - y3) / 4;
                    drawLine(x3, y3, x2, y2, 2);
                }
            if (MapWizard.SingleItem.Screen.IsHeadTailLink) {
                GL11.glColor4f(1f, 0f, 0f, 1f);
                int x1 = GetScreenX(MapWizard.SingleItem.Screen.ExtendedPointX[0]);
                int y1 = GetScreenY(MapWizard.SingleItem.Screen.ExtendedPointY[0]);
                int Ptr = MapWizard.SingleItem.Screen.ExtendedPointCount - 1;
                if (Ptr < 0) Ptr = 0;
                int x2 = GetScreenX(MapWizard.SingleItem.Screen.ExtendedPointX[Ptr]);
                int y2 = GetScreenY(MapWizard.SingleItem.Screen.ExtendedPointY[Ptr]);
                x1 += MapWizard.SingleItem.Screen.ScreenDeltaX;
                y1 += MapWizard.SingleItem.Screen.ScreenDeltaY;
                x2 += MapWizard.SingleItem.Screen.ScreenDeltaX;
                y2 += MapWizard.SingleItem.Screen.ScreenDeltaY;
                GL11.glEnable(GL11.GL_LINE_STIPPLE);
                drawLine(x1, y1, x2, y2, 2);
            }
            if (MapWizard.SingleItem.Screen.ExtendedPointSelectCount != 0) {
                for (int i = 0; i < MapWizard.SingleItem.Screen.ExtendedPointSelectCount; i++) {
                    xx = GetScreenX(MapWizard.SingleItem.Screen.ExtendedPointX[MapWizard.SingleItem.Screen.ExtendedPointSelectList[i]]);
                    yy = GetScreenY(MapWizard.SingleItem.Screen.ExtendedPointY[MapWizard.SingleItem.Screen.ExtendedPointSelectList[i]]);
                    xx += MapWizard.SingleItem.Screen.ScreenDeltaX;
                    yy += MapWizard.SingleItem.Screen.ScreenDeltaY;
                    GL11.glColor4f(1f, 0f, 0f, 1f);
                    drawPoint(xx, yy, 8);
                }
            }
        }
        GL11.glDisable(GL11.GL_LINE_STIPPLE);

        if(MapWizard.SingleItem.Screen.TextArea2Content!=null)
        if (MapWizard.SingleItem.Screen.IsTextArea2Visible) {
            GL11.glColor4f(0f, 1f, 0f, 0.88f);
            drawString(MapWizard.SingleItem.Screen.TextArea2Content, 10, Display.getHeight() - 15);
        }
        if(MapWizard.SingleItem.Screen.TextArea1Content!=null)
        if (MapWizard.SingleItem.Screen.IsTextArea1Visible) {
            GL11.glColor4f(0f, 0f, 0f, 0.99f);
            GL11.glBegin(GL11.GL_POLYGON);
            GL11.glVertex2f(0, 0);
            GL11.glVertex2f(ScreenWidth, 0);
            GL11.glVertex2f(ScreenWidth, 20);
            GL11.glVertex2f(0, 20);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glColor4f(1f, 0f, 0f, 0.88f);
            drawString(MapWizard.SingleItem.Screen.TextArea1Content, 10, 5);
        }

        if(GetScreenShot){
            ScreenShotFilePath = ScreenShot(ScreenShotFilePath);
            MapWizard.SingleItem.setTitle("ScreenShot::" + ScreenShotFilePath);
            GetScreenShot = false;
        }

        if(CacheLife == 0) {
            CacheLife = DefaultScreenCacheLife;
            if(DefaultScreenCacheLife == 0){
                if(PointInScreenCache.size() != 0) PointInScreenCache.clear();
                if(AlignedMapInScreenCache.size() != 0) AlignedMapInScreenCache.clear();
                if(LineInScreenCache.size() != 0) LineInScreenCache.clear();
                if(PolygonInScreenCache.size() != 0) PolygonInScreenCache.clear();
            }
        } else {
            CacheLife--;
            if(CacheLife == 0){
                if(PointInScreenCache.size() != 0) PointInScreenCache.clear();
                if(AlignedMapInScreenCache.size() != 0) AlignedMapInScreenCache.clear();
                if(LineInScreenCache.size() != 0) LineInScreenCache.clear();
                if(PolygonInScreenCache.size() != 0) PolygonInScreenCache.clear();
            }
        }
        if(PrintlnOpenGLRenderTime) System.out.println("en\t" + Calendar.getInstance().getTimeInMillis());
        if(PrintlnOpenGLRenderTime) System.out.println("===============================================");
    }
    private static int CacheLife = 0;
    private HashSet<Integer> PointInScreenCache = new HashSet<Integer>();
    private HashSet<Integer> LineInScreenCache = new HashSet<Integer>();
    private HashSet<Integer> PolygonInScreenCache = new HashSet<Integer>();
    private HashSet<Integer> AlignedMapInScreenCache = new HashSet<Integer>();
    private Object[] PointCacheIndexArr = null;
    private Object[] LineCacheIndexArr = null;
    private Object[] PolygonCacheIndexArr = null;
    private Object[] AlignedMapCacheIndexArr = null;

    public static boolean AllowAbsPixelPoint = true;
    public static boolean AllowPNGPoint = true;


//    static char[] MotherCharArr = new char[1024 * 8];
//    static char[] SonCharArr = new char[1024 * 8];
//    synchronized public static int KMPindexOf(String origin, String str, int fromIndex) {
//        for(int index = 0; index < origin.length(); index++) MotherCharArr[index] = origin.charAt(index);
//        for(int index = 0; index < str.length(); index++) SonCharArr[index] = str.charAt(index);
//        return indexOf(MotherCharArr, 0, origin.length(),
//                SonCharArr, 0, str.length(), fromIndex);
//    }
//    static int[] T = new int[1024 * 8];
//    synchronized static int indexOf(char[] source, int sourceOffset, int sourceCount,
//                       char[] target, int targetOffset, int targetCount, int fromIndex) {
//        if (fromIndex >= sourceCount) {
//            return (targetCount == 0 ? sourceCount : -1);
//        }
//        if (fromIndex < 0) {
//            fromIndex = 0;
//        }
//        if (targetCount == 0) {
//            return fromIndex;
//        }
//        int c=1,t=0;
//        while(c<targetCount){
//            if(t==0||target[c-1]==target[t-1]){
//                t++;c++;
//                if(target[t-1]!=target[c-1])T[c]=t;
//                else T[c]=T[t];
//            }else{
//                t=T[t];
//            }
//        }
//        c=targetOffset;
//        t=sourceOffset;
//        int max1=targetCount+targetOffset;
//        int max2=sourceCount+sourceOffset;
//        while(c<max1&&t<max2){
//            if(c==targetOffset||target[c]==source[t]){
//                c++;t++;}
//            else {
//                c=T[c]+targetOffset;
//            }
//        }
//        if(c>=max1)return t-sourceOffset-targetCount;
//        else return -1;
//    }

    public static void Sample() {
        Tessellator tess = new Tessellator();

        double[] verticesC1 = new double[]{
                0, 0, 0, //0
                100, 0, 0, //1
                100, 100, 0, //2
                0, 100, 0 //3
        };

        tess.gluBeginPolygon();

        tess.gluTessBeginContour();
        tess.gluTessVertex(verticesC1, 0, 0);
        tess.gluTessVertex(verticesC1, 3, 1);
        tess.gluTessVertex(verticesC1, 6, 2);
        tess.gluTessVertex(verticesC1, 9, 3);
        tess.gluTessEndContour();

        tess.gluEndPolygon();
        tess.gluDeleteTess();

        for (int i = 0; i < tess.primitives.size(); i++) {
            System.out.println(tess.primitives.get(i).toString());
        }
    }
}

class Primitive {
    public final int type;
    public final ArrayList<Integer> vertices = new ArrayList<>();

    public Primitive(int type) {
        this.type = type;
    }

    private String getTypeString() {
        switch (type) {
            case GL11.GL_TRIANGLES:
                return "GL_TRIANGLES";
            case GL11.GL_TRIANGLE_STRIP:
                return "GL_TRIANGLE_STRIP";
            case GL11.GL_TRIANGLE_FAN:
                return "GL_TRIANGLE_FAN";
            default:
                return Integer.toString(type);
        }
    }

    @Override
    public String toString() {
        String s = "New Primitive " + getTypeString();
        for (int i = 0; i < vertices.size(); i++) {
            s += "\nIndex: " + vertices.get(i);
        }
        return s;
    }
}

class Tessellator extends GLUtessellatorImpl {
    public ArrayList<Primitive> primitives = new ArrayList<Primitive>();

    public Tessellator() {
        org.lwjgl.util.glu.GLUtessellatorCallbackAdapter callback = new org.lwjgl.util.glu.GLUtessellatorCallbackAdapter() {
            @Override
            public void begin(int type) {
                Tessellator.this.primitives.add(new Primitive(type));
            }

            @Override
            public void vertex(Object vertexData) {
                Integer coords = (Integer) vertexData;
                Tessellator.this.getLastPrimitive().vertices.add(coords);
            }

            @Override
            public void error(int errnum) {
                throw new RuntimeException("GLU Error " + GLU.gluErrorString(errnum));
            }

        };
        this.gluTessCallback(GLU.GLU_TESS_BEGIN, callback);
        this.gluTessCallback(GLU.GLU_VERTEX, callback);
    }

    private Primitive getLastPrimitive() {
        return primitives.get(primitives.size() - 1);
    }
}