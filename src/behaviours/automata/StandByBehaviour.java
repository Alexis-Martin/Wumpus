package behaviours.automata;

import mas.HunterAgent;
import jade.core.behaviours.OneShotBehaviour;

public class StandByBehaviour extends OneShotBehaviour {
	private static final long serialVersionUID = -9136965155944199798L;
	private HunterAgent agent;
	private int nextState;
	private final long timeout = 2000;
	
	public StandByBehaviour(HunterAgent agent){
		super(agent);
		this.agent = agent;
	}

	@Override
	public void action() {

		this.nextState = 0;
		if(agent.isStandBy()){
			//Check wumpus...
			//System.out.println(agent.getLocalName() + " is on stand by");
			block(this.timeout);
		}else{
			if(agent.isTreasure()){
				this.nextState = 1;
				agent.setTreasure(false);
			}
			else if(agent.isRiskWell()){
				this.nextState = 2;
				agent.setRiskWell(false);
			}
			else
				this.nextState = 3;
		}
	}
	
	public int onEnd(){
		
		return nextState;
	}

}
