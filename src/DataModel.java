import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class DataModel {
	
	public static int getDepth() throws FileNotFoundException{
		String[] temp;
		Scanner in = new Scanner(new File("data"));
		temp = in.nextLine().split(",");
		in.close();
		return Integer.parseInt(temp[0]);
	}
	
	public static int getNumOfGames() throws FileNotFoundException{
		String[] temp;
		Scanner in = new Scanner(new File("data"));
		temp = in.nextLine().split(",");
		in.close();
		return Integer.parseInt(temp[1]);
	}
	
	public static int getMaxCell() throws FileNotFoundException{
		String[] temp;
		Scanner in = new Scanner(new File("data"));
		temp = in.nextLine().split(",");
		in.close();
		return Integer.parseInt(temp[2]);
	}
	
	public static void setDepth(int d) throws FileNotFoundException{
		if(d > 0){
			int tempG = getNumOfGames();
			int tempC = getMaxCell();
			
			PrintWriter out = new PrintWriter(new File("data"));
			out.print(d + "," + tempG + "," + tempC);
			out.close();
		}
	}
	
	public static void setNumOfGames(int g) throws FileNotFoundException{
		if(g > 0){
			int tempD = getDepth();
			int tempC = getMaxCell();
			
			PrintWriter out = new PrintWriter(new File("data"));
			out.print(tempD + "," + g + "," + tempC);
			out.close();
		}
	}
	
	public static void setMaxCell(int c) throws FileNotFoundException{
		if(c > 0){
			int tempD = getDepth();
			int tempG = getNumOfGames();
			
			PrintWriter out = new PrintWriter(new File("data"));
			out.print(tempD + "," + tempG + "," + c);
			out.close();
		}
	}
}