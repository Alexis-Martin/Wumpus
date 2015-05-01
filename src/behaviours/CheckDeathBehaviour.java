package behaviours;

import java.util.HashMap;
import java.util.List;

import mas.HunterAgent;
import mas.Map;
import mas.SerializationHelper;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 *Récupère la liste des agents morts recemment
 *<br/>
 *<br/>Si il y a eu du changement, transmet cette liste 5 fois toutes les 3 secondes
 *
 */
public class CheckDeathBehaviour extends SimpleBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -487112501153551789L;
	private boolean finished=false;
	private HunterAgent agent;
	
	public CheckDeathBehaviour(final HunterAgent myagent) {
		super(myagent);
		this.agent = myagent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		//1) receive the message
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM), MessageTemplate.MatchProtocol("DeathOf"));
		

		final ACLMessage msg = agent.receive(msgTemplate);
		if (msg != null) {
			HashMap<String, Boolean> team;
			String deadMan = null;
			try {
				team = (HashMap<String, Boolean>) msg.getContentObject();
				boolean lossPartner = false;
				for(String partner : agent.getAllPartners().keySet()){
					if(team.containsKey(partner) && !team.get(partner).equals(agent.getAllPartners().get(partner))){
						lossPartner = true;
						agent.setPartner(partner, team.get(partner));
						deadMan = partner;
					}
				}
				
				if(lossPartner){
					System.out.println(agent.getLocalName() + " learn the death of "+deadMan);
					//agent.pushLoss(5);
					//agent.addBehaviour(new ReportDeathBehaviour(agent));
				}
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