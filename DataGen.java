import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Poonkodi
 *
 * This class implements a data generator for MSLR data.
 * This is a modified version of Jessie Wright's implementation.
 */
public class DataGen {
	/**
	 * Difference records a & b and output CSV formatted string.
	 * 
	 */
	public static String DiffRecords(Record a, Record b){
		String toReturn = "";
		int diff = a.GetRelevance() - b.GetRelevance();
		for(int i = 0; i < a.GetFeatureSize(); i++){
			double k = 0;
			if(i > 94 && i < 100) {
				k = (int) a.GetFeatureVal(i) - b.GetFeatureVal(i);
			} else  {
				k =  a.GetFeatureVal(i) - b.GetFeatureVal(i);
			}
			toReturn += String.format("%.3f,", k);
		}
		String label = "L" + (diff > 0 ? 1 : 0) ;
		toReturn += label + "\n";	

		String weighted = toReturn;
		return weighted;
	}

	/**
	 * Generates the Data from an Input file and outputs 
	 * CSV formated pairwise records.
	 */
	public static void GenerateData(
			String infile, 
			String outfile,
			int sampler) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(infile));
		FileWriter writer = new FileWriter(outfile);
		BufferedWriter bf = new BufferedWriter(writer);
		String line = in.readLine();
		int currQID = 0, prevQID;
		Record currRecord;
		int numQueries = 0;
		int[] seen = new int[5];
		List<Record> recs = new ArrayList<Record>();

		int lines = 0;
		for (int i=0; i < 137; i++) {
			bf.write("attr"+i+",");
		}
		bf.write("label\n");
		while (line != null) {
			lines++;
			prevQID = currQID;
			currRecord = new Record(line, true);
			currQID = currRecord.GetQueryId();
			if (lines == 1) prevQID = currQID;
			line = in.readLine();

			if (currQID == prevQID) {
				int rel = currRecord.GetRelevance();
				if (seen[rel]++ <= sampler) {
					recs.add(currRecord);
				}
				continue;
			} else if (currQID != prevQID) {
				numQueries++;
				if (numQueries % 100 == 0) System.out.println("Processing query count: " + numQueries);
				String wbuffer = "";
				int wc = 0;

				for (int i = 0; i < recs.size(); i++) {
					for (int j = i+1; j < recs.size(); j++) {
						/*
						 * code to generate the pairwise only for required difference
						 */						
						double k = Math.random();
						int k1 = (k > .5)? i : j;
						int k2 = (k1 == i)? j : i;
						if (k1 != k2 && recs.get(k1) != null && recs.get(k2) != null)  {
							wbuffer += DiffRecords(recs.get(k1), recs.get(k2));
							wc++;
						} 
					}
				}
				bf.write(wbuffer);
				recs.clear();
				seen = new int[5];
			}
		}
		bf.flush();
		bf.close();
		in.close();
		System.out.println("pairwise output done : " + outfile);
	}

	public static void main(String[] args) throws Exception {

		System.out.println(args.length);
		for(String s: args){
			System.out.println(s);
		}

		int s = Integer.parseInt(args[1]);
		String csvFile = args[0] + ".csv";
		DataGen.GenerateData(args[0], csvFile, s);
		CsvtoArff.convert(csvFile);	
	}

}
