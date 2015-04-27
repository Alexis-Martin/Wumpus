package behaviours.automata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import mas.HunterAgent;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleNode;

import jade.core.behaviours.OneShotBehaviour;
/**
 * Fait parti du comportement de base du HunterAgent.
 * 
 *  Permet de choisir le prochain déplacement du HunterAgent
 */
public class DecideMoveBehaviour extends OneShotBehaviour {
	private static final long serialVersionUID = -8702619088303808521L;
	private HunterAgent agent;
	private int nextState;//sortie de l'état
	
	public DecideMoveBehaviour(HunterAgent agent){
		super(agent);
		this.agent = agent;
	}
	
	@Override
	public void action() {
		nextState = 0;
		
		//met à jour les puits 
		agent.getMap().updateWell(agent.getCurrentPosition(), agent.observe(agent.getCurrentPosition()));
		
		
		Node pos = agent.getMap().getNode(agent.getCurrentPosition());
		HashMap<String, Double> possible = new HashMap<String, Double>();
		Iterator<SingleNode> nexts = pos.getNeighborNodeIterator();
		Random rand = new Random();
		double maxU = 0;
		
		//pour tous les voisin
		while(nexts.hasNext()){
			Node next = nexts.next();
			//on get leur utilité
			double u = agent.getMap().getMoveUtility(pos.getId(), next.getId());
			//si l'utilité est > 0, déplacement possible sinon puit ou risque de puit
			if(u > 0){
				possible.put(next.getId(), u);
				if(u > maxU){
					maxU = u;
				}
			}
			//si on fait du random walk
			if(agent.isRandomWalk()){
				double r = rand.nextDouble();
				if(agent.getMap().getMoveUtility(pos.getId(), next.getId()) == 0){
					continue;
				}
				//on a 0.2% de chance de choisir ce déplacement
				if(r > 0.8){
					agent.setNextMove(next.getId());
					return;
				}
			}
		}
		
		//si il n'y a aucun déplacmeent possible (cas de début entouré de vent) 
		if(possible.isEmpty()){
			System.out.println(agent.getLocalName()+" in "+agent.getCurrentPosition()+" can't move anywhere");
			//attendre un peut puis lancer RiskBehaviour
			
			nextState = 2;
			//on sort de l'état en allant dans l'état stand by et on envoie notre map
			agent.setStandBy(true);
			agent.setSinglePush(true);
			return;
		}
		
		List<String> choices = new ArrayList<String>();
		for(String id:possible.keySet()){
			if(possible.get(id) == maxU){
				choices.add(id);
			}
		}
		Collections.shuffle(choices);
		agent.setNextMove(choices.get(0));
	}
	
	@Override
	public int onEnd(){
		return this.nextState;
	}

}
