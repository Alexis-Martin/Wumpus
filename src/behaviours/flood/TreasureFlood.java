package behaviours.flood;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TreasureFlood extends AbstractFlood {
	private static final long serialVersionUID = 660947212581865645L;

	
	public TreasureFlood(String id){
		super(id);
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
		
		if(capacity - quantity == 0)
			return -1;
		return (0.8 * (capacity + quantity) + 0.2 * (capacity - quantity - treasure) > 0)?0.8 * (capacity + quantity) + 0.2 * (capacity - quantity - treasure):0;
	}



}
