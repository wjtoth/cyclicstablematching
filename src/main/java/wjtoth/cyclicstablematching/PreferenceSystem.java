package wjtoth.cyclicstablematching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Class abstracts a problem instance for cyclic stable matching
 * 
 * @author wjtoth
 *
 */
public class PreferenceSystem {

	int[][][] preferences;
	int[][][] ranks;

	int numberOfGroups;
	int numberOfAgents;

	int extenderGroup;
	int extenderAgent;
	int length;

	int nextChoice;

	public PreferenceSystem(int numberOfGroups, int numberOfAgents) {

		this.numberOfAgents = numberOfAgents;
		this.numberOfGroups = numberOfGroups;

		preferences = new int[numberOfGroups][numberOfAgents][numberOfAgents];
		for (int i = 0; i < numberOfGroups; ++i) {
			for (int j = 0; j < numberOfAgents; ++j) {
				for (int k = 0; k < numberOfAgents; ++k) {
					preferences[i][j][k] = numberOfAgents;
				}
			}
		}

		ranks = new int[numberOfGroups][numberOfAgents][numberOfAgents];
		for (int i = 0; i < numberOfGroups; ++i) {
			for (int j = 0; j < numberOfAgents; ++j) {
				for (int k = 0; k < numberOfAgents; ++k) {
					ranks[i][j][k] = numberOfAgents;
				}
			}
		}

		extenderGroup = 0;
		extenderAgent = 0;
		length = 1;

		nextChoice = 0;
	}

	public boolean canExtend() {
		return nextChoice < numberOfAgents;
	}

	public PreferenceSystem extend() {
		preferences[extenderGroup][extenderAgent][length - 1] = nextChoice;
		ranks[extenderGroup][extenderAgent][nextChoice] = length-1;
		incrementIndicator();

		return this;
	}
	
	private void incrementIndicator() {
		extenderAgent = (extenderAgent + 1) % numberOfAgents;
		if(extenderAgent == 0) {
			extenderGroup = (extenderGroup + 1) % numberOfGroups;
		}
		if(extenderGroup == 0 && extenderAgent == 0) {
			++length;
		}
		nextChoice = 0;
		while(nextChoice < numberOfAgents && ranks[extenderGroup][extenderAgent][nextChoice] < numberOfAgents) {
			++nextChoice;
		}
	}
	
	public boolean hasParent() {
		return !(extenderGroup == 0 && extenderAgent == 0 && length == 1);
	}
	
	public PreferenceSystem parent() {
		decrementIndicator();
		int prevChoice = preferences[extenderGroup][extenderAgent][length-1];
		preferences[extenderGroup][extenderAgent][length-1] = numberOfAgents;
		ranks[extenderGroup][extenderAgent][prevChoice] = numberOfAgents;
		
		return this;
	}
	
	private void decrementIndicator() {
		extenderAgent = (extenderAgent - 1 + numberOfAgents) % numberOfAgents;
		if(extenderAgent == numberOfAgents - 1) {
			extenderGroup = (extenderGroup - 1 + numberOfGroups) % numberOfGroups;
		}
		if(extenderGroup == numberOfGroups - 1 && extenderAgent == numberOfAgents - 1) {
			--length;
		}
		nextChoice = preferences[extenderGroup][extenderAgent][length - 1];
		while(nextChoice < numberOfAgents &&  ranks[extenderGroup][extenderAgent][nextChoice] < numberOfAgents) {
			++nextChoice;
		}
	}
	
	public boolean hasNext() {
		return hasParent() || canExtend();
	}
	
	public PreferenceSystem next() {
		if(canExtend()) {
			return extend();
		} else if (hasParent()) {
			return parent();
		}
		return this;
	}
	
	public boolean isComplete() {
		return extenderGroup == 0 && extenderAgent == 0 && length == numberOfAgents+1;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < numberOfGroups; ++i) {
			sb.append("Group " + i + ":\n");
			for(int j = 0; j < numberOfAgents; ++j) {
				sb.append(j + ": ");
				sb.append(Arrays.toString(preferences[i][j]));
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	
}
