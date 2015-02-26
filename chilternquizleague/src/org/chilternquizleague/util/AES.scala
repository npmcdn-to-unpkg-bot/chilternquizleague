package org.chilternquizleague.util

import java.nio.charset.StandardCharsets
import java.util.Arrays
import com.google.common.io.BaseEncoding
import org.junit.Test
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import java.nio.charset.Charset

/**
 * @author gb106507
 */
trait AES {
  /**
   * AES Cipher function: encrypt 'input' state with Rijndael algorithm [�5.1];
   *   applies Nr rounds (10/12/14) using key schedule w for 'add round key' stage.
   *
   * @param   {number[]}   input - 16-byte (128-bit) input state array.
   * @param   {number[][]} w - Key schedule as 2D byte-array (Nr+1 x Nb bytes).
   * @returns {number[]}   Encrypted output state array.
   */
  def cipher(input: Array[Long], w: Array[Array[Long]]) = {
    var Nb = 4; // block size (in words): no of columns in state (fixed at 4 for AES)
    var Nr = w.length / Nb - 1; // no of rounds: 10/12/14 for 128/192/256-bit keys

    var state = Array.ofDim[Long](4, Nb)

    for (i <- 0 until 4 * Nb) {
      state(i % 4)(i / 4) = input(i)
    }

    state = addRoundKey(state, w, 0, Nb);

    for (round <- 1 until Nr) {
      state = subLongs(state, Nb);
      state = shiftRows(state, Nb);
      state = mixColumns(state, Nb);
      state = addRoundKey(state, w, round, Nb);
    }

    state = subLongs(state, Nb);
    state = shiftRows(state, Nb);
    state = addRoundKey(state, w, Nr, Nb);

    val output = Array.ofDim[Long](4 * Nb)

    for (i <- 0 until 4 * Nb) output(i) = (state(i % 4)(Math.floor(i / 4).toInt));

    output

  }

  /**
   * Perform key expansion to generate a key schedule from a cipher key [�5.2].
   *
   * @param   {number[]}   key - Cipher key as 16/24/32-byte array.
   * @returns {number[][]} Expanded key schedule as 2D byte-array (Nr+1 x Nb bytes).
   */
  def keyExpansion(key: Array[Long]) = {
    val Nb = 4; // block size (in words): no of columns in state (fixed at 4 for AES)
    val Nk = key.length / 4; // key length (in words): 4/6/8 for 128/192/256-bit keys
    val Nr = Nk + 6; // no of rounds: 10/12/14 for 128/192/256-bit keys

    val w = Array.ofDim[Array[Long]](Nb * (Nr + 1));
    var temp = Array.ofDim[Long](4);

    // initialise first Nk words of expanded key with cipher key
    for (i <- 0 until Nk) {
      val r = Array[Long](key(4 * i), key(4 * i + 1), key(4 * i + 2), key(4 * i + 3))
      w(i) = r;
    }

    // expand the key into the remainder of the schedule
    for (i <- Nk until Nb * (Nr + 1)) {
      w(i) = Array.ofDim[Long](4);
      for (t <- 0 until 4) temp(t) = w(i - 1)(t)
      // each Nk'th word has extra transformation
      if (i % Nk == 0) {
        temp = subWord(rotWord(temp));
        for (t <- 0 to 3) temp(t) = (temp(t) ^ rCon(i / Nk)(t)).toLong
      } // 256-bit key has subWord applied every 4th word
      else if (Nk > 6 && i % Nk == 4) {
        temp = subWord(temp);
      }
      // xor w[i] with w[i-1] and w[i-Nk]
      for (t <- 0 until 4) w(i)(t) = (w(i - Nk)(t) ^ temp(t)).toLong
    }

    w
  };

  /**
   * Apply SBox to state S [�5.1.1]
   * @private
   */
  private def subLongs(s: Array[Array[Long]], Nb: Int) = {
    for (r <- 0 until 4) {
      for (c <- 0 until Nb) s(r)(c) = sBox(byteConv(s(r)(c)).toInt);
    }
    s
  }

  /**
   * Shift row r of state S left by r bytes [�5.1.2]
   * @private
   */
  private def shiftRows(s: Array[Array[Long]], Nb: Int) = {
    val t = Array.ofDim[Long](4);
    for (r <- 1 to 3) {
      for (c <- 0 to 3) t(c) = s(r)((c + r) % Nb); // shift into temp copy
      for (c <- 0 to 3) s(r)(c) = t(c); // and copy back
    } // note that this will work for Nb=4,5,6, but not 7,8 (always 4 for AES):
    s // see asmaes.sourceforge.net/rijndael/rijndaelImplementation.pdf
  };

  /**
   * Combine bytes of each col of state S [�5.1.3]
   * @private
   */
  def mixColumns(s: Array[Array[Long]], Nb: Int) = {
    for (c <- 0 until 4) {
      var a = Array.ofDim[Long](4); // 'a' is a copy of the current column from 's'
      var b = Array.ofDim[Long](4); // 'b' is a�{02} in GF(2^8)
      for (i <- 0 until 4) {
        a(i) = s(i)(c);
        b(i) = if ((a(i) & 0x80L) != 0) a(i) << 1 ^ 0x011bL else a(i) << 1
      }
      // a[n] ^ b[n] is a�{03} in GF(2^8)
      s(0)(c) = b(0) ^ a(1) ^ b(1) ^ a(2) ^ a(3) // {02}�a0 + {03}�a1 + a2 + a3
      s(1)(c) = a(0) ^ b(1) ^ a(2) ^ b(2) ^ a(3) // a0 � {02}�a1 + {03}�a2 + a3
      s(2)(c) = a(0) ^ a(1) ^ b(2) ^ a(3) ^ b(3) // a0 + a1 + {02}�a2 + {03}�a3
      s(3)(c) = a(0) ^ b(0) ^ a(1) ^ a(2) ^ b(3) // {03}�a0 + a1 + a2 + {02}�a3
    }
    s
  };

  /**
   * Xor Round Key into state S [�5.1.4]
   * @private
   */
  def addRoundKey(state: Array[Array[Long]], w: Array[Array[Long]], rnd: Int, Nb: Int) = {
    for (r <- 0 until 4) {
      for (c <- 0 until Nb) {
        val a = state(r)(c)
        val b = w(rnd * 4 + c)(r)
        val res = a ^ b
        state(r)(c) = res.toLong
      }
    }
    state
  };

  def byteConv(b: Long): Long = if (b < 0) b + 256 else b

  /**
   * Apply SBox to 4-byte word w
   * @private
   */
  def subWord(w: Array[Long]) = {

    for (i <- 0 until 4) w(i) = sBox(byteConv(w(i)).toInt);
    w
  };

  /**
   * Rotate 4-byte word w left by one byte
   * @private
   */
  def rotWord(w: Array[Long]) = {
    var tmp = w(0)
    for (i <- 0 until 3) w(i) = w(i + 1);
    w(3) = tmp;
    w
  };

  // sBox is pre-computed multiplicative inverse in GF(2^8) used in subLongs and keyExpansion [�5.1.1]
  val sBox = Array[Long](0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
    0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
    0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
    0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
    0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
    0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
    0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
    0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
    0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
    0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
    0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
    0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
    0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
    0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
    0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
    0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16)

  // rCon is Round Constant used for the Key Expansion [1st col is 2^(r-1) in GF(2^8)] [�5.2]
  val rCon = Array[Array[Long]](Array(0x00, 0x00, 0x00, 0x00),
    Array(0x01, 0x00, 0x00, 0x00),
    Array(0x02, 0x00, 0x00, 0x00),
    Array(0x04, 0x00, 0x00, 0x00),
    Array(0x08, 0x00, 0x00, 0x00),
    Array(0x10, 0x00, 0x00, 0x00),
    Array(0x20, 0x00, 0x00, 0x00),
    Array(0x40, 0x00, 0x00, 0x00),
    Array(0x80, 0x00, 0x00, 0x00),
    Array(0x1b, 0x00, 0x00, 0x00),
    Array(0x36, 0x00, 0x00, 0x00))

}

/**
 * Encrypt a text using AES encryption in Counter mode of operation.
 *
 * Unicode multi-byte character safe
 *
 * @param   {string} plaintext - Source text to be encrypted.
 * @param   {string} password - The password to use to generate a key.
 * @param   {number} nBits - Number of bits to be used in the key; 128 / 192 / 256.
 * @returns {string} Encrypted text.
 *
 * @example
 *   var encr = Ctr.encrypt('big secret', 'pas�word', 256); // encr: 'lwGl66VVwVObKIr6of8HVqJr'
 */
object Crypto extends AES {
  def encrypt(pt: String, pwd: String, nBits: Int = 256): String = {
    val blockSize = 16; // block size fixed at 16 bytes / 128 bits (Nb=4) for AES
    if (!(nBits == 128 || nBits == 192 || nBits == 256)) return "" // standard allows 128/192/256 bit keys
    val plaintext = pt
    val password = pwd

    // use AES itself to encrypt password to get cipher key (using plain password as source for key
    // expansion) - gives us well encrypted key (though hashed key might be preferred for prod'n use)
    val nLongs = nBits / 8; // no bytes in key (16/24/32)
    val pwLongs: Array[Long] = (password.getBytes(StandardCharsets.UTF_8).map(a => byteConv(a.toLong)) ++ Array.ofDim[Long](nLongs).map(_ => 0.toLong)).slice(0, nLongs)

    var key = cipher(pwLongs, keyExpansion(pwLongs)); // gives us 16-byte key
    key = key ++ (key.slice(0, nLongs - 16)); // expand key to 16/24/32 bytes long

    // initialise 1st 8 bytes of counter block with nonce (NIST SP800-38A �B.2): [0-1] = millisec,
    // [2-3] = random, [4-7] = seconds, together giving full sub-millisec uniqueness up to Feb 2106
    val counterBlock = Array.ofDim[Long](blockSize);

    val nonce = System.currentTimeMillis() // timestamp: milliseconds since 1-Jan-1970
    val nonceMs = nonce % 1000;
    val nonceSec = Math.floor(nonce / 1000).toLong;
    val nonceRnd = Math.floor(Math.random() * 0xffff).toLong;
    // for debugging: nonce = nonceMs = nonceSec = nonceRnd = 0;

    for (i <- 0 until 2) counterBlock(i) = ((nonceMs >>> (i * 8)) & 0xff)
    for (i <- 0 until 2) counterBlock(i + 2) = ((nonceRnd >>> (i * 8)) & 0xff)
    for (i <- 0 until 4) counterBlock(i + 4) = ((nonceSec >>> i * 8) & 0xff)

    // and convert it to a string to go on the front of the ciphertext
    var ctrTxt = counterBlock.slice(0, 8)

    // generate key schedule - an expansion of the key into distinct Key Rounds for each round
    var keySchedule = keyExpansion(key);

    var blockCount = Math.ceil(plaintext.length / blockSize).round.toInt + 1
    blockCount = if (blockCount == 0) 1 else blockCount
    var ciphertxt = Array.ofDim[Long](blockCount, blockSize); // ciphertext as array of strings

    for (b <- 0 until blockCount) {
      // set counter (block #) in last 8 bytes of counter block (leaving nonce in 1st 8 bytes)
      // done in two stages for 32-bit ops: using two words allows us to go past 2^32 blocks (68GB)
      for (c <- 0 until 4) counterBlock(15 - c) = ((b >>> c * 8) & 0xff)
      for (c <- 0 until 4) counterBlock(15 - c - 4) = ((b / 0x100000000L >>> c * 8))

      var cipherCntr = cipher(counterBlock, keySchedule); // -- encrypt counter block --

      // block size is reduced on final block
      var blockLength = if (b < blockCount - 1) blockSize else (plaintext.length - 1) % blockSize + 1;
      var cipherChar = Array.ofDim[Long](blockLength);

      for (i <- 0 until blockLength) { // -- xor plaintext with ciphered counter char-by-char --
        cipherChar(i) = (cipherCntr(i) ^ byteConv(plaintext.charAt(b * blockSize + i).toLong))
      }
      ciphertxt(b) = cipherChar
    }

    // use Array.join() for better performance than repeated string appends

    val longs = ctrTxt ++ (for (a <- ciphertxt; b <- a) yield b)

    BaseEncoding.base64().encode(longs.map(_.toByte))
  }

  /**
   * Decrypt a text encrypted by AES in counter mode of operation
   *
   * @param   {string} ciphertext - Source text to be encrypted.
   * @param   {string} password - Password to use to generate a key.
   * @param   {number} nBits - Number of bits to be used in the key; 128 / 192 / 256.
   * @returns {string} Decrypted text
   *
   * @example
   *   var decr = Ctr.encrypt('lwGl66VVwVObKIr6of8HVqJr', 'pas�word', 256); // decr: 'big secret'
   */
  def decrypt(et: String, pwd: String, nBits: Int = 256): String = {
    val blockSize = 16; // block size fixed at 16 bytes / 128 bits (Nb=4) for AES
    if (!(nBits == 128 || nBits == 192 || nBits == 256)) return ""; // standard allows 128/192/256 bit keys
    val ciphertext = BaseEncoding.base64().decode(et).map(x => byteConv(x.toLong))
    val password = pwd.getBytes(StandardCharsets.UTF_8).map(_.toLong)

    // use AES to encrypt password (mirroring encrypt routine)
    val nLongs = nBits / 8; // no bytes in key

    val pwLongs: Array[Long] = (password ++ Array.ofDim[Long](nLongs)).slice(0, nLongs)

    var key = cipher(pwLongs, keyExpansion(pwLongs));
    key = key ++ (key.slice(0, nLongs - 16)); // expand key to 16/24/32 bytes long

    // recover nonce from 1st 8 bytes of ciphertext
    //var counterBlock = Array.ofDim[Long](8);
    val ctrTxt = ciphertext.slice(0, 8);
    val counterBlock = ctrTxt ++ Array.ofDim[Long](8)

    // generate key schedule
    val keySchedule = keyExpansion(key);

    // separate ciphertext into blocks (skipping past initial 8 bytes)
    var nBlocks = Math.ceil((ciphertext.length - 8) / blockSize).toInt + 1
    val ct = Array.ofDim[Long](nBlocks, blockSize);
    for (b <- 0 until nBlocks) ct(b) = ciphertext.slice(8 + b * blockSize, 8 + b * blockSize + blockSize);
    val ciphertextLong = ct.map(x => x.map(y => y)); // ciphertext is now array of block-length strings

    // plaintext will get generated block-by-block into array of block-length strings
    val plaintxt = Array.ofDim[Long](ciphertextLong.length, blockSize);

    for (b <- 0 until nBlocks) {
      // set counter (block #) in last 8 bytes of counter block (leaving nonce in 1st 8 bytes)
      for (c <- 0 until 4) counterBlock(15 - c) = (((b) >>> (c * 8)) & 0xff).toLong
      for (c <- 0 until 4) counterBlock(15 - c - 4) = ((((b + 1) / 0x100000000L - .9999999999).toLong >>> (c * 8)) & 0xff).toLong

      val cipherCntr = cipher(counterBlock, keySchedule); // encrypt counter block

      val plaintxtLong = Array.ofDim[Long](ciphertextLong(b).length);
      for (i <- 0 until ciphertextLong(b).length) {
        // -- xor plaintxt with ciphered counter byte-by-byte --
        plaintxtLong(i) = (cipherCntr(i) ^ ciphertextLong(b)(i))

      }
      plaintxt(b) = plaintxtLong
    }

    // join array of blocks into single plaintext string
    new String(for (a <- plaintxt; b <- a) yield b.toByte, StandardCharsets.UTF_8)

  }

}

object CtrTest extends App {

  val res = Crypto.encrypt("""{"email":"davidcgood@gmail.com","password":"277db9d5-42c6-49bd-ba23-ac7ce76c2b81"}""", "password")

  println(res)

  val dec = Crypto.decrypt(res, "password")

  println(dec)

 
}