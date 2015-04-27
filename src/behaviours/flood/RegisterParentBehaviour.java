package behaviours.flood;

import mas.HunterAgent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
/**
 * On enregistre notre parent dès que l'on à un ack de sa part. Si au bout de 300 ms on a aucune réponse, on part
 */
public class RegisterParentBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = -5673786944992246974L;
	private final long timeout = 300; //ms
	private boolean finished = false;
	private HunterAgent agent;
	private String protocol;
	private long begin;
	private boolean receive;
	
	public RegisterParentBehaviour(HunterAgent agent, String protocol){
		super(agent);
		this.agent = agent;
		this.protocol = protocol;
		this.begin = System.currentTimeMillis();
		this.receive = false;
	}

	@Override
	public void action() {
		//lit les messages de type confirm avec le protocol : le nom du flood
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM), MessageTemplate.MatchProtocol(this.protocol));
		final ACLMessage msg = agent.receive(msgTemplate);
		
		if(msg != null){
			//si on a recu le message, on transmet le flood pour contacter des agents qui seraient proche de nous
			agent.addBehaviour(new TransmitFloodBehaviour(agent, protocol));
			finished = true;
			receive = true;
		}else{
			long t = (this.timeout + this.begin) - System.currentTimeMillis();
			if(t > 0){
				block(t);
			}else{
				this.finished = true;
			}
		}
	}

	@Override
	public boolean done() {
		//Si le temps est écoulé mais qu'on a pas eu de réponse, on enlève le flood et on part
		if(!receive && finished){
			this.agent.removeFlood(protocol);
			this.agent.setStandBy(false);
		}
		return finished;
	}

}
