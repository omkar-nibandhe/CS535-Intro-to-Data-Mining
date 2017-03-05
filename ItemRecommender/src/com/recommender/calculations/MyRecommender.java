package com.recommender.calculations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class MyRecommender {
	// private String inputCSV = null;
	private File inputFile = null;
	private DataModel dataModel = null;
	private ItemSimilarity itemSimilarity = null;
	private Recommender rec = null;
	private Recommender cachingRecommender = null;
	private int[][] resultMatrix = null;

	/**
	 * constructor to set the input file name to the private member
	 * 
	 * @param inputFileName
	 */
	public MyRecommender(String inputFileName) {
		// inputCSV = inputFileName;
		inputFile = new File(inputFileName);
	}

	/**
	 * method to build data model using the file data model.
	 * 
	 * @throws IOException
	 * @throws TasteException
	 */
	public void buildDataModel() throws IOException, TasteException {
		// dataModel = new FileDataModel(new File(inputCSV));

		dataModel = new FileDataModel(inputFile);
		resultMatrix = new int[944][1683];
		init(resultMatrix);
	}

	/**
	 * method to initialse the matrix
	 * 
	 * @param resultMatrix2
	 */
	private void init(int[][] resultMatrix2) {
		// TODO Auto-generated method stub
		for (int i = 0; i < resultMatrix2.length; i++) {
			for (int j = 0; j < resultMatrix2[i].length; j++) {
				resultMatrix2[i][j] = 1;
			}
		}
	}

	/**
	 * method to build Pearson Correlation Similarity.
	 * 
	 * @throws TasteException
	 */
	public void buildItemSimilarity() throws TasteException {
		itemSimilarity = new PearsonCorrelationSimilarity(dataModel);
	}

	/**
	 * method to build generic item based recommender
	 * 
	 * @throws TasteException
	 */
	public void buildRecommender() throws TasteException {
		rec = new GenericItemBasedRecommender(dataModel, itemSimilarity);
		cachingRecommender = new CachingRecommender(rec);
	}

	/**
	 * main function to calculate the recommendation
	 * 
	 * @return matrix of final results ready for printing.
	 * @throws TasteException
	 */
	public int[][] go() throws TasteException {
		// TODO Auto-generated method stub
		LongPrimitiveIterator itemIterator = dataModel.getItemIDs();
		LongPrimitiveIterator userIterator = dataModel.getUserIDs();

		List<Long> itemList = new ArrayList<>();
		while (itemIterator.hasNext())
			itemList.add(itemIterator.next());

		List<Long> userList = new ArrayList<>();
		while (userIterator.hasNext())
			userList.add(userIterator.next());

		Collections.sort(itemList);
		Collections.sort(userList);

		System.err.println("Computing recommendations\n Progress : ");
		for (int i = 0; i < userList.size(); i++) {
			for (int j = 0; j < itemList.size(); j++) {
				insertInMatrix(i + 1, j + 1, cachingRecommender.estimatePreference(userList.get(i), itemList.get(j)));
			}
			if ((i % 100) == 0) {
				System.err.print("..");
			}
		}
		System.err.println(" Computing completed.");
		return resultMatrix;
		// System.out.println(recommendations);
	}

	/**
	 * 
	 * @param i
	 * @param j
	 * @param estimatePreference
	 */
	private void insertInMatrix(int i, int j, float estimatePreference) {
		// TODO Auto-generated method stub
		resultMatrix[i][j] = getIntValue(estimatePreference);

	}

	/**
	 * 
	 * @param estimatePreference
	 * @return estimatePreference count in integer by doing round function.
	 */
	private int getIntValue(float estimatePreference) {
		// TODO Auto-generated method stub
		if (estimatePreference <= 1.0) {
			return 1;
		}
		if (estimatePreference >= 5.0) {
			return 5;
		}
		int value = Math.round(estimatePreference);
		if (value == 0) {
			return 1;
		}
		return (value);
	}
}
