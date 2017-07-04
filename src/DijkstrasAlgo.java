import Graphs.Edge;
import Graphs.Graph;
import Graphs.Vertex;
import Map.Map;
import Map.ProbeHashMap;
import PriorityQueue.Entry;
import PriorityQueue.HeapAdaptablePriorityQueue;

public class DijkstrasAlgo {
	
	/* Computes shortest-path distances from source vertex to all reachable vertices of g */
	public static <V> ProbeHashMap<Vertex<V>, Integer> shortestPathLengths(Graph<V, Integer> g, Vertex<V> src){
	
		// d.get(v) is upper bound on distance from src to v 
		Map<Vertex<V>, Integer> d = new ProbeHashMap<>();
		
		// map reachable v to its final d value
		ProbeHashMap<Vertex<V>, Integer> cloud = new ProbeHashMap();
		
		// pq will have vertices as values, with d.get(v) as key 
		HeapAdaptablePriorityQueue<Integer, Vertex<V>> pq = new HeapAdaptablePriorityQueue<>();
		
		// maps from vertex to its pq locator
		Map<Vertex<V>, Entry<Integer, Vertex<V>>> pqTokens = new ProbeHashMap<>();
		
		// for each vertex v of the graph, add an entry to the priority queue, with
		// the source having distance 0 and all others having infinite distance
		for (Vertex<V> v : g.vertices()){
			if(v == src){
				d.put(v, 0);
			} else {
				d.put(v, Integer.MAX_VALUE);
			}
			
			pqTokens.put(v, pq.insert(d.get(v), v));		// save entry for future updates
		}
		
		// now begin adding reachable vertices to the cloud 
		while(!pq.isEmpty()){
			Entry<Integer, Vertex<V>> entry = pq.removeMin();
			int key = entry.getKey();
			Vertex<V> u = entry.getValue();
			
			cloud.put(u, key);								// this is actual distance to u
			pqTokens.remove(u);								// u is no longer in pq
			
			for(Edge<Integer> e : g.outgoingEdges(u)){
				Vertex<V> v = g.opposite(u, e);
				if(cloud.get(v) == null){
					// perform relaxation step n edge (u,v) 
					int wgt = e.getElement();
					if (d.get(u) + wgt < d.get(v)){					// better path to v?
						d.put(v, d.get(u) + wgt);					// update the distance
						pq.replaceKey(pqTokens.get(v), d.get(v));	// update the pq entry
					}
				}
			}
		}
		
		return cloud; // only includes reachable vertices
	}
	
	/* Computes shortest-path distances from source vertex to a destination vertex */
	public static <V> int shortestPathLength(Graph<V, Integer> g, Vertex<V> src, Vertex<V> dest){
	
		// d.get(v) is upper bound on distance from src to v 
		Map<Vertex<V>, Integer> d = new ProbeHashMap<>();
		
		// map reachable v to its final d value
		ProbeHashMap<Vertex<V>, Integer> visited = new ProbeHashMap();
		
		// pq will have vertices as values, with d.get(v) as key 
		HeapAdaptablePriorityQueue<Integer, Vertex<V>> pq = new HeapAdaptablePriorityQueue<>();
		
		// maps from vertex to its pq locator
		Map<Vertex<V>, Entry<Integer, Vertex<V>>> pqTokens = new ProbeHashMap<>();
		
		// for each vertex v of the graph, add an entry to the priority queue, with
		// the source having distance 0 and all others having infinite distance
		for (Vertex<V> v : g.vertices()){
			if(v == src){
				d.put(v, 0);
			} else {
				d.put(v, Integer.MAX_VALUE);
			}
			
			pqTokens.put(v, pq.insert(d.get(v), v));		// save entry for future updates
		}
		
		// now begin adding reachable vertices to the cloud 
		while(!pq.isEmpty()){
			Entry<Integer, Vertex<V>> entry = pq.removeMin();
			if(entry != dest){
				int key = entry.getKey();
				Vertex<V> u = entry.getValue();
				
				visited.put(u, key);								// this is actual distance to u
				pqTokens.remove(u);								    // u is no longer in pq
				
				for(Edge<Integer> e : g.outgoingEdges(u)){
					Vertex<V> v = g.opposite(u, e);
					if(visited.get(v) == null){
						// perform relaxation step n edge (u,v) 
						int edgeWeight = e.getElement();
						if (d.get(u) + edgeWeight < d.get(v)){					// better path to v?
							d.put(v, d.get(u) + edgeWeight);					// update the distance
							pq.replaceKey(pqTokens.get(v), d.get(v));	     	// update the pq entry
						}
					}
				}
			} else{
				int key = entry.getKey();
				Vertex<V> u = entry.getValue();
				
				visited.put(u, key);
				break;
			}
		}
		
		return visited.get(dest); // returns shortest path to destination node
	}
	
	/* Reconstructs a shortest-path tree rooted as vertex s, given distance map d.
	 * The tree is represented as a map from each reachable vertex v (other than s)
	 * to the edge  e = (u,v) that is used to reach v from its parent u in the tree 
	 */
	
	public static <V> Map<Vertex<V>, Edge<Integer>> spTree(Graph<V, Integer> g, Vertex<V> s, Map<Vertex<V>, Integer> d){
		Map<Vertex<V>, Edge<Integer>> tree = new ProbeHashMap<>();
		for(Vertex<V> v : d.keySet()){
			if (v != s){
				for (Edge<Integer> e : g.incomingEdges(v)){		// consider incoming edges
					Vertex<V> u = g.opposite(v, e);
					int edgeWeight = e.getElement();
					if(d.get(v) == d.get(u) + edgeWeight){
						tree.put(v, e);						   // edge to reach v has been found
					}
				}
			}
		}
		return tree;
	}
}
