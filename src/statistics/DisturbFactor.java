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
	//����ʷ��������ȡ��������
	public List<HashSet<String>> extractDisturbFactors() {
		//list�е�ÿ��Ԫ���൱��һ���������أ���hashset���Ǹ�������Ӱ��ķ���ڵ㼯��
		List<HashSet<String>> disFactors = new ArrayList<HashSet<String>>();
		
		return disFactors;
		
	}
	
	public void storeFactors(List<HashSet<String>> factors) {
		
	}
	
	public List<HashSet<String>> getFactorsFromStore(String filename) {
		List<HashSet<String>> disFactors = new ArrayList<HashSet<String>>();
		
		return disFactors;
	}
	
	//�ҵ������ڵ�metric����ƥ��ķ���ڵ㼯��
	public Set<String> matchDisturbFactor(List<HashSet<String>> disFactors, List<Double> currentSimilarity) {
		if (disFactors.isEmpty() || disFactors == null)
			return null;
		Set<String> matchedSet = new HashSet<String>();
		
		return matchedSet;
	}
}
