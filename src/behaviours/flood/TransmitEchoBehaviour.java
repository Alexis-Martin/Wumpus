package behaviours.flood;

import mas.HunterAgent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.MessageTemplate;

public class TransmitEchoBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = -6013275079546582660L;
	private boolean finished = false;
	private HunterAgent agent;
	private MessageTemplate msgTemplate;
	
	public TransmitEchoBehaviour(HunterAgent agent, MessageTemplate msgTemplate){
		super(agent);
		this.agent = agent;
		this.msgTemplate = msgTemplate;
	}
	
	@Override
	public void action() {
		//construit message a partir de l'objet flood de l'agent
		//envoit au pere (enregistre dans l'objet flood de l'agent)
		//lance la reception du resultat
		//finshed
	}

	@Override
	public boolean done() {
		return finished;
	}

}
