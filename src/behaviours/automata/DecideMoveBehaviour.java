package behaviours.automata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import mas.HunterAgent;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleNode;

import jade.core.behaviours.OneShotBehaviour;

public class DecideMoveBehaviour extends OneShotBehaviour {
	private static final long serialVersionUID = -8702619088303808521L;
	private HunterAgent agent;
	
	public DecideMoveBehaviour(HunterAgent agent){
		super(agent);
		this.agent = agent;
	}
	
	@Override
	public void action() {
		Node pos = agent.getMap().getNode(agent.getCurrentPosition());
		HashMap<String, Double> possible = new HashMap<String, Double>();
		Iterator<SingleNode> nexts = pos.getNeighborNodeIterator();
		Random rand = new Random();
		
		while(nexts.hasNext()){
			Node next = nexts.next();
			if(this.agent.getMap().getWell(next.getId()) < 3)
				possible.put(next.getId(), agent.getMap().getMoveUtility(pos.getId(), next.getId()));
			if(agent.isRandomWalk()){
				double r = rand.nextDouble();
				if(r > 0.8){
					agent.setNextMove(next.getId());
					return;
				}
			}
		}
		
		Collection<Double> utilities = possible.values();
		double maxU = Collections.max(utilities);
		List<String> choices = new ArrayList<String>();
		for(String id:possible.keySet()){
			if(possible.get(id) == maxU){
				choices.add(id);
			}
		}
		Collections.shuffle(choices);
		agent.setNextMove(choices.get(0));
	}

}
