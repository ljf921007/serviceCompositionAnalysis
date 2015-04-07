package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ServiceHandler {
	public String[] outputService(double[] pr, int topN) {
		if (pr.length <=0 || topN <= 0 || pr.length < topN)
			return null;
		String[] result = new String[topN];
		//double[] sortedArray = new double[pr.length];
		List<Double> sortedArray = new ArrayList<Double>();
		for (int i = 0;i < pr.length;i++) {
			sortedArray.add(pr[i]);
		}
		Collections.sort(sortedArray);
		for (int i = 0;i < topN;i++) {
			for (int j = 0;j < pr.length;j++) {
				if (pr[j] == sortedArray.get(i)) {
					result[i] = "·þÎñ" + i;
					break;
				}
			}
		}
		return result;
		
	}
}
