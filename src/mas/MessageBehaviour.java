package mas;

import jade.core.behaviours.OneShotBehaviour;

public class MessageBehaviour extends OneShotBehaviour {
	private static final long serialVersionUID = 1265712850480322775L;
	private HunterAgent agent;
	
	public MessageBehaviour(HunterAgent a) {
		super(a);
		this.agent = a;
	}

	@Override
	public void action() {
		System.out.println(agent.getLocalName() + " end its automata");
	}

}
