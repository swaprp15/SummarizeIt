package com.swap.reviews.crawler;

import com.swap.reviews.domain.*;

public interface ProductInfoCrawler  {
	ProductInfo crawlProductInfo(int productId);
}
