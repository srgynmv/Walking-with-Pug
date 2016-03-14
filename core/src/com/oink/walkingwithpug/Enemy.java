package com.oink.walkingwithpug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Enemy extends Actor {
    private int life;
    private Texture rawTexture;
    private TextureRegion enemyTexture;
    private GameScreen screen;

    Enemy(float scale, float x, float y, GameScreen screen) {
        super();

        life = 100;

        rawTexture = new Texture(Gdx.files.internal("enemy_dog.png"));
        enemyTexture = new TextureRegion(rawTexture);
        setWidth(enemyTexture.getRegionWidth() * scale);
        setHeight(enemyTexture.getRegionHeight() * scale);
        setBounds(0, 0, getWidth(), getHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);
        setX(x);
        setY(y);
        this.screen = screen;

        makeListeners(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(enemyTexture, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    @Override
    public void act(float delta) {
        if (life <= 0) {
            this.remove();
            return;
        }

        //Make vector from enemy to pug
        Vector2 eyeVector = new Vector2((screen.pug.getX() - getX()) * delta, (screen.pug.getY() - getY()) * delta);

        //If distance > 0 move and rotate to pug
        if (Vector2.dst2(getX() + getOriginX(), getY() + getOriginY(),
                screen.pug.getX() + screen.pug.getOriginX(),
                screen.pug.getY() + screen.pug.getOriginY()) > screen.pug.getWidth()) {
            moveBy(eyeVector.x, eyeVector.y);

            float angle = eyeVector.angle() - 90;
            if (angle < 0) angle += 360;

            setRotation(angle); //Add 90 degrees because of texture directed to top
        }
        else {
            //TODO Make remove life better!
            //screen.pug.removeLife(10f * delta);
        }
    }

    public void makeListeners(final Enemy enemy) {
        enemy.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                life -= 50;
            }
        });
    }

    @Override
    public boolean remove() {
        rawTexture.dispose();
        return super.remove();
    }
}