package mas;

import java.util.Iterator;
import java.util.List;

import env.Attribute;
import env.Environment;
import env.Environment.Couple;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class abstractAgent extends Agent {

	/**
	 * The environment topology is used to compute the reachable agents.
	 * For now, they all share the same communication range.
	 */
	private static final int COMMUNICATION_REACH=5;
	
	protected Environment realEnv;


	/**
	 * 
	 * @return The agent current position in the environment
	 */
	public String getCurrentPosition(){
		return this.realEnv.getCurrentPosition(this.getLocalName());
	}

	/**
	 * 
	 * @param nodeId my current position
	 * @return the available observations, null if the given position does not correspond to the one 
	 * of the agent.
	 */
	public List<Couple<String,List<Attribute>>> observe(String nodeId){
		return this.realEnv.observe(nodeId,this.getLocalName());
	}

	/**
	 * 
	 * @param myPosition the agent current position's ID in the environment
	 * @param myDestination the targeted nodeId
	 * @return true is the move is legit and done, false otherwise
	 */
	public boolean move(String myPosition, String myDestination){
		return this.realEnv.move(this,this.getLocalName(),myPosition,myDestination);
	}

	/**
	 * 
	 * @return the amount of wealth that the agent was able to pick. 0 if there is no treasure at this place or if the agent's 
	 * 	carrying capacity is already reached.
	 */
	public int pick(){
		return this.realEnv.pick(this.getLocalName(),getCurrentPosition());
	}

	/**
	 * 
	 * @return the available carrying capacity of the agent 
	 */
	public int getBackPackFreeSpace(){
		return this.realEnv.getBackPackFreeSpace(this.getLocalName());
	}


	/**
	 * This method must be used instead of the method JADE.core.Agent.send()  in order for the platform to control 
	 * the reach of the communications.
	 * @param msg the message to send
	 */
	//TODO wait for JadeIterator Update then clear the receiverNumber
	public void sendMessage(ACLMessage msg){

		//filter reachable agents
		Iterator<AID> iter=msg.getAllReceiver();
		int receiverNumber=0;
		
		while (iter.hasNext()){
			AID receiverAID= iter.next();
			receiverNumber++;
			if (!this.realEnv.isReachable(msg.getSender().getLocalName(),receiverAID.getLocalName(),COMMUNICATION_REACH)){
				iter.remove();
				receiverNumber--;
			}
		}

		if (receiverNumber>0){
			super.send(msg);
		}else{
			System.out.println("No agent within reach");
		}
	}

	

}

