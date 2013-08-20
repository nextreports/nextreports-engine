Integration zip contains in lib folder all the jars that a java/web application must use to integrate NextReports.

* jcalendar.jar is not needed (it is used for the swing demo).
* derby.jar is not nedded (it is used just to connect to demo derby database)

* nextreports-engine.jar is needed for all type of exports
* xstream.jar is needed for all type of exports
* commons-logging.jar is needed for all type of exports
* commons-jexl.jar is needed for all type of exports

* itext.jar is needed just for PDF and RTF exports
* itext-rtf.jar is needed just for RTF export

* poi.jar is needed just for EXCEL export
* poi-contrib.jar is needed just for EXCEL export

* jcommon.jar is needed for exporting reports which contain charts
* jfreechart.jar is needed for exporting reports which contain charts

* winstone.jar is needed just for chart demo
* jofc2.jar is needed just for chart demo 

The java docs contains the doc for the classes that someone has to use in an integration software process.

Practically to integrate nextreports there are two steps :
1. How to set the parameters, if any (this implies to create a user interface : see a simple class example for
   swing RuntimeParametersPanel.java)
2. How to run the report : see 2 simple demos to run a report : SimpleDemo and FluentSimpleDemo.

The swing demo shows as a hint how to use dependent parameters . In the user interface
you'll have to load at first only the parameters that are not dependent, and load those that are dependent only
after all parameters they depend on are selected. (see java doc, especially for ParameterUtil class).
The swing demo also shows a hint  about parameter default values (parameter values that are automatically
entered/selected for a user interface at runtime).

FluentSimpleDemo shows you how to register a listener to be notified about the current processed record.

To run the demo as it is (it runs Timesheet.report from the place you specify) you'll have to
update NEXTREPORTS_HOME in DemoUtil.java. 

To make your own database connection for testing you can create a DemoDefinition class (see DerbyDemoDefinition.java) 
and add it inside DemoDefinitionFactory.java. Then in DemoUtil you use this definition.

Timesheet.report has two images. The example takes care to copy those images to the folder where exported
report is generated (current directory). For HTML that is enough to view images. For other types like PDF, RTF, EXCEL,
you will have to add the folder where you copy images to the CLASSPATH (because report images are loaded from classpath).

Timesheet_Charts.reports contains internal charts. Exporting this report will also generate jpg images for charts.

ChartDemo shows you how to run a Next chart as flash, image or data.
FluentChartDemo exports a chart as an image with a specific name and size.

Integration using nextreports engine is free of charge.