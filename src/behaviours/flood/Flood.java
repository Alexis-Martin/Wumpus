package behaviours.flood;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;
/**
 * Inteface pour des objets Flood.
 * <br/>
 * <br/>Un objet Flood va gérer entièrement le type de flood qui sera lancé (flood pour ramasser un trésor, prendre un risque,...)
 * <br/>Cet objet va enregistrer le père, les enfants, va permettre de calculer l'utilité de l'agent, ... 
 */
public interface Flood extends Serializable {
	
	public static int TreasureHunt = 1;
	public static int Risk = 2;
	public static int Follow = 3;
	

	/**
	 * Renvoi le nom du père
	 * @return le nom (local) du père
	 */
	public String getParentId();
	
	/**
	 * la position du père
	 * @return l'id de la case du père
	 */
	public String getParentPos();
	
	/**
	 * Renvoi la liste des enfants
	 * @return un Set contenant le nom des enfants
	 */
	public Set<String> getChildren();
	
	/**
	 * Renvoi le nom du flood
	 * @return l'id du flood (qui est aussi son nom)
	 */
	public String getId();
	
	/**
	 * Renvoi le meilleur fils
	 * @return le meilleur fils (celui qui à la plus grande utilité)
	 */
	public String getBestChild();
	
	/**
	 * Renvoi un nouvel objet flood avec notre identifiant et notre position afin de l'envoyer à nos enfants 
	 * @param parentId notre id (on est le père de nos enfants)
	 * @param parentPos notre position
	 * @return une copy du flood avec les informations sur notre nom et notre position
	 */
	public Flood transmitFlood(String parentId, String parentPos);
	
	/**
	 * une chaine de caractère qui va être notre utilité, afin de transmettre cette information à notre père
	 * @return notre utilité
	 */
	public String transmitUtility();

	/**
	 * met à jour l'identifiant de notre père
	 * @param parent le nom de notre père
	 */
	public void setParentId(String parent);
	
	/**
	 * retourne la valeur correspondant à l'attribut attr
	 * @param attr la clé de l'attribut
	 * @return la valeur de cette attribut
	 */
	public Object getAttribute(String attr);
	
	/**
	 * enregistre un attribut avec la clé attr et la valeur obj
	 * @param attr la clé
	 * @param obj la valeur
	 */
	public void setAttribute(String attr, Object obj);

	/**
	 * ajoute le fils child
	 * @param child le nom du fils
	 */
	public void addChild(String child);

	/**
	 * regarde si il existe au moins 1 fils
	 * @return true si il existe au moins 1 fils, false sinon
	 */
	public boolean hasChild();

	/**
	 * regarde si on a un père
	 * @return true si on a un père, false sinon
	 */
	public boolean hasParent();

	/**
	 * retire tous les fils qui sont dans removeChildren
	 * @param removeChildren la liste des fils à supprimer
	 */
	public void removeAll(Set<String> removeChildren);

	/**
	 * renvoi le nom du meilleur entre les fils et soi-même
	 * @return le nom du fils qui est le meilleur, null si c'est nous
	 */
	public String getBestId();

	/**
	 * vérifie si on à l'utilité pour tous nos fils
	 * @return true si tous nos fils ont transmit leur utilité
	 */
	public boolean hasAllUtilities();
	
	/**
	 * renvoi la totalité des attributs
	 * @return une hashMap contenant tous les attributs
	 */
	public HashMap<String, Object> getAttributes();
	
	/**
	 * met à jour l'utilité du fils localName
	 * @param localName le nom du fils
	 * @param utility son utilité
	 */
	public void setChildUtility(String localName, double utility);
	
	/**
	 * renvoi mon utilité
	 * @return mon utilité dans ce flood
	 */
	public double getMyUtility();
	
	/**
	 * met à jour la position du père
	 * @param parentPos la nouvelle position du père
	 */
	void setParentPos(String parentPos);

	/**
	 * supprime tous les enfants du flood
	 */
	public void removeAllChild();

	/**
	 * clone le flood
	 * @return renvoie exactement le même flood
	 */
	public Flood clone();
	
	/**
	 * Renvoi le type de flood (treasure, risk, ...)
	 * @return
	 */
	public int getType();
	
	/**
	 * renvoi la meilleure utilité parmis nous et nos enfants
	 * @return la plus grande utilité
	 */
	double getBestValue();

}
