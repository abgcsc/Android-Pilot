package com.example.android.nfc;

public class ScavengerInfo
{
	String m_string;
	int m_current_index;
	
	public ScavengerInfo(String string)
	{
		m_string = string;
	}
	
	double getLatitude()
	{
		String temp_string = new String("");
		
		for(int i = 0; i < m_string.length(); i++)
		{
			m_current_index = i;
			if(m_string.charAt(i) == '\n')
			{
				m_current_index += 1;
				break;
			}
			else
			{
				temp_string = temp_string + m_string.charAt(i);
			}
		}
		double return_d = Double.parseDouble(temp_string);
		return return_d;
	}
	
	double getLongitude()
	{
		String temp_string = new String("");
		
		for(int i = m_current_index; i < m_string.length(); i++)
		{
			m_current_index = i;
			
			if(m_string.charAt(i) == '\n')
			{
				break;
			}
			else
			{
				temp_string = temp_string + m_string.charAt(i);
			}
		}
		double return_d = Double.parseDouble(temp_string);
		return return_d;
	}
	
	String getString()
	{
		return m_string;
	}
}