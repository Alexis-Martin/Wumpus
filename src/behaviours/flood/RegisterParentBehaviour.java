package behaviours.flood;

import mas.HunterAgent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.MessageTemplate;

public class RegisterParentBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = -5673786944992246974L;
	private final long timeout = 100; //ms
	private boolean finished = false;
	private HunterAgent agent;
	private String protocol;
	
	public RegisterParentBehaviour(HunterAgent agent, String protocol){
		super(agent);
		this.agent = agent;
		this.protocol = protocol;
	}

	@Override
	public void action() {
		//lire message avec msgTemplate
		//enregistre le parent
		//lance transmitflood avec nouveau msgTemplate (meme protocole en PROPAGATE)
	}

	@Override
	public boolean done() {
		return finished;
	}

}
