package ru.nkdhny.signature

/**
 * User: alexey
 * Date: 1/8/14
 * Time: 6:25 PM
 */
package object model {


  type Tagged[U] = { type Tag = U }
  type @@[T, U] = T with Tagged[U]

  class Tagger[U] {
    def apply[T](t : T) : T @@ U = t.asInstanceOf[T @@ U]
  }
  def tag[U] = new Tagger[U]

  trait id[T]
  type Id[T] = String @@ id[T]

  def Id[T](t: String): Id[T] =  tag[id[T]](t)

}
