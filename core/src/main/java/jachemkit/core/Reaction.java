package jachemkit.core;

import com.google.common.collect.Multiset;

public interface Reaction<T> {
	public Multiset<T> getReactants();
	public Multiset<T> getProducts();
}