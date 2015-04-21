package mas;

import java.util.List;

import env.Attribute;
import env.Environment.Couple;
import jade.core.behaviours.OneShotBehaviour;

public class SynchronizeAgentBehaviour extends OneShotBehaviour {
	private static final long serialVersionUID = -8641051938876748981L;
	private HunterAgent agent;
	
	public SynchronizeAgentBehaviour(HunterAgent a) {
		super(a);
		this.agent = a;
	}

	@Override
	public void action() {
		System.out.println(agent.getLocalName()+" start its automota");
		agent.setCapacity(agent.getBackPackFreeSpace());
		System.out.println(agent.getLocalName()+ " capacity " + agent.getCapacity());
		boolean surrounded = true;
		Couple<String,List<Attribute>> localObs = null;
		String myPosition = agent.getCurrentPosition();
		List<Couple<String,List<Attribute>>> lobs = agent.observe(myPosition);
		for(Couple<String,List<Attribute>> c : lobs){
			String rId = c.getL();
			if(rId.equals(myPosition)){
				localObs = c;
				continue;
			}
			boolean wind = false;
			for(Attribute attr : c.getR()){
				if(attr.getName().equals("Wind")){
					wind = true;
				}
			}
			if(!wind){
				surrounded = false;
				break;
			}
		}
		
		if(surrounded){
			System.out.println(agent.getLocalName()+" is surrounded");
			for(Attribute attr : localObs.getR()){
				if(attr.getName().equals("Wind")){
					agent.getMap().getNode(myPosition).addAttribute("well#", 2);
					agent.getMap().updateLayout(agent.getMap().getNode(myPosition));
				}
			}
		}
	}

}
