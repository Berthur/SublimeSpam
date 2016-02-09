package sublimeSpam

class Rail(game: Game) {
  private val entities = collection.mutable.Queue[Entity]()  //These are ALWAYS added in chronological order.
  
  /**
   * The maximal value of the miss countdown (the value the countdown starts from at every miss)
   */
  private val maxMissCountdown = 20
  
  /**
   * Flag describing whether the rail is currently pressed down (for Tails).
   */
  var down = false
  
  /**
   * Keeps track of the last tail hit's precision.
   */
  var lastTailOff = 0
  
  /**
   * The miss countdown (counting down from its maximal value to 0 upon each miss).
   */
  var missCountdown = 0
  
  /**
   * Adds an entity to the rail.
   */
  def add(e: Entity) = entities += e
  
  /**
   * Tells if the rail contains any more entities.
   */
  def isEmpty = entities.isEmpty
  
  /**
   * Resets the countdown since the last miss (back to its maximal value).
   */
  def resetMissCountdown() = missCountdown = maxMissCountdown
  
  /**
   * Updates the time on the rail.
   */
  def updateTime(time: Long) = {
    if (!entities.isEmpty) {
      val next = entities.head
      
      next.typeChar match {
        
        case 'q' => {
          if (time > next.time + game.threshold) {
            game.miss()
            entities.dequeue
            resetMissCountdown()
          }
        }
        
        case 't' => {
          if (down && time > next.time - game.threshold && time < next.time + next.asInstanceOf[Tail].duration) {
            if (game.frames % 10 == 0) {
              game.scoreTail(lastTailOff)
            }
          } else if (time > next.time + next.asInstanceOf[Tail].duration) {
            entities.dequeue
            down = false
          } else if (!down && time > next.time + game.threshold) {
            if (!next.asInstanceOf[Tail].hit) game.miss()
            entities.dequeue
          } else {
            down = false
          }
        }
        
      }
    }
  }
  
  /**
   * Hits the rail at time 'time'.
   */
  def hit(time: Long): Unit = {
    down = false
    if (!entities.isEmpty) {
      val next = entities.head
        if (time >= next.time - game.threshold) {   //(Should be) hit on entity
          if (time > next.time + game.threshold) {  //Should never happen here.
            game.miss()
            entities.dequeue
            resetMissCountdown()
          } else {                                  //Confirmed hit on entity
            next.typeChar match {
              
              case 'q' => {
                game.scoreHit(time - next.time)
                entities.dequeue
              }
              
              case 't' => {
                val off = time - next.time
                game.scoreHit(off)
                next.asInstanceOf[Tail].hit = true
                lastTailOff = off.toInt
                down = true
              }
              
            }
          }
        } else {                                    //Empty hit (miss)
          game.miss
          resetMissCountdown()
        }
        
    }
  }
  
  /**
   * Represents a key release on this rail at time 'time'.
   */
  def release(time: Long): Unit = {
    if (down) {
      down = false
      
    }
    
  }
  
  /**
   * Returns all the entities that this rail currently holds and which start before the given time.
   * Passing the parameter -1, all the entities will be returned.
   */
  def getEntities(time: Long): Vector[Entity] = {
    if (entities.isEmpty) Vector[Entity]()
    if (time < 0) entities.toVector
    else {
      val firstOutside = entities.zipWithIndex.find(_._1.time > time)
      if (firstOutside.isDefined) {
        entities.take(firstOutside.get._2).toVector
      } else {
        entities.toVector
      }
    }
  }
  
  /**
   * Returns all the entities as a Vector.
   */
  def getEntities(): Vector[Entity] = entities.toVector
  
  /**
   * Returns the entity next in turn, wrapped in an Option. (None if there are no entities left.)
   */
  def nextEntity = if (entities.isEmpty) None else Some(entities.head)
  
  /**
   * Returns the last entity, wrapped in an Option. (None if there are no entities left.)
   */
  def lastEntity = if (entities.isEmpty) None else Some(entities.last)
  
  /**
   * Replaces the last entity in this rail with the given entity.
   */
  def replaceLast(e: Entity): Unit = if (!entities.isEmpty) entities(entities.length - 1) = e
  
  
}