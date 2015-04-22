package behaviours.automata;

import mas.HunterAgent;

import jade.core.behaviours.OneShotBehaviour;

public class ExploreBehaviour extends OneShotBehaviour{
	private HunterAgent agent;
	private int nextState;
	
	public ExploreBehaviour (HunterAgent a){
		super(a);
		this.agent = a;
	}
	
	
	@Override
	public void action() {
		nextState = 1;
		System.out.println("which is going to end up in is own death");
		System.out.println("...");
		System.out.println("probably");
		agent.setRisk(false);
	}

	@Override
	public int onEnd() {
		return nextState;
	}
}
