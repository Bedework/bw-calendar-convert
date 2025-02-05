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
package org.bedework.convert.ical;

import org.bedework.base.exc.BedeworkException;
import org.bedework.calfacade.BwAttendee;
import org.bedework.calfacade.BwEvent;
import org.bedework.calfacade.BwEventObj;
import org.bedework.calfacade.BwFreeBusyComponent;
import org.bedework.calfacade.BwOrganizer;
import org.bedework.calfacade.ifs.IcalCallback;
import org.bedework.calfacade.svc.EventInfo;
import org.bedework.calfacade.util.ChangeTable;
import org.bedework.util.calendar.IcalDefs;
import org.bedework.util.calendar.PropertyIndex.PropertyInfoIndex;
import org.bedework.util.logging.BwLogger;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.parameter.FbType;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.Organizer;

/** Class to provide utility methods for translating to BwFreeBusy from ical4j classes
 *
 * @author Mike Douglass   douglm  rpi.edu
 */
public class BwFreeBusyUtil extends IcalUtil {
  private static final BwLogger logger =
          new BwLogger().setLoggedClass(BwFreeBusyUtil.class);
  
  /**
   * @param cb
   * @param val
   * @return BwFreeBusy
   */
  public static EventInfo toFreeBusy(final IcalCallback cb,
                                     final VFreeBusy val) {
    if (val == null) {
      return null;
    }

    try {
      final PropertyList<Property> pl = val.getProperties();

      if (pl == null) {
        // Empty VEvent
        return null;
      }

      final BwEvent fb = new BwEventObj();
      final EventInfo ei = new EventInfo(fb);

      final ChangeTable chg = ei.getChangeset(cb.getPrincipal().getPrincipalRef());

      fb.setEntityType(IcalDefs.entityTypeFreeAndBusy);

      setDates(cb.getPrincipal().getPrincipalRef(),
               ei,
               pl.getProperty(Property.DTSTART),
               pl.getProperty(Property.DTEND),
               pl.getProperty(Property.DURATION));

      for (final Property prop: pl) {
        String pval = prop.getValue();
        if ((pval != null) && (pval.length() == 0)) {
          pval = null;
        }

        final PropertyInfoIndex pi = PropertyInfoIndex.fromName(
                prop.getName());
        if (pi == null) {
          logger.debug(
                  "Unknown property with name " + prop.getName() +
                          " class " + prop.getClass() +
                          " and value " + pval);
          continue;
        }

        switch (pi) {
          case ATTENDEE:
            /* ------------------- Attendee -------------------- */

            final BwAttendee att = getAttendee(cb, (Attendee)prop);
            fb.addAttendee(att);
            chg.addValue(pi, att);

            break;

          case COMMENT:
            /* ------------------- Comment -------------------- */

            // LANG
            fb.addComment(null, pval);
            chg.addValue(pi, pval);

          case DTEND:
            /* ------------------- DtEnd -------------------- */

            break;

          case DTSTAMP:
            /* ------------------- DtStamp -------------------- */

            chg.changed(pi, fb.getDtstamp(), pval);
            fb.setDtstamp(pval);

          case DTSTART:
            /* ------------------- DtStart -------------------- */

            break;

          case FREEBUSY:
            /* ------------------- freebusy -------------------- */

            final FreeBusy fbusy = (FreeBusy)prop;
            final PeriodList perpl = fbusy.getPeriods();
            final Parameter par = getParameter(fbusy, "FBTYPE");
            final int fbtype;

            if (par == null) {
              fbtype = BwFreeBusyComponent.typeBusy;
            } else if (par.equals(FbType.BUSY)) {
              fbtype = BwFreeBusyComponent.typeBusy;
            } else if (par.equals(FbType.BUSY_TENTATIVE)) {
              fbtype = BwFreeBusyComponent.typeBusyTentative;
            } else if (par.equals(FbType.BUSY_UNAVAILABLE)) {
              fbtype = BwFreeBusyComponent.typeBusyUnavailable;
            } else if (par.equals(FbType.FREE)) {
              fbtype = BwFreeBusyComponent.typeFree;
            } else {
              if (logger.debug()) {
                logger.debug(
                        "Unsupported parameter " + par.getName());
              }

              throw new IcalMalformedException(
                      "parameter " + par.getName());
            }

            final BwFreeBusyComponent fbc = new BwFreeBusyComponent();

            fbc.setType(fbtype);

            for (final Period per: perpl) {
              fbc.addPeriod(per);
            }

            fb.addFreeBusyPeriod(fbc);
            chg.addValue(pi, fbc);

            break;

          case ORGANIZER:
            /* ------------------- Organizer -------------------- */

            final BwOrganizer org = getOrganizer(cb, (Organizer)prop);
            fb.setOrganizer(org);
            chg.addValue(pi, org);

            break;

          case UID:
            /* ------------------- Uid -------------------- */

            chg.changed(pi, fb.getUid(), pval);
            fb.setUid(pval);

            break;

          default:
            if (logger.debug()) {
              logger.debug("Unsupported property with class " +
                                   prop.getClass() +
                                   " and value " + pval);
            }
        }
      }

      return ei;
    } catch (final BedeworkException bfe) {
      throw bfe;
    } catch (final Throwable t) {
      throw new BedeworkException(t);
    }
  }
}
