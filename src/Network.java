import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeMap;
import java.util.TreeSet;

public class Network {
	public static final int INFINITY = 16;
	
	public TreeSet<Integer> nodeIDs;
	public TreeMap<Integer, Node> nodes;
	public int time;

	//Constructor
	public Network(String networkFilename) throws Exception {
		nodes = new TreeMap<Integer, Node>();
		time = 0;
		
		loadFromFile(networkFilename);
	}
	
	//Go through one iteration of sending / receiving distance vectors and recomputing routing tables
	public void tick() {
		for(int nodeID : nodes.keySet()) {
			Node currentNode = nodes.get(nodeID);
			if(currentNode.hasUnsentChanges) {
				//send message to neighbors
				for(int neighborID : currentNode.dvTable.keySet()) {
					if(neighborID != nodeID) {//dont send message to self
						nodes.get(neighborID).receiveMessage(nodeID, currentNode.dvTable.get(nodeID)); //send node's own row in table to others
					}
				}
				
				currentNode.hasUnsentChanges = false;
			}
		}
		
		for(int nodeID : nodes.keySet()) {
			if(nodes.get(nodeID).receivedNewMessage) {
				nodes.get(nodeID).computeTable();
			}
		}
		time++;
	}
	
	//Run tick until network is stable
	public void stabilize() {
		while(!isStable()) {
			tick();
		}
	}
	
	//Checks whether or not the network is stable (no changes to routing tables)
	public boolean isStable() {
		for(int nodeID : nodes.keySet()) {
			if(nodes.get(nodeID).hasUnsentChanges) {
				return false;
			}
		}
		
		return true; //return stable if all nodes do not have unsent changes
	}
	
	//Override link between two nodes
	public void changeLink(int nodeID1, int nodeID2, int cost) {
		nodes.get(nodeID1).setLink(nodeID2, cost);
		nodes.get(nodeID2).setLink(nodeID1, cost);
	}
	
	//Convert network to string representation
	public String toString() {
		String result = "";
		result += "*****TIME: " + time + "*****\n\n";
		result += "-----------------\n";
		for(int nodeID : nodes.keySet()) {
			result += nodes.get(nodeID).toString() + "\n";
			result += "-----------------\n";
		}
		
		return result;
	}

	//Parse data from the file and build up the network's data structures
	private void loadFromFile(String filename) throws Exception {
		try {
			nodeIDs = this.getIDsFromFile(filename);
			for(int id : nodeIDs) {
				nodes.put(id, new Node(id, nodeIDs));
			}
			
			//Open input file
			BufferedReader input = new BufferedReader(new FileReader(filename));
			
			//Read game data
			String line = null;
			while((line = input.readLine()) != null) { 
				String[] data = line.split(" ");
				int originID = Integer.parseInt(data[0]);
				int destinationID = Integer.parseInt(data[1]);
				int distance = Integer.parseInt(data[2]);
				
				nodes.get(originID).addNeighbor(destinationID, distance, nodeIDs);
				nodes.get(destinationID).addNeighbor(originID, distance, nodeIDs);
			}
			
			
			input.close();
		} catch(Exception e) {
			throw new Exception("Unable to load from file: " + filename);
		}
	}
	
	//Gets all ids of nodes from the file. Useful for implementation.
	private TreeSet<Integer> getIDsFromFile(String filename) throws Exception {
		TreeSet<Integer> ids = new TreeSet<Integer>();
		
		//Open input file
		BufferedReader input = new BufferedReader(new FileReader(filename));
				
		//Read game data
		String line = null;
		while((line = input.readLine()) != null) { 
			String[] data = line.split(" ");
			int originID = Integer.parseInt(data[0]);
			int destinationID = Integer.parseInt(data[1]);
			if(!ids.contains(originID)) {
				ids.add(originID);
			}
			
			if(!ids.contains(destinationID)) {
				ids.add(destinationID);
			}
		}
		
		input.close();
		
		return ids;
	}
}
