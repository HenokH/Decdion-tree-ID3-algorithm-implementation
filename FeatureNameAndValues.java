package decisiontree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureNameAndValues {
	public  final List<String> output= Arrays.asList("unacc", "acc", "good","vgood");
	public  final Map<String, String> Attributeclasses= new HashMap<String, String>();
	   {	Attributeclasses.put("0","buying");
		   	Attributeclasses.put("1","maint");
			Attributeclasses.put("2","doors");
			Attributeclasses.put("3","persons");
			Attributeclasses.put("4","lug_boot");
			Attributeclasses.put("5","safety");
	   }
	public  final Map<String, List<String>> AttributeValues = new HashMap<String, List<String>>();	    
	    {
	    	AttributeValues.put("buying", Arrays.asList("vhigh","high","med", "low"));
	    	AttributeValues.put("maint", Arrays.asList("vhigh","high","med", "low"));
	    	AttributeValues.put("doors", Arrays.asList("2","3","4","5more"));
	    	AttributeValues.put("persons", Arrays.asList("2","4","more"));
	    	AttributeValues.put("lug_boot", Arrays.asList("small","med","big"));
	    	AttributeValues.put("safety", Arrays.asList("low","med","high"));
	    }
}
