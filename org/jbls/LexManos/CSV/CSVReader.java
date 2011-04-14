package org.jbls.LexManos.CSV;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class CSVReader {

	private ArrayList<CSVRecord> mRecords = new ArrayList<CSVRecord>();
	private int mVersion = -1;
	
	public CSVReader(String file) throws FileNotFoundException, IOException{
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		
		String line = br.readLine();
		if(line == null)
			return;
		
		String[] cols = splitLine(line);
		int idx = 1;
		while ((line = br.readLine()) != null) {
			idx++;
			String[] values = splitLine(line);
			if (values[0].charAt(0) == '#')
				continue;

			if (values[0].charAt(0) == 'v'){
				mVersion = Integer.parseInt(line.substring(2));
				continue;
			}

			if (idx == 2)
				continue;
			
			if (values.length != cols.length){
				System.out.println("DC: Error, Line: " + idx + " is not the correct width");
				continue;
			}
			mRecords.add(new CSVRecord(cols, values));
		}
		fr.close();
	}
	public Iterator<CSVRecord> getRecords(){
		return mRecords.iterator();
	}
	public int getVersion(){
		return mVersion;
	}
	private String[] splitLine(String line){
		ArrayList<String> values = new ArrayList<String>();
		String tmp = line + ",";
		while(tmp.indexOf(",") > 0){
			if (tmp.startsWith("\"")){
				int idx = tmp.indexOf("\"", 2) + 1;
				
				values.add(tmp.substring(1, idx - 1));

				tmp = tmp.substring(idx+1);
			}else{
				int idx = tmp.indexOf(",");
				if (idx <= 0)
					idx = tmp.length();
				values.add(tmp.substring(0, idx));
				tmp = tmp.substring(idx+1);
			}
		}
		
		if (line.endsWith(",")) //this is a hack, I need to figure out whats causing the bug above.
			values.add("");
		
		String[] tarr = new String[values.size()];
		values.toArray(tarr);
		return tarr;
	}
	public static String join(String[] strings, String separator) {
	    StringBuffer sb = new StringBuffer();
	    for (int i=0; i < strings.length; i++) {
	        if (i != 0) sb.append(separator);
	  	    sb.append(strings[i]);
	  	}
	  	return sb.toString();
	}
}
