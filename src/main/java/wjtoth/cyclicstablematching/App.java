package wjtoth.cyclicstablematching;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		final int NUMBER_OF_GROUPS = 3;
		final int NUMBER_OF_AGENTS = 5;

		Scanner scanner = new Scanner(System.in);
		System.out.println("Check System?(y/n)");
		char checkSystem = scanner.nextLine().toLowerCase().charAt(0);
		if (checkSystem == 'y') {
			System.out.println("Input system to test:");
			checkSystem(scanner);
		} else {
			System.out.println("Performing Search");
			spaceSearch(NUMBER_OF_GROUPS, NUMBER_OF_AGENTS);
		}

	}

	private static void checkSystem(Scanner scanner) {
		int numberOfGroups = scanner.nextInt();
		int numberOfAgents = scanner.nextInt();
		PreferenceSystem preferenceSystem = new PreferenceSystem(numberOfGroups, numberOfAgents);
		for (int i = 0; i < numberOfGroups; ++i) {
			Group group = new Group(numberOfAgents, numberOfAgents, i);
			for (int j = 0; j < numberOfAgents; ++j) {
				Agent agent = new Agent(numberOfAgents, j, i);
				int[] preferences = new int[numberOfAgents];
				for (int k = 0; k < numberOfAgents; ++k) {
					int partner = scanner.nextInt();
					preferences[partner] = numberOfAgents - k;
				}
				agent.setPreferences(preferences);
				group.setGroupAgent(j, agent);
			}
			preferenceSystem.setSystemGroup(i, group);
		}
		System.out.println(preferenceSystem);
		System.out.println("Matchings to check (input -1 to check all possible):");
		int numMatchingsToCheck = scanner.nextInt();
		System.out.println("Read in matchingsToCheck: " + numMatchingsToCheck);
		StabilityChecker stabilityChecker = new StabilityChecker(numberOfAgents, numberOfGroups);
		stabilityChecker.setPreferenceSystem(preferenceSystem);
		System.out.println("Constructed checker");
		if (numMatchingsToCheck == -1) {
			System.out.println("Checking all possible");
			stabilityChecker.setPreferenceSystem(preferenceSystem);
			System.out.println(stabilityChecker.loudHasStableMatch());

		} else {
			ArrayList<PerfectMatching> matchingsToCheck = new ArrayList<PerfectMatching>();
			for (int i = 0; i < numMatchingsToCheck; ++i) {
				PerfectMatching perfectMatching = new PerfectMatching(numberOfGroups, numberOfAgents);
				ArrayList<int[]> matching = new ArrayList<int[]>();
				for (int j = 0; j < numberOfAgents; ++j) {
					int[] match = new int[numberOfGroups];
					for (int k = 0; k < numberOfGroups; ++k) {
						match[k] = scanner.nextInt();
					}
					matching.add(match);
				}
				perfectMatching.setMatching(matching);
				matchingsToCheck.add(perfectMatching);
			}
			for (PerfectMatching perfectMatching : matchingsToCheck) {
				System.out.println("Checking matching:");
				System.out.println(perfectMatching);
				System.out.println("Is Stable : " + stabilityChecker.isStable(perfectMatching));
			}
		}
		System.out.println("Done checking");
	}

	private static void spaceSearch(final int NUMBER_OF_GROUPS, final int NUMBER_OF_AGENTS)
			throws InterruptedException, ExecutionException {

		List<PreferenceSystem> toCheckQueue = new LinkedList<PreferenceSystem>();
		List<PreferenceSystem> toExtendQueue = new LinkedList<PreferenceSystem>();
		PreferenceSystem initialPreferenceSystem = new PreferenceSystem(NUMBER_OF_GROUPS, NUMBER_OF_AGENTS);

		toCheckQueue.add(initialPreferenceSystem);

		System.out.println("Constructing Stability Checker");

		StabilityChecker stabilityChecker = new StabilityChecker(NUMBER_OF_AGENTS, NUMBER_OF_GROUPS);

		System.out.println("Done constructing Stability Checker");

		int sizeCount = 0;
		
		while (!toCheckQueue.isEmpty() || !toExtendQueue.isEmpty()) {
			if (toCheckQueue.isEmpty()) {
				System.out.println("Extending");
				while (toExtendQueue.size() > 0) {
					PreferenceSystem preferenceSystem = toExtendQueue.remove(0);
					if(preferenceSystem.size() > NUMBER_OF_AGENTS*sizeCount) {
						System.out.println("ToCheckQueue Size:" + toCheckQueue.size());
						System.out.println("ExtensionQueue Size: " + toExtendQueue.size());
						System.out.println(preferenceSystem);
						System.out.println(preferenceSystem.computeHash());
						++sizeCount;
					}
					List<PreferenceSystem> extensions = preferenceSystem.extend();
					if (extensions.size() == 0) {

						break;
					}
					toCheckQueue.addAll(extensions);
				}
			}
			if (!toCheckQueue.isEmpty()) {
				List<List<PreferenceSystem>> extensions = processInputs(toCheckQueue, stabilityChecker);
				for (List<PreferenceSystem> extension : extensions) {
					toExtendQueue.addAll(extension);
				}
			}
		}
		System.out.println("DONE!");
	}

	/*
	 * *Method to Parallelize Computation
	 */
	public static List<List<PreferenceSystem>> processInputs(List<PreferenceSystem> inputs,
			StabilityChecker stabilityChecker) throws InterruptedException, ExecutionException {

		System.out.println("ProcessingInputs");
		
		int threads = Runtime.getRuntime().availableProcessors();
		ExecutorService service = Executors.newFixedThreadPool(threads);

		List<Future<List<PreferenceSystem>>> futures = new ArrayList<Future<List<PreferenceSystem>>>();
		for (final PreferenceSystem input : inputs) {
			Callable<List<PreferenceSystem>> callable = new Callable<List<PreferenceSystem>>() {
				public List<PreferenceSystem> call() throws Exception {
					List<PreferenceSystem> output = processPreferenceSystem(input, stabilityChecker);
					return output;
				}
			};
			futures.add(service.submit(callable));
		}

		service.shutdown();

		List<List<PreferenceSystem>> outputs = new ArrayList<List<PreferenceSystem>>();
		for (Future<List<PreferenceSystem>> future : futures) {
			outputs.add(future.get());
		}
		inputs.clear();
		return outputs;
	}

	private static List<PreferenceSystem> processPreferenceSystem(PreferenceSystem preferenceSystem,
			StabilityChecker stabilityChecker) {
		// preferenceSystem.sortPreferences();
		stabilityChecker.setPreferenceSystem(preferenceSystem);
		if (!stabilityChecker.hasStableMatch()) {
			if (preferenceSystem.size() == preferenceSystem.getNumberOfGroups()
					* preferenceSystem.getNumberOfAgents()) {
				System.out.println("Found Counter Example!");
				stabilityChecker.setPreferenceSystem(preferenceSystem);
				System.out.println(stabilityChecker.loudHasStableMatch());
				System.out.println(preferenceSystem);
				return new ArrayList<PreferenceSystem>();
			}
			return preferenceSystem.extend();
		}

		return new ArrayList<PreferenceSystem>();
	}
}
