package org.jbls.LexManos.CSV;

import java.util.Enumeration;
import java.util.Hashtable;

public class CSVRecord {

	private Hashtable<String, String> mValues = new Hashtable<String, String>();
	
	public CSVRecord(String[] headers, String[] values){
		for(int x = 0; x < headers.length; x++)
			mValues.put(headers[x].toLowerCase(), values[x]);
	}
	
	public String getString(String name) throws IndexOutOfBoundsException{
		if (!mValues.containsKey(name.toLowerCase()))
			throw new IndexOutOfBoundsException(String.format("CSV Missing index %s", name));
		
		return mValues.get(name.toLowerCase());
	}

	public int getInt(String name) throws IndexOutOfBoundsException, NumberFormatException{
		return Integer.parseInt(getString(name));		
	}
	public double getDouble(String name) throws IndexOutOfBoundsException, NumberFormatException{
		return Double.parseDouble(getString(name));		
	}
	

	public boolean getBool(String name) throws IndexOutOfBoundsException{
		return getString(name).equalsIgnoreCase("True");		
	}	
	public void print(){
		Enumeration<String> en = mValues.keys();
		while(en.hasMoreElements()){
			String s = en.nextElement();
			System.out.println(s + ": " + mValues.get(s));
		}
	}
}
