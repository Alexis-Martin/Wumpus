package mas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.implementations.SingleNode;

import env.Attribute;
import env.Environment.Couple;

/**
 * 
 *Représentation du monde d'un HunterAgent.
 *<br/>
 *<br/>Formellement, un graph, GraphStream.
 *
 *
 */
public class Map extends SingleGraph{
	
	public Map(){
		super("anonymous");
	}
	/**
	 * Construit un graph avec le nom id.
	 * @param id le nom du graphe
	 * @param display l'affiche si true
	 */
	public Map(String id, boolean display) {
		super(id);
		this.addAttribute("ui.quality");
		this.addAttribute("ui.antialias");
		if(display){
			this.display();
		}
	}
	
	/**
	 * copy l'ensemble d'une autre représentation du monde dans celle ci
	 * @param map 
	 */
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

	/**
	 * Ajoute une route entre la pièce srcId et la pièce dstId
	 * @param srcId le nom de la première pièce
	 * @param dstId le nom de la deuxième
	 * @return true si la route est ajoutée, false si elle existe déjà
	 */
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
	
	/**
	 * ajoute l'edge e si elle n'existe pas
	 * @param e la route à ajouter
	 * @return la nouvelle route créée, l'ancienne sinon, null si une des deux pièces n'existe pas.
	 */
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
	
	/**
	 * ajoute une pièce avec le nom id si elle n'existe pas.
	 * @param id le nom de la nouvelle pièce
	 * @param visited true si cette pièce à été visitée
	 * @return la nouvelle pièce si elle n'existe pas, l'ancienne sinon
	 */
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
	/**
	 * ajoute une pièce avec le nom id et les attributs attr si elle n'existe pas.
	 * @param id le nom de la nouvelle pièce
	 * @param visited true si cette pièce à été visitée
	 * @param attr les attribut pour cette salle
	 * @return la nouvelle pièce si elle n'existe pas, modifie les attribut de l'ancienne et la renvoi sinon
	 */
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
	
	/**
	 * ajoute la pièce n avec ses attributs si elle n'existe pas, modifie les attributs de l'ancienne sinon.
	 * @param n la salle à ajouter
	 * @return la nouvelle salle si elle n'existe pas, l'ancienne sinon
	 */
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
	
	/**
	 * Construit le chemin depuis currentPosition jusqu'à la première pièce non visitée.
	 * @param currentPosition ma position
	 * @return une liste de pièce à parcourir pour arriver à la première pièce non visitée, null si il n'y en a aucune.
	 */
	public ArrayList<String> goTo(String currentPosition){
		if(checkMapCompleteness())
			return null;
		return goTo(currentPosition, null);
				
	}
	
	/**
	 * onstruit le chemin depuis currentPosition jusqu'à la destination
	 * @param currentPosition ma position
	 * @param dest la salle d'arrivée
	 * @return le chemin entre ma position et celle souhaitée, null si c'est impossible
	 */
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
		    	  if((!suivant.hasAttribute("well#") || (int)suivant.getAttribute("well#") < 3) && (!suivant.hasAttribute("well?") || !(boolean)suivant.getAttribute("well?")) ){
		    		  
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
	
	/**
	 * renvoi true si la map est totalement visitée.
	 * @return 
	 */
	public boolean checkMapCompleteness(){ // O(# of rooms)...
		for(Node n : super.getNodeSet()){
			if((!n.hasAttribute("well#") || n.hasAttribute("well#") && (int)n.getAttribute("well#") < 3 ) && !(Boolean) n.getAttribute("visited?") && (!n.hasAttribute("well?") || !(boolean)n.getAttribute("well?"))){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * true si il y a encore un trésor sur la carte.
	 * @return
	 */
	public boolean isTreasure() {
		for(Node n : super.getNodeSet()){
			if(n.hasAttribute("treasure#") && (int)n.getAttribute("treasure#") > 0){
				System.out.println("il reste un trésor en " + n.getId());
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * vérifie si la route (idSrc, idDst) a été prise au moins une fois.
	 * @param idSrc le premier noeud
	 * @param idDst le second
	 * @return true si la route à été emprunté, false sinon.
	 */
	public boolean hasTaken(String idSrc, String idDst){
		if(this.getEdge(this.getEdgeId(idSrc, idDst)) != null){
			return this.getEdge(this.getEdgeId(idSrc, idDst)).hasAttribute("ui.class");
		}else{
			return false;
		}
	}
	
	/**
	 * 
	 * @param id1 première salle
	 * @param id2 deuxième salle
	 * @return l'id de la route entre id1 et id2, null si il n'y a pas de route.
	 */
	public String getEdgeId(String id1, String id2){
		if (super.getEdge(id1+"-"+id2) != null){
			return id1+"-"+id2;
		}else if(super.getEdge(id2+"-"+id1) != null){
			return id2+"-"+id1;
		}
		return null;
	}
	
	/**
	 * liste des route nom prise depuis notre position
	 * @param lobs observation de l'agent
	 * @param myPosition ma position
	 * @return une liste avec l'ensemble des noms des routes non prisent
	 */
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
	
	/**
	 * liste des pièces non visitées depuis notre position
	 * @param lobs observation de l'agent
	 * @param myPosition ma position
	 * @return la liste des pièces non visitées dans notre entourage.
	 */
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
	
	/**
	 * Utilité d'une route. Plus la route à été prise, moins elle est intéressante. si la destination n'a jamais été visitée, utilité maximale (1.0)
	 * @param src pièce de départ
	 * @param dst pièce d'arrivée
	 * @return un nombre entre 0 et 1 correspondant à l'utilité de la route.
	 */
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
	
	
	/**
	 * met à jour notre à priori sur la position des puits autours de la case pos
	 * @param pos notre position
	 * @param obs les observations du HunterAgent
	 */
	public void updateWell(String pos, List<Couple<String,List<Attribute>>> obs){
		List<String> well3 = new ArrayList<String>();
		for(Couple<String,List<Attribute>> c : obs){
			String r = c.getL();
			if(r.equals(pos)){
				continue;
			}
			for(Attribute a : c.getR()){
				if(a.getName().equals("Wind")){
					this.setWell(r);
					if(getWell(r) == 3){
						well3.add(r);
					}
				}
			}
			
			this.updateLayout(this.getNode(r));
		}
		
		for(String node : well3){
			isWell(node);
			this.updateLayout(this.getNode(node));
		}
	}
	
	
	/**
	 * Calcul la force du vent de la case room
	 * @param room
	 */
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
		
		else if(min >= 2 && (boolean)this.getNode(room).getAttribute("visited?"))
			min = 1;
			
		else if(min >= 3)
			min = 2;
		
		
		
		this.getNode(room).addAttribute("well#", min+1);
		
	}
	
	/**
	 * la force du vent sur cette case.
	 * <br/>0 si il n'y a pas de vent, 1 et deux si il y a du vent (plus ou moins important), 3 on ne sait pas si il y a un puit, 4 on est sur que c'est un puit.
	 * @param id le nom de la pièce
	 * @return un entier entre 0 et 4. 
	 */
	public int getWell(String id) {
		if(this.getNode(id).hasAttribute("well?"))
			return 4;
		if(this.getNode(id).hasAttribute("well#"))
			return getNode(id).getAttribute("well#");
		if(this.getNode(id).hasAttribute("wind?") && (boolean) this.getNode(id).getAttribute("wind?"))
			return 1;
		return 0;
	}
	
	/**
	 * Detecte si un vent de force 3 (potentiellement un puit) en ai vraiment un.
	 * @param id nom de la pièce 
	 * @return true si c'est vraiment un puit, false sinon.
	 */
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
	
	/**
	 * activé lorsqu'un agent meurt dans un puit, un nouvel attribut est ajouté sur ce noeud
	 * @param room la pièce ou l'agent est mort
	 * @param b la valeur de l'attribut (true normalement)
	 */
	public void well(String room, boolean b) {
		this.getNode(room).addAttribute("well?", b);
		this.updateLayout(this.getNode(room));
	}
	
	/**
	 * la liste des puits où des agents sont morts 
	 * @return un set<Node> qui correspond à la liste des puits
	 */
	public Set<Node> getWells(){
		Set<Node> retour = new HashSet<Node>();
		for(Node n : this.getNodeSet()){
			if(n.hasAttribute("well?") && (boolean)n.getAttribute("well?"))
				retour.add(n);
		}
		return retour;
	}

	/**
	 * la liste des puits où des agents ont pris des risques mais sont vivants
	 * @return un set<Node> qui correspond à la liste des "faux" puits
	 */
	public Set<Node> getWrongWells(){
		Set<Node> retour = new HashSet<Node>();
		for(Node n : this.getNodeSet()){
			if(n.hasAttribute("well?") && !(boolean)n.getAttribute("well?"))
				retour.add(n);
		}
		return retour;
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
		if(n.hasAttribute("well?") && (boolean)n.getAttribute("well?")){
			classes+= "well"+4+",";
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