public class Record{

	private int relevance;
	private int queryId;
	private String csvInput;
	private double[] features;
	private double score;

	public void SetScore(double s) {
		score = s;
	}
	
	public double GetScore() {
		return score;
	}
	
	public Record(String csvInputLine){
		String[] fields = csvInputLine.split(",");
		relevance = Integer.parseInt(fields[0]);
		queryId = Integer.parseInt(fields[1]);
		csvInput = csvInputLine;
		features = new double[136];
		for(int i = 0; i < 136; i++){
			features[i] = Double.parseDouble(fields[i+2]);
		}
	}

	// 1 qid:2 1:3 3:4. .....
	public Record(String sparseLine, boolean filename){
		String[] fields = sparseLine.split(" ");
		//System.out.println("fields@" + fields.length + " ff:" + fields);
		relevance = Integer.parseInt(fields[0]);
		String[] qid = fields[1].split(":");
		queryId = Integer.parseInt(qid[1]);
		//System.out.println("rel : " + relevance + " qid: "+ queryId);
		
		features = new double[137];
		for(int i = 2; i < fields.length; i++){
			String[] p = fields[i].split(":");
			int attr = Integer.parseInt(p[0]);
			double val = Double.parseDouble(p[1]);
			//System.out.println("fields " + i + " : " + fields[i] +" val:" + val);
			features[attr] = val;
		}
		//System.out.println("features:@" + features);
	}
	
	public String toString() {
		String retval = "";
		retval = "rel : " + relevance + " qid: "+ queryId;
		for(int i = 0; i < features.length; i++){
			retval += ", f" + i + ":" + features[i];
		}
		return retval;
	}
	
	public void SetRelevance(int newRelevance){
		relevance = newRelevance;
	}

	public int GetRelevance(){
		return relevance;
	}

	public void SetQueryID(int newQID){
		queryId = newQID;
	}

	public int GetQueryId(){
		return queryId;
	}

	public int GetFeatureSize(){
		return features.length;
	}
	
	public double GetFeatureVal(int i){
		if(i >= 0 && i < features.length) return features[i];
		else{
			System.out.println("I should really learn about exception: out of bounds in GetFeatureVal call, i = " + i);
			return 0;
		}
	}

	public void SetFeatureVal(int i, double val){
		if(i > 0 && i < features.length)  features[i] = val;
		else{
			System.out.println("I should really learn about exception: out of bounds in SetFeatureVal call");
		}
	}
	// a.Difference(b) -> a - b for printing
	// doesn't output QID
	public String Difference(Record b){
		String toReturn = "";
		toReturn += this.relevance - b.GetRelevance() > 0 ? 1 : -1;
		for(int i = 0; i < features.length; i++){

			toReturn += ",";
			//boolean values 95-99 produce categorical {-1, 0 , 1}  cast to int so they are enum for H20
			if(i > 94 && i < 100) toReturn += (int) this.features[i] - b.GetFeatureVal(i);
			else toReturn += this.features[i] - b.GetFeatureVal(i);
		}

		return toReturn;
	}
	
//	public Record DifferenceRecord(Record b){
//		String toReturn = "";
//		int diff = this.relevance - b.GetRelevance();
//		for(int i = 0; i < features.length; i++){
//			double k = 0;
//			//boolean values 95-99 produce categorical {-1, 0 , 1}  cast to int so they are enum for H20
//			if(i > 94 && i < 100) {
//				k = (int) this.features[i] - b.GetFeatureVal(i);
//			} else  {
//				k =  this.features[i] - b.GetFeatureVal(i);
//			}
//			// toReturn += String.format("%.4f,", k / Math.abs(diff));
//			toReturn += String.format("%.3f,", k);
//		}
//		String label = "L" + (diff > 0 ? 1 : 0) ;
//				//+ "# " + this.relevance + " & " + b.GetRelevance();
//		toReturn += label + "\n";	
//		
//		String weighted = toReturn;
////		for (int i = 0; i < Math.abs(diff); i++) {
////			weighted += toReturn;
////		}
////		
//		return weighted;
//	}
	
	public String Difference2(Record b){
		String toReturn = "";
		int diff = this.relevance - b.GetRelevance();
		for(int i = 0; i < features.length; i++){
			double k = 0;
			//boolean values 95-99 produce categorical {-1, 0 , 1}  cast to int so they are enum for H20
			if(i > 94 && i < 100) {
				k = (int) this.features[i] - b.GetFeatureVal(i);
			} else  {
				k =  this.features[i] - b.GetFeatureVal(i);
			}
			// toReturn += String.format("%.4f,", k / Math.abs(diff));
			toReturn += String.format("%.3f,", k);
		}
		String label = "L" + (diff > 0 ? 1 : 0) ;
				//+ "# " + this.relevance + " & " + b.GetRelevance();
		toReturn += label + "\n";	
		
		String weighted = toReturn;
//		for (int i = 0; i < Math.abs(diff); i++) {
//			weighted += toReturn;
//		}
//		
		return weighted;
	}

	public String DifferenceNew(Record b){
		String toReturn = "";
		int diff = this.relevance - b.GetRelevance();
		toReturn += diff > 0 ? 1 : 0;
		for(int i = 0; i < features.length; i++){
			toReturn += ",";
			//boolean values 95-99 produce categorical {-1, 0 , 1}  cast to int so they are enum for H20
			if(i > 94 && i < 100) { 
				toReturn += (double) (this.features[i] - b.GetFeatureVal(i)) / (1.0 * diff);
			}
			else toReturn += (double) (this.features[i] - b.GetFeatureVal(i)) / (1.0 * diff);
		}

		return toReturn;
	}
	
	


}