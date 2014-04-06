import java.io.*;
import java.util.*;
import java.text.*;

public class Log {
	String filename;

	public Log(String filename) {
		this.filename = filename;
	}

	public String log(String text) {
		try {
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date today = Calendar.getInstance().getTime();        
			String time = df.format(today);
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename + ".txt", true)));
			out.println("[" + time + "] " + text);
			out.close();
			return "[" + time + "] " + text;
		} catch (Exception e) {
			System.out.println(e);
			return "uh";
		}
	}

	public void logPrint(String text) {
		System.out.println(log(text));
	}

}