package util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {
	
	private static String placeholder = "\\{\\}";;
	
	public static String completeStr(String str, Object... parameters) {
	
		StringBuilder text = new StringBuilder(str);
		
		Matcher m = Pattern.compile(placeholder).matcher(text);
		int pointer = 0;
		int index = 0;
		while(m.find(pointer)) {
			pointer = m.end();
			if(index < parameters.length) {
				String parameter = parameters[index].toString();
				text.replace(m.start(), m.end(), parameter);
				pointer += parameter.length() - placeholder.length();
				index++;
			}
		}
		return text.toString();
	}
}
