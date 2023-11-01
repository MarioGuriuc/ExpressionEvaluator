package com.example.expressionevaluator;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;

import java.util.Objects;

public class MainPageController {

    static boolean success = true;
    Image smileyImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("smileyEmoji.png")));
    Image deadEmoji = new Image(Objects.requireNonNull(getClass().getResourceAsStream("deadEmoji.png")));
    @FXML
    private ImageView imageEmoji;
    @FXML
    private TextField mainExpressionTextField;
    @FXML
    private TextField resultText;

    @FXML
    private void computeResult(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            EvaluatorClass evaluatorClass = new EvaluatorClass(new StringBuilder(mainExpressionTextField.getText()));
            String result = evaluatorClass.getResult().toString();
            if (result.equals("Invalid expression!")) {
                success = false;
                resultText.setText(result);
            } else if (result.equals("Division by zero!")) {
                resultText.setText("Division by zero!");
            } else {
                resultText.setText("Result: " + String.format("%.2f", Double.parseDouble(result)));
            }
        }
        if (success) {
            imageEmoji.setImage(smileyImage);
        } else {
            imageEmoji.setImage(deadEmoji);
        }
    }

    @FXML
    private void checkFontSizeKeyTyped() {
        double fontSize = mainExpressionTextField.getFont().getSize();
        int textLength = mainExpressionTextField.getText().length();
        if (textLength >= fontSize - 10) {
            fontSize = 40 - (textLength * 0.5);
            fontSize = Math.max(fontSize, 12.0);
            mainExpressionTextField.setFont(Font.font(fontSize));
        }
        if (textLength == 0) {
            mainExpressionTextField.setFont(Font.font(40));
        }
    }

    @FXML
    private void checkFontSizeMouseClicked() {
        double fontSize = mainExpressionTextField.getFont().getSize();
        int textLength = mainExpressionTextField.getText().length();
        if (textLength >= fontSize - 10) {
            fontSize = 40 - (textLength * 0.5);
            fontSize = Math.max(fontSize, 12.0);
            mainExpressionTextField.setFont(Font.font(fontSize));
        }
        if (textLength == 0) {
            mainExpressionTextField.setFont(Font.font(40));
        }
    }

}
