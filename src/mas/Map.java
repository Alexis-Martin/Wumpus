package mas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.implementations.SingleNode;

import env.Attribute;
import env.Environment.Couple;

public class Map extends SingleGraph{
	
	public Map(){
		super("anonymous");
	}
	
	public Map(String id, boolean display) {
		super(id);
		this.addAttribute("ui.quality");
		this.addAttribute("ui.antialias");
		if(display){
			this.display();
		}
	}
	
	public void merge(Map map){
		for(Node n : map.getNodeSet()){
			Node room = this.addRoom(n);
			room.addAttribute("ui.label", room.getId());
			this.updateLayout(room);
		}
		for(Edge e : map.getEdgeSet()){
			this.addRoad(e);
		}
	}

	//Stop creating node on the fly
	public boolean addRoad(String srcId, String dstId){
		if( this.getEdgeId(srcId, dstId) == null ){
			if(super.getNode(srcId) == null || super.getNode(dstId) == null){
				System.out.println("Try to create an edge, but at least one node doesn't exist");
			}
			Edge e = super.addEdge(srcId+"-"+dstId, srcId, dstId, false);
			e.addAttribute("taken#", 0);
			return true;
		}else{
			return false;
		}
	}
	
	public Edge addRoad(Edge e){
		String id = e.getId();
		String srcId = e.getSourceNode().getId();
		String dstId = e.getTargetNode().getId();
		String eId = this.getEdgeId(srcId, dstId);
		Edge newEdge;
		if(eId == null){
			if(super.getNode(srcId) == null || super.getNode(dstId) == null){
				System.out.println("Try to create an edge, but at least one node doesn't exist");
			}
			newEdge = super.addEdge(id, srcId, dstId, false);
			for(String attr : e.getAttributeKeySet()){
				newEdge.addAttribute(attr, e.getAttribute(attr));
			}
		}else{
			newEdge = super.getEdge(eId);
		}
		return newEdge;
	}
	
	public Node addRoom(String id, boolean visited){
		Node n = super.getNode(id);
		if(n != null){
			return n;
		}
		n = super.addNode(id);
		n.addAttribute("ui.label", id);
		n.addAttribute("visited?", visited);
		this.updateLayout(n);
		return n;
	}
	
	public Node addRoom(String id, boolean visited, List<Attribute> attr){
		Node n = this.addRoom(id, visited);
		n.addAttribute("treasure#", 0);
		for(Attribute a:attr){
			if(a.getName().equals("Treasure")){
				n.addAttribute("treasure#", a.getValue());
			}
			if(a.getName().equals("Wind")){
				n.addAttribute("wind?", true);
			}
		}
		
		this.updateLayout(n, visited);
		return n;
	}
	
	public Node addRoom(Node n){
		String id = n.getId();
		Node newNode = super.getNode(id);
		if(newNode == null){
			newNode = super.addNode(id);
		}
		for(String attr:n.getAttributeKeySet()){
			newNode.addAttribute(attr, n.getAttribute(attr));
		}
		return newNode;
	}
	
	public ArrayList<String> goTo(String currentPosition){
		if(checkMapCompleteness())
			return null;
		return goTo(currentPosition, null);
				
	}
	public ArrayList<String> goTo(String currentPosition, String dest){

		SingleNode mine = this.getNode(currentPosition);
		
		ArrayList<SingleNode> file = new ArrayList<SingleNode>();
		int tete = 0;
		file.add(mine);
		mine.addAttribute("mark", (Object)null);
		
		SingleNode objectif = null;
	  
		while(file.size() != tete){
		  
		      SingleNode s = file.get(tete);
		      tete++;   
		      
		      Iterator<SingleNode> it = s.getNeighborNodeIterator();
		      while(it.hasNext()){
		    	  SingleNode suivant = it.next();
		    	  if(!suivant.hasAttribute("well#") || (int)suivant.getAttribute("well#") < 3){
		    		  
		    		  if(!suivant.hasAttribute("mark")){
		    			  file.add(suivant);
		    			  suivant.addAttribute("mark", s);
		    		  }
		    		  
		    		  if(dest == null && ! (Boolean) suivant.getAttribute("visited?")){
		    			  objectif = suivant;
		    			  break;
		    		  }
		    		  else if(dest != null && suivant.getId().equals(dest)){
		    			  objectif = suivant;
		    			  break;
		    		  }
		    	  }
		      }
		      if(objectif != null)
		    	  break;
		 }
		
		
		if(objectif != null){
			ArrayList<String> retour = new ArrayList<String>();
			while(objectif.getAttribute("mark") != null){
				retour.add(0, objectif.getId());
				objectif = (SingleNode)objectif.getAttribute("mark");
			}
			
			Iterator<SingleNode> it = this.getNodeIterator();
			while(it.hasNext()){
				it.next().removeAttribute("mark");
			}
			
			return retour;
		}
		Iterator<SingleNode> it = this.getNodeIterator();
		while(it.hasNext()){
			it.next().removeAttribute("mark");
		}
		return new ArrayList<String>();
	}	
	
	public boolean checkMapCompleteness(){ // O(# of rooms)...
		for(Node n : super.getNodeSet()){
			if(! (Boolean) n.getAttribute("visited?")){
				return false;
			}
		}
		return true;
	}
	
	
	
	
	public boolean hasTaken(String idSrc, String idDst){
		if(this.getEdge(this.getEdgeId(idSrc, idDst)) != null){
			return this.getEdge(this.getEdgeId(idSrc, idDst)).hasAttribute("ui.class");
		}else{
			return false;
		}
	}
	
	public String getEdgeId(String id1, String id2){
		if (super.getEdge(id1+"-"+id2) != null){
			return id1+"-"+id2;
		}else if(super.getEdge(id2+"-"+id1) != null){
			return id2+"-"+id1;
		}
		return null;
	}
	
	public List<String> roadNotTaken(List<Couple<String,List<Attribute>>> lobs, String myPosition){
		List<String> roadNotTaken = new ArrayList<String>();
		for(Couple<String,List<Attribute>> c:lobs){
			if(c.getL().equals(myPosition)){
				continue;
			}
			if(!this.hasTaken(myPosition, c.getL())){
				roadNotTaken.add(c.getL());
			}
		}
		return roadNotTaken;
	}
	
	public List<String> roomNotOpen(List<Couple<String,List<Attribute>>> lobs, String myPosition){
		List<String> roomNotOpen = new ArrayList<String>();
		for(Couple<String,List<Attribute>> c:lobs){
			if(c.getL().equals(myPosition)){
				continue;
			}
			if(super.getNode(c.getL()) == null){
				System.out.println("Node "+c.getL()+" not added to graph");
			}
			if(!super.getNode(c.getL()).hasAttribute("visited?")){
				System.out.println("Node without visited? attribute");
			}
			if(! (Boolean) super.getNode(c.getL()).getAttribute("visited?")){
				roomNotOpen.add(c.getL());
			}
		}
		return roomNotOpen;
	}
	
	public double getMoveUtility(String src, String dst){
		Node n = this.getNode(dst);
		Edge e = this.getEdge(this.getEdgeId(src, dst));
		double utility = 0.0;
		if(this.getWell(dst) >= 3){
			return 0;
		}
		if(!(Boolean) n.getAttribute("visited?")){
			utility += 1.0;
		}
		int taken = (int) e.getAttribute("taken#");
		utility += 1.0/(taken + 1.0);
		
		return utility;
	}
	
	public void updateWell(String pos, List<Couple<String,List<Attribute>>> obs){
		for(Couple<String,List<Attribute>> c : obs){
			String r = c.getL();
			if(r.equals(pos)){
				continue;
			}
			for(Attribute a : c.getR()){
				if(a.getName().equals("Wind")){
					this.setWell(r);
				}
			}
			this.updateLayout(this.getNode(r));
		}
	}
	
	
	public void setWell(String room){
		Iterator<SingleNode> it = this.getNode(room).getNeighborNodeIterator();
		int min = 6;
		while(it.hasNext()){
			SingleNode s = it.next();
			//System.out.println(s.getId()+" well : "+ this.getWell(s.getId()));
			if(getWell(s.getId()) < min)
				min = getWell(s.getId());
					
		}
		if(min == 6)
			min = 0;
		
		this.getNode(room).addAttribute("well#", min+1);
		
		if(getWell(room) == 3){
			isWell(room);
		}
		
	}
	
	public int getWell(String id) {
		if(this.getNode(id).hasAttribute("well#"))
			return getNode(id).getAttribute("well#");
		if(this.getNode(id).hasAttribute("wind?") && (boolean) this.getNode(id).getAttribute("wind?"))
			return 1;
		return 0;
	}
	
	public boolean isWell(String id){

		Iterator<SingleNode> voisin_1 = this.getNode(id).getNeighborNodeIterator();
		
		while(voisin_1.hasNext()){
			
			SingleNode v_1 = voisin_1.next();
			Iterator<SingleNode> voisin_2 = v_1.getNeighborNodeIterator();

			boolean b = true;
			
			ArrayList<SingleNode> vs_2 = new ArrayList<SingleNode>();
			
			while(voisin_2.hasNext()){
				SingleNode v_2 = voisin_2.next();
				
				if(!(v_2.getId().equals(id)) && getWell(v_2.getId()) >= 3 ){
					b = false;
					break;
				}
				else if(getWell(v_2.getId()) <= 1){
					vs_2.add(v_2);
				}
			}
			
			if(b){
				for(SingleNode v_2 : vs_2){
					boolean bo = true;
					Iterator<SingleNode> voisin_3 = v_2.getNeighborNodeIterator();
					while(voisin_3.hasNext()){
						SingleNode v_3 = voisin_3.next();
						if(!(v_3.getId().equals(v_1.getId())) && getWell(v_3.getId()) > 1){
							bo = false;
							break;
						}
					}
					if(bo){
						this.getNode(id).addAttribute("well#", 4);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void updateLayout(Node n){
		this.updateLayout(n, false);
	}
	
	
	public void updateLayout(Node n, boolean marker){
		String classes = "";
		if(n.hasAttribute("ui.class")){
			n.removeAttribute("ui.class");
		}
		if(n.hasAttribute("visited?") && !(boolean) n.getAttribute("visited?")){
			classes += "open,";
		}
		if(n.hasAttribute("treasure#") && (int) n.getAttribute("treasure#") > 0){
			classes += "treasure,";
		}
		if(n.hasAttribute("well#") && (int)n.getAttribute("well#") > 0){
			int w = (int)n.getAttribute("well#");
			classes+= "well"+w+",";
		}
		if(marker){
			classes += "marker,";
		}
		if(classes.length() > 0){
			classes = classes.substring(0, classes.length()-1);
			n.addAttribute("ui.class", classes);
		}
	}
	
	public void updateUIMarkers(String from, String to){
		Node src = super.getNode(from);
		Node dst = super.getNode(to);
		Edge e = super.getEdge(this.getEdgeId(from, to));
		
		//update nodes layout
		this.updateLayout(src);
		this.updateLayout(dst);
		String classes = "";
		if(dst.hasAttribute("ui.class")){
			classes = dst.getAttribute("ui.class");
			if(classes.length() > 0){
				classes += ",marker";
			}else{
				classes = "marker";
			}
		}else{
			classes = "marker";
		}
		dst.setAttribute("ui.class", classes);
		
		//update edge's layout
		int taken = (Integer) e.getAttribute("taken#");
		if(taken == 1){
			e.addAttribute("ui.class", "taken");
		}else{
			e.addAttribute("ui.class", "taken2");
		}
	}

}