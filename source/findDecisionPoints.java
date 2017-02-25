import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/*
 * to get the decision points
 */
public class findDecisionPoints {
	static ArrayList<String> decisionPoints = new ArrayList<String>();
	
	/*
	 * to get Decision Points
	 */
	public static ArrayList<String> getDecisionPoints(){
		HashMap<String,String> event_trans = new HashMap<String,String>();
		HashMap<String,Integer> event_trans_freq = new HashMap<String,Integer>();
		
		try {
			FileReader fileread = new FileReader("data_aware_pnml_m16_821.pnml");
			BufferedReader bufferedReader = new BufferedReader(fileread);
			String line=null;
			while ((line = bufferedReader.readLine()) != null) {
				if(line.contains("<transition id=")){
					String[] temp_line=line.split("\\=");
					String trans_id=temp_line[1].substring(1, temp_line[1].length()-2);
//					System.out.println(trans_id);
					bufferedReader.readLine();
					String name_line=bufferedReader.readLine().trim();
					String trans_name=name_line.substring(6,name_line.length()-7 );
					trans_name=trans_name.substring(0, trans_name.indexOf('+'));
					event_trans.put(trans_id, trans_name);
					event_trans_freq.put(trans_id, 0);
				}
				if(line.contains("<arc id")){
					break;
				}
			}
			String[] temp_line = line.split("source=");
			int target_occur = temp_line[1].indexOf('t');
			String source = temp_line[1].substring(1, target_occur-2);
			if(event_trans.containsKey(source)){
			   int value = event_trans_freq.get(source);
			   value++;
			   event_trans_freq.put(source, value);
			}
			while ((line = bufferedReader.readLine()) != null) {
				if(line.contains("<arc id=")){
					temp_line = line.split("source=");
					target_occur = temp_line[1].indexOf('t');
					source = temp_line[1].substring(1, target_occur-2);
					if(event_trans.containsKey(source)){
					   int value = event_trans_freq.get(source);
					   value++;
					   event_trans_freq.put(source, value);
					}
				}
			}
			for (String key : event_trans_freq.keySet()) {
				if (event_trans_freq.get(key) > 1) {
					decisionPoints.add(event_trans.get(key));
				}
			}

			bufferedReader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decisionPoints;
	}

}
