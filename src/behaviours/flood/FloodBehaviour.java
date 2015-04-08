package behaviours.flood;

import java.io.IOException;
import java.util.HashMap;

import mas.HunterAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class FloodBehaviour extends SimpleBehaviour {

	private boolean finished = false;
	private HunterAgent agent;
	
	public FloodBehaviour(HunterAgent agent){
		this.agent = agent;
	}
	
	@Override
	public void action() {
		System.out.println(agent.getLocalName()+" start a flood in position "+agent.getCurrentPosition());
/*
		HunterAgent a = ((HunterAgent)getAgent());
		String send = "tresor! " + a.getTresor();
		
		//Create a message in order to send it to the choosen agent
		final ACLMessage msg = new ACLMessage(ACLMessage.PROPAGATE);
		msg.setSender(this.myAgent.getAID());		
		
		
		for(String partner : a.getPartners()){
			msg.addReceiver(new AID(partner, AID.ISLOCALNAME)); // hardcoded= bad, must give it with objtab
		}
		try {
			msg.setContentObject(send);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.myAgent.send(msg);
		
		while(le temps){
			
			final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
		
	

			final ACLMessage msg = this.myAgent.receive(msgTemplate);
			if (msg != null) {		
			//	System.out.println("<----Message received from "+msg.getSender()+" ,content= "+msg.getContent());
				try {
					ajout enfant list
					send "i'm your father"
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			checkwumpus
			tempo
		}
		if(list vide)
			rien faire (pere)
			envoyer capacité
		else	
			echoBehaviour(list);
	*/
	}

	@Override
	public boolean done() {
		return this.finished;
	}
}
