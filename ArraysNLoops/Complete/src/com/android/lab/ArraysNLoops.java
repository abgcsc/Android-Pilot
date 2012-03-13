package com.android.lab;

import java.util.ArrayList;


public class ArraysNLoops 
{
	private String[] notes={"C","D","E","F","G","A","B"};
	private ArrayList<Integer> freq;
	
	public ArraysNLoops()
	{
		freq=new ArrayList<Integer>();
	}
	
	public void fillFreq(ArrayList<Integer> a)
	{
		for(int i=0;i<a.size();i++)
		{
			freq.add(a.get(i));
		}
	}
	
	public int getFreq(String s)
	{
		for(int i=0;i<notes.length;i++)
		{
			if (notes[i].equals(s))
			{
				return freq.get(i);
			}
		}
		return -1;
	}
	
	public void clearAll()
	{
		while(freq.size()>0)
		{
			freq.remove(0);
		}
	}
	
	public int getSize()
	{
		return freq.size();
	}
}
