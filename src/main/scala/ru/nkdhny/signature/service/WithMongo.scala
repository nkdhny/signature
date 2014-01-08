package ru.nkdhny.signature.service

import com.mongodb.casbah.Imports._
import com.typesafe.config.Config
import collection.JavaConversions._

/**
 * User: alexey
 * Date: 1/8/14
 * Time: 7:13 PM
 */
trait WithMongo {
  protected val config: Config
  private val mdbHosts = config.getStringList("signature.mongodb.host")
                               .map(h => h.split(":"))
                               .map(h=> new ServerAddress(h(0), h(1).toInt))

  protected val mongoDb = MongoClient(mdbHosts.toList)(config.getString("signature.mongodb.dbName"))
}
