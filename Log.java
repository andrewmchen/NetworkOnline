import java.io.*;
import java.util.*;
import java.text.*;

public class Log {
	String filename;

	public Log(String filename) {
		this.filename = filename;
	}

	public void log(String text) {
		try {
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date today = Calendar.getInstance().getTime();        
			String time = df.format(today);
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename + ".txt", true)));
			out.println("[" + time + "] " + text);
			out.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}