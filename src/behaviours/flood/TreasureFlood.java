package behaviours.flood;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TreasureFlood extends AbstractFlood {
	private static final long serialVersionUID = 660947212581865645L;

	
	public TreasureFlood(String id, double myUtility){
		super(id, myUtility);
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
		flood.setUtility(0);
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
		if(bestChild != null  && children.get(bestChild) > myUtility)
			return bestChild;
		return null;
	}
	
	@Override
	public String transmitUtility(){
		String best = getBestId();
		if(best == null)
			return "" + myUtility;
		return "" + children.get(best);
	}


	@SuppressWarnings("unchecked")
	@Override
	public Flood clone() {
		TreasureFlood flood = new TreasureFlood(id, myUtility);
		flood.setChildrenHashMap((HashMap<String, Double>) children.clone());
		flood.setParentId(getParentId());
		flood.setParentPos(getParentPos());
		flood.setAttributes(attributes);
		return flood;
	}

}
