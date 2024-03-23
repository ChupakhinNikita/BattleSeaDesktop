package org.example.views.panels;

import org.example.battlesea.views.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChoosePanel extends JPanel {

    private View view;
    private JPanel panelRadio;

    private JRadioButton oneDeck;
    private JRadioButton twoDeck;
    private JRadioButton threeDeck;
    private JRadioButton fourDeck;

    private JRadioButton VerticalOneDeck;
    private JRadioButton VerticalTwoDeck;
    private JRadioButton VerticalThreeDeck;
    private JRadioButton VerticalFourDeck;

    private JButton clearField;

    private ButtonGroup groupDeck;


    public ChoosePanel(View view) {

        /*----------------------------Установка свойств окна--------------------------------------*/
        this.view = view;
        setLayout(null);

        this.setPreferredSize(new Dimension(720, 450));
        panelRadio = new JPanel();
        panelRadio.setLayout(new FlowLayout());

        panelRadio.setBounds(10, 15, 700, 300);

        clearField = new JButton("Убрать все корабли");
        clearField.setBounds(50, 350, 230, 40);
        clearField.addActionListener(new ActionClearField());

        panelRadio.setBorder(BorderFactory.createTitledBorder("Корабли"));

        VerticalOneDeck = new JRadioButton();
        VerticalTwoDeck = new JRadioButton();
        VerticalThreeDeck = new JRadioButton();
        VerticalFourDeck = new JRadioButton();
        oneDeck = new JRadioButton();
        twoDeck = new JRadioButton();
        threeDeck = new JRadioButton();
        fourDeck = new JRadioButton();

        setNameOneDeck(4);
        setNameTwoDeck(3);
        setNameThreeDeck(2);
        setNameFourDeck(1);

        groupDeck = new ButtonGroup();

        add(panelRadio);
        add(clearField);

        panelRadio.add(oneDeck);
        panelRadio.add(VerticalOneDeck);

        panelRadio.add(twoDeck);
        panelRadio.add(VerticalTwoDeck);

        panelRadio.add(threeDeck);
        panelRadio.add(VerticalThreeDeck);

        panelRadio.add(fourDeck);
        panelRadio.add(VerticalFourDeck);

        groupDeck.add(oneDeck);
        groupDeck.add(twoDeck);
        groupDeck.add(threeDeck);
        groupDeck.add(fourDeck);
        groupDeck.add(VerticalOneDeck);
        groupDeck.add(VerticalTwoDeck);
        groupDeck.add(VerticalThreeDeck);
        groupDeck.add(VerticalFourDeck);


        // Установка открытия окна в центре экрана
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = getSize();
        setLocation((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) / 2);
    }

    public void setNameOneDeck(int count) {
        oneDeck.setText(" осталось - " + count);
        VerticalOneDeck.setText(" осталось - " + count);
        oneDeck.setIcon(new ImageIcon("C:\\Users\\Никита\\Desktop\\BattleSea\\img\\horizon_deck1.png"));
        VerticalOneDeck.setIcon(new ImageIcon("C:\\Users\\Никита\\Desktop\\BattleSea\\img\\vertical_deck1.png"));
    }

    public void setNameTwoDeck(int count) {
        twoDeck.setText(" осталось - " + count);
        VerticalTwoDeck.setText(" осталось - " + count);
        twoDeck.setIcon(new ImageIcon("C:\\Users\\Никита\\Desktop\\BattleSea\\img\\horizon_deck2.png"));
        VerticalTwoDeck.setIcon(new ImageIcon("C:\\Users\\Никита\\Desktop\\BattleSea\\img\\vertical_deck2.png"));
    }

    public void setNameThreeDeck(int count) {
        threeDeck.setText(" осталось - " + count);
        VerticalThreeDeck.setText(" осталось - " + count);
        threeDeck.setIcon(new ImageIcon("C:\\Users\\Никита\\Desktop\\BattleSea\\img\\horizon_deck3.png"));
        VerticalThreeDeck.setIcon(new ImageIcon("C:\\Users\\Никита\\Desktop\\BattleSea\\img\\vertical_deck3.png"));
    }

    public void setNameFourDeck(int count) {
        fourDeck.setText(" осталось - " + count);
        VerticalFourDeck.setText(" осталось - " + count);
        fourDeck.setIcon(new ImageIcon("C:\\Users\\Никита\\Desktop\\BattleSea\\img\\horizon_deck4.png"));
        VerticalFourDeck.setIcon(new ImageIcon("C:\\Users\\Никита\\Desktop\\BattleSea\\img\\vertical_deck4.png"));
    }

    // Кол-во палуб
    public int getCountDeck() {
        if (oneDeck.isSelected() || VerticalOneDeck.isSelected()) return 1;
        else if (twoDeck.isSelected() || VerticalTwoDeck.isSelected()) return 2;
        else if (threeDeck.isSelected() || VerticalThreeDeck.isSelected()) return 3;
        else if (fourDeck.isSelected() || VerticalFourDeck.isSelected()) return 4;
        else return 0;
    }

    // Ориентация корабля
    public int getPlacement() {
        if (VerticalOneDeck.isSelected() || VerticalTwoDeck.isSelected() || VerticalThreeDeck.isSelected() || VerticalFourDeck.isSelected()) return 1;
        else if (oneDeck.isSelected() || twoDeck.isSelected() || threeDeck.isSelected() || fourDeck.isSelected()) return 2;
        else return 0;
    }

    // Слушатель - загружает пустое поле при нажатии кнопки "Убрать все корабли"
    private class ActionClearField implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            view.loadEmptyMyField();
        }
    }

}
