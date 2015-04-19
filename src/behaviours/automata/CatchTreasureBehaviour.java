package behaviours.automata;

import mas.HunterAgent;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class CatchTreasureBehaviour extends OneShotBehaviour {

	private HunterAgent agent;
	
	public CatchTreasureBehaviour(HunterAgent a) {
		super(a);
		this.agent = a;
	}

	@Override
	public void action() {
		System.out.println("catch treasure");
		this.agent.pick();
	}

}
