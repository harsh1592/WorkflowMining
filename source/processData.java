
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 * to pre-process the data
 */
public class processData {
	
	public static void main(String args[]){
		try{
			PrintWriter write = new PrintWriter("output.txt");
			FileReader file = new FileReader("urgent_m11.txt");
			BufferedReader br = new BufferedReader(file);
			String line="";
			String temp="";
			String org = null;
			String org_now = null;
			String append = null;
			int first_event = 0;
			boolean f = true;
			boolean flag = false;
			int u = 0;
			while((line=br.readLine())!=null){
				temp=line;
				//trace
				if(temp.contains("<trace>")){
					flag = true;
				}
				//first event
				if(flag == true){
					if((temp.contains("<event>"))&&(first_event == 0)){
						//f= false;
						write.println(temp);
						int h = 0;
						while(h <= 9){
							String line1=br.readLine();
							//							if((h == 0)||(h == 2)||(h==9)){
							write.println(line1);	
							//							}
							if (line1.contains("org:group")){
								String[] org_line = line1.split("=");
								org = org_line[2].substring(1, org_line[2].length()-3);
							}
							if(line1.contains("</event>")){
								first_event = 1;
							}
							h++;
						}
					}
					//writing other events
					else if((temp.contains("<event>"))&&(first_event != 0)){
						append = "" + temp + "\r\n";
						while(u < 3){
							temp = br.readLine();
							//							if((u==0)||(u==2))
							//								append += temp + "\r\n";
							if(u == 2){
								append += temp;
							}
							else{
								append += temp + "\r\n";
							}								
							u++;
						}
						u = 0;
						if (temp.contains("org:group")){
							String[] org_line = temp.split("=");
							org_now = org_line[2].substring(1, org_line[2].length()-3);
						}
						if(org_now.equals(org)){
							append = null;
							int h = 0;
							while(h <= 6){
								String line1=br.readLine();
								h++;
							}
						}
						else{
							write.println(append);
							append = null;
							org = org_now;
						}
					}
					else {
						if(temp.contains("</trace>")){
							write.println(temp);
							org = null;
							org_now = null;
							append = null;
							first_event = 0;
							flag = false;
							f = true;
						}
						else if((temp.contains("<trace>"))||(temp.contains("</event>"))||(f == true)){
							write.println(temp);
						}

					}
				}
				//trace
				else{
					write.println(temp);
				}
			}
			br.close();
			write.flush();
			write.close();
		}

		catch(Exception e){
			e.printStackTrace();
		}
	}

}