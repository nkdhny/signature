package ru.nkdhny.signature.service

import com.typesafe.config.ConfigFactory

/**
 * User: alexey
 * Date: 1/8/14
 * Time: 7:15 PM
 */
trait WithConfig {
  protected val config = ConfigFactory.load()
}
