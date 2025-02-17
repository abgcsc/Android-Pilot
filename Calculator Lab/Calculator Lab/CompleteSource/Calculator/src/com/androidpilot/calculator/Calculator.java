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
   
           // Set button click listener of the keypad adapter
	       mKeypadAdapter.setOnButtonClickListener(new OnClickListener() {
	           public void onClick(View v) {
	               Button btn = (Button) v;
	               // Get the KeypadButton value which is used to identify the
	               // keypad button from the Button's tag
	                   KeypadButton keypadButton = (KeypadButton) btn.getTag();
	   
	                   // Process keypad button
	                   ProcessKeypadInput(keypadButton);
	               }
	       });
   
           mKeypadGrid.setOnItemClickListener(new OnItemClickListener() {
               public void onItemClick(AdapterView<?> parent, View v,
                       int position, long id) {
   
               }
           });
   
       }

	private void ProcessKeypadInput(KeypadButton keypadButton) {
		// Toast.makeText(this, keypadButton.getText(),
		// Toast.LENGTH_SHORT).show();
		String text = keypadButton.getText().toString();
		String currentInput = userInputText.getText().toString();
	
		int currentInputLen = currentInput.length();
		String evalResult = null;
		//double userInputValue = Double.NaN;
	
		switch (keypadButton) {
		case BACKSPACE: // Handle backspace
			// If has operand skip backspace
			if (resetInput)
				return;
	
			int endIndex = currentInputLen - 1;
	
			// There is one character at input so reset input to 0
			if (endIndex < 1) {
				userInputText.setText("0");
			}
			// Trim last character of the input text
			else {
				userInputText.setText(currentInput.subSequence(0, endIndex));
			}
			break;
		case SIGN: // Handle -/+ sign
			// input has text and is different than initial value 0
			if (currentInputLen > 0 && currentInput != "0") {
				// Already has (-) sign. Remove that sign
				if (currentInput.charAt(0) == '-') {
					userInputText.setText(currentInput.subSequence(1,
							currentInputLen));
				}
				// Prepend (-) sign
				else {
					userInputText.setText("-" + currentInput.toString());
				}
			}
			break;
		case CE: // Handle clear input
			userInputText.setText("0");
			break;
		case C: // Handle clear input and stack
			userInputText.setText("0");
			clearStacks();
			break;
		case DECIMAL_SEP: // Handle decimal seperator
			if (hasFinalResult || resetInput) {
				userInputText.setText("0" + mDecimalSeperator);
				hasFinalResult = false;
				resetInput = false;
			} else if (currentInput.contains("."))
				return;
			else
				userInputText.append(mDecimalSeperator);
			break;
		case SQRT:
		case RECIPROC:
			if (resetInput) {
				mInputStack.pop();
				mOperationStack.pop();
			} else {
				if (currentInput.charAt(0) == '-') {
					userInputText.setText("0");
					clearStacks();
					break;
				} 
				mOperationStack.add(currentInput);
			}
	
			mInputStack.add(text);
			mOperationStack.add(text);
	
			dumpInputStack();
			evalResult = evaluateResult(false);
			if (evalResult != null)
				userInputText.setText(evalResult);
	
			resetInput = true;
			break;
		case DIV:
		case PLUS:
		case MINUS:
		case MULTIPLY:
		case PERCENT:
			if (resetInput) {
				mInputStack.pop();
				mOperationStack.pop();
			} else {
				if (currentInput.charAt(0) == '-') {
					mInputStack.add("(" + currentInput + ")");
				}
				else {
						mInputStack.add(currentInput);
					}
				mOperationStack.add(currentInput);
			}
	
			mInputStack.add(text);
			mOperationStack.add(text);
	
			dumpInputStack();
			evalResult = evaluateResult(false);
			if (evalResult != null)
				userInputText.setText(evalResult);
	
			resetInput = true;
			break;
		case CALCULATE:
			if (mOperationStack.size() == 0)
				break;
	
			mOperationStack.add(currentInput);
			evalResult = evaluateResult(true);
			if (evalResult != null) {
				clearStacks();
				userInputText.setText(evalResult);
				resetInput = false;
				hasFinalResult = true;
			}
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
		mInputStack.clear();
		mOperationStack.clear();
		mStackText.setText("");
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
	
		String left = mOperationStack.get(0);
		String operator = mOperationStack.get(1);
		String right = mOperationStack.get(2);
		String tmp = null;
		if (!requestedByUser)
			tmp = mOperationStack.get(3);
	
		double leftVal = Double.parseDouble(left.toString());
		double rightVal = Double.parseDouble(right.toString());
		double result = Double.NaN;
	
		if (operator.equals(KeypadButton.DIV.getText())) {
			result = leftVal / rightVal;
		} else if (operator.equals(KeypadButton.MULTIPLY.getText())) {
			result = leftVal * rightVal;
		} else if (operator.equals(KeypadButton.PLUS.getText())) {
			result = leftVal + rightVal;
		} else if (operator.equals(KeypadButton.MINUS.getText())) {
			result = leftVal - rightVal;
		} else if (operator.equals(KeypadButton.SQRT.getText())) {
			result = StrictMath.sqrt(rightVal);
		} else if (operator.equals(KeypadButton.PERCENT.getText())) {
			result = (rightVal) * (leftVal / 100);
		} else if (operator.equals(KeypadButton.RECIPROC.getText())) {
			result = (1 / rightVal);
		}
	
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