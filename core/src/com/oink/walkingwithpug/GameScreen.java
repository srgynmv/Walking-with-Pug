package com.oink.walkingwithpug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;


public class GameScreen implements Screen {

    PugGame game;
    Stage stage;
    Roulette roulette;
    Pug pug;

    //DEBUG
    Texture map;

    float enemyTimer;
    Group enemiesGroup;

    OrthographicCamera camera;

    GameScreen(final PugGame game) {
        Gdx.app.log("INFO", "In a GameScreen constructor");
        this.game = game;
        //Settings up the scales of pug and roulette
        roulette = new Roulette(0.25f);
        pug = new Pug(0.4f, this);

        //DEBUG
        map = new Texture(Gdx.files.internal("random_map.png"));

        //Making camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.worldWidth * game.viewportRatio, game.worldHeight * game.viewportRatio * game.ratio);
        //Making viewport and input processor
        stage = new Stage(new StretchViewport(game.worldWidth * game.viewportRatio, game.worldHeight * game.viewportRatio * game.ratio, camera));
        Gdx.input.setInputProcessor(stage);

        pug.setX(stage.getWidth() / 2 - pug.getWidth() / 2);

        roulette.setX(stage.getWidth() / 2 - roulette.getWidth() / 2);
        roulette.setY(stage.getHeight() / 2 - roulette.getHeight() / 2);

        enemiesGroup = new Group();
        enemyTimer = 0;

        stage.addActor(enemiesGroup);
        stage.addActor(pug);
        stage.addActor(roulette);

        game.maxLineLengthSquared = stage.getWidth() / 4;
        game.maxLineLengthSquared *= game.maxLineLengthSquared;

        Gdx.app.log("CAMERA ZOOM", "" + camera.zoom);
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0.5f, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Checks, need to move camera or not
        moveCamera();

        stage.getCamera().update();

        //Update coordinates for roulette line
        roulette.rouletteLine.setPoints(pug, roulette);
        roulette.rouletteLine.setProjectionMatrix(stage.getCamera().combined);

        stage.act(delta);

        //DEBUG
        //Drawing map
        stage.getBatch().begin();
        stage.getBatch().draw(map, 0, 0, map.getWidth() * 8, map.getHeight() * 8);
//        game.font.draw(stage.getBatch(), "HP: " + pug.getLife(),
//                stage.getCamera().position.x  - stage.getWidth() / 2 + stage.getWidth() * 0.1f,
//                stage.getCamera().position.y + stage.getHeight() / 2 - stage.getHeight() * 0.1f);
        stage.getBatch().end();

        //TODO Make life works better!
//        if (pug.getLife() <= 0) {
//            pug.remove();
//            roulette.remove();
//        }

        if (enemyTimer > 3f && enemiesGroup.getChildren().size < 3) {
            enemyTimer = 0;
            //Making coordinates for new enemy
            Gdx.app.log("Camera x: ", "" + stage.getCamera().position.x);
            Gdx.app.log("Camera y: ", "" + stage.getCamera().position.y);

            float newX = MathUtils.random(0, game.worldWidth);
            float newY = MathUtils.random(0, game.worldHeight);
            if (newX >= stage.getCamera().position.x - stage.getWidth() / 2 && newX <= stage.getCamera().position.x + stage.getWidth() / 2) newX += stage.getWidth() * 2;
            if (newY >= stage.getCamera().position.y - stage.getHeight() / 2 && newY <= stage.getCamera().position.y + stage.getHeight() / 2) newY += stage.getHeight() * 2;
            Gdx.app.log("INFO", "Add new enemy at X: " + newX + " and Y: " + newY);
            enemiesGroup.addActor(new Enemy(0.55f, newX, newY, this));
        }

        stage.draw();

        enemyTimer = Math.min(enemyTimer + delta, 4f);
    }

    private void moveCamera() {
        //Difference on X between Roulette center and camera center.
        //Same to Y.
        float rouletteDx = roulette.getX() + roulette.getOriginX() - stage.getCamera().position.x;
        float rouletteDy = roulette.getY() + roulette.getOriginY() - stage.getCamera().position.y;

        //X and Y of roulette line middle.
        float rouletteLineMiddleX = (pug.getX() + pug.getOriginX() + roulette.getX() + roulette.getOriginX()) / 2;
        float rouletteLineMiddleY = (pug.getY() + pug.getOriginY() +roulette.getY() + roulette.getOriginY()) / 2;

        //If player doesn't touch screen, camera translates to rouletteLineMiddle.
        //Else camera moves to roulette.
        //TODO Change 10 with cameraSize - depending value
        if (roulette.isDragging) {
            if (Math.abs(rouletteDx) > 10) {
                stage.getCamera().translate(rouletteDx * Gdx.graphics.getDeltaTime(), 0, 0);
                roulette.moveBy(rouletteDx * Gdx.graphics.getDeltaTime(), 0);
            }
            if (Math.abs(rouletteDy) > 10 * game.ratio) {
                stage.getCamera().translate(0, rouletteDy / game.ratio * Gdx.graphics.getDeltaTime(), 0);
                roulette.moveBy(0, rouletteDy / game.ratio * Gdx.graphics.getDeltaTime());
            }

            //If roulette is moving, zooming up camera
            camera.zoom = Math.min(1.5f, camera.zoom + 0.01f);
        }
        else {
            stage.getCamera().translate(
                    (rouletteLineMiddleX - stage.getCamera().position.x) * Gdx.graphics.getDeltaTime(),
                    (rouletteLineMiddleY - stage.getCamera().position.y) * Gdx.graphics.getDeltaTime(),
                    0
            );
            //If staying, zooming down
            camera.zoom = Math.max(1.0f, camera.zoom - 0.01f);
        }
    }

    @Override
    public void show() {
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
