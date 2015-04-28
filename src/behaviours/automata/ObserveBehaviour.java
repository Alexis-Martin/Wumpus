package behaviours.automata;

import java.util.ArrayList;
import java.util.List;

import mas.HunterAgent;

import org.graphstream.graph.Node;

import env.Attribute;
import env.Environment.Couple;
import jade.core.behaviours.OneShotBehaviour;


/**
 * Behaviour faisant partie du comportement de base du HunterAgent.
 * <br/>
 * <br/>Permet d'observer son environnement, mettre à jour sa vision du monde et déclencher plusieurs comportement alternatif tels que la recherche de collègue pour rammasser le trésor ou prendre un risque...
 */
public class ObserveBehaviour extends OneShotBehaviour {
	private static final long serialVersionUID = -7959229920398697586L;
	private HunterAgent agent;
	private int nextState = 0;
	private final long pause = 1500;
	private int nbStepBeforeRisk = 7;
	
	public ObserveBehaviour(HunterAgent agent){
		this.agent = agent;
	}
	
	@Override
	public void action() {
		nextState = 0;
		String myPosition = agent.getCurrentPosition();
		String log = "";
		boolean risk = false;
		
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
						this.nextState = 1;
					}
					agent.getMap().updateLayout(n, true);
					continue;
				}
				
				//Try to add a new room to the graph
				Node n = agent.getMap().addRoom(pos, false, c.getR());
				agent.getDiff().addRoom(n);
				if(agent.getMap().addRoad(myPosition, pos)){
					agent.getDiff().addRoad(agent.getMap().getEdge(agent.getMap().getEdgeId(myPosition, pos)));
				}
				if(n.hasAttribute("well#") && (int) n.getAttribute("well#") == 3 && (!n.hasAttribute("well?") || !(boolean)n.getAttribute("well?"))){
					risk = true;
				}
			}
		}
		//System.out.println(log);
		this.block(this.pause);
		
		if(agent.isStandBy()){
			nextState = HunterAgent.STAND_BY;
		}else if(agent.getNbStep() > nbStepBeforeRisk && risk && agent.getMap().checkMapCompleteness() && !agent.getMap().isTreasure()){
			//ajout d'une proba de lancer le flood pour les limiter??
			nextState = 3;
		}
	}
	
	public int onEnd(){
		return this.nextState;
	}

}
