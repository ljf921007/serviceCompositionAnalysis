package internshipRemain.search;


import internshipRemain.graph.Entry;
import internshipRemain.graph.GraphDataModel;

import java.util.ArrayList;
import java.util.List;

public class NamespaceParser {

	private static final String SEPARATOR = "/";

	public List<Entry> parse(String namespace) {
		List<Entry> result = new ArrayList<Entry>();

		if (graphDataModel == null)
			throw new IllegalArgumentException("graphDataModel is empty.");

		if (namespace == null || "".equals(namespace))
			return result;

		String[] parts = namespace.split(SEPARATOR);

		Entry current = null;
		int i = 0;
		for (String part : parts) {

			if ("".equals(part))
				continue;

			String type = graphDataModel.getVertexLabelByPosition(i + 1);

			if (type != null) {
				Entry e = new Entry(type, part);
				result.add(e);
				current = e;
			} else {
				if (current != null) {
					String origin = current.getValue();
					current.setValue(origin + "/" + part);
				}
			}

			i++;
		}
		return result;
	}

	private GraphDataModel graphDataModel;

	public void setGraphDataModel(GraphDataModel graphDataModel) {
		this.graphDataModel = graphDataModel;
	}

}

