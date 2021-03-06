package com.oink.walkingwithpug.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.oink.walkingwithpug.GoalManager;
import com.oink.walkingwithpug.PugGame;
import com.oink.walkingwithpug.Utils;

import static com.badlogic.gdx.math.Interpolation.exp5;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class DayAcceptScreen implements Screen {
    private static final int BIG_FONT_SIZE = 200;
    public static final int SMALL_FONT_SIZE = 50;
    private static final String START_DAY_TEXTURE = "day_accept/start_day";
    private static final String EXIT_TO_MENU_TEXTURE = "day_accept/exit_to_menu";

    private Container<Label> labelContainer;
    private Label goalLabel;

    private ImageButton exitToMenuButton;
    private ImageButton startDayButton;
    private Table table;
    private PugGame game;
    private BitmapFont bigFont;
    private BitmapFont smallFont;
    private Stage stage;
    private Label label;
    private int dayNumber;
    private GoalManager goalManager;

    public DayAcceptScreen(final PugGame game) {
        //Loading preferences
        Preferences preferences = game.getPreferences();
        if (preferences.getInteger("day") == 0) {
            preferences.putInteger("day", 1);
            preferences.flush();
        }

        dayNumber = preferences.getInteger("day");
        this.game = game;

        stage = new Stage(new StretchViewport(
                PugGame.MENU_VIEWPORT_WIDTH,
                PugGame.MENU_VIEWPORT_HEIGHT * game.getAspectRatio()
        ));
        Gdx.input.setInputProcessor(stage);

        loadFonts();
        configureActors();
        addActorsToStage();
        createActions();

        goalManager = new GoalManager(dayNumber);
        goalLabel.setText(goalManager.getGoalText());
        //stage.setDebugAll(true);
    }

    private void loadFonts() {
        bigFont = Utils.loadFont(PugGame.TTF_FONT, BIG_FONT_SIZE, Color.YELLOW, 0);
        smallFont = Utils.loadFont(PugGame.TTF_FONT, SMALL_FONT_SIZE, Color.YELLOW, 0);
    }

    private void addActorsToStage() {
        stage.addActor(labelContainer);
        stage.addActor(table);
    }

    private void configureActors() {
        label = Utils.makeLabel("Day " + dayNumber, bigFont, Color.YELLOW);
        labelContainer = new Container<Label>(label);
        labelContainer.setTransform(true);
        //label.setOrigin(label.getWidth() / 2, label.getHeight() / 2);
        labelContainer.setOrigin(labelContainer.getWidth() / 2, labelContainer.getHeight() / 2);
        labelContainer.setPosition(stage.getWidth() / 2 - labelContainer.getWidth() / 2, stage.getHeight() / 2 - labelContainer.getHeight() / 2);

        goalLabel = Utils.makeLabel("", smallFont, Color.YELLOW);

        createButtons();
        createTable();
    }

    private void createButtons() {
        startDayButton = Utils.makeButton(START_DAY_TEXTURE, PugGame.MENU_TEXTURE_SCALE);
        startDayButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game, goalManager));
            }
        });
        exitToMenuButton = Utils.makeButton(EXIT_TO_MENU_TEXTURE, PugGame.MENU_TEXTURE_SCALE);
        exitToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
    }

    private void createActions() {
        table.addAction(parallel(fadeOut(0), touchable(Touchable.disabled)));
        labelContainer.addAction(sequence(delay(0.1f), parallel(moveBy(0, 200, 1f, exp5),scaleTo(0.5f, 0.5f, 1f, exp5)),run(new Runnable() {
            @Override
            public void run() {
                table.addAction(sequence(touchable(Touchable.childrenOnly), fadeIn(1f)));
            }
        })));
    }

    private void createTable() {
        table = new Table();
        table.setFillParent(true);
        table.align(Align.bottom);
        table.padBottom(stage.getHeight() / 6);
        table.add(goalLabel).center().expandX().colspan(2).pad(stage.getHeight() / 10);
        table.row();
        table.add(startDayButton).height(startDayButton.getHeight()).width(startDayButton.getWidth()).expandX();
        table.add(exitToMenuButton).height(exitToMenuButton.getHeight()).width(exitToMenuButton.getWidth()).expandX();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
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

    }
}
