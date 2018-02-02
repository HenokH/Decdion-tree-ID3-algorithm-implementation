package decisiontree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

public class DecisionTree{
	CarData cardata;
	ArrayList<List<String>> nodeAdress;
	NodeAnalysis nodeAnalysis;
	XmlCreator xmlCreator;
	FeatureNameAndValues featurenameandvalues;
	NodeAnalysisResult result;

	public DecisionTree(CarData cardata){
		this.cardata=cardata;
		 nodeAdress=new ArrayList<List<String>>();
		 nodeAnalysis= new NodeAnalysis(cardata,nodeAdress);
		 xmlCreator =new XmlCreator();
		featurenameandvalues=new FeatureNameAndValues();
	}
	 
	public void makeDecsionTree()throws TransformerException{
		//List<String> nextNode=new ArrayList<>();
		nodeAnalysis.countClassificationAtNode(nodeAdress);
		nodeAnalysis.calculateInformationGain();
		result=nodeAnalysis.findNextNode();
		xmlCreator.rootxmlnode(result.getEntropyAtNode());

		int decisonTreeBranchdepth=-1;
		Map<String,Integer> branchindex = new HashMap<>();
			for(String nodeFeature: featurenameandvalues.Attributeclasses.values()){
				branchindex.put(nodeFeature,0);
			}

		boolean  cont =true;
		outer: while (cont){

			if(result.isNodeOutputNode()==false){
				int currentbranchindex=branchindex.get(result.getAttributeAtNode());	
				String branchIndexInwords=featurenameandvalues.AttributeValues.get(result.getAttributeAtNode()).get(currentbranchindex);//what is the current branch number for this class
				
				List<String>tempnextnode=new ArrayList<String>() ;
				tempnextnode.add(result.getAttributeAtNode());
				tempnextnode.add(branchIndexInwords);//????
				nodeAdress.add(tempnextnode);
				decisonTreeBranchdepth=decisonTreeBranchdepth+1;
			}
			else{
				boolean repeatagain=true;
				int currentbranchindex=0;
				while(repeatagain==true){
					String  branchClass=nodeAdress.get(decisonTreeBranchdepth).get(0);
					int maxbranchnumber =featurenameandvalues.AttributeValues.get(branchClass).size();
					currentbranchindex=1+branchindex.get(nodeAdress.get(decisonTreeBranchdepth).get(0));
					if(currentbranchindex>maxbranchnumber-1){
						branchindex.put(nodeAdress.get(decisonTreeBranchdepth).get(0),0);
						nodeAdress.remove(decisonTreeBranchdepth);
						decisonTreeBranchdepth=decisonTreeBranchdepth-1;
						repeatagain=true;
						if(decisonTreeBranchdepth<0){
							cont=false;
							break outer;
						}
					}
					else{
						branchindex.put(nodeAdress.get(decisonTreeBranchdepth).get(0),currentbranchindex);
						repeatagain=false;
						}
				}//end of while loop
				String branchIndexInwords=featurenameandvalues.AttributeValues.get(nodeAdress.get(decisonTreeBranchdepth).get(0)).get(currentbranchindex);
				nodeAdress.get(decisonTreeBranchdepth).remove(1);				
				nodeAdress.get(decisonTreeBranchdepth).add(branchIndexInwords);
			}
			nodeAnalysis.countClassificationAtNode(nodeAdress);
			nodeAnalysis.calculateInformationGain();
			result=nodeAnalysis.findNextNode();
			if(result.isNodeOutputNode()==false){
				xmlCreator.newxmlnode(decisonTreeBranchdepth, nodeAdress.get(nodeAdress.size()-1).get(0), nodeAdress.get(nodeAdress.size()-1).get(1), result.getEntropyAtNode(), null);
			}
			else{
				xmlCreator.resultnode(decisonTreeBranchdepth, nodeAdress.get(nodeAdress.size()-1).get(0), nodeAdress.get(nodeAdress.size()-1).get(1),"0", result.getOutputNode());
			}
		}
		xmlCreator.printxml();
	}
}
