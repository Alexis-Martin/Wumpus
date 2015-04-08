package behaviours.flood;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

abstract class AbstractFlood implements Flood {
	
	private static final long serialVersionUID = 660947212581865645L;
	private String id;
	private double myUtility;
	private HashMap<String, Double> children;
	private String parentPos, parentId;
	private HashMap<String, Object> attributes;
	
	public AbstractFlood(String id, double myUtility){
		this.id = id;
		this.myUtility = myUtility;
	}
	@Override
	abstract String getMessage();

	@Override
	public String getParentId() {
		return parentId;
	}

	@Override
	public String getParentPos() {
		
		return parentPos;
	}

	@Override
	public Set<String> getChildren() {
		return children.keySet();
	}

	@Override
	public String getId() {
		return id;
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
	public Flood transmitFlood(String parentId, String parentPos) {
		Flood flood = Flood.clone();
		flood.setParentPos(parentPos);
		flood.setParentId(parentId);
		flood.removeAllChild();
		return flood;
	}

	@Override
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@Override
	public void setParentPos(String parentPos) {
		this.parentPos = parentPos;
	}
	
	@Override
	public Object getAttribute(String attr) {
		return attributes.get(attr);
	}

	@Override
	public void setAttribute(String attr, Object obj) {
		attributes.put(attr, obj);
	}

	@Override
	public void addChild(String child) {
		children.put(child, null);
	}

	@Override
	public boolean hasChild() {
		return !children.isEmpty();
	}

	@Override
	public boolean hasParent() {
		return (parentId == null)?false:true;
	}

	@Override
	public void removeAll(Set<String> removeChildren) {
		for(String child : removeChildren){
			children.remove(child);
		}
	}

	@Override
	public String getBestId() {
		String bestChild = getBestChild();
		if(children.get(bestChild) > myUtility)
			return bestChild;
		return null;
	}

	@Override
	public boolean hasAllUtilities() {
		for(String child: children.keySet()){
			if(children.get(child) == null)
				return false;
		}
		return true;
	}

	@Override
	public void setUtility(String localName, double utility) {
		children.put(localName, utility);
	}

	@Override
	public void setUtility(double utility) {
		this.myUtility = utility;
	}
	@Override
	public void removeAllChild() {
		children.clear();
	}
	@Override
	abstract Flood clone();
	

}
