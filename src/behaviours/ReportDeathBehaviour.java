package behaviours;

import java.io.IOException;

import mas.HunterAgent;
import mas.SerializationHelper;

import org.graphstream.graph.Node;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Signale la mort d'un agent
 * <br/>
 * <br/>Cette annonce est réalisé un certain nombre de fois toutes les 3 secondes
 */
public class ReportDeathBehaviour extends TickerBehaviour {
	private static final long serialVersionUID = -2009988805814997107L;
	private HunterAgent agent;
	
	public ReportDeathBehaviour (final HunterAgent myagent) {
		
		super(myagent, 3000);
		agent = myagent;
		System.out.println(agent.getLocalName() + " passe dans le constructeur de ReportDeath");
	}
	
	@Override
	protected void onTick() {
		if(agent.getPushLoss() > 0){
			System.out.println(agent.getLocalName() + " send " + agent.getPushLoss() + " message to inform the death of someone send to ");

			agent.decreasePushLoss();
			if(agent.hasPartners()){
				final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setProtocol("DeathOf");
				msg.setSender(agent.getAID());
				for(String aId:agent.getPartners()){
					msg.addReceiver(new AID(aId, AID.ISLOCALNAME));
				}
				try {
					msg.setContentObject(agent.getAllPartners());
				} catch (IOException e) {
					e.printStackTrace();
				}
				agent.send(msg);
				
			}
			else{
				stop();
			}
		}
		else{
			stop();
		}

	}

}