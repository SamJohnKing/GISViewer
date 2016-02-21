package LWJGLPackage;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import MapKernel.MapWizard;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class OriginalOpenGLWizard{

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

    public static void GetInstance(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OriginalOpenGLWizard displayExample = new OriginalOpenGLWizard();
                displayExample.start(1366, 768);
            }
        }).start();
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

    public static void drawString(String s, int x, int y, float r, float g, float b, float a) {
        GL11.glColor4f(r, g, b, a);
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
        //GL11.glClearColor(0.5f,0.5f,0.5f,1f);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        // draw quad
        drawString("ABCDEFGHIJKLMNOPQRSTUVWXYZ\nAaBbCcDdEefFGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz\n01/2345\\6789\nHello World\n2015-01-01 12:22:32", 300, 400, 0.5f, 1f, 1f, 0.5f);
        GL11.glColor4f(0.2f, 0.5f, 0.1f, 0.5f);
        // draw quad
        GL11.glBegin(GL11.GL_POLYGON);
        GL11.glVertex2f(100, 100);
        GL11.glVertex2f(100 + 200, 100);
        GL11.glVertex2f(100 + 200, 100 + 200);
        GL11.glVertex2f(100, 100 + 150);
        GL11.glVertex2f(150, 300);
        GL11.glEnd();
        GL11.glColor4f(0.6f, 0.2f, 0.4f, 0.5f);
        GL11.glBegin(GL11.GL_POLYGON);
        GL11.glVertex2f(200, 200);
        GL11.glVertex2f(200 + 200, 200);
        GL11.glVertex2f(200 + 200, 200 + 200);
        GL11.glVertex2f(200, 200 + 150);
        GL11.glVertex2f(250, 400);
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
