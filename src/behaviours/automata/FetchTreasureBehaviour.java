package behaviours.automata;

import java.util.List;

import org.graphstream.graph.Node;

import env.Attribute;
import env.Environment.Couple;
import mas.HunterAgent;
import jade.core.behaviours.OneShotBehaviour;


/**
 * Etat de l'automate qui correspond au comportement déclenché par l'élection du TreasureFlood
 * 
 * l'objectif de cette Behaviour est de se déplacer jusqu'à la position ou il faut prendre le trésor, le ramasser et repartir dans le comportement de base.
 */

public class FetchTreasureBehaviour extends OneShotBehaviour {
	private static final long serialVersionUID = 3227068047876450793L;
	private HunterAgent agent;
	private int nextState;
	
	public FetchTreasureBehaviour(HunterAgent a){
		super(a);
		this.agent = a;
	}
	
	
	@Override
	public void action() {
		String myPosition = agent.getCurrentPosition();
		String nextMove = agent.popStackMove();
		//on observe notre environement
		List<Couple<String,List<Attribute>>> lobs = agent.observe(myPosition);
		boolean canMove = false;
		
		nextState = 0;
		
		//si il n'y a pas de déplacement suivant, on est arrivé. On ramasse et on sort de l'état pour retourner dans le comprotement de base
		if(nextMove == null){
			System.out.println(agent.getLocalName()+" picked up a treasure in room "+myPosition);
			agent.pick();
			agent.setTreasure(false);
			nextState = 1;
			return;
		}
		//on marque notre case comme visité
		agent.getMap().getNode(myPosition).setAttribute("visited?", true);
		
		//on met à jour notre représentation du monde
		for(Couple<String,List<Attribute>> c:lobs){
			String pos = c.getL();
			if(pos.equals(myPosition)){
				Node n = agent.getMap().addRoom(pos, true, c.getR());
				agent.getDiff().addRoom(n);
				agent.getMap().updateLayout(n, true);
				continue;
			}
			if(pos.equals(nextMove)){
				canMove = true;
			}
			Node n = agent.getMap().addRoom(pos, false, c.getR());
			agent.getDiff().addRoom(n);
			if(agent.getMap().addRoad(myPosition, pos)){
				agent.getDiff().addRoad(agent.getMap().getEdge(agent.getMap().getEdgeId(myPosition, pos)));
			}
		}
		
		//on se déplace
		if(canMove){
			if(!agent.move(myPosition, nextMove)){
				System.out.println(agent.getLocalName()+" waiting for room "+nextMove+" to be released");
				agent.getStackMove().add(0, nextMove);
			}
		}
		//si on ne peut pas on retourne dans le comportement de base
		else{
			System.out.println("Error : room " + nextMove+" is not in the neighborhood of agent "+agent.getLocalName()+ " ("+agent.getCurrentPosition()+")");
			agent.setTreasure(false);
			nextState = 1;
			return;
		}
		block(1500);
	}

	@Override
	public int onEnd() {
		return nextState;
	}
}
