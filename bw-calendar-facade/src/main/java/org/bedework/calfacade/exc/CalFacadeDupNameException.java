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

package org.bedework.calfacade.exc;

import static org.bedework.calfacade.exc.CalFacadeErrorCode.duplicateName;

/** Access exception somewhere in the calendar facade
 *
 * @author Mike Douglass douglm@bedework.edu
 */
public class CalFacadeDupNameException extends CalFacadeException {
  /** Constructor
   * @param name
   */
  public CalFacadeDupNameException(String name) {
    super(duplicateName, name);
  }
}
