package behaviours.automata;

import mas.HunterAgent;
import jade.core.behaviours.OneShotBehaviour;

public class GoToBehaviour extends OneShotBehaviour {


	/**
	 * 
	 */
	private static final long serialVersionUID = 8875580766508173853L;
	private HunterAgent agent;
	
	public GoToBehaviour(HunterAgent a) {
		super(a);
		this.agent = a;
	}

	@Override
	public void action() {
		agent.setNextMove(agent.popStackMove());
		if(agent.isStackMoveEmpty())
			agent.setFollow(false);
	}

}
