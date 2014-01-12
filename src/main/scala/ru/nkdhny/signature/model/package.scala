package ru.nkdhny.signature

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.Imports

/**
 * User: alexey
 * Date: 1/8/14
 * Time: 6:25 PM
 */
package object model {

  import com.mongodb.casbah.commons.conversions.scala._
  RegisterConversionHelpers()
  RegisterJodaTimeConversionHelpers()


  type Tagged[U] = { type Tag = U }
  type @@[T, U] = T with Tagged[U]

  class Tagger[U] {
    def apply[T](t : T) : T @@ U = t.asInstanceOf[T @@ U]
  }
  def tag[U] = new Tagger[U]

  trait id[T]
  type Id[T] = String @@ id[T]

  def Id[T](t: String): Id[T] =  tag[id[T]](t)

  implicit def idDbView[T](idOfT: Id[T]): DBObject = {
    MongoDBObject("id" -> idOfT.toString)
  }
  implicit def idObjView[T](d: DBObject): Option[Id[T]] = {
    d.getAs[String]("id").map(Id)
  }

  def iterableObjView[T](results: Iterable[DBObject])(implicit objView: DBObject=> Option[T]): Iterable[T] = {
    results.map(objView).flatten
  }

  trait RichMongoDBCollection {
    val underlying: MongoCollection
    def one[A, T](o: A)(implicit dbView: A=> DBObject, objView: DBObject=> Option[T]): Option[T] = {
      underlying.findOne(o)(dbView).flatMap(objView(_))
    }
  }

  implicit val mdbToRichMdb: MongoCollection => RichMongoDBCollection = m => new RichMongoDBCollection {
    val underlying: Imports.MongoCollection = m
  }

}
