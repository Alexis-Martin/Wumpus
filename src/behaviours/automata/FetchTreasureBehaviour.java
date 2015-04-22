package behaviours.automata;

import java.util.List;

import org.graphstream.graph.Node;

import env.Attribute;
import env.Environment.Couple;
import mas.HunterAgent;
import jade.core.behaviours.OneShotBehaviour;

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
		List<Couple<String,List<Attribute>>> lobs = agent.observe(myPosition);
		boolean canMove = false;
		
		nextState = 0;
		
		if(nextMove == null){
			System.out.println(agent.getLocalName()+" picked up a treasure in room "+myPosition);
			agent.pick();
			agent.setTreasure(false);
			nextState = 1;
			return;
		}
		
		agent.getMap().getNode(myPosition).setAttribute("visited?", true);
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
		
		if(canMove){
			if(!agent.move(myPosition, nextMove)){
				System.out.println(agent.getLocalName()+" waiting for room "+nextMove+" to be released");
				agent.getStackMove().add(0, nextMove);
			}
		}else{
			System.out.println("Error : room " + nextMove+" is not in the neighborhood of agent "+agent.getLocalName()+ " ("+agent.getCurrentPosition()+")");
			agent.doDelete();
		}
		block(1500);
	}

	@Override
	public int onEnd() {
		return nextState;
	}
}
