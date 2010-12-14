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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * EJTS3ServerQuery library version 1.1
 * <br><br>
 * This library allows you to create a query connection to the Teamspeak 3 telnet interface.
 * Almost anything is supported: Query lists or just informations, get log entries, receiving events, add or delete complains, kick or move clients and of course send own commands.
 * <br><br>
 * 
 * EJTS3ServerQuery is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * <br>
 * 
 * <h3>Notes</h3>
 * This library was originally developed by Stefan Martens
 * (http://stefan1200.bplaced.net) and was then extended by Bernhard Eder
 * (bbots@bbgen.net).<br>
 * You can get the original source code of JTS3ServerQuery at
 * http://stefan1200.bplaced.net
 * <br><br>
 * 
 * <b>E-Mail:</b><br>github@bbgen.net<br><br>
 * <b>Homepage:</b><br>https://github.com/bb-generation/EJTS3ServerQuery
 * 
 * @author Bernhard Eder <bbots@bbgen.net>
 * @author Stefan Martens <info@stefan1200.de>
 * @version 1.1 (12.12.2010)
 */
public class EJTS3ServerQuery
{
	/**
	 * List mode for getList(), use this mode to get a list of clients currently online.<br><br>
	 * Possible optional arguments:<br>
	 * <code>-uid<br>-away<br>-voice<br>-times<br>-groups<br>-info<br>-icon<br>-country</code>
	 * @see EJTS3ServerQuery#getList(int)
	 * @see EJTS3ServerQuery#getList(int, String)
	 */
	public static final int LISTMODE_CLIENTLIST = 1;
	
	/**
	 * List mode for getList(), use this mode to get a list of current channels.<br><br>
	 * Possible optional arguments:<br>
	 * <code>-topic<br>-flags<br>-voice<br>-limits<br>-icon</code>
	 * @see EJTS3ServerQuery#getList(int)
	 * @see EJTS3ServerQuery#getList(int, String)
	 */
	public static final int LISTMODE_CHANNELLIST = 2;
	
	/**
	 * List mode for getList(), use this mode to get a list of virtual servers.<br><br>
	 * Possible optional arguments:<br>
	 * <code>-all<br>-onlyoffline<br>-short<br>-uid</code>
	 * @see EJTS3ServerQuery#getList(int)
	 * @see EJTS3ServerQuery#getList(int, String)
	 */
	public static final int LISTMODE_SERVERLIST = 3;
	
	/**
	 * List mode for getList(), use this mode to get a list of server groups.
	 * @see EJTS3ServerQuery#getList(int)
	 * @see EJTS3ServerQuery#getList(int, String)
	 */
	public static final int LISTMODE_SERVERGROUPLIST = 4;
	
	/**
	 * List mode for getList(), use this mode to get a list of all clients in database.
	 * By default only the first 25 entries will be returned.<br><br>
	 * Possible optional arguments:<br>
	 * <code>start=&lt;number&gt;<br>duration=&lt;number&gt;<br>-count</code>
	 * <br><br>
	 * For example:<br>
	 * <code>start=0<br>duration=25</code>
	 * <br><br>
	 * <b>Important:</b><br>
	 * Requesting to many clients at once make the TS3 server unstable.
	 * @see EJTS3ServerQuery#getList(int)
	 * @see EJTS3ServerQuery#getList(int, String)
	 */
	public static final int LISTMODE_CLIENTDBLIST = 5;
	
	/**
	 * List mode for getList(), use this mode to get a list of permissions.
	 * @see EJTS3ServerQuery#getList(int)
	 * @see EJTS3ServerQuery#getList(int, String)
	 */
	public static final int LISTMODE_PERMISSIONLIST = 6;
	
	/**
	 * List mode for getList(), use this mode to get a list of bans.
	 * @see EJTS3ServerQuery#getList(int)
	 * @see EJTS3ServerQuery#getList(int, String)
	 */
	public static final int LISTMODE_BANLIST = 7;
	
	/**
	 * List mode for getList(), use this mode to get a list of complains.
	 * Without arguments you get a list of all complains on the server.
	 * Use the optional argument to get complains of only one client.<br><br>
	 * Possible optional arguments:<br>
	 * <code>tcldbid=&lt;client database ID&gt;</code>
	 * <br><br>
	 * For example:<br>
	 * <code>tcldbid=2</code>
	 * @since 1.0
	 * @see EJTS3ServerQuery#getList(int)
	 * @see EJTS3ServerQuery#getList(int, String)
	 */
	public static final int LISTMODE_COMPLAINLIST = 8;
	
	/**
	 * Info mode for getInfo(), use this mode to get informations about the current selected server.
	 * @see EJTS3ServerQuery#getInfo(int, int)
	 */
	public static final int INFOMODE_SERVERINFO = 11;
	
	/**
	 * Info mode for getInfo(), use this mode to get informations about a channel.
	 * @see EJTS3ServerQuery#getInfo(int, int)
	 */
	public static final int INFOMODE_CHANNELINFO = 12;
	
	/**
	 * Info mode for getInfo(), use this mode to get informations about a client.
	 * @see EJTS3ServerQuery#getInfo(int, int)
	 */
	public static final int INFOMODE_CLIENTINFO = 13;
	
	/**
	 * Permission list mode for getPermissionList(), use this mode to get a list of channel permissions.
	 * @see EJTS3ServerQuery#getPermissionList(int, int)
	 */
	public static final int PERMLISTMODE_CHANNEL = 21;
	
	/**
	 * Permission list mode for getPermissionList(), use this mode to get a list of server group permissions.
	 * @see EJTS3ServerQuery#getPermissionList(int, int)
	 */
	public static final int PERMLISTMODE_SERVERGROUP = 22;
	
	/**
	 * Permission list mode for getPermissionList(), use this mode to get a list of client permissions.
	 * @see EJTS3ServerQuery#getPermissionList(int, int)
	 */
	public static final int PERMLISTMODE_CLIENT = 23;
	
	/**
	 * Text message target mode for sendTextMessage() to send a message to a single client.
	 * @see EJTS3ServerQuery#sendTextMessage(int, int, String)
	 */
	public static final int TEXTMESSAGE_TARGET_CLIENT = 1;

	/**
	 * Text message target mode for sendTextMessage() to send a message to a channel.
	 * @see EJTS3ServerQuery#sendTextMessage(int, int, String)
	 */
	public static final int TEXTMESSAGE_TARGET_CHANNEL = 2;
	
	/**
	 * Text message target mode for sendTextMessage() to send a message to a virtual server.
	 * @see EJTS3ServerQuery#sendTextMessage(int, int, String)
	 */
	public static final int TEXTMESSAGE_TARGET_VIRTUALSERVER = 3;
	
	/**
	 * Text message target mode for sendTextMessage() to send a message to all virtual servers.
	 * @see EJTS3ServerQuery#sendTextMessage(int, int, String)
	 */
	public static final int TEXTMESSAGE_TARGET_GLOBAL = 4;
	
	/**
	 * Event mode for addEventNotify() to add server chat events (like receiving or sending chat messages).
	 * @since 0.7
	 * @see EJTS3ServerQuery#addEventNotify(int, int)
	 */
	public static final int EVENT_MODE_TEXTSERVER = 1;
	
	/**
	 * Event mode for addEventNotify() to add channel chat events (like receiving or sending chat messages).
	 * @since 0.7
	 * @see EJTS3ServerQuery#addEventNotify(int, int)
	 */
	public static final int EVENT_MODE_TEXTCHANNEL = 2;
	
	/**
	 * Event mode for addEventNotify() to add private chat events (like receiving or sending chat messages).
	 * @since 0.7
	 * @see EJTS3ServerQuery#addEventNotify(int, int)
	 */
	public static final int EVENT_MODE_TEXTPRIVATE = 3;
	
	/**
	 * Event mode for addEventNotify() to add server events (like clients join or left the server).
	 * @since 0.7
	 * @see EJTS3ServerQuery#addEventNotify(int, int)
	 */
	public static final int EVENT_MODE_SERVER = 4;
	
	/**
	 * Event mode for addEventNotify() to add channel events (like clients join or left the channel).<br><br>
	 * <b>Notice:</b><br>
	 * This mode also need to set a channel ID.
	 * @since 0.7
	 * @see EJTS3ServerQuery#addEventNotify(int, int)
	 */
	public static final int EVENT_MODE_CHANNEL = 5;
	
	private boolean DEBUG = false;
	private boolean eventNotifyCheckActive = false;
	private TeamspeakActionListener actionClass = null;
	private int queryCurrentClientID = -1;
	private int queryCurrentServerID = -1;
	private int queryCurrentChannelID = -1;
	private String queryCurrentChannelPassword = null;
	
	private Socket socketQuery = null;
	private BufferedReader in = null;
	private PrintStream out = null;
	private Timer eventNotifyTimer = null;
	private TimerTask eventNotifyTimerTask = null;
	
	private void eventNotifyRun()
	{
		if (eventNotifyCheckActive && isConnected())
		{
			try
			{
				if (in.ready())
				{
					String inputLine = in.readLine();
					if (inputLine.length() > 0)
					{
						handleAction(inputLine);
					}
				}
			}
			catch (Exception ex)
			{
			}
		}
	}
	
	/**
	 * Set a class that should receive the Teamspeak events. This class must implement the TeamspeakActionListener interface.
	 * @param listenerClass - A class that implements the TeamspeakActionListener interface.
	 * @since 0.7
	 * @see TeamspeakActionListener
	 */
	public void setTeamspeakActionListener(TeamspeakActionListener listenerClass)
	{
		this.actionClass = listenerClass;
	}
	
	/**
	 * Remove the class from receiving Teamspeak events. This function also call removeAllEvents(), if needed.
	 * @since 0.7
	 * 
	 * @throws EJTS3ServerQueryException
	 */
	public void removeTeamspeakActionListener() throws EJTS3ServerQueryException
	{
		if (eventNotifyTimerTask != null)
		{
			removeAllEvents();
		}
		this.actionClass = null;
	}
	
	/**
	 * Activate a Teamspeak event notify.<br><br>
	 * <b>Notice:</b><br>
	 * You have to use setTeamspeakActionListener() first!
	 * @param eventMode Use an EVENT_MODE constant
	 * @param channelID A channel ID, only needed for EVENT_MODE_CHANNEL. Use any number for any other EVENT_MODE.
	 * @since 0.7
	 * @see EJTS3ServerQuery#EVENT_MODE_CHANNEL
	 * @see EJTS3ServerQuery#EVENT_MODE_SERVER
	 * @see EJTS3ServerQuery#EVENT_MODE_TEXTCHANNEL
	 * @see EJTS3ServerQuery#EVENT_MODE_TEXTPRIVATE
	 * @see EJTS3ServerQuery#EVENT_MODE_TEXTSERVER
	 * 
	 * @throws EJTS3ServerQueryException
	 * 
	 */
	public void addEventNotify(int eventMode, int channelID) throws EJTS3ServerQueryException
	{
		if (actionClass == null)
			throw new EJTS3ServerQueryException("Use setTeamspeakActionListener() first!");
		
		if (!isConnected())
			throw new EJTS3ServerQueryException("addEventNotify(): Not connected to TS3 server!");
		
		String command = null;
		
		if (eventMode == EVENT_MODE_SERVER)
		{
			command = "servernotifyregister event=server";
		}
		if (eventMode == EVENT_MODE_CHANNEL)
		{
			command = "servernotifyregister id=" + Integer.toString(channelID) + " event=channel";
		}
		if (eventMode == EVENT_MODE_TEXTSERVER)
		{
			command = "servernotifyregister event=textserver";
		}
		if (eventMode == EVENT_MODE_TEXTCHANNEL)
		{
			command = "servernotifyregister event=textchannel";
		}
		if (eventMode == EVENT_MODE_TEXTPRIVATE)
		{
			command = "servernotifyregister event=textprivate";
		}
		
		if (command == null)
			throw new EJTS3ServerQueryException("addEventNotify(): Invalid eventMode given!");
		
		HashMap<String, String> hmIn;
		try
		{
			hmIn = doInternalCommand(command);
			if (!hmIn.get("id").equals("0"))
				throw new EJTS3ServerQueryException(getErrorString("addEventNotify()", hmIn.get("id"), hmIn.get("msg"), hmIn.get("extra_msg"), hmIn.get("failed_permid")));
		}
		catch (Exception e)
		{
			if (DEBUG) e.printStackTrace();
			throw new EJTS3ServerQueryException("Exception addEventNotify(): " + e.toString());
		}
		
		if (eventNotifyTimerTask == null)
		{
			eventNotifyTimerTask = new TimerTask()
			{
				public void run()
				{
					eventNotifyRun();
				}
			};
			eventNotifyTimer.schedule(eventNotifyTimerTask, 200, 200);
		}
	}
	
	/**
	 * Removes all activated events.
	 * @since 0.7
	 * 
	 * @throws EJTS3ServerQueryException
	 */
	public void removeAllEvents() throws EJTS3ServerQueryException
	{
		if (!isConnected())
			throw new EJTS3ServerQueryException("removeAllEvents(): Not connected to TS3 server!");
		
		String command = "servernotifyunregister";
		
		HashMap<String, String> hmIn;
		try
		{
			hmIn = doInternalCommand(command);
			if (!hmIn.get("id").equals("0"))
				throw new EJTS3ServerQueryException(getErrorString("removeAllEvents()", hmIn.get("id"), hmIn.get("msg"), hmIn.get("extra_msg"), hmIn.get("failed_permid")));
		}
		catch (Exception e)
		{
			if (DEBUG) e.printStackTrace();
			throw new EJTS3ServerQueryException("Exception removeAllEvents(): " + e.toString());
		}
		
		if (eventNotifyTimerTask != null)
		{
			eventNotifyTimerTask.cancel();
			eventNotifyTimerTask = null;
		}
	}
	
	/**
	 * Open a query connection to the TS3 server. 
	 * @param ip IP or Host address to the TS3 server
	 * @param queryport Query Port of the TS3 server
	 * 
	 * @throws EJTS3ServerQueryException
	 */
	public void connectTS3Query(String ip, int queryport) throws EJTS3ServerQueryException
	{
		if (socketQuery != null)
			throw new EJTS3ServerQueryException("connectTS3Query(): Close connection first!");
		
		try
		{
			socketQuery = new Socket(ip, queryport);  // Open socket connection to TS3 telnet port
		}
		catch (Exception e)
		{
			if (DEBUG) e.printStackTrace();
			socketQuery = null;
			throw new EJTS3ServerQueryException("Exception connectTS3Query(): " + e.toString());
		}
		

		if (socketQuery.isConnected())
		{
			try
			{
				in = new BufferedReader(new InputStreamReader(socketQuery.getInputStream(), "UTF-8"));
				out = new PrintStream(socketQuery.getOutputStream(), true, "UTF-8");
				
				String serverIdent = in.readLine();
				if (!serverIdent.equals("TS3"))
				{
					closeTS3Connection();
					throw new EJTS3ServerQueryException("connectTS3Query(): Server does not respond as TS3 server!");
				}
				
				socketQuery.setSoTimeout(500);  // Set the timeout for reading all useless lines after connecting
				
				try
				{
					while (true)
					{
						in.readLine(); // Catch useless lines after connecting
					}
				}
				catch (Exception e)
				{
				}
				
				socketQuery.setSoTimeout(10000);  // Set shorter timeout than default
			}
			catch (Exception e)
			{
				if (DEBUG) e.printStackTrace();
				closeTS3Connection();
				throw new EJTS3ServerQueryException("Exception connectTS3Query(): " + e.toString());
			}
		}
		else
		{
			throw new EJTS3ServerQueryException("Could not open a query connection.");
		}
		
		if (eventNotifyTimer != null)
		{
			eventNotifyTimer.cancel();
			eventNotifyTimer = null;
		}
		if (eventNotifyTimerTask != null)
		{
			eventNotifyTimerTask.cancel();
			eventNotifyTimerTask = null;
		}
		eventNotifyTimer = new Timer(true);
	}
	
	/**
	 * Login with an account.
	 * @param loginname Login name
	 * @param password Login password
	 * @since 0.8
	 * 
	 * @throws EJTS3ServerQueryException
	 */
	public void loginTS3(String loginname, String password) throws EJTS3ServerQueryException
	{
		loginTS3(loginname, password, null);
	}
	
	/**
	 * Login with an account. Optional you can change the display name for this query connection.
	 * @deprecated This method also returns false, if login was successful, but the display name is already in use. Use setDisplayName() after login instead.
	 * @param loginname Login name
	 * @param password Login password
	 * @param displayName Use this to set another display name for this server query connection, at least 3 characters needed. Use <code>null</code> if not wanted.
	 * @see EJTS3ServerQuery#setDisplayName(String)
	 * @see EJTS3ServerQuery#loginTS3(String, String)
	 * 
	 * @throws EJTS3ServerQueryException
	 */
	public void loginTS3(String loginname, String password, String displayName) throws EJTS3ServerQueryException
	{
		if (!isConnected())
			throw new EJTS3ServerQueryException("loginTS3(): Not connected to TS3 server!");
		
		HashMap<String, String> hmIn;
		try
		{
			hmIn = doInternalCommand("login " + encodeTS3String(loginname) + " " + encodeTS3String(password));
			if (hmIn == null)
				throw new EJTS3ServerQueryException("Unable to login.");
			else if (!hmIn.get("id").equals("0"))
				throw new EJTS3ServerQueryException(getErrorString("loginTS3()", hmIn.get("id"), hmIn.get("msg"), hmIn.get("extra_msg"), hmIn.get("failed_permid")));
			
			updateClientIDChannelID();
			
			if (displayName != null && displayName.length() >= 3)
			{
				setDisplayName(displayName);
			}
		}
		catch (Exception e)
		{
			if (DEBUG) e.printStackTrace();
			throw new EJTS3ServerQueryException("Exception loginTS3(): " + e.toString());
		}
		
	}
	
	/**
	 * Change the display name on the Teamspeak 3 server of this query connection. This name will be displayed on many actions like kickClient(), moveClient(), pokeClient() and sendTextMessage().
	 * @param displayName A String with the new display name of this connection.
	 * @since 0.8
	 * 
	 * @throws EJTS3ServerQueryException
	 */
	public void setDisplayName(String displayName) throws EJTS3ServerQueryException
	{
		if (!isConnected())
			throw new EJTS3ServerQueryException("setDisplayName(): Not connected to TS3 server!");
		
		if (displayName == null || displayName.length() < 3)
			throw new EJTS3ServerQueryException("setDisplayName(): displayName null or shorter than 3 characters!");
		
		HashMap<String, String> hmIn;
		try
		{
			hmIn = doInternalCommand("clientupdate client_nickname=" + encodeTS3String(displayName));
			if (hmIn == null)
			{
				throw new EJTS3ServerQueryException("Unable to set display name.");
			}
			else if (!hmIn.get("id").equals("0"))
				throw new EJTS3ServerQueryException(getErrorString("setDisplayName()", hmIn.get("id"), hmIn.get("msg"), hmIn.get("extra_msg"), hmIn.get("failed_permid")));
		}
		catch (Exception e)
		{
			if (DEBUG) e.printStackTrace();
			throw new EJTS3ServerQueryException("Exception setDisplayName(): " + e.toString());
		}
		
	}

	/**
	 * Select a virtual server to work with.
	 * @param serverID A virtual server id
	 * 
	 * @throws EJTS3ServerQueryException
	 */
	public void selectVirtualServer(int serverID) throws EJTS3ServerQueryException
	{
		selectVirtualServer(serverID, false);
	}
	
	/**
	 * Select a virtual server to work with. This method allows to select the virtual server by id or port.
	 * @param server A virtual server id or port
	 * @param selectPort <code>true</code> if <code>server</code> is the virtual server port, <code>false</code> if <code>server</code> is the virtual server id.
	 * @since 0.9
	 * 
	 * @throws EJTS3ServerQueryException
	 */
	public void selectVirtualServer(int server, boolean selectPort) throws EJTS3ServerQueryException
	{
		if (!isConnected())
			throw new EJTS3ServerQueryException("selectVirtualServer(): Not connected to TS3 server!");
		
		HashMap<String, String> hmIn;
		try
		{
			String command;
			if (selectPort)
			{
				command = "use port=" + Integer.toString(server);
			}
			else
			{
				command = "use " + Integer.toString(server);
			}
						
			hmIn = doInternalCommand(command);
			if (!hmIn.get("id").equals("0"))
				throw new EJTS3ServerQueryException(getErrorString("selectVirtualServer()", hmIn.get("id"), hmIn.get("msg"), hmIn.get("extra_msg"), hmIn.get("failed_permid")));
		}
		catch (Exception e)
		{
			if (DEBUG) e.printStackTrace();
			throw new EJTS3ServerQueryException("Exception selectVirtualServer(): " + e.toString());
		}
		
		updateClientIDChannelID();
	}
	
	private void updateClientIDChannelID() throws EJTS3ServerQueryException
	{
		HashMap<String, String> hmIn;
		try
		{
			hmIn = doInternalCommand("whoami");
			if (!hmIn.get("id").equals("0"))
				throw new EJTS3ServerQueryException(getErrorString("updateClientIDChannelID()", hmIn.get("id"), hmIn.get("msg"), hmIn.get("extra_msg"), hmIn.get("failed_permid")));
			
			HashMap<String, String> response = parseLine(hmIn.get("response"));
			queryCurrentServerID = Integer.parseInt(response.get("virtualserver_id"));
			queryCurrentClientID = Integer.parseInt(response.get("client_id"));
			queryCurrentChannelID = Integer.parseInt(response.get("client_channel_id"));
			queryCurrentChannelPassword = null;
		}
		catch (Exception e)
		{
			if (DEBUG) e.printStackTrace();
			throw new EJTS3ServerQueryException("Exception updateClientIDChannelID(): " + e.toString());
		}
		
	}
	
	/**
	 * Close the query connection.
	 * 
	 * @throws EJTS3ServerQueryException
	 */
	public void closeTS3Connection() throws EJTS3ServerQueryException
	{

		if (eventNotifyTimerTask != null)
		{
			eventNotifyTimerTask.cancel();
			eventNotifyTimerTask = null;
			eventNotifyTimer.cancel();
			eventNotifyTimer = null;
		}
		
		queryCurrentClientID = -1;
		queryCurrentServerID = -1;
		queryCurrentChannelPassword = null;
		
		String exceptionString = "";
		
		try
		{
			if (out != null)
			{
				out.println("quit");
				out.close();
				out = null;
			}
		}
		catch (Exception e)
		{
			if (DEBUG) e.printStackTrace();
			exceptionString += "Exception closeTS3Connection(): " + e.toString();
		}
		
		try
		{
			if (in != null)
			{
				in.close();
				in = null;
			}
		}
		catch (Exception e)
		{
			if (DEBUG) e.printStackTrace();
			exceptionString += "Exception closeTS3Connection(): " + e.toString();
		}
		
		try
		{
			if (socketQuery != null)
			{
				socketQuery.close();
				socketQuery = null;
			}
		}
		catch (Exception e)
		{
			if (DEBUG) e.printStackTrace();
			exceptionString += "Exception closeTS3Connection(): " + e.toString();
		}
		
		if(!exceptionString.isEmpty())
			throw new EJTS3ServerQueryException(exceptionString);
	}
	
	
	private String getErrorString(String apiMethodName, String lastErrorID, String message, String extMessage, String failedID)
	{
		int failedId = -1;
		try
		{
			if(failedID != null)
				failedId = Integer.parseInt(failedID);
		} catch(Exception e)
		{	}
		
		
		return getErrorString(apiMethodName, Integer.parseInt(lastErrorID), message, extMessage, failedId);
	}
	
	private String getErrorString(String apiMethodName, int lastErrorID, String message, String extMessage, int failedID)
	{
		return "ServerQuery Error " + Integer.toString(lastErrorID) + " @ " + apiMethodName + ": " + message + (extMessage != null ? " - " + extMessage : "") + (failedID != -1 ? " - Permission ID: " + failedID : "");
	}

	
	/**
	 * Delete a channel of the server.
	 * @param channelID The Channel ID to be deleted
	 * @param forceDelete <code>true</code> for a force channel delete (kicks also clients out of it), <code>false</code> to delete only an empty channel
	 * 
	 * @throws EJTS3ServerQueryException
	 */
	public void deleteChannel(int channelID, boolean forceDelete) throws EJTS3ServerQueryException
	{
		if (!isConnected())
			throw new EJTS3ServerQueryException("deleteChannel(): Not connected to TS3 server!");
		
		HashMap<String, String> hmIn;
		try
		{
			String command = "channeldelete cid=" + Integer.toString(channelID) + " force=" + (forceDelete ? "1" : "0");
			
			hmIn = doInternalCommand(command);
			if (!hmIn.get("id").equals("0"))
				throw new EJTS3ServerQueryException(getErrorString("deleteChannel()", hmIn.get("id"), hmIn.get("msg"), hmIn.get("extra_msg"), hmIn.get("failed_permid")));
			
			if (queryCurrentChannelID == channelID)
			{
				updateClientIDChannelID();
			}
		}
		catch (Exception e)
		{
			if (DEBUG) e.printStackTrace();
			throw new EJTS3ServerQueryException("Exception deleteChannel(): " + e.toString());
		}
		
	}
	
	/**
	 * Move a client into another channel.
	 * @param clientID Current Client ID
	 * @param channelID Target Channel ID
	 * @param channelPassword Password of the target channel or <code>null</code> if no password needed
	 * 
	 * @throws EJTS3ServerQueryException
	 */
	public void moveClient(int clientID, int channelID, String channelPassword) throws EJTS3ServerQueryException
	{
		if (!isConnected())
			throw new EJTS3ServerQueryException("moveClient(): Not connected to TS3 server!");
		
		HashMap<String, String> hmIn;
		try
		{
			String command = "clientmove clid=" + Integer.toString(clientID) + " cid=" + Integer.toString(channelID);
			
			if (channelPassword != null && channelPassword.length() > 0)
			{
				command += " cpw=" + encodeTS3String(channelPassword);
			}
			
			hmIn = doInternalCommand(command);
			if (!hmIn.get("id").equals("0"))
				throw new EJTS3ServerQueryException(getErrorString("moveClient()", hmIn.get("id"), hmIn.get("msg"), hmIn.get("extra_msg"), hmIn.get("failed_permid")));
			
			if (clientID == queryCurrentClientID)
			{
				queryCurrentChannelID = channelID;
				queryCurrentChannelPassword = channelPassword;
			}
		}
		catch (Exception e)
		{
			if (DEBUG) e.printStackTrace();
			throw new EJTS3ServerQueryException("Exception moveClient(): " + e.toString());
		}
		
	}
	
	/**
	 * Move a client into another channel.
	 * @param clientIDs List of client IDs to be moved
	 * @param channelID Target Channel ID
	 * @param channelPassword Password of the target channel or <code>null</code> if no password needed
	 * 
	 * @throws EJTS3ServerQueryException
	 */
	public void moveClientList(List<Integer> clientIDs, int channelID, String channelPassword) throws EJTS3ServerQueryException
	{
		if (!isConnected())
			throw new EJTS3ServerQueryException("moveClient(): Not connected to TS3 server!");
		
		HashMap<String, String> hmIn;
		try
		{
			StringBuffer command = new StringBuffer("clientmove ");
			int clNr = 0;
			for(int clientID : clientIDs)
			{
				if(clNr++ > 0)
				{
					command.append("|");
				}
				command.append("clid=" + Integer.toString(clientID));
			}
			command.append(" cid=" + Integer.toString(channelID));
			
			if (channelPassword != null && channelPassword.length() > 0)
			{
				command.append(" cpw=" + encodeTS3String(channelPassword));
			}
			hmIn = doInternalCommand(command.toString());
			if (!hmIn.get("id").equals("0"))
				throw new EJTS3ServerQueryException(getErrorString("moveClient()", hmIn.get("id"), hmIn.get("msg"), hmIn.get("extra_msg"), hmIn.get("failed_permid")));
			
			if (clientIDs.contains(queryCurrentClientID))
			{
				queryCurrentChannelID = channelID;
				queryCurrentChannelPassword = channelPassword;
			}
		}
		catch (Exception e)
		{
			if (DEBUG) e.printStackTrace();
			throw new EJTS3ServerQueryException("Exception moveClient(): " + e.toString());
		}
		
	}
	
	/**
	 * Kick a client from channel or from server.
	 * @param cientID The Client ID to be kicked
	 * @param onlyChannelKick <code>true</code> for a channel kick, <code>false</code> for a server kick
	 * @param kickReason The kick reason
	 * 
	 * @throws EJTS3ServerQueryException
	 */
	public void kickClient(int cientID, boolean onlyChannelKick, String kickReason) throws EJTS3ServerQueryException
	{
		if (!isConnected())
			throw new EJTS3ServerQueryException("kickClient(): Not connected to TS3 server!");
		
		HashMap<String, String> hmIn;
		try
		{
			String command = "clientkick clid=" + Integer.toString(cientID) + " reasonid=" + (onlyChannelKick ? "4" : "5");
			
			if (kickReason != null && kickReason.length() > 0)
			{
				command += " reasonmsg=" + encodeTS3String(kickReason);
			}
			
			hmIn = doInternalCommand(command);
			if (!hmIn.get("id").equals("0"))
				throw new EJTS3ServerQueryException(getErrorString("kickClient()", hmIn.get("id"), hmIn.get("msg"), hmIn.get("extra_msg"), hmIn.get("failed_permid")));
		}
		catch (Exception e)
		{
			if (DEBUG) e.printStackTrace();
			throw new EJTS3ServerQueryException("Exception kickClient(): " + e.toString());
		}
		
	}
	
	/**
	 * Returns the current client ID of the query connection. You need this maybe to move the client or something else.
	 * @return The client ID or -1 if unknown.
	 * @since 0.6
	 */
	public int getCurrentQueryClientID()
	{
		return queryCurrentClientID;
	}
	
	/**
	 * Returns the current virtual server ID of the query connection.
	 * @return The virtual server ID or -1 if unknown.
	 * @since 0.6
	 */
	public int getCurrentQueryClientServerID()
	{
		return queryCurrentServerID;
	}
	
	/**
	 * Returns the current channel ID of the query client.
	 * @return The channel ID or -1 if unknown.
	 * @since 0.6
	 */
	public int getCurrentQueryClientChannelID()
	{
		return queryCurrentChannelID;
	}
	
	/**
	 * Sends a text message to a client / channel / virtual server / global (all virtual servers).<br><br>
	 * <b>Notice:</b><br>
	 * If you use a channel or virtual server id, which is not currently used by this connection, sendTextMessage() work as follow:<br>
	 * Switch to the channel or virtual server, sends the text message and switch back to old channel or virtual server.<br><br>
	 * If you want to send more messages to this channel or virtual server, just use selectVirtualServer() or moveClient() first.
	 * @param targetID The client, channel or virtual server id. Use any number for a global message.
	 * @param targetMode A text message target mode constant
	 * @param msg The message to be send
	 * @see EJTS3ServerQuery#TEXTMESSAGE_TARGET_CLIENT
	 * @see EJTS3ServerQuery#TEXTMESSAGE_TARGET_CHANNEL
	 * @see EJTS3ServerQuery#TEXTMESSAGE_TARGET_VIRTUALSERVER
	 * @see EJTS3ServerQuery#TEXTMESSAGE_TARGET_GLOBAL
	 * @see EJTS3ServerQuery#moveClient(int, int, String)
	 * @see EJTS3ServerQuery#selectVirtualServer(int)
	 * 
	 * @throws EJTS3ServerQueryException
	 */
	public void sendTextMessage(int targetID, int targetMode, String msg) throws EJTS3ServerQueryException
	{
		sendTextMessage(targetID, targetMode, msg, null);
	}
	
	/**
	 * Sends a text message to a client / channel / virtual server / global (all virtual servers).<br><br>
	 * <b>Notice:</b><br>
	 * If you use a channel or virtual server id, which is not currently used by this connection, sendTextMessage() work as follow:<br>
	 * Switch to the channel or virtual server, sends the text message and switch back to old channel or virtual server.<br><br>
	 * If you want to send more messages to this channel or virtual server, just use selectVirtualServer() or moveClient() first.
	 * @param targetID The client, channel or virtual server id. Use any number for a global message.
	 * @param targetMode A text message target mode constant
	 * @param msg The message to be send
	 * @param channelPassword Channel password, is only needed for a text message to channel. Use <code>null</code> if channel has no password or not a channel text message.
	 * @since 0.6
	 * @see EJTS3ServerQuery#TEXTMESSAGE_TARGET_CLIENT
	 * @see EJTS3ServerQuery#TEXTMESSAGE_TARGET_CHANNEL
	 * @see EJTS3ServerQuery#TEXTMESSAGE_TARGET_VIRTUALSERVER
	 * @see EJTS3ServerQuery#TEXTMESSAGE_TARGET_GLOBAL
	 * @see EJTS3ServerQuery#moveClient(int, int, String)
	 * @see EJTS3ServerQuery#selectVirtualServer(int)
	 * 
	 * @throws EJTS3ServerQueryException
	 */
	public void sendTextMessage(int targetID, int targetMode, String msg, String channelPassword) throws EJTS3ServerQueryException
	{
		if (!isConnected())
			throw new EJTS3ServerQueryException("sendTextMessage(): Not connected to TS3 server!");
		
		if (msg == null || msg.length() == 0)
			throw new EJTS3ServerQueryException("sendTextMessage(): No message given!");
		
		if (targetMode < TEXTMESSAGE_TARGET_CLIENT || targetMode > TEXTMESSAGE_TARGET_GLOBAL)
			throw new EJTS3ServerQueryException("sendTextMessage(): Invalid targetMode given!");
		
		HashMap<String, String> hmIn = null;
		try
		{
			String command = null;
			if (targetMode == TEXTMESSAGE_TARGET_GLOBAL)
			{
				command = "gm msg=" + encodeTS3String(msg);
				
				hmIn = doInternalCommand(command);
			}
			else if (targetMode == TEXTMESSAGE_TARGET_CHANNEL)
			{
				int oldChannel = -1;
				String oldChannelPassword = null;
				if (targetID != queryCurrentChannelID)
				{
					oldChannel = queryCurrentChannelID;
					oldChannelPassword = queryCurrentChannelPassword;
					moveClient(queryCurrentClientID, targetID, channelPassword);
				}
				
				command = "sendtextmessage targetmode=" + Integer.toString(targetMode) + " msg=" + encodeTS3String(msg);
				
				hmIn = doInternalCommand(command);
				
				if (oldChannel != -1)
				{
					moveClient(queryCurrentClientID, oldChannel, oldChannelPassword);
				}
			}
			else if (targetMode == TEXTMESSAGE_TARGET_CLIENT)
			{
				command = "sendtextmessage targetmode=" + Integer.toString(targetMode) + " msg=" + encodeTS3String(msg) + " target=" + Integer.toString(targetID);
				
				hmIn = doInternalCommand(command);
			}
			else if (targetMode == TEXTMESSAGE_TARGET_VIRTUALSERVER)
			{
				int oldServer = -1;
				if (targetID != queryCurrentServerID)
				{
					oldServer = queryCurrentServerID;
					selectVirtualServer(targetID);
				}
				
				command = "sendtextmessage targetmode=" + Integer.toString(targetMode) + " msg=" + encodeTS3String(msg);
				
				hmIn = doInternalCommand(command);
				
				if (oldServer != -1)
				{
					selectVirtualServer(oldServer);
				}
			}
			
			if (!hmIn.get("id").equals("0"))
				throw new EJTS3ServerQueryException(getErrorString("sendTextMessage()", hmIn.get("id"), hmIn.get("msg"), hmIn.get("extra_msg"), hmIn.get("failed_permid")));
		}
		catch (Exception e)
		{
			if (DEBUG) e.printStackTrace();
			throw new EJTS3ServerQueryException("Exception sendTextMessage(): " + e.toString());
		}
		
	}
	
	/**
	 * Send a single command to the TS3 server and read the response.<br><br>
	 * <b>Notice:</b><br>
	 * You can use parseRawData() to get the response String in a HashMap.<br>
	 * The returned HashMap can also contain a library error id and error message, if the connection to the Teamspeak 3 Server got lost while reading the response.<br><br>
	 * <b>Important:</b><br>
	 * Do not use the following commands here:<br>
	 * <code>channeldelete</code><br>
	 * <code>clientmove</code><br>
	 * <code>use</code><br>
	 * Please use deleteChannel(), moveClient() or selectVirtualServer() instead!
	 * @param command Any TS3 telnet command, see TS3 documentation or use the <code>help</code> command.
	 * @return An HashMap with 3 keys: <code>id</code> (error id), <code>msg</code> (error message) and <code>response</code> (unformatted server response).
	 * @see EJTS3ServerQuery#moveClient(int, int, String)
	 * @see EJTS3ServerQuery#selectVirtualServer(int)
	 * 
	 * @throws EJTS3ServerQueryException
	 */
	public HashMap<String, String> doCommand(String command) throws EJTS3ServerQueryException
	{
		if (command.startsWith("use ") || command.startsWith("clientmove ") || command.startsWith("channeldelete "))
			throw new EJTS3ServerQueryException("doCommand(): This commands are not allowed here. Please use deleteChannel(), moveClient() or selectVirtualServer()!");
		
		return doInternalCommand(command);
	}
	
	private synchronized HashMap<String, String> doInternalCommand(String command) throws EJTS3ServerQueryException
	{
		if (!isConnected())
			throw new EJTS3ServerQueryException("doCommand(): Not connected to TS3 server!");
		
		if (command == null || command.length() == 0)
			throw new EJTS3ServerQueryException("doCommand(): No command given!");
		
		eventNotifyCheckActive = false;
		
		if (DEBUG)
		{
			System.out.println("Send command:");
			System.out.println(command);
			System.out.println();
		}
		out.println(command);
		return readIncoming();
	}
	
	/**
	 * Poke a client. This opens a message dialog at the selected Teamspeak 3 client with the given message.
	 * @param clientID The client ID, which should get the message.
	 * @param msg The message for the message dialog.
	 * @throws EJTS3ServerQueryException 
	 * @since 0.4
	 * 
	 * @throws EJTS3ServerQueryException
	 */
	public void pokeClient(int clientID, String msg) throws EJTS3ServerQueryException
	{
		if (!isConnected())
			throw new EJTS3ServerQueryException("pokeClient(): Not connected to TS3 server!");
		
		if (msg == null || msg.length() == 0)
			throw new EJTS3ServerQueryException("pokeClient(): No message given!");
		
		String command = "clientpoke clid=" + Integer.toString(clientID) + " msg=" + encodeTS3String(msg);
		HashMap<String, String> hmIn = doInternalCommand(command);
		
		if (hmIn == null)
			throw new EJTS3ServerQueryException("Unable to poke client.");

		else if (!hmIn.get("id").equals("0"))
			throw new EJTS3ServerQueryException(getErrorString("pokeClient()", hmIn.get("id"), hmIn.get("msg"), hmIn.get("extra_msg"), hmIn.get("failed_permid")));
		
	}
	
	/**
	 * Add a complain to a client.
	 * @param clientDBID The client database ID, which should get the complain.
	 * @param msg The message of the complain.
	 * @since 1.0
	 * @throws EJTS3ServerQueryException
	 */
	public void complainAdd(int clientDBID, String msg) throws EJTS3ServerQueryException
	{
		if (!isConnected())
			throw new EJTS3ServerQueryException("complainAdd(): Not connected to TS3 server!");
		
		if (msg == null || msg.length() == 0)
			throw new EJTS3ServerQueryException("complainAdd(): No message given!");
		
		String command = "complainadd tcldbid=" + Integer.toString(clientDBID) + " message=" + encodeTS3String(msg);
		HashMap<String, String> hmIn = doInternalCommand(command);
		
		if (hmIn == null)
			throw new EJTS3ServerQueryException("Unable to add complain");
		else if (!hmIn.get("id").equals("0"))
			throw new EJTS3ServerQueryException(getErrorString("complainAdd()", hmIn.get("id"), hmIn.get("msg"), hmIn.get("extra_msg"), hmIn.get("failed_permid")));
		
	}
	
	/**
	 * Deletes complains from a client (from a specified sender).
	 * @param clientDBID The client database ID, which should get a complain removed.
	 * @param deleteClientDBID Delete complains submitted from this client database ID.
	 * @throws EJTS3ServerQueryException 
	 * @since 1.0
	 */
	public void complainDelete(int clientDBID, int deleteClientDBID) throws EJTS3ServerQueryException
	{
		if (!isConnected())
			throw new EJTS3ServerQueryException("complainDelete(): Not connected to TS3 server!");
		
		String command = "complaindel tcldbid=" + Integer.toString(clientDBID) + " fcldbid=" + Integer.toString(deleteClientDBID);
		HashMap<String, String> hmIn = doInternalCommand(command);
		
		if (hmIn == null)
			throw new EJTS3ServerQueryException("Unable to delete complain.");
		else if (!hmIn.get("id").equals("0"))
			throw new EJTS3ServerQueryException(getErrorString("complainDelete()", hmIn.get("id"), hmIn.get("msg"), hmIn.get("extra_msg"), hmIn.get("failed_permid")));
		
	}
	
	/**
	 * Check if connected to the TS3 server.
	 * @return <code>true</code> if connected, <code>false</code> if not.
	 */
	public boolean isConnected()
	{
		if (socketQuery == null || in == null || out == null)
		{
			return false;
		}
		
		return socketQuery.isConnected();
	}
	
	/**
	 * Parse unformatted response from TS3 server, like from the doCommand method.<br><br>
	 * <b>Notice:</b><br>
	 * Don't use this for help messages, since they are already formatted by the TS3 server.
	 * @param rawData The unformatted TS3 server response
	 * @return A Vector which contains a HashMap for each entry with the keys given by the TS3 Server. Returns <code>null</code> if no rawData given.
	 */
	public Vector<HashMap<String, String>> parseRawData(String rawData)
	{
		if (rawData == null)
		{
			return null;
		}
		
		Vector<HashMap<String, String>> formattedData = new Vector<HashMap<String, String>>();
		
		StringTokenizer stEntries = new StringTokenizer(rawData, "|", false);
		while(stEntries.hasMoreTokens())
		{
			formattedData.addElement(parseLine(stEntries.nextToken()));
		}
		
		return formattedData;
	}
	
	/**
	 * Get Informations about a server, channel or client.<br><br>
	 * <b>Notice:</b><br>
	 * If you want server informations, the server will return informations only about the current selected virtual server. To get informations about another virtual server, just select first.
	 * @param infoMode An INFOMODE constant.
	 * @param objectID A channel or client ID, use any number for server informations.
	 * @return A HashMap with the informations as key / value pairs like in the TS3 server response.
	 * @throws EJTS3ServerQueryException 
	 * @see EJTS3ServerQuery#INFOMODE_CHANNELINFO
	 * @see EJTS3ServerQuery#INFOMODE_CLIENTINFO
	 * @see EJTS3ServerQuery#INFOMODE_SERVERINFO
	 * @see EJTS3ServerQuery#selectVirtualServer(int)
	 */
	public HashMap<String, String> getInfo(int infoMode, int objectID) throws EJTS3ServerQueryException
	{
		String command = getCommand(infoMode, 2);
		
		if (command == null)
			throw new EJTS3ServerQueryException("getInfo(): Unknown infoMode!");
		
		if (infoMode != INFOMODE_SERVERINFO)
		{
			command += Integer.toString(objectID);
		}
		
		HashMap<String, String> hmIn = doInternalCommand(command);
		
		if (hmIn == null)
		{
			return null;
		}
		else if (!hmIn.get("id").equals("0"))
			throw new EJTS3ServerQueryException(getErrorString("getInfo()", hmIn.get("id"), hmIn.get("msg"), hmIn.get("extra_msg"), hmIn.get("failed_permid")));
		else if (hmIn.get("response") == null)
			throw new EJTS3ServerQueryException("getInfo(): No valid server response found!");
		
		HashMap<String, String> info = parseLine(hmIn.get("response"));
		
		return info;
	}
	
	/**
	 * Get informations about a permission ID.<br><br>
	 * If the permission ID was found, the HashMap will contain the following keys:<br>
	 * <code>permid</code> with the permission ID<br>
	 * <code>permname</code> with the permission name<br>
	 * <code>permdesc</code> with the permission description (may be empty, if not exist)
	 * @param permID A permission ID
	 * @return A HashMap with the information about the permission ID.
	 * @throws EJTS3ServerQueryException 
	 */
	public HashMap<String, String> getPermissionInfo(int permID) throws EJTS3ServerQueryException
	{
		Vector<HashMap<String, String>> permList = getList(LISTMODE_PERMISSIONLIST);
		
		if (permList == null)
		{
			return null;
		}
		
		HashMap<String, String> retPermInfo = null;
		
		try
		{
			for (HashMap<String, String> permInfo : permList)
			{
				if (Integer.parseInt(permInfo.get("permid")) == permID)
				{
					retPermInfo = permInfo;
					break;
				}
			}
		}
		catch (Exception e)
		{
			throw new EJTS3ServerQueryException("getPermissionInfo(): Error while searching permission ID: " + e.toString());
		}
		
		return retPermInfo;
	}
	
	/**
	 * Get a list of permissions of a server group / channel / client.
	 * @param permListMode A PERMLISTMODE constant
	 * @param targetID A channel, client or server group ID
	 * @return A Vector which contains a HashMap for each entry with the keys given by the TS3 Server.
	 * @throws EJTS3ServerQueryException 
	 * @see EJTS3ServerQuery#PERMLISTMODE_CHANNEL
	 * @see EJTS3ServerQuery#PERMLISTMODE_CLIENT
	 * @see EJTS3ServerQuery#PERMLISTMODE_SERVERGROUP
	 */
	public Vector<HashMap<String, String>> getPermissionList(int permListMode, int targetID) throws EJTS3ServerQueryException
	{
		String command = getCommand(permListMode, 3);
		
		if (command == null)
			throw new EJTS3ServerQueryException("getPermissionList(): Unknown permListMode!");
		
		command += Integer.toString(targetID);
		
		return getList(command);
	}
	
	/**
	 * Returns the last log entries or near a given time stamp.
	 * @param listLimitCount How many log entries should be returned, has to be between 1 and 500.
	 * @param searchMode Search direction, -1 = smaller than / 0 = equal / 1 = greater than the given time stamp. Only needed if searching for a time stamp. 
	 * @param timestamp A (Java) time stamp or -1 to disable time stamp search.
	 * @return A Vector which contains a HashMap for each entry with the keys given by the TS3 Server.
	 * @throws EJTS3ServerQueryException 
	 * @since 0.4
	 */
	public Vector<HashMap<String, String>> getLogEntries(int listLimitCount, int searchMode, long timestamp) throws EJTS3ServerQueryException
	{
		if (listLimitCount < 1 || listLimitCount > 500)
			throw new EJTS3ServerQueryException("getLogEntries(): listLimitCount has to be between 1 and 500!");
		
		String command = "logview limitcount=" + Integer.toString(listLimitCount);
		
		if (timestamp > 0)
		{
			if (searchMode < -1 || searchMode > 1)
				throw new EJTS3ServerQueryException("getLogEntries(): searchMode has to be between -1 and 1!");
			
			command += " comparator=";
			
			if (searchMode == -1)
			{
				command += "<";
			}
			else if (searchMode == 0)
			{
				command += "=";
			}
			else if (searchMode == 1)
			{
				command += ">";
			}
			
			command += " timestamp=" + Long.toString(timestamp / 1000);
		}
		
		return getList(command);
	}
	
	/**
	 * Get a list from the TS3 server. Use LISTMODE constants to get the wanted list.
	 * @param listMode Use a LISTMODE constant
	 * @return A Vector which contains a HashMap for each entry with the keys given by the TS3 Server.
	 * @throws EJTS3ServerQueryException 
	 * @see EJTS3ServerQuery#LISTMODE_BANLIST
	 * @see EJTS3ServerQuery#LISTMODE_CHANNELLIST
	 * @see EJTS3ServerQuery#LISTMODE_CLIENTDBLIST
	 * @see EJTS3ServerQuery#LISTMODE_CLIENTLIST
	 * @see EJTS3ServerQuery#LISTMODE_COMPLAINLIST
	 * @see EJTS3ServerQuery#LISTMODE_PERMISSIONLIST
	 * @see EJTS3ServerQuery#LISTMODE_SERVERGROUPLIST
	 * @see EJTS3ServerQuery#LISTMODE_SERVERLIST
	 */
	public Vector<HashMap<String, String>> getList(int listMode) throws EJTS3ServerQueryException
	{
		return getList(listMode, null);
	}
	
	/**
	 * Get a list from the TS3 server. Use LISTMODE constants to get the wanted list.<br><br>
	 * This method allows to pass many arguments separated with comma, see LISTMODE comments for possible arguments.
	 * @param listMode Use a LISTMODE constant
	 * @param arguments A comma separated list of arguments or a single argument for the LISTMODE. Or just <code>null</code> if no arguments needed.
	 * @return A Vector which contains a HashMap for each entry with the keys given by the TS3 Server.
	 * @throws EJTS3ServerQueryException 
	 * @see EJTS3ServerQuery#LISTMODE_BANLIST
	 * @see EJTS3ServerQuery#LISTMODE_CHANNELLIST
	 * @see EJTS3ServerQuery#LISTMODE_CLIENTDBLIST
	 * @see EJTS3ServerQuery#LISTMODE_CLIENTLIST
	 * @see EJTS3ServerQuery#LISTMODE_COMPLAINLIST
	 * @see EJTS3ServerQuery#LISTMODE_PERMISSIONLIST
	 * @see EJTS3ServerQuery#LISTMODE_SERVERGROUPLIST
	 * @see EJTS3ServerQuery#LISTMODE_SERVERLIST
	 */
	public Vector<HashMap<String, String>> getList(int listMode, String arguments) throws EJTS3ServerQueryException
	{
		String command = getCommand(listMode, 1);
		
		if (command == null)
			throw new EJTS3ServerQueryException("getList(): Unknown listMode!");
		
		if (arguments != null && arguments.length() > 1)
		{
			StringTokenizer st = new StringTokenizer(arguments, ",", false);
			String arg;
			while (st.hasMoreTokens())
			{
				arg = st.nextToken();
				if (checkListArguments(listMode, arg))
				{
					command += " " + arg;
				}
			}
		}
		
		return getList(command);
	}
	
	private Vector<HashMap<String, String>> getList(String command) throws EJTS3ServerQueryException
	{
		if (!isConnected())
			throw new EJTS3ServerQueryException("getList(): Not connected to TS3 server!");
				
		HashMap<String, String> hmIn = doInternalCommand(command);
		
		if (hmIn == null)
			throw new EJTS3ServerQueryException("Cannot get list");
		
		Vector<HashMap<String, String>> list;
		
		if (!hmIn.get("id").equals("0"))
			throw new EJTS3ServerQueryException(getErrorString("getList()", hmIn.get("id"), hmIn.get("msg"), hmIn.get("extra_msg"), hmIn.get("failed_permid")));
		else if (hmIn.get("response") == null)
			throw new EJTS3ServerQueryException("getList(): No valid server response found!");
		
		list = parseRawData(hmIn.get("response"));
				
		return list;
	}
	
	private HashMap<String, String> readIncoming()
	{
		String inData = "";
		HashMap<String, String> hmIn = new HashMap<String, String>();
		String temp;
		
		if (!isConnected())
		{
			hmIn.put("id", "-2");
			hmIn.put("msg", "readIncoming(): Not connected to TS3 server!");
			return hmIn;
		}
		
		if (DEBUG)
		{
			System.out.println("Read incoming:");
		}
		
		while (true)
		{
			try
			{
				temp = in.readLine();
				if (DEBUG)
				{
					System.out.println(temp);
				}
			}
			catch (SocketTimeoutException e1)
			{
				String closeConnectionException = "";
				try
				{
					closeTS3Connection();
				} catch (EJTS3ServerQueryException e)
				{
					closeConnectionException += "   " + e.getMessage();
				}
				hmIn.put("id", "-2");
				hmIn.put("msg", "Closed TS3 Connection, Exception readIncoming(): " + e1.toString() + closeConnectionException);
				return hmIn;
			}
			catch (SocketException e2)
			{
				String closeConnectionException = "";
				try
				{
					closeTS3Connection();
				} catch (EJTS3ServerQueryException e)
				{
					closeConnectionException += "   " + e.getMessage();
				}
				hmIn.put("id", "-2");
				hmIn.put("msg", "Closed TS3 Connection, Exception readIncoming(): " + e2.toString() + closeConnectionException);
				return hmIn;
			}
			catch (Exception e)
			{
				hmIn.put("id", "-1");
				hmIn.put("msg", "Exception readIncoming(): " + e.toString());
				return hmIn;
			}
			
			if (temp == null)
			{
				String closeConnectionException = "";
				try
				{
					closeTS3Connection();
				} catch (EJTS3ServerQueryException e)
				{
					closeConnectionException += "   " + e.getMessage();
				}
				hmIn.put("id", "-10");
				hmIn.put("msg", "readIncoming(): null object, maybe connection to TS3 server interrupted." + closeConnectionException);
				return hmIn;
			}
			
			// Jump out of the loop when reached the end of the server response.
			if (temp.startsWith("error "))
			{
				break;
			}
			
			// Save non empty lines of the response and add a new line
			if (temp.length() > 2)
			{
				if (!handleAction(temp)) // Parse notify messages
				{
					if (inData.length() != 0)
					{
						inData += System.getProperty("line.separator", "\n");
					}
					inData += temp;
				}
			}
		}
		
		// Creates a hash map with the parsed error id and message.
		hmIn = parseLine(temp);
		if (hmIn == null)
		{
			hmIn = new HashMap<String, String>();
			hmIn.put("id", "-10");
			hmIn.put("msg", "readIncoming(): null object, maybe connection to TS3 server interrupted.");
		}
		else
		{
			// Puts the server response in the hash map.
			hmIn.put("response", inData);
		}
		
		eventNotifyCheckActive = true;
		return hmIn;
	}
	
	/**
	 * Escape all special characters for the TS3 server.<br>Use this for all Strings you use as value while using doCommand()!<br><br>
	 * <b>Important:</b><br>
	 * Almost all functions in this library do this already if needed. You only need this if you want to send an own command with doCommand().
	 * @param str The String which should be escaped.
	 * @return The escaped String
	 * @since 0.5
	 * @see EJTS3ServerQuery#doCommand(String)
	 */
	public String encodeTS3String(String str)
	{
		str = str.replace("\\", "\\\\");
		str = str.replace(" ", "\\s");
		str = str.replace("/", "\\/");
		str = str.replace("|", "\\p");
		str = str.replace("\b", "\\b");
		str = str.replace("\f", "\\f");
		str = str.replace("\n", "\\n");
		str = str.replace("\r", "\\r");
		str = str.replace("\t", "\\t");

		Character cBell = new Character((char)7); // \a (not supported by Java)
		Character cVTab = new Character((char)11); // \v (not supported by Java)
		
		str = str.replace(cBell.toString(), "\\a");
		str = str.replace(cVTab.toString(), "\\v");
		
		return str;
	}
	
	/**
	 * Convert escaped characters to normal characters.<br>Use this for received String values after using doCommand()<br><br>
	 * <b>Important:</b><br>
	 * Almost all functions in this library do this already if needed. You only need this if you want to read the server response after using doCommand() without using parseRawData().
	 * @param str The String which should be unescaped.
	 * @return The unescaped String
	 * @since 0.5
	 * @see EJTS3ServerQuery#doCommand(String)
	 * @see EJTS3ServerQuery#parseRawData(String)
	 */
	public String decodeTS3String(String str)
	{
		str = str.replace("\\\\", "\\[$mksave]");
		str = str.replace("\\s", " ");
		str = str.replace("\\/", "/");
		str = str.replace("\\p", "|");
		str = str.replace("\\b", "\b");
		str = str.replace("\\f", "\f");
		str = str.replace("\\n", "\n");
		str = str.replace("\\r", "\r");
		str = str.replace("\\t", "\t");

		Character cBell = new Character((char)7); // \a (not supported by Java)
		Character cVTab = new Character((char)11); // \v (not supported by Java)
		
		str = str.replace("\\a", cBell.toString());
		str = str.replace("\\v", cVTab.toString());
		
		str = str.replace("\\[$mksave]", "\\");
		return str;
	}
	
	private HashMap<String, String> parseLine(String line)
	{
		if (line == null || line.length() == 0)
		{
			return null;
		}
		
		StringTokenizer st = new StringTokenizer(line, " ", false);
		HashMap<String, String> retValue = new HashMap<String, String>();
		String key;
		String temp;
		int pos = -1;
		
		while (st.hasMoreTokens())
		{
			temp = st.nextToken();
			
			// The next 10 lines split the key / value pair at the equal sign and put this into the hash map.
			pos = temp.indexOf("=");
			
			if (pos == -1)
			{
				retValue.put(temp, "");
			}
			else
			{
				key = temp.substring(0, pos);
				retValue.put(key, decodeTS3String(temp.substring(pos+1)));
			}
		}
		
		return retValue;
	}
	
	private boolean checkListArguments(int listMode, String argument)
	{
		if (listMode == LISTMODE_CHANNELLIST)
		{
			if (argument.equalsIgnoreCase("-topic"))
			{
				return true;
			}
			if (argument.equalsIgnoreCase("-flags"))
			{
				return true;
			}
			if (argument.equalsIgnoreCase("-voice"))
			{
				return true;
			}
			if (argument.equalsIgnoreCase("-limits"))
			{
				return true;
			}
			if (argument.equalsIgnoreCase("-icon"))
			{
				return true;
			}
		}
		
		if (listMode == LISTMODE_CLIENTLIST)
		{
			if (argument.equalsIgnoreCase("-uid"))
			{
				return true;
			}
			if (argument.equalsIgnoreCase("-away"))
			{
				return true;
			}
			if (argument.equalsIgnoreCase("-voice"))
			{
				return true;
			}
			if (argument.equalsIgnoreCase("-times"))
			{
				return true;
			}
			if (argument.equalsIgnoreCase("-groups"))
			{
				return true;
			}
			if (argument.equalsIgnoreCase("-info"))
			{
				return true;
			}
			if (argument.equalsIgnoreCase("-icon"))
			{
				return true;
			}
			if (argument.equalsIgnoreCase("-country"))
			{
				return true;
			}
		}
		
		if (listMode == LISTMODE_SERVERLIST)
		{
			if (argument.equalsIgnoreCase("-uid"))
			{
				return true;
			}
			if (argument.equalsIgnoreCase("-all"))
			{
				return true;
			}
			if (argument.equalsIgnoreCase("-short"))
			{
				return true;
			}
			if (argument.equalsIgnoreCase("-onlyoffline"))
			{
				return true;
			}
		}
		
		if (listMode == LISTMODE_CLIENTDBLIST)
		{
			if (argument.startsWith("start=") && (argument.indexOf(" ") == -1))
			{
				return true;
			}
			if (argument.startsWith("duration=") && (argument.indexOf(" ") == -1))
			{
				return true;
			}
			if (argument.equalsIgnoreCase("-count"))
			{
				return true;
			}
		}
		
		if (listMode == LISTMODE_COMPLAINLIST)
		{
			if (argument.startsWith("tcldbid=") && (argument.indexOf(" ") == -1))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private String getCommand(int mode, int listType)
	{
		if (listType == 1)
		{
			if (mode == LISTMODE_CHANNELLIST)
			{
				return "channellist";
			}
			else if (mode == LISTMODE_CLIENTDBLIST)
			{
				return "clientdblist";
			}
			else if (mode == LISTMODE_CLIENTLIST)
			{
				return "clientlist";
			}
			else if (mode == LISTMODE_PERMISSIONLIST)
			{
				return "permissionlist";
			}
			else if (mode == LISTMODE_SERVERGROUPLIST)
			{
				return "servergrouplist";
			}
			else if (mode == LISTMODE_SERVERLIST)
			{
				return "serverlist";
			}
			else if (mode == LISTMODE_BANLIST)
			{
				return "banlist";
			}
			else if (mode == LISTMODE_COMPLAINLIST)
			{
				return "complainlist";
			}
		}
		else if (listType == 2)
		{
			if (mode == INFOMODE_SERVERINFO)
			{
				return "serverinfo";
			}
			else if (mode == INFOMODE_CHANNELINFO)
			{
				return "channelinfo cid=";
			}
			else if (mode == INFOMODE_CLIENTINFO)
			{
				return "clientinfo clid=";
			}
		}
		else if (listType == 3)
		{
			if (mode == PERMLISTMODE_CHANNEL)
			{
				return "channelpermlist cid=";
			}
			else if (mode == PERMLISTMODE_CLIENT)
			{
				return "clientpermlist cldbid=";
			}
			else if (mode == PERMLISTMODE_SERVERGROUP)
			{
				return "servergrouppermlist sgid=";
			}
		}
		
		return null;
	}
	
	private boolean handleAction(final String actionLine)
	{
		if (!actionLine.startsWith("notify"))
		{
			return false;
		}
		
		if (actionClass != null)
		{
			final int pos = actionLine.indexOf(" ");
			
			if (pos != -1)
			{
				final String eventType = actionLine.substring(0, pos);

				new Thread(new Runnable()
				{
					public void run()
					{
						try
						{
							actionClass.teamspeakActionPerformed(eventType, parseLine(actionLine.substring(pos+1)));	
						}
						catch (Exception e)
						{
							if (DEBUG) e.printStackTrace();
						}						
					}
				}).start();
			}
		}
		
		return true;
	}
}
