package com.pxirou.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView calculatorScreen, memoryIndicator, textView, textView2, textView3, exchangeResult;
    Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9,
            buttonDot, buttonEqual, buttonAddition, buttonSubtraction, buttonMultiplication,
            buttonDivision, buttonSquareRoot, buttonPercent, buttonMemoryAddition,
            buttonMemorySubtraction, buttonMemoryRecall, buttonClearEntry, buttonConvert;
    Spinner spinner1, spinner2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calculatorScreen = findViewById(R.id.calculator_screen);
        memoryIndicator = findViewById(R.id.memory_indicator);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        exchangeResult = findViewById(R.id.exchange_result);
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
        button0 = findViewById(R.id.button_0);
        button1 = findViewById(R.id.button_1);
        button2 = findViewById(R.id.button_2);
        button3 = findViewById(R.id.button_3);
        button4 = findViewById(R.id.button_4);
        button5 = findViewById(R.id.button_5);
        button6 = findViewById(R.id.button_6);
        button7 = findViewById(R.id.button_7);
        button8 = findViewById(R.id.button_8);
        button9 = findViewById(R.id.button_9);
        buttonDot = findViewById(R.id.button_dot);
        buttonEqual = findViewById(R.id.button_equal);
        buttonAddition = findViewById(R.id.button_addition);
        buttonSubtraction = findViewById(R.id.button_subtraction);
        buttonMultiplication = findViewById(R.id.button_multiplication);
        buttonDivision = findViewById(R.id.button_division);
        buttonMemoryAddition = findViewById(R.id.button_memory_addition);
        buttonMemorySubtraction = findViewById(R.id.button_memory_subtraction);
        buttonMemoryRecall = findViewById(R.id.button_memory_recall);
        buttonClearEntry = findViewById(R.id.button_clear_entry);
        buttonSquareRoot = findViewById(R.id.button_square_root);
        buttonPercent = findViewById(R.id.button_percent);
        buttonConvert = findViewById(R.id.button_convert);

    }

    public void button0Clicked(View view) {
    }

    public void button1Clicked(View view) {
    }

    public void button2Clicked(View view) {
    }

    public void button3Clicked(View view) {
    }

    public void button4Clicked(View view) {
    }

    public void button5Clicked(View view) {
    }

    public void button6Clicked(View view) {
    }

    public void button7Clicked(View view) {
        calculatorScreen.setText(calculatorScreen.getText().toString() + "7");
    }

    public void button8Clicked(View view) {
    }

    public void button9Clicked(View view) {
    }

    public void buttonDotClicked(View view) {
    }

    public void buttonEqualClicked(View view) {
    }

    public void buttonAdditionClicked(View view) {
    }

    public void buttonSubtractionClicked(View view) {
    }

    public void buttonMultiplicationClicked(View view) {
    }

    public void buttonDivisionClicked(View view) {
    }

    public void buttonSquareRootClicked(View view) {
    }

    public void buttonPercentClicked(View view) {
    }

    public void buttonClearEntryClicked(View view) {
    }

    public void buttonMemoryAdditionClicked(View view) {
    }

    public void buttonMemorySubtractionClicked(View view) {
    }

    public void buttonMemoryRecallClicked(View view) {
    }

    public void buttonConvertClicked(View view) {
    }
}
