package sublimeSpam.io

object Confuser {
  
  /*def leftRotate(s: Seq[Short], n: Int) = {
    require(n >= 0 && n < 32)
  	val first = s.head
  	var i = 0
  	val result = Array.ofDim[Short](s.length)
  	for (x <- s) {
  		val next = if (i == s.length - 1) first else s(i + 1)
  		result(i) = (((s(i) << n) | (next >>> (16 - n))) & 0xFFFF).toShort
  		i += 1
  	}
  	result
  }
  
  def rightRotate(s: Seq[Short], n: Int) = {
    require(n >= 0 && n < 32)
    val last = s.last
    var i = s.length - 1
    val result = Array.ofDim[Short](s.length)
    for (x <- s.reverse) {
      val previous = if (i == 0) last else s(i - 1)
      result(i) = (((s(i) >>> n) | (previous << (16 - n))) & 0xFFFF).toShort
      i -= 1
    }
    result
  }*/
  
  def leftRotate(s: String, n: Int) = {
    require(n >= 0 && n < 16)
  	val first = s.head
  	var i = 0
  	val result = Array.ofDim[Char](s.length)
  	for (x <- s) {
  		val next = if (i == s.length - 1) first else s(i + 1)
  		result(i) = (((s(i) << n) | (next >>> (16 - n))) & 0xFFFF).toChar
  		i += 1
  	}
  	result.mkString
  }
  
  def rightRotate(s: String, n: Int) = {
    require(n >= 0 && n < 16)
    val last = s.last
    var i = s.length - 1
    val result = Array.ofDim[Char](s.length)
    for (x <- s.reverse) {
      val previous = if (i == 0) last else s(i - 1)
      result(i) = (((s(i) >>> n) | (previous << (16 - n))) & 0xFFFF).toChar
      i -= 1
    }
    result.mkString
  }
  
  
  def invert(s: String): String = s.map(c => (~c & 0xFFFF).toChar)
  
  def hashString(s: String) = util.hashing.MurmurHash3.arrayHash[Char](s.toArray)
  
  def hashByteSeq(s: Seq[Byte]) = util.hashing.MurmurHash3.arrayHash[Byte](s.toArray)
  
  def stringToByteArray(s: String) = {
    val result = Array.ofDim[Byte](s.length * 2)
    var i = 0
    while (i < s.length) {
      val c = s(i)
      result(i * 2) = ((c >>> 8) & 0xFF).toByte    //first 8 bits
      result(i * 2 + 1) = (c & 0xFF).toByte        //last 8 bits
      i += 1
    }
    result
  }
  
  def byteArrayToString(s: Seq[Byte]) = {
    require(s.length % 2 == 0)
    val result = Array.ofDim[Char](s.length / 2)
    var i = 0
    while (i < s.length) {
      val firstByte = s(i)
      val lastByte = s(i + 1)
      result(i / 2) = (((firstByte & 0xFF) << 8) | (lastByte & 0xFF)).toChar
      i += 2
    }
    result.mkString
  }
  
  def intToByteArray(n: Int) = {
    val result = Array.ofDim[Byte](4)
    for (i <- 0 until 4) {
      result(i) = ((n >>> ((3 - i) * 8)) & 0xFF).toByte
    }
    result
  }
  
  def byteArrayToInt(s: Seq[Byte]) = {
    require(s.length == 4)
    var result = 0
    for (i <- 0 until 4) {
      result = result | ((s(i) & 0xFF) << ((3 - i) * 8))
    }
    result
  }
  
  /*def shortToByteArray(s: Seq[Short]) = {
    val result = Array.ofDim[Byte](s.length * 2)
    var i = 0
    while (i < s.length) {
      val short = s(i)
      result(i * 2) = (short >>> 8).toByte    //first 8 bits
      result(i * 2 + 1) = (short & 0xFF).toByte        //last 8 bits
      i += 1
    }
    result
  }
  
  def byteToShortArray(s: Seq[Byte]) = {
    require(s.length % 2 == 0)
    val result = Array.ofDim[Short](s.length / 2)
    var i = 0
    while (i < s.length) {
      val firstByte = s(i)
      val lastByte = s(i + 1)
      result(i / 2) = (((firstByte & 0xFF) << 8) | (lastByte & 0xFF)).toShort
      i += 2
    }
    result
  }*/
  
}