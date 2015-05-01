package behaviours.flood;

import java.util.HashMap;
/**
 * Objet flood qui gère le flood pour trouver élire le meilleur pour prendre un risque.
 */
public class RiskFlood extends AbstractFlood {

	private RiskFlood(String id, int type){
		super(id, type);
	}
	
	public RiskFlood(String id){
		this(id, Flood.Risk);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Flood clone() {
		RiskFlood flood = new RiskFlood(id);
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
		
		if(quantity > 0)
			return 0;
		return capacity; 
	}
	
	@Override
	public String getBestChild() {
		String bestChild = null;
		double best = -1;
		
		for(String child: children.keySet()){
			if(children.get(child) > 0 && (best == -1 || children.get(child) < best)){
				bestChild = child;
				best = children.get(child);
			}
		}
		return bestChild;
	}
	
	@Override
	public String getBestId() {
		String bestChild = getBestChild();
		if(bestChild != null  && (getMyUtility() <= 0 || children.get(bestChild) < getMyUtility()))
			return bestChild;
		return null;
	}
	
	@Override
	public double getBestValue(){
		String bestChild = getBestChild();
		if(bestChild != null  && (getMyUtility() <= 0 || children.get(bestChild) < getMyUtility()))
			return children.get(bestChild);
		return getMyUtility();
	}

}
