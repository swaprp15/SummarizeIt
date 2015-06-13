package com.swap.reviews.main;

import com.swap.reviews.domain.Sentence;

public interface DependencyParser {
	
	Sentence parse(final Sentence sentence);
	
}
