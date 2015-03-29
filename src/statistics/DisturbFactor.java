package statistics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DisturbFactor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	//在历史数据中提取干扰因素
	public List<HashSet<String>> extractDisturbFactors() {
		//list中的每个元素相当于一个干扰因素，而hashset则是干扰因素影响的服务节点集合
		List<HashSet<String>> disFactors = new ArrayList<HashSet<String>>();
		
		return disFactors;
		
	}
	
	public void storeFactors(List<HashSet<String>> factors) {
		
	}
	
	public List<HashSet<String>> getFactorsFromStore(String filename) {
		List<HashSet<String>> disFactors = new ArrayList<HashSet<String>>();
		
		return disFactors;
	}
	
	//找到与现在的metric数据匹配的服务节点集合
	public Set<String> matchDisturbFactor(List<HashSet<String>> disFactors, List<Double> currentSimilarity) {
		if (disFactors.isEmpty() || disFactors == null)
			return null;
		Set<String> matchedSet = new HashSet<String>();
		
		return matchedSet;
	}
}
