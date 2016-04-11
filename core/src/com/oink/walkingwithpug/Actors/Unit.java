package com.oink.walkingwithpug.Actors;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class Unit extends Actor {
    public float getCenterX()
    {
        return getX() + getOriginX();
    }

    public float getCenterY()
    {
        return getY() + getOriginY();
    }
}