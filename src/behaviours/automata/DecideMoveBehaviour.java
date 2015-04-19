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
		double maxU = 0;
		
		while(nexts.hasNext()){
			Node next = nexts.next();
			double u = agent.getMap().getMoveUtility(pos.getId(), next.getId());
			if(u > 0){
				possible.put(next.getId(), u);
				if(u > maxU){
					maxU = u;
				}
			}
			
			if(agent.isRandomWalk()){
				double r = rand.nextDouble();
				if(agent.getMap().getMoveUtility(pos.getId(), next.getId()) == 0){
					continue;
				}
				if(r > 0.8){
					agent.setNextMove(next.getId());
					return;
				}
			}
		}
		
		if(possible.isEmpty()){
			System.out.println(agent.getLocalName()+" in "+agent.getCurrentPosition()+" can't move anywhere");
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

}
