package statistics;

import model.Params;

public class PerPageRank {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	public double[] runPageRank(double[][] transformMatrix, double[] preference) {
		double[] pageRank = new double[preference.length];
		double[] prePageRank = new double[preference.length];
		for (int i = 0;i < preference.length;i++) {
			prePageRank[i] = 0.1;
		}
		double alpha = Params.alpha;
		double threshold = Params.threshold;
		while (checkPR(pageRank, prePageRank, threshold)) {
			for (int i = 0;i < preference.length;i++) { 
				prePageRank[i] = pageRank[i];
			}
			for (int i = 0;i < preference.length;i++) {
				double temp = 0;
				for (int j = 0;j < preference.length;j++) {
					temp = transformMatrix[i][j]*pageRank[j];
				}
				pageRank[i] = alpha*temp + (1-alpha)*preference[i];
			}
		}
		return pageRank;
	}
	
	private boolean checkPR(double[] PR, double[] prePR, double threshold) {
		boolean flag = false;
		for (int i = 0;i < PR.length;i++) {
			if (Math.abs(PR[i] - prePR[i]) > threshold) {
				flag = true;
				break;
			}
		}
		return flag;
	}
}
