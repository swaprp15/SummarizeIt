package com.swap.reviews.crawler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.swap.reviews.domain.ProductInfo;
import com.swap.reviews.domain.Review;
import com.swap.reviews.repository.MySqlProductRepository;
import com.swap.reviews.repository.ProductRepository;

class Constants
{
	public static String urlFormat = "http://api.walmartlabs.com/v1/reviews/%d?format=json&apiKey=%s";
}

class RatingDistributionsJSON
{
	int count;
	int ratingValue;
}

class ReviewStatisticsJSON
{
	float averageOverallRating;
	int overallRatingRange;
	List<RatingDistributionsJSON> ratingDistributions;
	int totalReviewCount;
}

class OverallRatingJSON
{
	String label;
	int rating;
}

class ProductAttributeJSON
{
	String label;
	int rating;
}

class ReviewJSON
{
	String name;
	OverallRatingJSON overallRating;
	List<ProductAttributeJSON> productAttributes;
	String reviewer;
	String reviewText;
	String submissionTime;
	String title;
	int upVotes;
	int downVotes;
}

class ProductJSON
{
	String itemId;
	String name;
	float salePrice;
	long upc;
	String categoryPath;
	String brandName;
	String productTrackingUrl;
	String productUrl;
	List<ReviewJSON> reviews;
	ReviewStatisticsJSON reviewStatistics;
	boolean availableOnline;
}

public class SimpleProductInfoCrawler implements ProductInfoCrawler {
	
	private ProductRepository dbObj = new MySqlProductRepository();
	
	private static String configFile = "config.properties";
	
	private String apiKey;
	private static String apiKeyConfigName = "apiKey";
	
	public SimpleProductInfoCrawler()
	{
		Properties properties = new Properties();
		
		InputStream input = null;
		
		try
		{
			input = new FileInputStream(configFile);
			
			properties.load(input);

			apiKey = properties.getProperty(apiKeyConfigName);
		}
		catch(IOException e)
		{	
			e.printStackTrace();
		}
		finally
		{
			try {
				input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String [] args)
	{
		SimpleProductInfoCrawler obj = new SimpleProductInfoCrawler();
	
		final int productId = 0;
		obj.crawlProductInfo(productId);
	}
	
	private ProductJSON GetJSON(final int productId)
	{
		try  {
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			
			//DefaultHttpClient httpClient = new DefaultHttpClient();
			
		    HttpGet request = new HttpGet(String.format(Constants.urlFormat, productId, apiKey));
		    //StringEntity params = new StringEntity(body);
		    request.addHeader("content-type", "application/json");
		    //request.setEntity(params);
		    HttpResponse result = httpClient.execute(request);
		    String json = EntityUtils.toString(result.getEntity(), "UTF-8");

		    com.google.gson.Gson gson = new com.google.gson.Gson();
		    ProductJSON productJSON = gson.fromJson(json, ProductJSON.class);

            System.out.println(productJSON.itemId);
		    
		    System.out.println(json);
		    
		    return productJSON;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	public ProductInfo crawlProductInfo(final int productId) {

		ProductInfo productInfo = new ProductInfo();

		ProductJSON productJSON = GetJSON(productId);
		
		if(productJSON == null)
			return null;
		
		// parse JSON and fill product Info
		productInfo.setId(productId);
		productInfo.setName(productJSON.name);
		productInfo.setCategory(productJSON.categoryPath);
		productInfo.setAverageRating(productJSON.reviewStatistics.averageOverallRating);
		
		List<Review> reviews = new ArrayList<>();
		
		int reviewId = 0;
		
		for(ReviewJSON reviewJSON : productJSON.reviews)
		{
			Review review = new Review();
			review.setReviewId(++reviewId);
			review.setComment(reviewJSON.reviewText);
			review.setCommentedDate(reviewJSON.submissionTime);
			review.setCommentedUserName(reviewJSON.reviewer);
			review.setDownVotes(reviewJSON.downVotes);
			review.setProductId(productId);
			review.setRating(reviewJSON.overallRating.rating);
			review.setUpvotes(reviewJSON.upVotes);
			
			reviews.add(review);
		}
		
		productInfo.setReviews(reviews);
		
	    dbObj.saveProductInfo(productInfo);
	    
	    System.out.println("completed crawling product : " + productId );
	    
		return productInfo;
	}

}
