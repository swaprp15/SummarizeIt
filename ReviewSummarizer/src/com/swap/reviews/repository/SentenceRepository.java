package com.swap.reviews.repository;

import com.swap.reviews.domain.Sentence;

public interface SentenceRepository {

	public int saveSentence(final int productId, final int reviewId, final Sentence sentence);
	
	public FeatureSummary findFeatureSummary(final int productId, final String feature);
}
