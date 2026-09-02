package com.electrovotesuperx.view;

import javafx.scene.Scene;

public interface Page {

    Scene getScene(Runnable backCallback);
}