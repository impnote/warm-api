package com.donler.repository.trend

import com.donler.model.persistent.trend.TrendItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
/**
 * Created by apple on 16/9/20.
 */
interface TrendItemRepository extends MongoRepository<TrendItem, String> {
    List<TrendItem> findByCompanyId(def companyId)
    TrendItem findByTrendId(def trendId)
    Page<TrendItem> findByCompanyId(def companyId, Pageable pageable)
    Page<TrendItem> findByTeamId(def teamId, Pageable pageable)
    Page<TrendItem> findByTeamIdAndCreatedAt(def teamId,Date createdAt, Pageable pageable)
    Page<TrendItem> findByCreatedAtBefore(Date createdAt,Pageable pageable)
    Page<TrendItem> findByCompanyIdAndCreatedAtBefore(def companyId, Date createdAt,Pageable pageable)
    Page<TrendItem> findByAuthorId(def uid, Pageable pageable)
    Page<TrendItem> findByAuthorIdAndCreatedAt(def uid, Date createdAt, Pageable pageable)
}