package behaviours.flood;

import java.util.HashMap;

/**
 * Objet flood qui gère le flood pour trouver élire le meilleur pour suivre un explorateur
 */
public class SearchFollowerFlood extends AbstractFlood {

	private static final long serialVersionUID = 94304415304166758L;

	private SearchFollowerFlood(String id, int type){
		super(id, type);
	}
	
	public SearchFollowerFlood(String id){
		this(id, Flood.Follow);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Flood clone() {
		SearchFollowerFlood flood = new SearchFollowerFlood(id);
		flood.setChildrenHashMap((HashMap<String, Double>) children.clone());
		flood.setParentId(getParentId());
		flood.setParentPos(getParentPos());
		flood.setAttributes(attributes);
		return flood;
	}

	@Override
	public double getMyUtility() {
		int capacity = 0;
		int quantity = 0;
		if(attributes.containsKey("capacity"))
			capacity = (int)attributes.get("capacity");
		if(attributes.containsKey("quantity"))
			quantity = (int)attributes.get("quantity");
		
		return capacity - quantity; 
	}
	
	@Override
	public String getBestChild() {
		String bestChild = null;
		double best = -1;
		
		for(String child: children.keySet()){
			if(best == -1 || children.get(child) > best){
				bestChild = child;
				best = children.get(child);
			}
		}
		return bestChild;
	}
	
	@Override
	public String getBestId() {
		String bestChild = getBestChild();
		if(bestChild != null  && children.get(bestChild) > getMyUtility())
			return bestChild;
		return null;
	}
	
	@Override
	public double getBestValue(){
		String bestChild = getBestChild();
		if(bestChild != null  && children.get(bestChild) > getMyUtility())
			return children.get(bestChild);
		return getMyUtility();
	}

}
