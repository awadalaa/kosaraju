kosaraju
========

Kosaraju's algorithm is a linear time algorithm to find the strongly connected components of a directed graph

The algorithm
-------------
Kosaraju's algorithm works as follows:
* Let G be a directed graph and S be an empty stack.
* While S does not contain all vertices:
** Choose an arbitrary vertex ''v'' not in S. Perform a depth first search starting at ''v''. Each time that depth-first search finishes expanding a vertex ''u'', push ''u'' onto S.
* Reverse the directions of all arcs to obtain the transpose graph.
* While S is nonempty:
** Pop the top vertex ''v'' from S. Perform a depth-first search starting at ''v'' in the transpose graph. The set of visited vertices will give the strongly connected component containing ''v''; record this and remove all these vertices from the graph G and the stack S. Equivalently, breadth-first search (BFS) can be used instead of depth-first search.

Performance
-----------
It should be noted that if input size of the graph is large, the recursive approach will cause StackOverflowException. So, iterative version is used here.
