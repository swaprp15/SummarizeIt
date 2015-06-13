package com.swap.reviews.restservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.swap.reviews.domain.Sentence;
import com.swap.reviews.domain.ProductInfo;
import com.swap.reviews.domain.Review;


public class MySqlProductRepository extends BaseRepository implements ProductRepository {

	private static String FIND_REVIEWS_FOR_PRODUCT = "select review_id, product_id, user, date_time, comment_text, rating, upvotes, downvotes from PRODUCT_REVIEWS where product_id=?";

	
	private int sentence_id_counter = 1;

	private final String SAVE_SENTENCE = "insert into REVIEW_SENTENCES (product_id, review_id, sentence_id, sentence_text) values (?, ?, ?, ?)";
	private final String FEATURE_SUMAMRY = "select a.review_id, a.sentence_id, b.sentence_text, a.sentiment from SENTENCE_SENTIMENT a, REVIEW_SENTENCES b where a.product_id = b.product_id and a.review_id = b.review_id and a.sentence_id = b.sentence_id and a.product_id = ? and a.feature = ?";


	private static String SAVE_SENTIMENT = "insert into SENTENCE_SENTIMENT (product_id, review_id, sentence_id, feature, opinion, sentiment) values (?, ?, ?, ?, ?, ?)";
	private static String PURE_SUPPORT_FOR_FEATURE = "select sentence_id from  SENTENCE_SENTIMENT where feature=? and product_id=?";


	private String SAVE_PRODUCT_FEATURES = "insert into PRODUCT_FEATURES (product_id, feature) values (?, ?)";
	private String FIND_PRODUCT_FEATURES = "select feature from PRODUCT_FEATURES where product_id=?";
	
	private static String FIND_INFO_FOR_PRODUCT = "select id, name, avg_rating from PRODUCT_INFO where id=?";
	
//	private String SAVE_PRODUCT_FEATURES = "insert into PRODUCT_FEATURES (product_id, feature, is_frequent_feature) values (?, ?, ?)";
//	private String FIND_PRODUCT_FEATURES = "select feature from PRODUCT_FEATURES where product_id=? and is_frequent_feature=?";
//	
	
	private String EscapeSingeQuote(String str) {
		return str.replaceAll("'", "''");

	}

	public int saveProductInfo(final ProductInfo product) {
		/**
		 * Here you need to write code to insert product info in product
		 * argument into mysql table.
		 */

		try {
			Connection con = getConnection();
			Statement st = con.createStatement();

			int productId = product.getId();
			String name = EscapeSingeQuote(product.getName());

			float avgRating = product.getAverageRating();
			String category = EscapeSingeQuote(product.getCategory());

			// int res = st.executeUpdate("insert into PRODUCT_INFO values (" +
			// id + ", '" + name + "', " + rating + ", '" + category + "')");

			int res = st
					.executeUpdate("insert into PRODUCT_INFO (id, name, avg_rating, category) values ("
							+ productId
							+ ", '"
							+ EscapeSingeQuote(name)
							+ "', "
							+ avgRating
							+ ", '"
							+ EscapeSingeQuote(category) + "')");

			for (Review review : product.getReviews()) {
				res = st.executeUpdate("insert into PRODUCT_REVIEWS (review_id, product_id, user, date_time, comment_text, rating, upvotes, downvotes) values ("
						+ review.getReviewId()
						+ ", "
						+ productId
						+ ", '"
						+ EscapeSingeQuote(review.getCommentedUserName())
						+ "', '"
						+ EscapeSingeQuote(review.getCommentedDate())
						+ "', '"
						+ EscapeSingeQuote(review.getComment())
						+ "', " + review.getRating()
						+  ", "
						+ review.getUpvotes()
						+  ", "
						+ review.getDownVotes()
						+ ")");
			}

			con.commit();
			closeStatment(st);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return 0;
	}

	public List<Review> findReviewsForProduct(int productId) {
		List<Review> reviews = new ArrayList<Review>();
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = getConnection();
			
			 st = con
					.prepareStatement(FIND_REVIEWS_FOR_PRODUCT);
			 st.setInt(1, productId);
			 rs = st.executeQuery();

			while (rs.next()) {
				Review review = new Review();
				review.setReviewId(rs.getInt("review_id"));
				review.setProductId(rs.getInt("product_id"));
				review.setRating(rs.getInt("rating"));
				review.setCommentedUserName(rs.getString("user"));
				review.setCommentedDate(rs.getString("date_time"));
				review.setComment(rs.getString("comment_text"));
				review.setUpvotes(rs.getInt("upvotes"));
				review.setDownVotes(rs.getInt("downvotes"));
				reviews.add(review);
			}

		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException(
					"exception while fetching reviews from mysql:", e);
		} finally {
			closeStatment(st);
			closeResultset(rs);
		}
		return reviews;
	}
	
	public int saveSentence(int productId, int reviewId, Sentence sentence) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = getConnection();

			st = con.prepareStatement(SAVE_SENTENCE);
			int i = 1;
			st.setInt(i++, productId);
			st.setInt(i++, reviewId);
			st.setInt(i++, sentence_id_counter);
			st.setString(i++, sentence.getText());
			st.executeUpdate();
			con.commit();

		} catch (final Exception e) {
			throw new RuntimeException(
					"exception while inserting sentences in mysql:", e);
		} finally {
			closeStatment(st);
			closeResultset(rs);
		}

		return sentence_id_counter++;
	}

	public FeatureSummary findFeatureSummary(final int productId, final String feature) {
		
		FeatureSummary featureSummary = new FeatureSummary();
		
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = getConnection();

			st = con.prepareStatement(FEATURE_SUMAMRY);
			int i = 1;
			st.setInt(i++, productId);
			st.setString(i++, feature);
			rs = st.executeQuery();
			
			while(rs.next()) {
				SentenceAndSentiment ss = new SentenceAndSentiment();
				ss.setReviewId(rs.getInt("review_id"));
				ss.setSentenceId(rs.getInt("sentence_id"));
				ss.setSentence(rs.getString("sentence_text"));
				ss.setSentiment(rs.getInt("sentiment"));
				featureSummary.addSentenceAndSentiment(ss);
			}
			
			featureSummary.setFeatureName(feature);

		} catch (final Exception e) {
			
			e.printStackTrace();
			
			throw new RuntimeException(
					"exception while finding sumamry for feature:" + feature, e);
		} finally {
			closeStatment(st);
			closeResultset(rs);
		}

		return featureSummary;
	}

	public void saveSentiment(int productId, int reviewId, int sentenceId,
			String feature, String opinion, int sentiment) {

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = getConnection();

			st = con.prepareStatement(SAVE_SENTIMENT);
			int i = 1;
			st.setInt(i++, productId);
			st.setInt(i++, reviewId);
			st.setInt(i++, sentenceId);
			st.setString(i++, feature);
			st.setString(i++, opinion);
			st.setInt(i++, sentiment);

			st.executeUpdate();

			con.commit();

		} catch (final Exception e) {
			throw new RuntimeException(
					"exception while inserting sentiment in mysql:", e);
		} finally {
			closeStatment(st);
			closeResultset(rs);
		}

	}
	
	public void saveProductFeatures(final int productId, final List<String> allFeatures) { //, final List<String> frequentFeatures) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = getConnection();

			st = con.prepareStatement(SAVE_PRODUCT_FEATURES);
			for(String feature : allFeatures) {
				int i = 1;
				st.setInt(i++, productId);
				st.setString(i++, feature);
				
				/*
				 if(frequentFeatures.contains(feature)) {
				 
					st.setString(i++, "YES");
				} else {
					st.setString(i++, "NO");
				}
				*/
				st.addBatch();
				st.executeBatch();
			}
			con.commit();
		} catch (final Exception e) {
			throw new RuntimeException(
					"exception while inserting product features in from mysql:", e);
		} finally {
			closeStatment(st);
			closeResultset(rs);
		}

	}

	public List<String> findProductFeatures(int productId/*, boolean isFrequent*/) {
		
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		List<String> features = new ArrayList<String>();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = getConnection();
			st = con.prepareStatement(FIND_PRODUCT_FEATURES);
			int i=1;
			st.setInt(i++, productId);
			/*
			if(isFrequent) {
				st.setString(i++, "YES");
			} else {
				st.setString(i++, "NO");
			}
			*/
			rs = st.executeQuery();
			
			while(rs.next()) {
				features.add(rs.getString("feature"));
			}
			
		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException(
					"exception while retrieving product features in from mysql:", e);
		} finally {
			closeStatment(st);
			closeResultset(rs);
		}	
		return features;
	}
	
	@Override
	public ProductInfo findProductInfo(int productId) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		ProductInfo pInfo = new ProductInfo();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = getConnection();

			st = con.prepareStatement(FIND_INFO_FOR_PRODUCT);
			int i = 1;
			st.setInt(i++, productId);
			rs = st.executeQuery();
			
			while(rs.next()) {
				pInfo.setId(rs.getInt("id"));
				pInfo.setName(rs.getString("name"));
				pInfo.setAverageRating(rs.getInt("avg_rating"));
			}
			
		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException(
					"exception while finding info  for productId:" + productId , e);
		} finally {
			closeStatment(st);
			closeResultset(rs);
		}	
		
		return pInfo;	
	}
	
}
