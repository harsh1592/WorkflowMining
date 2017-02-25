import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;

/*
 * To extract the data for each decision point
 */
public class dataExtract {
	static int window_size=3;
	static int count=0;
	static HashSet<String> subsequence = new HashSet<String>();
	static HashMap<String,Integer> subMap = new HashMap<String,Integer>();
	static ArrayList<String> decisionPoints = new ArrayList<String>();
	
	public static void main(String args[]){

		try {
			//find out the decision points
			decisionPoints = findDecisionPoints.getDecisionPoints();
//			decisionPoints = new ArrayList<String>();
//			decisionPoints.add("Pain clinic");
			//data extraction for each decision point 
			for(int i=0;i<decisionPoints.size();i++){
				subsequence = new HashSet<String>();
				subMap = new HashMap<String,Integer>();
				count = 0;
				if(decisionPoints.get(i).equals("surgery   urology clinic"))
					findSubSequences("surgery &amp; urology clinic");
				else if(decisionPoints.get(i).equals("Obstetrics Gynaecology clinic"))
					findSubSequences("Obstetrics &amp; Gynaecology clinic");
				else findSubSequences(decisionPoints.get(i));
				System.out.println(decisionPoints.get(i));
				System.out.println("Size: " + subsequence.size());
//				System.out.println("MapSize: " + subMap.size());
				for( String s: subsequence){
					System.out.println(s+"	"+subMap.get(s));
				}
				System.out.println("---------------");
//				if(i==3)
//					break;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	/*
	 * To find out the subsequences formed by each decision point and extract the data
	 * for that sub sequence
	 */
	public static void findSubSequences(String decision_point)throws Exception{
		//input file
		FileReader fileread = new FileReader("M16_821.xes");
		ArrayList<String> next_class = new ArrayList<String>();
		//to write the file 
		PrintWriter write;
		if(decision_point.equals("Recovery room / high care"))
			write = new PrintWriter("RecoveryRoom.txt");
		else write = new PrintWriter(decision_point+".txt");
//		FileReader fileread = new FileReader("dummy.txt");
		BufferedReader bufferedReader =  new BufferedReader(fileread);
		Queue<String> subsequenceQueue = new LinkedList<String>();
		String event="";
		boolean trace_level=false,event_level=false;
		String line=null;
		ArrayList<String> event_attribute = initialiseVariables("events");
		String[] event_attribute_values = new String[event_attribute.size()];
		ArrayList<String> trace_attribute = initialiseVariables("trace");
		String[] trace_attribute_values = new String[trace_attribute.size()];
		int counter = 0;boolean found = false;
		int y=0;
		//reading the input file to find out the sub-sequences 
		while ((line = bufferedReader.readLine()) != null) {
			if(line.contains("<trace>")){
				found = false;y=0;
				trace_attribute_values = new String[trace_attribute.size()];
				trace_level=true;
				resetQueue(subsequenceQueue);
				//reading trace attributes
				while((line = bufferedReader.readLine()) != null){
					if(line.contains("<event>"))
						break;
					else{
						String t = line.substring(line.indexOf("key=")+5,line.indexOf("value=")-2);
						if(trace_attribute.contains(t)){
							int index = trace_attribute.indexOf(t);
							String value =  line.substring(line.indexOf("value=")+7,line.indexOf(">")-2);
							trace_attribute_values[index] = value;
						}
					}
				}
			}
			//for each trace
			if(trace_level){
				if(line.contains("<event")){
					event_level=true;
				}
				if(event_level){
					//storing first 2 lines in event
					if((line.contains("Specialism code"))||(line.contains("category:name"))){				
						String t = line.substring(line.indexOf("key=")+5,line.indexOf("value=")-2);
						if(event_attribute.contains(t)){
							int index = event_attribute.indexOf(t);
							String value =  line.substring(line.indexOf("value=")+7,line.indexOf(">")-2);
							event_attribute_values[index] = value;
			
						}
					}
					if(line.contains("concept:name")){
						String temp_event[]=line.split("value=");
						event=temp_event[1].substring(1, temp_event[1].length()-3);
						if(found == true){
							next_class.add(event);
							found = false;
						}
						if((count==window_size) && (event.equals(decision_point))){
							subsequenceQueue.remove();
							subsequenceQueue.add(event);
							//add to the hashset
							String sub_temp="";
							for(int i=0;i<window_size;i++){
								sub_temp+=subsequenceQueue.peek()+"|";
								subsequenceQueue.remove();
							}
							if(subMap.containsKey(sub_temp)){
								int value = subMap.get(sub_temp);
								subMap.put(sub_temp, value+1);
							}else subMap.put(sub_temp, 1);
//							System.out.println("found subsequence");
							
							//add the other event attributes
							int h = 0;
							while(h < 6){
								line = bufferedReader.readLine();
								String t1 = line.substring(line.indexOf("key=")+5,line.indexOf("value=")-2);
								if(event_attribute.contains(t1)){
									int index = event_attribute.indexOf(t1);
									String value =  line.substring(line.indexOf("value=")+7,line.indexOf(">")-2);
									event_attribute_values[index] = value;
								}
								h++;
							}
							//write trace to file 
//							if(y==0){
								write.println("");;
								write.print("trace: ");
								for(int ij = 0; ij < trace_attribute_values.length;ij++){
									write.print(trace_attribute_values[ij]+",");
								}
								write.print("||||");
								write.print("event:");
//							}
//							y++;
							counter++;
							found = true;
							//write event to file
							
							for(int ij = 0; ij < event_attribute_values.length;ij++){
								write.print(event_attribute_values[ij]+",");
							}
//							write.print("|");
							
							subsequence.add(sub_temp);
							resetQueue(subsequenceQueue);

						}
						else if(count==window_size){
							subsequenceQueue.remove();
							subsequenceQueue.add(event);
						}
						else{
							subsequenceQueue.remove();
							subsequenceQueue.add(event);
							count++;
							if(event.equals(decision_point)&&(count==window_size)){
								String sub_temp="";
								for(int i=0;i<window_size;i++){
									sub_temp+=subsequenceQueue.peek()+"|";
									subsequenceQueue.remove();
								}
								if(subMap.containsKey(sub_temp)){
									int value = subMap.get(sub_temp);
									subMap.put(sub_temp, value+1);
								}else subMap.put(sub_temp, 1);
//								System.out.println("found subsequence");
								
								//add the other event attributes
								int h = 0;
								while(h < 6){
									line = bufferedReader.readLine();
									String t1 = line.substring(line.indexOf("key=")+5,line.indexOf("value=")-2);
									if(event_attribute.contains(t1)){
										int index = event_attribute.indexOf(t1);
										String value =  line.substring(line.indexOf("value=")+7,line.indexOf(">")-2);
										event_attribute_values[index] = value;
									}
									h++;
								}
								//write event to file 
//								if(y==0){
									write.println("");;
									write.print("trace: ");
									for(int ij = 0; ij < trace_attribute_values.length;ij++){
										write.print(trace_attribute_values[ij]+",");
									}
									write.print("||||");
									write.print("event:");
//								}
//								y++;
								counter++;
								found = true;
								//write event to file
								
								for(int ij = 0; ij < event_attribute_values.length;ij++){
									write.print(event_attribute_values[ij]+",");
								}
//								write.println("");
								
								subsequence.add(sub_temp);
								resetQueue(subsequenceQueue);
							}
						}
					}
					if(line.contains("</event>")){
						event_level=false;
						event_attribute_values = new String[event_attribute.size()];
					}
				}
				if(line.contains("</trace")){
					trace_level=false;
					if(found == true){
						next_class.add("Success");
						found = false;
					}
				}
				//add to the queue
			}

		}
//		System.out.println("Total Occurence in Map "+ subMap.get() );
		System.out.println("total found is "+counter);
		bufferedReader.close();
		write.flush();
		write.close();
		PrintWriter write2;
		if(decision_point.equals("Recovery room / high care"))
			write2 = new PrintWriter("RecoveryRoom_nextClass.txt");
		else write2 =  new PrintWriter(decision_point+"_nextClass.txt");
		for(int i=0;i<next_class.size();i++){
			write2.println(next_class.get(i));
		}
		write2.flush();
		write2.close();
		
	}

	/*
	 * To initialise the arraylist with attribute values
	 */
	public static ArrayList<String> initialiseVariables(String type){
		ArrayList<String> attributes = null;
		if(type.equals("events")){
			attributes = new ArrayList<String>();
			attributes.add("Specialism code");
			attributes.add("category:name");
			attributes.add("Number of executions");
			attributes.add("Producer code");
			attributes.add("Section");
			attributes.add("Activity code");
		}
		if(type.equals("trace")){
			attributes = new ArrayList<String>();
			attributes.add("Treatment code");
			attributes.add("Treatment code:1");
			attributes.add("Treatment code:2");
			attributes.add("Treatment code:3");
			attributes.add("Treatment code:4");
			attributes.add("Diagnosis code");
			attributes.add("Diagnosis code:1");
			attributes.add("Diagnosis code:2");
			attributes.add("Diagnosis code:3");
			attributes.add("Diagnosis code:4");		    
			attributes.add("Specialism code");
			attributes.add("Specialism code:1");
			attributes.add("Specialism code:2");
			attributes.add("Specialism code:3");
			attributes.add("Specialism code:4");
			attributes.add("Diagnosis Treatment Combination ID");
			attributes.add("Diagnosis Treatment Combination ID:1");
			attributes.add("Diagnosis Treatment Combination ID:2");
			attributes.add("Diagnosis Treatment Combination ID:3");
			attributes.add("Diagnosis Treatment Combination ID:4");
			attributes.add("Age");
			attributes.add("Age:1");
			attributes.add("Age:2");
			attributes.add("Age:3");
			attributes.add("Age:4");
		}

		return attributes;
	}

	/*
	 * to reste the queue
	 */
	public static Queue<String> resetQueue(Queue<String> subsequenceQueue){
		subsequenceQueue.clear();
		for(int i=0;i<window_size;i++){
			subsequenceQueue.add("#");
		}
		count=0;
		return subsequenceQueue;
	}

}
