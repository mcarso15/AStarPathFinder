//Matt Carson
import java.util.Scanner;
import java.util.ArrayList;
public class AStarPathFinder {

	Node[][] board;
	Scanner kb;
	
	//ArrayList<Node> path = new ArrayList<>();
	ArrayList<Node> open; //List of nodes to be visited
	ArrayList<Node> closed; //List of nodes already visited
	
	final int BOARD_ROWS = 15;
	final int BOARD_COLS = 15;
	
	final int MAX_BLOCK_PERCENT = 10;
	
	public static void main(String[] args) {
		AStar a = new AStar();
	}
	
	public AStar() {
		kb = new Scanner(System.in);
		board = new Node[BOARD_ROWS][BOARD_COLS];
		
		fillBoard(board, MAX_BLOCK_PERCENT);
		
		System.out.println("This is a " + BOARD_ROWS + "x" + BOARD_COLS + " board of nodes, where X represents blocked paths.");
		printBoard(board);
		
		String in = "";
		String[] coords;
		
		Node start;
		Node end;
		
		do {
			open = new ArrayList<>();
			closed = new ArrayList<>();
			
			System.out.println("Enter the starting node position as Row,Column with numbers from 1 - " + BOARD_ROWS);
			System.out.println("Enter the word Exit to exit the program.");
			in = kb.nextLine();
			
			if(in.compareToIgnoreCase("exit") != 0) {
				//Verifying inputs are valid
				while(in.matches("[a-zA-Z]+")) {
					System.out.println("Please only enter numbers from 1 - " + BOARD_ROWS);
					System.out.println("Enter the starting node position as Row,Column with numbers from 1 - " + BOARD_ROWS);
					in = kb.nextLine();
				}
				coords = in.split(",");
				int startX = Integer.parseInt(coords[0]) - 1;
				int startY = Integer.parseInt(coords[1]) - 1;
				
				//Make sure start is a valid starting point, as in is not a blocked node
				while(board[startX][startY].getType() == 1 || (startX < 0 || startX > BOARD_ROWS) || (startY < 0 || startY > BOARD_COLS)) {
					System.out.println("Invalid entry!");
					if(board[startX][startY].getType() == 1) 
						System.out.println("Cannot select a blocked ('X') node");
					else
						System.out.println("Must select a number within range of 1 - " + BOARD_ROWS);
					System.out.println("Enter the starting node position as Row,Column with numbers from 1 - " + BOARD_ROWS);
					
					in = kb.nextLine();
					//Verifying inputs are valid
					while(in.matches("[a-zA-Z]+")) {
						System.out.println("Please only enter numbers from 1 - " + BOARD_ROWS);
						System.out.println("Enter the starting node position as Row,Column with numbers from 1 - " + BOARD_ROWS);
						in = kb.nextLine();
					}
					coords = in.split(",");
					startX = Integer.parseInt(coords[0]) - 1;
					startY = Integer.parseInt(coords[1]) - 1;
					
				}
		
				start = board[startX][startY];
		
				start.setG(0);
		
				System.out.println("Enter the ending node position as row,column");
				in = kb.nextLine();
				
				//Verifying inputs are valid
				while(in.matches("[a-zA-Z]+")) {
					System.out.println("Please only enter numbers from 1 - " + BOARD_ROWS);
					System.out.println("Enter the ending node position as Row,Column with numbers from 1 - " + BOARD_ROWS);
					in = kb.nextLine();
				}
				
				
		
				coords = in.split(",");
				int endX = Integer.parseInt(coords[0]) - 1;
				int endY = Integer.parseInt(coords[1]) - 1;
				
				//Make sure end is a valid ending point, as in is not a blocked node
				while(board[endX][endY].getType() == 1 || (endX < 0 || endX > BOARD_ROWS) || (endY < 0 || endY > BOARD_COLS)) {
					System.out.println("Invalid entry!");
					if(board[endX][endY].getType() == 1) 
						System.out.println("Cannot select a blocked ('X') node");
					else
						System.out.println("Must select a number within range of 1 - " + BOARD_ROWS);
					System.out.println("Enter the ending node position as Row,Column with numbers from 1 - " + BOARD_ROWS);
					
					in = kb.nextLine();
					//Verifying inputs are valid
					while(in.matches("[a-zA-Z]+")) {
						System.out.println("Please only enter numbers from 1 - " + BOARD_ROWS);
						System.out.println("Enter the ending node position as Row,Column with numbers from 1 - " + BOARD_ROWS);
						in = kb.nextLine();
					}
					coords = in.split(",");
					endX = Integer.parseInt(coords[0]) - 1;
					endY = Integer.parseInt(coords[1]) - 1;
					
				}
				
				end = board[endX][endY];
		
				setHeuristic(board, end);
				pathFind(board,start,end,open,closed);
			}
		}while(in.compareToIgnoreCase("exit") != 0);
		
		System.out.println("Goodbye!");
		
		//Close inputs at the end
		kb.close();
	}
	
	void fillBoard(Node[][] b, int blocks) {
		int numBlocks = 1;
		int rand;
		
		/*
		 * This forces a more spread out distribution of the blocked nodes,
		 * setting more than a 1% chance a given node was blocked tended
		 * to have more blocked nodes appear at the top of the board, and
		 * few to none appear towards the end.
		 * 
		 * This loops to make sure 10% of total nodes are blocked
		 */
		while(numBlocks > 0) {
			numBlocks = (int)((blocks/100.0) * (b.length * b[0].length));
			for(int j = 0; j < BOARD_ROWS; j++) {
				for(int k = 0; k < BOARD_COLS; k++) {
					if(numBlocks > 0) {
						rand = (int)(Math.random() * 10);
						if(rand >= 9) {
							b[j][k] = new Node(j,k,1);
							numBlocks--;
						}
						else
							b[j][k] = new Node(j,k,0);
					}
					else
						b[j][k] = new Node(j,k,0);
				}
			}
		}
	}
	
	void printBoard(Node[][] b) {
		int type;
		for(int j = 0; j < BOARD_ROWS; j++) {
			for(int k = 0; k < BOARD_COLS; k++) {
				type = b[j][k].getType();
				if(type == 0) 
					System.out.print("0 ");
				else
					System.out.print("X ");
			}
			System.out.println();
		}
	}
	
	void setHeuristic(Node[][] b, Node destination) {
		for(int j = 0; j < b.length; j++) {
			for(int k = 0; k < b[0].length; k++) {
				b[j][k].setH((Math.abs(j - destination.getRow())) + (Math.abs(k - destination.getCol())));
			}
		}
	}
	
	void pathFind(Node[][] b, Node s, Node d, ArrayList<Node> o, ArrayList<Node> c) {
		o.add(s);
		boolean search = true;
		Node n;
		if(o.size() == 0) 
			System.out.println("No path was found!");
		else {
			while(search) {
				n = o.remove(0);
			
				if(n.equals(d)) {
					System.out.println("Goal reached!");
					c.add(d);
					System.out.println("Path: ");
				
					for(Node pNode : c) {
						System.out.print("[" + (pNode.getRow() + 1) + "," + (pNode.getCol() + 1) + "] ");
					}
					System.out.println("");
					System.out.println("");
					search = false;				
				}
				else {
					findNeighbors(b,n,o,c);
				}
			}
		}
	}
	
	void findNeighbors(Node[][] b, Node n, ArrayList<Node> openNodes, ArrayList<Node> closedNodes){
		int nRow = n.getRow();
		int nCol = n.getCol();
		int newG = 0;
		Node nOpen = null;
		
		Node newNode;
		for(int j = nRow - 1; j <= nRow + 1; j++) {
			for(int k = nCol - 1; k <= nCol + 1; k++) {
				if((j >= 0 && j < b.length) && (k >= 0 && k < b[0].length) && (j != nRow || k != nCol) && b[j][k].getType() != 1) {
					newNode = b[j][k];
					newNode.setParent(n);
					
					if(Math.abs(j - nRow) + Math.abs(k - nCol) == 2) 
						newG = 2;
					else
						newG = 1;
					
					newNode.setG(n.getG() + newG);
					newNode.setF();
					
					if(checkList(newNode, closedNodes) == null){
						nOpen = checkList(newNode, openNodes);
						if(nOpen == null)
							openNodes.add(newNode);
						else {
							if(newNode.getG() < nOpen.getG()) {
								nOpen.setG(newNode.getG());
								nOpen.setParent(n);
							}
						}
					}
				}
			}
		}
		sortF(openNodes);
		closedNodes.add(n);
	}
	
	Node checkList(Node n, ArrayList<Node> list) {
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).equals(n)) {
				return list.get(i);
			}
		}
		return null;
	}
	
	void sortF(ArrayList<Node> openNodes) {
		int lowestFPos;
		Node tmp;
		
		for(int i = 0; i < openNodes.size(); i++) {
			lowestFPos = i;
			for(int j = i; j < openNodes.size(); j++) {
				if(openNodes.get(j).getF() < openNodes.get(lowestFPos).getF()) {
					tmp = openNodes.get(j);
					openNodes.set(j, openNodes.get(lowestFPos));
					openNodes.set(lowestFPos, tmp);
				}
				/*
				 * Two neighbor nodes may have the same F value, but one with a lower H and
				 * higher G value than the other. This prefers the node with the lower H
				 * value in order to achieve the minimum amount of steps.
				 */
				else if(openNodes.get(j).getF() == openNodes.get(lowestFPos).getF()) {
					if(openNodes.get(j).getH() < openNodes.get(lowestFPos).getH()) {
						tmp = openNodes.get(j);
						openNodes.set(j, openNodes.get(lowestFPos));
						openNodes.set(lowestFPos, tmp);
					}
				}
			}
		}
	}
}
