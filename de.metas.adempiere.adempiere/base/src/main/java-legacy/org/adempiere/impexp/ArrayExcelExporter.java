/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution *
 * Copyright (C) 2008 SC ARHIPAC SERVICE SRL. All Rights Reserved. *
 * This program is free software; you can redistribute it and/or modify it *
 * under the terms version 2 of the GNU General Public License as published *
 * by the Free Software Foundation. This program is distributed in the hope *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. *
 * See the GNU General Public License for more details. *
 * You should have received a copy of the GNU General Public License along *
 * with this program; if not, write to the Free Software Foundation, Inc., *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA. *
 *****************************************************************************/
package org.adempiere.impexp;

import java.util.List;
import java.util.Properties;

import de.metas.i18n.IMsgBL;
import de.metas.util.Services;
import lombok.Builder;
import lombok.NonNull;

/**
 * Export excel from ArrayList of data
 * 
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 *
 */
public class ArrayExcelExporter extends AbstractExcelExporter
{
	private final IMsgBL msgBL = Services.get(IMsgBL.class);

	private Properties m_ctx = null;
	private List<List<Object>> m_data = null;

	@Builder
	private ArrayExcelExporter(
			@NonNull final Properties ctx,
			@NonNull final List<List<Object>> data)
	{
		m_ctx = ctx;
		m_data = data;
	}

	@Override
	public Properties getCtx()
	{
		return m_ctx;
	}

	@Override
	public int getColumnCount()
	{
		return m_data.get(0).size();
	}

	@Override
	public int getDisplayType(final int row, final int col)
	{
		final List<Object> dataRow = m_data.get(row + 1);
		final Object value = dataRow.get(col);
		return CellValues.extractDisplayTypeFromValue(value);
	}

	@Override
	public String getHeaderName(final int col)
	{
		final Object headerNameObj = m_data.get(0).get(col);
		final String headerName = headerNameObj != null ? headerNameObj.toString() : null;

		final String adLanguage = getLanguage().getAD_Language();
		return msgBL.translatable(headerName).translate(adLanguage);
	}

	@Override
	public int getRowCount()
	{
		return m_data.size() - 1;
	}

	@Override
	public CellValue getValueAt(final int row, final int col)
	{
		final List<Object> dataRow = m_data.get(row + 1);
		final Object value = dataRow.get(col);
		return CellValues.toCellValue(value);
	}

	@Override
	public boolean isColumnPrinted(final int col)
	{
		return true;
	}

	@Override
	public boolean isFunctionRow(final int row)
	{
		return false;
	}

	@Override
	public boolean isPageBreak(final int row, final int col)
	{
		return false;
	}
}
