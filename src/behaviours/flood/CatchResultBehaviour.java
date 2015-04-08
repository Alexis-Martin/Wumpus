package behaviours.flood;

import mas.HunterAgent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.MessageTemplate;

public class CatchResultBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = -2417706979155315948L;
	private boolean finished = false;
	private HunterAgent agent;
	private MessageTemplate msgTemplate;
	
	public CatchResultBehaviour(HunterAgent agent, MessageTemplate msgTemplate){
		super(agent);
		this.agent = agent;
		this.msgTemplate = msgTemplate;
	}
	
	@Override
	public void action() {
		//lit le message de resultat
		//si rejete
			//dismiss son fils
		//si accepte
			//si enfant:
				//envoit accept au fils
				//maj objet flood
			//si pas enfant:
				//met a jour sa destination
		//fin du protocole
		//standby = false
		//finished = true;

	}

	@Override
	public boolean done() {
		return finished;
	}

}
