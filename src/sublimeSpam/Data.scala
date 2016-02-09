package sublimeSpam

import sublimeSpam.io.Loader

object Data {
  
  private var configInfo = Loader.loadConfig
  
  var username = ""
  
  var windowHeightValue = 3    //0-4
  
  var speedValue = 3           //0-4
  
  val highScores = collection.mutable.Map[String, (Int, String, String)]() ++ io.Loader.loadHighScores
  
  println(highScores)
  
  loadConfig()
  
  def saveHighScores() = io.Loader.saveHighScores(Map[String, (Int, String, String)]() ++ highScores)
  
  def registerScore(score: Int, trackName: String, player: String) = {
    if (!highScores.contains(trackName) || highScores(trackName)._1 < score) {
      val dateString = java.time.LocalDate.now.toString
      highScores += trackName -> (score, player, dateString)
      saveHighScores()
    }
  }
  
  /**
   * Loads the config file so that the Data object matches it.
   */
  def loadConfig(): Unit = {
    configInfo = Loader.loadConfig
    try {
      username = configInfo("username")
      if (configInfo.contains("windowHeightValue")) {
        val candidate = configInfo("windowHeightValue")
        if (!candidate.isEmpty && !candidate.find(!_.isDigit).isDefined) {
          windowHeightValue = Math.max(0, Math.min(4, candidate.toInt))
        } else {
          Loader.saveToConfig("windowHeightValue", windowHeightValue.toString)
        }
      } else {
        Loader.saveToConfig("windowHeightValue", windowHeightValue.toString)
      }
      if (configInfo.contains("speedValue")) {
        val candidate = configInfo("speedValue")
        if (!candidate.isEmpty && !candidate.find(!_.isDigit).isDefined) {
          speedValue = Math.max(0, Math.min(4, candidate.toInt))
        } else {
          Loader.saveToConfig("speedValue", speedValue.toString)
        }
      } else {
        Loader.saveToConfig("speedValue", speedValue.toString)
      }
    } catch {
      case e1: java.util.NoSuchElementException => {    //Add username parameter to config file
        Loader.saveToConfig("username", "")
        configInfo = Loader.loadConfig
        loadConfig()
      }
      case e2: Error => throw e2
    }
  }
  
  /**
   * Updates the config file to match the current settings of the Data object.
   */
  def updateConfig(): Unit = {
    println("UPDATING CONFIG")
    Loader.saveToConfig("username", username)
    Loader.saveToConfig("windowHeightValue", windowHeightValue.toString)
    Loader.saveToConfig("speedValue", speedValue.toString)
    configInfo = Loader.loadConfig
  }
  
  // ===Track data===
  def entityVisibilityTime = speedValue match {
    case 0 => 1400
    case 1 => 1200
    case 2 => 1000
    case 3 => 850
    case 4 => 700
    case _ => 850
  }
  
  def arenaWidth = 600
  
  def arenaHeight = windowHeightValue match {
    case 0 => 450
    case 1 => 600
    case 2 => 750
    case 3 => 900
    case 4 => 1050
    case _ => 900
  }
  
  def headerHeight = windowHeightValue match {
    case 0 => 60
    case 1 => 70
    case 2 => 85
    case 3 => 100
    case 4 => 120
    case _ => 100
  }
  
  def ballRadius = windowHeightValue match {
    case 0 => 30
    case 1 => 35
    case 2 => 40
    case 3 => 50
    case 4 => 65
    case _ => 50
  }
  
  def ballRoundness = windowHeightValue match {
    case 0 => 40
    case 1 => 50
    case 2 => 60
    case 3 => 75
    case 4 => 85
    case _ => 75
  }
  
  
  def formatLargeNumber(n: Int) = {
    var nString = n.toString
    var formattedNString = ""
    while (nString.length > 3) {
      formattedNString = nString.drop(nString.length - 3) + " " + formattedNString
      nString = nString.take(nString.length - 3)
    }
    formattedNString = nString + " " + formattedNString
    formattedNString
  }
  
}