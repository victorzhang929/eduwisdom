package com.vz.eduwisdom.service;

import java.util.List;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

public interface RecommendationService {

    List<RecommendedItem> listUserBasedRecommendationResource(long userId) throws Exception;

    List<RecommendedItem> listItemBasedRecommendationResource(long userId) throws Exception;

}
