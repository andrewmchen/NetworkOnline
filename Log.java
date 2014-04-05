import java.io.*;

public class Log {
	String filename;

	public Log(String filename) {
		this.filename = filename;
	}

	public void log(String text) {
		try {
			String time = DateFormatUtils.format(yourDate, "yyyy-MM-dd HH:mm:SS");
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename + ".txt", true)));
			out.println("[" + time + "] " + text);
			out.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}