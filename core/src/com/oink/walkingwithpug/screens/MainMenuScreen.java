package com.oink.walkingwithpug.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.oink.walkingwithpug.PugGame;

/**
 * This class makes a new game and exit buttons on the screen.
 */
public class MainMenuScreen implements Screen {

    final PugGame game;

    private Texture backgroundTexture;
    private TextureRegion currentPugFrame;
    private Sprite logoSprite;
    private Stage stage;
    private Table table;

    ImageButton newGameButton;
    ImageButton quitButton;

    float textureScale;

    public MainMenuScreen(final PugGame game) {

        game.isRunning = false;

        backgroundTexture = new Texture(Gdx.files.internal("menu/background.png"));

        this.game = game;
        stage = new Stage(new StretchViewport(game.worldWidth * game.viewportRatio, game.worldHeight * game.viewportRatio * game.ratio));
        Gdx.input.setInputProcessor(stage);

        textureScale = stage.getWidth() / 1920f;

        Gdx.app.log("SCALE", textureScale + "");
        //Create some buttons.
        newGameButton = PugGame.makeButton("menu/buttons/new_game", textureScale);
        newGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new com.oink.walkingwithpug.Screens.GameScreen(game));
            }
        });
        quitButton = PugGame.makeButton("menu/buttons/quit", textureScale);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        //Create logo with properties.
        logoSprite = new Sprite(new Texture(Gdx.files.internal("menu/game_logo.png")));
        logoSprite.setSize(logoSprite.getWidth() * textureScale, logoSprite.getHeight() * textureScale);
        logoSprite.setOrigin(logoSprite.getWidth() / 2, logoSprite.getHeight() / 2);
        logoSprite.setPosition(stage.getWidth() * 2f / 5f - logoSprite.getOriginX(), stage.getHeight() * 3f / 4f - logoSprite.getOriginY());

        currentPugFrame = new TextureRegion(new Texture(Gdx.files.internal("menu/pug.png")));

        table = new Table();
        table.setFillParent(true);

        table.align(Align.bottom);
        table.padBottom(stage.getHeight() / 6);
        table.add(newGameButton).height(newGameButton.getHeight()).width(newGameButton.getWidth()).expandX();
        table.add(quitButton).height(quitButton.getHeight()).width(quitButton.getWidth()).expandX();

        stage.addActor(table);

        //stage.setDebugAll(true);
    }


    @Override
    public void show() {
        
    }

    float angleFactor = 1;

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0.5f, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (logoSprite.getRotation() > 5) angleFactor = -1;
        if (logoSprite.getRotation() < 0) angleFactor = 1;
        logoSprite.rotate(1.5f * delta * angleFactor);

        //Drawing
        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, 0, 0, stage.getWidth(), stage.getHeight());
        stage.getBatch().draw(
                currentPugFrame,
                stage.getWidth() / 2 - stage.getWidth() / 20,
                stage.getHeight() / 2 - stage.getHeight() / 20,
                currentPugFrame.getRegionWidth() * textureScale,
                currentPugFrame.getRegionHeight() * textureScale
        );
        logoSprite.draw(stage.getBatch());
        stage.getBatch().end();

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}