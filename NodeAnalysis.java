package decisiontree;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeAnalysis{
	private CarData carDataa;
	private List<List<String>> nodeAdress;//adresses a node by specifying the feature at higher node and the correspong feature value, eg. safety:low, maint:high......
	private FeatureNameAndValues featurenameandvalues;
	private Map<String,List<Float>> classificationCountAtNode;//classification outcome count at a node
						//(assumes all the features already assigned on higher level i.e all feature classes not included on the node adress) could be assigned to a node
	private Map<String,List<List<Float>>> classificationCountAtbranchesOfNode;//distribution of outcome according the  values a feature 
	private Map<String,Double> entropyAtNode;//  entropy calculated at a node(using formal information entropy formula) using the counts from method countClassificationAtNode() 
	private Map<String,Double> averageEntropyAtBranchesOfNode;//entropy value averaged across branches of a node
	private Map<String,Double> informationGainAtNode;// entropy gain from entropyAtNode to averageEntropyAtBranchesOfNode 

	public NodeAnalysis(CarData carDataa,List<List<String>> nodeAdress ){
		this.carDataa=carDataa;
		this.nodeAdress=new ArrayList<List<String>>();
		this.nodeAdress=nodeAdress;
		featurenameandvalues=new FeatureNameAndValues();
	}

/*counts classification output at a node and its possible values
 *takes as a parameter an address of a node which is a heirarchical adress specifying the features and values at higher nodes of a tree
 *stores counts on two hashmaps classificationCountAtNode and classificationCountAtNode with keys  possible features that could be assigned to a node 
*/
	public void countClassificationAtNode(List<List<String>> adress){// 
		nodeAdress=adress;
		classificationCountAtbranchesOfNode = new HashMap<>();//attribute class to output count
		classificationCountAtNode = new HashMap<>();//attribute class to output count
		int previousValidLine=0;
		int currentRowLine=0;

		do{
			CountableCar countablecar=new CountableCar();
			//check if the car data line satisfies the criteria for counting(node adress)
			checkIfCarShouldbeCounted(nodeAdress,carDataa.getInstanceData().get(currentRowLine),countablecar);
			//if the data line satisfies adress conditon then we could therefore jump straight to this data line when in the next round if we are asked with this condition
			previousValidLine=storeNextDataLineSatisfyingAdress(previousValidLine,  currentRowLine, nodeAdress,  countablecar);
			/*if(nodeAdress.size()>=5){System.out.println(nodeAdress);}*/
			if(countablecar.isToBeCounted()==true){	
				//Counting begins here with bz checking whether data row is valid(satisfies limiting criteria or not)
				for (int j=0;j<carDataa.getNumberOfColumns()-1;j++){    ///list of attributes classes 
					String featureName=featurenameandvalues.Attributeclasses.get(String.valueOf(j));
					if(!countablecar.getColumnsNottobeconsiderd().contains((Integer)j)||nodeAdress.size()==6){
						
						int featureBranchNumbers =featurenameandvalues.AttributeValues.get(featureName).size();
						List<Float> outputcountTemp=new ArrayList<Float>();//decision values count at a node with 4 tzpes of outcome values (4 values: unacc, acc, good, vgood)
						List<List<Float>> outputcountTempChildren = new ArrayList<List<Float>>();//decision values count at at each branch of a node that is possible values of a node
						if(classificationCountAtNode.get(featureName)!=null){//could go above after decleration and not necessarz
							outputcountTemp= classificationCountAtNode.get(featurenameandvalues.Attributeclasses.get(String.valueOf(j)));///////////aaaaa1111111111111111111111111111111111
							outputcountTempChildren = classificationCountAtbranchesOfNode.get(featurenameandvalues.Attributeclasses.get(String.valueOf(j)));
						}
						else{
							outputcountTemp= Arrays.asList((float)0,(float)0,(float)0,(float)0);//////////////////////////////////////
							outputcountTempChildren = new ArrayList<List<Float>>();
							for(int s=0;s<featureBranchNumbers;s++){
								outputcountTempChildren.add(Arrays.asList((float)0,(float)0,(float)0,(float)0));
							}
						}
						for(int k=0;k<4;k++){
							if(carDataa.getInstanceData().get(currentRowLine).get(6).equals(featurenameandvalues.output.get(k))){/// check if this is the same for general case
								float tempcount=outputcountTemp.get(k);
								tempcount=tempcount+1;
								outputcountTemp.set(k,tempcount);
								for(int l=0;l<featureBranchNumbers;l++){//l is counting the branches of the node or attributes
									String columnvalue =featurenameandvalues.AttributeValues.get(featureName).get(l);//atrtibute value corresponding to l
									if(carDataa.getInstanceData().get(currentRowLine).get(j).equals(columnvalue)){//checks the value of each attribute value 
										List<Float> outputcountTempbranches= outputcountTempChildren.get(l);// what is my branch value
										float tempcountbranches=outputcountTempbranches.get(k);
										tempcountbranches=tempcountbranches+1;
										outputcountTempbranches.set(k,tempcountbranches);							
									}
								}//end of l loop over feature branches
							}//end of if(instanceData.get(currentRowLine).get(j).equals(columnvalue)){//checks the value of each attribute value 
						}
						if(nodeAdress.size()<6){
							
							classificationCountAtNode.put(featurenameandvalues.Attributeclasses.get(String.valueOf(j)),outputcountTemp);
							classificationCountAtbranchesOfNode.put(featurenameandvalues.Attributeclasses.get(String.valueOf(j)),outputcountTempChildren);
						}
						else{
							classificationCountAtNode.put(nodeAdress.get(5).get(0),outputcountTemp);
							classificationCountAtbranchesOfNode.put(nodeAdress.get(5).get(0),outputcountTempChildren);
						}
					}
				}//end of a loop over car data columnscolumns 
			}//end of valid row line if 
		}
		/*find next line to check using the stored data line adress from previous adress requiremtn
		*PreviousAdress.size()=CurrentAdress.size()-1
		*/
		while ((currentRowLine=findNexttobecountedline(currentRowLine,nodeAdress))<carDataa.getNumberOfRow());
		/*if(nodeAdress.size()>=5){System.out.println(classificationCountAtNode);
		System.out.println(classificationCountAtbranchesOfNode);}*/

	}
	
	
	public int findNexttobecountedline(int currentRowLine,List<List<String>> adress){
		if(nodeAdress.size()<=1||currentRowLine==(carDataa.getNumberOfRow()-1)){currentRowLine++;}
		else{
			currentRowLine=Integer.valueOf(carDataa.getInstanceData().get(currentRowLine).get(5+nodeAdress.size()));
		}
		return currentRowLine;
	}
	
	public int storeNextDataLineSatisfyingAdress(int previousValidLine,int currentRowLine, List<List<String>> adress, CountableCar countablecar){
		if(((countablecar.isToBeCounted()==true&&currentRowLine!=0)||currentRowLine==(carDataa.getNumberOfRow()-1))&&(nodeAdress.size()>0)){
			carDataa.getInstanceData().get(previousValidLine).add((6+nodeAdress.size()),String.valueOf(currentRowLine));
			previousValidLine=currentRowLine;
			return previousValidLine;
		}
		else {return previousValidLine;}
	}
	/*checks if data line should be counted to select the next node
	 * Uses the adress of the node to decide
	 * returns a boolean stamp(true if line is going to be counted
	 * returns as well a list of columns that have alreadz been included on the node tree which couldnt
	 * couldnt be assigned to the next node
	 */
	public CountableCar checkIfCarShouldbeCounted(List<List<String>> adress, List<String> currentRow,CountableCar countablecar){
		if(!adress.isEmpty()){
			int teststamp=0;
			for (int b=0;b<adress.size();b++){
				boolean test =false;
				for (int c=0;c<carDataa.getNumberOfColumns()-1;c++){
					List<String> tobecheckeditems= new ArrayList<String>();
					tobecheckeditems.add(featurenameandvalues.Attributeclasses.get(String.valueOf(c)));
					tobecheckeditems.add(currentRow.get(c));
					if(adress.get(b).contains(tobecheckeditems.get(0))&&adress.get(b).contains(tobecheckeditems.get(1))){
						test =true;
						countablecar.getColumnsNottobeconsiderd().add(c);// havent used this yet but could be good it have all the values of columns that are already on the upper node
						teststamp=teststamp+1;
					}//end of if chcecking if line contains the necessary restriction in criteria
				}
				if((test==true)&&(b==adress.size()-1)&&(teststamp==adress.size())){
					countablecar.setToBeCounted(true);
				}
			}
		}
		else{countablecar.setToBeCounted(true);}
		//end of dataline validation
		/*if(nodeAdress.size()>=5){
			System.out.println(countablecar.isToBeCounted()+" > "+countablecar.getColumnsNottobeconsiderd());
		}*/
		return countablecar;
		
	}
	/*
	 * calculates the information gain 
	 * uses the coun result of the classificationcountatnode method 
	 * which counts possible distribution of outcomes over all possible node features which havent already been on the tree
	 * saves the result on a hashmap information gain with feature name as key
	 */
	public void calculateInformationGain(){
		entropyAtNode = new HashMap<>();// a hashmap to store entropy calculated using the counts from formula countClassificationAtNode() 
		averageEntropyAtBranchesOfNode = new HashMap<>();//entropz value averaged across branches
		informationGainAtNode=new HashMap<>();// differnce between parent and child entropz gain of using a particular attribute at a node
		float totalCarCountAtNode=0;
		
		for(String featureName: classificationCountAtNode.keySet()){
			Double entropyNode=(double) 0;//???
			Double DentropyNode=(double) 0;//????
			String nodename=featureName;
			float sum=0; for(int j=0;j<4;j++){sum=sum+classificationCountAtNode.get(nodename).get(j);}//total instances at the top node level to calculate the parent entropy
			totalCarCountAtNode=sum; //Total number of cars(Summation of all the cars counted at a node using countClassificationAtNode()
			for(int j=0;j<4;j++){
				float tempvalue=classificationCountAtNode.get(nodename).get(j);
				if(sum!=0){
					if(tempvalue!=0){
						entropyNode=entropyNode-((tempvalue/sum)*(Math.log(tempvalue/sum)/Math.log(4)));
					}else {entropyNode=entropyNode-0;}
				}else{entropyNode=(double)0;}
				DentropyNode = entropyNode;
			}//calculates  entropy over 4 classification classes
			entropyAtNode.put(nodename,DentropyNode);
		}//save result for each eligible feature(attribute)  at a node
					
		//average branch entropy assuming the node is assigned one of the eligible feature(attribute)  at a node
		for(String nodename: classificationCountAtNode.keySet()){//loop for each column(attributeclass)
			Double entropyNode=(double) 0;
			Double DentropyNode=(double) 0;
			float Totalinstancesoverbranch =0;
			double AveragechildEntropy=0;
			List<Double> BranchEntropyValue=new ArrayList<Double>();
			List<List<Float>> OutputproportionAtBranch=new ArrayList<List<Float>>();
			OutputproportionAtBranch=classificationCountAtbranchesOfNode.get(nodename);//each attribute class output proportion then devided by branch(attribute value)
			for(int k=0;k<OutputproportionAtBranch.size();k++){//OutputproportionAtBranch.size()>>is equal to number of branchs from a node (attribute vaues)
				float sum=0; 
				for(int a=0;a<4;a++){sum=sum+OutputproportionAtBranch.get(k).get(a);}// sum of one (out put class unacc,acc,good vgood
				Totalinstancesoverbranch=sum;		
				entropyNode=(double) 0;
				for(int j=0;j<4;j++){
					float tempvalue=OutputproportionAtBranch.get(k).get(j);
					if(sum!=0){
						if(tempvalue!=0){entropyNode=entropyNode-(tempvalue/sum)*(Math.log(tempvalue/sum)/Math.log(4));
						}
						else{entropyNode=entropyNode+0;}
					}
					else{entropyNode=(double)0;}
					DentropyNode = entropyNode;
				}
				BranchEntropyValue.add(k,DentropyNode);	//BEFORE AVERAGINGI
				if(totalCarCountAtNode!= 0){
					AveragechildEntropy=AveragechildEntropy+(entropyNode*Totalinstancesoverbranch/totalCarCountAtNode);
				}
				else{AveragechildEntropy=0;}
			}//end of k
			averageEntropyAtBranchesOfNode.put(nodename, AveragechildEntropy);
			double gain=entropyAtNode.get(nodename)-averageEntropyAtBranchesOfNode.get(nodename);
			informationGainAtNode.put(nodename,gain);
		}//end of i LOOP OVER COLUMNS 6
	}
	
	/*
	 * selects the feature which gives the highest information gain at a node
	 * uses the calculation of the informationGainAtNodemethod
	 */
	public NodeAnalysisResult findNextNode(){	
		String Nextnode=null;

		String maxInformationgainColumname="";
		double maxInformationgain=0;
		for(String temp : informationGainAtNode.keySet()){
			if(informationGainAtNode.get(temp)>maxInformationgain){
				maxInformationgain=informationGainAtNode.get(temp);
				maxInformationgainColumname =temp;
			}
		}
		Nextnode = maxInformationgainColumname;
		double HighestEntropygain=maxInformationgain;
		 NodeAnalysisResult nodeAnalysisResult=new NodeAnalysisResult();	
		if(HighestEntropygain==0){
			nodeAnalysisResult.setNodeOutputNode(true);;
			String Outcomeclass="nothing";
			outerloop:
					for(String outcomeSearch:classificationCountAtNode.keySet()){
						for(int nonZeroCountOutcome=0; nonZeroCountOutcome<4;nonZeroCountOutcome++){
							if((classificationCountAtNode.get(outcomeSearch).get(nonZeroCountOutcome)>0)){
								Outcomeclass=featurenameandvalues.output.get(nonZeroCountOutcome);
								break outerloop;
							}
						}
					}
			nodeAnalysisResult.setOutputNode(Outcomeclass);
		}
		else{
			nodeAnalysisResult.setNodeOutputNode(false);
			nodeAnalysisResult.setAttributeAtNode(Nextnode);
			DecimalFormat df = new DecimalFormat("#.###");
						
			nodeAnalysisResult.setEntropyAtNode(df.format(entropyAtNode.get(Nextnode)));;

			
		}
		return(nodeAnalysisResult);
	}
}

class CountableCar{
	private boolean  toBeCounted;
	private List<Integer> columnsNottobeconsiderd; 
	CountableCar(){
		toBeCounted=false;
		columnsNottobeconsiderd = new ArrayList<>();// dont know yet what to do with this
		
	}
	public boolean isToBeCounted() {
		return toBeCounted;
	}
	
	public void setToBeCounted(boolean toBeCounted) {
		this.toBeCounted = toBeCounted;
	}
	public List<Integer> getColumnsNottobeconsiderd() {
		return columnsNottobeconsiderd;
	}
	
}
