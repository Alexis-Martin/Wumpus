package behaviours.flood;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

abstract class AbstractFlood implements Flood {
	
	private static final long serialVersionUID = 660947212581865645L;
	protected String id;
	protected double myUtility;
	protected HashMap<String, Double> children;
	protected String parentPos, parentId;
	protected HashMap<String, Object> attributes;
	
	public AbstractFlood(String id, double myUtility){
		this.id = id;
		this.myUtility = myUtility;
		this.attributes = new HashMap<String, Object>();
		this.children= new HashMap<String, Double>();
	}
	@Override
	public abstract String getMessage();

	@Override
	public String getParentId() {
		return parentId;
	}

	@Override
	public String getParentPos() {
		
		return parentPos;
	}
	
	public HashMap<String, Double> getChildrenHashMap(){
		return children;
	}
	
	public void setChildrenHashMap(HashMap<String, Double> children){
		this.children = children;
	}
	
	@Override
	public HashMap<String, Object> getAttributes(){
		return attributes;
	}
	
	public void setAttributes(HashMap<String, Object> attributes){
		this.attributes = attributes;
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
	public abstract String getBestChild();

	@Override
	public abstract Flood transmitFlood(String parentId, String parentPos);

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
	public abstract String getBestId();

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
	public abstract Flood clone();
	

}
