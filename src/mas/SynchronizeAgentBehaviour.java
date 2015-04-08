package mas;

import jade.core.behaviours.OneShotBehaviour;

public class SynchronizeAgentBehaviour extends OneShotBehaviour {
	private static final long serialVersionUID = -8641051938876748981L;
	private HunterAgent agent;
	
	public SynchronizeAgentBehaviour(HunterAgent a) {
		super(a);
		this.agent = a;
	}

	@Override
	public void action() {
		System.out.println(agent.getLocalName()+" start its automota");
	}

}
