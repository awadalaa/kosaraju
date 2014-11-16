package com.technalaa.kosaraju;


import java.util.*;
 
public abstract class Graph<V extends AbstractVertex<E>, E extends AbstractEdge<V>> {
 
    private final TreeMap<Integer, V> vertices = new TreeMap<Integer, V>(
            new Comparator<Integer>() {
                //for pretty printing
                @Override
                public int compare( Integer arg0, Integer arg1 ) {
                    return arg0.compareTo( arg1 );
                }
            } );
 
    //need list for random access
    private final List<E> edges = new ArrayList<E>();
    
    private VertexFactory<V, E> f;
    
    public Graph( VertexFactory<V, E> f ) {
        if( f == null )
            throw new IllegalArgumentException( "Vertex factory needs to be specified" );
        this.f = f;
    }
    
    public void addVertex( V v ) {
        vertices.put( v.getLbl(), v );
    }
 
    public void addEdge( E e ) {
        edges.add( e );
    }
    
    public V getVertex( int lbl ) {
        V v;
        if ( ( v = vertices.get( lbl ) ) == null ) {
            v = f.newInstance( lbl );
            addVertex( v );
        }
        return v;
    }
    
    /**
     * @return the vertices
     */
    public Map<Integer, V> getVertices() {
        return vertices;
    }
 
    public Map<Integer, V> getVerticesInReversedOrder() {
        return vertices.descendingMap();
    }
 
    /**
     * @return the edges
     */
    public List<E> getEdges() {
        return edges;
    }
 
    public void reset() {
        for ( V v : vertices.values() ) {
            v.reset();
        }
    }
    
}
 
class UndirectedGraph extends Graph<Vertex, Edge> {
 
    public UndirectedGraph() {
        super( Vertex.getFactory() );
    }
}
 
class DirectedGraph extends Graph<DirectedVertex, DirectedEdge> {
 
    public interface EdgeTraversalPolicy {
        public Set<DirectedEdge> edges( DirectedVertex v );
        
        public DirectedVertex vertex( DirectedEdge e );
    }
    
    public final static EdgeTraversalPolicy FORWARD_TRAVERSAL = new EdgeTraversalPolicy() {
 
        @Override
        public Set<DirectedEdge> edges( DirectedVertex v ) {
            return v.getOutgoingEdges();
        }
 
        @Override
        public DirectedVertex vertex( DirectedEdge e ) {
            return e.getHead();
        }
    };
    
    public final static EdgeTraversalPolicy BACKWARD_TRAVERSAL = new EdgeTraversalPolicy() {
 
        @Override
        public Set<DirectedEdge> edges( DirectedVertex v ) {
            return v.getIncomingEdges();
        }
 
        @Override
        public DirectedVertex vertex( DirectedEdge e ) {
            return e.getTail();
        }
    };
    
    public DirectedGraph() {
        super( DirectedVertex.getFactory() );
    }
}
 
interface VertexFactory<V extends AbstractVertex<E>, E extends AbstractEdge<V>> {
    public V newInstance( int _lbl );
}
 
class AbstractVertex<E extends AbstractEdge<? extends AbstractVertex<?>>> {
 
    private final int lbl;
    private final Set<E> edges = new HashSet<E>();
 
    public AbstractVertex( int lbl ) {
        this.lbl = lbl;
    }
 
    public void addEdge( E edge ) {
        edges.add( edge );
    }
 
    public E getEdgeTo( AbstractVertex<E> v2 ) {
        for ( E edge : edges ) {
            if ( edge.contains( this, v2 ) )
                return edge;
        }
        return null;
    }
 
    /**
     * @return the lbl
     */
    public int getLbl() {
        return lbl;
    }
 
    /**
     * @return the edges
     */
    public Set<E> getEdges() {
        return edges;
    }
    
    public void reset() {}
    
    @Override
    public String toString() {
        return Integer.toString( getLbl() );
    }
}
 
class Vertex extends AbstractVertex<Edge> {
 
    private final static VertexFactory<Vertex, Edge> factory = new VertexFactory<Vertex, Edge>() {
 
        @Override
        public Vertex newInstance( int _lbl ) {
            return new Vertex( _lbl );
        }
    };
    
    public Vertex( int lbl ) {
        super( lbl );
    }
    
    public static VertexFactory<Vertex, Edge> getFactory() {
        return factory;
    }
 
}
 
class Edge extends AbstractEdge<Vertex> {
 
    public Edge( Vertex fst, Vertex snd ) {
        super( fst, snd );
    }
    
}
 
abstract class AbstractEdge<V extends AbstractVertex<? extends AbstractEdge<?>>> {
 
    private final List<V> ends = new ArrayList<V>();
 
    public AbstractEdge( V fst, V snd ) {
        if ( fst == null || snd == null ) {
            throw new IllegalArgumentException(
                    "Both vertices are required" );
        }
        ends.add( fst );
        ends.add( snd );
    }
 
    public boolean contains( AbstractVertex<? extends AbstractEdge<?>> v1, AbstractVertex<? extends AbstractEdge<?>> v2 ) {
        return ends.contains( v1 ) && ends.contains( v2 );
    }
 
    public V getOppositeVertex( V v ) {
        if ( !ends.contains( v ) ) {
            throw new IllegalArgumentException( "Vertex " + v.getLbl() );
        }
        return ends.get( 1 - ends.indexOf( v ) );
    }
 
    public void replaceVertex( V oldV, V newV ) {
        if ( !ends.contains( oldV ) ) {
            throw new IllegalArgumentException( "Vertex " + oldV.getLbl() );
        }
        ends.remove( oldV );
        ends.add( newV );
    }
    
    public V getFirst() {
        return ends.get( 0 );
    }
    
    public V getSecond() {
        return ends.get( 1 );
    }
}
 
class DirectedVertex extends AbstractVertex<DirectedEdge> {
    
    private final static VertexFactory<DirectedVertex, DirectedEdge> factory = new VertexFactory<DirectedVertex, DirectedEdge>() {
 
        @Override
        public DirectedVertex newInstance( int _lbl ) {
            return new DirectedVertex( _lbl );
        }
    };
    
    private final Set<DirectedEdge> incomingEdges = new HashSet<DirectedEdge>();
    private boolean visited;
    private int f;
    
    public DirectedVertex( int lbl ) {
        super( lbl );
    }
    
    public static VertexFactory<DirectedVertex, DirectedEdge> getFactory() {
        return factory;
    }
    
    public void addIncomingEdge( DirectedEdge e ) {
        incomingEdges.add( e );
    }
    
    public void addOutgoingEdge( DirectedEdge e ) {
        super.addEdge( e );
    }
    
    //this vertex is head
    public Set<DirectedEdge> getIncomingEdges() {
        return incomingEdges;
    }
    
    //this vertex is tail
    public Set<DirectedEdge> getOutgoingEdges() {
        return super.getEdges();
    }
    
    @Override
    public Set<DirectedEdge> getEdges() {
        return getOutgoingEdges();
    }
 
    /**
     * @return the visited
     */
    public boolean isVisited() {
        return visited;
    }
 
    /**
     * @param visited the visited to set
     */
    public void setVisited( boolean visited ) {
        this.visited = visited;
    }
    
    @Override
    public void reset() {
        setVisited( false );
    }
    
    public void setF( int f ) {
        this.f = f;
    }
    
    public int getF() {
        return f;
    }
}
 
class DirectedEdge extends AbstractEdge<DirectedVertex> {
    
    public DirectedEdge( DirectedVertex tail, DirectedVertex head ) {
        super( tail, head );
    }
    
    public DirectedVertex getTail() {
        return getFirst();
    }
    
    public DirectedVertex getHead() {
        return getSecond();
    }
}