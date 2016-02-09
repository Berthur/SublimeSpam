package sublimeSpam

class Quick(time: Long) extends Entity(time) {
  
  override def toString = "q" + time
  
  val typeChar = 'q'
}