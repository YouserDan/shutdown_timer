package com.example.shutdown_timer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.Duration;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class ShutdownController {
    private Timeline countdownTimer;
    private int totalSeconds;
    private boolean isPaused = false;

    @FXML
    private TextField innerHours;
    @FXML
    private TextField innerMinutes;
    @FXML
    private TextField innerSeconds;
    @FXML
    private TextField timeLeft;
    @FXML
    private Button stopButton;

    @FXML
    public void initialize() {
        timeLeft.setEditable(false);
        timeLeft.setFocusTraversable(false);
        timeLeft.setDisable(true);
        stopButton.setDisable(true);

        setupTimeField(innerHours, 2, 24);
        setupTimeField(innerMinutes, 5, 59);
        setupTimeField(innerSeconds, 5, 59);
    }

    public void setupTimeField(TextField textField, int maxFirstDigit, int maxValue) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            // Проверяем, только ли цифры введены
            if (!newText.matches("\\d*")) return null;

            // Если поле пустое – разрешаем ввод
            if (newText.isEmpty()) return change;

            // Преобразуем в число
            try {
                int value = Integer.parseInt(newText);

                // Проверяем допустимый диапазон для значений от 0 до maxValue
                if (value > maxValue) return null;

                // Ограничение на первую цифру для 2-значных чисел
                if (newText.length() == 2) {
                    int firstDigit = Character.getNumericValue(newText.charAt(0));

                    // Для часов ограничение на первую цифру (не более 2)
                    if (maxValue == 24 && firstDigit > 2) return null; // Для часов maxValue = 24
                    // Для минут и секунд ограничение на первую цифру (не более 5)
                    if (maxValue != 24 && firstDigit > 5) return null; // Для минут и секунд maxValue = 59
                }

                return change;
            } catch (NumberFormatException e) {
                return null; // Если не число — блокируем ввод
            }
        }));
    }



    @FXML
    public void startTimer(ActionEvent event) {
        System.out.println("Попытка запуска таймера");

        int hours = parseIntOrDefault(innerHours.getText(), 0);
        int minutes = parseIntOrDefault(innerMinutes.getText(), 0);
        int seconds = parseIntOrDefault(innerSeconds.getText(), 0);

        totalSeconds = hours * 3600 + minutes * 60 + seconds;

        if (totalSeconds == 0) {
            System.out.println("Ошибка: время не задано");
            return;
        }

        stopButton.setDisable(false);
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        isPaused = false;
        stopButton.setText("Pause");

        countdownTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));
        countdownTimer.setCycleCount(Timeline.INDEFINITE);
        countdownTimer.play();
    }

    private void updateTimer() {
        if (totalSeconds > 0) {
            totalSeconds--;

            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;

            String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            timeLeft.setText(formattedTime);
        } else {
            countdownTimer.stop(); // Останавливаем таймер

            // После завершения таймера делаем паузу 5 секунд и выключаем компьютер
            System.out.println("Таймер завершился!");

            // Сначала нажимаем Shift + F2 (с помощью Robot)
            pressShiftF2();

            // Затем через 5 секунд выполняем команду выключения компьютера
            try {
                Thread.sleep(5000); // Задержка 5 секунд
                shutdownComputer(); // Включаем выключение компьютера
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void pressShiftF2() {
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_SHIFT);  // Нажимаем Shift
            robot.keyPress(KeyEvent.VK_F2);    // Нажимаем F2
            robot.keyRelease(KeyEvent.VK_F2);  // Отпускаем F2
            robot.keyRelease(KeyEvent.VK_SHIFT); // Отпускаем Shift
            System.out.println("Shift + F2 нажаты!");
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    private void shutdownComputer() {
        try {
            String shutdownCommand = "shutdown -s -t 5"; // Команда для выключения
            Process process = Runtime.getRuntime().exec(shutdownCommand);
            System.out.println("Команда на выключение компьютера отправлена.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void simulateKeyPress(int key1, int key2) {
        try {
            Robot robot = new Robot();

            // Нажимаем Shift
            robot.keyPress(key1);

            // Нажимаем F2
            robot.keyPress(key2);
            robot.keyRelease(key2); // Отпускаем F2

            // Добавляем небольшую задержку (например, 100 миллисекунд)
            Thread.sleep(100);

            // Отпускаем Shift
            robot.keyRelease(key1);

            System.out.println("Сочетание клавиш Shift + F2 отправлено!");
        } catch (Exception e) {
            System.err.println("Ошибка при эмуляции клавиш: " + e.getMessage());
        }
    }


    @FXML
    public void stopTimer(ActionEvent event) {
        if (countdownTimer == null) return;

        if (isPaused) {
            countdownTimer.play();
            stopButton.setText("Pause");
            System.out.println("Таймер продолжен");
        } else {
            countdownTimer.pause();
            stopButton.setText("Resume");
            System.out.println("Таймер поставлен на паузу");
        }

        isPaused = !isPaused;
    }

    private int parseIntOrDefault(String text, int defaultValue) {
        try {
            return text.isEmpty() ? defaultValue : Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
