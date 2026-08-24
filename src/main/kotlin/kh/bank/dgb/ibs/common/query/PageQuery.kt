package kh.bank.dgb.ibs.common.query

/**
 * Shared paged-search parameter shape, matching the old app's `dataGridDTO.{searchKeyword,start,
 * pageSize}` convention used across the FAQ/News-Event list queries. Kept as one shared type
 * rather than duplicated per-DAO since the mapper XML's `<if>` blocks reference the same field
 * names either way.
 */
data class PageQuery(
	val searchKeyword: String? = null,
	val start: Int? = null,
	val pageSize: Int? = null,
)
