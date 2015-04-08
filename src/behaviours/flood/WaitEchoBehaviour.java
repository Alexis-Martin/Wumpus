package behaviours.flood;

import mas.HunterAgent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class WaitEchoBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = 7472093251688148340L;
	private HunterAgent agent;
	private boolean finished = false;
	private MessageTemplate msgTemplate;
	
	public WaitEchoBehaviour(HunterAgent agent, MessageTemplate template){
		super(agent);
		this.agent = agent;
		this.msgTemplate = template;
	}
	
	@Override
	public void action() {
		final ACLMessage msg = this.agent.receive(this.msgTemplate);
		if(msg != null){
			//traiter le reception
			System.out.println(this.agent.getLocalName()+" received an echo");
			
			//
			
			finished = true;
		}else{
			block();
		}
	}

	@Override
	public boolean done() {
		return finished;
	}

}
