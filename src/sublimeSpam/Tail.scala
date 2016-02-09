package sublimeSpam

class Tail(time: Long, val duration: Long) extends Entity(time) {
  require(duration >= 100)    //Shorter (or negative) durations might cause trouble.
  
  override def toString = "t" + time + "-" + (time + duration)
  
  val typeChar = 't'
  
  var hit = false
}