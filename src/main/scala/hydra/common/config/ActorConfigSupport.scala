/*
 * Copyright (C) 2016 Pluralsight, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package hydra.common.config

import akka.actor.Actor
import com.typesafe.config.{Config, ConfigFactory}
import configs.syntax._
import hydra.util.ActorUtils

/**
  * Created by alexsilva on 10/28/15.
  */
trait ActorConfigSupport extends ConfigSupport {
  this: Actor =>

  /**
    * @see StringUtils.camel2underscores
    * @return The "standard" default name for actors used in Hydra, which is created by converting the
    *         class name of the actor from camel case to underscore case.
    */
  val thisActorName: String = ActorUtils.actorName(getClass)

  /**
    * Looks up config settings under the ``actor.{actorName}`` path defined in the application configuration file.
    *
    * @return The configuration object for this actor; empty if path not found.
    */
  val actorConfig: Config = applicationConfig.get[Config](s"actors.${thisActorName}").valueOrElse(ConfigFactory.empty())
}
