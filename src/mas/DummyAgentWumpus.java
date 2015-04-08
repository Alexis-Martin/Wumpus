package mas;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.graphstream.graph.Node;

import env.Attribute;
import env.Environment;
import env.Environment.Couple;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;


public class DummyAgentWumpus extends abstractAgent{


	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1 set the agent attributes 
	 *	 		2 add the behaviours
	 *          
	 */
	protected void setup(){

		super.setup();

		//get the parameters given into the object[]
		final Object[] args = getArguments();
		if(args[0]!=null){
			realEnv = (Environment) args[0];
			realEnv.deployWumpus(this.getLocalName());

		}else{
			System.out.println("Erreur lors du tranfert des parametres");
		}

		//Add the behaviours
		//addBehaviour(new RandomWalkBehaviour(this,realEnv));

		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}

	/**
	 * This method is automatically called after doDelete()
	 */
	protected void takeDown(){

	}


	/**************************************
	 * 
	 * 
	 * 				BEHAVIOURS
	 * 
	 * 
	 **************************************/


	public class RandomWalkBehaviour extends TickerBehaviour{
		/**
		 * When an agent choose to move
		 *  
		 */
		private static final long serialVersionUID = 9088209402507795289L;

		private boolean finished=false;
		private Environment realEnv;

		public RandomWalkBehaviour (final Agent myagent,Environment realEnv) {
			super(myagent, 5000);
			this.realEnv=realEnv;


		}

		protected void onTick() {

			String myPosition=getCurrentPosition();
			if (myPosition!=""){
				List<Couple<String,List<Attribute>>> lobs=observe(myPosition);
			
				Random r= new Random();

				int moveId=r.nextInt(lobs.size());

				
				move(myPosition, lobs.get(moveId).getL());
			}

		}

	}


}
