package ro.nextreports.engine.context;

import ro.fortsoft.dataset.core.DataSet;

public class DataSetConnectionContext implements XConnectionContext {

	private DataSet dataSet;

	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}
		
}
