import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/*
 * Usage:
 *   Ranker ranker = new Ranker("path to weights file");
 *   double score = ranker.Rank("path to data_file", "path to outputfile");
 *   double score2 = ranker.Rank("path to data_file2", "path to outputfile2");
 */

public class MultiRanker {
	
	private ArrayList<Weight> weights;
	double[] scores;
	int[] counts;
	
	
	int[][] incorrect = new int[5][5];
	int[][] count = new int[5][5];
	/** to add test data relavance */
	private ArrayList<Integer> relevanceList;
	

	
	/* calls the constructor of Weight class and puts the attribute name
	 *  and values in hastable */
	
	public MultiRanker() throws IOException {
		weights = new ArrayList<Weight>();
		scores = new double[5];
		counts = new int[5];
		relevanceList = new ArrayList<Integer>();
		
	}	
	
	public void AddWeights(String weights_file) throws IOException {
		weights.add(new Weight(weights_file));
	}
	
	
	public ArrayList<Record> Rank(ArrayList<Record> records) {		
		Map<Double,Record> map = new TreeMap<Double,Record>();		
		for (Record r : records) {
			double score = 0;
			for (Weight w : weights) {
				score += w.ApplyWeight(r);
			}
			
			int rel = r.GetRelevance();
			/** add relavance to the list **/
			
			relevanceList.add(r.GetRelevance());
			
			scores[rel] += score;
			counts[rel] ++;
			
			r.SetScore(score);
			map.put(score, r);
		}
		ArrayList<Record> sorted_records = new ArrayList<Record>(map.values());
		return sorted_records;
	}	
	
	public double Score(Record r) {
		double score = 0;
		for (Weight w : weights) {
			score += w.ApplyWeight(r);		}
		return score;
	}
	
	public void RankPairWise(String filename) throws IOException {
		File file = new File(filename);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		FileWriter writer = new FileWriter(filename + ".ranked");
		BufferedWriter bf = new BufferedWriter(writer);
		ArrayList<Record> Recordset = new ArrayList<Record>();
		int prevQid = -1;
		int curQid = -2;
		String line;
		int numlines = 0;
		while ((line = reader.readLine()) != null) {
			Record r = new Record(line,true);
			curQid = r.GetQueryId();
			if (++numlines % 5000 == 0) {
				System.out.println("num lines : " + numlines);
				PrintPairwiseRes();
			}
			if (curQid == prevQid || prevQid == -1) {
				Recordset.add(r);
				
				
			} else {
				for (int i = 0; i < Recordset.size(); i++) {
					int rel_i = Recordset.get(i).GetRelevance();
					for (int j = i +1; j < Recordset.size(); j++) {
						int rel_j = Recordset.get(j).GetRelevance();
						if (rel_i == rel_j) continue;
						double s1 = Score(Recordset.get(i));
						double s2 = Score(Recordset.get(j));
						
						boolean corr = (s1 > s2) == (rel_i > rel_j);
						
						System.out.println("captured relevance"+relevanceList.get(i));
						
						//System.out.println(rel_i + " - " + rel_j + " : " + s1 + "\t" + s2 + "( " + corr);
						int k1 = (rel_i > rel_j)? rel_i : rel_j;
						int k2 = (rel_i > rel_j)? rel_j : rel_i;
						
						count[k1][k2]++;
						if (corr) {
							incorrect[k1][k2]++;
						}
						
					}
				}
				Recordset.clear();
				Recordset.add(r);
			}
			prevQid = curQid;
		}
		
		
		System.out.println("Pairwise comparision");
		
		PrintPairwiseRes();

		System.out.println("average scores");
		for(int i = 0; i<5 ;i++) {
			System.out.println("avg " +i + " :: " + scores[i] / counts[i]);
			bf.write("\navg " +i + " :: " + scores[i] / counts[i]);
		}
		bf.close();
	}	
	
	public void PrintPairwiseRes() {
		System.out.println("Pairwise comparision");
		
		for (int i = 0; i < incorrect.length; i++) {
			System.out.printf("\tREL-" + i );
		}
		System.out.println();
		for (int i = 0; i < incorrect.length; i++) {
			System.out.print("REL-" + i + "\t");
			for (int j = 0; j < incorrect.length; j++) {
				if (count[i][j] == 0) 
					System.out.print("-\t");
				else {
					System.out.printf("%.3f\t ", 
							(incorrect[i][j] * 1.0 ) / count[i][j]);
				}
			}
			System.out.println("");
		}
	}
	
	
	
	
	public static void main(String args[]) throws Exception{
		/* 
		 * The weight file contains the weights of all the features
		 * data file is the test file for which output has to be predicted
		 *
		 * */
		
		if (args.length < 2) {
			System.out.println("Error. atleast 2 args");
			System.exit(2);
		}
		String datafile = args[0];

		MultiRanker ranker = new MultiRanker();
		for (int i = 1; i < args.length; i++) {
			ranker.AddWeights(args[i]);
			System.out.println("Loaded Weights : " + args[i] );			}
				ranker.RankPairWise(datafile);
				System.out.println("Loaded Data"); 		
	}

}




