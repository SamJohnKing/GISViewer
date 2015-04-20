package ExtendedToolPane;

import MapKernel.MapControl;

public interface ExtendedToolPaneInterface {
	abstract public void setLongitudeLatitude(double x,double y);
	abstract public void emerge();
	abstract public void convey(double x,double y);
	abstract public void convey(double x1,double y1,double x2,double y2);
	abstract public void setHandle(MapControl MainHandle);
	abstract public void confirm();
	abstract public String GetSocketResult(String SocketQuery) throws Exception;
}
