/*
 * Added by haichen
 * Encode and decode the index for sensor data.
 */

package dalvik.system;

import java.util.Vector;

public final class TaintIndexCoder {
	
	static public final int LOW_PART_BITS = 20;
	static public final int HIGH_PART_BITS = 4;
	static public final int PERIOD = LOW_PART_BITS * HIGH_PART_BITS;
	static public final int LOW_PART_MASK	= 0x000FFFFF;
	static public final int HIGH_PART_MASK = 0x00F00000;
	static public final int CODE_MASK 		= 0x00FFFFFF;
	
	static public int encode(int x) {
		x = x % PERIOD;
		int low = x % LOW_PART_BITS;
		int high = x / LOW_PART_BITS;
		int lowCode = 1 << low;
		int highCode = 1 << high;
		
		return ((highCode << LOW_PART_BITS) | lowCode);
	}
	
	static private int[] bitsOn(int x) {
		Vector<Integer> v = new Vector<Integer>();
		
		int l = 0;
		while (x > 0) {
			if ((x & 1) != 0) v.add(l);
			l++;
			x >>= 1;
		}
		
		int ret[] = new int[v.size()];
		for (int i = 0; i < v.size(); i++)
			ret[i] = v.get(i);
		
		return ret;
	}
	
	static public int[] decode(int x) {
		
		x &= CODE_MASK;
		
		int lowCode = x & LOW_PART_MASK;
		int highCode = (x & HIGH_PART_MASK) >> LOW_PART_BITS;
		
		int[] lowBits = bitsOn(lowCode);
		int[] highBits = bitsOn(highCode);
		
		int[] ret = new int[lowBits.length * highBits.length];
		for (int i = 0; i < highBits.length; i++)
			for (int j = 0; j < lowBits.length; j++)
				ret[i * lowBits.length + j] = highBits[i] * LOW_PART_BITS + lowBits[j];
		
		return ret;
	}
}
