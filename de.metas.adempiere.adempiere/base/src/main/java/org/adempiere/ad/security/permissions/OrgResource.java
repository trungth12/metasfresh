package org.adempiere.ad.security.permissions;

/*
 * #%L
 * de.metas.adempiere.adempiere.base
 * %%
 * Copyright (C) 2015 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */


import javax.annotation.concurrent.Immutable;

import org.adempiere.service.IClientDAO;
import org.adempiere.service.IOrgDAO;
import org.adempiere.util.Check;
import org.adempiere.util.Services;
import org.adempiere.util.lang.EqualsBuilder;
import org.adempiere.util.lang.HashcodeBuilder;
import org.compiere.model.I_AD_Client;
import org.compiere.model.I_AD_Org;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;

import com.google.common.base.Function;

/**
 * Identifies a particular organization.
 *
 * @author tsa
 *
 */
@Immutable
public final class OrgResource implements Resource
{
	/** Any Org */
	public static final OrgResource ANY = new OrgResource();

	public static final OrgResource of(final int adClientId, final int adOrgId)
	{
		return new OrgResource(adClientId, adOrgId);
	}

	public static final Function<OrgResource, KeyNamePair> TO_ClientKeyNamePair_Function = new Function<OrgResource, KeyNamePair>()
	{
		@Override
		public KeyNamePair apply(OrgResource orgResource)
		{
			return orgResource.asClientKeyNamePair();
		}
	};

	public static final Function<OrgResource, KeyNamePair> TO_OrgKeyNamePair_Function = new Function<OrgResource, KeyNamePair>()
	{
		@Override
		public KeyNamePair apply(OrgResource orgResource)
		{
			return orgResource.asOrgKeyNamePair();
		}
	};

	/** Client */
	private final int _adClientId;
	/** Organization */
	private final int _adOrgId;

	// cached values
	private int hashcode = 0;
	private String clientName;
	private String orgName;
	private Boolean summaryOrg;

	private OrgResource(final int adClientId, final int adOrgId)
	{
		super();

		Check.assume(adClientId >= 0, "adClientId >= 0");
		_adClientId = adClientId;

		Check.assume(adOrgId >= 0, "adOrgId >= 0");
		_adOrgId = adOrgId;
	}

	/** Any Org constructor */
	private OrgResource()
	{
		super();
		_adClientId = -1;
		_adOrgId = -1;
		clientName = "-";
		orgName = "-";
		summaryOrg = false;
	}

	@Override
	public int hashCode()
	{
		if (hashcode == 0)
		{
			hashcode = new HashcodeBuilder()
					.append(31) // seed
					.append(_adClientId)
					.append(_adOrgId)
					.toHashcode();
		}
		return hashcode;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		final OrgResource other = EqualsBuilder.getOther(this, obj);
		if (other == null)
		{
			return false;
		}

		return new EqualsBuilder()
				.append(_adClientId, other._adClientId)
				.append(_adOrgId, other._adOrgId)
				.isEqual();
	}

	@Override
	public String toString()
	{
		final String clientName = getClientName();
		final String orgName = getOrgName();
		final StringBuilder sb = new StringBuilder();
		sb.append("@AD_Client_ID@").append("=").append(clientName)
				.append(" - ")
				.append("@AD_Org_ID@").append("=").append(orgName);
		return sb.toString();
	}

	public final String getClientName()
	{
		if (clientName == null)
		{
			final int adClientId = getAD_Client_ID();
			if (adClientId > 0)
			{
				final I_AD_Client client = Services.get(IClientDAO.class).retriveClient(Env.getCtx(), adClientId);
				clientName = client == null ? String.valueOf(adClientId) : client.getName();
			}
			else
			{
				clientName = "System";
			}
		}
		return clientName;

	}

	public final String getOrgName()
	{
		if (orgName == null)
		{
			int adOrgId = getAD_Org_ID();
			if (adOrgId > 0)
			{
				final I_AD_Org org = Services.get(IOrgDAO.class).retrieveOrg(Env.getCtx(), adOrgId);
				orgName = org == null ? String.valueOf(adOrgId) : org.getName();
			}
			else
			{
				orgName = "*";
			}
		}
		return orgName;
	}

	public final boolean isSummaryOrganization()
	{
		if (summaryOrg == null)
		{
			final I_AD_Org org = Services.get(IOrgDAO.class).retrieveOrg(Env.getCtx(), getAD_Org_ID());
			summaryOrg = org == null ? false : org.isSummary();
		}
		return summaryOrg;
	}

	public int getAD_Client_ID()
	{
		return _adClientId;
	}

	public int getAD_Org_ID()
	{
		return _adOrgId;
	}

	public KeyNamePair asClientKeyNamePair()
	{
		if (this == ANY)
		{
			return KeyNamePair.EMPTY;
		}
		return new KeyNamePair(getAD_Client_ID(), getClientName());
	}

	public KeyNamePair asOrgKeyNamePair()
	{
		if (this == ANY)
		{
			return KeyNamePair.EMPTY;
		}
		return new KeyNamePair(getAD_Org_ID(), getOrgName());
	}

}
