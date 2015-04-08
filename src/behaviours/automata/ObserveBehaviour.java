package behaviours.automata;

import java.util.List;

import mas.HunterAgent;

import org.graphstream.graph.Node;

import env.Attribute;
import env.Environment.Couple;
import jade.core.behaviours.OneShotBehaviour;

public class ObserveBehaviour extends OneShotBehaviour {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959229920398697586L;
	private HunterAgent agent;
	private int nextState = 0;
	private final long pause = 1500;
	
	public ObserveBehaviour(HunterAgent agent){
		this.agent = agent;
	}
	
	@Override
	public void action() {
		String myPosition = agent.getCurrentPosition();
		String log = "";
		
		nextState = 0;
		log += "\nPosition of agent "+agent.getLocalName()+" : "+myPosition;
		if (myPosition!=""){
			List<Couple<String,List<Attribute>>> lobs = agent.observe(myPosition);
			//Update current Room
			agent.getMap().getNode(myPosition).setAttribute("visited?", true);
			
			//Update Map
			for(Couple<String,List<Attribute>> c:lobs){
				String pos = c.getL();
				//Skip the current room
				if(pos.equals(myPosition)){
					Node n = agent.getMap().addRoom(pos, true, c.getR());
					agent.getDiff().addRoom(n);
					if (n.hasAttribute("treasure#") && (int) n.getAttribute("treasure#") > 0){
						this.nextState = 2;
					}
					continue;
				}
				
				//Try to add a new room to the graph
				Node n = agent.getMap().addRoom(pos, false, c.getR());
				if(agent.getMap().addRoad(myPosition, pos)){
					agent.getDiff().addRoom(n);
					agent.getDiff().addRoad(agent.getMap().getEdge(agent.getMap().getEdgeId(myPosition, pos)));
				}
			}
		}
		//System.out.println(log);
		this.block(this.pause);
	}
	
	public int onEnd(){
		return this.nextState;
	}

}
