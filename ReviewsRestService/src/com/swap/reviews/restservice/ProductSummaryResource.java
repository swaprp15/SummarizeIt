package com.swap.reviews.restservice;


import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

@Path("/")
public class ProductSummaryResource {

	private ProductSummarizer productSummarizer = new ProductSummarizer();

	@GET
	@Produces("application/json")
	@Path("/productsummary/{productId}")
	public Response getProductSummary(@PathParam("productId") String productId) {
		int pid = Integer.valueOf(productId);
		ProductSummary summary = productSummarizer.getSummaryForProduct(pid);
		return Response.ok(marshalProductSummary(summary)).header("Allow-Control-Allow-Methods", "POST,GET,OPTIONS")
				.header("Access-Control-Allow-Origin", "*")
				.build();
	}

	private String marshalProductSummary(final ProductSummary summary) {
		String response = new String();
		try {
			
			Gson gson = new Gson();
			response = gson.toJson(summary);
			
			
		} catch (final Exception e) {
			throw new RuntimeException("failed to marshal product summary:", e);
		}

		return response;
	}
}