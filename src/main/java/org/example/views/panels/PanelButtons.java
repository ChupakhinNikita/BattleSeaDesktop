package org.example.views.panels;

import org.example.views.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelButtons extends JPanel {
    private View view;
    private JTextField infoField;
    private JButton startGameButton;
    private JButton exitButton;
    private JButton restartGameButton;

    public PanelButtons(View view) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = getSize();
        setLocation((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) / 2);
        this.view = view;
        setLayout(null);
        setPreferredSize(new Dimension(600, 50));

        infoField = new JTextField();
        setTextInfo(" Постановка кораблей");
        infoField.setEnabled(false);
        infoField.setBounds(10, 15, 250, 20);
        startGameButton = new JButton("Начать игру");
        startGameButton.setFont(new Font("Arial", 1, 12));
        startGameButton.setBounds(450, 0, 200, 45);
        startGameButton.addActionListener(new ActionButtonStartClass());

        exitButton = new JButton("Прервать и выйти");
        exitButton.setFont(new Font("Arial", 1, 12));
        exitButton.setBounds(500, 0, 200, 45);
        exitButton.addActionListener(new ActionButtonDisconnect());
        exitButton.setVisible(false);

        restartGameButton = new JButton("Играть еще");
        restartGameButton.setFont(new Font("Arial", 1, 12));
        restartGameButton.setBounds(750, 0, 200, 45);
        restartGameButton.setVisible(false);
        restartGameButton.addActionListener(new ActionButtonRestartGame());

        add(infoField);
        add(startGameButton);
        add(exitButton);
        add(restartGameButton);
    }

    public JButton getRestartGameButton() {
        return restartGameButton;
    }

    public JButton getStartGameButton() {
        return startGameButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }

    public void setTextInfo(String text) {
        infoField.setText(text);
        infoField.setFont(new Font("Arial", 1, 13));
    }

    private class ActionButtonStartClass implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            view.startGame();
        }
    }

    private class ActionButtonDisconnect implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            view.disconnectGameRoom();
            startGameButton.setBounds(250, 0, 200, 45);
            exitButton.setVisible(false);
            restartGameButton.setVisible(true);
        }
    }

    private class ActionButtonRestartGame implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            view.init();
        }
    }
}
