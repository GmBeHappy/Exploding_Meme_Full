/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exploding_meme_full;

/**
 *
 * @author GP73
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.text.Font;


public class Textuse extends Label
{
     public final static String FONT_PATH = "file:src/exploding_meme_full/resource/kenvector_future.ttf";
     
     public Textuse(String text)
    {
        setPrefHeight(0);
        setPrefWidth(0);
        setText(text);
        setWrapText(true);
        setLabelFont();
        setAlignment(Pos.TOP_RIGHT);
    
    }

    private void setLabelFont()
    {
        try {
        setFont(Font.loadFont(FONT_PATH,50) );
        } catch (Exception e) {
            setFont(Font.font("Verdana",50));
        }
    }
}