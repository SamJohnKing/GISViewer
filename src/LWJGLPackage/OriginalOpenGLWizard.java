package LWJGLPackage;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class OriginalOpenGLWizard {

    static OriginalOpenGLWizard SingleItem = null;
    /**
     * position of quad
     */
    float x = 512, y = 384;
    int ScreenWidth = -1;
    int ScreenHeight = -1;
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

    public static void main_sample(){
        OriginalOpenGLWizard displayExample = new OriginalOpenGLWizard();
        displayExample.start(1366,768);
    }

    public void start(int Width, int Height) {
        try {
            Display.setDisplayMode(new DisplayMode(Width, Height));
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        initGL(Width, Height); // init OpenGL
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

    public static void drawString(String s, int x, int y, float r, float g, float b) {
        GL11.glColor3f(r,g,b);
        int startX = x;
        GL11.glBegin(GL11.GL_POINTS);
        for (char c : s.toLowerCase().toCharArray()) {
            if (c == 'a') {
                for (int i = 0; i < 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                    GL11.glVertex2f(x + 7, y + i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 8);
                    GL11.glVertex2f(x + i, y + 4);
                }
                x += 8;
            } else if (c == 'b') {
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
            } else if (c == 'c') {
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
            } else if (c == 'd') {
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
            } else if (c == 'e') {
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
            } else if (c == 'f') {
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
            } else if (c == 'g') {
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
            } else if (c == 'h') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                    GL11.glVertex2f(x + 7, y + i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 4);
                }
                x += 8;
            } else if (c == 'i') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2f(x + 3, y + i);
                }
                for (int i = 1; i <= 5; i++) {
                    GL11.glVertex2f(x + i, y + 0);
                    GL11.glVertex2f(x + i, y + 8);
                }
                x += 7;
            } else if (c == 'j') {
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
            } else if (c == 'k') {
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
            } else if (c == 'l') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                }
                for (int i = 1; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y);
                }
                x += 7;
            } else if (c == 'm') {
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
            } else if (c == 'n') {
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
            } else if (c == 'o' || c == '0') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                    GL11.glVertex2f(x + 7, y + i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 8);
                    GL11.glVertex2f(x + i, y + 0);
                }
                x += 8;
            } else if (c == 'p') {
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
            } else if (c == 'q') {
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
            } else if (c == 'r') {
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
            } else if (c == 's') {
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
            } else if (c == 't') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2f(x + 4, y + i);
                }
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2f(x + i, y + 8);
                }
                x += 7;
            } else if (c == 'u') {
                for (int i = 1; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                    GL11.glVertex2f(x + 7, y + i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y + 0);
                }
                x += 8;
            } else if (c == 'v') {
                for (int i = 2; i <= 8; i++) {
                    GL11.glVertex2f(x + 1, y + i);
                    GL11.glVertex2f(x + 6, y + i);
                }
                GL11.glVertex2f(x + 2, y + 1);
                GL11.glVertex2f(x + 5, y + 1);
                GL11.glVertex2f(x + 3, y);
                GL11.glVertex2f(x + 4, y);
                x += 7;
            } else if (c == 'w') {
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
            } else if (c == 'x') {
                for (int i = 1; i <= 7; i++)
                    GL11.glVertex2f(x + i, y + i);
                for (int i = 7; i >= 1; i--)
                    GL11.glVertex2f(x + i, y + 8 - i);
                x += 8;
            } else if (c == 'y') {
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
            } else if (c == 'z') {
                for (int i = 1; i <= 6; i++) {
                    GL11.glVertex2f(x + i, y);
                    GL11.glVertex2f(x + i, y + 8);
                    GL11.glVertex2f(x + i, y + i);
                }
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
                y -= 10;
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
            }
        }
        GL11.glEnd();
    }

    private void HandleInput() {
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

    public void renderGL() {
        // Clear The Screen And The Depth Buffer
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        // draw quad
        drawString("Hello:World", 400, 400, 1f, 1f, 1f);

        GL11.glColor4f(0.2f,0.5f,0.1f,0f);
        // draw quad
        GL11.glBegin(GL11.GL_POLYGON);
        GL11.glVertex2f(100,100);
        GL11.glVertex2f(100+200,100);
        GL11.glVertex2f(100+200,100+200);
        GL11.glVertex2f(100,100+150);
        GL11.glVertex2f(150,300);
        GL11.glEnd();
        GL11.glPopMatrix();

        for (int dx = -50; dx <= 50; dx++)
            for (int dy = -50; dy <= 50; dy++) {
                GL11.glColor3f((float) (dx + 50) / 100, (float) (dy + 50) / 100, 0.5f);
                x += dx * 20;
                y += dy * 20;
                GL11.glPushMatrix();
                GL11.glBegin(GL11.GL_LINES);
/*
                        GL11.glVertex2f(x - 6, y + 5);
                        GL11.glVertex2f(x - 3, y + 2);
                        
                        GL11.glVertex2f(x - 3, y + 2);
                        GL11.glVertex2f(x + 3, y + 7);
                        
                        GL11.glVertex2f(x + 3, y + 7);
                        GL11.glVertex2f(x + 3, y + 1);
                        
                        GL11.glVertex2f(x + 3, y + 1);
                        GL11.glVertex2f(x + 7, y + 2);
                        
                        GL11.glVertex2f(x + 7, y + 2);
                        GL11.glVertex2f(x + 2, y - 1);
                        
                        GL11.glVertex2f(x + 2, y - 1);
                        GL11.glVertex2f(x + 5, y - 5);
                        
                        GL11.glVertex2f(x + 5, y - 5);
                        GL11.glVertex2f(x + 0, y - 4);
                        
                        GL11.glVertex2f(x + 0, y - 4);
                        GL11.glVertex2f(x - 5, y - 6);
                        
                        GL11.glVertex2f(x - 5, y - 6);
                        GL11.glVertex2f(x - 4, y + 0);
                        
                        GL11.glVertex2f(x - 4, y + 0);
                        GL11.glVertex2f(x - 6, y + 5);
*/
                GL11.glEnd();
                GL11.glPopMatrix();

                x -= dx * 20;
                y -= dy * 20;
            }
    }
}
