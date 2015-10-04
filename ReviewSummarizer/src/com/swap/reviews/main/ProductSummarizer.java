package com.swap.reviews.main;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import org.apache.mahout.common.Pair;



import com.swap.reviews.domain.FeatureOpinionPair;
import com.swap.reviews.domain.Review;
import com.swap.reviews.domain.Sentence;
import com.swap.reviews.repository.*;;

public class ProductSummarizer {

	private ProductRepository productRepository = new MySqlProductRepository();
	private SentenceTokenizer sentenceTokenizer = new OpenNLPSentenceTokenizer("models/en-sent.bin", "models/en-token.bin");
	private DependencyParser dependencyParser = new StanfordDependencyParser();
	private SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
	//private FrequentPatternMining frequentPatternMining = new FrequentPatternMining();
	//private JunkFeaturePruner junkFeaturePruner = new JunkFeaturePruner("junk_features.txt");
	
	//use only if FPM is used
	//private RedundantFeaturePruner redundantFeaturePruner = new RedundantFeaturePruner(3);


	public ProductSummarizer() {
		sentimentAnalyzer.loadSeedList();
	}
	
	public void generateSummaryForProduct(final int productId) {
		List<Review> reviews = productRepository.findReviewsForProduct(productId);
		Set<String> features = new HashSet<String>();
		
		//List<Pair<List<String>, Long>> featuresInAllSentences = new ArrayList<Pair<List<String>, Long>>();
		
		for(Review review : reviews) {
			System.out.println("parsing review:" + review.getReviewId() + " in product:" + review.getProductId());
			List<Sentence> sentences = sentenceTokenizer.toSentences(review.getComment());
			
			
			
			for(Sentence sent : sentences) {
				dependencyParser.parse(sent);
				
				int sentenceId = productRepository.saveSentence(productId, review.getReviewId(), sent);
				List<FeatureOpinionPair> pairs = sent.getFeatureOpinionPairs();
				
				Set<String> sentenceFeatures = new HashSet<String>();
				for(FeatureOpinionPair fop : pairs) {
					String opinionWord = fop.getOpinion();
					if(opinionWord != null) {
						int sentiment = sentimentAnalyzer.getSentiment(opinionWord);
						String feature = fop.getFeature().toLowerCase();
						features.add(feature);
						sentenceFeatures.add(feature);
						
						productRepository.saveSentiment(productId, review.getReviewId(),   
																	 sentenceId, 
																	 feature, 
																	 opinionWord, 
																	 sentiment);
			
					}
				}
				//featuresInAllSentences.add(new Pair<List<String>, Long>(new ArrayList<String>(sentenceFeatures),1L));	
			}
		}
		List<String> allFeatures = new ArrayList<String>(features);
		System.out.println("all features count:" + allFeatures.size());
		
		/*
		List<String> frequentFeatures = frequentPatternMining.findFrequentFeatures(allFeatures, featuresInAllSentences, 3);
		System.out.println("frequent features count:" + frequentFeatures.size());
		List<String> featuresAfterPruningJunk = junkFeaturePruner.pruneJunkFeatures(frequentFeatures);
		System.out.println("frequent features after pruning junk:" + featuresAfterPruningJunk.size());
		List<String> featuresAfterPruningRedundantFeatures = redundantFeaturePruner.pruneRedundantFeatures(featuresAfterPruningJunk, productId);
		System.out.println("frequent features after pruning redundancy:" + featuresAfterPruningRedundantFeatures.size());
		featuresRepository.saveProductFeatures(productId, allFeatures, featuresAfterPruningRedundantFeatures);
		 */
		
		//List<String> featuresAfterPruningJunk = junkFeaturePruner.pruneJunkFeatures(allFeatures);
		//System.out.println("frequent features after pruning junk:" + featuresAfterPruningJunk.size());
		
		//List<String> featuresAfterPruningRedundantFeatures = redundantFeaturePruner.pruneRedundantFeatures(featuresAfterPruningJunk, productId);
		//System.out.println("frequent features after pruning redundancy:" + featuresAfterPruningRedundantFeatures.size());
		productRepository.saveProductFeatures(productId, allFeatures);//, featuresAfterPruningJunk);
	}
	
	public ProductSummary getSummaryForProduct(final int productId) {
		List<String> features = productRepository.findProductFeatures(productId);//, true);

		ProductSummary productSummary = new ProductSummary();
		
		for(String feature : features) {
			FeatureSummary fs = productRepository.findFeatureSummary(productId, feature);
			productSummary.addFeatrueSummary(fs);
		}
		
		return productSummary;
	}
	
	
	public static void main(final String[] args) throws Exception {
		ProductSummarizer summarizer = new ProductSummarizer();
		ProductSummary summary = summarizer.getSummaryForProduct(1);
		FileOutputStream fos = new FileOutputStream("product_summary.txt");
		fos.write(summary.toString().getBytes());
		fos.close();

	}

}
