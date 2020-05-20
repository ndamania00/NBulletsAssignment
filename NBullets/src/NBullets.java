import javalib.funworld.*;
import javalib.worldimages.*;
import java.util.Random;
import java.awt.Color;

import tester.*;

// Represents the NBullets Game
class NBullets extends World {

  // game constants
  static final int WIDTH =  720;
  static final int HEIGHT = WIDTH * 9 / 16; // ensures 16:9 aspect ratio
  static final double TICKRATE = 1.0 / 28;
  static final int BULLETSPEED = 8;
  static final int SHIPSPEED = BULLETSPEED / 2;
  static final int SHIPSIZE = WIDTH / 30;
  static final Color SHIPCOLOR = Color.magenta;
  static final Color BULLETCOLOR = Color.YELLOW;
  static final int DEFAULTBULLETS = 10;
  static final int BULLETSIZE = 2;
  static final int MAX_BULLETSIZE = SHIPSIZE / 2;
  static final double SHIP_SPAWN_RATE = 1; //measured in seconds
  static final WorldScene blankScene = new WorldScene(WIDTH, HEIGHT)
      .placeImageXY(
          new RectangleImage(
              WIDTH,
              HEIGHT,
              OutlineMode.SOLID,
              Color.DARK_GRAY), WIDTH / 2, HEIGHT / 2);

  int bulletsLeft = 40;
  int shipsDestroyed = 0;
  ILoBullet activeBullets;
  ILoShip activeShips;
  int currentTicks;
  Random randGen = new Random();

  // Constructor
  NBullets(int bulletsLeft, int shipsDestroyed, ILoBullet bulletList, 
      ILoShip shipList, int currentTicks) {
    this.bulletsLeft = bulletsLeft;
    this.shipsDestroyed = shipsDestroyed;
    this.activeBullets = bulletList;
    this.activeShips = shipList;
    this.currentTicks = currentTicks;
  }

  // Represents the bullets left to shoot
  NBullets(int bulletsLeft) {
    this(bulletsLeft, 0, new MtLoBullet(), new MtLoShip(), 0);
  }

  // Represents a random number generator
  NBullets(Random randGen) {
    this(DEFAULTBULLETS, 0, new MtLoBullet(), new MtLoShip(), 0);
    this.randGen = randGen;
  }

  // Draws the updating world scene
  public WorldScene makeScene() {

    WorldImage remainingBullets = new TextImage("Remaining Bullets: " + this.bulletsLeft, 
        Color.CYAN);
    WorldImage shipsDestroyedImage = new TextImage("   Score: " + this.shipsDestroyed, 
        Color.ORANGE);

    WorldImage combinedTextImage = new BesideImage(remainingBullets, shipsDestroyedImage);


    WorldScene sceneWithBullets = this.activeBullets.drawBullets(blankScene);
    WorldScene sceneWithPieces = this.activeShips.drawShips(sceneWithBullets);

    return sceneWithPieces.placeImageXY(combinedTextImage, WIDTH / 2, HEIGHT  - 20);
  }

  // Represents the on tick method and continuously updates the methods
  public World onTick() {

    int curTicks = (this.currentTicks + 1) % (int) (SHIP_SPAWN_RATE * Math.pow(TICKRATE, -1));

    if (curTicks == 0) {
      return new NBullets(
          this.bulletsLeft,
          this.shipsDestroyed + this.activeBullets.countHitShips(this.activeShips),
          this.activeBullets.updateBullets(this.activeShips),
          generateRandomShips(this.activeShips.updateShips(this.activeBullets)),
          curTicks++);
    }
    return new NBullets(
        this.bulletsLeft,
        this.shipsDestroyed + this.activeBullets.countHitShips(this.activeShips),
        this.activeBullets.updateBullets(this.activeShips),
        this.activeShips.updateShips(this.activeBullets),
        curTicks++);
  }

  // Launches the bullet which is shot by the user when pressing a key
  public World onKeyEvent(String keyName) {

    if (keyName.equals(" ") && this.bulletsLeft > 0) {
      return new NBullets(
          this.bulletsLeft - 1, 
          this.shipsDestroyed,
          new ConsLoBullet(
              new Bullet(
                  WIDTH / 2,
                  HEIGHT,
                  BULLETSIZE,
                  270,
                  1,
                  BULLETCOLOR), this.activeBullets),
          this.activeShips,
          this.currentTicks);
    }
    return this;

  }

  // Represents the world end method for when the game is over
  public WorldEnd worldEnds() {
    if (this.bulletsLeft == 0 && this.activeBullets.isEmpty()) {
      return new WorldEnd(
          true, 
          blankScene.placeImageXY(
              new AboveImage(
                  new TextImage("GAME OVER", 36, Color.cyan),
                  new TextImage("Final Score: " + this.shipsDestroyed, 18, Color.ORANGE)),
              WIDTH / 2, HEIGHT / 2));
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  // Generates a random number of ships for the game to display
  public ILoShip generateRandomShips(ILoShip other) {

    double x = this.randGen.nextDouble();

    if (x <= .6) {

      int leftOrRight = (int) Math.round(this.randGen.nextDouble());

      return new ConsLoShip(
          new Ship(
              leftOrRight * WIDTH,
              SHIPSIZE + this.randGen.nextDouble() * (HEIGHT - 2 * SHIPSIZE),
              SHIPSIZE, 180 * leftOrRight,
              SHIPCOLOR),
          generateRandomShips(other));
    }
    else {
      return other;
    }
  }
}

//Examples 
class ExamplesNBullets {

  Bullet b1 = new Bullet(45, 21, 10, 0, 1, NBullets.BULLETCOLOR);
  Bullet b2 = new Bullet(2, 302, 10, 180, 1, NBullets.BULLETCOLOR);
  Bullet b3 = new Bullet(122, 400, 10, 0, 1, NBullets.BULLETCOLOR);
  Bullet b4 = new Bullet(-1, 0, 10, 0, 1, NBullets.BULLETCOLOR);
  Bullet b5 = new Bullet(721, 5, 10, 0, 1, NBullets.BULLETCOLOR);
  Bullet b6 = new Bullet(5, 1280, 10, 0, 1, NBullets.BULLETCOLOR);
  Bullet b7 = new Bullet(45, 21, 12, 90, 2, NBullets.BULLETCOLOR);
  Bullet b8 = new Bullet(45, 21, 12, 45, 2, NBullets.BULLETCOLOR);
  Bullet b9 = new Bullet(53, 21, 10, 0, 1, NBullets.BULLETCOLOR);
  Bullet b10 = new Bullet(-6, 302, 10, 180, 1, NBullets.BULLETCOLOR);
  Bullet b11 = new Bullet(130 ,400, 10, 0, 1, NBullets.BULLETCOLOR);
  Bullet b12 = new Bullet(2, 302, 12, 360, 2, NBullets.BULLETCOLOR);
  Bullet b13 = new Bullet(2, 302, 12, 180, 2, NBullets.BULLETCOLOR);
  Bullet b14 = new Bullet(130, 400, 10, 0, 1, NBullets.BULLETCOLOR);
  Bullet b15 = new Bullet(53, 21, 10, 0, 1, NBullets.BULLETCOLOR);
  Bullet b16 = new Bullet(12, 105, 10, 180, 1, NBullets.BULLETCOLOR);

  ILoBullet bulletList =
      new ConsLoBullet(this.b1,
          new ConsLoBullet(this.b2,
              new ConsLoBullet(this.b3,
                  new ConsLoBullet(this.b4,
                      new MtLoBullet()))));

  ILoBullet bulletList2 = 
      new ConsLoBullet(this.b7,
          new ConsLoBullet(this.b8,
              new ConsLoBullet(this.b1,
                  new ConsLoBullet(this.b2,
                      new ConsLoBullet(this.b3, 
                          new ConsLoBullet(this.b4,
                              new MtLoBullet()))))));

  ILoBullet bulletList3 =
      new ConsLoBullet(this.b2,
          new ConsLoBullet(this.b3,
              new MtLoBullet()));

  ILoBullet bulletList4 = 
      new ConsLoBullet(this.b12,
          new ConsLoBullet(this.b13,
              new ConsLoBullet(this.b14,
                  new MtLoBullet())));

  ILoBullet bulletList5 = 
      new ConsLoBullet(this.b15,
          new ConsLoBullet(this.b12,
              new ConsLoBullet(this.b13,
                  new ConsLoBullet(this.b14,
                      new MtLoBullet()))));

  ILoBullet bulletList6 = 
      new ConsLoBullet(this.b7,
          new ConsLoBullet(this.b8,
              new ConsLoBullet(this.b1,
                  new ConsLoBullet(this.b2,
                      new ConsLoBullet(this.b3, 
                          new ConsLoBullet(this.b4,
                              new ConsLoBullet(this.b16,
                                  new MtLoBullet())))))));

  Ship s1 = new Ship(100, 10, NBullets.SHIPSIZE, 0, NBullets.SHIPCOLOR);
  Ship s2 = new Ship(100, 300, NBullets.SHIPSIZE, 180, NBullets.SHIPCOLOR);
  Ship s3 = new Ship(20, 300, NBullets.SHIPSIZE, 180, NBullets.SHIPCOLOR);
  Ship s4 = new Ship(152, 122, NBullets.SHIPSIZE, 180, NBullets.SHIPCOLOR);
  Ship s5 = new Ship(10, 100, NBullets.SHIPSIZE, 0, NBullets.SHIPCOLOR);
  Ship s6 = new Ship(77, 77, NBullets.SHIPSIZE, 180, NBullets.SHIPCOLOR);
  Ship s7 = new Ship(500, 300, NBullets.SHIPSIZE, 180, NBullets.SHIPCOLOR);
  Ship s8 = new Ship(302, 577, NBullets.SHIPSIZE, 180, NBullets.SHIPCOLOR);
  Ship s9 = new Ship(11, 302, NBullets.SHIPSIZE, 180, NBullets.SHIPCOLOR);
  Ship s10 = new Ship(14, 100, NBullets.SHIPSIZE, 0, NBullets.SHIPCOLOR);
  Ship s11 = new Ship(152 + NBullets.SHIPSPEED * Math.cos(180), 
      122, NBullets.SHIPSIZE, 180, NBullets.SHIPCOLOR);
  Ship s12 = new Ship(104, 10, NBullets.SHIPSIZE, 0, NBullets.SHIPCOLOR);
  Ship s13 = new Ship(100 + NBullets.SHIPSPEED * Math.cos(180), 
      300, NBullets.SHIPSIZE, 180, NBullets.SHIPCOLOR);
  Ship s14 = new Ship(720, 577, NBullets.SHIPSIZE, 180, NBullets.SHIPCOLOR);

  ILoShip mtShipList = new MtLoShip();
  ILoBullet mtBulletList = new MtLoBullet();

  ILoShip shipList = 
      new ConsLoShip(this.s1,
          new ConsLoShip(this.s2,
              this.mtShipList));

  ILoShip shipList1 = 
      new ConsLoShip(this.s12,
          new ConsLoShip(this.s13,
              this.mtShipList));

  ILoShip shipList2 = 
      new ConsLoShip(this.s1,
          new ConsLoShip(this.s2,
              new ConsLoShip(this.s3,
                  new ConsLoShip(this.s4,
                      new ConsLoShip(this.s5,
                          new ConsLoShip(this.s6,
                              new ConsLoShip(this.s7,
                                  new ConsLoShip(this.s8,
                                      new ConsLoShip(this.s9,
                                          new MtLoShip())))))))));

  ILoShip shipList3 = 
      new ConsLoShip(this.s1,
          new ConsLoShip(this.s2,
              new ConsLoShip(this.s14,
                  this.mtShipList)));

  NBullets game = new NBullets(10);

  CircleImage i1 = new CircleImage(NBullets.SHIPSIZE, OutlineMode.SOLID, NBullets.SHIPCOLOR);
  CircleImage i2 = new CircleImage(10, OutlineMode.SOLID, NBullets.BULLETCOLOR);


  WorldScene ws = new WorldScene(NBullets.WIDTH, NBullets.HEIGHT);
  WorldScene ws1 = new WorldScene(NBullets.WIDTH, NBullets.HEIGHT).placeImageXY(
      this.i1, 100, 10).placeImageXY(this.i1, 100, 300);
  WorldScene ws2 = new WorldScene(NBullets.WIDTH, NBullets.HEIGHT).placeImageXY(
      this.i2, 2, 302).placeImageXY(this.i2, 122, 400);

  boolean testBigBang(Tester t) {
    return game.bigBang(NBullets.WIDTH, NBullets.HEIGHT, NBullets.TICKRATE);
  }

  void testIsOffScreen(Tester t) {
    t.checkExpect(this.b1.isOffScreen(), false);
    t.checkExpect(this.b4.isOffScreen(), true);
    t.checkExpect(this.b5.isOffScreen(), true);
    t.checkExpect(this.b6.isOffScreen(), true);
    t.checkExpect(this.s4.isOffScreen(), false);
    t.checkExpect(this.s14.isOffScreen(), true);
  }

  void testCollidedWith(Tester t) {
    t.checkExpect(this.b1.collidedWith(this.s1), false);
    t.checkExpect(this.s3.collidedWith(this.b3), false);
    t.checkExpect(this.b2.collidedWith(this.s9), true);
    t.checkExpect(this.s3.collidedWith(this.b2), true);
  }

  void testDrawPiece(Tester t) {
    t.checkExpect(this.b1.drawPiece(), this.i2);
    t.checkExpect(this.s3.drawPiece(), new CircleImage(NBullets.SHIPSIZE, 
        OutlineMode.SOLID, NBullets.SHIPCOLOR));
  }

  void testGenerateNewBullets(Tester t) {
    t.checkExpect(this.b1.generateNewBullets(0, 180, this.bulletList), this.bulletList);
    t.checkExpect(this.b1.generateNewBullets(2, 45, this.bulletList), this.bulletList2);
  }

  void testUpdateBullet(Tester t) {
    t.checkExpect(this.b3.updateBullet(), this.b11);
    t.checkExpect(this.b2.updateBullet(), this.b10);
    t.checkExpect(this.b1.updateBullet(), this.b9);
  }

  void testUpdateShip(Tester t) {
    t.checkExpect(this.s4.updateShip(), this.s11);
    t.checkExpect(this.s5.updateShip(), this.s10);
  }

  void testCollidedWithBullets(Tester t) {
    t.checkExpect(this.s3.collidedWithBullets(this.bulletList), true);
    t.checkExpect(this.s2.collidedWithBullets(this.bulletList), false);
  }

  void testDrawShips(Tester t) {
    t.checkExpect(this.mtShipList.drawShips(NBullets.blankScene), NBullets.blankScene);
    t.checkExpect(this.shipList.drawShips(this.ws), this.ws1);
  }
  
  void testUpdateShips(Tester t) {
    t.checkExpect(this.mtShipList.updateShips(this.bulletList), this.mtShipList);
    t.checkExpect(this.shipList.updateShips(this.bulletList), this.shipList1);
    t.checkExpect(this.shipList3.updateShips(this.bulletList), this.shipList1);
  }

  void testCollidedWithBullet(Tester t) {
    t.checkExpect(this.mtShipList.listCollidedWithBullet(this.b1), false);
    t.checkExpect(this.shipList.listCollidedWithBullet(this.b4), false);
    t.checkExpect(this.shipList2.listCollidedWithBullet(this.b2), true);
  }

  void testDrawBullets(Tester t) {
    t.checkExpect(this.mtBulletList.drawBullets(this.ws), this.ws);
    t.checkExpect(this.mtBulletList.drawBullets(this.ws1), this.ws1);
    t.checkExpect(this.bulletList3.drawBullets(this.ws), this.ws2);
  }

  void testListCollidedWithShip(Tester t) {
    t.checkExpect(this.mtBulletList.listCollidedWithShip(this.s4), false);
    t.checkExpect(this.bulletList.listCollidedWithShip(this.s1), false);
    t.checkExpect(this.bulletList.listCollidedWithShip(this.s3), true);
  }

  void testUpdateBullets(Tester t) {
    t.checkExpect(this.mtBulletList.updateBullets(this.mtShipList), this.mtBulletList);
    t.checkExpect(this.mtBulletList.updateBullets(this.shipList2), this.mtBulletList);
    t.checkExpect(this.bulletList3.updateBullets(this.shipList2), this.bulletList4);
    t.checkExpect(this.bulletList.updateBullets(this.shipList2), this.bulletList5);
  }

  void testCountHitShips(Tester t) {
    t.checkExpect(this.mtBulletList.countHitShips(this.mtShipList), 0);
    t.checkExpect(this.mtBulletList.countHitShips(this.shipList2), 0);
    t.checkExpect(this.bulletList2.countHitShips(this.shipList2), 1);
    t.checkExpect(this.bulletList6.countHitShips(this.shipList2), 2);
  }

  void testIsEmpty(Tester t) {
    t.checkExpect(this.mtBulletList.isEmpty(), true);
    t.checkExpect(this.bulletList.isEmpty(), false);
  }
}