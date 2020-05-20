import javalib.funworld.*;

// Represents a list of ships
interface ILoShip {

  // draws ships onto a given WorldScene
  WorldScene drawShips(WorldScene prevScene);

  //update the state of every ship given a list of bullets
  ILoShip updateShips(ILoBullet activeBullets);

  // determine if any ship in list collided with given bullet
  boolean listCollidedWithBullet(Bullet first);

}

// Represents an empty list of ships
class MtLoShip implements ILoShip {

  // Draws an empty list of ships onto a given WorldScene
  public WorldScene drawShips(WorldScene prevScene) {
    return prevScene;
  }
  
  // update the state of all the ships in an empty list of ships
  // given a list of bullets
  public ILoShip updateShips(ILoBullet activeBullets) {
    return this;
  }

  // determine if any ship in an empty list of ships
  // collided with a given bullet
  public boolean listCollidedWithBullet(Bullet first) {
    return false;
  }
}

// Represents a list of ships with elements within it
class ConsLoShip implements ILoShip {
  Ship first;
  ILoShip rest;

  // Constructor
  ConsLoShip(Ship first, ILoShip rest) {
    this.first = first;
    this.rest = rest;
  }

  // draws every ship from a list of ships onto a given WorldScene
  public WorldScene drawShips(WorldScene prevScene) {
    return this.rest.drawShips(
        prevScene.placeImageXY(
            this.first.drawPiece(),
            (int) this.first.x,
            (int) this.first.y));
  }

  // updates the state of all the ships in a list of ships
  // given a list of bullets if they haven't collided with
  // the bullets or left the screen
  public ILoShip updateShips(ILoBullet activeBullets) {
    if (this.first.collidedWithBullets(activeBullets)
        || this.first.isOffScreen()) {
      return this.rest.updateShips(activeBullets);
    }
    else {
      return new ConsLoShip(
          this.first.updateShip(),
          this.rest.updateShips(activeBullets));
    }
  }

  // determine if any ship in a list of ships
  // collided with a given bullet
  public boolean listCollidedWithBullet(Bullet first) {
    return this.first.collidedWith(first)
        || this.rest.listCollidedWithBullet(first);
  }

}

// Represents a list of bullets
interface ILoBullet {

  //draws this list of bullets onto a given WorldScene
  WorldScene drawBullets(WorldScene prevScene);

  // Determines if any bullets in this list of bullets 
  // collided with the given ship
  boolean listCollidedWithShip(Ship ship);

  // updates all bullets in this list of ships
  ILoBullet updateBullets(ILoShip activeShips);

  // counts the number of ships in this list of ships that were 
  // hit by bullets
  int countHitShips(ILoShip activeShips);

  // determines if this list of bullets is empty
  boolean isEmpty();

}

//Represents an empty list of bullets
class MtLoBullet implements ILoBullet {

  //draws the bullets from this empty list of bullets onto a 
  // given WorldScene
  public WorldScene drawBullets(WorldScene prevScene) {
    return prevScene;
  }
  
  // Determines if any bullets in this empty list of bullets 
  // collided with the given ship
  public boolean listCollidedWithShip(Ship ship) {
    return false;
  }

  // updates all the bullets in a this empty list of ships
  public ILoBullet updateBullets(ILoShip activeShips) {
    return this;
  }

  // counts the number of ships in this empty list of ships that were 
  // hit by bullets
  public int countHitShips(ILoShip activeShips) {
    return 0;
  }

  // determines if this empty list of bullets is empty
  public boolean isEmpty() {
    return true;
  }
}

// Represents a list of bullets with elements within it
class ConsLoBullet implements ILoBullet {

  Bullet first;
  ILoBullet rest;

  // Constructor
  ConsLoBullet(Bullet first, ILoBullet rest) {
    this.first = first;
    this.rest = rest;
  }

  //draws all the bullets from this list of bullets onto a given WorldScene
  public WorldScene drawBullets(WorldScene prevScene) {
    return this.rest.drawBullets(
        prevScene.placeImageXY(
            this.first.drawPiece(),
            (int)this.first.x, 
            (int)this.first.y));
  }
  
  // Determines if any bullets in this list of bullets 
  // collided with the given ship
  public boolean listCollidedWithShip(Ship ship) {
    return this.first.collidedWith(ship)
        || this.rest.listCollidedWithShip(ship);
  }

  // updates all the bullets in this list of ships based on if
  // they collided or left the screen
  public ILoBullet updateBullets(ILoShip activeShips) {

    int curChain = this.first.colSoFar;

    if (activeShips.listCollidedWithBullet(this.first)) {
      //generate new list of bullets of length curChain + 1, CHECK
      //append that list to the rest of the bullets, CHECK
      // update the rest of the bullets with curChain + 1, CHECK
      return this.first.generateNewBullets(
          curChain + 1,
          360 / (curChain + 1),
          this.rest.updateBullets(activeShips));
    }
    else if (this.first.isOffScreen()) {
      return this.rest.updateBullets(activeShips);
    }
    else {
      return new ConsLoBullet(
          this.first.updateBullet(),
          this.rest.updateBullets(activeShips));
    }
  }

  // counts the number of ships in this list of ships that were 
  // hit by bullets
  public int countHitShips(ILoShip activeShips) {
    if (activeShips.listCollidedWithBullet(this.first)) {
      return 1 + this.rest.countHitShips(activeShips);
    }
    else {
      return this.rest.countHitShips(activeShips);
    }
  }

  // determines if this list of bullets is empty
  public boolean isEmpty() {
    return false;
  }

}