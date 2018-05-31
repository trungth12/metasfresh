package de.metas.money.grossprofit;

import org.adempiere.util.lang.ExtendedMemorizingSupplier;

import com.google.common.collect.ImmutableList;

import de.metas.money.Money;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

/*
 * #%L
 * de.metas.business
 * %%
 * Copyright (C) 2018 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

@Value
@Builder
class NetPriceCalculator
{
	boolean soTrx;

	@NonNull
	Money basePrice;

	@Singular
	ImmutableList<GrossProfitComponent> profitCompponents;

	ExtendedMemorizingSupplier<Money> netPriceSupplier = ExtendedMemorizingSupplier.of(this::computeNetPrice);

	public Money getNetPrice()
	{
		return netPriceSupplier.get();
	}

	private Money computeNetPrice()
	{
		Money netPrice = basePrice;
		for (final GrossProfitComponent profitComponent : profitCompponents)
		{
			netPrice = profitComponent.applyToInput(netPrice);
		}
		return netPrice;
	}
}