package behaviours.flood;

import mas.HunterAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 * Behaviour qui lit les messages de flood. Si on est dans aucun flood et qu'on a rien à faire, on répond et on lance la behaviour RegisterParent.
 */
public class CatchFloodBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = -583348112306128893L;
	private boolean finished = false;
	private HunterAgent agent;
	
	public CatchFloodBehaviour(HunterAgent agent){
		super(agent);
		this.agent = agent;
	}

	@Override
	public void action() {
		//lit les messages propagate avec comme protocol "flood"
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE), MessageTemplate.MatchProtocol("flood"));
		final ACLMessage msg = agent.receive(msgTemplate);
		if (msg != null) {
			//si on est en train de faire quelque chose on ne répond pas
			if(agent.isWaitingFollower() || agent.isOnExploration())
				return;
			
			//sinon on rentre en standby 
			this.agent.setStandBy(true); 
			Flood flood;
			try {
				flood = (Flood) msg.getContentObject();
				int capacity = agent.getCapacity();
				int quantity = capacity - agent.getBackPackFreeSpace();
				flood.setAttribute("capacity", capacity);
				flood.setAttribute("quantity", quantity);
				//si on est pas déjà dans un flood et qu'on a un chemin pour aller rejoindre notre père on accepte le flood
				if(!agent.isInAFlood() && !agent.getMap().goTo(agent.getCurrentPosition(), flood.getParentPos()).isEmpty()){
					System.out.println(agent.getLocalName() + " (" + quantity + "/" + capacity + ") in the flood "+flood.getId());
					this.agent.addFlood(flood.getId(), flood);
					
				}
				//sinon on refuse le flood
				else{
					return;
				}
				
				//si tout va bien on répond en envoyant un message agree
				final ACLMessage msgSend = new ACLMessage(ACLMessage.AGREE);
				msgSend.setProtocol(flood.getId());
				msgSend.setSender(this.agent.getAID());
				msgSend.addReceiver(new AID(flood.getParentId(), AID.ISLOCALNAME));
				agent.sendMessage(msgSend);
				
				agent.addBehaviour(new RegisterParentBehaviour(agent, flood.getId()));
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		}else{
			block();
		}
	}

	@Override
	public boolean done() {
		return finished;
	}

}
