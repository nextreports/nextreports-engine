package ro.nextreports.integration.dataset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import ro.fortsoft.dataset.core.BaseDataSetMetaData;
import ro.fortsoft.dataset.core.DataSet;
import ro.fortsoft.dataset.core.DataSetBuilder;
import ro.fortsoft.dataset.xml.XmlDataSet;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportRunner;
import ro.nextreports.engine.context.DataSetConnectionContext;
import ro.nextreports.engine.util.LoadReportException;
import ro.nextreports.engine.util.ReportUtil;
import ro.nextreports.engine.util.StringUtil;

public class XmlDemo {
	
public static final String NEXTREPORTS_HOME = "C:\\Users\\mihai.panaitescu\\.nextreports-8.3-SNAPSHOT";
	
    public static Report loadDemoReport() throws FileNotFoundException, LoadReportException {
    	String location = NEXTREPORTS_HOME + File.separator + "output" +
	            File.separator + "BnrExchangeRemote" + File.separator + "Reports";
		
		String file = location + File.separator + "XmlTest.report";    	              
        
        Report report = ReportUtil.loadReport(new FileInputStream(file));       
        return report;    	
    }
    
    public static void run(Report report) throws Exception {
    	
    	Map<String, Class<?>> columns = new HashMap<>(); 
    	columns.put("date", String.class);
    	columns.put("currency", String.class);
    	columns.put("rate", Integer.class);    	
    	
    	//String path = "http://www.bnr.ro/nbrfxrates.xml";
    	String path = "http://www.bnro.ro/nbrfxrates10days.xml";

    	BaseDataSetMetaData dataSetMetaData = new BaseDataSetMetaData();
    	for (String column : columns.keySet()) {
    		dataSetMetaData.addField(column, columns.get(column));
    	}            

    	// proxy settings    	
		System.getProperties().put("proxyPort", "128");
		System.getProperties().put("proxyHost", "192.168.16.7");
    	
    	URL url = new URL(path);		
		URLConnection con = url.openConnection();
		// for ASF proxy to work!
		con.setRequestProperty("X-Forwarded-For", "8.8.8.8");
		InputStream is = con.getInputStream();
    	
    	DataSetBuilder builder = new XmlDataSet.Builder(is)
              .setMetaData(dataSetMetaData)
              .setExpression("//Cube/Rate")
              .setFieldExpression("currency", "./@currency")
              .setFieldExpression("date", "../@date");

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
