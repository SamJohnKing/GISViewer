package Algorithm;
public interface AlgorithmInterface {
	abstract void setHandle(MapKernel.MapControl Handle);
	abstract void setInput(Object Input);
	abstract void setInput(Object Input1,Object Input2);
	abstract Object getOutput();
	abstract void AlgorithmProcessor();
}
