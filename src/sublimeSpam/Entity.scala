package sublimeSpam

abstract class Entity(val time: Long) {
  
  override def toString: String
  
  val typeChar: Char
}