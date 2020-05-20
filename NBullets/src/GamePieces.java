import java.awt.Color;
import javalib.worldimages.*;

// Represents a game piece in the game
interface IGamePiece {

  //draws this GamePiece
  WorldImage drawPiece();

  //determines if this GamePiece is off screen or on screen
  boolean isOffScreen();

}

//Represents the abstraction of a game piece in the game
abstract class AGamePiece implements IGamePiece {

  double x;
  double y;
  int size;
  Color color;
  double direction; 

  // Constructor
  AGamePiece(double x, double y, int size, double direction, Color color) {
    this.x = x;
    this.y = y;
    this.size = size;
    this.direction = direction;
    this.color = color;
  }

  // Determines if this game piece is off the screen or not
  public boolean isOffScreen() {
    return this.x > NBullets.WIDTH
        || this.x < 0
        || this.y < 0
        || this.y > NBullets.HEIGHT;
  }

  //Determines if this game piece collided with another game piece
  public boolean collidedWith(AGamePiece other) {
    return Math.hypot(this.x - other.x, this.y - other.y) <= this.size + other.size;
  }

  // Draws this game piece
  public WorldImage drawPiece() {
    return new CircleImage(this.size, OutlineMode.SOLID, this.color);
  }
}

// Represents a bullet which is a game piece in the game
class Bullet extends AGamePiece {

  int colSoFar;

  // Constructor
  Bullet(double x, double y, int size, double direction, int colSoFar, Color color) {
    super(x, y, size, direction, color);
    this.colSoFar = colSoFar;
  }

  // Based on the number of collisions so far (represented by CurChain), creates a list of 
  // bullets with modified size and continues decreasing to zero
  public ILoBullet generateNewBullets(int curChain, int angleToAdd, ILoBullet restOfBullets) {
    if (curChain == 0) {
      return restOfBullets;
    }
    else {
      Bullet newBullet = 
          new Bullet(
              this.x,
              this.y,
              (this.size < NBullets.MAX_BULLETSIZE) ? this.size + 2 : this.size,
                  curChain * angleToAdd,
                  this.colSoFar + 1,
                  this.color);
      return new ConsLoBullet(
          newBullet, this.generateNewBullets(curChain - 1, angleToAdd, restOfBullets));
    }
  }
  
  // Creates a new Bullet with updated x and y coordinates to move in specified direction
  public Bullet updateBullet() {
    return new Bullet(
        this.x + (Math.cos(Math.toRadians(this.direction)) * NBullets.BULLETSPEED),
        this.y + (Math.sin(Math.toRadians(this.direction)) * NBullets.BULLETSPEED),
        this.size,
        this.direction,
        this.colSoFar,
        this.color);
  }
}

// Represents a Ship as a game piece in the game
class Ship extends AGamePiece {

  //Constructor
  Ship(double x, double y, int size, double direction, Color color) {
    super(x, y, size, direction, color);
  }

  // Creates a new ship with updated x coordinate to move either left or right in the game
  public Ship updateShip() {
    return new Ship(
        this.x + NBullets.SHIPSPEED * Math.cos(this.direction),
        this.y, this.size, this.direction, this.color);
  }

  // Determines if this ship collided with any of the bullets in the activebullets
  public boolean collidedWithBullets(ILoBullet activeBullets) {
    return activeBullets.listCollidedWithShip(this);
  }

}