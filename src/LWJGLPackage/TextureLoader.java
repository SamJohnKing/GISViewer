package LWJGLPackage;
import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;

public class TextureLoader {
    private static final int BYTES_PER_PIXEL = 4;// 3 for RGB, 4 for RGBA
    private static HashMap<BufferedImage, ByteBuffer> ByteBufferofFullScaleImage = new HashMap<BufferedImage, ByteBuffer>();
    private static int[] pixels = new int[2048 * 2048];
    private static int LastStartX = -1, LastStartY = -1, LastEndX = -1, LastEndY = -1;
    private static BufferedImage Lastimage = null;
    private static ByteBuffer LastBuf= null;
    private static ByteBuffer StaticBuf = BufferUtils.createByteBuffer(2048 * 2048 * BYTES_PER_PIXEL);
    public static int loadBackgroundTexture(BufferedImage image, int StartX, int StartY, int EndX, int EndY) {//使用于唯一背景大图，分片分屏动态读取
        int New_Width = EndX - StartX;
        int New_Height = EndY - StartY;
        ByteBuffer buffer = null;
        if((StartX == LastStartX) && (StartY == LastStartY) && (EndX == LastEndX) && (EndY == LastEndY) && (image == Lastimage)){
            buffer = LastBuf;
        } else {
            buffer = StaticBuf;
            buffer.clear();
            //int[] pixels = new int[New_Width * New_Height]; 用静态全局加速
            image.getRGB(StartX, StartY, New_Width, New_Height, pixels, 0, New_Width);
            // 4 for RGBA, 3 for RGB
            for (int y = New_Height - 1; y >= 0; y--) {
                for (int x = 0; x < New_Width; x++) {
                    int pixel = pixels[y * New_Width + x];
                    buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
                    buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
                    buffer.put((byte) (pixel & 0xFF)); // Blue component
                    buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component.
                    // Only for RGBA
                }
            }
            buffer.flip(); // FOR THE LOVE OF GOD DO NOT FORGET THIS
            LastStartX = StartX; LastStartY = StartY; LastEndX = EndX; LastEndY = EndY; Lastimage = image; LastBuf = buffer;
        }
        // You now have a ByteBuffer filled with the color data of each pixel.
        // Now just create a texture ID and bind it. Then you can load it using
        // whatever OpenGL method you want, for example:
        int textureID = glGenTextures(); // Generate texture ID
        glBindTexture(GL_TEXTURE_2D, textureID); // Bind texture ID
        // Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        // Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, New_Width, New_Height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        // Return the texture ID so we can bind it later again
        return textureID;
    }
    public static int loadTexture(BufferedImage image, int StartX, int StartY, int EndX, int EndY) { //适用与大量小图片全图一次性加载,生成永久性缓存
        int New_Width = EndX - StartX;
        int New_Height = EndY - StartY;
        ByteBuffer buffer = null;
        if((StartX == 0) && (StartY == 0) && (EndX == image.getWidth()) && (EndY == image.getHeight()) && (ByteBufferofFullScaleImage.containsKey(image))){
            buffer = ByteBufferofFullScaleImage.get(image);
        } else {
            buffer = BufferUtils.createByteBuffer(New_Width * New_Height * BYTES_PER_PIXEL);
            //int[] pixels = new int[New_Width * New_Height]; 用静态全局加速
            image.getRGB(StartX, StartY, New_Width, New_Height, pixels, 0, New_Width);
            // 4 for RGBA, 3 for RGB
            for (int y = New_Height - 1; y >= 0; y--) {
                for (int x = 0; x < New_Width; x++) {
                    int pixel = pixels[y * New_Width + x];
                    buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
                    buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
                    buffer.put((byte) (pixel & 0xFF)); // Blue component
                    buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component.
                    // Only for RGBA
                }
            }
            buffer.flip(); // FOR THE LOVE OF GOD DO NOT FORGET THIS
            if((StartX == 0) && (StartY == 0) && (EndX == image.getWidth()) && (EndY == image.getHeight())) {
                if(ByteBufferofFullScaleImage.size() > OriginalOpenGLWizard.DefaultBufferImageNumber) ByteBufferofFullScaleImage.clear();
                ByteBufferofFullScaleImage.put(image, buffer);
            }
        }
        // You now have a ByteBuffer filled with the color data of each pixel.
        // Now just create a texture ID and bind it. Then you can load it using
        // whatever OpenGL method you want, for example:
        int textureID = glGenTextures(); // Generate texture ID
        glBindTexture(GL_TEXTURE_2D, textureID); // Bind texture ID
        // Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        // Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, New_Width, New_Height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        // Return the texture ID so we can bind it later again
        return textureID;
    }

    public static BufferedImage loadImage(String loc) {
        try {
            return ImageIO.read(new java.io.File(loc));
        } catch (IOException e) {
            // Error Handling Here
        }
        return null;
    }
}