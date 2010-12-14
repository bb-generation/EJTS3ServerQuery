/*
 *  This file is part of EJTS3ServerQuery.
 *
 *  EJTS3ServerQuery is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  EJTS3ServerQuery is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with EJTS3ServerQuery.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


package at.bbgen.ejts3serverquery;

import java.util.HashMap;

/**
 * Implement this interface, if you want to receive notify events from Teamspeak 3 server.
 * @author Stefan Martens
 * @since 0.7
 * @see EJTS3ServerQuery#setTeamspeakActionListener(TeamspeakActionListener)
 * @see EJTS3ServerQuery#removeTeamspeakActionListener()
 */
public interface TeamspeakActionListener
{
	/**
	 * This function will be called, if the Teamspeak server sends an event notify.<br><br>
	 * Following event types can occur:<br>
	 * <code>notifycliententerview</code> - Client join server<br>
	 * <code>notifyclientleftview</code> - Client left server<br>
	 * <code>notifytextmessage</code> - Chat message received or sent<br>
	 * <code>notifyclientmoved</code> - Client was moved or switched channel
	 * @param eventType The type of the event, for a small list, see above.
	 * @param eventInfo A HashMap which contains all keys of the event
	 */
	public void teamspeakActionPerformed(String eventType, HashMap<String, String> eventInfo);
}
