

/**
 * Public Transit
 * Author: aurora sharif and Carolyn Yao
 * Does this compile? Y
 */

import java.util.Random;

/**
 * This class contains solutions to the Public, Public Transit problem in the
 * shortestTimeToTravelTo method. There is an existing implementation of a
 * shortest-paths algorithm. As it is, you can run this class and get the solutions
 * from the existing shortest-path algorithm.
 */
public class FastestRoutePublicTransit
{
  private int[][] generateRandomTable(int row, int col, int bound)
  {
    int[][] table = new int[row][col];
    Random r = new Random();
    for (int i = 0; i < table.length; i++)
    {
      for (int j = 0; j < table[i].length; j++)
      {
        table[i][j] = r.nextInt(bound);
      }
    }
    return table;
  }
  
  /**
   * The algorithm that could solve for shortest travel time from a station S
   * to a station T given various tables of information about each edge (u,v)
   *
   * @param S the s th vertex/station in the transit map, start From
   * @param T the t th vertex/station in the transit map, end at
   * @param startTime the start time in terms of number of minutes from 5:30am
   * @param lengths lengths[u][v] The time it takes for a train to get between two adjacent stations u and v
   * @param first first[u][v] The time of the first train that stops at u on its way to v, int in minutes from 5:30am
   * @param freq freq[u][v] How frequently is the train that stops at u on its way to v
   * @return shortest travel time between S and T
   */
  public int myShortestTravelTime(int S, int T, int startTime, int[][] lengths, int[][] first, int[][] freq)
  {
    // Your code along with comments here. Feel free to borrow code from any
    // of the existing method. You can also make new helper methods.
  
    //finds the shortest path from starting station to destination
    int[] arrPaths = Dijkstra(lengths, S, T);
    int index = first[0].length - 1;
    
    while(arrPaths[index] != S && index > 0)
    {
      index--;
    }
  
    //time to get to destination from the station
    int totalTime = 0;
    int timeNow = startTime;
    
    for(int i = index; i >= 1; i--)
    {
      int trainTime = upcomingStationsTrain(lengths, first, freq, arrPaths, i, timeNow);
      totalTime = totalTime + (trainTime - timeNow);
      timeNow = trainTime;
    }
    return totalTime;
  }
  
  //Dijkstra's shortest path
  public int[] Dijkstra(int [][] graph, int source, int destination)
  {
    //all shortest paths from source to destination
    int[] paths = new int[graph[0].length];
    
    //all shortest times
    int[] shortestTimes = new int[graph[0].length];
    
    //previous stations we passed
    int[] previousStation = new int[graph[0].length];
    previousStation[source] = -1;
    
    //boolean array to keep track if vertex is visited
    Boolean[] visited = new Boolean[graph[0].length];
    
    //set all vertex visited to false and MAX VALUE to the time
    for (int i = 0; i < graph[0].length; i++)
    {
      visited[i] = false;
      shortestTimes[i] = Integer.MAX_VALUE;
      paths[i] = -1;
    }
    
    //source vertex has no distance
    shortestTimes[source] = 0;
    
    findAndUpdateShortestPath(graph, shortestTimes, previousStation, visited);
    
    return backTrackToSource(paths, previousStation, destination);
  }
  
  //get the next train to get to v from station u
  public int upcomingStationsTrain(int[][] lengths, int[][] trainTimes, int[][] freq, int[] paths, int index, int currentTime)
  {
    if(index == 0)
    {
      return 0;
    }
    
    //current station
    int currentStation = paths[index];
    
    //next stop
    int nextStation = paths[index - 1];
    
    //time for current train
    int trainTimesNow = trainTimes[currentStation][nextStation];
    
    int j = 0;
    
    //calculate next time for train
    while (trainTimesNow < currentTime)
    {
      trainTimesNow = trainTimes[currentStation][nextStation] + ( j * freq[currentStation][nextStation]);
      j++;
    }
    
    return lengths[currentStation][nextStation] + trainTimesNow;
  }
  
  private boolean canBeVisited(int[][] graph, Boolean[] visited, int[] times, int from, int toVisit)
  {
    return (!visited[toVisit] && graph[from][toVisit] != 0
            && times[from] != Integer.MAX_VALUE && times[from]
            + graph[from][toVisit] < times[toVisit]);
  }
  
  private int[] backTrackToSource(int[] paths, int[] previousStation, int destination)
  {
    int stations = destination;
  
    //current station
    int index = 0;
  
    //from the previous array going back to source from dest and
    //since source was set to -1 if we find it then we are at the source
    while(previousStation[stations] != -1)
    {
      paths[index ] = stations;
      index++;
      stations = previousStation[stations];
    }
    
    paths[index] = stations;
    return paths;
  }
  
  private void findAndUpdateShortestPath(int[][] graph, int[] shortestTimes, int[] previous, Boolean[] visited)
  {
    //find shortest path to all the vertices
    for (int i = 0; i < graph[0].length - 1 ; i++)
    {
      //find a vertex that has not visited and set visited to true
      int from = findNextToProcess(shortestTimes, visited);
      visited[from] = true;
    
      for (int j = 0; j < graph[0].length; j++)
      {
        //if a vertex can be visited and has an edge and time is less than we have
        if (canBeVisited(graph, visited, shortestTimes, from, j))
        {
          shortestTimes[j] = shortestTimes[from] + graph[from][j];
          previous[j] = from;
        }
      }
    }
  }

  /**
   * Finds the vertex with the minimum time from the source that has not been
   * processed yet.
   * @param times The shortest times from the source
   * @param processed boolean array tells you which vertices have been fully processed
   * @return the index of the vertex that is next vertex to process
   */
  public int findNextToProcess(int[] times, Boolean[] processed) {
    int min = Integer.MAX_VALUE;
    int minIndex = -1;

    for (int i = 0; i < times.length; i++) {
      if (processed[i] == false && times[i] <= min) {
        min = times[i];
        minIndex = i;
      }
    }
    return minIndex;
  }

  public void printShortestTimes(int times[]) {
    System.out.println("Vertex Distances (time) from Source");
    for (int i = 0; i < times.length; i++)
        System.out.println(i + ": " + times[i] + " minutes");
  }

  /**
   * Given an adjacency matrix of a graph, implements
   * @param graph The connected, directed graph in an adjacency matrix where
   *              if graph[i][j] != 0 there is an edge with the weight graph[i][j]
   * @param source The starting vertex
   */
  public void shortestTime(int graph[][], int source) {
    int numVertices = graph[0].length;

    // This is the array where we'll store all the final shortest times
    int[] times = new int[numVertices];

    // processed[i] will true if vertex i's shortest time is already finalized
    Boolean[] processed = new Boolean[numVertices];

    // Initialize all distances as INFINITE and processed[] as false
    for (int v = 0; v < numVertices; v++) {
      times[v] = Integer.MAX_VALUE;
      processed[v] = false;
    }

    // Distance of source vertex from itself is always 0
    times[source] = 0;

    // Find shortest path to all the vertices
    for (int count = 0; count < numVertices - 1 ; count++) {
      // Pick the minimum distance vertex from the set of vertices not yet processed.
      // u is always equal to source in first iteration.
      // Mark u as processed.
      int u = findNextToProcess(times, processed);
      processed[u] = true;

      // Update time value of all the adjacent vertices of the picked vertex.
      for (int v = 0; v < numVertices; v++) {
        // Update time[v] only if is not processed yet, there is an edge from u to v,
        // and total weight of path from source to v through u is smaller than current value of time[v]
        if (!processed[v] && graph[u][v]!=0 && times[u] != Integer.MAX_VALUE && times[u]+graph[u][v] < times[v]) {
          times[v] = times[u] + graph[u][v];
        }
      }
    }

    printShortestTimes(times);
  }

  public static void main (String[] args) {
    /* length(e) */
    int lengthTimeGraph[][] = new int[][]{
      {0, 4, 0, 0, 0, 0, 0, 8, 0},
      {4, 0, 8, 0, 0, 0, 0, 11, 0},
      {0, 8, 0, 7, 0, 4, 0, 0, 2},
      {0, 0, 7, 0, 9, 14, 0, 0, 0},
      {0, 0, 0, 9, 0, 10, 0, 0, 0},
      {0, 0, 4, 14, 10, 0, 2, 0, 0},
      {0, 0, 0, 0, 0, 2, 0, 1, 6},
      {8, 11, 0, 0, 0, 0, 1, 0, 7},
      {0, 0, 2, 0, 0, 0, 6, 7, 0}
    };
    FastestRoutePublicTransit t = new FastestRoutePublicTransit();
    t.shortestTime(lengthTimeGraph, 0);

    // You can create a test case for your implemented method for extra credit below
    FastestRoutePublicTransit extra = new FastestRoutePublicTransit();
    
    int[][] length = extra.generateRandomTable(9, 9, 8);
    int[][] first = extra.generateRandomTable(9, 9, 20);
    int[][] freq = extra.generateRandomTable(9, 9, 10);
    
    System.out.println("From 1 to 5 total time is " +
            extra.myShortestTravelTime(1, 5, 5, length, first, freq) + " min");
    
  }
}
