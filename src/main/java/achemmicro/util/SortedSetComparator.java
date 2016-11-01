package achemmicro.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

/**
 * Comparator for sorted sets of objects that compares the sorted sets by size, 
 * then by comparing each element
 * 
 * @author Adam Faulconbridge
 *
 * @param <T> the type of the object within the sets to be compared
 */
public class SortedSetComparator<T extends Comparable<T>> implements Comparator<SortedSet<T>>{

	@Override
	public int compare(SortedSet<T> set1, SortedSet<T> set2) {
        // compare should return < 0 if set1 is supposed to be
        // less than set2, > 0 if set1 is supposed to be greater than 
        // set2 and 0 if they are supposed to be equal
		//compare by size
		if (set1.size() < set2.size()) {
			return -1;
		} else if (set1.size() > set2.size()) {
			return 1;
		} else {
			//same size
			//iterate over each set
			//since they are sorted set this will be in consistent order
			Iterator<T> set1Iter = set1.iterator();
			Iterator<T> set2Iter = set2.iterator();
			while (set1Iter.hasNext() && set2Iter.hasNext()) {
				T set1Next = set1Iter.next();
				T set2Next = set2Iter.next();
				int cmp = set1Next.compareTo(set2Next);
				if (cmp != 0) {
					return cmp;
				}
			}
		}
		//same things in the same order, so sets are the same
		return 0;
	}

}
