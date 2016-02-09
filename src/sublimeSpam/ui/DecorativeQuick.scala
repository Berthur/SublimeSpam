package sublimeSpam.ui

class DecorativeQuick(rand: util.Random, maxX: Int, maxY: Int) {
  private val realMinX = -maxX
  private val realMaxX = maxX * 2
  private val realMinY = -maxY
  private val realMaxY = maxY * 2
  
  val railColor = rand.nextInt(3)
  
  val dirX = {
    val candidateDirX = rand.nextInt(8) - 4
    if (candidateDirX >= 0) candidateDirX + 1 else candidateDirX
  }
  
  val dirY = {
    val candidateDirY = rand.nextInt(8) - 4
    if (candidateDirY >= 0) candidateDirY + 1 else candidateDirY
  }
  
  var x = {
    val candidateX = rand.nextInt(maxX)
    if (dirX > 0) -candidateX else maxX + candidateX
  }
  
  var y = {
    val candidateY = rand.nextInt(maxY)
    if (dirY > 0) - candidateY else maxY + candidateY
  }
  
  /**
   * Updates the Quick's position variables and checks if it is still inside the allowed area.
   * Returns true if it is, false if not.
   */
  def updatePosition(): Boolean = {
    x += dirX
    y += dirY
    if (x < realMinX || x > realMaxX || y < realMinY || y > realMaxY) false else true
  }
  
}
