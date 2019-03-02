import java.util.ArrayList;
import java.util.Random;

public class ProbabilityDistribution {
	private static Random random = new Random();
	double maxProbability = .985;
	
	double firstProbNum = 0, firstProbDenom = 0;
	
	double[][] mutualInformationNum;
	double[][] mutualInformationDenom;
	
	ArrayList<Node> nodes;
	ArrayList<Edge> edges;
	
	public ProbabilityDistribution(ArrayList<ByteString> byteStrings) {
		//Create graph.
		int numBytes = byteStrings.get(0).byteString.length;
		nodes = new ArrayList<Node>();
		for (int i = 0; i < numBytes; i++)
			for (int j = 0; j < 8; j++)
				nodes.add(new Node(i, j));
		
		mutualInformationNum = new double[numBytes * 8][numBytes * 8];
		mutualInformationDenom = new double[numBytes * 8][numBytes * 8];
		
		//Figure out the mutual information between every two nodes.
		for (ByteString byteString : byteStrings) {
			ArrayList<Boolean> bitString = byteString.getBitString();
			for (int i = 0; i < bitString.size(); i++) {
				if (bitString.get(0))
					firstProbNum++;
				firstProbDenom++;
				for (int j = i + 1; j < bitString.size(); j++) {
					mutualInformationNum[i][j] += (bitString.get(i) == bitString.get(j)) ? 1 : 0;
					mutualInformationDenom[i][j] += 1;
				}
			}
		}
		
		double[][] mutualInformation = new double[numBytes * 8][numBytes * 8];
		for (int i = 0; i < numBytes * 8; i++)
			for (int j = 0; j < numBytes * 8; j++)
				mutualInformation[i][j] = mutualInformationNum[i][j] / mutualInformationDenom[i][j];
		
		//Create max spanning tree.
		edges = new ArrayList<Edge>();
		ArrayList<Node> nodesInGraph = new ArrayList<Node>();
		ArrayList<Node> nodesNotInGraph = new ArrayList<Node>();
		for (Node node : nodes) {
			if (nodesInGraph.size() == 0)
				nodesInGraph.add(node);
			else
				nodesNotInGraph.add(node);
		}
		
		while (nodesNotInGraph.size() > 0) {
			double bestEdgeMutualInfo = 0;
			boolean bestEdgeInverse = false;
			Node bestEdgeStart = null;
			Node bestEdgeEnd = null;
			
			for (Node nodeIn : nodesInGraph) {
				for (Node nodeOut : nodesNotInGraph) {
					int listOffset1 = Math.min(nodeIn.listOffset(), nodeOut.listOffset());
					int listOffset2 = Math.max(nodeIn.listOffset(), nodeOut.listOffset());
					double mutualInfo = mutualInformation[listOffset1][listOffset2];
					boolean inverse = mutualInfo < .5;
					mutualInfo = Math.abs(mutualInfo - .5) + .5;
					
					if (mutualInfo > bestEdgeMutualInfo) {
						bestEdgeMutualInfo = mutualInfo;
						bestEdgeStart = nodeIn;
						bestEdgeEnd = nodeOut;
						bestEdgeInverse = inverse;
					}
				}
			}
			
			nodesInGraph.add(bestEdgeEnd);
			nodesNotInGraph.remove(bestEdgeEnd);
			edges.add(new Edge(bestEdgeStart, bestEdgeEnd, bestEdgeMutualInfo, bestEdgeInverse));
		}
	}
	
	public ArrayList<ByteString> sample(int samples) {
		ArrayList<ByteString> byteStrings = new ArrayList<ByteString>();
		
		for (int i = 0; i < samples; i++) {
			boolean[] bitString = new boolean[nodes.size()];
			
			bitString[nodes.get(0).listOffset()] = random.nextDouble() < Math.min(maxProbability, (firstProbNum / firstProbDenom));
			ArrayList<Node> exploredNodes = new ArrayList<Node>();
			ArrayList<Node> unexploredNodes = new ArrayList<Node>();
			for (Node node : nodes) {
				if (exploredNodes.size() == 0)
					exploredNodes.add(node);
				else
					unexploredNodes.add(node);
			}
			
			while (unexploredNodes.size() > 0) {
				for (Edge edge : edges) {
					boolean exploredContainsA = exploredNodes.contains(edge.a);
					boolean exploredContainsB = exploredNodes.contains(edge.b);
					boolean unexploredContainsA = unexploredNodes.contains(edge.a);
					boolean unexploredContainsB = unexploredNodes.contains(edge.b);
					
					if (exploredContainsA && unexploredContainsB) {
						exploredNodes.add(edge.b);
						unexploredNodes.remove(edge.b);
						bitString[edge.b.listOffset()] = randomSample(bitString[edge.a.listOffset()], 
								edge.probability, edge.inverse);
					} else if (exploredContainsB && unexploredContainsA) {
						exploredNodes.add(edge.a);
						unexploredNodes.remove(edge.a);
						bitString[edge.a.listOffset()] = randomSample(bitString[edge.b.listOffset()], 
								edge.probability, edge.inverse);
					}
				}
			}
			
			ByteString newByteString = new ByteString(bitString.length / 8);
			for (int j = 0; j < bitString.length / 8; j++) {
				boolean[] b = new boolean[8];
				for (int n = 0; n < 8; n++)
					b[n] = bitString[j * 8 + n];
					
				newByteString.setByte(j, b);
			}
			
			byteStrings.add(newByteString);
		}
		
		return byteStrings;
	}
	
	private boolean randomSample(boolean parent, double probability, boolean inverse) {
		probability = Math.min(maxProbability, probability);
		
		boolean sample;
		if (random.nextDouble() <= probability)
			sample = parent;
		else
			sample = !parent;
		
		if (inverse)
			sample = !sample;
		return sample;
	}
}

class Node {
	int byteOffset;
	int bitOffset;
	
	public Node(int byteOff, int bitOff) {
		byteOffset = byteOff;
		bitOffset = bitOff;
	}
	
	public int listOffset() { return byteOffset * 8 + bitOffset; }
}

class Edge {
	Node a, b;
	double probability;
	boolean inverse;
	
	public Edge(Node a, Node b, double probability, boolean inverse) {
		this.a = a;
		this.b = b;
		this.probability = probability;
		this.inverse = inverse;
	}
}


