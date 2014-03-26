package ro.nextreports.engine.chart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class XYZList implements Serializable {
		
	private static final long serialVersionUID = -529136438152791292L;
	
	private List<Number> xList;
	private List<Number> yList;
	private List<Number> zList;
	private List<String> labels;
	
	public XYZList() {
		xList = new ArrayList<Number>();
		yList = new ArrayList<Number>();
		zList = new ArrayList<Number>();
		labels = new LinkedList<String>();
	}

	public List<Number> getxList() {
		return xList;
	}

	public List<Number> getyList() {
		return yList;
	}

	public List<Number> getzList() {
		return zList;
	}

	public List<String> getLabels() {
		return labels;
	}		
		
}
