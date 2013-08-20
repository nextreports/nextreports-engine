/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ro.nextreports.engine.util.converter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// Converter from versions less than 5.2 to current engine version

// less than 5.2:
//         <headerBand>
//            <name>Header</name>
//            <row>
//               <band-element> 
//                     ....
//                     <startOnNewPage>false</startOnNewPage>
//               </band-element>
//            </row>
//         </headerBand>
//
// starting with 5.2:
//         <headerBand>
//            <name>Header</name>
//            <rows>
//               <row-element>
//                  <elements>
//                     <band-element> 
//                          ....
//                          -- no more startOnNewPage  
//                     </band-element>
//                  </elements>
//                  -- formatting conditions
//                  <startOnNewPage>false</startOnNewPage>
//               <row-element>
//            </rows>
//         </headerBand>

public class Converter_5_2 extends AbstractNextConverter {	
	
	public String getConverterVersion() {
		return "5.2";
	}
		
	protected Document convert(Document doc) throws Exception {

		XPathFactory xfactory = XPathFactory.newInstance();
		XPath xpath = xfactory.newXPath();

		// look for bands (second expression is for bands from 
		// groupHeaderBand and groupFooterBand)
		XPathExpression expr = xpath.compile(
				"//layout/*[name()='headerBand' or name()='pageHeaderBand' "
				+ "or name()='detailBand' or name()='pageFooterBand' "
				+ "or name()='footerBand'] | //layout/*/*[name()='band']");

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		for (int i = 0; i < nodes.getLength(); i++) {

			// first child is band <name> (take care also of the #text
			// node or other nodes that are not ELEMENT_NODE),
			Node one = nodes.item(i).getFirstChild();							
			while (one.getNodeType() != Node.ELEMENT_NODE) {
				one = one.getNextSibling();				
			}

			// <rows> node will be the first sibling of <name>
			Node row = doc.createElement("rows");
			nodes.item(i).insertBefore(row, one);

			List<Node> removables = new ArrayList<Node>();
			Node sibling = one;
			// create a <row-element> under <rows> for every old <row> tag
			// move the <row> under <row-element> and change old <row> tag
			// to <elements>
			while (sibling != null) {
				if ("row".equals(sibling.getNodeName())) {
					Node rowE = doc.createElement("row-element");
					row.appendChild(rowE);
					Node add = sibling.cloneNode(true);
					rowE.appendChild(add);					
					Node renamed = doc.renameNode(add, null, "elements");
					
					// <startOnNewPage> is removed from all band elements and it is added to <row-element> node
					Node newPageNode = doc.createElement("startOnNewPage");										
					boolean startOnNewPageValue = deleteOldStartOnNewPageNodes(renamed);
					newPageNode.setTextContent(String.valueOf(startOnNewPageValue));
					rowE.appendChild(newPageNode);
					
					removables.add(sibling);										
				}
				sibling = sibling.getNextSibling();
			}

			// remove old <row> tags under <band> tags
			for (Node node : removables) {
				nodes.item(i).removeChild(node);
			}
		}			
				
		return doc;
	}	
	
	private boolean deleteOldStartOnNewPageNodes(Node add) {	
		boolean result = false;
		NodeList bandElements = add.getChildNodes();
		for (int k = 0; k < bandElements.getLength(); k++) {						
			NodeList pNodes = bandElements.item(k).getChildNodes();			
			for (int m = 0; m < pNodes.getLength(); m++) {
				String name = pNodes.item(m).getNodeName();				
				if ("startOnNewPage".equals(name)) {			
					if ("true".equals(pNodes.item(m).getTextContent())) {
						result = true;
					}					
					bandElements.item(k).removeChild(pNodes.item(m));					
				}
			}						
		}
		return result;
		
	}
	
}
