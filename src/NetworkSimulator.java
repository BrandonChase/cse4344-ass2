import java.io.IOException;
import java.util.Scanner;

public class NetworkSimulator {
	public static final int INFINITY = 16;
	public static void main(String[] args) {
		int lapse = 0;
		boolean showLapse = false;
		
		try {
			String input = "";
			Scanner reader = new Scanner(System.in);
			
			System.out.print("Please enter network filename: ");
			input = reader.nextLine(); //change to input argument
			Network net = new Network(input);
			
			//main loop
			do {
				clearConsole();
				System.out.println(net);
				if(net.isStable()) {
					System.out.print("***STABLE STATE REACHED AT TIME " + net.time + "***!");
					if(showLapse) {
						System.out.println(" (" + lapse + " after stabilize started)");
						showLapse = false;
					}
				}
				System.out.print("Enter command ( [n]ext step / [s]tablize / [c]hange cost / [q]uit ): ");
				input = reader.nextLine();
				
				if(input.equals("n") && !net.isStable()) {
					net.tick();
				} else if(input.equals("s") && !net.isStable()) {
					int start = net.time;
					net.stabilize();
					int end = net.time;
					lapse = end - start;
					showLapse = true;
				} else if(input.equals("c")) {
					System.out.print("Enter change ([nodeID] [nodeID] [cost]): ");
					input = reader.nextLine();
					String data[] = input.split(" ");
					int nodeID1 = Integer.parseInt(data[0]);
					int nodeID2 = Integer.parseInt(data[1]);
					int cost = Integer.parseInt(data[2]);
					
					net.changeLink(nodeID1, nodeID2, cost);
				}
			} while(!input.equals("q"));
			
			reader.close();
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		
	}
	
	//Clears console so that GUI stays in one place and is easier for user to read
	private static void clearConsole() throws IOException {
		String ESC = "\033[";
		System.out.print(ESC + "2J");
	}
}
