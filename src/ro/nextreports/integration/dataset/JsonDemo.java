package ro.nextreports.integration.dataset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import ro.fortsoft.dataset.core.BaseDataSetMetaData;
import ro.fortsoft.dataset.core.DataSet;
import ro.fortsoft.dataset.core.DataSetBuilder;
import ro.fortsoft.dataset.json.JsonDataSet;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportRunner;
import ro.nextreports.engine.context.DataSetConnectionContext;
import ro.nextreports.engine.queryexec.DataSetResult;
import ro.nextreports.engine.util.LoadReportException;
import ro.nextreports.engine.util.ReportUtil;
import ro.nextreports.engine.util.StringUtil;

public class JsonDemo {
	
	public static final String NEXTREPORTS_HOME = "C:\\Users\\mihai.panaitescu\\.nextreports-8.3-SNAPSHOT";
	
    public static Report loadDemoReport() throws FileNotFoundException, LoadReportException {
    	String location = NEXTREPORTS_HOME + File.separator + "output" +
	            File.separator + "Demo" + File.separator + "Reports";
		
		String file = location + File.separator + "SimpleTest.report";    	              
        
        Report report = ReportUtil.loadReport(new FileInputStream(file));       
        return report;    	
    }
    
    public static void run(Report report) throws Exception {
    	
    	Map<String, Class<?>> columns = new HashMap<>(); 
    	columns.put("EMPLOYEEID", String.class);
    	columns.put("NAME", String.class);
    	columns.put("FIRSTNAME", String.class);
    	columns.put("EMAIL", String.class);
    	
    	String path = "test1.json";
    	
    	BaseDataSetMetaData dataSetMetaData = new BaseDataSetMetaData();
    	for (String column : columns.keySet()) {
    		dataSetMetaData.addField(column, columns.get(column));
    	}            

    	InputStream is = StringUtil.getInputStream(path);
        DataSetBuilder builder = new JsonDataSet.Builder(is).setMetaData(dataSetMetaData);
        DataSet ds = builder.build();
        
        DataSetConnectionContext context = new DataSetConnectionContext();
        context.setDataSet(ds);        
    	
    	ReportRunner runner = new ReportRunner();
    	runner.setReport(report);    	
    	runner.setConnectionContext(context);
    	runner.setFormat(ReportRunner.PDF_FORMAT);
    	runner.run(new FileOutputStream("test.pdf"));
    }
    
    public static void main(String[] args) {
		try {
			Report rep = loadDemoReport();
			System.out.println(rep);
			run(rep);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
