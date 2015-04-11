package behaviours.automata;

import mas.HunterAgent;
import jade.core.behaviours.OneShotBehaviour;

public class MoveBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 20070325733578106L;
	private int nextState;
	private HunterAgent agent;
	
	public MoveBehaviour(HunterAgent a) {
		super(a);
		this.agent = a;
	}

	@Override
	public void action() {
		if(agent.isStandBy()){
			nextState = HunterAgent.STAND_BY;
			return;
		}
		String pos = agent.getCurrentPosition();
		String dst = agent.getNextMove();
		String log = "";

		nextState = 0;

		log += "\nAgent "+agent.getLocalName()+" is heading to position "+dst;
		if(!agent.move(pos, dst)){
			log += "\nAgent "+agent.getLocalName()+" Cannot go into a full room";
			agent.setRandomWalk(true);
			this.nextState = 1;
		}else{
			agent.setRandomWalk(false);
		}
		
		//System.out.println(log);
	}
	public int onEnd(){
		return this.nextState;
	}

}
