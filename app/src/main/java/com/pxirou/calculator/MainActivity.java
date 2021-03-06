package com.pxirou.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pxirou.calculator.pojo.ErrorResponse;
import com.pxirou.calculator.pojo.RetrieveCurrency;
import com.pxirou.calculator.networking.GetDataService;
import com.pxirou.calculator.networking.RetrofitClientInstance;

import java.io.IOException;
import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TextView calculatorScreen, memoryIndicator, exchangeResult, operatorIndicator;
    Spinner spinner1, spinner2;
    StateIndex stateIndex;
    boolean additionClicked, subtractionClicked, multiplicationClicked, divisionClicked, memoryValueHadJustClickedOnce, operationHadJustClicked, equalHadJustClicked;
    byte dotIndex, tempValueShownCharacters;
    double permanentValue, tempValue, memoryValue, lastValue;
    String[] currencies;
    String procrustes; // You can easily understand why i named this variable like this :-)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calculatorScreen = findViewById(R.id.calculator_screen);
        memoryIndicator = findViewById(R.id.memory_indicator);
        operatorIndicator = findViewById(R.id.operator_indicator);
        exchangeResult = findViewById(R.id.exchange_result);
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);

        stateIndex = StateIndex.STANDBY;
        CurrencyListInitializer();
        SpinnerChoicesInitializer();
    }

    public void button0Clicked(View view) {
        numberClicked(0);
    }

    public void button1Clicked(View view) {
        numberClicked(1);
    }

    public void button2Clicked(View view) {
        numberClicked(2);
    }

    public void button3Clicked(View view) {
        numberClicked(3);
    }

    public void button4Clicked(View view) {
        numberClicked(4);
    }

    public void button5Clicked(View view) {
        numberClicked(5);
    }

    public void button6Clicked(View view) {
        numberClicked(6);
    }

    public void button7Clicked(View view) {
        numberClicked(7);
    }

    public void button8Clicked(View view) {
        numberClicked(8);
    }

    public void button9Clicked(View view) {
        numberClicked(9);
    }

    public void buttonDotClicked(View view) {
        equalHadJustClicked = false;
        if (memoryValueHadJustClickedOnce) {
            tempValueInitializer();
            memoryValueHadJustClickedOnce = false;
        }
        if (tempValueShownCharacters < 10) {
            if (stateIndex == StateIndex.EQUAL_CLICKED) {
                stateIndex = StateIndex.STANDBY;
            }
            if (dotIndex == 0) {
                if (tempValue == 0.0) {
                    tempValueShownCharacters++;
                }
                calculatorScreen.setText(getString(R.string.calculator_screen_general, "" + (int) Math.floor(tempValue), "."));
                dotIndex++;
                tempValueShownCharacters++;
            }
        } else {
            Toast.makeText(getApplicationContext(), "No more available space", Toast.LENGTH_LONG).show();
            vibrate();
        }
    }

    private void numberClicked(int i) {
        equalHadJustClicked = false;
        operationHadJustClicked = false;
        if (memoryValueHadJustClickedOnce) {
            tempValueInitializer();
            memoryValueHadJustClickedOnce = false;
        }
        if (tempValueShownCharacters < 10) {
            if (stateIndex == StateIndex.EQUAL_CLICKED) {
                stateIndex = StateIndex.STANDBY;
                operatorIndicator.setText("");
            }
            if (dotIndex == 0) {
                tempValue = tempValue * 10 + i;
            } else {
                //correct floating point error
                tempValue = (Math.round((tempValue + i / Math.pow(10, dotIndex)) * (Math.pow(10, dotIndex))) / Math.pow(10, dotIndex));
                dotIndex++;
                // adds zeros at the end of decimal (if zeros tapped)
                if (i == 0) {
                    checkObsoleteZeros();
                    return;
                }
            }
            updateView();
            tempValueShownCharacters++;
        } else {
            Toast.makeText(getApplicationContext(), "No more available space", Toast.LENGTH_LONG).show();
            vibrate();
        }
    }

    private void dotInitializer() {
        dotIndex = 0;
    }

    private void tempValueInitializer() {
        tempValue = 0;
        tempValueShownCharacters = 0;
    }

    private void updateView() {
        if (dotIndex == 0 && tempValue == Math.floor(tempValue) && !Double.isInfinite(tempValue)) {
            calculatorScreen.setText(getString(R.string.calculator_screen_general, "" + (long) Math.floor(tempValue), ""));
        } else {
            calculatorScreen.setText(getString(R.string.calculator_screen_general, "" + tempValue, ""));
        }
        shownDigitsLimiter();
    }

    private void showOperatorResult() {
        if ((permanentValue == Math.floor(permanentValue)) && !Double.isInfinite(permanentValue)) {
            calculatorScreen.setText(getString(R.string.calculator_screen_general, "" + (long) Math.floor(permanentValue), ""));
        } else {
            calculatorScreen.setText(getString(R.string.calculator_screen_general, "" + permanentValue, ""));
        }
        if (Math.abs(permanentValue) > Long.parseLong("9999999999")) {
            checkOverflow();
        } else if (Math.abs(permanentValue) < Double.parseDouble("0.00000001")) {
            checkUnderflow();
        }
        shownDigitsLimiter();
    }

    private void checkObsoleteZeros() {
        procrustes = "" + tempValue;
        int i = procrustes.indexOf('.') + dotIndex;
        if (i > procrustes.length()) {
            calculatorScreen.setText(getString(R.string.calculator_screen_general, calculatorScreen.getText().toString(), "0"));
        }
    }

    //based on the 10 digits screen capacity limit
    private void checkOverflow() {
        Toast.makeText(getApplicationContext(), "Number overflow!", Toast.LENGTH_LONG).show();
        permanentValue = 0.0;
        stateIndex = StateIndex.EQUAL_CLICKED;
        operatorInitializer();
        vibrate();
    }

    //based on the 10 digits screen capacity limit
    private void checkUnderflow() {
        Toast.makeText(getApplicationContext(), "Number underflow!", Toast.LENGTH_LONG).show();
        permanentValue = 0.0;
        stateIndex = StateIndex.EQUAL_CLICKED;
        operatorInitializer();
        vibrate();
    }

    private void shownDigitsLimiter() {
        //limits digits to maximum of 10
        procrustes = calculatorScreen.getText().toString();
        if (procrustes.length() > 10) {
            procrustes = procrustes.substring(0, 10);
            calculatorScreen.setText(procrustes);
        }
    }

    private void operatorInitializer() {
        additionClicked = false;
        subtractionClicked = false;
        multiplicationClicked = false;
        divisionClicked = false;
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        assert vibrator != null;
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(100);
        }
    }

    public void buttonAdditionClicked(View view) {
        memoryValueHadJustClickedOnce = false;
        equalHadJustClicked = false;
        operatorIndicator.setText("+");
        if (stateIndex == StateIndex.EQUAL_CLICKED) {
            tempValueInitializer();
            operatorInitializer();
            dotInitializer();
            additionClicked = true;
            stateIndex = StateIndex.OPERATOR_CLICKED;
        } else if (stateIndex == StateIndex.OPERATOR_CLICKED) {
            doOperation();
            tempValueInitializer();
            operatorInitializer();
            additionClicked = true;
            dotInitializer();
        } else if (stateIndex == StateIndex.STANDBY) {
            stateIndex = StateIndex.OPERATOR_CLICKED;
            permanentValue = tempValue;
            tempValueInitializer();
            additionClicked = true;
        }
    }

    public void buttonSubtractionClicked(View view) {
        memoryValueHadJustClickedOnce = false;
        equalHadJustClicked = false;
        operatorIndicator.setText("-");
        if (stateIndex == StateIndex.EQUAL_CLICKED) {
            tempValueInitializer();
            operatorInitializer();
            dotInitializer();
            subtractionClicked = true;
            stateIndex = StateIndex.OPERATOR_CLICKED;
        } else if (stateIndex == StateIndex.OPERATOR_CLICKED) {
            doOperation();
            tempValueInitializer();
            operatorInitializer();
            subtractionClicked = true;
            dotInitializer();
        } else if (stateIndex == StateIndex.STANDBY) {
            stateIndex = StateIndex.OPERATOR_CLICKED;
            permanentValue = tempValue;
            tempValueInitializer();
            subtractionClicked = true;
        }
    }

    public void buttonMultiplicationClicked(View view) {
        memoryValueHadJustClickedOnce = false;
        equalHadJustClicked = false;
        if (stateIndex == StateIndex.EQUAL_CLICKED) {
            tempValueInitializer();
            operatorInitializer();
            dotInitializer();
            multiplicationClicked = true;
            stateIndex = StateIndex.OPERATOR_CLICKED;
        } else if (stateIndex == StateIndex.OPERATOR_CLICKED) {
            doOperation();
            tempValueInitializer();
            operatorInitializer();
            multiplicationClicked = true;
            dotInitializer();
        } else if (stateIndex == StateIndex.STANDBY) {
            stateIndex = StateIndex.OPERATOR_CLICKED;
            permanentValue = tempValue;
            tempValueInitializer();
            multiplicationClicked = true;
        }
        operationHadJustClicked = true;
        operatorIndicator.setText("x");
    }

    public void buttonDivisionClicked(View view) {
        memoryValueHadJustClickedOnce = false;
        equalHadJustClicked = false;
        if (stateIndex == StateIndex.EQUAL_CLICKED) {
            tempValueInitializer();
            operatorInitializer();
            dotInitializer();
            divisionClicked = true;
            stateIndex = StateIndex.OPERATOR_CLICKED;
        } else if (stateIndex == StateIndex.OPERATOR_CLICKED) {
            doOperation();
            tempValueInitializer();
            operatorInitializer();
            divisionClicked = true;
            dotInitializer();
        } else if (stateIndex == StateIndex.STANDBY) {
            stateIndex = StateIndex.OPERATOR_CLICKED;
            permanentValue = tempValue;
            tempValueInitializer();
            divisionClicked = true;
        }
        operationHadJustClicked = true;
        operatorIndicator.setText(":");
    }

    private void doOperation() {
        if (!operationHadJustClicked) {
            if (additionClicked) {
                permanentValue += tempValue;
            } else if (subtractionClicked) {
                permanentValue -= tempValue;
            } else if (multiplicationClicked) {
                if (operatorIndicator.getText().toString().equals("x") && tempValue == 0.0) {
                    tempValue = 1;
                }
                permanentValue *= tempValue;
            } else if (divisionClicked) {
                if (operatorIndicator.getText().toString().equals(":") && tempValue == 0.0) {
                    tempValue = 1;
                }
                if (tempValue != 0.0) {
                    permanentValue /= tempValue;
                } else {
                    divisionBy0();
                    return;
                }
            }
            showOperatorResult();
        }
    }

    public void buttonEqualClicked(View view) {
        //keeps lastValue and use it in case of consecutive equals
        if (equalHadJustClicked) {
            tempValue = lastValue;
            stateIndex = StateIndex.OPERATOR_CLICKED;
        }
        lastValue = tempValue;
        operatorIndicator.setText("=");
        equalHadJustClicked = true;
        if (stateIndex == StateIndex.OPERATOR_CLICKED) {
            if (additionClicked)
                permanentValue += tempValue;
            else if (subtractionClicked)
                permanentValue -= tempValue;
            else if (multiplicationClicked)
                permanentValue *= tempValue;
            else if (divisionClicked) {
                if (tempValue != 0.0) {
                    permanentValue /= tempValue;
                } else {
                    divisionBy0();
                    return;
                }
            }
            showOperatorResult();
            dotInitializer();
            stateIndex = StateIndex.EQUAL_CLICKED;
            tempValueInitializer();
        }
    }

    private void divisionBy0() {
        calculatorScreen.setText(getString(R.string.calculator_screen_general, "Err : ", "Div by 0"));
        Toast.makeText(getApplicationContext(), "Division by 0 not permitted!", Toast.LENGTH_LONG).show();
        permanentValue = 0.0;
        stateIndex = StateIndex.EQUAL_CLICKED;
        operatorInitializer();
        vibrate();
    }

    public void buttonSquareRootClicked(View view) {
        equalHadJustClicked = false;
        memoryValueHadJustClickedOnce = false;
        if (stateIndex == StateIndex.EQUAL_CLICKED) {
            permanentValue = Math.sqrt(permanentValue);
            showOperatorResult();
        } else if (stateIndex == StateIndex.OPERATOR_CLICKED) {
            tempValue = Math.sqrt(tempValue);
            updateView();
            dotInitializer();
        } else {
            tempValue = Math.sqrt(tempValue);
            updateView();
            dotInitializer();
        }
    }

    public void buttonPercentClicked(View view) {
        equalHadJustClicked = false;
        memoryValueHadJustClickedOnce = false;
        tempValue = permanentValue * tempValue / 100;
        dotInitializer();
        updateView();
    }

    public void buttonClearAllClicked(View view) {
        equalHadJustClicked = false;
        permanentValue = 0.0;
        operatorIndicator.setText("");
        tempValueInitializer();
        dotInitializer();
        operatorInitializer();
        updateView();
        stateIndex = StateIndex.STANDBY;
    }

    public void buttonClearEntryClicked(View view) {
        equalHadJustClicked = false;
        tempValueInitializer();
        dotInitializer();
        updateView();
    }

    public void buttonMemoryAdditionClicked(View view) {
        equalHadJustClicked = false;
        memoryValue += Double.parseDouble(calculatorScreen.getText().toString());
        stateIndex = StateIndex.EQUAL_CLICKED;
        permanentValue = tempValue;
        tempValueInitializer();
        memoryIndicator.setVisibility(View.VISIBLE);
        dotInitializer();
    }

    public void buttonMemorySubtractionClicked(View view) {
        equalHadJustClicked = false;
        memoryValue -= Double.parseDouble(calculatorScreen.getText().toString());
        stateIndex = StateIndex.EQUAL_CLICKED;
        permanentValue = tempValue;
        tempValueInitializer();
        memoryIndicator.setVisibility(View.VISIBLE);
        dotInitializer();
    }

    public void buttonMemoryRecallClicked(View view) {
        equalHadJustClicked = false;
        if (memoryValueHadJustClickedOnce) {
            memoryValue = 0.0;
            memoryIndicator.setVisibility(View.INVISIBLE);
        } else {
            memoryValueHadJustClickedOnce = true;
            tempValue = memoryValue;
            updateView();
            if (stateIndex != StateIndex.OPERATOR_CLICKED) {
                stateIndex = StateIndex.STANDBY;
            }
        }
    }

    public void buttonConvertClicked(View view) {
        if (Double.parseDouble(calculatorScreen.getText().toString()) > 0) {
            String conversionUrl = currencies[spinner1.getSelectedItemPosition()].substring(0, 3) + "/" + currencies[spinner2.getSelectedItemPosition()].substring(0, 3) + "/" + calculatorScreen.getText().toString();
            getNewCurrency(conversionUrl);
        } else {
            Toast.makeText(getApplicationContext(), "Type a positive amount for conversion", Toast.LENGTH_LONG).show();
            vibrate();
        }
    }

    private void getNewCurrency(String conversionUrl) {
        if (isNetworkConnected(this)) {
            Call<RetrieveCurrency> call = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class).applyConversion(conversionUrl);
            call.enqueue(new Callback<RetrieveCurrency>() {
                @Override
                public void onResponse(Call<RetrieveCurrency> call, Response<RetrieveCurrency> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        exchangeResult.setText(getString(R.string.exchange_result, calculatorScreen.getText().toString() + " " + response.body().baseCode, (Math.round(response.body().conversionResult * 100)) / 100 + " " + response.body().targetCode));
                        exchangeResult.setVisibility(View.VISIBLE);
                    } else {
                        Gson gson = new Gson();
                        ErrorResponse errorResponse = null;
                        try {
                            errorResponse = gson.fromJson(
                                    response.errorBody().string(),
                                    ErrorResponse.class);
                            Toast.makeText(getApplicationContext(), "Cannot proceed due to " + errorResponse.getErrorType(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RetrieveCurrency> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_LONG).show();
                    vibrate();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "???????????? ???? ???????????????? ?? ???? wi-fi ?????? ????????????????????????????", Toast.LENGTH_SHORT).show();
            vibrate();
        }
    }

    private static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    private void CurrencyListInitializer() {
        currencies = new String[]{"AED - UAE Dirham", "AFN - Afghan Afghani", "ALL - Albanian Lek", "AMD - Armenian Dram",
                "ANG - Netherlands Antillian Guilder", "AOA - Angolan Kwanza", "ARS - Argentine Peso",
                "AUD - Australian Dollar", "AWG - Aruban Florin", "AZN - Azerbaijani Manat",
                "BAM - Bosnia and Herzegovina Mark", "BBD - Barbados Dollar", "BDT - Bangladeshi Taka",
                "BGN - Bulgarian Lev", "BHD - Bahraini Dinar", "BIF - Burundian Franc", "BMD - Bermudian Dollar",
                "BND - Brunei Dollar", "BOB - Bolivian Boliviano", "BRL - Brazilian Real", "BSD - Bahamian Dollar",
                "BTN - Bhutanese Ngultrum", "BWP - Botswana Pula", "BYN - Belarusian Ruble", "BZD - Belize Dollar",
                "CAD - Canadian Dollar", "CDF - Congolese Franc", "CHF - Swiss Franc", "CLP - Chilean Peso",
                "CNY - Chinese Renminbi", "COP - Colombian Peso", "CRC - Costa Rican Colon",
                "CUC - Cuban Convertible Peso", "CUP - Cuban Peso", "CVE - Cape Verdean Escudo",
                "CZK - Czech Koruna", "DJF - Djiboutian Franc", "DKK - Danish Krone", "DOP - Dominican Peso",
                "DZD - Algerian Dinar", "EGP - Egyptian Pound", "ERN - Eritrean Nakfa", "ETB - Ethiopian Birr",
                "EUR - Euro", "FJD - Fiji Dollar", "FKP - Falkland Islands Pound", "FOK - Faroese Kr??na",
                "GBP - Pound Sterling", "GEL - Georgian Lari", "GGP - Guernsey Pound", "GHS - Ghanaian Cedi",
                "GIP - Gibraltar Pound", "GMD - Gambian Dalasi", "GNF - Guinean Franc", "GTQ - Guatemalan Quetzal",
                "GYD - Guyanese Dollar", "HKD - Hong Kong Dollar", "HNL - Honduran Lempira", "HRK - Croatian Kuna",
                "HTG - Haitian Gourde", "HUF - Hungarian Forint", "IDR - Indonesian Rupiah",
                "ILS - Israeli New Shekel", "IMP - Manx Pound", "INR - Indian Rupee", "IQD - Iraqi Dinar",
                "IRR - Iranian Rial", "ISK - Icelandic Kr??na", "JMD - Jamaican Dollar", "JOD - Jordanian Dinar",
                "JPY - Japanese Yen", "KES - Kenyan Shilling", "KGS - Kyrgyzstani Som", "KHR - Cambodian Riel",
                "KID - Kiribati Dollar", "KMF - Comorian Franc", "KRW - South Korean Won", "KWD - Kuwaiti Dinar",
                "KYD - Cayman Islands Dollar", "KZT - Kazakhstani Tenge", "LAK - Lao Kip", "LBP - Lebanese Pound",
                "LKR - Sri Lanka Rupee", "LRD - Liberian Dollar", "LSL - Lesotho Loti", "LYD - Libyan Dinar",
                "MAD - Moroccan Dirham", "MDL - Moldovan Leu", "MGA - Malagasy Ariary",
                "MKD - (North)Macedonian Denar", "MMK - Burmese Kyat", "MNT - Mongolian T??gr??g",
                "MOP - Macanese Pataca", "MRU - Mauritanian Ouguiya", "MUR - Mauritian Rupee",
                "MVR - Maldivian Rufiyaa", "MWK - Malawian Kwacha", "MXN - Mexican Peso", "MYR - Malaysian Ringgit",
                "MZN - Mozambican Metical", "NAD - Namibian Dollar", "NGN - Nigerian Naira",
                "NIO - Nicaraguan C??rdoba", "NOK - Norwegian Krone", "NPR - Nepalese Rupee",
                "NZD - New Zealand Dollar", "OMR - Omani Rial", "PAB - Panamanian Balboa", "PEN - Peruvian Sol",
                "PGK - Papua New Guinean Kina", "PHP - Philippine Peso", "PKR - Pakistani Rupee",
                "PLN - Polish Z??oty", "PYG - Paraguayan Guaran??", "QAR - Qatari Riyal", "RON - Romanian Leu",
                "RSD - Serbian Dinar", "RUB - Russian Ruble", "RWF - Rwandan Franc", "SAR - Saudi Riyal",
                "SBD - Solomon Islands Dollar", "SCR - Seychellois Rupee", "SDG - Sudanese Pound",
                "SEK - Swedish Krona", "SGD - Singapore Dollar", "SHP - Saint Helena Pound",
                "SLL - Sierra Leonean Leone", "SOS - Somali Shilling", "SRD - Surinamese Dollar",
                "SSP - South Sudanese Pound", "STN - S??o Tom?? and Pr??ncipe Dobra", "SYP - Syrian Pound",
                "SZL - Eswatini Lilangeni", "THB - Thai Baht", "TJS - Tajikistani Somoni",
                "TMT - Turkmenistan Manat", "TND - Tunisian Dinar", "TOP - Tongan Pa??anga", "TRY - Turkish Lira",
                "TTD - Trinidad and Tobago Dollar", "TVD - Tuvaluan Dollar", "TWD - New Taiwan Dollar",
                "TZS - Tanzanian Shilling", "UAH - Ukrainian Hryvnia", "UGX - Ugandan Shilling",
                "USD - United States Dollar", "UYU - Uruguayan Peso", "UZS - Uzbekistani So'm",
                "VES - Venezuelan Bol??var Soberano", "VND - Vietnamese ?????ng", "VUV - Vanuatu Vatu",
                "WST - Samoan T??l??", "XAF - Central African CFA Franc", "XCD - East Caribbean Dollar",
                "XDR - Special Drawing Rights", "XOF - West African CFA franc", "XPF - CFP Franc",
                "YER - Yemeni Rial", "ZAR - South African Rand", "ZMW - Zambian Kwacha"
        };
    }

    private void SpinnerChoicesInitializer() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
    }

}