package com.androidpilot.calculator;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Stack;
import java.lang.StrictMath;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.view.View;
import android.view.View.OnClickListener;

public class Calculator extends Activity {
	 GridView mKeypadGrid;
	 TextView userInputText;
	 TextView memoryStatText;
	 Stack<String> mInputStack;
	 Stack<String> mOperationStack;
	 KeypadAdapter mKeypadAdapter;
	 TextView mStackText;
	 boolean resetInput = false;
	 boolean hasFinalResult = false;
	 String mDecimalSeperator;
	 double memoryValue = Double.NaN;

	 /** Called when the activity is first created. */
	 @Override
     public void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
   
           DecimalFormat currencyFormatter = (DecimalFormat) NumberFormat
                   .getInstance();
           char decimalSeperator = currencyFormatter.getDecimalFormatSymbols()
                   .getDecimalSeparator();
           mDecimalSeperator = Character.toString(decimalSeperator);
   
           setContentView(R.layout.main);
   
           // Create the stack
           mInputStack = new Stack<String>();
           mOperationStack = new Stack<String>();
   
           // Get reference to the keypad button GridView
           mKeypadGrid = (GridView) findViewById(R.id.grdButtons);
   
           // Get reference to the user input TextView
           userInputText = (TextView) findViewById(R.id.txtInput);
           userInputText.setText("0");
   
           mStackText = (TextView) findViewById(R.id.txtStack);
   
           // Create Keypad Adapter
           mKeypadAdapter = new KeypadAdapter(this);
   
           // Set adapter of the keypad grid
           mKeypadGrid.setAdapter(mKeypadAdapter);
   
           // Set button click listener of the keypad adapter.  You will need to separate listeners here.  Hint use anonymous inner classes.
           
       }

	private void ProcessKeypadInput(KeypadButton keypadButton) {
		String text = keypadButton.getText().toString();
		String currentInput = userInputText.getText().toString();
	
		int currentInputLen = currentInput.length();
		String evalResult = null;
		//double userInputValue = Double.NaN;
	
		
		
		/* The cases for the switch below need to be filled in to handle the input for each case.  Hint some of the cases use the same code as other cases.
		 * Example:  DIV, PLUS, MINUS, MULTIPLY, and PERCENT all use the same code to handle input.  You can place that code in the PERCENT case and all 5 will use it, because the 
		 * first 4 don't have break statements.
		 */
		
		switch (keypadButton) {
		case BACKSPACE: // Handle backspace
			break;
		case SIGN: // Handle -/+ sign
			break;
		case CE: // Handle clear input
			break;
		case C: // Handle clear input and stack
			break;
		case DECIMAL_SEP: // Handle decimal seperator
			break;
		case SQRT:
		case RECIPROC:
			break;
		case DIV:
		case PLUS:
		case MINUS:
		case MULTIPLY:
		case PERCENT:
			break;
		case CALCULATE:
			break;
		default:
			if (Character.isDigit(text.charAt(0))) {
				if (currentInput.equals("0") || resetInput || hasFinalResult) {
					userInputText.setText(text);
					resetInput = false;
					hasFinalResult = false;
				} else {
					userInputText.append(text);
					resetInput = false;
				}
	
			}
			break;
	
		}
	
	}
	
	private void clearStacks() {
		
	}
	
	private void dumpInputStack() {
		Iterator<String> it = mInputStack.iterator();
		StringBuilder sb = new StringBuilder();
	
		while (it.hasNext()) {
			CharSequence iValue = it.next();
			sb.append(iValue);
	
		}
	
		mStackText.setText(sb.toString());
	}
	
	private String evaluateResult(boolean requestedByUser) {
		if ((!requestedByUser && mOperationStack.size() != 4)
				|| (requestedByUser && mOperationStack.size() != 3))
			return null;
	
		//Only one of the strings below is supposed to be initialized to null.  Change the other 3 appropriately.
		String left = null;
		String operator = null;
		String right = null;
		String tmp = null;
		if (!requestedByUser)
			tmp = mOperationStack.get(3);
	
		double leftVal = Double.parseDouble(left.toString());
		double rightVal = Double.parseDouble(right.toString());
		double result = Double.NaN;
	
		if (operator.equals(KeypadButton.DIV.getText())) {
		//Fill in code for division functionality.
		}
		
		//Place code for other operations here. Such as multiplication, addition, and subtraction. For certain cases you will need to use the Java Strict Math API.
	
		String resultStr = doubleToString(result);
		if (resultStr == null)
			return null;
	
		mOperationStack.clear();
		if (!requestedByUser) {
			mOperationStack.add(resultStr);
			mOperationStack.add(tmp);
		}
	
		return resultStr;
	}
	
	private String doubleToString(double value) {
		if (Double.isNaN(value))
			return null;
	
		long longVal = (long) value;
		if (longVal == value)
			return Long.toString(longVal);
		else
			return Double.toString(value);
	
	}
	
	private double tryParseUserInput() {
		String inputStr = userInputText.getText().toString();
		double result = Double.NaN;
		try {
			result = Double.parseDouble(inputStr);
		} catch (NumberFormatException nfe) {}
		
		return result;
	
	}
}