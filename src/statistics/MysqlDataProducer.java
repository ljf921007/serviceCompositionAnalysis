package statistics;

import dao.BaseDao;

public class MysqlDataProducer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BaseDao bd = new BaseDao();
		/*try {
			bd.createTable();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		try {
		//	bd.addItem("first", "thinking", "many values", "under considering");
			bd.addItems();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
