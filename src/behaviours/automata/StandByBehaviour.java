package behaviours.automata;

import behaviours.WaitFollowerBehaviour;
import behaviours.flood.Flood;
import mas.HunterAgent;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Etat de l'automate ou on se met en pause afin de faire une autre action
 */

public class StandByBehaviour extends OneShotBehaviour {
	private static final long serialVersionUID = -9136965155944199798L;
	private HunterAgent agent;
	private int nextState;
	private final long timeout = 2000;

	public boolean begin = true;
	public StandByBehaviour(HunterAgent agent){
		super(agent);
		this.agent = agent;
	}

	@Override
	public void action() {

		this.nextState = 0;
		if(agent.isStandBy() || agent.isOnExploration()){
			agent.setStandBy(true);
			//Check wumpus...
			//System.out.println(agent.getLocalName() + " is on stand by");
				
			block(this.timeout);
		}else{
			if(agent.isTreasure()){
				this.nextState = Flood.TreasureHunt;
			}
			else if(agent.isRisk()){
				this.nextState = Flood.Risk;
			}
			else if(agent.isFollowing()){
				this.nextState = Flood.Follow;
			}
			else if (agent.isWaitingFollower()){
				agent.setStandBy(true);
				agent.setWaitFollower(false);
				agent.addBehaviour(new WaitFollowerBehaviour(agent));
			}

			else
				this.nextState = 4;
		}
		if(!agent.isStandBy()){
			System.out.println(agent.getLocalName()+" is no longer in StandBy. Next state is "+nextState);
		}
	}
	
	public int onEnd(){
		
		return nextState;
	}

}
