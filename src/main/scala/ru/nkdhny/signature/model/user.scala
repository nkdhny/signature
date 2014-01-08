package ru.nkdhny.signature.model

/**
 * User: alexey
 * Date: 1/8/14
 * Time: 6:30 PM
 */
abstract class User

case class RegisteredUser(id: Id[RegisteredUser]) extends User
