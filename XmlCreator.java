package decisiontree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


/**
 * @author henok
 *
 */
public class XmlCreator{
	
    List<Element> pathway =new ArrayList<>();
    DocumentBuilderFactory dbFactory;
	DocumentBuilder dBuilder;
	Document doc;
	TransformerFactory transformerFactory;
	Transformer transformer;
	DOMSource source;
	StreamResult result;
	StreamResult consoleResult;
	public List<String> pathadress= new ArrayList<>();
	public XmlCreator() {
		dbFactory =DocumentBuilderFactory.newInstance();
		try {
			dBuilder= dbFactory.newDocumentBuilder();
			doc= dBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	public  void rootxmlnode(String entropy){
		Element rootElement= doc.createElement("Tree");
	    doc.appendChild(rootElement);
		Element firstnode=doc.createElement("Node");
		firstnode.setAttribute("Class", "Tree");
		firstnode.setAttribute("Value","----");
		firstnode.setAttribute("Entropy",entropy);
		rootElement.appendChild(firstnode);
        pathadress.add("/Tree");
        pathadress.add("/Node[@Class='Tree']");
        
	}
	public  void newxmlnode(int nodelevel, String nodeclass, String classvalue, String entropy, String  decisionclass)  {
		String currentadress="";
		if((pathadress.size()-nodelevel)>1){
			int requiredPathAdressIndex=nodelevel+1;
			for(int removecount=(pathadress.size()-1);removecount>requiredPathAdressIndex;removecount--){
				pathadress.remove(removecount);
			}
		}
		for(int i=0;i<pathadress.size();i++){currentadress+=pathadress.get(i);}
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Element currentnode=null;
		try {
			currentnode = (Element) xpath.evaluate(currentadress,doc,XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Element newelement= doc.createElement(nodeclass);
		newelement.setAttribute("Entropy",entropy);
		newelement.setAttribute("Value",classvalue);
		currentnode.appendChild(newelement);
		pathadress.add("/"+nodeclass+"[@Value='"+classvalue+"']");
	}
	public  void resultnode(int nodelevel, String nodeclass, String classvalue, String entropy, String  decisionclass)  {
		String currentadress="";
		if((pathadress.size()-nodelevel)>1){
			int requiredPathAdressIndex=nodelevel+1;
			for(int removecount=(pathadress.size()-1);removecount>requiredPathAdressIndex;removecount--){
				pathadress.remove(removecount);
			}
		}
			for(int i=0;i<pathadress.size();i++){currentadress+=pathadress.get(i);}
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Element currentnode=null;
		try {
			currentnode = (Element) xpath.evaluate(currentadress,doc,XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Element newelement= doc.createElement(nodeclass);
		newelement.setAttribute("Entropy",entropy);
		newelement.setAttribute("Value",classvalue);
        Text outcome = doc.createTextNode(decisionclass);
        newelement.appendChild(outcome);
		currentnode.appendChild(newelement);
		//"/Node[@Class='"+nodeclass+"'][@Value='"+classvalue+"'
		pathadress.add("/"+nodeclass+"[@Value='"+classvalue+"']");
	}
	
	// write the content into xml file
	public  void printxml() throws TransformerException{
		
    	 TransformerFactory transformerFactory = TransformerFactory.newInstance();
         Transformer transformer = transformerFactory.newTransformer();
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
         transformer.setOutputProperty(OutputKeys.METHOD, "xml");
         transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
         DOMSource source = new DOMSource(doc);
         StreamResult result = new StreamResult(new File("cars.xml"));
         transformer.transform(source, result);
         
         // Output to console for testing
         StreamResult consoleResult = new StreamResult(System.out);
         
		  transformer.transform(source, consoleResult);	
	   }
}