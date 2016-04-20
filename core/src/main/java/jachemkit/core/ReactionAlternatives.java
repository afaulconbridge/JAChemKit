package jachemkit.core;

import java.util.Set;

import com.google.common.collect.Multiset;

public interface ReactionAlternatives<T> {

	public Multiset<T> getReactants();
	public Set<Multiset<T>> getProductsSet();
}
