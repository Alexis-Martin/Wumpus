package behaviours.flood;

import java.util.HashMap;

public class TreasureFlood extends AbstractFlood {
	private static final long serialVersionUID = 660947212581865645L;

	
	public TreasureFlood(String id, int type){
		super(id, type);
	}

	@Override
	public String getMessage() {
		
		return null;
	}
	


	@Override
	public Flood transmitFlood(String parentId, String parentPos) {
		Flood flood = clone();
		flood.setParentPos(parentPos);
		flood.setParentId(parentId);
		flood.removeAllChild();
		return flood;
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
	public String transmitUtility(){
		String best = getBestId();
		if(best == null)
			return "" + getMyUtility();
		return "" + children.get(best);
	}


	@SuppressWarnings("unchecked")
	@Override
	public Flood clone() {
		TreasureFlood flood = new TreasureFlood(id, this.getType());
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
		
		if(capacity - quantity == 0)
			return -1;
		return (Math.pow(capacity, 2) - Math.pow(quantity, 2)) > 0 ? (Math.pow(capacity, 2) - Math.pow(quantity, 2)) : 0;
	}



}
