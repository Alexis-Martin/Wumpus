package behaviours.flood;

import java.util.HashMap;

/**
 * Objet flood qui gère le flood pour trouver élire le meilleur pour ramasser un trésor
 */
public class TreasureFlood extends AbstractFlood {
	private static final long serialVersionUID = 660947212581865645L;

	private TreasureFlood(String id, int type){
		super(id, type);
	}
	
	public TreasureFlood(String id){
		this(id, Flood.TreasureHunt);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Flood clone() {
		TreasureFlood flood = new TreasureFlood(id);
		flood.setChildrenHashMap((HashMap<String, Double>) children.clone());
		flood.setParentId(getParentId());
		flood.setParentPos(getParentPos());
		flood.setAttributes(attributes);
		return flood;
	}

	@Override
	public double getMyUtility() {
		int treasure = 0;
		int capacity = 0;
		int quantity = 0;
		if(attributes.containsKey("treasure"))
			treasure = (int)attributes.get("treasure");
		if(attributes.containsKey("capacity"))
			capacity = (int)attributes.get("capacity");
		if(attributes.containsKey("quantity"))
			quantity = (int)attributes.get("quantity");
		
		return (Math.pow(capacity, 2) - Math.pow(quantity, 2)) > 0 ? (Math.pow(capacity, 2) - Math.pow(quantity, 2)) : 0;
	}

	@Override
	public String getBestChild() {
		String bestChild = null;
		double best = 0;
		
		for(String child: children.keySet()){
			
			if(best == 0 || children.get(child) > best){
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
