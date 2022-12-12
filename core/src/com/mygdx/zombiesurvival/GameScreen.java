package com.mygdx.zombiesurvival;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Random;

public class GameScreen implements Screen {

    //Screen
    private Camera cam;
    private Viewport viewport;

    //graphics
    private SpriteBatch sprites;
    private TextureRegion background;
    private TextureAtlas textureAtlas;

    //game textures
    private Array<TextureRegion> playerRobotTextures;
    private Array<TextureRegion> enemyZombieTextures;
    private Array<TextureRegion> zombieDeathTextures;
    private Array<TextureRegion> robotDeathTextures;
    private TextureRegion playerShotTextureRegion;
    private TextureRegion playerShotTextureRegion2;

    //game objects
    private PlayerCharacter player1;
    private LinkedList<ZombieCharacter> zombies;
    private LinkedList<Projectiles> playerProjectiles;
    private LinkedList<Projectiles> enemyProjectiles;
    private DeathAnimation   robotDeath;
    private LinkedList<DeathAnimation>   zombieDeaths;
    boolean player1Dead = false;

    //timing
    private float backgroundOffset;
    private float backgroundSpeedIncrease = 3;
    private float zombieSpawnTimer  = 0f;

    //parameters
    private final int SCREEN_WIDTH = Gdx.graphics.getWidth();
    private final int SCREEN_HEIGHT = Gdx.graphics.getHeight();
    private final float MOVEMENT_THRESHOLD = 0.5f;

    //zombie Params for dynamic difficulty
    private float zombieMovementSpeed   = 200f;
    private float zombieSpawnTime       = 3f;
    private int   zombieHealth          = 6;
    private float hitTimer = 0;
    private float hitRegistrationInterval = 0.8f;


    //hud displays
    private int zombieKillCount = 0;
    private int playerHealthVal = 10;

    //hud variables
    BitmapFont font;
    float hudVerticalMargin, hudLeftX, hudRightX, hudCentreX, hudRow1Y, hudRow2Y, hudSectionWidth;


    GameScreen(){
        cam = new OrthographicCamera(); //basic 2D cam, simplest specification of Camera abstract class.
        viewport = new StretchViewport(SCREEN_HEIGHT, SCREEN_WIDTH, cam);
        sprites = new SpriteBatch();

        initTextures();


        player1 = new PlayerCharacter(400,10, SCREEN_WIDTH /6, SCREEN_HEIGHT /2,315,315,playerRobotTextures,
                playerShotTextureRegion,60, 25, 1000,0.5f, 8, 1f);

        zombies = new LinkedList<>();
        playerProjectiles = new LinkedList<>();
        enemyProjectiles =  new LinkedList<>();


        zombieDeaths = new LinkedList<>();
        createHUD();
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {
        sprites.begin();

        //Render Background
        backgroundOffset+=backgroundSpeedIncrease;
        if(backgroundOffset % SCREEN_WIDTH == 0){
            backgroundOffset = 0f;
        }


        sprites.draw(background,-backgroundOffset,0, SCREEN_WIDTH, SCREEN_HEIGHT);
        sprites.draw(background,-backgroundOffset+ SCREEN_WIDTH,0, SCREEN_WIDTH, SCREEN_HEIGHT);


        if(!player1Dead) {
            detectInput(deltaTime);
            player1.update(deltaTime);

            spawnZombies(deltaTime);

            ListIterator<ZombieCharacter> zombieIterator = zombies.listIterator();
            while(zombieIterator.hasNext()){
                ZombieCharacter nextZombie = zombieIterator.next();
                moveEnemies(nextZombie, deltaTime);
                nextZombie.update(deltaTime);
                nextZombie.draw(sprites);

                player1.draw(sprites);

                if(player1.canShoot()){
                    Projectiles[] projectiles = player1.shootProjectiles();
                    for(int i = 0; i<projectiles.length; i++){
                        playerProjectiles.add(projectiles[i]);
                    }
                }



            }

            if(zombieKillCount == 15){
                upgradeRobot();
            }
            else if (zombieKillCount == 12){
                upgradeZombies();
            }
            else if(zombieKillCount % 7 == 0){
                spawnFastZombie();
            }
            else if (zombieKillCount % 20 == 0){
                spawnBossZombie();
            }
            detectCollisions(deltaTime);

        }
        ListIterator<Projectiles> iterator = playerProjectiles.listIterator();
        while(iterator.hasNext()){
            Projectiles proj = iterator.next();
            proj.draw(sprites);
            proj.xPos += proj.movementSpeed * deltaTime;

            if(proj.xPos > SCREEN_WIDTH){
                iterator.remove();
            }
        }



        renderDeaths(deltaTime);


        renderHUD();

        sprites.end();

    }

    public void upgradeRobot(){
        player1.setShotInterval(0.3f);
        player1.setProjectileTexture(playerShotTextureRegion2);
    }

    public void upgradeZombies(){
        zombieSpawnTime = 2.3f;
        zombieMovementSpeed = 280f;

    }

    public void spawnBossZombie(){

    }

    public void spawnFastZombie(){

    }

    private void createHUD(){
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ZombieBlood-z8q2D.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParam.size = 90;
        fontParam.borderWidth = 3.6f;
        fontParam.color = Color.RED;
        fontParam.borderColor = Color.BLACK;

        font = fontGenerator.generateFont(fontParam);
        font.getData().setScale(1.0f);

        hudVerticalMargin = font.getCapHeight() / 2;
        hudLeftX = hudVerticalMargin;
        hudRightX =    (float)(SCREEN_WIDTH)  * 2/3 - hudLeftX;
        hudCentreX =   (float)(SCREEN_WIDTH)  * 1/3;
        hudRow1Y = (float)(SCREEN_HEIGHT) - hudVerticalMargin;
        hudRow2Y = hudRow1Y - hudVerticalMargin - font.getCapHeight();
        hudSectionWidth = (float)(SCREEN_WIDTH)/3;

    }

    private void renderHUD(){
        font.draw(sprites, "Zombies Killed: ",hudLeftX, hudRow1Y,hudSectionWidth, Align.left, false);
        font.draw(sprites, "Health", hudCentreX, hudRow1Y, hudSectionWidth, Align.center, false);

        font.draw(sprites, String.format(Locale.getDefault(), "%06d", zombieKillCount),hudLeftX, hudRow2Y, hudSectionWidth, Align.left, false);
        font.draw(sprites, String.format(Locale.getDefault(), "%02d", playerHealthVal),hudCentreX, hudRow2Y, hudSectionWidth, Align.center, false);

    }

    public void detectInput(float deltaTime){
        float topBoundary, bottomBoundary, leftBoundary, rightBoundary;

        bottomBoundary  = -player1.getY() + player1.height/2;
        leftBoundary    = -player1.getX();
        rightBoundary   = SCREEN_WIDTH * 0.5f - player1.getX() - player1.width;
        topBoundary     = SCREEN_HEIGHT * 0.9f  - player1.getY() - player1.height;

        //touch input

        if(Gdx.input.isTouched()){

            float xScreenTouch = Gdx.input.getX();
            float yScreenTouch = Gdx.input.getY();

            Vector2 touchPoint          = new Vector2(xScreenTouch,yScreenTouch);
            Vector2 playerIconCentre    = new Vector2(player1.getX()+player1.width/2, player1.getY()+player1.height/2);

            float distance = touchPoint.dst(playerIconCentre);

            if(distance > MOVEMENT_THRESHOLD){
                float xDiff = touchPoint.x - playerIconCentre.x;
                float yDiff = touchPoint.y - playerIconCentre.y;


                float xMove = xDiff/distance * player1.movementSpeed * deltaTime;
                float yMove = -yDiff/distance * player1.movementSpeed * deltaTime;

                if(xMove > 0){
                    xMove = Math.min(xMove, rightBoundary);
                }
                else{
                    xMove = Math.max(xMove, leftBoundary);
                }

                if(yMove > 0){
                    yMove = Math.min(yMove, topBoundary);
                }
                else{
                    yMove = Math.max(yMove, bottomBoundary);
                }

                player1.moveIcon(xMove, yMove);

            }




        }

    }

    public void detectCollisions(float deltaTime){
        ListIterator<Projectiles> projectileIterator = playerProjectiles.listIterator();
        while(projectileIterator.hasNext()){
            Projectiles proj = projectileIterator.next();
            ListIterator<ZombieCharacter> zombieIterator = zombies.listIterator();
            while(zombieIterator.hasNext())
            {
                ZombieCharacter enemy1 = zombieIterator.next();
                if(enemy1.checkCollision(proj.getBoundingBox())){
                    if(enemy1.hitAndCheckDead()){

                        Rectangle deadZombo = new Rectangle(enemy1.getX(), enemy1.getY(),350,350);
                        System.out.println("X coord: "+enemy1.getX());
                        zombieDeaths.add(new DeathAnimation(zombieDeathTextures,deadZombo,1.2f, 12));
                        System.out.println(zombieDeaths.size());
                        zombieIterator.remove();
                        zombieKillCount+= 1;
                    }
                    projectileIterator.remove();
                    break;
                }
            }

        }

        ListIterator<ZombieCharacter> zombieCharacterListIterator = zombies.listIterator();
        hitTimer += deltaTime;
        while(zombieCharacterListIterator.hasNext()){
            ZombieCharacter currZombo = zombieCharacterListIterator.next();
            if(player1.checkCollision(new Rectangle(currZombo.getX()+200, currZombo.getY(), 1, 1)) && hitTimer >= hitRegistrationInterval){

                playerHealthVal--;
                if(player1.hitAndCheckDead()){
                    Rectangle deadRobot = new Rectangle(player1.getX(), player1.getY(),player1.getWidth(), player1.getHeight());
                    player1Dead = true;
                    robotDeath= new DeathAnimation(robotDeathTextures,deadRobot, 1.2f, 9);
                }
                hitTimer = 0;
                break;
            }
        }
    }

    public void renderDeaths(float deltaTime){
        ListIterator<DeathAnimation> deathIterator = zombieDeaths.listIterator();
        while(deathIterator.hasNext()){
            DeathAnimation deadZombo = deathIterator.next();
            deadZombo.update(deltaTime);
            if(deadZombo.isAnimationFinished()){
                deathIterator.remove();
            }
            else{
                deadZombo.draw(sprites);
            }
        }

        if(player1Dead){
            robotDeath.update(deltaTime);
            if(robotDeath.isAnimationFinished()){

            }
            else{
                robotDeath.draw(sprites);
            }

        }
    }

    private void spawnZombies(float deltaTime){
        zombieSpawnTimer += deltaTime;
        Random rn = new Random();
        float rand = 0.2f + (0.8f - 0.2f) * rn.nextFloat();
        if(zombieSpawnTimer > zombieSpawnTime){
            zombies.add(new ZombieCharacter(zombieMovementSpeed,zombieHealth,SCREEN_WIDTH+10, SCREEN_HEIGHT*rand,300,300,enemyZombieTextures,
                    playerShotTextureRegion, 20, 40, 450, 0.5f, 9, 1f));
            zombieSpawnTimer -= zombieSpawnTime;

        }

    }

    public void moveEnemies(ZombieCharacter enemy1, float deltaTime){
        float topBoundary, bottomBoundary, leftBoundary, rightBoundary;
        bottomBoundary  = -enemy1.getY() + enemy1.height/2;
        leftBoundary    = -enemy1.getX();
        rightBoundary   = SCREEN_WIDTH - enemy1.getX() - enemy1.width;
        topBoundary     = SCREEN_HEIGHT * 0.9f  - enemy1.getY() - enemy1.height;

        Vector2 playerIconCentre    = new Vector2(player1.xPos+player1.width/2, player1.yPos+player1.height/2);
        Vector2 zombieIconCentre    = new Vector2(enemy1.xPos+enemy1.width/2,enemy1.yPos+enemy1.height/2);

        float dist = playerIconCentre.dst(zombieIconCentre);
        float xDiff = playerIconCentre.x - zombieIconCentre.x;
        float yDiff = playerIconCentre.y - zombieIconCentre.y;

        float xMove = xDiff/dist * enemy1.movementSpeed * deltaTime;
        float yMove = yDiff/dist * enemy1.movementSpeed * deltaTime;

        if(xMove > 0){
            xMove = Math.min(xMove, rightBoundary);
        }
        else{
            xMove = Math.max(xMove, leftBoundary);
        }

        if(yMove > 0){
            yMove = Math.min(yMove, topBoundary);
        }
        else{
            yMove = Math.max(yMove, bottomBoundary);
        }

        enemy1.moveIcon(xMove, yMove);



    }


    private void initTextures(){
        textureAtlas = new TextureAtlas("images.atlas");
        background = textureAtlas.findRegion("deathCave");

        playerRobotTextures = new Array<TextureRegion>();
        enemyZombieTextures = new Array<TextureRegion>();
        zombieDeathTextures = new Array<TextureRegion>();
        robotDeathTextures  = new Array<TextureRegion>();

        playerRobotTextures.add(textureAtlas.findRegion("RobotShoot01"));
        playerRobotTextures.add(textureAtlas.findRegion("RobotShoot02"));
        playerRobotTextures.add(textureAtlas.findRegion("RobotShoot03"));
        playerRobotTextures.add(textureAtlas.findRegion("RobotShoot04"));
        playerRobotTextures.add(textureAtlas.findRegion("RobotShoot05"));
        playerRobotTextures.add(textureAtlas.findRegion("RobotShoot06"));
        playerRobotTextures.add(textureAtlas.findRegion("RobotShoot07"));
        playerRobotTextures.add(textureAtlas.findRegion("RobotShoot08"));

        TextureRegion enemyZombieTextureRegion01 = textureAtlas.findRegion("ZombieWalk01");
        enemyZombieTextureRegion01.flip(true, false);
        enemyZombieTextures.add(enemyZombieTextureRegion01);

        TextureRegion enemyZombieTextureRegion02 = textureAtlas.findRegion("ZombieWalk02");
        enemyZombieTextureRegion02.flip(true, false);
        enemyZombieTextures.add(enemyZombieTextureRegion02);

        TextureRegion enemyZombieTextureRegion03 = textureAtlas.findRegion("ZombieWalk03");
        enemyZombieTextureRegion03.flip(true, false);
        enemyZombieTextures.add(enemyZombieTextureRegion03);

        TextureRegion enemyZombieTextureRegion04 = textureAtlas.findRegion("ZombieWalk04");
        enemyZombieTextureRegion04.flip(true, false);
        enemyZombieTextures.add(enemyZombieTextureRegion04);

        TextureRegion enemyZombieTextureRegion05 = textureAtlas.findRegion("ZombieWalk05");
        enemyZombieTextureRegion05.flip(true, false);
        enemyZombieTextures.add(enemyZombieTextureRegion05);

        TextureRegion enemyZombieTextureRegion06 = textureAtlas.findRegion("ZombieWalk06");
        enemyZombieTextureRegion06.flip(true, false);
        enemyZombieTextures.add(enemyZombieTextureRegion06);

        TextureRegion enemyZombieTextureRegion07 = textureAtlas.findRegion("ZombieWalk07");
        enemyZombieTextureRegion07.flip(true, false);
        enemyZombieTextures.add(enemyZombieTextureRegion07);

        TextureRegion enemyZombieTextureRegion08 = textureAtlas.findRegion("ZombieWalk08");
        enemyZombieTextureRegion08.flip(true, false);
        enemyZombieTextures.add(enemyZombieTextureRegion08);

        TextureRegion enemyZombieTextureRegion09 = textureAtlas.findRegion("ZombieWalk09");
        enemyZombieTextureRegion09.flip(true, false);
        enemyZombieTextures.add(enemyZombieTextureRegion09);


        TextureRegion zombieDeathTexture01 = textureAtlas.findRegion("Death01");
        zombieDeathTexture01.flip(true, false);
        zombieDeathTextures.add(zombieDeathTexture01);

        TextureRegion zombieDeathTexture02 = textureAtlas.findRegion("Death02");
        zombieDeathTexture02.flip(true, false);
        zombieDeathTextures.add(zombieDeathTexture02);

        TextureRegion zombieDeathTexture03 = textureAtlas.findRegion("Death03");
        zombieDeathTexture03.flip(true, false);
        zombieDeathTextures.add(zombieDeathTexture03);

        TextureRegion zombieDeathTexture04 = textureAtlas.findRegion("Death04");
        zombieDeathTexture04.flip(true, false);
        zombieDeathTextures.add(zombieDeathTexture04);

        TextureRegion zombieDeathTexture05 = textureAtlas.findRegion("Death05");
        zombieDeathTexture05.flip(true, false);
        zombieDeathTextures.add(zombieDeathTexture05);

        TextureRegion zombieDeathTexture06 = textureAtlas.findRegion("Death06");
        zombieDeathTexture06.flip(true, false);
        zombieDeathTextures.add(zombieDeathTexture06);

        TextureRegion zombieDeathTexture07 = textureAtlas.findRegion("Death07");
        zombieDeathTexture07.flip(true, false);
        zombieDeathTextures.add(zombieDeathTexture07);

        TextureRegion zombieDeathTexture08 = textureAtlas.findRegion("Death08");
        zombieDeathTexture08.flip(true, false);
        zombieDeathTextures.add(zombieDeathTexture08);

        TextureRegion zombieDeathTexture09 = textureAtlas.findRegion("Death09");
        zombieDeathTexture09.flip(true, false);
        zombieDeathTextures.add(zombieDeathTexture09);

        TextureRegion zombieDeathTexture10 = textureAtlas.findRegion("Death10");
        zombieDeathTexture10.flip(true, false);
        zombieDeathTextures.add(zombieDeathTexture10);

        TextureRegion zombieDeathTexture11 = textureAtlas.findRegion("Death11");
        zombieDeathTexture11.flip(true, false);
        zombieDeathTextures.add(zombieDeathTexture11);

        TextureRegion zombieDeathTexture12 = textureAtlas.findRegion("Death12");
        zombieDeathTexture12.flip(true, false);
        zombieDeathTextures.add(zombieDeathTexture12);

        TextureRegion robotDeathTexture01 = textureAtlas.findRegion("RobotDeath01");
        robotDeathTextures.add(robotDeathTexture01);

        TextureRegion robotDeathTexture02 = textureAtlas.findRegion("RobotDeath02");
        robotDeathTextures.add(robotDeathTexture02);

        TextureRegion robotDeathTexture03 = textureAtlas.findRegion("RobotDeath03");
        robotDeathTextures.add(robotDeathTexture03);

        TextureRegion robotDeathTexture04 = textureAtlas.findRegion("RobotDeath04");
        robotDeathTextures.add(robotDeathTexture04);

        TextureRegion robotDeathTexture05 = textureAtlas.findRegion("RobotDeath05");
        robotDeathTextures.add(robotDeathTexture05);

        TextureRegion robotDeathTexture06 = textureAtlas.findRegion("RobotDeath06");
        robotDeathTextures.add(robotDeathTexture06);

        TextureRegion robotDeathTexture07 = textureAtlas.findRegion("RobotDeath07");
        robotDeathTextures.add(robotDeathTexture07);

        TextureRegion robotDeathTexture08 = textureAtlas.findRegion("RobotDeath08");
        robotDeathTextures.add(robotDeathTexture08);

        TextureRegion robotDeathTexture09 = textureAtlas.findRegion("RobotDeath09");
        robotDeathTextures.add(robotDeathTexture09);







        playerShotTextureRegion = textureAtlas.findRegion("laserBlue03");
        playerShotTextureRegion2 = textureAtlas.findRegion("laserRed03");
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
