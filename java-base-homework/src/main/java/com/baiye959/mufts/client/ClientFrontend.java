package com.baiye959.mufts.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * author Baiye959
 */
public class ClientFrontend extends Application {
    private ClientHandler clientHandler;
    private TextField usernameField;
    private TextField commandField;
    private Label statusLabel;
    private ProgressBar progressBar;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("多用户文件传输系统");

        // 用户名输入框和连接按钮
        Label usernameLabel = new Label("用户名:");
        usernameField = new TextField();
        Button connectButton = new Button("连接");
        connectButton.setOnAction(e -> connectToServer());

        // 取消连接按钮
        Button disconnectButton = new Button("取消连接");
        disconnectButton.setOnAction(e -> disconnectFromServer());

        // 命令输入框和发送按钮
        Label commandLabel = new Label("输入命令:");
        commandField = new TextField();
        Button sendButton = new Button("发送");
        sendButton.setOnAction(e -> sendCommand());

        // 状态显示标签
        statusLabel = new Label();

        // 进度条
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(400);

        // 布局
        VBox vbox = new VBox(10, usernameLabel, usernameField, connectButton, disconnectButton, commandLabel, commandField, sendButton, statusLabel, progressBar);
        vbox.setPadding(new Insets(20));
        vbox.setPrefSize(500, 300);

        Scene scene = new Scene(vbox);
        primaryStage.setScene(scene);

        // 固定窗口大小
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void connectToServer() {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            setStatusLabel("用户名不能为空", "red");
            return;
        }

        try {
            clientHandler = new ClientHandler(username, this);
            setStatusLabel("已连接到服务器", "blue");
        } catch (Exception e) {
            setStatusLabel("连接失败: " + e.getMessage(), "red");
        }
    }

    private void disconnectFromServer() {
        if (clientHandler != null) {
            clientHandler.stop();
            clientHandler = null;
            setStatusLabel("已断开连接", "blue");
        }
    }

    private void sendCommand() {
        if (clientHandler == null) {
            setStatusLabel("请先连接到服务器", "red");
            return;
        }

        String command = commandField.getText();
        if (command.isEmpty()) {
            setStatusLabel("命令不能为空", "red");
            return;
        }

        try {
            clientHandler.handleRequest(command);
            setStatusLabel("命令已发送", "blue");
        } catch (Exception e) {
            setStatusLabel("发送失败: " + e.getMessage(), "red");
        }
    }

    public void setStatusLabel(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    public static void displayFileContent(String filePath, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("文件内容");
            alert.setHeaderText("文件内容: " + filePath);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public void updateProgress(double progress) {
        Platform.runLater(() -> progressBar.setProgress(progress));
    }

    public void showProgressAlert(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
