package jachemkit.hashchem.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class HashAtom {

	public ImmutableList<Byte> value;
	
	@SuppressWarnings("unused")
	private HashAtom(){
		
	};
	
	public HashAtom(List<Byte> value) {
		this.value = ImmutableList.copyOf(value);
	}
}
