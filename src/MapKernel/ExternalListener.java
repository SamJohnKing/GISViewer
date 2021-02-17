package MapKernel;

public interface ExternalListener {
	abstract void MousePressedListener(double x, double y, int xPixel, int yPixel, boolean isLeft);
	//abstract void MouseDragListener(double x1, double y1, double x2, double y2, int x1Pixel, int y1Pixel, int x2Pixel, int y2Pixel, boolean isLeft);
}
