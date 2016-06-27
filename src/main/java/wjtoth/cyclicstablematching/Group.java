package wjtoth.cyclicstablematching;
import java.util.ArrayList;

public class Group {

	private int groupSize;
	private int partnerGroupSize;
	private ArrayList<Agent> agents;

	public Group(int groupSize, int partnerGroupSize) {
		this.groupSize = groupSize;
		this.partnerGroupSize = partnerGroupSize;
		initializeAgents();
	}
	
	public void setGroupAgent(int agentIndex, Agent agent) {
		agents.set(agentIndex, agent);
	}
	
	public int getGroupSize() {
		return groupSize;
	}

	public int getPartnerGroupSize() {
		return partnerGroupSize;
	}
	
	public ArrayList<Agent> getAgents() {
		return agents;
	}

	public void setAgents(ArrayList<Agent> agents) {
		this.agents = agents;
	}

	private void initializeAgents() {
		agents = new ArrayList<Agent>(groupSize);
		for(int i = 0; i<groupSize; ++i) {
			Agent agent = new Agent(partnerGroupSize);
			agents.add(agent);
		}
	}
	
	public int sumAcceptablePartnerCount(){
		int sum = 0;
		for(Agent agent : agents) {
			sum += agent.getAcceptablePartnerCount();
		}
		return sum;
	}
	
	public Group deepCopy() {
		Group group = new Group(groupSize, partnerGroupSize);
		for(int i = 0; i<agents.size(); ++i) {
			Agent agentCopy = agents.get(i).deepCopy();
			group.setGroupAgent(i, agentCopy);
		}
		return group;
	}
	
	public Agent shortestAgent() {
		Agent shortest = agents.get(0);
		for(int i = 1; i<agents.size(); ++i) {
			Agent candidate = agents.get(i);
			if (candidate.getAcceptablePartnerCount() < shortest.getAcceptablePartnerCount()) {
				shortest = candidate;
			}
		}
		return shortest;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i<agents.size(); ++i) {
			sb.append(i+": ");
			sb.append(agents.get(i).toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}