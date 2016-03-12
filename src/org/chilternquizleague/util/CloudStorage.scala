package org.chilternquizleague.util

import com.google.appengine.tools.cloudstorage.GcsFileOptions
import com.google.appengine.tools.cloudstorage.GcsFilename
import com.google.appengine.tools.cloudstorage.GcsInputChannel
import com.google.appengine.tools.cloudstorage.GcsOutputChannel
import com.google.appengine.tools.cloudstorage.GcsService
import com.google.appengine.tools.cloudstorage.GcsServiceFactory
import com.google.appengine.tools.cloudstorage.RetryParams
import java.io.ObjectOutputStream
import java.nio.channels.Channels
import java.nio.ByteBuffer
import java.io.ObjectInputStream
import java.io.OutputStream
import java.io.InputStream
import java.io.ByteArrayInputStream
import java.io.Reader
import java.io.Writer
import java.nio.charset.CharsetEncoder
import java.nio.charset.StandardCharsets

/**
 *
 */
object CloudStorage {

  private val bucket = "seismic-bonfire-602.appspot.com"
  private val BUFFER_SIZE = 2 * 1024 * 1024
  
  /**
   * This is the service from which all requests are initiated.
   * The retry and exponential backoff settings are configured here.
   */
  private val gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance())

  /**
   * Writes the provided object to the specified file using Java serialization. One could use
   * this same technique to write many objects, or with another format such as Json or XML or just a
   * DataOutputStream.
   *
   * Notice at the end closing the ObjectOutputStream is not done in a finally block.
   * See below for why.
   */
  private def writeObjectToFile(fileName: GcsFilename, content: Object) = {
    val outputChannel =
      gcsService.createOrReplace(fileName, GcsFileOptions.getDefaultInstance())

    val oout = new ObjectOutputStream(Channels.newOutputStream(outputChannel))
    try {
      oout.writeObject(content)
    } finally oout.close()
  }

  /**
   * Writes the byte array to the specified file. Note that the close at the end is not in a
   * finally.This is intentional. Because the file only exists for reading if close is called, if
   * there is an exception thrown while writing the file won't ever exist. (This way there is no
   * need to worry about cleaning up partly written files)
   */
  private def writeToFile(fileName: GcsFilename, mimeType:String, content:InputStream) = {

    val outputChannel = gcsService.createOrReplace(fileName, new GcsFileOptions.Builder().mimeType(mimeType).acl("public-read").build())
    
    try
    {
      copy(content, Channels.newOutputStream(outputChannel))
    }
 
    finally outputChannel.close()
  }
  
  private def copy(input:InputStream,  output:OutputStream) {
    try {
      val buffer = new Array[Byte](BUFFER_SIZE)
      var bytesRead = input.read(buffer);
      
      while (bytesRead != -1) {
        output.write(buffer, 0, bytesRead);
        bytesRead = input.read(buffer);
      }
    } finally {
      input.close();
      output.close();
    }
  }

  /**
   * Reads an object from the specified file using Java serialization. One could use this same
   * technique to read many objects, or with another format such as Json or XML or just a
   * DataInputStream.
   *
   * The final parameter to openPrefetchingReadChannel is a buffer size. It will attempt to buffer
   * the input by at least this many bytes. (This must be at least 1kb and less than 10mb) If
   * buffering is undesirable openReadChannel could be called instead, which is totally unbuffered.
   */
  private def readObjectFromFile(fileName: GcsFilename) = {
    val readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, 1024 * 1024)
    val oin = new ObjectInputStream(Channels.newInputStream(readChannel))
    oin.readObject()

  }

  /**
   * Reads the contents of an entire file and returns it as a byte array. This works by first
   * requesting the length, and then fetching the whole file in a single call. (Because it calls
   * openReadChannel instead of openPrefetchingReadChannel there is no buffering, and thus there is
   * no need to wrap the read call in a loop)
   *
   * This is really only a good idea for small files. Large files should be streamed out using the
   * prefetchingReadChannel and processed incrementally.
   */
  private def readFromFile(fileName: GcsFilename) = {
    val fileSize = gcsService.getMetadata(fileName).getLength().asInstanceOf[Int]
    val result = ByteBuffer.allocate(fileSize)
    val readChannel = gcsService.openReadChannel(fileName, 0)
    readChannel.read(result)

    result.array()
  }

  def saveFile(name:String, mimeType:String, content:InputStream, makePublic:Boolean = true)={
    val fileName = new GcsFilename(bucket,name)
    writeToFile(fileName, mimeType, content)
    s"http://storage.googleapis.com/$bucket/$name"
  }
  
  def getACL(name:String):String = {
    val fileName = new GcsFilename(bucket,name)
    gcsService.getMetadata(fileName).getOptions.getAcl
  }
}