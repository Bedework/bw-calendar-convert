/* ********************************************************************
    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.
*/
package org.bedework.calsvci;

import org.bedework.calfacade.BwPrincipal;
import org.bedework.calfacade.BwSystem;

import java.io.Serializable;
import java.util.Collection;

/** Interface for handling bedework system parameters.
 *
 * @author Mike Douglass
 *
 */
public interface SysparsI extends Serializable {
  /** Get the (possibly cached) system pars using name supplied at init
   *
   * @return BwSystem object
   */
  BwSystem get();

  /** Get the system pars given name - will update cache object if the name is
   * the current system name.
   *
   * @param name
   * @return BwSystem object
   */
  BwSystem get(String name);

  /** Get the list of root accounts.
   *
   * @return Collection of String
   */
  Collection<String> getRootUsers();

  /** See if this is a calendar super user
   *
   * @param val
   * @return boolean true for a super user
   */
  boolean isRootUser(BwPrincipal val);

  /** Test for the presence of syspars. Helps ensure an empty system.
   *
   * @return true if BwSystem object is present in db
   */
  boolean present();
}
