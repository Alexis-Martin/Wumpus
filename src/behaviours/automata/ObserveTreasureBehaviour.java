package behaviours.automata;

import mas.HunterAgent;
import jade.core.behaviours.OneShotBehaviour;

public class ObserveTreasureBehaviour extends OneShotBehaviour {

	private HunterAgent agent;
	private int nextState = 0;
	private final long pause = 1500;
	
	public ObserveTreasureBehaviour(HunterAgent agent){
		this.agent = agent;
	}
	
	@Override
	public void action() {
		//observe
		//si pas wumpus
		if(agent.isFollow())
			nextState = 0;
		else
			nextState = 4;
		//si wumpus
			//decide
	}
	public int onEnd() {
		return nextState;
	}

}