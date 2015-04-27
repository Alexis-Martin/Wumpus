package behaviours.automata;

import mas.HunterAgent;
import jade.core.behaviours.OneShotBehaviour;

public class MoveTreasureBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 20070325733578106L;
	private int nextState;
	private HunterAgent agent;
	
	public MoveTreasureBehaviour(HunterAgent a) {
		super(a);
		this.agent = a;
	}

	@Override
	public void action() {

		String pos = agent.getCurrentPosition();
		String dst = agent.getNextMove();
		String log = "";

		nextState = 0;

		log += "\nAgent "+agent.getLocalName()+" is heading to position "+dst;
		if(!agent.move(pos, dst)){
			log += "\nAgent "+agent.getLocalName()+" Cannot go into a full room";
			agent.setRandomWalk(true);
			System.out.println(agent.getLocalName() + " veut aller en " + dst);
			block(300);
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
