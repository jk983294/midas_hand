package com.victor.midas.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.victor.midas.model.db.StockInfoDb;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.StringPatternAware;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.victor.midas.dao.StockInfoDao;
import com.victor.utilities.datastructures.tree.TernaryTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TypeAhead {
	
	private static final Logger logger = Logger.getLogger(TypeAhead.class);
	
	private static TernaryTree data = new TernaryTree();
	/**
	 * characters that we don't bother to concern
	 */
	
	@Autowired
	public TypeAhead(StockInfoDao stockInfoDao){
        init(MidasConstants.actions);
		List<StockInfoDb> stocks = stockInfoDao.queryAllBasicInfo();
		List<String> data = new ArrayList<>();
		if (stocks != null) {
			for (StockInfoDb stock : stocks) {
				data.add(stock.getName());
			}
			init(data);
		}
	}
	
	public TypeAhead(List<String> names){
		init(names);
	}
	
	public static void init(Collection<String> names){
		if(CollectionUtils.isNotEmpty(names)){
			for (String string : names) {
				data.add(string);
			}
		}
	}
	
	/**
	 * deal with raw query string, replace non relevant character to space, split it to sub-query
	 */
	public List<String> query(String toFinds){
		List<String> results = new ArrayList<>();
        toFinds = toFinds.replaceAll(StringPatternAware.NOT_UNDER_CONCERN, " ");
		String[] subQueries = toFinds.split(" ");
		for (String subQuery : subQueries) {
            results.addAll(querySingle(subQuery));
		}
        return results;
	}
	
	/**
	 * deal with single name query
	 */
	private List<String> querySingle(String toFind){
		ArrayList<String> results = new ArrayList<>();
		if("|".equals(toFind)){
            results.add(toFind);
        } else if(StringPatternAware.isOnlyNumber(toFind)) {
			results.addAll(autoAddPrefix(toFind));
		} else {
			List<String> finds = data.getCompletionsFor(toFind);
			if (finds != null) {
				results.addAll(finds);
			}
		}
        Collections.reverse(results);
		return results;
	}
	
	/**
	 * for numbers, should add prefix to look up in ternary tree
	 */
	private List<String> autoAddPrefix(String numbers){
		ArrayList<String> results = new ArrayList<>();
		for (String prefix : StringPatternAware.STOCK_PREFIX) {
			List<String> finds = data.getCompletionsFor(prefix + numbers);
			if (finds != null) {
				results.addAll(finds);
			}
		}	
		return results;
	}
}
