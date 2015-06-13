package com.swap.reviews.restservice;

import java.util.List;

import com.swap.reviews.domain.ProductInfo;
import com.swap.reviews.domain.Review;
import com.swap.reviews.domain.Sentence;

public interface ProductRepository {
	int saveProductInfo(final ProductInfo product);
	List<Review> findReviewsForProduct(final int productId);
	int saveSentence(int productId, int reviewId, Sentence sentence);
	FeatureSummary findFeatureSummary(final int productId, final String feature);
	void saveSentiment(int productId, int reviewId, int sentenceId, 
					   String feature, String opinion, int sentiment);
	public void saveProductFeatures(final int productId, final List<String> allFeatures); 
			 //final List<String> frequentFeatures);

	public List<String> findProductFeatures(final int productId/*, final boolean isFrequent*/);
	public ProductInfo findProductInfo(int productId);
}
