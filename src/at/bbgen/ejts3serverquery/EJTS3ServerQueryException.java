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

/**
 * Exception thrown by EJTS3ServerQuery
 * @author bb
 * @since 1.1
 */
public class EJTS3ServerQueryException extends Exception
{
	private static final long serialVersionUID = 1L;

	public EJTS3ServerQueryException()
	{
		super();
	}
	
	public EJTS3ServerQueryException(String err)
	{
		super(err);
	}
}