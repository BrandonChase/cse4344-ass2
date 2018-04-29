import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;

public class Node {
	public static final int INFINITY = 16;
	
	public int ID;
	public TreeMap<Integer, TreeMap<Integer, Integer>> dvTable; //distance vector table
	public TreeMap<Integer, Integer> routingTable;
	public boolean hasUnsentChanges;
	public boolean receivedNewMessage;
	
	//Constructor
	public Node(int ID, TreeSet<Integer> ids) {
		this.ID = ID;
		
		dvTable = new TreeMap<Integer, TreeMap<Integer, Integer>>();
		routingTable = new TreeMap<Integer, Integer>();
		//add this nodes row into dvtable
		dvTable.put(ID, new TreeMap<Integer, Integer>());
		//add all the "to" columns for this row
		for(int id : ids) {
			if(id == this.ID) {
				dvTable.get(this.ID).put(id, 0);
				routingTable.put(this.ID, this.ID);
			} else {
				dvTable.get(this.ID).put(id, INFINITY);
				routingTable.put(id, INFINITY);
			}
		}
		
		hasUnsentChanges = true;
		receivedNewMessage = false;
	}
	
	//Runs the Bellman-Ford algorithm on the routing table
	public void computeTable() {
		for(int destinationID : dvTable.get(this.ID).keySet()) {
			TreeMap<Integer, Integer> neighborDistances = new TreeMap<Integer, Integer>();
			for(int neighborID : dvTable.keySet()) {
				int thisToNeighbor = dvTable.get(this.ID).get(neighborID);
				int neighborToDestination = dvTable.get(neighborID).get(destinationID);
				neighborDistances.put(thisToNeighbor + neighborToDestination, neighborID);
			}
			
			int leastCost = Collections.min(neighborDistances.keySet());
			int oldLeastCost = dvTable.get(this.ID).get(destinationID);
			if(leastCost < oldLeastCost) {
				hasUnsentChanges = true;
				dvTable.get(this.ID).put(destinationID, leastCost);
			}
		}
		
		receivedNewMessage = false; //node has handled new message
	}
	
	//Replace row in routing table with received row from neighbor
	@SuppressWarnings("unchecked")
	public void receiveMessage(int senderID, TreeMap<Integer, Integer> tableEntry) {
		receivedNewMessage = true;
		
		dvTable.put(senderID, (TreeMap<Integer, Integer>) tableEntry.clone()); //override the entire row of the sender node
	}
	
	//Convert routing table into string representation
	public String toString() {
		String result = "";
		//header
		result += "Node: " + this.ID + ":\n\n";
		//destination IDs line
		result += " ";
		for(int destinationID : dvTable.get(this.ID).keySet()) {
			result += String.format("%3d", destinationID);
		}
		result += "\n";
		
		//Print each row
		for(int originID : dvTable.keySet()) {
			result += originID + "|";
			for(int destinationID : dvTable.get(originID).keySet()) {
				result += String.format("%2d|", dvTable.get(originID).get(destinationID));
			}
			result += "\n";
		}
		
		return result;
	}
	
	//Add neighbor as a row into this node's routing table and initialize values
	public void addNeighbor(int neighborID, int distance, TreeSet<Integer> ids) {
		//add new row for neighbor in the dvTable
		dvTable.put(neighborID, new TreeMap<Integer, Integer>());
		//add all the "to" columns for the neighbor row
		for(int id : ids) {
			if(id == neighborID) {
				dvTable.get(neighborID).put(id, 0);
			} else {
				dvTable.get(neighborID).put(id, INFINITY);
			}
		}
		
		//set distance to the neighbor for this node's entry in the dvTable
		dvTable.get(this.ID).put(neighborID, distance);
		//set distance to this node from neighbor in neighbors entry in table
		//dvTable.get(neighborID).put(this.ID, distance);
	}
	
	//Override link between two specified nodes with given cost
	public void setLink(int neighborID, int cost) {
		dvTable.get(this.ID).put(neighborID, cost);
		//dvTable.get(neighborID).put(this.ID, cost);
		hasUnsentChanges = true;
	}
}
