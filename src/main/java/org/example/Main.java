package org.example;

import org.example.controllers.Controller;
import org.example.models.Model;
import org.example.sound.Sound;
import org.example.views.View;

public class Main {

    public static void main(String[] args) {
        Model model = new Model();
        View view = new View();
        Controller controller = new Controller(view, model);


        view.setController(controller);
        view.setModel(model);
        view.init();
        Sound.playSound("C:\\Users\\Никита\\Desktop\\BattleSea\\wav\\seaSound.wav").join();
    }
}
