package sublimeSpam.io

import scala.io.Source
import java.io._
import scala.Vector
import sublimeSpam.Game
import sublimeSpam.Quick
import sublimeSpam.Rail
import sublimeSpam.Tail

object Loader {
  
  val trackFormatHead = "SublimeSpamTrack"
  val trackFilePrefix = "tracks/"
  val highscoreFile = "resources/highscores"
  
  def confuse(s: String) = Confuser.invert(Confuser.rightRotate(s, 13))
  def deconfuse(s: String) = Confuser.leftRotate(Confuser.invert(s), 13)
  
  def loadTrack(trackPath: String): Game = {
    var rails = -1
    var length = -1
    var bodyBegun = false
    var bodyEnded = false
    val railBuffer = collection.mutable.Buffer[Rail]()
    var gameOption: Option[Game] = None
    
    val file = new File(trackFilePrefix + trackPath)
    if (!file.exists()) throw new LoadTrackException("Requested track file does not exist.")
    val source = Source.fromFile(file)
    
    try {
      
      val lines = source.getLines.map( _.trim ).filter( !_.isEmpty )  //Empty lines are allowed in the file, and extra spaces in the end/beginning of lines.
      if (lines.isEmpty || lines.next() != trackFormatHead) throw new LoadTrackException("Wrong file format.")
      while(lines.hasNext && !bodyEnded) {
        val line = lines.next
        if (line.contains("rails:")) {
          val no = line.split(":").last.trim
          if (no.find(!_.isDigit).isDefined) throw new LoadTrackException("Track file error (rails:)")
          else {
            rails = no.toInt
          }
        } else if (line.contains("length:")) {
          val no = line.split(":").last.trim
          if (no.find(!_.isDigit).isDefined) throw new LoadTrackException("Track file error (length:)")
          else {
            length = no.toInt
          }
        } else if (line.contains("begin")) {
          if (rails <= 1) throw new LoadTrackException("Track file error (less than 2 rails or no rails defined)")
          else if (length < 1000) throw new LoadTrackException("Track file error (length less than 1000ms or no length defined)")
          else {
            bodyBegun = true
            gameOption = Some(new Game(rails))
            for (i <- 0 until rails) {
              railBuffer += new Rail(gameOption.get)
            }
          }
        } else if (line.contains("end")) {
          if (!bodyBegun) throw new LoadTrackException("Track file error (format body ended before it started)")
          else {
            bodyEnded = true
          }
        } else if (bodyBegun) {                //Entity listing
          val entityType = line.find(!_.isDigit).getOrElse('-')
          if (entityType == '-') throw new LoadTrackException("Track file error (no entity type defined)")
          val railNo = line.split(entityType).head
          if (railNo.isEmpty || railNo.find(!_.isDigit).isDefined) throw new LoadTrackException("Track file error (entity rail number)")
          if (railNo.toInt < 0 || railNo.toInt >= length) throw new LoadTrackException("Track file error (entity rail number out of bounds)")
          val parameter = line.split(entityType).tail.mkString("").trim
          // >>>>> Continue with the parameter (time):
          entityType match {
            case 'q' => {      //Quick
              if (parameter.find(!_.isDigit).isDefined) throw new LoadTrackException("Track file error (parameter for Quick entity not a number)")
              railBuffer(railNo.toInt).add(new Quick(parameter.toLong))
            }
            case 't' => {      //Tail
              val times = parameter.split("-").map(_.trim)
              if (times.length != 2) throw new LoadTrackException("Track file error: erroneous entity of type Tail.")
              if (times(0).find(!_.isDigit).isDefined || times(1).find(!_.isDigit).isDefined) throw new LoadTrackException("Track file error: erroneous entity of type Tail.")
              val start = times(0).toInt
              val end = times(1).toInt
              if (end - start < 100) throw new LoadTrackException("Track file error: entity of type Tail too short (or negative).")
              railBuffer(railNo.toInt).add(new Tail(start, end - start))
            }
            case _ => throw new LoadTrackException("Track file error: entityType '" + entityType + "' unknown.")
          }
        }
      }
      if (!bodyBegun) throw new LoadTrackException("Track file error: format body does not begin.")
      
    } catch {
      //Exception handling:
      case e: LoadTrackException => throw e
      case _: Exception => throw new LoadTrackException("Unknown error loading map.")
    } finally {
      source.close()
    }
  
    gameOption.get.rails = railBuffer.toVector
    gameOption.get.duration = length
    gameOption.get
  }
  
  
  private def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) }
    finally { p.close() }
  }
  
  
  def saveTrack(game: Game, mp3fileName: String, difficulty: String, username: String): Unit = {
    val railNo = game.rails.length
    val allEntities = game.rails.zipWithIndex.map( tuple => (tuple._1.getEntities(-1), tuple._2) ).map(tuple => tuple._1.map(ent => (ent, tuple._2))).flatten
    val last = allEntities.maxBy(tuple => if (tuple._1.typeChar == 't') tuple._1.time + tuple._1.asInstanceOf[Tail].duration else tuple._1.time)._1
    val length = (if (last.typeChar == 't') last.time + last.asInstanceOf[Tail].duration  else last.time) + 4000
    val entities = allEntities.sortBy(_._1.time)
    val data = Vector(trackFormatHead, "rails:" + railNo, "length:" + length, "begin") ++ entities.map(tuple => tuple._2 + tuple._1.toString) :+ "end"
    
    val userFolder = new File(trackFilePrefix + "/" + username)
    val path = if (userFolder.exists && userFolder.isDirectory) trackFilePrefix + "/" + username + "/" else trackFilePrefix
    val filenameStem = mp3fileName.take(mp3fileName.length - 4) + ";" + difficulty + ";"
    var id = 1
    while (new File(path + filenameStem + id).exists) {
      id += 1
    }
    write(path + filenameStem + id)
    
    def write(pathAndName: String) = {
      printToFile(new File(pathAndName)) { p =>
        data.foreach(p.println)
      }
    }
    
  }
  
  
  def loadFileToString(filename: String) = {
    val file = new File(filename)
    if (!file.exists()) throw new LoadFileException("Requested file: " + filename + " does not exist.")
    val source = Source.fromFile(file)
    var result = ""
    try {
      val  t = source.mkString
      result = t
    } catch {
      case _: Exception => throw new LoadFileException("Unknown error loading file.")
    } finally {
      source.close()
    }
    result
  }
  
  
  /**
   * Loads the configuration file and returns a Map[String, String] containing its
   * information. All integer values are returned as strings and should be checked.
   */
  def loadConfig: Map[String, String] = {
    val file = new File("config.txt")
    if (!file.exists()) throw new LoadConfigException("Requested config file does not exist.")
    val source = Source.fromFile(file)
    
    try {
      val lines = source.getLines.map( _.trim ).filter( str => !str.isEmpty && !str.startsWith("#") )  //Empty lines and comments (#) are allowed in the file, and extra spaces in the end/beginning of lines.
      val result = collection.mutable.Map[String, String]()
      for (line <- lines) {
        if (line.contains(":")) {
          val parameter = line.split(":")(0).trim
          val value = line.split(":").tail.mkString(":").trim
          result += (parameter -> value)
        }
      }
      result.toMap
    } catch {
      //Exception handling:
      case e: LoadConfigException => throw e
      case _: Exception => throw new LoadConfigException("Unknown error loading config file.")
    } finally {
      source.close()
    }
  }
  
  
  def saveToConfig(attribute: String, value: String) = {
    val file = new File("config.txt")
    if (!file.exists()) throw new LoadConfigException("Requested config file does not exist.")
    var source = Source.fromFile(file)
    
    try {
      val lines = source.getLines.map( _.trim )
      var lineCount = 0
      var lineToChange = -1
      var done = false
      while (lines.hasNext) {
        lineCount += 1
        val line = lines.next()
        if (!done) {
          if (line.contains(":")) {
            val parameter = line.split(":")(0).trim
            val value = line.split(":").tail.map(_.trim).mkString(":")
            if (parameter == attribute) {
              lineToChange = lineCount - 1
              done = true
            }
          }
        }
      }
      source.close
      source = Source.fromFile(file)
      val originalLines = Array.ofDim[String](lineCount)
      source.getLines.copyToArray(originalLines)
      source.close()
      val newLines = if (done) {
        originalLines(lineToChange) = attribute + ": " + value
        originalLines
      } else {
        originalLines :+ (attribute + ": " + value)
      }
      printToFile(new File("config.txt")) { p =>
        newLines.foreach( p.println )
      }
    } catch {
      case e: LoadConfigException => throw e
      case e: SaveConfigException => throw e
      //case _: Exception => throw new SaveConfigException("Unknown error saving to config file.")
      case e: Exception => throw e
    } finally{
      source.close()
    }
  }
  
  
  def getTracks: Array[(java.io.File, String)] = {
    val authors = new java.io.File(trackFilePrefix).listFiles.filter(_.isDirectory).map(_.getName)
    authors.map( author => new java.io.File(trackFilePrefix + "/" + author).listFiles.filter( !_.getName.endsWith(".mp3") ).map( trackFile => (trackFile, author) ) ).flatten
  }
  
  /**
   * Returns the list of all tracks and some of their data in the form of an Array[Array[String]].
   * The data is stored in the following order: trackName, artist, difficulty, id.
   * The returned array is sorted first by trackname, then by artist and then by difficulty.
   */
  def getTrackList: Array[Array[String]] = {
    val splitList = getTracks.map(tuple => tuple._1.getName.split(";") :+ tuple._2)
    val possibleError = splitList.find(_.length != 5)
    if (possibleError.isDefined) {
      throw new LoadTrackException("Incorrect trackname: '" + possibleError.get.mkString(";") + "'.")
    }
    splitList.sortBy( data => (data(0), data(1), (data(2) match {case "e" => 0; case "m" => 1; case "h" => 2; case "x" => 3 case _ => 4} )) ) 
  }
  
  /**
   * Returns a list of all the mp3 files in 'username''s directory.
   */
  def getmp3s(username: String): Array[File] = {
    val file = new File(trackFilePrefix + username)
    if (file.exists && file.isDirectory) {
      file.listFiles.filter(_.getName.endsWith(".mp3"))
    } else {
      Array[File]()
    }
  }
  
  /**
   * Returns a list of the filename data of all the mp3 files in 'username''s directory.
   * Said data is in the form: trackName, artist.
   * The returned array is sorted first by trackname, then by artist.
   */
  def getmp3List(username: String): Array[Array[String]] = {
    val splitList = getmp3s(username).map(file => (file.getName.take(file.getName.length - 4) + " ").split(";").map(_.trim))    //Converts the list of mp3s into a list of their filename data.
    val possibleError = splitList.find(_.length != 2)
    if (possibleError.isDefined) {
      throw new LoadTrackException("Incorrect mp3-filename: '" + possibleError.get.mkString(";") + "'.")
    }
    splitList.sortBy( data => (data(0), data(1)) )
  }
  
  
  /**
   * Writes the binary array s to a file 'filename'.
   * (No try/catch in this method!)
   */
  def writeBinary(filename: String, s: Array[Byte]): Unit = {
    val out = new java.io.FileOutputStream(filename)
    out.write(s)
    out.close()
  }
  
  /**
   * Reads and returns the file 'filename' as an array of Bytes.
   * (No try/catch in this method!)
   */
  def readBinary(filename: String): Array[Byte] = {
    val lengthInBytes = new java.io.File(filename).length.toInt
    val in = new java.io.FileInputStream(filename)
    val byteArray = Array.ofDim[Byte](lengthInBytes)
    in.read(byteArray)
    in.close()
    byteArray
  }
  
  /**
   * Confuses the highscore data and saves it in the highscores file, together with a hash of itself.
   */
  def saveHighScores(data: Map[String, (Int, String, String)]): Unit = {
    if (data.isEmpty) {}
    else {
      var dataString = ""
      for (tuple <- data) {
        val compString = "<" + tuple._1 + ";" + tuple._2._1 + ";" + tuple._2._2 + ";" + tuple._2._3 + ">"
        dataString += compString
      }
      val confusedString = confuse(dataString)
      val dataBytes = Confuser.stringToByteArray(confusedString)
      val hash = Confuser.hashByteSeq(dataBytes)
      val resultBytes = dataBytes ++ Confuser.intToByteArray(hash)
      try {
        writeBinary(highscoreFile, resultBytes)
      } catch {
        case _: Exception => throw new SaveHighScoresException("Unknown error saving highscores.")
      }
    }
    
  }
  
  /**
   * Loads and deconfuses the highscores from its file, checks their validity and checks (briefly) that the data
   * has not been altered. Then, returns the highscore data in the form of a Map.
   */
  def loadHighScores: Map[String, (Int, String, String)] = {
    val result = collection.mutable.Map[String, (Int, String, String)]()  // Author/trackname -> (highest score, username, date)
    try {
      if (new File(highscoreFile).length == 0) Map[String, (Int, String, String)]()
      else {
        val bytes = readBinary(highscoreFile)
        val data = bytes.take(bytes.length - 4)
        val hash = Confuser.byteArrayToInt(bytes.drop(bytes.length - 4))
        val newHash = Confuser.hashByteSeq(data)
        if (hash == newHash) {
          val confusedString = Confuser.byteArrayToString(data)
          val string = deconfuse(confusedString)
          if (!string.contains("<")) throw new LoadHighScoresException("Invalid result string.")
          val trackStrings = string.split("<").filter(_.length > 0).map(_.filter(_ != '>'))
          for (trackString <- trackStrings) {
            val args = trackString.split(";")
            if (args.length < 7) throw new LoadHighScoresException("To few arguments in '" + trackString + "'.")
            val trackname = args.take(4).mkString(";")
            if (args(4).find(char => !char.isDigit && char != '-').isDefined) throw new LoadHighScoresException("Invalid formatting (Int) in: '" + trackString + "'.")
            val score = args(4).toInt
            val player = args(5)
            val date = args(6)
            if (date.split("-").length != 3 || date.filter(_ != '-').find(!_.isDigit).isDefined) throw new LoadHighScoresException("Invalid date format '" + date + "'.")
            result += trackname -> (score, player, date)
          }
          Map[String, (Int, String, String)]() ++ result
          
        } else {
          throw new LoadHighScoresException("Highscores file altered (hash does not match)!")
        }
      }
    }
    catch {
      case e: LoadHighScoresException => throw e
      case _: Exception => throw new LoadHighScoresException("Unknown exception loading highscores.")
    }
  }
  
}

class LoadTrackException(msg: String) extends Exception(msg)
class LoadFileException(msg: String) extends Exception(msg)
class LoadConfigException(msg: String) extends Exception(msg)
class SaveConfigException(msg: String) extends Exception(msg)
class LoadHighScoresException(msg: String)  extends Exception(msg)
class SaveHighScoresException(msg: String) extends Exception(msg)
