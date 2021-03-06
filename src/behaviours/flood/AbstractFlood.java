package behaviours.flood;

import java.util.HashMap;
import java.util.Set;

/**
 * Classe abstraite permettant de gérer les fonctions de l'interface Flood qui sont similaire à tous les floods  
 */
abstract class AbstractFlood implements Flood {
	
	private static final long serialVersionUID = 660947212581865645L;
	protected String id;
	protected HashMap<String, Double> children;
	protected String parentPos, parentId;
	protected HashMap<String, Object> attributes;
	protected int type;
	
	private AbstractFlood(){
	}
	
	public AbstractFlood(String id, int t){
		this();
		this.id = id;
		this.attributes = new HashMap<String, Object>();
		this.children= new HashMap<String, Double>();
		this.type = t;
	}


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
	public Flood transmitFlood(String parentId, String parentPos) {
		Flood flood = clone();
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
	public abstract String getBestChild();

	@Override
	public boolean hasAllUtilities() {
		for(String child: children.keySet()){
			if(children.get(child) == null)
				return false;
		}
		return true;
	}

	@Override
	public void setChildUtility(String localName, double utility) {
		children.put(localName, utility);
	}

	@Override
	public String transmitUtility(){
		String best = getBestId();
		if(best == null)
			return "" + getMyUtility();
		return "" + children.get(best);
	}
	
	public abstract double getMyUtility();
	
	@Override
	public void removeAllChild() {
		children.clear();
	}
	@Override
	public abstract Flood clone();
	
	@Override
	public int getType(){
		return type;
	}
	

}
