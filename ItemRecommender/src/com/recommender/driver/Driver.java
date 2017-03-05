package com.recommender.driver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import com.recommender.calculations.MyRecommender;

public class Driver {
	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws TasteException
	 */
	public static void main(String[] args) throws IOException, TasteException {
		// TODO Auto-generated method stub
		Driver d = new Driver();
		d.argumentChecker(args);
		String inputFileName = d.convertToCommaSeperated(args[0]);
		MyRecommender myRecommender = new MyRecommender(inputFileName);
		myRecommender.buildDataModel();
		myRecommender.buildItemSimilarity();
		myRecommender.buildRecommender();
		int[][] finalResultantMatrix = myRecommender.go();
		d.printMatrixToFile(finalResultantMatrix, args[1]);
		d.printMatrixToConsole(finalResultantMatrix, false);
		/*
		 * DataModel model = new FileDataModel(new File(inputFileName));
		 * ItemSimilarity itemSimilarity = new
		 * PearsonCorrelationSimilarity(model); Recommender recommender = new
		 * GenericItemBasedRecommender(model, itemSimilarity); Recommender
		 * cachingRecommender = new CachingRecommender(recommender);
		 * 
		 * for (long i = 1; i < model.getNumUsers(); i++) { for (long j = 1; j <
		 * model.getNumItems(); j++) { cachingRecommender.estimatePreference(i,
		 * j); } }
		 */
	}

	/**
	 * 
	 * @param finalResultantMatrix
	 * @param test
	 */
	private void printMatrixToConsole(int[][] finalResultantMatrix, boolean test) {
		// TODO Auto-generated method stub
		if (test) {
			for (int userID = 1; userID < finalResultantMatrix.length; userID++) {
				for (int itemID = 1; itemID < finalResultantMatrix[userID].length; itemID++) {
					System.out.println(userID + " " + itemID + " " + finalResultantMatrix[userID][itemID]);
				}
			}
		}
	}

	/**
	 * 
	 * @param finalResultantMatrix
	 * @param outputFileName
	 * @throws IOException
	 */
	private void printMatrixToFile(int[][] finalResultantMatrix, String outputFileName) throws IOException {
		// TODO Auto-generated method stub
		File outputFile = new File(outputFileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		StringBuilder lineBuffer = new StringBuilder();
		for (int userID = 1; userID < finalResultantMatrix.length; userID++) {
			for (int itemID = 1; itemID < finalResultantMatrix[userID].length; itemID++) {
				lineBuffer.append(getLine(userID, itemID, finalResultantMatrix[userID][itemID]));
				// bw.write(getLine(userID, itemID,
				// finalResultantMatrix[userID][itemID]));
			}
		}
		bw.write(lineBuffer.toString());
		bw.flush();
		bw.close();
	}

	/**
	 * 
	 * @param userID
	 * @param itemID
	 * @param value
	 * @return convert matrix value to string
	 */
	private String getLine(int userID, int itemID, int value) {
		// TODO Auto-generated method stub
		return (new String(userID + " " + itemID + " " + value + "\n"));
	}

	/**
	 * 
	 * @param string
	 * @return filename of temp csv for dataModel
	 * @throws IOException
	 */
	private String convertToCommaSeperated(String string) throws IOException {
		// TODO Auto-generated method stub
		String returnString = "temp.csv";
		File inputFile = new File(string);
		if (!inputFile.exists()) {
			System.out.println(string + " file does not exist.");
		}
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(inputFile));
			bw = new BufferedWriter(new FileWriter(returnString));
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(" ");
				bw.write(values[0] + "," + values[1] + "," + values[2] + "\n");

			}
			bw.flush();
			br.close();
			bw.close();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
		}
		return returnString;
	}

	/**
	 * Check arguments for input_file.txt and output_file.txt
	 * 
	 * @param args
	 */
	private void argumentChecker(String[] args) {
		// TODO Auto-generated method stub
		if (args.length != 2) {
			System.out.println("Invalid arguments. \nExpected:\tDriver inputFile.txt outputFile.txt");
		}
	}

}
