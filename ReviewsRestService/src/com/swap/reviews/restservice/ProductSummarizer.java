package com.swap.reviews.restservice;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.swap.reviews.domain.*;

public class ProductSummarizer {

	private ProductRepository productRepository = new MySqlProductRepository();
	 

	public ProductSummarizer() {
	}
	
	public ProductSummary getSummaryForProduct(final int productId) {
		
		System.out.println("Processing request to get summary for productId : " + productId);
		
		List<String> features = productRepository.findProductFeatures(productId);
		ProductInfo productInfo = productRepository.findProductInfo(productId);
		ProductSummary productSummary = new ProductSummary();
		productSummary.setProductId(productInfo.getId());
		productSummary.setProductName(productInfo.getName());
		productSummary.setAvgRating(productInfo.getAverageRating());
		
		for(String feature : features) {
			FeatureSummary fs = productRepository.findFeatureSummary(productId, feature);
			productSummary.addFeatrueSummary(fs);
		}
		
		
		
		List<FeatureSummary> currentFeatureSummary = productSummary.getFeatureSummaries();
		
		System.out.println("All features count : " + currentFeatureSummary.size());
		
		if(currentFeatureSummary.size() > 10)
		{
			Collections.sort(currentFeatureSummary, new Comparator<FeatureSummary>() {
	
				@Override
				public int compare(FeatureSummary fs1, FeatureSummary fs2) {
					if(fs1.getPositiveCount() + fs1.getNegtiveCount() > fs2.getPositiveCount() + fs2.getNegtiveCount())
						return -1;
					else
						return 1;
				}
				
			});
			
			List<FeatureSummary> truncatedList = currentFeatureSummary.subList(0, 10);
		
			System.out.println("Truncated features count : " + truncatedList.size());
			
			productSummary.setFeatureSummaries(truncatedList);
		}
		
		return productSummary;
	}

}
