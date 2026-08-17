package com.elctrovotesuperx.view;

import javafx.scene.Scene;

public interface Page {

    Scene getScene(Runnable backCallback);
}