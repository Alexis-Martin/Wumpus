package mas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

/**
 * Permet de sérialiser et désérialiser une représentation du monde
 *
 */
public class SerializationHelper {
	
	/**
	 * Serialise une map
	 * @param map la représentation du monde
	 * @return la map avec un format que l'on peut envoyer
	 */
	public static HashMap<String, List<String>> serializeMapInfo(Map map){
		HashMap<String, List<String>> sMap = new HashMap<String, List<String>>();
		for(Node n : map.getNodeSet()){
			List<String> data = new ArrayList<String>();
			String id = n.getId();
			for(String attr : n.getAttributeKeySet()){
				if(attr.contains("?") && (boolean)n.getAttribute(attr)){
					data.add(attr);
				}else if(attr.contains("#")){
					data.add(attr+n.getAttribute(attr));
				}
			}
			sMap.put(id, data);
		}
		
		for(Edge e : map.getEdgeSet()){
			List<String> data = new ArrayList<String>();
			String id = e.getId();
			for(String attr : e.getAttributeKeySet()){
				if(attr.contains("?") && (boolean)e.getAttribute(attr)){
					data.add(attr);
				}else if(attr.contains("#")){
					data.add(attr+e.getAttribute(attr));
				}
			}
			sMap.put(id, data);
		}
		
		return sMap;
	}
	/**
	 * désérialise une map
	 * @param info la représentation du monde sous format sérialisé
	 * @return une map
	 */
	public static Map deserializeMapInfo(HashMap<String, List<String>> info){
		Map map = new Map();
		for(String id : info.keySet()){
			if(!id.contains("-")){
				Node n = map.addRoom(id, false);
				for(String attr : info.get(id)){
					if(attr.contains("?")){
						n.addAttribute(attr, true);
					}else if(attr.contains("#")){
						String[] split = attr.split("#");
						if(attr.equals("well#") && n.hasAttribute("well#") && (int)n.getAttribute("well#") <= Integer.parseInt(split[1])){
							continue;
						}
						else if(attr.equals("well#") && n.hasAttribute("well#") && (int)n.getAttribute("well#") >= Integer.parseInt(split[1])){
							System.out.println("j'ai recu " + Integer.parseInt(split[1]) + " pour la force du puit de " + n.getId() + " et le mien est " + n.getAttribute("well#")+ ". Je met a jour!.");
						}
								
						n.addAttribute(split[0]+"#", Integer.parseInt(split[1]));
					}
				}
			}
		}
		for(String id : info.keySet()){
			if(id.contains("-")){
				String[] nodes =  id.split("-");
				map.addEdge(id, nodes[0], nodes[1]);
				Edge e = map.getEdge(map.getEdgeId(nodes[0], nodes[1]));
				for(String attr : info.get(id)){
					if(attr.contains("?")){
						e.addAttribute(attr, true);
					}else if(attr.contains("#")){
						String[] split = attr.split("#");
						e.addAttribute(split[0]+"#", Integer.parseInt(split[1]));
					}
				}
			}
		}
		return map;
	}
}