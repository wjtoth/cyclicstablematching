package wjtoth.cyclicstablematching;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Permutations {

	public static ArrayList<PermutationArray> permutations(int n) {
		return permutations(array(n));
	}

	public static List<PermutationArray> permutationsOfAllSubsets(int n) {
		return permutationsOfAllSubsets(array(n));
	}

	public static int[] array(int n) {
		int[] retval = new int[n];
		for(int i = 0; i<n;++i) {
			retval[i] = i;
		}
		return retval;
	}

	public static List<PermutationArray> permutationsOfAllSubsets(int[] array) {
		int subsets = (int)Math.pow(2, array.length);
		int[] indices = new int[array.length];

		ArrayList<PermutationArray> retval = new ArrayList<>();

		for(int k = 1; k<subsets; ++k) {
			int[] subset = new int[array.length];
			for(int i = 0; i<indices.length;++i) {
				int include = (k >> i) % 2;
				if(include == 1) {
					subset[i] = array[i];
				} else{
					subset[i] = -1;
				}
			}
			retval.addAll(permutations(subset));
		}

		return retval.stream().distinct().collect(Collectors.toList());
	}

	public static ArrayList<PermutationArray> permutations(int[] array) {
	    ArrayList<PermutationArray> retval = new ArrayList<>();
	    if (array.length == 1) {
	        retval.add(new PermutationArray(array));
	    } else if (array.length > 1) {
	        int lastIndex = array.length - 1;
	        int last = array[lastIndex];
	        int[] rest = new int[array.length-1];
	        for(int i = 0; i<rest.length; ++i) {
	        	rest[i] = array[i];
	        }
	        retval = merge(permutations(rest), last);
	    }
	    return retval;
	}
	
	public static ArrayList<PermutationArray> merge (ArrayList<PermutationArray> list, int addend) {
		ArrayList<PermutationArray> retval = new ArrayList<>();
		for(PermutationArray prev : list) {
			for(int i = 0; i<prev.getArray().length+1; ++i) {
				int[] next = new int[prev.getArray().length+1];
				for(int j = 0; j<i; ++j) {
					next[j] = prev.getArray()[j];
				}
				next[i] = addend;
				for(int j = i+1; j<next.length; ++j) {
					next[j] = prev.getArray()[j-1];
				}
				retval.add(new PermutationArray(next));
			}
		}
		return retval;
	}

}
