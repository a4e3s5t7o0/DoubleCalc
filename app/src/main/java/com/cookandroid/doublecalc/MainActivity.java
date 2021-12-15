package com.cookandroid.doublecalc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    boolean isFirstInput = true;

    ScrollView scrollView;
    TextView resultOperatorTextView;
    TextView resultTextView;

    ImageButton allClearButton;
    ImageButton clearEntryButton;
    ImageButton backSpaceButton;
    ImageButton decimalButton;

    Button[] numberButton = new Button[10];
    ImageButton[] operatorButton = new ImageButton[5];

    Calculator calculator = new Calculator(new DecimalFormat("###,###.##########"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollView = findViewById(R.id.scrollview);
        resultOperatorTextView = findViewById(R.id.result_operator_text_view);
        resultTextView = findViewById(R.id.result_text_view);

        allClearButton = findViewById(R.id.all_clear_button);
        clearEntryButton = findViewById(R.id.clear_entry_button);
        backSpaceButton = findViewById(R.id.back_space_button);
        decimalButton = findViewById(R.id.decimal_button);

        for (int i = 0; i < numberButton.length; i++) {
            numberButton[i] = findViewById(R.id.number_button_0 + i);
        }

        for (int i = 0; i < operatorButton.length; i++) {
            operatorButton[i] = findViewById(R.id.operator_button_0 + i);
        }

        for (Button button : numberButton) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    numberButtonClick(view);
                }
            });
        }
        for (ImageButton imageButton : operatorButton) {
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    operatorButtonClick(view);
                }
            });
        }

        allClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allClearButtonClick(view);
            }
        });

        clearEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearEntryButtonClick(view);
            }
        });

        backSpaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backSpaceButtonClick(view);
            }
        });

        decimalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decimalButtonClick(view);
            }
        });
    }

    private void decimalButtonClick(View view) {
        if (isFirstInput) { //첫 입력이라면 숫자 색을 바꾸고 "0." 표기
            resultTextView.setTextColor(0xFFFFFFFF);
            resultTextView.setText("0.");
        } else {
            if (resultTextView.getText().toString().contains(".")) {
                Toast.makeText(getApplicationContext(), "이미 소숫점이 존재합니다.", Toast.LENGTH_SHORT).show();
            } else {
                resultTextView.append(".");
            }
        }
    }

    private void backSpaceButtonClick(View view) { // 마지막으로 입력된 숫자 지우기
        if (isFirstInput && !calculator.getOperatorString().equals("")) {
            Toast.makeText(getApplicationContext(), "결과값은 지울 수 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            if (resultTextView.getText().toString().length() > 1) {
                String getResultString = resultTextView.getText().toString().replace(",", "");
                String subString = getResultString.substring(0, getResultString.length() - 1);
                String decimalString = calculator.getDecimalString(subString);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { //API 26 버전(오레오)보다 작으면 작동
                    resultTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getStringSize(decimalString));
                }
                resultTextView.setText(decimalString);

            } else {
                clearText();
            }
        }
    }

    private void clearEntryButtonClick(View view) {
        clearText();
    }

    private void allClearButtonClick(View view) {  // 전부 지우기, 수식까지 초기화
        calculator.setAllClear();
        resultOperatorTextView.setText(calculator.getOperatorString());
        clearText();
    }

    private void clearText() {
        isFirstInput = true;
        resultTextView.setTextColor(0xFFCFCFCF);
        resultTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
        resultTextView.setText(calculator.getclearInputText());

    }

    private void operatorButtonClick(View view) {
        String getResultString = resultTextView.getText().toString();
        String operator = view.getTag().toString();
        String getResult = calculator.getResult(isFirstInput, getResultString, operator);
        resultTextView.setText(getResult);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { //API 26 버전(오레오)보다 작으면 작동
            resultTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getStringSize(getResult));
        }
        resultOperatorTextView.setText(calculator.getOperatorString());
        isFirstInput = true;
    }


    private void numberButtonClick(View view) { //첫 번째 입력이 참일때 그 텍스트를 화면에 표시
        if (isFirstInput) {
            resultTextView.setTextColor(0xFFFFFFFF);
            resultTextView.setText(view.getTag().toString());
            isFirstInput = false;
        } else { // 3자리마다 , 로 구분
            String getResultText = resultTextView.getText().toString().replace(",", ""); // 12,000 -> 12000
            if (getResultText.length() > 15) {
                Toast.makeText(getApplicationContext(), "16자리 까지 입력 가능합니다.", Toast.LENGTH_SHORT).show();
            } else {
                getResultText = getResultText + view.getTag().toString();
                String getDecimalString = calculator.getDecimalString(getResultText);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { //API 26 버전(오레오)보다 작으면 작동
                    resultTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getStringSize(getDecimalString));
                }
                resultTextView.setText(getDecimalString);
            }
        }

    }
    private int getStringSize(String getDecimalString){

        if(getDecimalString.length() > 30){
            return 25;
        }
        else if(getDecimalString.length() > 25){
            return 30;
        }
        else if(getDecimalString.length() > 20){
            return 35;
        }
        else if(getDecimalString.length() > 15 ){ // 텍스트 크기 조정
            return 40;
        }
        return 50;
    }
}

