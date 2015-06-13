package com.swap.reviews.main;

import java.io.FileOutputStream;

import com.swap.reviews.crawler.ProductInfoCrawler;
import com.swap.reviews.crawler.SimpleProductInfoCrawler;
import com.swap.reviews.domain.ProductInfo;
import com.swap.reviews.repository.MySqlProductRepository;

public class Main {

	public static void main(String[] args) throws Exception {
		
		ProductInfoCrawler crawler = new SimpleProductInfoCrawler();
		
		int productId = 42104299;
		
		ProductInfo productInfo = crawler.crawlProductInfo(productId);
		
		ProductSummarizer productSummarizer = new ProductSummarizer();

		FileOutputStream fos = new FileOutputStream(productInfo.getName());
		productSummarizer.generateSummaryForProduct(productId);
		ProductSummary summary = productSummarizer.getSummaryForProduct(productId);
		System.out.println("Summarization for product :[" + productInfo.getName() + "," + productId + "]");
		System.out.println(summary.toString());
		fos.write(summary.toString().getBytes());
		fos.close();

		
		MySqlProductRepository.closeConnection();
	}

}
