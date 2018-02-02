package decisiontree;

import java.util.ArrayList;
import java.util.List;

public class NodeAnalysisInputs {
List<List<String>> nodeAdress;
List<List<String>> carsAtNodeAdress;
public NodeAnalysisInputs() {
	nodeAdress=new ArrayList<List<String>>();
	carsAtNodeAdress=new ArrayList<List<String>>();
}
public List<List<String>> getNodeAdress() {
	return nodeAdress;
}
public List<List<String>> getCarsAtNodeAdress() {
	return carsAtNodeAdress;
}

}
