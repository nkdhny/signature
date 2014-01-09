package ru.nkdhny.signature.auth

/**
 * User: alexey
 * Date: 1/9/14
 * Time: 10:41 PM
 */
trait WithStubbingAuth {

  protected val authService: AuthService = new AuthServiceStub()

}
