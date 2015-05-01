package behaviours.automata;

import java.util.List;

import org.graphstream.graph.Node;

import env.Attribute;
import env.Environment.Couple;
import mas.HunterAgent;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Etat de l'automate qui correspond au comportement déclenché par l'élection du riskFlood
 * 
 * l'objectif de cette Behaviour est de se déplacer jusqu'à la position ou il faut prendre un risque et aller dans l'état de l'automate ou on va lancer le flood pour trouver le collègue qui vas nous couvrir
 */
public class ExploreBehaviour extends OneShotBehaviour{
	private HunterAgent agent;
	private int nextState;
	private int try_move;
	
	public ExploreBehaviour (HunterAgent a){
		super(a);
		this.agent = a;
		try_move = 0;
	}
	
	
	@Override
	public void action() {
		
		String myPosition = agent.getCurrentPosition();
		String nextMove = agent.popStackMove();
		//on observe notre environement
		List<Couple<String,List<Attribute>>> lobs = agent.observe(myPosition);
		boolean canMove = false;
		
		nextState = 0;
		//si on a aucun mouvement a faire (on est arrivé, on sort de l'état par la sortie standard)
		if(nextMove == null){
			System.out.println(agent.getLocalName()+" arrived in "+myPosition + " and search for a follower");
			agent.setWaitFollower(true);
			agent.setRisk(false);
			nextState = 1;
			return;
		}
		//on met la case courante comme visité dans notre représentation du monde
		agent.getMap().getNode(myPosition).setAttribute("visited?", true);
		//on traite notre environnement
		for(Couple<String,List<Attribute>> c:lobs){
			String pos = c.getL();
			if(pos.equals(myPosition)){
				Node n = agent.getMap().addRoom(pos, true, c.getR());
				agent.getDiff().addRoom(n);
				agent.getMap().updateLayout(n, true);
				continue;
			}
			 
			if(pos.equals(nextMove)){
				canMove = true;
			}
			Node n = agent.getMap().addRoom(pos, false, c.getR());
			agent.getDiff().addRoom(n);
			if(agent.getMap().addRoad(myPosition, pos)){
				agent.getDiff().addRoad(agent.getMap().getEdge(agent.getMap().getEdgeId(myPosition, pos)));
			}
		}
		//si le prochain mouvement existe dans notre mouvement on se déplace
		if(canMove){
			if(!agent.move(myPosition, nextMove)){
				System.out.println(agent.getLocalName()+" waiting for room "+nextMove+" to be released");
				agent.getStackMove().add(0, nextMove);
				
				if(try_move == 10){
					agent.setRisk(false);
					agent.getStackMove().clear();
					nextState = 2;
					System.out.println(agent.getLocalName()+" abandonne sa prise de risque");
					return;
				}
				try_move++;
			}
			else 
				try_move = 0;

		}
		//sinon on affiche une erreur et on repart dans le comportement de base
		else{
			System.out.println("Error : room " + nextMove+" is not in the neighborhood of agent "+agent.getLocalName()+ " ("+agent.getCurrentPosition()+")");
			agent.setRisk(false);
			agent.getStackMove().clear();
			nextState = 2;
			return;
		}
		//block(1500);
		
		/*
		nextState = 1;
		System.out.println("which is going to end up in is own death");
		System.out.println("...");
		System.out.println("probably");
		agent.setRisk(false);
		*/
	}

	@Override
	public int onEnd() {
		return nextState;
	}
}
