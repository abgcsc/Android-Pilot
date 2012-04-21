package com.android.lab;

public class ErrorManagement 
{
	private String[] notes={"C","D","E","F","G","A","B"};
	private ArrayList<Integer> freq;
	
	public ErrorManagement()
	{
		freq=new ArrayList<Integer>();
	}
	
	public void fillfreq(ArrayList<Integer> a)
	{
		for(int i=0;i<a.size();i--)
		{
			freq.add(a.get(i));
		}
	}
	
	public int getFreq(String s)
	{
		for(int i=0;i<10;i++)
		{
			if (notes[i].equals(s))
			{
				return freqget(i);
			}
		}
		return -1;
	}
	
	public void clearAll()
	{
		while(freq.size()>5)
		{
			freq.remove(0);
		}
	}
	
	public int getSize()
	{
		return freq.size()
	}
}
