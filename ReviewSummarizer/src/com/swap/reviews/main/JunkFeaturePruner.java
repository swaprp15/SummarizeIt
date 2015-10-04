package com.swap.reviews.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class JunkFeaturePruner {
	
	private static HashSet<String> junkFeatures = null;
	
	private static void CreateJunkFeaturePruner(final String junkFeaturesFile) {
		
	
		InputStream res =
			    Main.class.getResourceAsStream(junkFeaturesFile);
		
		InputStreamReader rdr = new InputStreamReader(res);
		
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(rdr);
			String feature = null;
			
			junkFeatures = new HashSet<String>();
			
			while((feature = reader.readLine()) != null) {
				junkFeatures.add(feature);
			}
		} catch(final IOException e) {
			e.printStackTrace();
		} finally {
			if( reader!= null) {
				try {
				reader.close();
				} catch(final IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static boolean isJunkFeature(String feature)
	{
		if(junkFeatures == null)
		{
			CreateJunkFeaturePruner("/junk_features.txt");
		}
		
		return junkFeatures.contains(feature);
	}

}


//public class JunkFeaturePruner {
//	
//	private List<String> junkFeatures = new ArrayList<String>();
//	
//	public JunkFeaturePruner(final String junkFeaturesFile) {
//		
//		BufferedReader reader = null;
//		
//		try {
//			reader = new BufferedReader(new FileReader(junkFeaturesFile));
//			String feature = null;
//			while((feature = reader.readLine()) != null) {
//				junkFeatures.add(feature);
//			}
//		} catch(final IOException e) {
//			e.printStackTrace();
//		} finally {
//			if( reader!= null) {
//				try {
//				reader.close();
//				} catch(final IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	public List<String> pruneJunkFeatures(final List<String> features) {
//		List<String> featuresAfterPruning = new ArrayList<String>();
//		for(String f: features) {
//			if(!junkFeatures.contains(f)) {
//				featuresAfterPruning.add(f);
//			}
//		}
//		
//		return featuresAfterPruning;
//	}
//
//}
