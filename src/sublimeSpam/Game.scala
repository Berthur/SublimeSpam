package sublimeSpam

class Game(railNo: Int) {
  require(railNo > 1)    //This is strictly required.
  val threshold = 175
  val baseScore = 100
  val tailScore = 5      //Per tenth of a second
  val streakPerMultiplier = 15
  val multipliers = Vector(1, 2, 4, 8)
  val missPenaltyCoefficient = 0.25
  
  var duration = -1
  
  var rails: Vector[Rail] = {
    var res = Vector[Rail]()
    for (i <- 0 until railNo) {
      res = res :+ new Rail(this) {
      }
    }
    res
  }
  
  def entitiesLeft = rails.map(_.getEntities.length).sum
  
  var time = 0L
  
  var score = 0
  
  var streak = 0
  
  var hits = 0
  
  var passed = 0
  
  var lastHitOffset: Option[Int] = None
  
  var frames = 0
  
  /**
   * The current score multiplier.
   */
  def multiplier = multipliers(Math.min(streak / streakPerMultiplier, multipliers.length - 1))
  
  /**
   * Updates the Game.
   */
  def frame() = {
    for (rail <- rails) {
      rail.updateTime(time)
    }
    frames += 1
  }
  
  /**
   * Calculates the score to be added when an entity is hit 'off' milliseconds off.
   */
  def scoreHit(off: Long) = {
    lastHitOffset = Some(off.toInt)
    val coefficient = (threshold - Math.abs(off).toDouble) / threshold
    val rawScore = (coefficient * multiplier * baseScore).toInt
    val hitScore = ((rawScore / 10 + Math.round((rawScore % 10) / 10.toDouble)) * 10).toInt  //Rounds to closest 10s.
    score += hitScore
    println("Hit! " + off + " off; +" + hitScore + " score.")
    streak += 1
    hits += 1
    passed += 1
  }
  
  /**
   * Calculates the score to be added when a Tail entity is (continued to be) hit 'off' milliseconds off.
   */
  def scoreTail(off: Long) = {
    val coefficient = (threshold - Math.abs(off).toDouble) / threshold
    val hitScore = (coefficient * multiplier * tailScore).toInt + 1
    score += hitScore
  }
  
  /**
   * Tells the game that the player has missed.
   */
  def miss() = {
    lastHitOffset = None
    streak = 0
    println("Empty hit!")
    score -= (baseScore * missPenaltyCoefficient).toInt
    passed += 1
  }
  
}