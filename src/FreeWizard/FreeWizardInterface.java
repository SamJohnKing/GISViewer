package FreeWizard;

import MapKernel.MapControl;

public interface FreeWizardInterface {
	abstract public void emerge();
	abstract public void submerge();
	abstract public void setHandle(MapControl MainHandle);
	abstract public boolean IsEmerge();
}
