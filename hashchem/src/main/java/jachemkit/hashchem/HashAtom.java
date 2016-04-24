package jachemkit.hashchem;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class HashAtom {

	public ImmutableList<Byte> value;
	
	public HashAtom(List<Byte> value) {
		this.value = ImmutableList.copyOf(value);
	}
}
